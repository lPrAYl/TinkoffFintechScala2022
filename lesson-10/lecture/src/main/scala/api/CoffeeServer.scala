package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, handleExceptions}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.ExceptionHandler
import coffeeshop.{CoffeeShopService, OrderNotFoundException}

object CoffeeServer {
  def main(args: Array[String]) = {
    implicit val as = ActorSystem()
    implicit val ec = as.dispatcher

    val service = new CoffeeShopService()
    val endpoints = new CoffeeEndpoint(service)

    val exceptionHandler = ExceptionHandler { case _: OrderNotFoundException =>
      complete(StatusCodes.NotFound)
    }

    val finalEndpoint = handleExceptions(exceptionHandler) {
      endpoints.route
    }

    Http()
      .newServerAt("localhost", 8080)
      .bind(finalEndpoint)
      .foreach(binding => println(s"server at ${binding.localAddress}"))
  }

}
