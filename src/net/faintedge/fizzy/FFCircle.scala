package net.faintedge.fizzy

import org.newdawn.fizzy.Circle
import org.jbox2d.dynamics.Filter
import org.newdawn.fizzy.Body

class FFCircle(val radius: Float, val filter: Filter = null) extends Circle(radius) {

  override def createInBody(body: Body[_]) {
    super.createInBody(body)
    if (filter != null) {
      jbox2DFixture.setFilterData(filter)
    }
  }
  
}