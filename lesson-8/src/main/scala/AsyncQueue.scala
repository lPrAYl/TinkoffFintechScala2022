import scala.concurrent.Future

import collection.mutable.Queue
import scala.concurrent.{Future, Promise}

class AsyncQueue[T] {

  @volatile private var underlying = Queue.empty[T]
  @volatile private var queuePromise = Queue.empty[Promise[T]]

  def add(item: T): Unit = this.synchronized {
    queuePromise.isEmpty match {
      case false => queuePromise.dequeue().success(item)
      case true => Future.successful { underlying.enqueue(item) }
    }
  }

  def take(): Future[T] = this.synchronized {
    underlying.isEmpty match {
      case false => Future.successful { underlying.dequeue() }
      case true =>
        val promise = Promise[T]()
        queuePromise.enqueue(promise)
        promise.future
    }
  }
}
