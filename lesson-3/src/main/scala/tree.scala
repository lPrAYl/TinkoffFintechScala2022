import scala.annotation.tailrec
import scala.concurrent.Future.never.value

object solutionTree {
  def main(argc: Array[String]): Unit = {

    sealed trait Tree

    case class Node(value: Int, left: Tree, right: Tree) extends Tree

    case object RedLeaf extends Tree

    case object YellowLeaf extends Tree

    case object GreenLeaf extends Tree

    object Tree {
      /**
       * Sum values of nodes that have either yellow or red leaf if no such leaves found sum is zero
       */
      def countYellowAndRedValues(tree: Tree): Int = {
        def go(tree: Tree, sum: Int): Int = tree match {
          case Node(value, YellowLeaf | RedLeaf, right) => go(right, sum + value)
          case Node(value, left, YellowLeaf | RedLeaf) => go(left, sum + value)
          case Node(_, left, right) => go(left, go(right, sum))
          case _ => sum
        }
        go(tree, 0)
      }

      /**
       * Find max value in tree
       */
      def maxValue(tree: Tree): Option[Int] = {
        def go(tree: Tree, max: Option[Int]): Option[Int] = tree match {
          case Node(value, left, right) =>
            go(left, go(right, max = maxTwo(max, value)))
          case _ => max
        }
        def maxTwo(max: Option[Int], value: Int): Option[Int] = max match {
          case Some(max) => Option(math.max(max, value))
          case None => Option(value)
        }
        go(tree, None)
      }
    }
  }
}
