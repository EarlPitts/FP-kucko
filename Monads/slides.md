- What's the abstraction:
    - Programmable semicolon
    - compare it to imperative
- Most popular ones
- Useful combinators
- Monad transformers:
    - Why they dont combine
    
# Monads

- "Programozhato pontosvesszo"
- Functor + Applicative + flatMap
- Fuggveny kompozicio valami plusz szemantikaval
- Struktura/kontextus valtozhat
- Szekvencialis jellegu szemantikat tudunk megadni
- Elozo ertek alapjan dontest hozni

---

```scala
val flatMap[A,B]: M[A] => (A => M[B]) => M[B]
val pure[A]: A => M[A]
val >>[A,B]: M[A] => M[B] => M[B]
```

---

```scala
val f: A => M[B]
val g: B => M[C]

f(a).flatMap(g)
```
---

# Identity

>> "Encodes the effect of having no effect."

- Tesztelesnel hasznos tud lenni
- Monad transformereknel meg elo fog jonni

---

# List

- Nem-determinisztikussag

---

# Option

- Meglepo modon opcionalitast fejez ki
- Hibakezeles
- Elso "hibanal" "leall"

---

# Either

- Hasonlo az Option-hoz
- Plusz informaciot lehet kodolni a kontextusban
- Either vs Validated

---

# Reader

- Fuggveny monad
- Az input a kontextus resze
- "Read-only"
- Cats-ben, ScalaZ-ben Kleisli
- Dependency-injection

---

# Writer

- 2-Tuple
- "Write-only"
- A kontextusban levo tipus monoid kell, hogy legyen
- Logging

---

# State

- Olvasni es irni is lehet a kontextusban levo erteket

---

# Monad Transformerek

- Problema: kulonbozo monadok nem kombinalhatok trivialisan
- Ket akarmilyen monadot nem tudunk kombinalni
- De: Ha az egyiket le tudjuk fixalni, akkor mar mukodik
- Altalaban "T" postfix jelzi oket
- Cats-ben a `cats.data` package-ben vannak
- Pl.: OptionT, ReaderT, WriterT, StateT...
- Fun fact:
    - Reader[A,B] = ReaderT[Id, A, B]
    - Writer[A,B] = WriterT[Id, A, B]
    - State[A,B]  = StateT[Id, A, B]
