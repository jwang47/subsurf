package net.faintedge.subsurf
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.Graphics

object DebugRender {
  def renderHighlightedTile(g: Graphics, cameraLocation: Vector2f, renderer: TilePageRenderer, mouse: (Int, Int)) {
    // transform mouse location to world space
    var mouseX = mouse._1
    var mouseY = mouse._2
  }
}