package ru.tinkoff.lecture6

import scala.concurrent._
import scala.io.StdIn
import scala.util.Success
import scala.concurrent.duration._
import java.util.concurrent.Executors

object promise_and_async_callbacks extends App {
  // in real apps we would use bounded pool for blocking IO
  val ioPool = Executors.newCachedThreadPool()
  def readLine(cb: String => Unit): Unit = {
    ioPool.submit(new Runnable {
      override def run(): Unit = cb(StdIn.readLine())
    })
    ()
  }

  def read: Future[String] = {
    val promise01 = Promise[String]()
    readLine({ x => promise01.complete(Success(x)) })
    promise01.future
  }

  println(Await.result(read.map(x => x * 10)(ExecutionContext.global), 10.seconds))  

  ioPool.shutdown()
}
