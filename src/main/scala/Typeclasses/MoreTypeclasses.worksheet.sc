//  _____                     _                         
// |_   _|                   | |                        
//   | |_   _ _ __   ___  ___| | __ _ ___ ___  ___  ___ 
//   | | | | | '_ \ / _ \/ __| |/ _` / __/ __|/ _ \/ __|
//   | | |_| | |_) |  __/ (__| | (_| \__ \__ \  __/\__ \
//   \_/\__, | .__/ \___|\___|_|\__,_|___/___/\___||___/
//       __/ | |                                        
//      |___/|_|                                        

import Types.*
import cats.data.NonEmptyList

//------- Semigroups --------//

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

  implicit def nonEmptyListSemigroup[A]: Semigroup[NonEmptyList[A]] = new Semigroup[NonEmptyList[A]]:
    def combine(x: NonEmptyList[A], y: NonEmptyList[A]): NonEmptyList[A] = x ::: y 

  implicit def intWithAdditionSemigroup[A]: Semigroup[Int] = new Semigroup[Int]:
    def combine(x: Int, y: Int): Int = x + y

  // implicit def intWithMultiplicationSemigroup[A]: Semigroup[Int] = new Semigroup[Int]:
  //   def combine(x: Int, y: Int): Int = x * y

Semigroup[Boolean].combine(true, false)
Semigroup[List[Int]].combine(List(1,2,3), List(4,5,6))
Semigroup[NonEmptyList[Int]].combine(NonEmptyList(1,List(2,3)), NonEmptyList(4,List(5,6)))

Semigroup.apply[Int](using Semigroup.intWithAdditionSemigroup).combine(3, 3)
// Semigroup.apply[Int](using Semigroup.intWithMultiplicationSemigroup).combine(3, 3)

//------- Monoids --------//

trait Monoid[A: Semigroup]:
  def empty: A
  def combine = Semigroup[A].combine

object Monoid:
  def apply[A](implicit instance: Monoid[A]) = instance

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]]:
    def empty: List[A] = List.empty

  implicit def nonEmptyListMonoid[A]: Monoid[NonEmptyList[A]] = new Monoid[NonEmptyList[A]]:
    def empty: NonEmptyList[A] = ???

  // implicit def intWithAdditionMonoid[A]: Monoid[Int] = new Monoid[Int]:
  //   def empty: Int = 0

  implicit def intWithMultiplicationMonoid[A]: Monoid[Int] = new Monoid[Int]:
    def empty: Int = 1

Monoid[List[Int]].empty
Monoid[List[Int]].combine(List(1,2,3), List(4,5,6))

Monoid[Any].combine(Any(true), Any(false))
Monoid[All].combine(All(true), All(false))

//------- Functors --------//

trait Functor[F[_]]:
  def map[A, B](fa: F[A], f: A => B): F[B]

object Functor:
  def apply[F[_]](implicit instance: Functor[F]) = instance

  implicit def optionFunctor[A]: Functor[Option] = new Functor[Option]:
    def map[A, B](fa: Option[A], f: A => B): Option[B] = fa match
      case None => None
      case Some(a) => Some(f(a))

  // TODO
  // type EitherA[A] = Either[A,_]
  // implicit def eitherFunctor[A]: Functor[EitherA] = new Functor[EitherA]:
  //   def map[A, B](fa: EitherA[A], f: A => B): EitherA[B] = fa match
  //     // case Left(a) => Left(a)
  //     case Right(a) => Right(f(a))

  implicit def listFunctor[A]: Functor[List] = new Functor[List]:
    def map[A, B](a: List[A], f: A => B): List[B] = a match
      case Nil => Nil
      case a::as => f(a) :: map(as, f)

  // type Arrow[A] = A => _
  // implicit def arrowFunctor[A]: Functor[Arrow] = new Functor[Arrow]:
  //   def map[A, B](a: Arrow[A], f: A => B): Arrow[B] = f compose a

Functor[Option].map(Some(2), _ + 1)
Functor[Option].map(Option.empty[Int], _ + 1)
Functor[List].map(List(1,2,3), _ + 1)
Functor[List].map(List.empty[Int], _ + 1)

//------- Applicatives --------//

