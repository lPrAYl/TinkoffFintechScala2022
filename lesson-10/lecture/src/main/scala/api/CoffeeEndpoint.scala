package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import coffeeshop.{CoffeeOrder, CoffeeShopService}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

class CoffeeEndpoint(coffeeShopService: CoffeeShopService) {
  private val submitCoffeeOrder: Route = {
    (post & entity(as[CoffeeOrder])) { order =>
      complete(coffeeShopService.makeCoffee(order))
    }
  }

  private val getOrderEndpoint: Route = {
    (get & path(JavaUUID)) { id =>
      complete(coffeeShopService.getCoffeeOrder(id))
    }
  }

  val route: Route = pathPrefix("api" / "v1" / "coffee") {
    concat(submitCoffeeOrder, getOrderEndpoint)
  }
}
