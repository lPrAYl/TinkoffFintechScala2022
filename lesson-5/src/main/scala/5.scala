import scala.collection.View

trait RepeatList[+T] extends Iterable[T]

case class Salary(employee: String, amount: Double)

object Salary {
  implicit def multiplySalary: Multiply[Salary] = new Multiply[Salary] {
    override def twice(m: Salary): Salary = Salary(m.employee, 2 * m.amount)
    override def thrice(m: Salary): Salary = Salary(m.employee, 3 * m.amount)
    override def fourTimes(m: Salary): Salary = Salary(m.employee, 4 * m.amount)
  }
}

class RepeatListFromIterable[+T](iterable: Iterable[T]) extends RepeatList[T] {
  // should repeat iterable indefinitely
  override def iterator: Iterator[T] =
    if (iterable.nonEmpty) iterable.iterator ++ iterator
    else iterable.iterator
}

object RepeatList {
  def apply[T](iterable: Iterable[T]): RepeatList[T] = new RepeatListFromIterable[T](iterable)

  implicit def multiplyRepeatList[T]: Multiply[RepeatList[T]] = new Multiply[RepeatList[T]] {
    override def twice(m: RepeatList[T]): RepeatList[T] = RepeatList(m.view.flatMap(x => Iterator(x, x)))
    override def thrice(m: RepeatList[T]): RepeatList[T] = RepeatList(m.view.flatMap(x => Iterator(x, x, x)))
    override def fourTimes(m: RepeatList[T]): RepeatList[T] = RepeatList(m.view.flatMap(x => Iterator(x, x, x, x)))
  }
}

trait Multiply[M] {
  def twice(m: M): M
  def thrice(m: M): M
  def fourTimes(m: M): M
}

object helper {
  implicit class MultiplySyntax[T](val x: T) extends AnyVal {
    def twice(implicit m: Multiply[T]): T = m.twice(x)
    def thrice(implicit m: Multiply[T]): T = m.thrice(x)
    def fourTimes(implicit m: Multiply[T]): T = m.fourTimes(x)
  }
}

import helper._

object solutionFive extends App {
  val list = RepeatList(Seq(1, 2, 3))
  val salary = Salary("Bob", 300.0)

  println(list.thrice.take(50))
  println(salary.fourTimes)
}
