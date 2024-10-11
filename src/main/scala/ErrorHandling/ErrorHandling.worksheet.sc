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

import java.util.Date
import scala.util.Try

// import cats.data.Validated.*
// import cats.data.Validated


// Unsafe vs Safe head
List(1, 2, 3).head

List().headOption
Try(List().head)

Try(List().headOption.get)

// Java to Scala
Try(3/0)
Try(3/0).toOption
Try(3/0).toEither

Option(null)

// Valid(3)

// type ErrorOr[A] = Either[String,A]
// type ValidatedOr[A] = Validated[String, A]
//
// ApplicativeError[ErrorOr, String].pure(2)
// ApplicativeError[ErrorOr, String].raiseError("Fatal error")
//
// ApplicativeError[ValidatedOr, String].pure(2)
//
// MonadError[ErrorOr, String].pure(2)
// MonadError[ErrorOr, String].raiseError("Fatal error")
//
"sajt".raiseError

Exception("sajt").raiseError[IO, Int].attempt.unsafeRunSync()

type User = String

object withIO:
  case object WrongUserName extends RuntimeException("No user with that name")
  case object WrongPassword extends RuntimeException("Wrong password")
  case class ExpiredSubscription(expirationDate: Date)
      extends RuntimeException("Expired subscription")
  case object BannedUser extends RuntimeException("User is banned")

  def findUserByName(username: String): IO[User] = WrongUserName.raiseError
  def checkPassword(user: User, password: String): IO[Unit] = IO.unit
  def checkSubscription(user: User): IO[Unit] = IO.unit
  def checkUserStatus(user: User): IO[Unit] = IO.unit

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
  authenticate("a", "b").handleErrorWith(e => log(e.getMessage) >> "sajt".pure)

val b = authenticate("a", "b").handleErrorWith {
  case WrongPassword => log(WrongPassword.getMessage) >> "benaUser".pure
  case _             => log("no idea") >> "benaUser".pure
}

b.unsafeRunSync()

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

withEither.authenticate("beep", "boop")

object withEitherT:
  sealed trait AuthenticationError
  case object WrongUserName extends AuthenticationError
  case object WrongPassword extends AuthenticationError
  final case class ExpiredSubscription(expirationDate: Date)
      extends AuthenticationError
  case object BannedUser extends AuthenticationError

  def findUserByName(username: String) =
    EitherT[IO, AuthenticationError, User]("bela".asRight.pure)
  def checkPassword(
      user: User,
      password: String
  ): IO[Either[AuthenticationError, Unit]] = ().asRight.pure
  def checkSubscription(user: User): IO[Either[AuthenticationError, Unit]] =
    ().asRight.pure
  def checkUserStatus(user: User): IO[Either[AuthenticationError, Unit]] =
    ().asRight.pure

  def authenticate(
      userName: String,
      password: String
  ): EitherT[IO, AuthenticationError, User] =
    for {
      user <- findUserByName(userName)
      _ <- EitherT.right(IO.unit)
      _ <- EitherT(checkPassword(user, password))
      _ <- EitherT(checkSubscription(user))
      _ <- EitherT(checkUserStatus(user))
    } yield user

withEitherT.authenticate("beep", "boop").value.unsafeRunSync()

// EitherT[Option, Int, Int].pure(3)
3.pure: EitherT[Option, Int, Int]

//
// import cats.data.Validated
// import cats.data.Validated.*
// import cats.*
// import cats.implicits.*
//
// enum ParsingError:
//   case BadAge(s: String)
//   case BadName
//   case BadAddress
//
// import ParsingError.*
//
// type User = String
//
// BadAge("a") : ParsingError
//
// val res: Either[ParsingError, User] = Left(BadAddress)
//
// res.fold(handleError, identity)
//
// def handleError(e: ParsingError): String = e match
//   case BadAddress => "The address was bad"
//
// def f(name: String): Validated[List[ParsingError], String] =
//   if name == "john" then Valid("john") else Invalid(List(BadName))
//
// def g(addr: String): Validated[List[ParsingError], String] =
//   if addr == "john street" then Valid("john street") else Invalid(List(BadAddress))
//
// type A = Validated[List[ParsingError], String]
//
// // ((x: String) => (y: String) => (x ++ y)) map f("john") <*> g("john street")
