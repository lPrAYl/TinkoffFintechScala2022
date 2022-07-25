package ru.tinkoff.lecture6

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._


object future_parallel_combinators extends App {
  def twice(x: Int)(implicit ec: ExecutionContext) = Future(x * 2)

  // because futures is non lazy, future1 and future2 will be launched in one time
  object forPar {
    import scala.concurrent.ExecutionContext.Implicits.global

    def fuInt(int: Int) = Future(int)

    val future1 = fuInt(1)
    val future2 = fuInt(2)

    for {
      a <- future1
      b <- future2
    } yield (a, b)

    // more elegant way

    val ab = future1 zip future2
  }

  object combinators {
    import scala.concurrent.ExecutionContext.Implicits.global
  // val ec = scala.concurrent.ExecutionContext.global

    val list = List(1,2,3,4,5)
    val traverse = Future.traverse(list)(twice)
    val sequence = Future.sequence(list.map(twice))
    val zip = Future.successful(1) zip Future.successful(2)
    val firstOf = Future.firstCompletedOf(list.map(twice))
  }

  Await.result(combinators.traverse, 100.seconds)
  Await.result(combinators.sequence, 100.seconds)
  Await.result(combinators.zip,      100.seconds)
  Await.result(combinators.firstOf,  100.seconds)
}
