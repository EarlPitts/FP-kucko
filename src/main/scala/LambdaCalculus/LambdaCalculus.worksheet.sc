import LambdaCalculus.*
import LambdaCalculus.Term.*
import LambdaCalculus.RawTerm.*

val rt = "\\x.x"
val rt2 = "\\x.\\y.x"
val rt3 = "\\x.\\y.(x y)"
val rt4 = "\\x.\\y.\\z.((x y) z)"
val rt5 = "(\\x.x \\x.x)"

p.parse(rt).map(Term.toNameless)

val t = TmApp(TmAbs(TmVar(0,1)), (TmAbs(TmVar(0,1))))
val t2 = TmApp(TmAbs(TmVar(1,2)), (TmAbs(TmVar(0,2))))
val t3 = TmApp(TmAbs(TmAbs(TmApp(TmVar(2,3), TmVar(0,3)))), TmAbs(TmVar(0,2)))

def app(t1: Term, t2: Term): Term = TmApp(t1,t2)

val emptyCtx = List()

// Basic combinators
val id = TmAbs(TmVar(0,1))
val const = TmAbs(TmAbs(TmVar(1,2)))
val comp = TmAbs(TmAbs(TmAbs(TmApp(TmVar(2,3),TmApp(TmVar(1,3),TmVar(0,3))))))

def id(x: Term) = TmApp(TmAbs(TmVar(0,1)), x)
def const(x: Term, y: Term) = TmApp(TmApp(TmAbs(TmAbs(TmVar(1,2))), y), x)

// Booleans
val tru = TmAbs(TmAbs(TmVar(1,2)))
val fls = TmAbs(TmAbs(TmVar(0,2)))

def and(x: Term, y: Term): Term = app(app(x,y),fls)

def myIf(g: Term, t1: Term, t2: Term): Term = TmApp(TmApp(g, t1), t2)

Term.eval(emptyCtx, t)
Term.eval(emptyCtx, myIf(fls, fls, tru))
Term.eval(emptyCtx, and(tru, tru))