trait Applicative[F[_]: Functor]:
  def map[A,B] = Functor[F].map[A,B]
  def ap[A,B](fab: F[A => B], fa: F[A]): F[B]
  def pure[A](a: A): F[A]

object Applicative:
  def apply[F[_]](implicit instance: Applicative[F]) = instance

  implicit def listApplicative: Applicative[List] = new Applicative[List]:
    def pure[A](a: A): List[A] = List(a)
    def ap[A, B](fab: List[A => B], fa: List[A]): List[B] = fab match
      case f :: fs => map(fa,f) ++ ap(fs, fa)
      case Nil => Nil

  implicit def optionApplicative[A]: Applicative[Option] = new Applicative[Option]:
    def pure[A](a: A): Option[A] = Some(a)
    def ap[A, B](fab: Option[A => B], fa: Option[A]): Option[B] = (fab, fa) match
      case (Some(f), Some(a)) => Some(f(a))
      case _ => None

Applicative[Option].ap[Int, Int](Some(_ + 1), Some(1))
Applicative[List].ap[Int, Int](List(_ + 1, _ + 2), List(1,2))

//------- Monads --------//

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
        succeed),        // Some(3)
      succeed),          // Some(4) 
    fail),               // None
  succeed)

for
  a <- Some(2)
  b <- succeed(a)        // Some(3)
  c <- succeed(b)        // Some(4)
  d <- fail(c)           // None
  e <- succeed(d)        // None
yield c

//------- Monoid and Folding --------//

List(1,2,3).foldLeft(Monoid[Int].empty)(Monoid[Int].combine)
List(true,false,true).map(All(_)).foldLeft(Monoid[All].empty)(Monoid[All].combine)
List(true,false,true).map(Any(_)).foldLeft(Monoid[Any].empty)(Monoid[Any].combine)


//------- Foldable --------//

import cats.implicits.*
import cats.Foldable

List.range(1,10).foldMap(identity)

Foldable[Tree].foldLeft(Tree.fromList(List(4,1,9,7,13,2,27)), 0)(_ + _)

//------- Traversable --------//

import cats.Traverse

Traverse[List].sequence(List(Option(2), Option(3)))
Traverse[List].sequence(List(Option(2), None, Option(3)))
Traverse[Option].sequence(Option(List(1,2,3)))

Traverse[List].traverse(List(1,2,3,4))(Option(_))
Traverse[List].traverse(List(1,2,3,4))((n: Int) => if n < 5 then Option(n) else None)
Traverse[List].traverse(List(1,2,3,4,5))((n: Int) => if n < 5 then Option(n) else None)

//------- Helper stuff --------//

object Types:
  case class All(value: Boolean)

  object All:
    implicit val allSemigroup: Semigroup[All] = new Semigroup[All]:
      def combine(x: All, y: All): All = All(x.value && y.value)

    implicit val allMonoid: Monoid[All] = new Monoid[All]:
      def empty: All = All(true)

  case class Any(value: Boolean)

  object Any:
    implicit val anySemigroup: Semigroup[Any] = new Semigroup[Any]:
      def combine(x: Any, y: Any): Any = Any(x.value || y.value)

    implicit val anyMonoid: Monoid[Any] = new Monoid[Any]:
      def empty: Any = Any(false)

  import cats.Order
  import cats.Eval
  enum Tree[+A]:
    case Leaf
    case Node(l: Tree[A], v: A, r: Tree[A])

  object Tree:
    def insertTree[A: Order](t: Tree[A], a: A): Tree[A] = t match
      case Leaf => Node(Leaf, a, Leaf)
      case Node(l,v,r) => if Order[A].gt(a, v) then Node(l,v,insertTree(r,a)) else Node(insertTree(l,a),v,r)

    def fromList[A: Order](l: List[A]): Tree[A] =
      l.foldl(Leaf: Tree[A])(insertTree)

    implicit val foldableInstance: Foldable[Tree] = new Foldable[Tree]:
      def foldLeft[A, B](fa: Tree[A], b: B)(f: (B, A) => B): B = fa match
        case Leaf => b
        case Node(l, v, r) => f(foldLeft(l, foldLeft(r, b)(f))(f), v)
      def foldRight[A, B](fa: Tree[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = ???
      // def foldRight[A, B](fa: Tree[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = fa match
      //   case Leaf => lb
      //   case Node(l, v, r) => foldRight(l, foldRight(
