import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}


@nowarn
object stepik extends App {
  val rdm = new Random()

  case class FlightProfile(id: String, city: String) {
    def search(destination: FlightProfile) =
      println(s"found flights from ${this.city} to ${destination.city}")
  }

  object FlightNetwork {
    val sources = Map(
      "r1-msc" -> "Moscow",
      "r2-spb" -> "St Petersburg"
    )

    val routes = Map(
      "r1-msc" -> "r2-spb"
    )

    def fetchSource(id: String): Future[FlightProfile] = Future {
      Thread.sleep(rdm.nextInt(100))
      FlightProfile(id, sources(id))
    }

    def fetchDestination(profile: FlightProfile): Future[FlightProfile] = Future {
      Thread.sleep(rdm.nextInt(200))
      val destinationId = routes(profile.id)
      FlightProfile(destinationId, sources(destinationId))
    }
  }

  val sourceCity = FlightNetwork.fetchSource("r1-msc")

  sourceCity.onComplete {
    case Success(sourceProfile) => {
      val destination = FlightNetwork.fetchDestination((sourceProfile))

      destination.onComplete {
        case Success(destinationProfile) => sourceProfile.search(destinationProfile)
        case Failure(exception)          => exception.printStackTrace()
      }
    }
    case Failure(exception) => exception.printStackTrace()
  }

  Thread.sleep(rdm.nextInt(800))


  def veryLongComputation: String = {
    Thread.sleep(1000)
    "Long Computation Done"
  }

  val aFuture = Future {
    veryLongComputation
  }

  aFuture.onComplete {
    case Success(value) => println(s"$value")
    case Failure(exception) => println(s"got an exception $exception")
  }

  Thread.sleep(1100)
}
