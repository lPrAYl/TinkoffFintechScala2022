package ru.tinkoff.lecture6.practice

import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.Promise

// import scala.annotation.nowarn
// @nowarn
object SleepSort extends App {
  /*
    Sleep sort
    https://www.quora.com/What-is-sleep-sort
   */

  val scheduler = Executors.newSingleThreadScheduledExecutor()

  def printWithDelay(delay: FiniteDuration, s: String): Future[Unit] = {
    val promise = Promise[Unit]()
    scheduler
      .schedule(() => promise.success(println(s)), delay.length, delay.unit)
    promise.future
  }

  def sleepSort[T](
    itemsF: Future[Seq[T]], 
    rank: T => Int,
    show: T => String
  )(implicit ec: ExecutionContext): Future[Unit] = 
    for {
      items <- itemsF
      _     <- Future.traverse(items){i => printWithDelay(rank(i).millis, show(i))}
    } yield ()



  val result = Await.result(
    sleepSort(
      DataScience.asyncLineLengths,
      {x: (String, Int) => x._2},
      {x: (String, Int) => x._1}
    )(scala.concurrent.ExecutionContext.global), 
    100.seconds
  )

  scheduler.shutdown()
}
