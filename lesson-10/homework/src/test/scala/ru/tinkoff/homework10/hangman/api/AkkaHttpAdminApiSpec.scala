package ru.tinkoff.homework10.hangman.api

import akka.http.scaladsl.server.Route
import ru.tinkoff.homework10.hangman.api.base.AdminApiSpecBase

class AkkaHttpAdminApiSpec extends AdminApiSpecBase {

  override val route: Route = Route.seal(
    new AdminApi(mockGameService).route
  )(exceptionHandler =
    HangmanExceptionHandler.exceptionHandler
  ) // ExceptionHandler - обрабатывает ошибки, которые произошли при обработке запрос)

}
