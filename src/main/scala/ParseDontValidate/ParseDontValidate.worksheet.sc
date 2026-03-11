def div(a: Int, b: Int): Int =
  a / b

// Weakening the return type
// Pushing the responsibility to the consumer (downward)
def safeDiv(a: Int, b: Int): Option[Int] =
  if b == 0 then None else Some(a / b)

case class NonZero(n: Int)

def mkNonZero(a: Int): Option[NonZero] =
  if a == 0 then None else Some(NonZero(a))

// Strengthening the parameter type
// Pushing the responsibility to the caller (upwards)
def safeDiv2(a: Int, b: NonZero): Int =
  a / b.n

// div(2,0)
safeDiv(2, 0)

// Making illegal states unrepresentable

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

