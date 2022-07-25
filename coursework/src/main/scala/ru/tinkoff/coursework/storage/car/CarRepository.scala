package ru.tinkoff.coursework.storage.car

import cats.effect.IO
import cats.effect.kernel.Resource
import doobie.implicits._
import doobie.free.connection.ConnectionIO
import doobie.h2.H2Transactor
import doobie.implicits.toSqlInterpolator
import doobie.h2.implicits.UuidType

import ru.tinkoff.coursework.Car

import java.util.UUID

trait CarRepository[F[_]] {
  def addCar(car: Car): F[Boolean]

//  def getCarById(id: UUID): F[Option[Car]]
}

class CarRepositoryImpl(implicit rtx: Resource[IO, H2Transactor[IO]]) extends CarRepository[ConnectionIO] {

  override def addCar(car: Car): ConnectionIO[Boolean] =
    sql"""
          insert into carTable (id, carModel, number, fuelLevel, x, y)
            values (${car.id}, ${car.carFeature.carModel}, ${car.carFeature.number}, ${car.carFeature.fuelLevel},
            ${car.carFeature.position.x}, ${car.carFeature.position.y})
          """
      .update
      .run
      .attemptSql.map {
      case Left(_) => false
      case Right(value) => value != 0
    }

//  override def getCarById(id: UUID): ConnectionIO[Option[Car]] = ???
}
