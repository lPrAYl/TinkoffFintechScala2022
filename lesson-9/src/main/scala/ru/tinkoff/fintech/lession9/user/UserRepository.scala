package ru.tinkoff.fintech.lession9.user

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2._
import doobie.h2.implicits.UuidType
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.implicits.toSqlInterpolator

import java.util.UUID

trait UserRepository[F[_]] {
  def createUser(user: User): F[Boolean]

  def getById(id: UUID): F[Option[User]]

  def getByEmail(email: String): F[Option[User]]

  def getBySession(session: String): F[Option[User]]

  def attachEmail(userId: UUID, email: String, password: String): F[Boolean]

  def authorizeEmail(session: String, email: String, password: String): F[Option[User]]
}

class UserRepositoryImpl(implicit rtx: Resource[IO, H2Transactor[IO]]) extends UserRepository[ConnectionIO] {

  override def createUser(user: User): ConnectionIO[Boolean] =
    sql"insert into userTable (id, profile) values (${user.id}, ${user.profile})"
      .update
      .run
      .attemptSql.map {
      case Left(_) => false
      case Right(value) => value != 0
    }

  override def getById(id: UUID): ConnectionIO[Option[User]] =
    sql"select id, profile from userTable where id = $id"
      .query[User]
      .option

  override def getByEmail(email: String): ConnectionIO[Option[User]] =
    sql"""select userTable.id, profile from userTable join emailTable
         on userTable.id = emailTable.id
         where email = $email
         """
      .query[User]
      .option

  override def getBySession(session: String): ConnectionIO[Option[User]] =
    sql"""select userTable.id, profile from userTable join sessionTable
         on userTable.id = sessionTable.id
         where session = $session
         """
      .query[User]
      .option

  override def attachEmail(userId: UUID, email: String, password: String): ConnectionIO[Boolean] =
    sql"insert into emailTable (id, email, password) values ($userId, $email, $password)"
      .update
      .run
      .attemptSql.map {
      case Left(_) => false
      case Right(value) => value != 0
    }

  override def authorizeEmail(session: String, email: String, password: String): ConnectionIO[Option[User]] = {
    for {
      user <-
        sql"""
              select userTable.id, profile from userTable join emailTable
              on userTable.id = emailTable.id
              where email = $email and password = $password
             """
        .query[User]
        .option

      authorized <- user match {
        case Some(v) => sql"insert into sessionTable (id, session) values (${v.id}, $session)"
          .update
          .run
          .attemptSql.map {
          case Left(_) => None
          case Right(_) => user
        }
        case None => None.pure[ConnectionIO]
      }
    } yield authorized
  }
}

