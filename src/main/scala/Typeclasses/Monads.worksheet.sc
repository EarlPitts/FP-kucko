// ___  ___                      _
// |  \/  |                     | |
// | .  . | ___  _ __   __ _  __| |___
// | |\/| |/ _ \| '_ \ / _` |/ _` / __|
// | |  | | (_) | | | | (_| | (_| \__ \
// \_|  |_/\___/|_| |_|\__,_|\__,_|___/
//

import cats.Monad
import cats.implicits.*

val f: Int => Int = _ + 1
List(f).ap(List(1))

List(1, 2, 3).flatMap(_ => List(2))

//------- Id --------//
type Id[A] = A

Monad[Id].pure(2)
2.pure[Id]

val a: Id[Int] = 3
a.flatMap(_ + 3)
a.flatMap(_ => 4.pure[Id])

//------- List --------//
val even: Int => Boolean = _ % 2 == 0
val xs = (1 to 20).toList

for
  x <- xs
  res <- if even(x) then List(x * 2) else Nil
yield res

for
  x <- xs
  res <- Monad[List].ifM(List(even(x)))(List(x * 2), Nil)
yield res

def cartesian(xs: List[Int], ys: List[Int]) = for
  x <- xs
  y <- ys
yield (x,y)

cartesian(List(1,2,3), List(4,5,6))

def permutations(xs: List[Int]) =
  Monad[List].replicateA(xs.length, xs)

permutations(List(1,2,3))

//------- Option --------//
type Name = String
type Age = Int
type UserId = String
case class User(name: Name, age: Age, id: UserId)

val validAge: Age => Option[Age] = n => if n > 0 then Option(n) else None
val validName: Name => Option[Name] = name =>
  if name.length < 100 then Option(name) else None
val validId: UserId => Option[UserId] = id =>
  if id.contains("validId") then Option(id) else None

def makeUser(name: Name, age: Age, id: UserId): Option[User] = for
  name <- validName(name)
  age <- validAge(age)
  id <- validId(id)
yield User(name, age, id)

val longName = (1 to 101).toList.mkString
makeUser("Karcsi", 92, "123validId456")
makeUser("Karcsi", -4, "123validId456")
makeUser(longName, 4, "123validId456")
makeUser("Bela", 2, "random")

//------- Either --------//
sealed trait ValidationError
case object InvalidName extends ValidationError
case object InvalidAge extends ValidationError
case object InvalidId extends ValidationError

val validAge2: Age => Either[ValidationError, Age] = n =>
  if n > 0 then n.asRight[ValidationError] else InvalidAge.asLeft[Age]
val validName2: Name => Either[ValidationError, Name] = name =>
  if name.length < 100 then name.asRight[ValidationError]
  else InvalidName.asLeft[Name]
val validId2: UserId => Either[ValidationError, UserId] = id =>
  if id.contains("validId") then id.asRight[ValidationError]
  else InvalidId.asLeft[UserId]

def makeUser2(name: Name, age: Age, id: UserId): Either[ValidationError, User] =
  for
    name <- validName2(name)
    age <- validAge2(age)
    id <- validId2(id)
  yield User(name, age, id)

makeUser2("Karcsi", 92, "123validId456")
makeUser2("Karcsi", -4, "123validId456")
makeUser2(longName, 4, "123validId456")
makeUser2("Bela", 2, "random")

//------- Reader --------//
import cats.data.Reader // also known as Kleisli

val multiplyByTwo = Reader[Int, Int](n => n * 2)
val addOne = Reader[Int, Int](n => n + 1)

val combined = for
  x <- multiplyByTwo
  y <- addOne
yield x + y

combined.run(2) // 4 + 3
combined.run(4) // 8 + 5

case class Db(usernames: Map[Int, String], passwords: Map[String, String])

type DbReader[A] = Reader[Db, A]

