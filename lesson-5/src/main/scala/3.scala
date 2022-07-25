import scala.reflect.runtime.{universe => ru}

class KnowNothing
class Aggressive extends KnowNothing
class KnowSomething extends KnowNothing
class PoorlyEducated extends KnowSomething
class Normal extends PoorlyEducated
class Enlightened extends Normal
class Genius extends Enlightened

class SchoolClass[T <: KnowNothing](collection: Seq[T]) {
  def accept[P >: T <: KnowNothing](students: Seq[P]): SchoolClass[P] = new SchoolClass[P](collection ++ students)
}

object solutionThree extends App {
  def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]

  val John = new KnowNothing
  val Kim = new Aggressive
  val Thomas = new KnowSomething
  val Julia = new PoorlyEducated
  val Ben = new Normal
  val Lin = new Enlightened
  val Bob = new Genius

  val class1 = new SchoolClass(Seq(Bob, Julia))
  println(getTypeTag(class1).tpe)
  val class2 = class1.accept(Seq(Lin, Ben))
  println(getTypeTag(class2).tpe)
  val class3 = class2.accept(Seq(Kim))
  println(getTypeTag(class3).tpe)
  val class4 = class3.accept(Seq(Thomas, John))
  println(getTypeTag(class4).tpe)
}
