package net.faintedge.subsurf;
import scala.collection.mutable.HashSet
import scala.collection.mutable.Set
import scala.util.Random
import org.jbox2d.common.Vec2
import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.BasicGame
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import net.faintedge.math.Vector2
import net.faintedge.core.Entity
import net.faintedge.core.Camera
import net.faintedge.template.slick.FizzyBodyRenderer
import net.faintedge.core.CircleEntity
import net.faintedge.template.slick.Box2DBodyRenderer
import net.faintedge.util.WorldHelper
import org.newdawn.fizzy.World
import org.newdawn.fizzy.Shape
import net.faintedge.core.PlayerEntity

object SubsurfGame extends BasicGame("Subsurf") {
  
  abstract sealed class ViewType(val name: String, val num: Int)
  case object Normal extends ViewType("Normal", 0)
  case object Physics extends ViewType("Physics", 1)
  var viewType: ViewType = Normal

  val HIGHLIGHT_COLOR = new Color(0.5f, 0.5f, 1.0f, 0.45f)

  val gravity = new Vec2(0, 10)
  val world = new World(gravity)
  val timeStep = 1.0f / 60.0f

  val camera = new Camera()
  val page = new PhysicsTilePage(40, 30, world)
  val tileRenderer = new TilePageRenderer(page)

  var selectedTile: Tuple2[Int, Int] = (0, 0)
  var entities: Set[Entity] = new HashSet[Entity]()

  var player: Entity = null
  var blockType: Int = 1

  val app = new AppGameContainer(this)
  def main(args: Array[String]) {
    app.setDisplayMode(1024, 768, false)
    app.setVSync(true)
    app.start()
  }

  override def init(gc: GameContainer) {
    camera.gc = gc
    
    for (col <- 0 until page.width) {
      for (row <- 0 until page.height) {
        if (Math.random < 0.1) {
          page.setTile(row, col, 1)
        }
      }
    }

    val random = new Random()
    for (i <- 1 to 20) {
      val entity = new CircleEntity(world, (i * 10 - (10 * 20 / 2), random.nextInt(5)), 5.0f, Color.cyan)
      entities.add(entity)
    }

    player = new PlayerEntity(world, (0, -100), 5.0f, 10.0f, Color.orange)
    player.addControl(new ForceControl())
    entities.add(player)
  }

  override def update(gc: GameContainer, delta: Int) {
    val deltaSeconds = delta / 1000.0f
    for (val entity <- entities) { entity.update(deltaSeconds) }
    world.update(timeStep)

    camera.position := (player.body.getX(), player.body.getY())

    selectedTile = getSelectedTile((gc.getInput().getMouseX(), gc.getInput().getMouseY()), camera.offset)
    if (gc.getInput().isMouseButtonDown(0)) {
      page.setTile(selectedTile._1, selectedTile._2, blockType)
    }
    if (gc.getInput().isMouseButtonDown(1)) {
      page.setTile(selectedTile._1, selectedTile._2, 0)
    }

    if (gc.getInput().isKeyDown(Input.KEY_1)) {
    	viewType = Normal
    } else if (gc.getInput().isKeyDown(Input.KEY_2)) {
    	viewType = Physics
    }

    if (gc.getInput().isKeyDown(Input.KEY_Q)) {
      blockType = 1
    } else if (gc.getInput().isKeyDown(Input.KEY_W)) {
      blockType = 2
    }
  }

  def render(gc: GameContainer, g: Graphics) {
    // CAMERA SPACE
    val cameraOffset = camera.offset
    g.translate(cameraOffset.x, cameraOffset.y)
    if (viewType == Normal) {
      tileRenderer.render(g)
      for (entity <- entities) { entity.render(g) }
      tileRenderer.fillTile(g, selectedTile._1, selectedTile._2, HIGHLIGHT_COLOR)
    } else if (viewType == Physics) {
      for (i <- 0 until world.getBodyCount()) {
        FizzyBodyRenderer.drawBody(g, world.getBody(i))
      }
    }
    g.resetTransform()

    // NOT CAMERA SPACE
    g.setColor(Color.green)
    g.drawString("num bodies: " + world.getBodyCount(), 30, 30)
    g.drawString("view mode: " + viewType.name, 30, 40)
  }

  private def getSelectedTile(mouseLocation: (Float, Float), cameraOffset: Vector2): (Int, Int) = {
    // calculate mouse location in TilePage space (top left corner of TilePage = 0,0)
    val mouseX = mouseLocation._1 - cameraOffset.x + page.totalWidth / 2
    val mouseY = mouseLocation._2 - cameraOffset.y + page.totalHeight / 2
    // get tile associated with the mouse location
    val tileRow = ((mouseY + page.tileHeight / 2) / page.tileHeight).toInt
    val tileCol = ((mouseX + page.tileWidth / 2) / page.tileWidth).toInt
    (Util.constrain(tileRow, 0, page.height - 1), Util.constrain(tileCol, 0, page.width - 1))
  }
}