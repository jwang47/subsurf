package net.faintedge.core
import org.newdawn.slick.GameContainer
import org.newdawn.slick.geom.Vector2f
import net.faintedge.math.Vector2

class Camera() {
  var gc: GameContainer = null
  var position: Vector2 = new Vector2(0, 0)
  
  def offset: Vector2 = new Vector2(-x + gc.getWidth()/2, -y + gc.getHeight()/2)
  def x: Float = position.x
  def y: Float = position.y

}