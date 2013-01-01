package net.faintedge.math

class Vector2(var x: Float = 0, var y: Float = 0) {

  def length: Float = Math.sqrt((x * x) + (y * y)).toFloat
  def length2: Float = (x * x) + (y * y)
  def normalized: Vector2 = this * (1/length)
  def angle: Float = Math.atan2(y, x).toFloat
  def degrees: Float = (Math.atan2(y, x) * 180 / Math.Pi).toFloat
  
  def fromAngle(angle: Float): Vector2 = {
    x = Math.cos(angle).toFloat
    y = Math.sin(angle).toFloat
    this
  }
  
  def fromDegrees(angle: Float): Vector2 = {
    this.x = Math.cos(angle * Math.Pi / 180).toFloat
    this.y = Math.sin(angle * Math.Pi / 180).toFloat
    this
  }
  
  def normalize(): Vector2 = {
    x *= 1/length
    y *= 1/length
    this
  }
  
  def rotate(angle: Float): Vector2 = {
    x += Math.cos(angle).toFloat
    y += Math.sin(angle).toFloat
    this
  }
  
  def ==(other: Vector2): Boolean = {
    x == other.x && y == other.y
  }
  
  def +(other: Vector2): Vector2 = {
    new Vector2(x + other.x, y + other.y)
  }
  
  def +(other: (Float, Float)): Vector2 = {
    new Vector2(x + other._1, y + other._2)
  }
  
  def +=(other: Vector2): Vector2 = {
    x += other.x
    y += other.y
    this
  }
  
  def +=(other: (Float, Float)): Vector2 = {
    x += other._1
    y += other._2
    this
  }
  
  def *(multiplier: Float): Vector2 = {
    new Vector2(x * multiplier, y * multiplier)
  }
  
  def *=(multiplier: Float): Vector2 = {
    x *= multiplier
    y *= multiplier
    this
  }
  
  def :=(other: Vector2) {
    x = other.x
    y = other.y
  }
  
  def :=(other: (Float, Float)) {
    x = other._1
    y = other._2
  }
  
  override def toString(): String = {
    "Vector2(" + x + ", " + y + ")"
  }
  
}