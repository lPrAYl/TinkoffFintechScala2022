package ru.tinkoff.hangman

import monix.eval.Task
import monix.execution.Scheduler

//import scala.annotation.nowarn
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.Random

object Console {
  def putStrLn(string: String): Task[Unit] = Task(println(string))

  val getStrLn: Task[String] = Task(StdIn.readLine())
  val getChar: Task[Char] = Task(StdIn.readChar())
}

//@nowarn
object Hangman {
  import Dictionary.Dictionary

  /**
   * TODO 1
   *
   * Реализация должна принимать один символ от пользователя и возвращать его в нижнем регистре
   * Воспользуйтесь классом Console
   */
  val getChoice: Task[Char] = Console.getChar.map(_.toLower)

  /**
   * TODO 2
   *
   * Реализация должна запрашивать у пользователя имя и возвращать его. Воспользуйтесь классом Console.
   */
  val getName: Task[String] = {
    Console.putStrLn("Enter your name: ").flatMap(_ => Console.getStrLn)
  }

  /**
   * TODO 3
   *
   * Реализация должна возвращать случайное слово из справочника Dictionary. Воспользуйтесь Task.apply, чтобы разные
   * вызовы возвращали разные слова.
   */
  val chooseWord: Task[String] = Task.apply(Dictionary(Random.nextInt(Dictionary.size)))

  /**
   * TODO 4
   * Реализуйте игровой цикл, в котором у пользователя запрашивается следующая буква до тех пор, пока пользователь
   * не угадал слово или у него не закончатся попытки.
   * Для принятия решения о продолжении цикла используйте функцию analyzeNewInput
   * Для реализации этого метода вам понадобится рекурсия.
   */
  def gameLoop(oldState: State): Task[Unit] = {
    getChoice.flatMap { c =>
      val newState = oldState.addChar(c)
      val result = analyzeNewInput(oldState, newState, c)
      result match {
        case GuessResult.Won =>
          Console.putStrLn(s" Congratulation ${newState.name}! You won!") *> renderState(newState)
        case GuessResult.Lost =>
          Console.putStrLn(s" Sorry ${newState.name}! You lost! Word was ${newState.word}") *> renderState(newState)
        case GuessResult.Correct =>
          Console.putStrLn(s" Good guess, ${newState.name}!") *> renderState(newState) *> gameLoop(newState)
        case GuessResult.Incorrect =>
          Console.putStrLn(s" Bad guess, ${newState.name}") *> renderState(newState) *> gameLoop(newState)
        case GuessResult.Unchanged =>
          Console.putStrLn(s" ${newState.name}, You've already tried that letter") *> renderState(newState) *> gameLoop(newState)
      }
    }
  }

  def renderState(state: State): Task[Unit] = {

    /**
     *
     * f     n  c  t  o
     *  - -  -  -  -  -  -
     *
     * Guesses: a, z, y, x
     *
     */
    val word =
      state.word.toList
        .map(c => if (state.guesses.contains(c)) s" $c " else "   ")
        .mkString("")

    val line = List.fill(state.word.length)(" - ").mkString("")

    val guesses = " Guesses: " + state.guesses.mkString(", ")

    val text = word + "\n" + line + "\n\n" + guesses + "\n"

    Console.putStrLn(text)
  }

  final case class State(name: String, guesses: Set[Char], word: String) {
    def failures: Int = (guesses -- word.toSet).size

    def playerLost: Boolean = failures > 10

    def playerWon: Boolean = (word.toSet -- guesses).isEmpty

    def addChar(char: Char): State = copy(guesses = guesses + char)
  }

  sealed trait GuessResult

  object GuessResult {

    case object Won extends GuessResult

    case object Lost extends GuessResult

    case object Correct extends GuessResult

    case object Incorrect extends GuessResult

    case object Unchanged extends GuessResult

  }

  def analyzeNewInput(oldState: State,
                      newState: State,
                      char: Char
                     ): GuessResult =
    if (oldState.guesses.contains(char)) GuessResult.Unchanged
    else if (newState.playerWon) GuessResult.Won
    else if (newState.playerLost) GuessResult.Lost
    else if (oldState.word.contains(char)) GuessResult.Correct
    else GuessResult.Incorrect


  /**
   * TODO 5
   *
   * Запустите main и проверьте, что программа работает, как задумано
   */
  def main(args: Array[String]): Unit = {
    val program: Task[Unit] =
      for {
        name <- getName
        word <- chooseWord
        state = State(name, Set(), word)
        _ <- renderState(state)
        _ <- gameLoop(state)
      } yield ()


    import Scheduler.Implicits.global
    program.runSyncUnsafe(Duration.Inf)
  }
}
