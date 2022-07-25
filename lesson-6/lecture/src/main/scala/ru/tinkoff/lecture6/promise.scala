package ru.tinkoff.lecture6

import scala.concurrent._
import scala.io.StdIn
import scala.concurrent.duration._
import scala.util.Success

object promise extends App {
  val promise01 = Promise[String]()
  val future01 = promise01.future

  val th = new Thread(new Runnable {
    override def run(): Unit =
      promise01.complete(
        Success{
          println("input?");
          StdIn.readLine()
        }
      )
  })
  th.setDaemon(true)
  th.start()

  val promise02 = Promise[String]()
  promise02.completeWith(future01)

  val future02 = promise02.future

  println(Await.result(future01.map(x => x * 10)(ExecutionContext.global), 10.seconds))
}
