package MonadTransformers

import cats.*
import cats.implicits.*
import cats.data.*
import cats.effect.*
import cats.effect.std.*

//------- ReaderT --------//
import cats.data.ReaderT

case class Env(cfg: Map[String, Int])

object App extends IOApp.Simple:
  def run: IO[Unit] =
    readEnv.flatMap(env => program[IO].run(env))

  def program[F[_]: Monad: Console]: ReaderT[F, Env, Unit] = for
    a <- doSomething
    b <- doSomethingElse
    // _ <- ReaderT.pure(Console[F].println(s"Mennyi az annyi: ${a + b}")) // This won't work!
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
