//  _____                      _   _                 _ _ _
// |  ___|                    | | | |               | | (_)
// | |__ _ __ _ __ ___  _ __  | |_| | __ _ _ __   __| | |_ _ __   __ _
// |  __| '__| '__/ _ \| '__| |  _  |/ _` | '_ \ / _` | | | '_ \ / _` |
// | |__| |  | | | (_) | |    | | | | (_| | | | | (_| | | | | | | (_| |
// \____/_|  |_|  \___/|_|    \_| |_/\__,_|_| |_|\__,_|_|_|_| |_|\__, |
//                                                                __/ |
//                                                               |___/
import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import cats.data.EitherT
import cats.data.Validated.*
import cats.data.Validated

import java.util.Date
import scala.util.Try

// Unsafe vs Safe head
List(1, 2, 3).head

List().headOption
Try(List().head)

Try(List().headOption.get)

// Java to Scala
lazy val veryDangerousExpression = 3/0
Try(veryDangerousExpression)
Try(veryDangerousExpression).toOption
Try(veryDangerousExpression).toEither

Option(null)

// ApplicativeError and MonadError
type EitherOr[A] = Either[String,A]
type EitherThrowable[A] = Either[Throwable,A]
type ValidatedOr[A] = Validated[String, A]

ApplicativeError[ValidatedOr, String].pure(2)
ApplicativeError[ValidatedOr, String].raiseError("Fatal error")

ApplicativeError[EitherOr, String].pure(2)
ApplicativeError[EitherOr, String].raiseError("Fatal error")

ApplicativeError[IO, Throwable].pure(2)
ApplicativeError[IO, Throwable].raiseError(new Exception("Fatal error"))

// ApplicativeThrow[F[_]] == ApplicativeError[F[_], Throwable]
ApplicativeThrow[IO].pure(2)
ApplicativeThrow[EitherThrowable].pure(2)

// Validated has no Monad/MonadError instance!
// MonadError[ValidatedOr, String].pure(2)
// MonadError[ValidatedOr, String].raiseError("Fatal error")

MonadError[EitherOr, String].pure(2)
MonadError[EitherOr, String].raiseError("Fatal error")

// MonadThrow[F[_]] == MonadError[F[_], Throwable]
MonadThrow[IO].pure(2)
MonadThrow[EitherThrowable].pure(2)

// Syntax
val badResult = "very bad error".raiseError
val otherBadResult = "very bad error".raiseError[ValidatedOr, Int]
badResult.handleError(errorStr => s"handled $errorStr")
badResult.handleErrorWith(errorStr => Right(s"handled $errorStr"))

Exception("big error")
  .raiseError[IO, Int]
  .handleErrorWith(err => log("baj happened").as(3))
  .unsafeRunSync()

Exception("big error")
  .raiseError[IO, Int]
  .attempt                                         // Transforming to an Either
  .unsafeRunSync()

Exception("big error")
  .raiseError[IO, Int]
  .adaptError(err => new Exception("other error")) // Adapting errors for our domain
  .attempt
  .unsafeRunSync()

Exception("big error")
  .raiseError[IO, Int]
  .onError(err => log("problem tortent"))
  .attempt
  .unsafeRunSync()

// recover
// recoverWith
// redeem
// rethrow

type User = String

object withIO:
  // Has to extend Throwable
  case object WrongUserName extends RuntimeException("No user with that name")
  case object WrongPassword extends RuntimeException("Wrong password")
  case class ExpiredSubscription(expirationDate: Date)
      extends RuntimeException("Expired subscription")
  case object BannedUser extends RuntimeException("User is banned")

  def findUserByName(username: String): IO[User] = WrongUserName.raiseError
  def checkPassword(user: User, password: String): IO[Unit] = ???
  def checkSubscription(user: User): IO[Unit] = ???
  def checkUserStatus(user: User): IO[Unit] = ???

  def authenticate(userName: String, password: String): IO[User] =
    for {
      user <- findUserByName(userName)
      _ <- checkPassword(user, password)
      _ <- checkSubscription(user)
      _ <- checkUserStatus(user)
    } yield user

import withIO.*

def log(msg: String): IO[Unit] = IO.unit

val a =
  authenticate("admin", "12345").handleErrorWith(e => log(e.getMessage) >> "defaultUser".pure)

val b = authenticate("a", "b").handleErrorWith {
  case WrongPassword => log(WrongPassword.getMessage) >> "defaultUser".pure
  case _             => log("badness happened") >> "noUser".pure
}

b.unsafeRunSync()

// We get better type signatures,
// warnings for inexhaustive pattern matches,
// and more help from the compiler
object withEither:
  sealed trait AuthenticationError
  case object WrongUserName extends AuthenticationError
  case object WrongPassword extends AuthenticationError
  final case class ExpiredSubscription(expirationDate: Date) extends AuthenticationError
  case object BannedUser extends AuthenticationError

  def findUserByName(username: String): Either[AuthenticationError, User] = Left(WrongUserName)
  def checkPassword(user: User, password: String): Either[AuthenticationError, Unit] = ???
  def checkSubscription(user: User): Either[AuthenticationError, Unit] = ???
  def checkUserStatus(user: User): Either[AuthenticationError, Unit] = ???

  def authenticate(userName: String, password: String): Either[AuthenticationError, User] =
  for {
    user <- findUserByName(userName)
    _ <- checkPassword(user, password)
    _ <- checkSubscription(user)
    _ <- checkUserStatus(user)
  } yield user

withEither.authenticate("beep", "boop") match
  case Left(withEither.WrongUserName) => "Wrong username!"
  case Right(_) => ???

withEither.authenticate("beep", "boop").handleError(errorHandler)

def errorHandler(err: withEither.AuthenticationError): User = err match
  case withEither.WrongUserName => "kacsa"

// Problem: we often want to have other effects too (e.g.: IO)
// Solution: monad transformers
object withEitherT:
  sealed trait AuthenticationError
  case object WrongUserName extends AuthenticationError
  case object WrongPassword extends AuthenticationError
  final case class ExpiredSubscription(expirationDate: Date)
      extends AuthenticationError
  case object BannedUser extends AuthenticationError

  def findUserByName(username: String) = EitherT[IO, AuthenticationError, User]("bela".asRight.pure)
  def checkPassword(user: User, password: String): IO[Either[AuthenticationError, Unit]] = ().asRight.pure
  def checkSubscription(user: User): IO[Either[AuthenticationError, Unit]] = ().asRight.pure
  def checkUserStatus(user: User): IO[Either[AuthenticationError, Unit]] = ().asRight.pure

  def authenticate(
      userName: String,
      password: String
  ): EitherT[IO, AuthenticationError, User] =
    for {
      user <- findUserByName(userName)
      _ <- EitherT(checkPassword(user, password))
      // _ <- EitherT(log("did something").as(().asRight[AuthenticationError]))
      // _ <- EitherT.liftF(log("did something"))
      _ <- EitherT(checkPassword(user, password))
      _ <- EitherT(checkSubscription(user))
      _ <- EitherT(checkUserStatus(user))
    } yield user

withEitherT.authenticate("beep", "boop").value.unsafeRunSync()
