package ru.tinkoff.fintech.lession9.document

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2._
import doobie.h2.implicits.UuidType
import doobie.implicits._
import doobie.util.ExecutionContexts

import java.util.UUID

trait DocumentRepository[F[_]] {
  def createVersion(id: UUID, link: String, lastVersion: Int, editor: UUID): F[Boolean]

  def getByEditor(id: UUID): F[Seq[Document]]

  def getById(id: UUID): F[Seq[Document]]
}

class DocumentRepositoryImpl(implicit rtx: Resource[IO, H2Transactor[IO]]) extends DocumentRepository[ConnectionIO] {

  override def createVersion(id: UUID, link: String, lastVersion: Int, editor: UUID): ConnectionIO[Boolean] = {
    for {
      selected <-
        sql"""
              select id, link, version, creator, editor from documentTable
              where id = $id
              order by version
              desc limit 1
             """
        .query[Document]
        .option

      created <- selected match {
        case Some(document) => if (document.version < lastVersion)
          sql"""
                insert into documentTable (id, link, version, creator, editor)
                values ($id, $link, $lastVersion, ${document.creator}, $editor)
               """
            .update
            .run
            .attemptSql.map {
            case Left(_) => false
            case Right(value) => value != 0
          } else false.pure[ConnectionIO]
        case None =>
          sql"""
                insert into documentTable (id, link, version, creator, editor)
                values ($id, $link, $lastVersion, $editor, $editor)
               """
            .update
            .run
            .attemptSql.map {
            case Left(_) => false
            case Right(value) => value != 0
          }
      }
    } yield created
  }

  override def getById(id: UUID): ConnectionIO[Seq[Document]] =
    sql"""
          select * from documentTable
          where id = $id
         """
      .query[Document]
      .stream
      .compile
      .to(Seq)

  override def getByEditor(id: UUID): ConnectionIO[Seq[Document]] =
    sql"""
          select * from documentTable
          where editor = $id
         """
      .query[Document]
      .stream
      .compile
      .to(Seq)
}
