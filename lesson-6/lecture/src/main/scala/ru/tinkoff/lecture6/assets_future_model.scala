package ru.tinkoff.lecture6

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object assets_future_model {
  case class User(id: UUID)
  case class Asset(id: String, qnt: BigDecimal)

  trait AuthService {
    def authenticate(name: String, pass: String): Future[User]
  }

  trait AssetsService {
    def getAssets(userId: UUID): Future[List[Asset]]
  }

  trait PriceService {
    def getPrice(asset: Asset): Future[BigDecimal]
  }

  def sum(name: String, pass: String)(
    authService: AuthService,
    assetsService: AssetsService,
    priceService: PriceService
  )(
    implicit ec: ExecutionContext
  ): Future[BigDecimal] =
    for {
      user      <- authService.authenticate(name, pass)
      assets    <- assetsService.getAssets(user.id)
      prices    <- Future.traverse(assets)(a => priceService.getPrice(a).map(a -> _))
    } yield prices.foldLeft(BigDecimal(0)) { case (sum, (asset, price)) =>
      sum + price * asset.qnt
    }
}
