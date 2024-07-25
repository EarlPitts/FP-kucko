//  _____                     _                         
// |_   _|                   | |                        
//   | |_   _ _ __   ___  ___| | __ _ ___ ___  ___  ___ 
//   | | | | | '_ \ / _ \/ __| |/ _` / __/ __|/ _ \/ __|
//   | | |_| | |_) |  __/ (__| | (_| \__ \__ \  __/\__ \
//   \_/\__, | .__/ \___|\___|_|\__,_|___/___/\___||___/
//       __/ | |                                        
//      |___/|_|                                        

trait Semigroup[A]:
  def combine(x: A, y: A): A

object Semigroup:
  def apply[A](implicit instance: Semigroup[A]) = instance

  implicit val andSemigroup: Semigroup[Boolean] = new Semigroup[Boolean]:
    def combine(x: Boolean, y: Boolean): Boolean = x && y

  // implicit val orSemigroup: Semigroup[Boolean] = new Semigroup[Boolean]:
  //   def combine(x: Boolean, y: Boolean): Boolean = x || y

  implicit def listSemigroup[A]: Semigroup[List[A]] = new Semigroup[List[A]]:
    def combine(x: List[A], y: List[A]): List[A] = x ++ y

Semigroup[List[Int]].combine(List(1,2,3), List(4,5,6))

trait Monoid[A: Semigroup]:
  def empty: A
  def combine = Semigroup[A].combine

object Monoid:
  def apply[A](implicit instance: Monoid[A]) = instance

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]]:
    def empty: List[A] = List.empty

Monoid[List[Int]].empty
Monoid[List[Int]].combine(List(1,2,3), List(4,5,6))

trait Functor[F[_]]:
  def map[A, B](a: F[A], f: A => B): F[B]

object Functor:
  def apply[F[_]](implicit instance: Functor[F]) = instance

  implicit def optionFunctor[A]: Functor[Option] = new Functor[Option]:
    def map[A, B](fa: Option[A], f: A => B): Option[B] = fa match
      case None => None
      case Some(a) => Some(f(a))

  implicit def listFunctor[A]: Functor[List] = new Functor[List]:
    def map[A, B](a: List[A], f: A => B): List[B] = a match
      case Nil => Nil
      case a::as => f(a) :: map(as, f)

Functor[List].map(List(1,2,3), _ + 1)

trait Applicative[F[_]: Functor]:
  def map[A,B] = Functor[F].map[A,B]
  def pure[A](a: A): F[A]

object Applicative:
  def apply[F[_]](implicit instance: Applicative[F]) = instance

  implicit def listApplicative: Applicative[List] = new Applicative[List]:
    def pure[A](a: A): List[A] = List(a)

  implicit def optionApplicative[A]: Applicative[Option] = new Applicative[Option]:
    def pure[A](a: A): Option[A] = Some(a)

Applicative[List].map(List(1,2,3), _ + 1)

trait Monad[F[_]: Applicative]:
  def map[A,B] = Applicative[F].map[A,B]
  def pure[A] = Applicative[F].pure[A]
  def flatMap[A,B](fa: F[A], f: A => F[B]): F[B]

object Monad:
  def apply[F[_]](implicit instance: Monad[F]) = instance

  implicit def listMonad: Monad[List] = new Monad[List]:
    private def flat[A](l: List[List[A]]): List[A] = l match
      case Nil => Nil
      case a :: as => a ++ flat(as)

    def flatMap[A, B](fa: List[A], f: A => List[B]): List[B] = fa match
      case Nil => Nil
      case a :: as => flat(f(a) :: map(as, f))

  implicit def optionMonad: Monad[Option] = new Monad[Option]:
    private def flat[A](ffa: Option[Option[A]]): Option[A] = ffa match
      case Some(Some(x)) => Some(x)
      case _ => None
    def flatMap[A, B](fa: Option[A], f: A => Option[B]): Option[B] = fa match
      case None => None
      case sa => flat(map(sa,f))

Monad[List].flatMap(List(1,2,3), (n => List(n + 1, n + 2)))

def succeed(x: Int): Option[Int] = Some(x + 1)
def fail(x: Int): Option[Int] = None

def flatMap = Monad[Option].flatMap[Int, Int]

flatMap(
  flatMap(
    flatMap(
      flatMap(Some(2),
        succeed),
      succeed),
    fail),
  succeed)

for
  a <- Some(2)
  b <- succeed(a)
yield b
