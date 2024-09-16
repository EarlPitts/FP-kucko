# Turing Machine

- Alan Turing (1936)
- State machine with infinite tape
- Based on mutation
- Operational/mechanical
- Foundation for imperative languages

---

# Untyped Lambda Calculus

- Alonzo Church (1936)
- Turing complete
- Church-Turing Theses
- Mathematical Functions
- Foundation for functional languages

---

# Terms

- Lambda Abstraction
- Lambda Application
- Variables

```
(λx.x)          // Abstraction
(λx.x) (λy.y)   // Application
```
---

# Currying

- Every function has only one parameter
- But we have syntax sugar:

```
(λx.λy.x) -> (λxy.x)
```

---

```scala
def f[A](x: A, y: A): A
def g[A](x: A)(y: A): A

val f: [A] => (A, A) => A
val g: [A] => A => A => A
```

---

# Evaluation

- β-reduction (function application):
    - Substitution
    - Capture avoidance
- Normal form: lambda abstraction
- Evaluation strategies:
  - Call-by-value (strict)
  - Call-by-name (lazy)

---

## Substitution

```
M[x := N]

(λxy.x) (λx.x)
(λy.x)[x := (λx.x)]
(λy.(λx.x))
```

---

## Example

```
((λx.λy.x x) (λx.x)) (λx y.x y)
(λy.(λx.x) (λx.x)) (λx y.x y)
(λx.x) (λx.x)
(λx.x)
```

---

# General Recusion

- Y combinator (also Z for strict languages)
- Y = λf.((λx.f (x x) (λx.f (x x))))

---

```
λf.(λx.f (x x)) (λx.f (x x))
λf.f((λx.f (x x)) (λx.f (x x)))
λf.ff((λx.f (x x)) (λx.f (x x)))
λf.fff((λx.f (x x)) (λx.f (x x)))
λf.ffff((λx.f (x x)) (λx.f (x x)))
...
```
---

# Church Encoding

- Representing data types with functions

---

# Combinatory Logic

- No variables (only the arguments can be used)
- SKI calculus
- I = (λx.x)
- K = (λxy.x)
- S = (λxyz.x z (y z))

---

# Hello World

```
S(SI(K(S(S(SSI)I))))
(S(SI(K(S(SIS)(KS))))
 (S(SS)I(K(S(SI(K(S(S(SI(K(SS)))(K(KI))))))))
  (S(SI(K(SS(S(K(S(SI)))K))))
   (S(SI(K(S(SI(S(K(SIS)))))))
    (S(SI(K(S(S(S(S(SSI))))K)))
     (S(SI(K(SI(S(S(SS))I))))
      (S(SI(K(SS(S(K(S(SI)))K))))
       (S(SI(K(S(SI(SI(S(K(S(SI)))))))))
        (S(SI(K(S(S(SI(K(SS)))(K(KI))))))
         (S(SI(K(S(SII)(K(SS)))))
          (S(SI(K(K(S(S(S(S(SI)))(KI))))))
           (K(KI)))))))))))))
(S(KS)
 (S(K(S(K(S(KS)K))))
  (S(K(SI))
   (S(KK)
    (S
     (S
      (S
       (SS(S(S(KS)S))(KS))
       (KS))
      (K(S(KS)K)))
     (KI))))))
```

---

# The Ultimate Turing Tarpit

- Iota: a single combinator

---

# References

- [A Flock of Functions I](https://youtu.be/6BnVo7EHO_8?si=wmogqZIP5j0dw_tp)
- [A Flock of Functions II](https://youtu.be/pAnLQ9jwN-E?si=CohrrQG5PIAPTi1g)
- https://combinatorylogic.com/
- https://en.wikipedia.org/wiki/Iota_and_Jot
- Benjamin Pierce: Types and Programming Languages
- Tom Stuart: Understanding Computation
- [Programming with Nothing](https://youtu.be/VUhlNx_-wYk?si=jmzNWwHkcskYMjZc)
- https://en.wikipedia.org/wiki/Lambda_calculus
