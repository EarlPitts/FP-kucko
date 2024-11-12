import cats._
import cats.implicits._
import cats.effect._

import cats.effect.unsafe.implicits.global

trait Num[A]:
  def +(l: A, r: A): A
  // Other numberish methods...

object Num:
  def apply[A](implicit instance: Num[A]) = instance

  given Num[Int] with
    def +(l: Int, r: Int): Int = l + r

enum Expr[A, T]:
  case Literal(n: A)
  case Var(v: T)
  case Sum(l: Expr[A, T], r: Expr[A, T])
import Expr._

def evaluate[A: Num, F[_]: ApplicativeThrow, T](
    f: T => F[A],
    e: Expr[A, T]
): F[A] =
  e match
    case Var(v)     => f(v)
    case Sum(l, r)  => Apply[F].map2(evaluate(f, l), evaluate(f, r))(Num[A].+)
    case Literal(n) => Applicative[F].pure(n)

val expr1 = Sum(Literal(2), Literal(3))
val expr2 = Sum(Var("a"), Sum(Literal(2), Literal(3)))
val expr3 = Sum(Var("b"), Sum(Literal(2), Literal(3)))

val ioEnv: String => IO[Int] = str =>
  str match
    case "a" => 3.pure
    case _   => new Exception("Variable not found").raiseError

val eitherEnv: String => Either[Throwable, Int] = str =>
  str match
    case "a" => 3.pure
    case _   => new Exception("Variable not found").raiseError

evaluate(eitherEnv, expr1)
evaluate(eitherEnv, expr2)
evaluate(eitherEnv, expr3)

evaluate(ioEnv, expr1).unsafeRunSync()
evaluate(ioEnv, expr2).unsafeRunSync()
// evaluate(ioEnv, expr3).unsafeRunSync()

Map("a" -> 1, "b" -> 2).map((x, y) => y)
Map("a" -> 1, "b" -> 2).flatMap((x, y) => List((x,y)))
