package ru.tinkoff.homework10.hangman.logic

import ru.tinkoff.homework10.hangman.Game

//import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future}

/** @inheritdoc ru.tinkoff.homework10.hangman.logic.PlayerGameService
  */
//@nowarn
class PlayerGameServiceImpl(delegate: GameService)(implicit
    ec: ExecutionContext
) extends PlayerGameService {

  def hiddenGame(game: Game): Game = {
    val hiddenWord = game.state.word
      .map(c => if (game.state.guesses.contains(c)) c else '*')

    game.copy(state = game.state.copy(word = hiddenWord))
  }

  def find(gameId: Long): Future[Option[Game]] =
    delegate.find(gameId).map { mayBeGame => mayBeGame.map(hiddenGame) }

  override def startNewGame(userName: String): Future[Game] =
    delegate.startNewGame(userName).map { game => hiddenGame(game) }

  override def makeGuess(id: Long, guess: Char): Future[Game] =
    delegate.makeGuess(id, guess).map { game => hiddenGame(game) }
}
