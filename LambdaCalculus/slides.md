# Untyped Lambda Calculus

- Alonzo Church (1936)
- Turing complete
- Church-Turing Theses
- Mathematical Functions

---

# Terms

- Lambda Abstraction
- Lambda Application
- Variables

```
(λx.x)
(λx.x) (λy.y)
```
---

# Currying

```
(λx.λy.x) -> (λxy.x)
```

---

# Evaluation

- β-reduction (function application):
    - Substitution
    - Capture avoidance
- Normal form: lambda abstraction
- Evaluation strategies

---

```
M[x := N]

(λxy.x) (λx.x)
(λy.x)[x := (λx.x)]
(λy.(λx.x))
```

---

```
((λx.λy.x x) (λx.x)) (λx y.x y)
(λy.(λx.x) (λx.x)) (λx y.x y)
(λx.x) (λx.x)
(λx.x)
```

---

# Combinatory Logic

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

- Iota: a single combinator

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



# Church Encoding

- Representing data types with functions

---

# References

- [A Flock of Functions I](https://youtu.be/6BnVo7EHO_8?si=wmogqZIP5j0dw_tp)
- [A Flock of Functions II](https://youtu.be/pAnLQ9jwN-E?si=CohrrQG5PIAPTi1g)
- https://combinatorylogic.com/
- https://en.wikipedia.org/wiki/Iota_and_Jot
- Benjamin Pierce: Types and Programming Languages
- Tom Stuart: Understanding Computation
- https://en.wikipedia.org/wiki/Lambda_calculus
