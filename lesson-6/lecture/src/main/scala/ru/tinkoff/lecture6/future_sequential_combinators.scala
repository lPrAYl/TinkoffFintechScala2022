package ru.tinkoff.lecture6

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.Await

object future_sequential_combinators extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.blocking

  // val ec = scala.concurrent.ExecutionContext.global
 
  def input = Future { blocking {
    println("input?")
    StdIn.readLine()
  } }(global)

  def toIntF(string: String) = Future.fromTry(Try { string.toInt })
  
  val res1 = input.flatMap(toIntF).map(_ * 3)
  // Await.result(res1, 100.seconds)

  val res2 = for {
    str <- input
    int <- toIntF(str)
  } yield int * 3

  println(Await.result(res2, 100.seconds))
}