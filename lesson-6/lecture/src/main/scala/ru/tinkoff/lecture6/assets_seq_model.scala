package ru.tinkoff.lecture6

import java.util.UUID

object assets_seq_model {
  case class User(id: UUID)
  case class Asset(id: String, qnt: BigDecimal)

  trait AuthService {
    def authenticate(name: String, pass: String): User
  }

  trait AssetsService {
    def getAssets(userId: UUID): List[Asset]
  }

  trait PriceService {
    def getPrice(asset: Asset): BigDecimal
  }


  def sum(name: String, pass: String)(
    authService: AuthService,
    assetsService: AssetsService,
    priceService: PriceService
  ): BigDecimal = {
    val user = authService.authenticate(name, pass)
    val assets = assetsService.getAssets(user.id)
    val prices = assets.map(a => (a -> priceService.getPrice(a)))
    
    prices.foldLeft(BigDecimal(0)) { case (sum, (asset, price)) =>
      sum + price * asset.qnt
    }
  }
}
