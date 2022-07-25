package tapir

import coffeeshop.{CoffeeOrder, CoffeeShopService}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class TapirCoffeeEndpoint(coffeeShopService: CoffeeShopService)(implicit
    val executionContext: ExecutionContext
) {

  import sttp.tapir._

  private val coffeeEndpoint = endpoint.in("api" / "v1" / "coffee")

  val submitCoffee = coffeeEndpoint.post
    .in(jsonBody[CoffeeOrder])
    .out(jsonBody[UUID])
    .serverLogic[Future](order =>
      coffeeShopService.makeCoffee(order).map(id => Right(id))
    )

  val getCoffeeEndpoint = coffeeEndpoint.get
    .in(path[UUID]("id").description("Номер заказа на приготовление конфе"))
    .out(jsonBody[CoffeeOrder])
    .serverLogic[Future] { id =>
      coffeeShopService.getCoffeeOrder(id).map(Right(_))
    }

  val all = List(submitCoffee, getCoffeeEndpoint)
}
