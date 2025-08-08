// ______                _   _                   _ 
// |  ___|              | | (_)                 | |
// | |_ _   _ _ __   ___| |_ _  ___  _ __   __ _| |
// |  _| | | | '_ \ / __| __| |/ _ \| '_ \ / _` | |
// | | | |_| | | | | (__| |_| | (_) | | | | (_| | |
// \_|  \__,_|_| |_|\___|\__|_|\___/|_| |_|\__,_|_|
//                                                 
//  _____ __  __          _       
// |  ___/ _|/ _|        | |      
// | |__| |_| |_ ___  ___| |_ ___ 
// |  __|  _|  _/ _ \/ __| __/ __|
// | |__| | | ||  __/ (__| |_\__ \
// \____/_| |_| \___|\___|\__|___/
                               
// # Substitution Model / Referential Transparency / Local Reasoning
val succ: Int => Int = _ + 1

succ(succ(10))
succ(10 + 1)
succ(11)
11 + 1
12

// Can you inline without breaking the semantics?

// Program 1
val r1 = 42
(r1, r1)

// Program 2
(42, 42)

// Program 1
val r2 = println("Hello!")
(r2, r2)

// Program 2
(println("Hello!"), println("Hello!"))

// Program 1
import scala.util.Random

val r3 = Random().nextInt()
(r3, r3)

// Program 2
(Random().nextInt(), Random().nextInt())

// Program 1
val r4 = Array(1,2,3)
(r4, r4)

// Program 2
(Array(1,2,3), Array(1,2,3))

// # Compositionality

def f: String => Int = ???
def g: Int => Boolean = ???

def h = f andThen g

// ## Identity

def leftId = identity[String] andThen h
def rightId = h andThen identity[Boolean]

// ## Associativity

def i: Boolean => Double = ???

def leftAssoc = (f andThen g) andThen i
def rightAssoc = f andThen (g andThen i)

// # Side-Effect vs Managed Effect

// ## Imperative programs

def doSomething: Unit = ???
def doSomethingElse: Unit = ???

def doBoth = { doSomething ; doSomethingElse }

// Can we implement a function that changes the
// order of these programs?
// flipThem(doBoth)

// Nope,
// the effects have already happened at this point

// ## Partiality

def partial(a: Int): Int | Null =
  if a < 10 && a > 0 then a + 1 else null

def betterPartial(a: Int): Option[Int] =
  if a < 10 && a > 0 then Some(a + 1) else None

// Scala 3 has union types, so it's a
// bit hard to demonstrate this
def addToIt(b: Int): Int =
  partial(3).nn + 2

// ## Exceptions

def exception(a: Int): Int =
  require(a < 10 && a > 0)
  a + 1

def betterException(a: Int): Either[String, Int] =
  if a < 10 && a > 0
  then Right(a + 1)
  else Left("Not in the range!")

// exception(21)
betterException(21)

import scala.util.Try

// We can convert to these if we have some Java API
Option(partial(11))
Try(exception(11)).toEither

// For other effects (list, reader, writer, state)
// see `Monads.worksheet.sc`

// # Compositionality?

// betterPartial andThen betterPartial
// betterException andThen betterException

// We can combine effectful functions within the Kleisli category
import cats.data.Kleisli
import cats.implicits.*

Kleisli(betterPartial).andThen(betterPartial).run(2)
Kleisli(betterPartial).andThen(betterPartial).run(11)

Kleisli(betterException).andThen(betterException).run(2)
Kleisli(betterException).andThen(betterException).run(11)

def composedPartial = betterPartial >=> betterPartial
def composedException = betterException >=> betterException

composedPartial(2)
composedException(2)

// ## Applicative style

betterPartial(2) >>= betterPartial
betterPartial(2).flatMap(betterPartial)
for
  n <- betterPartial(2)
  n2 <- betterPartial(n)
yield n2

betterException(2) >>= (betterException)
betterException(2).flatMap(betterException)
for
  n <- betterException(2)
  n2 <- betterException(n)
yield n2
