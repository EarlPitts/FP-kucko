# Monads

- Functor + Applicative + flatMap
- Struktura/kontextus valtozhat
- Szekvencialis jellegu szemantikat tudunk megadni
- Elozo ertek alapjan dontest hozni
- "Programozhato pontosvesszo"
- Fuggveny kompozicio valami plusz szemantikaval

---

```python
a = input();
_ = print(a);
b = 1 + 2;
_ = print(b);
```

```haskell
do
  a <- getLine
  print a
  let b = 1 + 2
  print b
```

```scala
for
  a <- IO.readLine
  _ <- IO.println(a)
  b = 1 + 2
  _ <- IO.println(b)
yield ()
```

---

```scala
val flatMap[A,B]: M[A] => (A => M[B]) => M[B]
val pure[A]: A => M[A]
val >>[A,B]: M[A] => M[B] => M[B]

val >=>[A,B,C]: (A => M[B]) => (B => M[C]) => A => M[C]
```

---

```scala
val f: A => M[B]
val g: B => M[C]

f(a).flatMap(g)
f >==> g
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
- Typesafe "exceptions": Nem keruljuk meg a tipusrendszert
- A compiler "kikenyszeriti" a hibakezelest

---

# Either

- Hasonlo az Option-hoz
- Plusz informaciot lehet kodolni a kontextusban
- Tobb-parameteres tipusoknal:
    - a jobb szelso parameter a monadban levo ertek
    - a tobbi a kontextus resze
- Either vs Validated

---

# Reader

- Fuggveny monad
- Az input a kontextus resze
- "Read-only"
- Cats-ben, ScalaZ-ben Kleisli
- Kenyelmesen lehet egy extra parametert minden fuggvenynek atadni
- Dependency-injection

---

```scala
//kontextus resze  ---v v--- monadban levo ertek
            type `=>`[A,B] = A => B
               Either[A,B]
//kontextus resze  ---^ ^--- monadban levo ertek
```

---

# Writer

- 2-Tuple
- "Write-only"
- A kontextusban levo tipus monoid kell, hogy legyen
- Logging

---

# State

- Olvasni es irni is lehet a kontextusban levo erteket
- Kombinalja a Readert es a Writert:
    - Egyparameteres fuggveny, 2-tuple visszateresi ertekkel
- Evalt ad vissza, stack-safety miatt (`.value`)

---

# Monad Transformerek

- Problema: kulonbozo monadok nem kombinalhatok trivialisan
- Ket akarmilyen monadot nem tudunk kombinalni
- Stackelheto, de nehez vele dolgozni (`x.map(_.map(_.map(f))) ...`)
- De: Ha az egyiket le tudjuk fixalni, akkor mar mukodik
- Altalaban "T" postfix jelzi oket
- Cats-ben a `cats.data` package-ben vannak
- Pl.: OptionT, ReaderT, WriterT, StateT...
- Fun fact:
    - Reader[A,B] = ReaderT[Id, A, B]
    - Writer[A,B] = WriterT[Id, A, B]
    - State[A,B]  = StateT[Id, A, B]

---

- [Brian Beckman: Don't fear the Monad](https://youtu.be/ZhuHCtR3xq8?si=_iD4E8vwbOpXgAal)
- Scala with Cats
