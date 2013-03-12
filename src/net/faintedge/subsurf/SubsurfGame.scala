package net.faintedge.subsurf
import org.newdawn.slick.GameContainer
import net.faintedge.subsurf.PlayState

import net.faintedge.fiber.Application

object SubsurfGame extends Application("subsurf") {
  def main(args: Array[String]) = SubsurfGame.start()
  override def initStatesList(gc: GameContainer) {
    addState(new PlayState(this))
  }

}