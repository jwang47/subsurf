package net.faintedge.fizzy
import org.newdawn.fizzy.Rectangle
import org.newdawn.fizzy.Body
import org.jbox2d.dynamics.Filter

class FFRectangle(val width: Float, val height: Float, val filter: Filter = null) extends Rectangle(width, height) {
  
  override def createInBody(body: Body[_]) {
    super.createInBody(body)
    if (filter != null) {
      jbox2DFixture.setFilterData(filter)
    }
  }
  
}