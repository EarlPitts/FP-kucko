- implicit class for syntax
- higher kinded types
- for comprhension for my type
- Deriving typeclasses
- cats lib:
    - comes a bunch of functionality if you implement 2-3 functions

---

# Semigroup

- Biner operator:
    - Asszociativ
- Kombinalhatosag
- Parallelism
- Map/reduce
- Pl.:
    - Termeszetes szamok osszeadassal
    - Stringek, string konkatenacioval
    - Logok
    - NonEmptyList

---

## Laws

```scala
// Asszociativitas
combine(combine(x, y), z) == combine(x, combine(y, z))
```

---

# Monoid

- Biner operator:
    - Asszociativ
- Egysegelem
- NotEmptyList

---

## Laws

```scala
// Egysegelem
combine(x, empty) == combine(empty, x) == x

// Asszociativitas
combine(combine(x, y), z) == combine(x, combine(y, z))
```
---

# Functor

- "Mappable"
- Higher Kinded Type
- Intuicio: kontextus, struktura, kontener
- Pl.:
    - List
    - Option
    - Either

---

## Laws

```scala
// Mapping identity has no effect
map(fa, identity) == identity(fa)

// Mapping the composition is the same as composing maps
map(map(fa, f), g) == map(fa, f compose g)
```

---

# Apply + Applicative

- "Fuggveny hivas strukturan belul"
- Effects
- Parallelism

---

```scala
// The identity law
pure(identity).ap(fa) == fa

// Homomorphism
pure f <*> pure x = pure (f x)
pure(f).ap(pure(x)) == pure(f(x))
```

---

# Monad

- Fuggoseg

# Foldable

TODO

# Traversable

TODO
