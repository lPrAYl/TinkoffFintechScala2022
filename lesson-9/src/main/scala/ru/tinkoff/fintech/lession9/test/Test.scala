package ru.tinkoff.fintech.lession9.test

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2._
import doobie.implicits._
import doobie.util.ExecutionContexts
import ru.tinkoff.fintech.lession9.config.DBInit
import ru.tinkoff.fintech.lession9.user.{User, UserRepositoryImpl}
import ru.tinkoff.fintech.lession9.document.{DocumentRepository, DocumentRepositoryImpl}

import java.util.UUID

object H2App extends IOApp {

  implicit val transactor: Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        "sa",
        "",
        ce
      )
    } yield xa

  val dbInit = new DBInit()
  val userRepository: UserRepositoryImpl = new UserRepositoryImpl
  val documentRepository: DocumentRepositoryImpl = new DocumentRepositoryImpl

  val userOne = User(UUID.randomUUID(), "userOne profile")
  val userTwo = User(UUID.randomUUID(), "userTwo profile")

  val documentId = UUID.randomUUID()

  def init: IO[ExitCode] = transactor.use { xa =>
    for {
      _ <- dbInit.initUserTable.transact(xa)
      _ <- dbInit.initEmailTable.transact(xa)
      _ <- dbInit.initSessionTable.transact(xa)
      _ <- dbInit.initDocumentTable.transact(xa)
    } yield ExitCode.Success
  }

  def addUser: IO[ExitCode] = transactor.use { xa =>
    for {
      added <- userRepository.createUser(userOne).transact(xa)      //  add userOne
      _ <- IO.pure(println(s"add userOne - $added"))
      addedAgain <- userRepository.createUser(userOne).transact(xa) // try add userOne again
      _ <- IO.pure(println(s"try add userOne again - $addedAgain"))
      _ <- userRepository.createUser(userOne).transact(xa)
    } yield ExitCode.Success
  }

  def attach: IO[ExitCode] = transactor.use { xa =>
    for {
      attachOne <- userRepository.attachEmail(userOne.id, "emailOne", "passwordOne").transact(xa)
      _ <- IO.pure(println(s"attach correct email and password - $attachOne"))
      attachTwo <- userRepository.attachEmail(userOne.id, "emailOne", "passwordTwo").transact(xa)
      _ <- IO.pure(println(s"attach correct email and incorrect password - $attachTwo"))
      attachThree <- userRepository.attachEmail(userOne.id, "emailTwo", "passwordTwo").transact(xa)
      _ <- IO.pure(println(s"attach another correct email and password - $attachThree"))
      attachFour <- userRepository.attachEmail(UUID.randomUUID(), "emailThree", "passwordThree").transact(xa)
      _ <- IO.pure(println(s"attach email to incorrect user - $attachFour"))
      attachFive <- userRepository.attachEmail(userTwo.id, "emailOne", "passwordOne").transact(xa)
      _ <- IO.pure(println(s"attach another user to email of current user - $attachFive"))
    } yield ExitCode.Success
  }

  def optionCheck(aux: Option[User]): String = aux match {
    case Some(v) => "success"
    case None => "failure"
  }

  def authorize: IO[ExitCode] = transactor.use { xa =>
    for {
      authorizeOne <- userRepository.authorizeEmail("sessionOne", "emailOne", "passwordOne").transact(xa)
      _ <- IO.pure(println(s"authorize correct email and password - ${optionCheck(authorizeOne)}"))
      authorizeTwo <- userRepository.authorizeEmail("sessionTwo", "emailOne", "passworTwo").transact(xa)
      _ <- IO.pure(println(s"authorize correct email and incorrect password - ${optionCheck(authorizeTwo)}"))
      authorizeThree <- userRepository.authorizeEmail("sessionOne", "emailTwo", "passwordTwo").transact(xa)
      _ <- IO.pure(println(s"authorize another user in current session - ${optionCheck(authorizeThree)}"))
      authorizeFour <- userRepository.authorizeEmail("sessionTwo", "emailTwo", "passwordTwo").transact(xa)
      _ <- IO.pure(println(s"authorize another correct email and password - ${optionCheck(authorizeFour)}"))
      authorizeFive <- userRepository.authorizeEmail("sessionThree", "emailThree", "passwordThree").transact(xa)
      _ <- IO.pure(println(s"authorize not exists email - ${optionCheck(authorizeFive)}"))

    } yield ExitCode.Success
  }

  def getUser: IO[ExitCode] = transactor.use { xa =>
    for {
      /* getById  */
      getByIdOne <- userRepository.getById(userOne.id).transact(xa)
      _ <- IO.pure(println(s"getById exists user - ${optionCheck(getByIdOne)}"))
      getByIdTwo <- userRepository.getById(UUID.randomUUID()).transact(xa)
      _ <- IO.pure(println(s"getById not exists user - ${optionCheck(getByIdTwo)}"))

      /* getByEmail */
      getByEmailOne <- userRepository.getByEmail("emailOne").transact(xa)
      _ <- IO.pure(println(s"getByEmail exists user - ${optionCheck(getByEmailOne)}"))
      getByEmailTwo <- userRepository.getByEmail("emailThree").transact(xa)
      _ <- IO.pure(println(s"getByEmail not exists user - ${optionCheck(getByEmailTwo)}"))

      /*  getBySession  */
      getBySessionOne <- userRepository.getBySession("sessionOne").transact(xa)
      _ <- IO.pure(println(s"getBySession exists user - ${optionCheck(getBySessionOne)}"))
      getBySessionTwo <- userRepository.getBySession("sessionThree").transact(xa)
      _ <- IO.pure(println(s"getBySession not exists user - ${optionCheck(getBySessionTwo)}"))
    } yield ExitCode.Success
  }

  def addDocument: IO[ExitCode] = transactor.use { xa =>
    for {
      createdOne <- documentRepository.createVersion(documentId, "link", 5, userOne.id).transact(xa)
      _ <- IO.pure(println(s"create correct document - $createdOne"))
      createdTwo <- documentRepository.createVersion(UUID.randomUUID(), "link", 5, userOne.id).transact(xa)
      _ <- IO.pure(println(s"create document with current link - $createdTwo"))
      createdThree <- documentRepository.createVersion(documentId, "new link", 4, userTwo.id).transact(xa)
      _ <- IO.pure(println(s"create old version of document - $createdThree"))
      createdFour <- documentRepository.createVersion(documentId, "another new link", 6, userOne.id).transact(xa)
      _ <- IO.pure(println(s"create new version of document - $createdFour"))
    } yield ExitCode.Success
  }

  def getDocument: IO[ExitCode] = transactor.use { xa =>
    for {
      /*  getDocById  */
      getByIdOne <- documentRepository.getById(documentId).transact(xa)
      _ <- IO.pure(println(s"seq of documents - $getByIdOne"))
      getByIdTwo <- documentRepository.getById(UUID.randomUUID()).transact(xa)
      _ <- IO.pure(println(s"empty seq - $getByIdTwo"))

      /*  getDocByEditor  */
      getByEditorOne <- documentRepository.getByEditor(userOne.id).transact(xa)
      _ <- IO.pure(println(s"seq of documents - $getByEditorOne"))
      getByEditorTwo <- documentRepository.getByEditor(userTwo.id).transact(xa)
      _ <- IO.pure(println(s"empty seq - $getByEditorTwo"))
    } yield ExitCode.Success
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- init         //  сreating empty tables
      _ <- addUser      //  adding users in userTable
      _ <- attach       //  attaching emails/passwords to users
      _ <- authorize    //  authorizing emails, creating sessions
      _ <- getUser      //  getting users
      _ <- addDocument  //  adding documents in documentTable
      _ <- getDocument  //  getting document
    } yield ExitCode.Success
  }
}
