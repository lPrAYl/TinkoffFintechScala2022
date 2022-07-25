package ru.tinkoff.lecture6.future.assignment.assignment

import com.typesafe.scalalogging.StrictLogging
import ru.tinkoff.lecture6.future.assignment.bcrypt.AsyncBcrypt
import ru.tinkoff.lecture6.future.assignment.store.AsyncCredentialStore
//import ru.tinkoff.lecture6.future.assignment.util.Scheduler

import java.util.{Timer, TimerTask}
import scala.concurrent.{Promise, TimeoutException}

//import scala.reflect.runtime.universe.Try

//import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

//@nowarn("msg=never used")
class Assignment(bcrypt: AsyncBcrypt, credentialStore: AsyncCredentialStore)
                (implicit executionContext: ExecutionContext) extends StrictLogging {

  /**
    * проверяет пароль для пользователя
    * возвращает Future со значением false:
    *   - если пользователь не найден
    *   - если пароль не подходит к хешу
    */
  def verifyCredentials(user: String, password: String): Future[Boolean] =
    for {
      hashOpt <- credentialStore.find(user)
      verified <- hashOpt match {
        case Some(value) => bcrypt.verify(password, value)
        case None => Future.successful(false)
      }
    } yield verified

  /**
    * выполняет блок кода, только если переданы верные учетные данные
    * возвращает Future c ошибкой InvalidCredentialsException, если проверка не пройдена
    */
  def withCredentials[A](user: String, password: String)(block: => A): Future[A] =
    verifyCredentials(user, password).flatMap {
      case true => Future.successful(block)
      case false => Future.failed(new InvalidCredentialsException)
    }

  /**
    * хеширует каждый пароль из списка и возвращает пары пароль-хеш
    */
  def hashPasswordList(passwords: Seq[String]): Future[Seq[(String, String)]] =
    Future
      .traverse(passwords) { password =>
        bcrypt.hash(password)
          .map(hash => password -> hash)
      }

  /**
    * проверяет все пароли из списка против хеша, и если есть подходящий - возвращает его
    * если подходит несколько - возвращается любой
    */
  def findMatchingPassword(passwords: Seq[String], hash: String): Future[Option[String]] = {
    passwords.foldLeft(Future.successful(Option.empty[String])) {
      (foundPasswordOptFuture, password) =>
        foundPasswordOptFuture.flatMap {
          case None => bcrypt.verify(password, hash).map {
            case true => Option(password)
            case _    => None
          }
          case _ => foundPasswordOptFuture
        }
    }
  }

  /**
    * логирует начало и окончание выполнения Future, и продолжительность выполнения
    */
  def withLogging[A](tag: String)(f: => Future[A]): Future[A] = {
    val startTimeMillis = System.currentTimeMillis()
    logger.info(s"Start executing Future with tag: $tag")
    f.andThen { case _ =>
        val timeOfExecutionMillis = System.currentTimeMillis() - startTimeMillis
        logger.info(s"Finished executing Future with tag: $tag. " +
          s"Run time: $timeOfExecutionMillis milliseconds")
      }
  }

  /**
    * пытается повторно выполнить f retries раз, до первого успеха
    * если все попытки провалены, возвращает первую ошибку
    *
    * Важно: f не должна выполняться большее число раз, чем необходимо
    */
  def withRetry[A](f: => Future[A], retries: Int): Future[A] =
    f.recoverWith {
      case _ if retries > 0 => withRetry(f, retries - 1)
    }

  /**
    * по истечению таймаута возвращает Future.failed с java.util.concurrent.TimeoutException
    */
  def withTimeout[A](f: Future[A], timeout: FiniteDuration): Future[A] = {
    val timer = new Timer(true)
    val promise = Promise[A]()
    val timerTask = new TimerTask {
      override def run(): Unit = {
        promise.tryFailure(new TimeoutException())
        ()
      }
    }
    timer.schedule(timerTask, timeout.toMillis)
    f.map {
      result => if (promise.trySuccess(result)) timerTask.cancel()
    }
      .recover {
        case e: Exception => if (promise.tryFailure(e)) timerTask.cancel()
      }
    promise.future
  }

  /**
    * делает то же, что и hashPasswordList, но дополнительно:
    *   - каждая попытка хеширования отдельного пароля выполняется с таймаутом
    *   - при ошибке хеширования отдельного пароля, попытка повторяется в пределах retries (свой на каждый пароль)
    *   - возвращаются все успешные результаты
    */
  def hashPasswordListReliably(passwords: Seq[String], retries: Int, timeout: FiniteDuration): Future[Seq[(String, String)]] =
    Future.traverse(passwords) { password =>
      withRetry(withTimeout(bcrypt.hash(password), timeout), retries)
        .map(password -> _)
    }
}
