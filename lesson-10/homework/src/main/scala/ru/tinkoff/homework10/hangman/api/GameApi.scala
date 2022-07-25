package ru.tinkoff.homework10.hangman.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import ru.tinkoff.homework10.hangman.logic.PlayerGameService

//import scala.annotation.nowarn

//@nowarn
class GameApi(gameService: PlayerGameService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._


  def route: Route = pathPrefix("game") {
    (get & path(LongNumber)) {
      id => complete(gameService.find(id))
    } ~
      (post & parameters("userName")) {
        userName => complete((gameService.startNewGame(userName)))
      } ~
      (post & path(LongNumber / "guess")) {
        id =>
          parameters("letter".as[Char]) {
            guess => complete(gameService.makeGuess(id, guess))
          }
      }
  }

  implicit val charUnmarshaller: Unmarshaller[String, Char] =
    Unmarshaller.strict[String, Char] { string =>
      if (string.length == 1) string.head
      else
        throw new IllegalArgumentException(
          "Got string or nothing but char expected"
        )
    }
}
