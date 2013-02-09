package net.faintedge.subsurf

object Util {

  def ofDim[T: ClassManifest](n1: Int, n2: Int, default: (Int, Int) => T): Array[Array[T]] = {
    val arr: Array[Array[T]] = (new Array[Array[T]](n1): Array[Array[T]])
    for (i <- 0 until n1) {
      arr(i) = new Array[T](n2)
      for (j <- 0 until n2) {
        arr(i)(j) = default.apply(i, j)
      }
    }
    arr
  }
  
  def constrain(num: Int, min: Int, max: Int): Int = {
    Math.max(Math.min(num, max), min)
  }
  
}