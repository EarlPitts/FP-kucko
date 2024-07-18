# Mi az a Typeclass?

## Nyelvi Funkcio

- Idris
- Haskell
- Purescript

## Design Pattern

- Scala

---

# Polimorfizmus

- altalanosabb kod
- kod ujrahasznalat

---

# Polimorfizmus Tipusok

- Parametrikus
- Ad-hoc

---

# Parametrikus Polimorfizmus

- van legalabb egy tipusparameter, ami barmilyen erteket felvehet
- a fuggvenynek ugyanugy kell viselkednie minden parameterre

Pl:

```scala
def reverse[A]: List[A] => List[A]
```
---

# Ad-Hoc Polimorfizmus

- kulonbozo tipusu parameterekre mas-mas implementacio
- hasonlo: overloading

Pl:

```scala
===
+
```
---

# Interface

- egy megoldast ad ad-hoc polimorfizmusra

```java
interface Equal<A> {
    public boolean eq(A other);
}

class Segment {
    public String segmentId;
    public int customerId;
```

---

```java
interface Equal<A> {
    public boolean eq(A other);
}

class Segment implements Equal<Segment> {
    public String segmentId;
    public int customerId;
    public boolean eq(Segment other) {
        return this.customerId == other.customerId
        && this.segmentId.equals(other.segmentId);
    }
```
---

```java
static <A extends Equal<A>> boolean elementOf(A a, List<A> list) {
    for (A element : list) {
        if (a.eq(element)) return true;
    }
    return false;
}
```
---

```java
"kecskefa".eq("kecskefa")
```
---

```java
package java.lang;

class String {
    private char[] value;
    // other definitions
}
```

---

```java
package java.lang;

class String implements Equal<String> {
    private char[] value;
    // other definitions
}
```
- ilyet sajna nem lehet :(

---

```java
class List<A> implements Equal<List<A>> {
    // implementation details
    
    public boolean eq(List<A> other) {
        // implementation...
        // ... but how do we compare A for equality?
    }
}
```
- es ilyet sem

---

- nem lehet kondicionalis
- nem tudunk olyan tipushoz implementaciot adni, ami nem a mienk

---

# Type Classok

- mint az interfacek, csak jobbak
- valamilyen tulajdonsag alapjan osszecsoportositunk tipusokat
- Pl.: meghatarozhato kozottuk valamilyen fajta egyenloseg

---


```scala
trait Equal[A]:
  def eq(x: A, y: A): Boolean
```

---

```scala
trait Equal[A]:
  def eq(x: A, y: A): Boolean
  
case class Segment(segmentId: String, customerId: Int)
```

---

```scala
trait Equal[A]:
  def eq(x: A, y: A): Boolean
  
case class Segment(segmentId: String, customerId: Int)

object Segment:
  implicit val eqInstance: Equal[Segment] = new Equal[Segment]:
    def eq(s1: Segment, s2: Segment): Boolean =
      s1.segmentId == s2.segmentId &&
      s1.customerId == s2.customerId
```

---

```scala
trait Equal[A]:
  def eq(x: A, y: A): Boolean
  
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
```

---

```scala
implicit def listEq[A: Equal]: Equal[List[A]] = new Equal[List[A]]:
    def eq(l1: List[A], l2: List[A]): Boolean = (l1,l2) match 
        case (x :: xs, y :: ys) => if Equal[A].eq(x,y) then eq(xs, ys) else false
        case (_ :: _, Nil) => false
        case (Nil, _ :: _) => false
        case (Nil, Nil) => true
```

---

# Mivel jobb ez?

- megadhatunk peldanyokat olyan tipusokhoz, amik nem a mienk
- a peldanyaink fugghetnek masik typeclass peldanyoktol
- modularisabb, nagyobb a kifejezo ereje

---

# Coherence

- egy tipushoz maximum 1 typeclass instance tartozhat
- azok a nyelvek, amik nativan tamogatjak a typeclassokat, nem engednek tobb instance-t (pl.: Idris)
- sajnos Scalaban ezt alapbol nem koveteli meg a compiler (de Scala 3-ban mar kicsivel jobb a helyzet)

---

# Orphan Instances

- ket helyen szokas typeclass instance-okat definialni:
    - a typeclass mellett (modul, companion object, stb...)
    - a definialt tipusunk mellett (modul, companion object, stb...)
- ha mashova tesszuk, orphan instance lesz

---

# Implicits

- rugalmasabbak es kifejezobbek, mint a typeclassok
- pont emiatt konnyebb elrontani, tul sok mindent megenged, nem segit a nyelv
- nincs megkotes a szamukon
- nincs "orphan implicit"
