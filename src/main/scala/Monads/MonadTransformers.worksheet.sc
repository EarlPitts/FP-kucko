import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.std.Console
import cats.effect.std.Env

import cats.data.*

//------- The Problem --------//
val nested = List(Option(3))

// We have to map twice to access the inner value
nested.map(_.map(_ + 1))

import cats.data.Writer

type Nested = Writer[List[String], Option[Int]]

def subtractOne(n: Int): Nested =
  if n == 0
    then List("Can't do that!").tell >> Option.empty[Int].pure
    else List("Did it!").tell >> Option(n-1).pure

// Same here, have to map twice for this
subtractOne(3).map(_.map(_ + 1))

//------- OptionT --------//
import cats.data.OptionT

OptionT(nested).map(_ + 1).value
OptionT(subtractOne(3)).map(_ + 1).value

type Logged[A] = Writer[List[String], A]

def parseNumber(str: String): Logged[Option[Int]] =
  util.Try(str.toInt).toOption match
    case Some(num) => Writer(List(s"Read $str"), Some(num))
    case None      => Writer(List(s"Failed on $str"), None)

def addAll(a: String, b: String, c: String): Logged[Option[Int]] =
  val result = for
    a <- OptionT(parseNumber(a))
    b <- OptionT(parseNumber(b))
    c <- OptionT(parseNumber(c))
  yield a + b + c
  result.value

addAll("1", "2", "3").run
addAll("1", "a", "3").run
