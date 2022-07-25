object solution {
  def main(args: Array[String]): Unit = {
    trait GreatHouse {
      val name: String
      val wealth: Wealth
    }

    trait Wealth {
      val moneyAmount: Int
      val armyStrength: Int

      override def toString: String = s"Wealth(moneyAmount: $moneyAmount, armyStrength: $armyStrength)"
    }

    object Wealth {
      def apply(moneyAmount_ : Int, armyStrength_ : Int) = new Wealth {
        override val moneyAmount: Int = moneyAmount_
        override val armyStrength: Int = armyStrength_
      }
    }

    trait MakeWildFire {
      this: GreatHouse =>
      def makeWildFire: Wealth = new Wealth {
        override val moneyAmount: Int = wealth.moneyAmount
        override val armyStrength: Int = wealth.armyStrength + 100
      }
    }

    trait BorrowMoney {
      this: GreatHouse =>
      def borrowMoney: Wealth = new Wealth {
        override val moneyAmount: Int = wealth.moneyAmount + 100
        override val armyStrength: Int = wealth.armyStrength
      }
    }

    trait CallDragon {
      this: GreatHouse =>
      def callDragon: Wealth = new Wealth {
        override val moneyAmount: Int = wealth.moneyAmount
        override val armyStrength: Int = wealth.armyStrength * 2
      }
    }

    trait Attack {
      def attack(wealth: Wealth): Wealth
    }

    case class Lannisters(wealth: Wealth) extends GreatHouse with MakeWildFire with BorrowMoney with Attack {
      override val name = "Lannisters"

      override def attack(wealthT: Wealth): Wealth = new Wealth {
        override val moneyAmount: Int = wealthT.moneyAmount - wealth.moneyAmount / 2
        override val armyStrength: Int = wealthT.armyStrength - wealth.armyStrength / 2
      }
    }

    case class Targaryen(wealth: Wealth) extends GreatHouse with CallDragon with Attack {
      override val name = "Targaryen"

      override def attack(wealthL: Wealth): Wealth = new Wealth {
        override val moneyAmount: Int = wealthL.moneyAmount - wealth.moneyAmount / 2
        override val armyStrength: Int = wealthL.armyStrength - wealth.armyStrength / 2
      }
    }

     class GameOfThrones(val lannisters: Lannisters, val targaryen: Targaryen, val turn: Int = 0) {
       def nextTurn(strategyLannnisters: Lannisters => Wealth)(strategyTargayen: Targaryen => Wealth): GameOfThrones =
         new GameOfThrones(lannisters.copy(strategyLannnisters(lannisters)), targaryen.copy(strategyTargayen(targaryen)), turn + 1)
     }

      val gameOfThrones = new GameOfThrones(Lannisters(Wealth(100, 100)), Targaryen(Wealth(100, 100)))
      gameOfThrones
        .nextTurn(lannisters => lannisters.makeWildFire)(targaryen => targaryen.callDragon)
        .nextTurn(lannisters => lannisters.borrowMoney)(targaryen => targaryen.callDragon)
  }
}
