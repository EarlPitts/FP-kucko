- try:
    - Working with java libs
    - isomorphic to either
- Parsing
- safehead
- Applicative vs Monad:
    - Monad has shortcircuiting semantics
- Technical failure vs. business edge case



# Conventional Exceptions

- GOTO-like semantics
- Typesystem is bypassed
- Potential of error-throwing is not reflected in function signature:
    - You need some informal way to communicate this (e.g.: comments)
    - Or read the implementation (cannot treat module as black box)
- Compiler cannot give you guarantees:
    - Unhandled errors blow up your program at runtime
- Have to read implementation of all methods recursively
- Throwing error -> Partial function

# Typed Exceptions

- Reification of errors as data
- Compiler forces you to handle errors:
    - Errors represented as ADTs -> compiler checks if everything is handled
    - Much stronger guarantee that there won't be runtime errors

# Null

# Cats Effect IO

- Based on continuations:
    - FP's GOTO

# Cheat Sheet


| Data Structure    | Failure Case | Success Case |
|-------------------|--------------|--------------|
| Option[_]         | None         | Some[_]      |
| Try[_]            | Failure[_]   | Success[_]   |
| Either[E,_]       | Left[E, _]   | Right[E,_]   |
| Validated[F[E],_] |              |              |
| IO[_]             | IO[_]        | IO[_]        |

| ApplicativeError[E,_] | ApplicativeError[E,_] | ApplicativeError[E,_] |
| MonadError[E,_] | MonadError[E,_] | MonadError[E,_] |
| MonadThrow[_]   | MonadThrow[_]   | MonadThrow[_]   |


# Resources

- https://guillaumebogard.dev/posts/functional-error-handling/
- https://youtu.be/KQZjOJjnHIE?si=8hIh390yd51kECcL
