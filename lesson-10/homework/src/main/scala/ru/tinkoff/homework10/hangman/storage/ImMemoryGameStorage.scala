package ru.tinkoff.homework10.hangman.storage

import ru.tinkoff.homework10.hangman.{Game, GameStatus, State}
import ru.tinkoff.homework10.hangman._

import java.time.Instant
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

/** In-memory хранилище для состояния игры на основе TrieMap
  */
class ImMemoryGameStorage extends GameStorage {
  private val gameStore: TrieMap[Long, Game] = TrieMap[Long, Game]()
  @volatile private var id: Long = 0

  override def find(id: Long): Future[Option[Game]] = Future.successful(gameStore.get(id))

  override def insert(
      startedAt: Instant,
      state: State,
      status: GameStatus
  ): Future[Game] = {
    val gameId = this.synchronized {
      id = id + 1
      id
    }
    val game = Game(gameId, startedAt, state, status)
    gameStore.put(gameId, game)
    Future.successful(game)
  }

  override def update(game: Game): Future[Game] = {
    val gameOpt = gameStore.get(game.id)
    gameOpt match {
      case Some(_) =>
        gameStore.update(game.id, game)
        Future.successful(game)
      case None => Future.failed(GameNotFoundException(game.id))
    }
  }
}
