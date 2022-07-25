class Economy
class UpgradedEconomy extends Economy
class Special1b extends UpgradedEconomy
class ExtendedEconomy extends Economy
class Business extends ExtendedEconomy
class Elite extends Business
class Platinum extends Business

class ServiceLevelAdvance[Current <: Economy] {
  def advance[Upper <: Current]: ServiceLevelAdvance[Upper] = new ServiceLevelAdvance[Upper]
}

object solutionTwo extends App {
  val economy: ServiceLevelAdvance[Economy] = new ServiceLevelAdvance[Economy]

  val upgradedEconomy: ServiceLevelAdvance[UpgradedEconomy] = economy.advance[UpgradedEconomy]
}
