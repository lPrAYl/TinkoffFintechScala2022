package ru.tinkoff.lecture6.practice

import scala.concurrent.Future
import scala.collection.concurrent.TrieMap

import scala.concurrent.Promise

class Cache[I, O](f: I => Future[O]) extends Function1[I, Future[O]] {
  val storage = TrieMap.empty[I, Future[O]]

  //hint use storage.putIfAbsent and Promise

  def apply(in: I): Future[O] = {
    val p = Promise[O]()
    storage.putIfAbsent(in, p.future) match {
      case None           => p.completeWith(f(in))
      case Some(existing) => p.completeWith(existing)
    }
    p.future
  }
}