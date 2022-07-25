import java.util.UUID
import java.util.concurrent.Executor

import scala.util.{Failure, Success, Try}
import scala.util.control.NoStackTrace
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

sealed trait MCall
case class SendCall(ret: Either[String, UUID], text: String, user: String) extends MCall
case class LikeCall(ret: Either[String, Boolean], id: UUID) extends MCall
case class UnlikeCall(ret: Either[String, Boolean], id: UUID) extends MCall
case class GetCall(ret: Either[String, (UUID, String, String, Int)], id: UUID) extends MCall


object SocialNetRemoteEmulator {
  val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  def apply(calls0: List[MCall]) = new SocialNetRemoteEmulator(calls0, ec)
}


class SocialNetRemoteEmulator(calls0: List[MCall], ec: Executor) extends SocialNetRemote {
  final val lock = new String("locklock")
  var _calls = calls0

  private def fireAndForget(r: () => Unit): Unit = {
    ec.execute(new Runnable {
      override def run(): Unit = {
        try {
          r()
        } catch {
          case e: Throwable => println("exception in emulator " + e.getMessage)
        }
      }
    })
  }

  private def popCall[T](selector: PartialFunction[MCall, T]): T = lock.synchronized[T] {
    _calls.find(selector.isDefinedAt) match {
      case None => throw new IllegalStateException("unexpected call") with NoStackTrace
      case Some(c) =>
        _calls = _calls.filterNot(_ eq c)
        selector(c)
    }
  }

  private def ett[T](e: Either[String, T]): Try[T] = e match {
    case Left(msg) => Failure(new RuntimeException(msg) with NoStackTrace)
    case Right(v)  => Success(v)
  }

  def calls() = lock.synchronized {
    _calls
  }

  override def send(Text: Text, User: User)(cb: Try[Id] => Unit): Unit = {
    val call = popCall({case SendCall(ret, Text, User) => ett(ret)})
    fireAndForget(() => cb(call))
  }

  override def like(Id: Id)(cb: Try[Boolean] => Unit): Unit = {
    val call = popCall({case LikeCall(ret, Id) => ett(ret)})
    fireAndForget(() => cb(call))
  }

  override def unlike(Id: Id)(cb: Try[Boolean] => Unit): Unit = {
//    println(s"Unlike $Id on ${calls()}")
    val call = popCall({case UnlikeCall(ret, Id) => ett(ret)})
    fireAndForget(() => cb(call))
  }

  override def get(Id: Id)(cb: Try[(Id, User, Text, LikeCount)] => Unit): Unit = {
//    println(s"Get $Id on ${calls()}")
    val call = popCall({case GetCall(ret, Id) => ett(ret)})
    fireAndForget(() => cb(call))
  }
}