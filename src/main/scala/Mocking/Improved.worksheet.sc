import cats.data.WriterT
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
val expr3 = Sum(Literal(1), Sum(Var("a"), Sum(Var("b"), Var("c"))))
val expr4 = Sum(Var("d"), Sum(Literal(2), Literal(3)))

val ioEnv: String => IO[Int] = str =>
  str match
    case "a" => 3.pure
    case "b" => 4.pure
    case "c" => 5.pure
    case _   => new Exception("Variable not found").raiseError

val eitherEnv: String => Either[Throwable, Int] = str =>
  str match
    case "a" => 3.pure
    case "b" => 4.pure
    case "c" => 5.pure
    case _   => new Exception("Variable not found").raiseError

evaluate(eitherEnv, expr1)
evaluate(eitherEnv, expr2)
evaluate(eitherEnv, expr3)
evaluate(eitherEnv, expr4)

evaluate(ioEnv, expr1).unsafeRunSync()
evaluate(ioEnv, expr2).unsafeRunSync()
evaluate(ioEnv, expr3).unsafeRunSync()
// evaluate(ioEnv, expr4).unsafeRunSync()

Map("a" -> 1, "b" -> 2).map((x, y) => y)
Map("a" -> 1, "b" -> 2).flatMap((x, y) => List((x, y)))

def wrapLog[A: Num, F[_]: MonadThrow, T](f: T => F[A])(t: T) =
  (WriterT.tell(s"Accessing $t ") >> WriterT.liftF(f(t)))

val loggedIOEnv = wrapLog(ioEnv)
val loggedEitherEnv = wrapLog(eitherEnv)

evaluate(loggedIOEnv, expr3).run.unsafeRunSync()
evaluate(loggedEitherEnv, expr3).run

enum Expression[T]:
  case IntLit(value: Int) extends Expression[Int]
  case BoolLit(value: Boolean) extends Expression[Boolean]
  case IfExpr(
    cond: Expression[Boolean],
    when: Expression[T],
    otherwise: Expression[T],
  )
import Expression._

IfExpr(BoolLit(true), IntLit(3), IntLit(4))

// data Expr a where
//   Literal :: a -> Expr a
//   Sum :: (Num a) => Expr a -> Expr a -> Expr a
//   Even :: (Integral a) => Expr a -> Expr Bool
