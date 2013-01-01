package net.faintedge.core
import scala.collection.mutable.HashSet
import org.newdawn.fizzy.Body
import org.newdawn.fizzy.Circle
import org.newdawn.fizzy.DynamicBody
import org.newdawn.fizzy.Shape
import org.newdawn.fizzy.World
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import net.faintedge.math.Constants
import net.faintedge.subsurf.GameConstants
import scala.collection.JavaConversions._
import net.faintedge.fizzy.FFCircle
import org.jbox2d.dynamics.Filter

object Entity {
  val DEFAULT_RESTITUTION = 0.2f
}

abstract class Entity(world: World, spawn: (Float, Float) = (0, 0)) {
  val controls: HashSet[Control] = new HashSet[Control]
  val shape: Shape = constructShape(); shape.setRestitution(GameConstants.DEFAULT_ENTITY_RESTITUTION)
  val body: Body[Entity] = new DynamicBody(shape, spawn._1, spawn._2)
  body.setUserData(this)
  world.add(body)

  def constructShape(): Shape
  def subRender(g: Graphics)

  // delta: time in seconds
  def update(delta: Float) {
    for (control <- controls) {
      control.update(delta)
    }
  }

  def render(g: Graphics) {
    g.translate(body.getX(), body.getY())
    g.rotate(0, 0, body.getRotation() * Constants.DEGREES_PER_RADIANS)

    subRender(g)

    g.rotate(0, 0, -body.getRotation() * Constants.DEGREES_PER_RADIANS)
    g.translate(-body.getX(), -body.getY())
  }

  def addControl(control: Control) {
    controls.add(control)
    control.owner = this
  }
}

class CircleEntity(world: World, spawn: (Float, Float) = (0, 0), radius: Float, color: Color) extends Entity(world, spawn) {
  def constructShape() = {
    /*val filter = new Filter()
    filter.categoryBits = 0x0002
    filter.maskBits = 0x0001*/
    new FFCircle(radius/*, filter = filter*/)
  }
  def subRender(g: Graphics) {
    g.setColor(color)
    g.drawOval(-radius, -radius, radius * 2, radius * 2)
    g.drawLine(0, 0, 0, -radius.toInt)
  }
}