import cats.*
import cats.data.*
import cats.implicits.*
import cats.effect.*

object App extends IOApp.Simple:
  def run: IO[Unit] = program[ReaderT[IO, Env, Unit]]

  def program[M[_]: Monad]: M[Unit] = for
    _ <- 12.pure
  yield ()

