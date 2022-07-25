package tapir

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import coffeeshop.CoffeeShopService
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.SwaggerUI

import scala.concurrent.Future

object TapirServer {
  def main(args: Array[String]) = {
    implicit val as = ActorSystem()
    implicit val es = as.dispatcher

    val service = new CoffeeShopService()
    val tapirCoffeeEndpoint = new TapirCoffeeEndpoint(service)

    val routes = AkkaHttpServerInterpreter().toRoute(tapirCoffeeEndpoint.all)
    val openApi = OpenAPIDocsInterpreter().serverEndpointsToOpenAPI(
      tapirCoffeeEndpoint.all,
      "coffee server",
      "0.0.1"
    )

    val swagger =
      AkkaHttpServerInterpreter().toRoute(SwaggerUI[Future](openApi.toYaml))

    import akka.http.scaladsl.server.RouteConcatenation._

    Http()
      .newServerAt("localhost", 8080)
      .bind(routes ~ swagger)
      .foreach(binding => println(binding.toString))
  }
}
