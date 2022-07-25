package ru.tinkoff.homework10.hangman.logic

import ru.tinkoff.homework10.hangman.Game
import ru.tinkoff.homework10.hangman.GameStatus.InProgress
import ru.tinkoff.homework10.hangman.storage.GameStorage
import ru.tinkoff.homework10.hangman._

import java.time.Instant
//import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future}

//@nowarn
class GameServiceImpl(
    dictionaryService: DictionaryService,
    gameStorage: GameStorage
)(implicit ec: ExecutionContext)
    extends GameService {

  override def find(gameId: Long): Future[Option[Game]] = gameStorage.find(gameId)

  override def startNewGame(userName: String): Future[Game] =
    gameStorage.insert(Instant.now(), State(userName, Set.empty[Char], dictionaryService.chooseWord()), InProgress)

  override def makeGuess(id: Long, guess: Char): Future[Game] =
    find(id).flatMap {
      case None => Future.failed(GameNotFoundException(id))
      case Some(game) => game.status match {
        case InProgress =>
          val newState = game.state.addChar(guess)
          gameStorage.update(game.copy(state = newState, status = newState.status))
        case _ => Future.failed(GameAlreadyFinishedException(id, game.status))
      }
    }
}
