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

  def main(args: Array[String]): Unit = {

  }
}
