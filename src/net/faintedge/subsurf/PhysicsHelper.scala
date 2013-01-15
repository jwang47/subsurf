package net.faintedge.subsurf
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.FixtureDef
import net.faintedge.fiber.physics.PhysicsManager
import org.jbox2d.dynamics.BodyType
import net.faintedge.fiber.physics.Box2DConversions._
import net.faintedge.fiber.shape.Shape
import org.jbox2d.dynamics.Filter

object PhysicsHelper {

  private def ptm = PhysicsManager.ptm
  def newBody(world: World, bodyType: BodyType, shape: Shape, userData: AnyRef = null, x: Float = 0, y: Float = 0, angle: Float = 0, filter: Filter = new Filter()): Body = {
    val bodyDef = new BodyDef()
    bodyDef.`type` = bodyType
    bodyDef.position.set(x / ptm, -y / ptm)
    bodyDef.angle = -angle
    bodyDef.userData = userData
    val body = world.createBody(bodyDef)

    val fixtureDef = new FixtureDef()
    fixtureDef.shape = shape.scale(1 / ptm * 0.5f)
    fixtureDef.density = 1
    fixtureDef.friction = 0.3f
    fixtureDef.filter = filter
    body.createFixture(fixtureDef)

    body
  }

}