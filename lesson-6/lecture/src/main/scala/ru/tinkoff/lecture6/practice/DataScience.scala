package ru.tinkoff.lecture6.practice

import java.io.Closeable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.{BufferedSource, Source}

object DataScience extends App {

  private val defaultFileName = "in.txt"

  private def getSource(fileName: String): BufferedSource =
    Source.fromResource(fileName)

  private def read(in: BufferedSource): Future[Iterator[String]] =
    Future(blocking(in.getLines()))


  def asyncWithResource[R <: Closeable, T](resource: R)(code: R => Future[T]): Future[T] = 
    code(resource).andThen {case _ => resource.close() }

  def asyncCountLines: Future[Int] = asyncWithResource(getSource(defaultFileName)) { source => 
    read(source).map(_.size)
  }

  def asyncLineLengths: Future[Seq[(String, Int)]] = asyncWithResource(getSource(defaultFileName)) { source => 
    for {
      iteratror <- read(source)
    } yield iteratror.map(line => line -> line.length()).toSeq
  }

  def asyncTotalLength: Future[Int] = asyncWithResource(getSource(defaultFileName)) { source => 
    read(source).map(lines => lines.map(_.length()).sum)
  }

  def countShorterThan(maxLength: Int): Future[Int] = asyncWithResource(getSource(defaultFileName)) { source => 
    for {
      iteratror <- read(source)
    } yield iteratror.count(_.length() < maxLength)
  }


  val result = Await.result(asyncLineLengths, 100.seconds)
  println(result)
}
