package net.faintedge.core

abstract class Control {
  var owner: Entity = null
  
  // delta: time in seconds
  def update(delta: Float)
  
}