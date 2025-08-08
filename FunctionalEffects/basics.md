# Purity

- Computing values (mathematical functions)
- Mapping values to other values
- If it can be replaced by a map, it's pure:
  - caching
- Not enough:
  - Lacks interaction with the outside world
  - We want to see/use the results
  - We want **effects**!

---

# Reasoning

- Substitution model
- Local reasoning
- Referential transparency

---

# Expressions

- Functional programs are **expressions**
- We run the program by **evaluating** the expression
- Expressions (values) **compose**

---

# Compositionailty

- Functions can be composed together to form bigger functions
- Types and functions over them form a **category**
- **Composition** of morphisms is function composition:
  - Composition is **associative**
  - **Identity** is the `identity` function

---

# Effects

- Partial functions
- Exceptions
- Nondeterminism
- Communication with the outside world (outside the local scope):
  - Accessing the filesystem
  - Writing into/receiving from a socket
  - Getting the current time from the OS
  - Reading from/writing to stdin/stdout
  - Mutating values
- We need effects to do anything that the *shareholders* want
- Pure functions can only heat up the CPU

---

# Side-Effect vs Managed Effect

> Effects are good. Side effects are bugs. - Rob Norris

- Values vs. Statements:
  - Functional Effects are values
  - Side-effects are statements
- Values compose, statements not (as well):
  - You can only compose statements by sequencing them
  - In imperative-style code, sequencing is implicit (;)
  - With managed effects, they become explicit (control structures expressed with monads)
- Managing side-effects:
  - Defensive programming
  - Localize effects

---

# Managed Effects

- The effect is represented by a value:
  - We can freely manipulate this value until we run it
- The type should contain the kind of effect the program performs (and the value)
- We have a separate DSL for side-effects inside our language

---

# Compositionailty Round 2

- How do we keep compositionality?
- The function types don't match!
- Are we doomed?

---

# Let's look at the types

```scala
type F[A] = Option[A]
type F[A] = Either[E, A]
type F[A] = List[A]
type F[A] = Reader[R, A]
type F[A] = Writer[W, A]
type F[A] = State[S, A]
```

- All of them share the same structure:
  - `F[_]` is the effect
  - It has some value it computes (`A`)
  - It can also have some other value it threads through
- We want to compose functions that do something *extra*

---

# Solution

- Kleisli
- Does it look familiar?

```scala
trait Kleisli[F[_]]:
  // Identity
  def pure[A](a: A): F[A]
  
  // Composition
  def >=>[A, B, C](f: A => F[B], g: B => F[C]): F[C]
```

---

# Monad

-  `def flatMap[A,B](a: F[A], f: A => F[B]): F[B]`
- Applicative vs. Point-Free style
- Monad is really about composing effectful functions

---

- Resources:
  - [Functional Programming with Effects - Rob Norris](https://youtu.be/30q6BkBv5MY?si=fA2-bZyiKOb5SSlZ)
  - Essential Effects (first chapter)
  - [Magic Tricks with Functional Effects - John De Goes](https://www.youtube.com/watch?v=xpz4rf1RS8c&t=3762s&pp=ygUXam9obiBkZSBnb2VzIG1hZ2ljIHdpdGg%3D)
  - FP in scala (chapter 13)
