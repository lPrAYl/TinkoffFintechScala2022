import scala.annotation.tailrec

object solutionBuilding {
  def main(argc: Array[String]): Unit = {
    /**
     * Building should have:
     *  - string address
     *  - floors (link to first floor)
     *    Floor can be either residential floor or attic
     *    Each residential floor has two persons living on it and ladder to next floor (just a link)
     *    Attic has no person living in it
     *    Each person has age and sex (male/female)
     */

    sealed trait Floor

    case class Building(address: String, floors: Floor)

    case class ResidentialFloor(residents: (Person, Person), ladder: Floor) extends Floor

    case object Attic extends Floor

    sealed trait Sex

    case object Male extends Sex

    case object Female extends Sex

    case class Person(age: Int, sex: Sex)

    object Building {
      /**
       * Traverse building bottom to top applying function [[f]] on each residential floor accumulating
       * result in [[acc0]]
       */
      def protoFold(building: Building, acc0: Int)(f: (Int, ResidentialFloor) => Int): Int = {
        def go(floor: Floor, acc: Int): Int = floor match {
          case residentialFloor: ResidentialFloor => go(residentialFloor.ladder, f(acc, residentialFloor))
          case _ => acc
        }

        go (building.floors, acc0)
      }

      /**
       * Count number of floors where there is at least one man older than [[olderThan]]
       * NOTE: use [[protoFold]]
       */
      def countOldManFloors(building: Building, olderThan: Int): Int = {
        def counter(acc: Int, residentialFloor: ResidentialFloor) = residentialFloor match {
          case ResidentialFloor((Person(age1, Male), Person(age2, Male)), _)
            if (age1 > olderThan || age2 > olderThan) => acc + 1
          case ResidentialFloor((Person(age, Male), _), _)
            if (age > olderThan) => acc + 1
          case ResidentialFloor((_, Person(age, Male)), _)
            if (age > olderThan) => acc + 1
          case _ => acc
        }

        protoFold(building, 0)(counter)
      }

      /**
       * Find age of eldest woman
       * NOTE: use [[protoFold]]
       */
      def womenMaxAge(building: Building): Int = {
        def maxAge(maxAge: Int, floor: ResidentialFloor): Int = floor.residents match {
          case (Person(age1, Female), Person(age2, Female)) => math.max(math.max(age1, age2), maxAge)
          case (Person(age, Female), _) => math.max(age, maxAge)
          case (_, Person(age, Female)) => math.max(age, maxAge)
          case _ => maxAge
        }

        protoFold(building, 0)(maxAge)
      }
    }
  }
}