def findUsername(userId: Int): DbReader[Option[String]] =
  Reader(db => db.usernames.get(userId))

def checkPassword(username: String, password: String): DbReader[Boolean] =
  Reader(db => db.passwords.get(username).contains(password))

def checkLogin(userId: Int, password: String): DbReader[Boolean] = for
  maybeUsername <- findUsername(userId)
  result <- maybeUsername
    .map { username =>
      checkPassword(username, password)
    }
    .getOrElse(false.pure[DbReader])
yield result

val users = Map(
  1 -> "dade",
  2 -> "kate",
  3 -> "margo"
)

val passwords = Map(
  "dade" -> "zerocool",
  "kate" -> "acidburn",
  "margo" -> "secret"
)

val db = Db(users, passwords)

checkLogin(1, "zerocool").run(db)
checkLogin(4, "davinci").run(db)

//------- Writer --------//
import cats.data.Writer

type Logged[A] = Writer[String, A]

// Needs a monoid instance in scope
123.pure[Logged]

val someComputation: Logged[Int] = for {
  _ <- "First computation\n".tell
  a <- 10.pure[Logged]
  _ <- "Second computation\n".tell
  b <- 32.pure[Logged]
  result <- (a + b).writer("Adding up results\n")
} yield a + b

val someOtherComputation: Logged[Int] = for {
  _ <- "Third computation\n".tell
  a <- 123.pure[Logged]
  _ <- "Fourth computation\n".tell
  b <- 42.pure[Logged]
  result <- (a + b).writer("Adding up results\n")
} yield a + b

def finalResult = for
  someResult <- someComputation
  someOtherResult <- someOtherComputation
yield someResult + someOtherResult

finalResult.run

case class Log(msg: String, timestamp: Long)
def getTime = System.currentTimeMillis // Don't do this!

type NicerLogged[A] = Writer[List[Log], A]

for
  _ <- List(Log("First computation\n", getTime)).tell
  a <- 10.pure[NicerLogged]
  _ <- List(Log("Second computation\n", getTime)).tell
  b <- 32.pure[NicerLogged]
yield a + b

// Transform the value in context
finalResult.mapWritten(_.toUpperCase).run

// Transform both
finalResult
  .bimap(
    _.toUpperCase,
    res => res * 100
  )
  .run

finalResult.mapBoth((log, res) => (log.toUpperCase, res * 100)).run

// Change value in context to identity value
finalResult.reset

// Swap values
finalResult.swap
finalResult.swap.tell(12)

//------- State --------//
import cats.data.State

val withState: State[Int, String] = State(n => (n, s"The state is $n"))

// The result is wrapped inside an Eval for stack-safety
withState.run(2).value
withState.runS(2).value
withState.runA(2).value

// Each State instance represents a single state transition
val step1 = State[Int, String] { num =>
  val ans = num + 1
  (ans, s"Result of step 1 is $ans")
}

val step2 = State[Int, String] { num =>
  val ans = num * 2
  (ans, s"Result of step 2 is $ans")
}

val both = for {
  a <- step1 // Update state to 2 + 1 = 3
  b <- step2 // Update state to 2 * 3 = 6
} yield (a, b)

both.runS(2).value
both.runA(2).value

val getting = State.get[Int]              // Gets the current state from the context
val setting = State.set[Int]              // Sets the current state, discarding the previous one
val puring = State.pure[Int, Int]         // Wraps a pure value, preserving the state
val inspecting = State.inspect[Int, Int]  // Uses current state to return some value
val modifying = State.modify[Int]         // Modifies current state based on previous one

val program: State[Int, String] = for {
  a <- getting            // 3
  _ <- modifying(_ * 2)   // 6
  b <- getting            // 6
  _ <- setting(28)        // 28
  c <- inspecting(_ + 4)  // 28
  d <- getting            // 28
} yield s"$a then $b then $c then $d"

program.run(3).value

//------- Monad Transformers--------//
