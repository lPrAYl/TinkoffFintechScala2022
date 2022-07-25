package httpClients

import sttp.capabilities
import sttp.client3.httpclient.HttpClientFutureBackend
import sttp.client3.{SttpBackend, UriContext, asString, basicRequest}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object HttpClientExample {
  def main(args: Array[String]) = {

    val backend: SttpBackend[Future, capabilities.WebSockets] =
      HttpClientFutureBackend()

    Await.result(
      basicRequest
        .get(
          uri"http://localhost:8080/api/v1/coffee/2db4e5db-8a7d-489e-b369-a67bbbec3192"
        )
        .response(asString)
        .send(backend)
        .map(response => {
          println(response.code)
          println(response.body)
        }),
      10.seconds
    )

  }
}
