package ru.tinkoff.lecture6

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.Success
import scala.util.Failure

object future_error_handling extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.blocking

  // val ec = scala.concurrent.ExecutionContext.global
 
  def input = Future { blocking {
    StdIn.readLine()
  } }

  def toIntF(string: String) = Future.fromTry(Try { string.toInt })
  
  def readAndDecode = for {
    str <- input
    int <- toIntF(str)
  } yield int * 3


  def handled1 = readAndDecode.recover {
    case _: NumberFormatException => 0
  }

  def handled2 = readAndDecode.recoverWith {
    case _: NumberFormatException => readAndDecode
  }

  def handled3 = readAndDecode.transform {
    case Success(x) => Success(Some(x))
    case Failure(_) => Success(None)
  }

  def handled4 = readAndDecode.transformWith {
    case Success(x) => Future.successful(x)
    case Failure(_) => readAndDecode
  }

  def handled5: Future[Int] = readAndDecode.fallbackTo(readAndDecode)

  println(
    Await.result(handled5, 100.seconds)
  )
}