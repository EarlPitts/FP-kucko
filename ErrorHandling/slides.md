# Conventional Exceptions

- GOTO-like semantics
- Typesystem is bypassed
- Potential of error-throwing is not reflected in function signature:
    - You need some informal way to communicate this (e.g.: comments)
    - Or read the implementation (cannot treat module as black box)
- Compiler cannot give you guarantees:
    - Unhandled errors blow up your program at runtime
- Have to read implementation of all methods recursively
- Throwing error -> Partial function

## Null

- Million dollar mistake
- Inhabits any type:
    - The typechecker cannot help
- Defensive programming
- Checks needed everywhere

You have to remember to handle these, the compiler won't remind you

---

# Typed Exceptions

- Goal: avoid runtime exceptions
- Reification of errors as data
- Representing errors through the type system
- Making the implicit explicit
- Compiler forces you to handle errors:
    - Errors represented as ADTs -> compiler checks if everything is handled
    - Much stronger guarantee that there won't be runtime errors
    - Removes the mental burden of keeping track of fallible computations

---

# Option/Maybe

- Nullable
- Represents the optionality of some value

```scala
case class Person(
  name: String,
  address: Address,
  hairColor: Option[Color]
)
```

```scala
def div(numerator: Float, denominator: Float): Option[Float]
```

---

# Either

- Also has information about the failure case
- By convention `Left` contains a failure value

```scala
def parseAddress(address: String): Either[AddressParseError, ValidAddress]
```

---

```scala
def doSomething(): Int =
    result = getSomeResult()
    if result != "good result" then
        throw new InvalidResultError(result)
    42
```

```scala
def doSomething(): Either[InvalidResultError, Int] =
    result = getSomeResult()
    if result != "good result" then
        Left(InvalidResultError(result))
    else Right(42)
```

---

# Java to Scala

- `Try`
- `Option`
- `toOption`
- `toEither`

---

# ApplicativeError/MonadError

- Typeclass for error handling
- Extends applicative/monad with error handling semantics

```scala
trait ApplicativeError[F[_], E] extends Applicative[F] {
  def raiseError[A](e: E): F[A]
  def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
  def handleError[A](fa: F[A])(f: E => A): F[A]
  def attempt[A](fa: F[A]): F[Either[E, A]]
  //More functions elided
}
```

---

# Cheat Sheet

| Data Structure    | Failure Case  | Success Case |
|-------------------|---------------|--------------|
| Option[_]         | None          | Some[_]      |
| Try[_]            | Failure[_]    | Success[_]   |
| Either[E,_]       | Left[E,_]     | Right[E,_]   |
| Validated[E,_]    | Invalid[E]    | Valid[_]     |
| IO[_]             | IO[_]         | IO[_]        |

---

# Resources

- [Functional Error Handling with Cats – Mark Canlas](https://youtu.be/KQZjOJjnHIE?si=8hIh390yd51kECcL)
- [Functional error handling with monads, monad transformers and Cats MTL](https://guillaumebogard.dev/posts/functional-error-handling/)
- [ApplicativeError and MonadError](https://typelevel.org/cats/typeclasses/applicativemonaderror.html)
