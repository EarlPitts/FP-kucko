//  _____                      _   _                 _ _ _
// |  ___|                    | | | |               | | (_)
// | |__ _ __ _ __ ___  _ __  | |_| | __ _ _ __   __| | |_ _ __   __ _
// |  __| '__| '__/ _ \| '__| |  _  |/ _` | '_ \ / _` | | | '_ \ / _` |
// | |__| |  | | | (_) | |    | | | | (_| | | | | (_| | | | | | | (_| |
// \____/_|  |_|  \___/|_|    \_| |_/\__,_|_| |_|\__,_|_|_|_| |_|\__, |
//                                                                __/ |
//                                                               |___/
import java.util.Date

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.data.EitherT

"sajt".raiseError

import cats.effect.unsafe.implicits.global

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

val a = authenticate("a", "b").handleErrorWith(e => log(e.getMessage) >> "sajt".pure)

val b = authenticate("a", "b").handleErrorWith {
  case WrongPassword => log(WrongPassword.getMessage) >> "benaUser".pure
  case _ => log("no idea") >> "benaUser".pure
}

b.unsafeRunSync()

//
//
// object withEither:
//   sealed trait AuthenticationError
//   case object WrongUserName extends AuthenticationError
//   case object WrongPassword extends AuthenticationError
//   final case class ExpiredSubscription(expirationDate: Date) extends AuthenticationError
//   case object BannedUser extends AuthenticationError
//
//   def findUserByName(username: String): IO[Either[AuthenticationError, User]] = ???
//   def checkPassword(user: User, password: String): IO[Either[AuthenticationError, Unit]] = ???
//   def checkSubscription(user: User): IO[Either[AuthenticationError, Unit]] = ???
//   def checkUserStatus(user: User): IO[Either[AuthenticationError, Unit]] = ???
//
//   def authenticate(userName: String, password: String): IO[Either[AuthenticationError, User]] =
//   for {
//     user <- findUserByName(userName)
//     _ <- checkPassword(user, password)
//     _ <- checkSubscription(user)
//     _ <- checkUserStatus(user)
//   } yield user
//
//
object withEitherT:
  sealed trait AuthenticationError
  case object WrongUserName extends AuthenticationError
  case object WrongPassword extends AuthenticationError
  final case class ExpiredSubscription(expirationDate: Date) extends AuthenticationError
  case object BannedUser extends AuthenticationError

  def findUserByName(username: String) = EitherT[IO, AuthenticationError, User]("bela".asRight.pure)
  def checkPassword(user: User, password: String): IO[Either[AuthenticationError, Unit]] = ().asRight.pure
  def checkSubscription(user: User): IO[Either[AuthenticationError, Unit]] = ().asRight.pure
  def checkUserStatus(user: User): IO[Either[AuthenticationError, Unit]] = ().asRight.pure

  def authenticate(userName: String, password: String): EitherT[IO, AuthenticationError, User] =
    for {
      user <- findUserByName(userName)
      _ <- EitherT.right(IO.unit)
      _ <- EitherT(checkPassword(user, password))
      _ <- EitherT(checkSubscription(user))
      _ <- EitherT(checkUserStatus(user))
    } yield user

withEitherT.authenticate("beep", "boop").value.unsafeRunSync()

// EitherT[Option, Int, Int].pure(3)
3.pure : EitherT[Option, Int, Int]
