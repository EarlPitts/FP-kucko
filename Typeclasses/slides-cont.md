# Semigroup

- Biner operator:
    - Asszociativ
- Kombinalhatosag
- Parallelizmus
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
- Pl.:
    - Lista (ures lista az egysegelem)
    - (ℕ,+) (0 egysegelemmel)
    - (ℕ,*) (1 egysegelemmel)
- Semigroup, de nem Monoid: `NotEmptyList`

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

- Higher Kinded Type: `Functor[F[_]]`
- "Mappable"
- Intuicio: kontextus, struktura, kontener
- Beemelunk egy fuggvenyt valami kontextusba
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
// (Can be used for optimization)
map(map(fa, f), g) == map(fa, f compose g)
```

---

# Applicative

- "Fuggveny hivas strukturan belul"
- Beagyazhatunk ertekeket egy "effect-mentes" alapertelmezett kontextusba: `pure`
- Effects

---

```scala
// The identity law
pure(identity).ap(fa) == fa

// Homomorphism
pure(f).ap(pure(x)) == pure(f(x))
```
---

# Monad

- Fuggoseg
- `flatMap[A, B, M[_]](ma: M[A], f: A => M[B])`
- "Imperativ DSL"

---

```scala
// Identity laws
pure(a).flatMap(f) == f(a)
m.flatMap(pure(_)) == m
```

---

# Foldable

- Foldolhato strukturak
- Ossze lehet "lapitani" egy ertekke
- Pl.: Listak, fak (nem kecske)
- Monoidokkal/Semigroupokkal jo baratok

---

# Traversable

- Foldable es Functor
- "Effectful map"

---

# Miert jo ez nekunk?

- Hasznos eszkoztar
- Laws!
- Cats
- Megadok 2-3 fuggvenyt, kapok cserebe masik 100-at
