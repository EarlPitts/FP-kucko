# Phantom Types

- Adding additional type information
- No value level/runtime representation:
  - Only for compile-time type-checking
- Encoding additional type-level information:
  - state
  - flags
- E.g.:
  - initialized/uninitialized
  - validated/unvalidated
  - cannot use unvalidated data by mistake
- Opaque constructor
- Exported smart constructor:
  - smart constructor <-> factory method

---

# Normal ADTs

- Building composite types from existing types

## Scala 2

```scala
case class Product(x: Int, y: Int)

trait Sum
case class LeftInj(x: Int) extends Sum
case class RightInt(y: Int) extends Sum
```

## Scala 3

```scala
case class Product(x: Int, y: Int)

enum Sum:
  case LeftInj(x: Int)
  case RightInt(y: Int)
```

---

# GADTs

- Generalised Algebraic Data Types
- More detailed type constructors:
  - Refine the output type
- The type of the GADT value can be determined by the constructor that created it
- Encoding additional constraints for a data type, that are checked at compile time

---

# Resources

- [GADTs for dummies](https://wiki.haskell.org/GADTs_for_dummies): Haskell
- [Real World OCaml - GADTs](https://dev.realworldocaml.org/gadts.html): OCaml
- [Thinking with Types](https://thinkingwithtypes.com/): Also haskell, book about type level programming
