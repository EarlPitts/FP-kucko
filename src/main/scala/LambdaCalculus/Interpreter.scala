package LambdaCalculus

import cats.*
import cats.implicits.*

import collection.immutable.List.*
import parsley.Success
import parsley.Parsley
import parsley.character.{char, lower, spaces}
import parsley.combinator.{some, eof}
import parsley.implicits.character.{charLift, stringLift}

import Term.*
import RawTerm.*

// Parser
lazy val p: Parsley[RawTerm] = pVar <|> pAbs <|> pApp

lazy val pApp: Parsley[RawTmApp] = for
  _ <- char('(')
  t1 <- p
  _ <- spaces
  t2 <- p
  _ <- char(')')
yield RawTmApp(t1, t2)

lazy val pVar: Parsley[RawTmVar] = some(lower).map(x => RawTmVar(x.toString()))

lazy val pAbs: Parsley[RawTmAbs] = for
  _ <- char('\\')
  v <- some(lower).map(_.toString())
  _ <- char('.')
  t <- p
yield RawTmAbs(v, t)

// Syntax
enum RawTerm:
  case RawTmVar(n: Name)
  case RawTmApp(t1: RawTerm, t2: RawTerm)
  case RawTmAbs(v: Name, t: RawTerm)

enum Term:
  case TmVar(ind: Int, size: Int)
  case TmApp(t1: Term, t2: Term)
  case TmAbs(t1: Term)

type Name = String
type Context = List[Name]

object Term:

  // TODO Standard lib

  def toNameless(t: RawTerm): Term =
    def go(t: RawTerm, ctx: Context): Term =
      t match
        case RawTerm.RawTmApp(t1, t2) => TmApp(go(t1, ctx), go(t2, ctx))
        case RawTerm.RawTmAbs(n, t)   => TmAbs(go(t, n :: ctx))
        case RawTerm.RawTmVar(n)      => TmVar(ctx.indexOf(n), ctx.length)
    go(t, List())

  // ### Evaluation ###

  // Only lambda abstractions are considered values
  def isVal(ctx: Context, t: Term): Boolean =
    t match
      case TmAbs(_) => true
      case _        => false

  // Shifts each free variable's de Broijn index up by one
  def shift(d: Int, t: Term): Term =
    def walk(c: Int, t: Term): Term = t match
      case TmVar(x, s) =>
        if x >= c then TmVar(x + d, s + d) else TmVar(x, s + d)
      case TmAbs(t)      => TmAbs(walk(c + 1, t))
      case TmApp(t1, t2) => TmApp(walk(c, t1), walk(c, t2))
    walk(0, t)

  // Substitutes the variable i in some term with s
  // e.g.: [b -> a](b (λx.λy.b))
  def subst(i: Int, v: Term, t: Term): Term =
    def walk(c: Int, t: Term): Term = t match
      case TmVar(x, n)   => if x == i + c then shift(c, v) else TmVar(x, n)
      case TmAbs(t)      => TmAbs(walk(c + 1, t))
      case TmApp(t1, t2) => TmApp(walk(c, t1), walk(c, t2))
    walk(0, t)

  def substTop(s: Term, t: Term): Term =
    shift(-1, (subst(0, (shift(1, s)), t)))

  // Small-step semantics
  def reduce(ctx: Context, t: Term): Option[Term] = t match
    case TmApp(TmAbs(t1), v2 @ (TmAbs(_))) => Some(substTop(v2, t1))
    case TmApp(v1 @ (TmAbs(_)), t2)        => reduce(ctx, t2).map(TmApp(v1, _))
    case TmApp(t1, t2) => reduce(ctx, t1).map2(Some(t2))(TmApp)
    case _             => None

  def eval(ctx: Context, t: Term): Option[Term] = reduce(ctx, t) match
    case None    => Some(t)
    case Some(t) => eval(ctx, t)
