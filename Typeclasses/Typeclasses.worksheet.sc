def reverse[A]: List[A] => List[A] =
  xs => xs match
    case Nil => Nil
    case x :: xs => reverse(xs) :+ x

trait Equal[A]:
  def eq(x: A, y: A): Boolean

object Equal:
  def apply[A](implicit instance: Equal[A]): Equal[A] = instance

  implicit def listEq[A: Equal]: Equal[List[A]] = new Equal[List[A]]:
    def eq(l1: List[A], l2: List[A]): Boolean = (l1,l2) match 
      case (x :: xs, y :: ys) => if Equal[A].eq(x,y) then eq(xs, ys) else false
      case (_ :: _, Nil) => false
      case (Nil, _ :: _) => false
      case (Nil, Nil) => true


case class Segment(segmentId: String, customerId: Int)

object Segment:
  // implicit val eqInstance: Equal[Segment] = new Equal[Segment]:
  //   def eq(s1: Segment, s2: Segment): Boolean =
  //     s1.segmentId == s2.segmentId &&
  //     s1.customerId == s2.customerId

  given Equal[Segment] with
    def eq(s1: Segment, s2: Segment): Boolean =
      s1.segmentId == s2.segmentId &&
      s1.customerId == s2.customerId
    
val s1 = Segment("kecske", 12345)
val s2 = Segment("fa", 12345)
val s3 = Segment("kecske", 12345)
val s4 = Segment("fokhagyma", 12345)

Equal[Segment].eq(s1,s3)

def elementOf[A: Equal](a: A, as: List[A]): Boolean = as match
  case Nil => false
  case h :: t => Equal[A].eq(a,h) || elementOf(a,t)

val segments = List(s1,s2,s3,s4)

elementOf(s4, segments)

Equal[List[Segment]].eq(segments, segments)

//--------------//

sealed trait Ordering
case object LT extends Ordering
case object EQ extends Ordering
case object GT extends Ordering

trait Order[A]:
  def compare(x: A, y: A): Ordering

object Order:
  def apply[A](implicit instance: Order[A]): Order[A] = instance

case class Dog(name: String, age: Int)

object Dog:
  implicit val dogOrderingAge: Order[Dog] = new Order[Dog]:
    def compare(x: Dog, y: Dog): Ordering =
      if x.age > y.age then GT
      else if x.age < y.age then LT
      else EQ

  // implicit val dogOrderingName: Order[Dog] = new Order[Dog]:
  //   def compare(x: Dog, y: Dog): Ordering =
  //     if x.name > y.name then GT
  //     else if x.name < y.name then LT
  //     else EQ
  

def sort[A: Order](ns: List[A]): List[A] = ns match
  case Nil => Nil
  case n::ns =>
    val lower = sort(ns.filter(Order[A].compare(_,n) == LT))
    val higher = sort(ns.filter(Order[A].compare(_,n) == GT))
    lower ++ List(n) ++ higher

val d1 = Dog("Mazsola", 5)
val d2 = Dog("Berci", 7)
val d3 = Dog("Morgo", 3)
    
sort(List(d1,d2,d3))

