package ru.tinkoff.homework10.hangman

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

final case class State(name: String, guesses: Set[Char], word: String) {
  def failures: Int = (guesses -- word.toSet).size

  def playerLost: Boolean = failures >= 10

  def playerWon: Boolean = (word.toSet -- guesses).isEmpty

  def addChar(char: Char): State = copy(guesses = guesses + char)

  def status: GameStatus = GameStatus.InProgress match {
    case _ if playerLost => GameStatus.Lost
    case _ if playerWon => GameStatus.Won
    case _ => GameStatus.InProgress
  }
}

object State {
  implicit val jsonDecoder: Decoder[State] = deriveDecoder
  implicit val jsonEncoder: Encoder[State] = deriveEncoder
}
