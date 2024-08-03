package MonadTransformers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.std.Console
import cats.effect.std.Env

//------- OptionT --------//
import cats.data.Writer
import cats.data.OptionT

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

@main
def main: Unit =
  println(addAll("1", "2", "3").run)
  println(addAll("1", "a", "3").run)

//------- ReaderT --------//
import cats.data.ReaderT

case class Env(cfg: Map[String, Int])

object App extends IOApp.Simple:
  def run: IO[Unit] =
    readEnv.flatMap(env => program[IO].run(env))

  def program[F[_]: Monad: Console]: ReaderT[F, Env, Unit] = for
    a <- doSomething
    b <- doSomethingElse
    _ <- ReaderT.liftF(Console[F].println(s"Mennyi az annyi: ${a + b}"))
  yield ()

def readEnv: IO[Env] =
  IO(Env(Map("barack" -> 12, "alma" -> 24)))

def doSomething[F[_]: Monad: Console]: ReaderT[F, Env, Int] = ReaderT { env =>
  for
    result <- 12.pure
    alma = env.cfg.get("alma").getOrElse(0)
    _ <- Console[F].println(s"Jol kiprintelem az alma erteket: $alma")
  yield result
}

def doSomethingElse[F[_]: Monad]: ReaderT[F, Env, Int] = ReaderT { env =>
  for
    barack <- env.cfg.get("barack").getOrElse(0).pure
    barackPlusz = barack + 13
  yield barackPlusz
}
