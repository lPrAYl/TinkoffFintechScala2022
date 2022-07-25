package ru.tinkoff.homework10.hangman.api

import akka.http.scaladsl.server.Route
import ru.tinkoff.homework10.hangman.api.base.GameApiSpecBase

class AkkaHttpGameApiSpec extends GameApiSpecBase {
  override val route = Route.seal(
    new GameApi(mockGameService).route
  )(exceptionHandler =
    HangmanExceptionHandler.exceptionHandler
  ) // ExceptionHandler - обрабатывает ошибки, которые произошли при обработке запроса

}
