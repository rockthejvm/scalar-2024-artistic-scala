package com.rockthejvm

import org.scalajs.dom
import org.scalajs.dom.*

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.math.*

object Scalar2024 {
  import Tools.*

  val displayCanvas = dom.document.getElementById("displayCanvas").asInstanceOf[HTMLCanvasElement]
  val context: CanvasRenderingContext2D = displayCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val offScreenCanvas = dom.document.getElementById("offCanvas").asInstanceOf[HTMLCanvasElement]
  val offScreenContext:CanvasRenderingContext2D = offScreenCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val video = dom.document.getElementById("video").asInstanceOf[HTMLVideoElement]

  val displayWidth = displayCanvas.width
  val displayHeight = displayCanvas.height
  val gridSize = 100
  val grid = Array.ofDim[Double](gridSize, gridSize)
  val resolution = displayWidth / gridSize
  val nParticles = 4000
  val maxAge = 30
  val stepLength = 5

  def startVideoMagic(): Unit = {
    dom.window.navigator.mediaDevices.getUserMedia(new MediaStreamConstraints {
      this.video = new MediaTrackConstraints {
        width = 100
        height = 100
      }
    }).`then` { stream =>
      video.srcObject = stream
      video.play()
    }
  }

  def makeSpecialFlowField(): Unit = {
    val scale = Math.min(offScreenCanvas.width / video.width, offScreenCanvas.height / video.height);
    val vidWidth = scale * video.videoWidth
    val vidHeight = scale * video.videoHeight

    offScreenContext.clearRect(0, 0, offScreenCanvas.width, offScreenCanvas.height)
    offScreenContext.drawImage(video, 0, 0, offScreenCanvas.width, offScreenCanvas.height)

    val imageData = offScreenContext.getImageData(0, 0, displayWidth, displayHeight)
    val brightnessMatrix = calculateBrightness(imageData) // todo recalculate the brightness matrix every 20 frames
    //    val scaledTime = Date.now() * 0.00005
    for {
      col <- 0 until gridSize
      row <- 0 until gridSize
    } {
      val x = col * displayWidth / gridSize
      val y = row * displayHeight / gridSize
      // perlin noise 3D
      val scaledX = col * 0.002
      val scaledY = row * 0.002
      val scaledZ = brightnessMatrix(row)(col) * 0.0015

      val angle = Perlin.noise3D(scaledX, scaledY, scaledZ) * Math.PI * 2

      grid(row)(col) = angle
    }
  }

  def makeRandomField(): Unit = {
    val time = Date.now() * 0.00005
    for {
      row <- 0 until gridSize
      col <- 0 until gridSize
    } {
      val scaledX = col * 0.02
      val scaledY = row * 0.01
      grid(row)(col) = Perlin.noise3D(scaledX, scaledY, time) * Math.PI * 2
    }
  }

  def drawParticles(particleTrails: List[List[(Double, Double, Int)]]): Unit = {
    makeSpecialFlowField()

    context.fillStyle = "black"
    context.fillRect(0,0,displayWidth, displayHeight)

    val newParticles = particleTrails.head.map {
      case (x,y,age) =>
        val row = (y / resolution).toInt
        val col = (x / resolution).toInt
        if (row >= 0 && row < gridSize && col >= 0 && col < gridSize && age < maxAge) {
          val angle = grid(row)(col)
          (x + stepLength * cos(angle), y + stepLength * sin(angle), age + 1)
        } else {
          val newX = Math.random() * displayWidth
          val newY = Math.random() * displayHeight
          (newX, newY, 0)
        }
    }

    // new snakes
    val newParticleTrails =
      if (particleTrails.length > maxAge)
        newParticles :: particleTrails.init
      else
        newParticles :: particleTrails

    newParticleTrails.init.zip(newParticleTrails.tail).zipWithIndex.foreach {
      case ((newParticles, oldParticles), age) =>
        newParticles.zip(oldParticles)
          .filter {
            case ((newX, newY, _), (x, y, _)) =>
              Math.abs(newX - x) <= stepLength && Math.abs(newY - y) <= stepLength
          }
          .foreach {
            case ((newX, newY, a), (x, y, _)) =>
              context.beginPath()
              context.strokeStyle = lerpColorFade(age * 1.0 / maxAge, 0x0080FF, 0x8000FF)
              context.lineWidth = 1
              context.moveTo(x,y)
              context.lineTo(newX, newY)
              context.stroke()
          }
    }

    dom.window.requestAnimationFrame(_ => drawParticles(newParticleTrails))
  }

  def makeGoodArt(): Unit = {

    val particles = (1 to nParticles).map { _ =>
      val x = Math.random() * displayWidth
      val y = Math.random() * displayHeight
      val age = (Math.random() * maxAge).toInt
      (x,y,age)
    }

    drawParticles(List(particles.toList))
  }

  def main(args: Array[String]): Unit = {
    startVideoMagic()
    makeGoodArt()
  }
}
