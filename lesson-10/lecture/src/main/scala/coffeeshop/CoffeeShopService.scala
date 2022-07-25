package coffeeshop

import java.util.UUID
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

case class OrderNotFoundException(coffee: UUID) extends Throwable

class CoffeeShopService {
  def makeCoffee(coffeeOrder: CoffeeOrder): Future[UUID] = {
    val orderId = UUID.randomUUID()
    orderStore.put(orderId, coffeeOrder)
    Future.successful(orderId)
  }

  def getCoffeeOrder(id: UUID): Future[CoffeeOrder] = {
    val es = orderStore.get(id)
    es match {
      case Some(value) => Future.successful(value)
      case None => Future.failed(OrderNotFoundException(id))
    }
  }

  private val orderStore = TrieMap[UUID, CoffeeOrder]()
}
