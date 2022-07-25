package ru.tinkoff.lecture6.future.assignment

import com.typesafe.config.ConfigFactory
import ru.tinkoff.lecture6.future.assignment.assignment.InvalidCredentialsException

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.{Future, TimeoutException}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
//import org.scalatest.Ignore
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.lecture6.future.assignment.assignment.Assignment
import ru.tinkoff.lecture6.future.assignment.bcrypt.AsyncBcryptImpl

//@Ignore
class AssignmentTest extends AsyncFlatSpec with Matchers {

  val config = ConfigFactory.load()
  val credentialStore = new ConfigCredentialStore(config)
  val reliableBcrypt = new AsyncBcryptImpl
  val assignment = new Assignment(reliableBcrypt, credentialStore)

  import assignment._

  behavior of "verifyCredentials"

  it should "return true for valid user-password pair" in {
    verifyCredentials("winnie", "pooh").map { result =>
      result shouldBe true
    }
  }

  it should "return false if user does not exist in store" in {
    verifyCredentials("swinnie", "pooh").map { result =>
      result shouldBe false
    }
  }
  it should "return false for invalid password" in {
    verifyCredentials("winnie", "poooh").map { result =>
      result shouldBe false
    }
  }

  behavior of "withCredentials"

  it should "execute code block if credentials are valid" in {
    withCredentials("winnie", "pooh")(21 + 21)
      .map(result => result shouldBe 42)
  }
  it should "not execute code block if credentials are not valid" in
    withCredentials("swinnie", "pooh")(21 + 21)
      .recover {
        case _: InvalidCredentialsException => 0
      }.map(result => result shouldBe 0)

  behavior of "hashPasswordList"

  it should "return matching password-hash pairs" in {
    val passwords = Seq("pooh", "doge")
    for {
      hashPasswordPairs <- hashPasswordList(passwords)
      verified          <- Future.traverse(hashPasswordPairs) {
        case (password, hash) => reliableBcrypt.verify(password, hash)
      }
    } yield verified.forall(identity) shouldBe true
  }

  behavior of "findMatchingPassword"

  it should "return matching password from the list" in {
    val passwordForTest = "password"
    val passwords = Seq("pooh", "doge", "password")
    for {
      hash              <- reliableBcrypt.hash(passwordForTest)
      matchingPassword  <- findMatchingPassword(passwords, hash)
    } yield matchingPassword shouldBe Some(passwordForTest)
  }

  it should "return None if no matching password is found" in {
    val passwordForTest = "password"
    val passwords = Seq("pooh", "doge")
    for {
      hash              <- reliableBcrypt.hash(passwordForTest)
      matchingPassword  <- findMatchingPassword(passwords, hash)
    } yield matchingPassword shouldBe None
  }

  behavior of "withRetry"

  it should "return result on passed future's success" in {
    val counter = new AtomicInteger(0)

    withRetry(Future {
      counter.incrementAndGet()
    }, 1).map(_ shouldBe 1)
  }

  it should "not execute more than specified number of retries" in {
    val counter = new AtomicInteger(0)
    val retries = 2

    withRetry(Future {
      if (counter.get < retries) {
        counter.incrementAndGet
        throw new RuntimeException
      } else counter.get
    }, retries).map(_ shouldBe retries)
  }

  it should "not execute unnecessary retries" in {
    val counter = new AtomicInteger(0)
    val retries = 2

    withRetry(Future {
      if (counter.get < retries - 1) {
        counter.incrementAndGet
        throw new RuntimeException
      } else counter.get
    }, retries).map(_ shouldBe retries - 1)
  }
  it should "return the first error, if all attempts fail" in {
    val counter = new AtomicInteger(0)
    val retries = 3

    case class ForTestException(msg: String) extends Exception

    withRetry(Future {
      throw ForTestException(counter.get.toString)
    }, retries)
      .recover { case ForTestException(x) => x}
      .map(_ shouldBe "0")
  }

  behavior of "withTimeout"

  it should "return result on passed future success" in {
    withTimeout(Future(42), 3 seconds).map(_ shouldBe 42)
  }

  it should "return result on passed future failure" in {
    withTimeout(Future.failed(new RuntimeException), 3 seconds)
      .recover { case _: RuntimeException => 0 }
    .map(_ shouldBe 0)
  }

  it should "complete on never-completing future" in {
    withTimeout(Future.never, 3 seconds)
      .recover { case _: TimeoutException => 0 }
      .map(_ shouldBe 0)
  }

  behavior of "hashPasswordListReliably"
  val assignmentFlaky = new Assignment(new FlakyBcryptWrapper(reliableBcrypt), credentialStore)

  it should "return password-hash pairs for successful hashing operations" in {
    val passwords = Seq("pooh", "doge")
    for {
      hashPasswordPairs <- assignmentFlaky.hashPasswordListReliably(passwords, retries = 3, timeout = 1 seconds)
      verified          <- Future.sequence {
        hashPasswordPairs.map {case (password, hash) => reliableBcrypt.verify(password, hash) }
      }
    } yield verified.forall(identity) shouldBe true
  }
}
