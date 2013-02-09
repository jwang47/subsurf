package net.faintedge.subsurf
import net.faintedge.math.Vector2
import org.newdawn.slick.Input
import org.jbox2d.common.Vec2
import net.faintedge.fiber.Control
import net.faintedge.fiber.Updatable
import net.faintedge.fiber.physics.Physics
import net.faintedge.fiber.Sibling
import net.faintedge.fiber.physics.Box2DConversions._
import net.faintedge.fiber.Renderable
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Color

class Player extends Control with Updatable {
  override def update(delta: Float) {

  }
}

class ForceControl(val force: Float = 1) extends Control with Updatable {
  @Sibling var physics: Physics = null
  val forceVector = new Vector2(0, 0)
  override def update(delta: Float) {
    forceVector := (0, 0)
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_W)) { forceVector.y += force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_S)) { forceVector.y -= force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_A)) { forceVector.x -= force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_D)) { forceVector.x += force }
    physics.body.applyLinearImpulse(forceVector, physics.body.getPosition())
  }
}

class TileSelector(val tilePage: TilePage) extends Control with Updatable with Renderable {
  val highlightColor = new Color(0.5f, 0.5f, 1.0f, 0.45f)
  var selectedTile = (0, 0)

  def input = SubsurfGame.app.getInput()
  def camera = SubsurfGame.cam

  private def getSelectedTile(mouseLocation: (Float, Float), cameraOffset: Vector2): (Int, Int) = {
    // calculate mouse location in TilePage space (top left corner of TilePage = 0,0)
    val mouseX = mouseLocation._1 - cameraOffset.x + tilePage.totalWidth / 2
    val mouseY = -(mouseLocation._2 - cameraOffset.y - tilePage.totalHeight / 2)
    // get tile associated with the mouse location
    val tileRow = ((mouseY + tilePage.tileHeight / 2) / tilePage.tileHeight).toInt
    val tileCol = ((mouseX + tilePage.tileWidth / 2) / tilePage.tileWidth).toInt
    (Util.constrain(tileRow, 0, tilePage.height - 1), Util.constrain(tileCol, 0, tilePage.width - 1))
  }

  def update(delta: Float) {
    selectedTile = getSelectedTile((input.getMouseX(), input.getMouseY()), camera.offset)
    if (input.isMouseButtonDown(0)) {
      tilePage.setTile(selectedTile._1, selectedTile._2, 1)
    }
    if (input.isMouseButtonDown(1)) {
      tilePage.setTile(selectedTile._1, selectedTile._2, 0)
    }
  }

  def render(gc: GameContainer, g: Graphics) {
    tilePage.fillTile(g, selectedTile._1, selectedTile._2, highlightColor)
  }
}