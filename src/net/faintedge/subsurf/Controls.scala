package net.faintedge.subsurf
import net.faintedge.core.Control
import net.faintedge.math.Vector2
import org.newdawn.slick.Input
import org.jbox2d.common.Vec2

class Player extends Control {
  override def update(delta: Float) {
    
  }
}

class ForceControl extends Control {
  override def update(delta: Float) {
    val force = 5f;
    val forceVector = new Vector2(0, 0)
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_W)) { forceVector.y -= force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_S)) { forceVector.y += force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_A)) { forceVector.x -= force }
    if (SubsurfGame.app.getInput().isKeyDown(Input.KEY_D)) { forceVector.x += force }
    owner.body.applyImpulse(forceVector.x, forceVector.y)
  }
}