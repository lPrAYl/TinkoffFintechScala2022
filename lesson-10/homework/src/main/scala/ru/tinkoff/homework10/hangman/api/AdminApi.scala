package ru.tinkoff.homework10.hangman.api

import ru.tinkoff.homework10.hangman.logic.GameService

class AdminApi(gameService: GameService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.marshaller

  val route = pathPrefix("admin") {
    (get & path("game" / LongNumber)) {
      id => complete(gameService.find(id))
    }
  }
}
