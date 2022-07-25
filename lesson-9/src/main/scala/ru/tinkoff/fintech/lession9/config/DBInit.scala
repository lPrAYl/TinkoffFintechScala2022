package ru.tinkoff.fintech.lession9.config

import cats.effect._
import doobie.free.connection.ConnectionIO
import doobie.h2.H2Transactor
import doobie.implicits.toSqlInterpolator

import java.util.UUID

class DBInit(implicit rtx: Resource[IO, H2Transactor[IO]]) {

  val initUserTable: ConnectionIO[Int] =
    sql"""
          create table if not exists userTable (
            id  UUID default random_uuid() primary key,
            profile varchar
          )
         """.update.run

  val initEmailTable: ConnectionIO[Int] =
    sql"""
          create table if not exists emailTable (
            id    UUID not null references userTable(id) on delete cascade,
            email     varchar not null unique,
            password  varchar not null
          )
      """.update.run

  val initSessionTable: ConnectionIO[Int] =
    sql"""
          create table if not exists sessionTable (
            id  UUID not null references userTable(id) on delete cascade,
            session varchar not null unique
            )
       """.update.run

    val initDocumentTable: ConnectionIO[Int] =
      sql"""
            create table if not exists documentTable (
              id      UUID not null default random_uuid(),
              link    varchar not null unique,
              version int not null,
              creator UUID not null,
              editor  UUID not null
              )
        """.update.run
}
