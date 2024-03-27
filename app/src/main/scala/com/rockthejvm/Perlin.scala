package com.rockthejvm

object Perlin {
  case class Vector2(x: Double, y: Double) {
    def *(other: Vector2) =
      x * other.x + y * other.y
  }

  case class Vector3(x: Double, y: Double, z: Double) {
    def *(other: Vector3) =
      x * other.x + y * other.y + z * other.z
  }

  val permutation = makePermutation(256)

  def shuffle(arrayToShuffle: Array[Int]): Unit = {
    (1 until arrayToShuffle.length).foreach { e =>
      val index = Math.round(Math.random * (e - 1)).toInt
      val temp = arrayToShuffle(e)
      arrayToShuffle(e) = arrayToShuffle(index)
      arrayToShuffle(index) = temp
    }
  }

  def makePermutation(range: Int) = {
    val array = (0 until range).toArray
    shuffle(array)
    array ++ array
  }

  def getConstantVector2D(v: Int) = (v & 3) match { // this should be the same as v % 3
    case 0 => Vector2(1.0, 1.0)
    case 1 => Vector2(-1.0, 1.0)
    case 2 => Vector2(-1.0, -1.0)
    case _ => Vector2(1.0, -1.0)
  }

  def getConstantVector3D(v: Int) = (v & 7) match {
    case 0 => Vector3(1.0, 1.0, 1.0)
    case 1 => Vector3(-1.0, 1.0, 1.0)
    case 2 => Vector3(-1.0, -1.0, 1.0)
    case 3 => Vector3(1.0, -1.0, 1.0)
    case 4 => Vector3(1.0, 1.0, -1.0)
    case 5 => Vector3(-1.0, 1.0, -1.0)
    case 6 => Vector3(-1.0, -1.0, -1.0)
    case 7 => Vector3(1.0, -1.0, -1.0)
  }

  // an ease curve used by Perlin: for t in [0,1] we return 6t^5 - 15t^4 + 10t^3
  def fade(t: Double) =
    ((6 * t - 15) * t + 10) * t * t * t

  // basic linear interpolation
  def lerp(t: Double, a1: Double, a2: Double) =
    a1 + t * (a2 - a1)

  def noise2D(x: Double, y: Double): Double = {
    val cellX = Math.floor(x).toInt & 255
    val cellY = Math.floor(y).toInt & 255
    val xf = x - Math.floor(x)
    val yf = y - Math.floor(y)

    val topRight = Vector2(xf-1.0, yf-1.0)
    val topLeft = Vector2(xf, yf-1.0)
    val bottomRight = Vector2(xf-1.0, yf)
    val bottomLeft = Vector2(xf, yf)

    val valueTopRight = permutation(permutation(cellX + 1) + cellY + 1)
    val valueTopLeft = permutation(permutation(cellX) + cellY + 1)
    val valueBottomRight = permutation(permutation(cellX + 1) + cellY)
    val valueBottomLeft = permutation(permutation(cellX) + cellY)

    val dotTopRight = topRight * getConstantVector2D(valueTopRight)
    val dotTopLeft = topLeft * getConstantVector2D(valueTopLeft)
    val dotBottomRight = bottomRight * getConstantVector2D(valueBottomRight)
    val dotBottomLeft = bottomLeft * getConstantVector2D(valueBottomLeft)

    val u = fade(xf)
    val v = fade(yf)

    lerp(u, lerp(v, dotBottomLeft, dotTopLeft), lerp(v, dotBottomRight, dotTopRight))
  }

  def noise3D(x: Double, y: Double, z: Double): Double = {
    val cellX = Math.floor(x).toInt & 255
    val cellY = Math.floor(y).toInt & 255
    val cellZ = Math.floor(z).toInt & 255
    val xf = x - Math.floor(x)
    val yf = y - Math.floor(y)
    val zf = z - Math.floor(z)

    val topRightNear = Vector3(xf - 1.0, yf - 1.0, zf - 1.0)
    val topLeftNear = Vector3(xf, yf - 1.0, zf - 1.0)
    val bottomRightNear = Vector3(xf - 1.0, yf, zf - 1.0)
    val bottomLeftNear = Vector3(xf, yf, zf - 1.0)
    val topRightFar = Vector3(xf - 1.0, yf - 1.0, zf)
    val topLeftFar = Vector3(xf, yf - 1.0, zf)
    val bottomRightFar = Vector3(xf - 1.0, yf, zf)
    val bottomLeftFar = Vector3(xf, yf, zf)

    val valueTopRightNear = permutation(permutation(permutation(cellX + 1) + cellY + 1) + cellZ + 1)
    val valueTopLeftNear = permutation(permutation(permutation(cellX) + cellY + 1) + cellZ + 1)
    val valueBottomRightNear = permutation(permutation(permutation(cellX + 1) + cellY) + cellZ + 1)
    val valueBottomLeftNear = permutation(permutation(permutation(cellX) + cellY) + cellZ + 1)
    val valueTopRightFar = permutation(permutation(permutation(cellX + 1) + cellY + 1) + cellZ)
    val valueTopLeftFar = permutation(permutation(permutation(cellX) + cellY + 1) + cellZ)
    val valueBottomRightFar = permutation(permutation(permutation(cellX + 1) + cellY) + cellZ)
    val valueBottomLeftFar = permutation(permutation(permutation(cellX) + cellY) + cellZ)

    val dotTopRightNear = topRightNear * getConstantVector3D(valueTopRightNear)
    val dotTopLeftNear = topLeftNear * getConstantVector3D(valueTopLeftNear)
    val dotBottomRightNear = bottomRightNear * getConstantVector3D(valueBottomRightNear)
    val dotBottomLeftNear = bottomLeftNear * getConstantVector3D(valueBottomLeftNear)
    val dotTopRightFar = topRightNear * getConstantVector3D(valueTopRightFar)
    val dotTopLeftFar = topLeftNear * getConstantVector3D(valueTopLeftFar)
    val dotBottomRightFar = bottomRightNear * getConstantVector3D(valueBottomRightFar)
    val dotBottomLeftFar = bottomLeftNear * getConstantVector3D(valueBottomLeftFar)

    val u = fade(xf)
    val v = fade(yf)
    val w = fade(zf)

    lerp(u,
      lerp(v,
        lerp(w, dotBottomLeftNear, dotBottomLeftFar),
        lerp(w, dotTopLeftNear, dotTopLeftFar)
      ),
      lerp(v,
        lerp(w, dotBottomRightNear, dotBottomRightFar),
        lerp(w, dotTopRightNear, dotTopRightFar)
      )
    )
  }
}