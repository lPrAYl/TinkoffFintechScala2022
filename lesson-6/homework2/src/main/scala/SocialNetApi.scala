import java.util.UUID
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

case class Post(id: UUID,
                user: String,
                text: String,
                likes: Int)


class SocialNetApi(remote: SocialNetRemote)(implicit val ec: ExecutionContext) {
  /**
    * отправить твит
    * возвращает UUID твита либо текст ошибки
    *
    * @param user
    * @param text
    * @return
    */
  def send(user: String, text: String): Future[Either[String, UUID]] = {
    val promise = Promise[Either[String, UUID]]()
    remote.send(text, user) {
      case Failure(exception) => promise success Left(exception.getMessage)
      case Success(value) => promise success Right(value)
    }
    promise.future
  }

  /**
    * получить твит
    * возвращает твиты и тексты ошибок с идентификаторами
    *
    * @param idSet
    * @return
    */
  def get(idSet: Set[UUID]): Future[(Set[(UUID, String)], Set[Post])] = {
    Future.traverse(idSet) { id =>
      val promise = Promise[Either[(UUID, String), Post]]()
      remote.get(id) {
        case Failure(exception) => promise success Left((id, exception.getMessage))
        case Success(post) => promise success Right(Post.tupled(post))
      }
      promise.future
    }.map(_.partitionMap(identity))
  }

  /**
    * лайкнуть твит
    * возвращает флаг принятия либо текст ощибки
    *
    * @param id
    * @return
    */
  def like(id: UUID): Future[Either[String, Boolean]] = {
    val promise = Promise[Either[String, Boolean]]()
    remote.like(id) {
      case Failure(exception) => promise success Left(exception.getMessage)
      case Success(liked) => promise success Right(liked)
    }
    promise.future
  }

  /**
    * дизлайкнуть твит
    * возвращает флаг принятия либо текст ощибки
    *
    * @param id
    * @return
    */
  def unlike(id: UUID): Future[Either[String, Boolean]] = {
    val promise = Promise[Either[String, Boolean]]()
    remote.unlike(id) {
      case Failure(exception) => promise success Left(exception.getMessage)
      case Success(unliked) => promise success Right(unliked)
    }
    promise.future
  }

  /**
    * лайкнуть твиты.
    *
    * @param idList
    * @return
    */
  def likeSeq(idList: Set[UUID]): Future[Either[Set[(UUID, String)], Set[(UUID, Boolean)]]] = {
    def actionWithSeq(value: Set[UUID], action: UUID =>
      Future[Either[String, Boolean]]): Future[(Set[(UUID, String)], Set[(UUID, Boolean)])] = {
      Future.traverse(value)(id => action(id).map(retAction => (id, retAction)))
        .map { setFromIdAction =>
          setFromIdAction.partitionMap {
            case (id, Left(errorMsq)) => Left((id, errorMsq))
            case (id, Right(accepted)) => Right((id, accepted))
          }
        }
    }

    actionWithSeq(idList, like).flatMap { case (failLike, successLike) =>
      if (failLike.nonEmpty) {
        def unlikeSet(set: Set[UUID]): Future[Unit] = {
          if (set.isEmpty) Future.unit
          else actionWithSeq(set, unlike).flatMap { case (failUnlike, _) => unlikeSet(failUnlike.map(_._1)) }
        }
        unlikeSet(successLike.map(_._1)).map(_ => Left(failLike))
      }
      else Future.successful(Right(successLike))
    }
  }
}

object SocialNetApi {
  def apply(remote: SocialNetRemote)(implicit ec: ExecutionContext): SocialNetApi =
    new SocialNetApi(remote)(ec) {}
}