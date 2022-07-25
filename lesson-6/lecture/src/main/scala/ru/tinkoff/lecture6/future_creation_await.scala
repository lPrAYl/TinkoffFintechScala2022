package ru.tinkoff.lecture6

import scala.concurrent.Future
import scala.io.StdIn
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try


object future_creation_await extends App {
  val readyFuture = Future.successful(42)
  val readyFutureFailed = Future.failed[Int](new Exception("error"))
  val readyFutureFromTry = Future.fromTry(Try {42 / 1})

  object there_we_need_implicit_ec {
    import scala.concurrent.ExecutionContext.Implicits.global
  // val ec = scala.concurrent.ExecutionContext.global

    val computingFuture = Future { 
      println("computing")
      18 + 24 
    }

    import scala.concurrent.blocking
    val blockingFuture  = Future { blocking {
      println("cheburek " + StdIn.readLine())
    } }

    val someFuture = blockingFuture.flatMap(_ => computingFuture)

    someFuture.onComplete{res => println("hello onComplete " + res)}

    // keep order
    someFuture
      .andThen(res => println("hello andThen " +res))
      .andThen(res => println("keeping order " +res))
  }

  println(Await.result(there_we_need_implicit_ec.someFuture, 100.seconds))
  println(Await.result(there_we_need_implicit_ec.someFuture, 100.seconds))
}
