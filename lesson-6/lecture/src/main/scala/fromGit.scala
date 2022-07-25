
import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

@nowarn
object fromGit extends App {

  // Определим осмысленные синонимы:
  type CoffeeBeans = String
  type GroundCoffee = String
  case class Water(temperature: Int)
  type Milk = String
  type FrothedMilk = String
  type Espresso = String
  type Cappuccino = String

  // Методы-заглушки для отдельных шагов алгоритма:
  def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
    println("start grinding...")
    Thread.sleep(Random.nextInt(2))
    if (beans == "baked beans") throw GrindingException("are you joking?")
    println("finished grinding...")
    s"ground coffee of $beans"
  }

  def heatWater(water: Water): Future[Water] = Future {
    println("heating the water now")
    Thread.sleep(Random.nextInt(2))
    println("hot, it's hot!")
    water.copy(temperature = 85)
  }

  def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
    println("milk frothing system engaged!")
    Thread.sleep(Random.nextInt(2000))
    println("shutting dowm milk frothing system")
    s"frothed $milk"
  }

  def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = Future {
    println("happy brewing :)")
    Thread.sleep(Random.nextInt(2000))
    println("it's brewed!")
    "espresso"
  }

  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"

  // Исключения, на случай если что-то пойдёт не так
  // (они понадобяться нам позже):
  case class GrindingException(msg: String) extends Exception(msg)
  case class FrothingException(msg: String) extends Exception(msg)
  case class WaterBoilingException(msg: String) extends Exception(msg)
  case class BrewingException(msg: String) extends Exception(msg)

  grind("baked beans") onComplete {
    case Success(ground) => println(s"got my $ground")
    case Failure(exception) => println(s"This grinder needs a replacement, seriously!")
  }

  val temperatureOkay: Future[Boolean] = heatWater(Water(25)).map { water =>
    println("Мы в будущем!")
    (80 to 85).contains(water.temperature)
  }

//  // последовательно выполним алгоритм:
//  def prepareCappuccino(): Try[Cappuccino] = for {
//    ground <- Try(grind("arabica beans"))
//    water <- Try(heatWater(Water(25)))
//    espresso <- Try(brew(ground, water))
//    foam <- Try(frothMilk("milk"))
//  } yield combine(espresso, foam)

}
