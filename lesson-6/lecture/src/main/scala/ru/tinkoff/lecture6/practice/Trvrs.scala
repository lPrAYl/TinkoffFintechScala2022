package ru.tinkoff.lecture6.practice

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object Trvrs {
  // fold list fo futures to Future[A] with given fold operations
  def foldLeft[T, A](fs: List[Future[T]], zero: A)(fold: (A, T) => A)(implicit ec: ExecutionContext): Future[A] =
    fs.foldLeft(Future.successful(zero)) {(accF, xF) =>
      for {
        acc <- accF
        x   <- xF
      } yield fold(acc, x)
    }

  // traverse list with function and recover
  def trvrs[T, A](
    xs: List[A],
    f:  A => Future[T]
  )(recover: Throwable => Future[T])(implicit ec: ExecutionContext): Future[List[T]] = 
    foldLeft(
      xs.map(x => f(x).recoverWith{case t => recover(t)}),
      List.empty[T]
    ) {case (xs, x) => x :: xs}.map(_.reverse)
}
