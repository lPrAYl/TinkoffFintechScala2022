package ru.tinkoff.coursework

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import ru.tinkoff.coursework.api.{CarsharingHandlerException}

object CarsharingHttpApp {
  implicit val ac: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = ac.dispatcher

  def main(argc: Array[String]): Unit = {
    Await.result(Carsharing().start(), Duration.Inf)
    ()
  }
}

case class Carsharing()(implicit ac: ActorSystem, ec: ExecutionContext) extends LazyLogging {

  val routes = Route.seal(
    ???
  )(exceptionHandler = CarsharingHandlerException.exceptionHandler)

  def start(): Future[Http.ServerBinding] =
    Http()
      .newServerAt("localhost", 8080)
      .bind(routes)
      .andThen { case b => logger.info(s"server started at: $b") }
}
