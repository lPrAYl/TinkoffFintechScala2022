object Russian
object English

trait Greeting[T] {
  def text: String
}

class Greeter[T] {
  def greet(greetings: Greeting[T]): Unit = println(greetings.text)
}


object solutionOne extends App {
  val greetingRu = new Greeting[Russian.type] {
    def text: String = "Хорошего дня!"
  }

  val greetingEn = new Greeting[English.type] {
    def text: String = "Have a good day!"
  }

  val greeterRussian = new Greeter[Russian.type]
  greeterRussian.greet(greetingRu)

  val greeterEnglish = new Greeter[English.type]
  greeterEnglish.greet(greetingEn)
}
