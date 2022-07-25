package ru.tinkoff.coursework.test

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2._
import doobie.implicits._
import doobie.util.ExecutionContexts
import ru.tinkoff.coursework.CarStatus.Free
import ru.tinkoff.coursework.logic.Point
import ru.tinkoff.coursework.{Car, Feature}
import ru.tinkoff.coursework.storage.car.CarRepositoryImpl
import ru.tinkoff.coursework.storage.config.DBInit

import java.util.UUID

object Test extends IOApp {

  implicit val transactor: Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        "sa",
        "",
        ce
      )
    } yield xa

  val dbInit = new DBInit()
  val carRepository: CarRepositoryImpl = new CarRepositoryImpl

  val featureOne = Feature("kia", "001", 100, Point(10, 10))
  val carOne = Car(UUID.randomUUID(), featureOne, Free)
  val featureTwo = Feature("nissan", "002", 100, Point(20, 20))
  val carTwo = Car(UUID.randomUUID(), featureTwo, Free)
  val featureThree = Feature("toyota", "003", 100, Point(30, 30))
  val carThree = Car(UUID.randomUUID(), featureThree, Free)

  def init: IO[ExitCode] = transactor.use { xa =>
    for {
      _ <- dbInit.initCarTable.transact(xa)
    } yield ExitCode.Success
  }

  def addCar: IO[ExitCode] = transactor.use { xa =>
    for {
      added <- carRepository.addCar(carOne).transact(xa)      //  add userOne
      _ <- IO.pure(println(s"add userOne - $added"))
      addedAgain <- carRepository.addCar(carOne).transact(xa) // try add userOne again
      _ <- IO.pure(println(s"try add userOne again - $addedAgain"))
      _ <- carRepository.addCar(carTwo).transact(xa)
      _ <- carRepository.addCar(carThree).transact(xa)
    } yield ExitCode.Success
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- init         //  сreating empty table
      _ <- addCar      //  adding cars in carTable
    } yield ExitCode.Success
  }
}
