import cats.data.NonEmptyList

def div(a: Int, b: Int): Int =
  a / b

// Weakening the return type
// Pushing the responsibility to the consumer (downward)
def safeDiv(a: Int, b: Int): Option[Int] =
  if b == 0 then None else Some(a / b)

object Weaken:
  type FilePath = String

  def getConfigDirs: List[FilePath] =
    val configDirsString = sys.env("CONFIG_DIRS")
    val configDirList = configDirsString.split(",").toList
    if configDirList.isEmpty
    then throw new RuntimeException("CONFIG_DIRS cannot be empty")
    else configDirList

  def initializeCache: String => Unit = ???

  def app: Unit =
    val configDirs = getConfigDirs
    configDirs.headOption match
      case None => throw Exception("Cannot happen, we already checked!")
      case Some(cacheDir) => initializeCache(cacheDir)

  // This is annoying
  // It also has redundant checks
  // But worse still: if someone modifies getConfigDirs
  // to not check for empty, but forgets to modify
  // the call-site, it could cause a bug!

  // We want to statically prove the impossibility,
  // so a wrong usage would fail to compile

case class NonZero(n: Int)

def mkNonZero(a: Int): Option[NonZero] =
  if a == 0 then None else Some(NonZero(a))

// Strengthening the parameter type
// Pushing the responsibility to the caller (upwards)
def safeDiv2(a: Int, b: NonZero): Int =
  a / b.n

// div(2,0)
safeDiv(2, 0)

object Strengthen:
  type FilePath = String

  def getConfigDirs: NonEmptyList[FilePath] =
    val configDirsString = sys.env("CONFIG_DIRS")
    val configDirList = configDirsString.split(",").toList
    NonEmptyList.fromList(configDirList) match
      case None => throw new RuntimeException("CONFIG_DIRS cannot be empty")
      case Some(nonEmptyConfigDirList) => nonEmptyConfigDirList

  def initializeCache: String => Unit = ???

  def app: Unit =
    val configDirs = getConfigDirs
    initializeCache(configDirs.head)

  // Information about the non-emptiness of
  // the value is preserved inside the type-system

  // Dropping the check forces the return type to change,
  // which makes to program not compile if the call-site is not modified

  // It's also trivial to get back the original
  // weakened head, because our type has strictly
  // more information about its values
  def ourHeadOption[A](l: List[A]): Option[A] =
    NonEmptyList
      .fromList(l)
      .map(_.head)

// Validation vs. Parsing

// In case of validating a value, we immediately lose
// the information that it ever happened
def validateNonEmpty[A]: List[A] => Unit =
  case (_ :: _) => ()
  case Nil      => throw new RuntimeException("List cannot be empty")

// When parsing however, we retain this information
// in the typesystem itself
def parseNonEmpty[A]: List[A] => NonEmptyList[A] =
  case (x :: xs) => NonEmptyList(x, xs)
  case Nil       => throw new RuntimeException("List cannot be empty")

// Making illegal states unrepresentable
// Use encodings that guarantee that later
// you can write total functions that act
// upon them

type Definition = String
type Template = String
type Result = Int

def runDefinition(d: Definition): Result = 10
def runTemplate(d: Template): Result = 10

object Bad:

  case class Segment(
      definition: Option[Definition],
      template: Option[Template]
  )

  // Each time we run a segment, we have to validate
  // if the value represents a valid case of our domain
  def runSegment(s: Segment): Option[Result] =
    if s.definition.isEmpty && s.template.isEmpty
    then None
    else if s.definition.isDefined
    then Some(runDefinition(s.definition.get))
    else Some(runTemplate(s.template.get))

object Good:
  enum Segment:
    case Defined(definition: Definition)
    case Stored(template: Template)

    def runSegment(s: Segment): Result = s match
      case Defined(d) => runDefinition(d)
      case Stored(t)  => runTemplate(t)

// Use smart-consctuctors and opaque types

opaque type CustomerId = Int
object CustomerId:
  def apply(n: Int): Option[CustomerId] =
    Option.when(n < 10000 && n > 0)(n)

opaque type TemplateId = Int
object TemplateId:
  def apply(n: Int): Option[TemplateId] =
    Option.when(n < 100 && n > 0)(n)

opaque type BusinessAreaId = String
object BusinessAreaId:
  def apply(baString: String): Option[BusinessAreaId] =
    Option.when {
      baString.length < 10 &&
      baString.forall(_.isLower)
    }(baString)

// In Scala 2
// case class TemplateId(n: Int) extends AnyVal
// object TemplateId:
//   def apply(n: Int): Option[TemplateId] =
//     Option.when(n < 100 && n > 0)(new TemplateId(n))

case class Segment(
    customerId: CustomerId,
    templateId: TemplateId,
    businessAreaId: BusinessAreaId
)
