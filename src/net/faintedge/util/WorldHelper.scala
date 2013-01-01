package net.faintedge.util
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.dynamics.FixtureDef

object WorldHelper {

  val DEFAULT_PTM = 10.0f

  def newRectangleShape(width: Float, height: Float, ptm: Float = DEFAULT_PTM): Shape = {
    val shape = new PolygonShape()
    shape.setAsBox(width / ptm, height / ptm)
    shape
  }

  def newCircleShape(radius: Float, ptm: Float = DEFAULT_PTM): Shape = {
    val shape = new CircleShape()
    shape.m_radius = radius / ptm
    shape
  }

  def add(world: World, fixtureShapes: Array[Shape],
    spawnX: Float, spawnY: Float, ptm: Float = DEFAULT_PTM,
    userData: Object = null, position: Vec2 = new Vec2(),
    angle: Float = 0f, linearVelocity: Vec2 = new Vec2(),
    angularVelocity: Float = 0f, linearDamping: Float = 0f,
    angularDamping: Float = 0f, allowSleep: Boolean = true,
    awake: Boolean = true, fixedRotation: Boolean = false,
    bullet: Boolean = false, typ: BodyType = BodyType.STATIC,
    active: Boolean = true, inertiaScale: Float = 1.0f): Body = {
    var bodyDef = new BodyDef()
    bodyDef.userData = userData
    bodyDef.position.x = position.x / ptm
    bodyDef.position.y = position.y / ptm
    bodyDef.angle = angle
    bodyDef.linearVelocity.x = linearVelocity.x / ptm
    bodyDef.linearVelocity.y = linearVelocity.y / ptm
    bodyDef.angularVelocity = angularVelocity
    bodyDef.linearDamping = linearDamping
    bodyDef.angularDamping = angularDamping
    bodyDef.allowSleep = allowSleep
    bodyDef.awake = awake
    bodyDef.fixedRotation = fixedRotation
    bodyDef.bullet = bullet
    bodyDef.`type` = typ
    bodyDef.active = active
    bodyDef.inertiaScale = inertiaScale
    val body = world.createBody(bodyDef)
    for (fixtureShape <- fixtureShapes) {
      val fixtureDef = new FixtureDef()
      fixtureDef.shape = fixtureShape
      fixtureDef.density = if (typ == BodyType.STATIC) 0f else 1f
      fixtureDef.friction = 0.3f
      body.createFixture(fixtureDef)
    }
    body
  }

}