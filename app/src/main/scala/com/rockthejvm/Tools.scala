package com.rockthejvm


import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement, HTMLVideoElement, ImageData, MediaStreamConstraints, MediaTrackConstraints}


object Tools {
  def calculateBrightness(imageData: ImageData) = {
    val brightnessMatrix = Array.ofDim[Double](imageData.height, imageData.width)
    (0 until imageData.height).foreach { y =>
      (0 until imageData.width).foreach { x =>
        val index = (y * imageData.width + x) * 4
        val r = imageData.data(index)
        val g = imageData.data(index + 1)
        val b = imageData.data(index + 2)
        val brightness = 0.299 * r + 0.587 * g + 0.114 * b
        brightnessMatrix(y)(x) = brightness
      }
    }

    brightnessMatrix
  }

  // returns a CSS color in between a and b, with alpha in between
  def lerpColorFade(v: Double, a: Int, b: Int): String = {
    val redA = (a & 0xFF0000) >> 16
    val greenA = (a & 0xFF00) >> 8
    val blueA = a & 0xFF
    val redB = (b & 0xFF0000) >> 16
    val greenB = (b & 0xFF00) >> 8
    val blueB = b & 0xFF

    val t =
      if (v < 0) 0
      else if (v > 1) 1
      else v

    val red = (t * redA + (1 - t) * redB).toInt
    val green = (t * greenA + (1 - t) * greenB).toInt
    val blue = (t * blueA + (1 - t) * blueB).toInt
    val alpha = 1-t

    s"rgba($red,$green,$blue,$alpha)"
  }

  extension (ctx: CanvasRenderingContext2D) {
    def drawArrow(x: Double, y: Double, length: Double, angle: Double): Unit = {
      ctx.save()
      ctx.translate(x, y)
      ctx.rotate(angle)

      ctx.beginPath();
      ctx.moveTo(0, 0);

      // Main line of the arrow
      ctx.lineTo(length, 0);

      // Arrowhead
      ctx.lineTo(length - 2, -2);
      ctx.moveTo(length, 0);
      ctx.lineTo(length - 2, 2);

      // Set the style of the arrow
      ctx.strokeStyle = "black"
      ctx.lineWidth = 1;
      ctx.stroke();
      ctx.restore()
    }
  }
}
