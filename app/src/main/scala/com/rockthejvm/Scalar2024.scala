package com.rockthejvm

import org.scalajs.dom
import org.scalajs.dom.*

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.math.*
import scala.util.Random

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
  val nParticles = 2000
  val maxAge = 30
  val stepLength = 5

  def startVideo(): Unit = {
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

  def makeRandomField(): Unit = {
    offScreenContext.clearRect(0,0,offScreenCanvas.width, offScreenCanvas.height)
    offScreenContext.drawImage(video, 0,0, offScreenCanvas.width, offScreenCanvas.height)

    val imageData = offScreenContext.getImageData(0,0,offScreenCanvas.width, offScreenCanvas.height)
    val brightnessMatrix = calculateBrightness(imageData)

//    val time = Date.now() * 0.0001
    for {
      row <- 0 until gridSize
      col <- 0 until gridSize
    } {
      val scaledBrightness = brightnessMatrix(row)(col) * 0.002
      grid(row)(col) = Perlin.noise3D(row * 0.002, col * 0.002, scaledBrightness) * 2 * Math.PI
    }
  }

  def drawParticles(particleTrails: List[List[(Double, Double, Int)]]): Unit = {
    makeRandomField()

    context.fillStyle = "black"
    context.fillRect(0,0,displayWidth,displayHeight)

    val newParticles = particleTrails.head.map {
      case (x,y,age) =>
        val row = (y / resolution).toInt
        val col = (x / resolution).toInt
        if (row >= 0 && row < gridSize && col >= 0 && col < gridSize && age < maxAge) {
          val angle = grid(row)(col)
          (x + stepLength * cos(angle), y + stepLength * sin(angle), age + 1)
        } else {
          (Math.random() * displayWidth, Math.random() * displayHeight, 0)
        }
    }

    val newParticleTrails =
      if (particleTrails.length > maxAge)
        newParticles :: particleTrails.init // skip the last portion of every particle (last segment of their "snakes")
      else
        newParticles :: particleTrails

    newParticleTrails.init.zip(newParticleTrails.tail).zipWithIndex.foreach {
      case ((newPositions, oldPositions), age) =>
        newPositions.zip(oldPositions)
          .filter {
            case ((newX, newY, _), (x, y, _)) =>
              Math.abs(newX - x) <= stepLength && Math.abs(newY - y) <= stepLength
          }
          .foreach {
            case ((newX, newY, a), ((x, y, _))) =>
              context.beginPath()
              context.strokeStyle = lerpColorFade(age * 1.0 / maxAge, 0x0080FF, 0x8000FF)
              context.lineWidth = 1
              context.moveTo(x, y)
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
      (x, y, age)
    }

    drawParticles(List(particles.toList))
  }

  def main(args: Array[String]): Unit = {
    startVideo()
    makeGoodArt()
  }
}
