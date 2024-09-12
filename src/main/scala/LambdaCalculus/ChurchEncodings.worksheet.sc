// Booleans
def tru[A](x: A, y: A): A = x
def fls[A](x: A, y: A): A = y

def iff[A](b: (A,A) => A, x: A, y: A): A = b(x,y)

def and[A](a: (A, A) => A, b: (A, A) => A): (A, A) => A =
  (x, y) => a(b(x, y), fls(x,y))

def or[A](a: (A, A) => A, b: (A, A) => A): (A, A) => A =
  (x, y) => a(tru(x,y), b(x, y))

def toBool(b: (Boolean,Boolean) => Boolean): Boolean = b(true, false)

toBool(iff(fls, fls, tru))
toBool(and(tru, tru))

// Pairs
def pair[A](fst: A, snd: A)(b: (A, A) => A): A = b(fst, snd)
def fst[A](p: ((A,A) => A) => A): A = p(tru)
def snd[A](p: ((A,A) => A) => A): A = p(fls)

// Church Numerals
def c0[A](s: A => A, z: A): A = z
def c1[A](s: A => A, z: A): A = s(z)
def c2[A](s: A => A, z: A): A = s(s(z))
def c3[A](s: A => A, z: A): A = s(s(s(z)))

def scc[A](n: ((A => A), A) => A)(s: (A => A), z: A): A =
  s(n(s, z))

def toNum(n: ((Int => Int), Int) => Int): Int = n((_ + 1), 0)

type Num[A] = ((A => A), A) => A

def plus[A](m: Num[A])(n: Num[A])(s: (A => A), z: A): A = m(s,n(s,z))
// def times[A](m: Num[A], n: Num[A])(s: (A => A), z: A): A = m(plus(n),c0)


toNum(c2)
toNum(scc(c2))
toNum(scc(scc(c2)))

toNum(plus[Int](c2)(c3))

toBool(snd(pair(tru,fls)))
