package net.faintedge.subsurf
import org.jbox2d.dynamics.Filter
import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image
import net.faintedge.fiber.controls.CircleForm
import net.faintedge.fiber.controls.ImageRender
import net.faintedge.fiber.controls.RectangleForm
import net.faintedge.fiber.controls.RectangleRender
import net.faintedge.fiber.controls.Transform
import net.faintedge.fiber.physics.Physics
import net.faintedge.fiber.Application
import net.faintedge.fiber.Entity
import net.faintedge.fiber.EntityConversions._
import net.faintedge.fiber.controls.CircleRender

object SubsurfGame extends Application("subsurf") {
  def main(args: Array[String]) = SubsurfGame.start()
  
  var player: Entity = null
  var tilePage: TilePage = null

  override def init(gc: GameContainer) {
    initWorld
    initUi
    
    cam.follow(player)
  }
  
  private def initUi {
    root += new Entity("selector", Array(new TileSelector(tilePage)))
  }
  
  private def initWorld {
    val backgroundCategory = 1
    val defaultCategory = 2
    
    val defaultFilter = new Filter()
    defaultFilter.categoryBits = defaultCategory
    defaultFilter.maskBits = backgroundCategory | defaultCategory
    
    val bgFilter = new Filter()
    bgFilter.categoryBits = backgroundCategory
    bgFilter.maskBits = backgroundCategory | defaultCategory
    
    val coinImage = new Image("data/coin.png")
    player = new Entity("player", Array(new Transform(), new Physics(filter = defaultFilter), new ForceControl(), new RectangleForm(10, 10), new RectangleRender()))
    
    val tileData = Array(
      TileInfo(0, "empty", new NullTileRenderer()),
      TileInfo(1, "mud", new ImageTileRenderer(new Image("data/30.png"))),
      TileInfo(2, "stone", new ImageTileRenderer(new Image("data/21.png"))))
      
    val tilePageEntity = new Entity("tile page", Array(new Transform(), new PhysicsTilePage(bgFilter, tileData, 40, 30)))
    val tilePageOption = tilePageEntity.getControl(classOf[TilePage])
    tilePage = tilePageOption.get
    for (col <- 0 until tilePage.width; row <- 0 until tilePage.height) {
      if (Math.random < 0.1) {
        tilePage.setTile(row, col, 1)
      }
    }
    
    root += new Entity("coin", Array(new Transform(), new Physics(fixedRotation = true, filter = defaultFilter), new CircleForm(coinImage.getHeight/2), new ImageRender(coinImage)))
    root += new Entity("test box", Array(new Transform(0, 0, (Math.Pi / 4).toFloat), new Physics(filter = defaultFilter), new RectangleForm(10, 10), new RectangleRender(Color.white)))
    root += new Entity("test circle", Array(new Transform(), new Physics(filter = defaultFilter), new CircleForm(10), new CircleRender(Color.white)))
    root += player
    root += tilePageEntity
  }

}