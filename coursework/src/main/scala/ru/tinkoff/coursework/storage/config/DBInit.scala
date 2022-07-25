package ru.tinkoff.coursework.storage.config

import cats.effect._
import doobie.free.connection.ConnectionIO
import doobie.h2.H2Transactor
import doobie.implicits.toSqlInterpolator

class DBInit(implicit rtx: Resource[IO, H2Transactor[IO]]) {

  val initCarTable: ConnectionIO[Int] =
    sql"""
         create table if not exists carTable (
          id UUID default random_uuid() primary key,
          carModel varchar,
          number varchar,
          fuelLevel int not null,
          x int not null,
          y int not null
        )
       """.update.run
}
