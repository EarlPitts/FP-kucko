import cats._
import cats.implicits._

enum Expr:
  case Literal(n: Int)
  case Var(v: String)
  case Sum(l: Expr, r: Expr)
import Expr._

def evaluate(m: Map[String, Int], e: Expr): Option[Int] = e match
  case Var(v)     => m.get(v)
  case Sum(l, r)  => Apply[Option].map2(evaluate(m, l), evaluate(m, r))(_ + _)
  case Literal(n) => Some(n)

val expr1 = Sum(Literal(2), Literal(3))
val expr2 = Sum(Var("a"), Sum(Literal(2), Literal(3)))

evaluate(Map.empty[String, Int], expr1)
evaluate(Map("a" -> 3), expr2)

// Problems:
//   - Dependencies are hardcoded: Map, Option
