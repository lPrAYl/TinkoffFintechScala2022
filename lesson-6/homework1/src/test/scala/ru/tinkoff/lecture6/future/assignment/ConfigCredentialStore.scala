package ru.tinkoff.lecture6.future.assignment

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import ru.tinkoff.lecture6.future.assignment.store.AsyncCredentialStore

//import scala.annotation.nowarn
import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global

//@nowarn("msg=never used")
class ConfigCredentialStore(config: Config) extends AsyncCredentialStore {
  private val credentialMap = config.as[Map[String, String]]("credentials")

  /**
    * возвращает хеш пользовательского пароля
    */
  override def find(user: String): Future[Option[String]] = Future.successful { credentialMap get user }
}