# Partial Functions

- weaken the postconditions (promise):
  - convenient when implementing
  - harder every time when calling!
- strenghten the preconditions:
  - more structured data types
  - more static information about values
  - NonEmptyList[A] <=> List[A] + proof of non-emptiness
  - NonZero <=> Int + proof of non-negativeness
  - No redundant checks

---

# Parsing vs Validation

- parse, don't validate:
  - make illegal states unrepresentable
  - capturing invariants in the type system
  - proving invariants should be done as early as possible
  - Shotgun parsing

---

# Validating

- Data -> Unit
- Information is lost
- We have to validate again if we want to make sure
- Nothing stops us from accidentally removing validations
- Doesn't really take advantage of the type-system

---

# Parsing

- Data -> Option[StructuredData]
- We *refine* the input type
- We go from less to more structured data
- The parser "gives us back" the information it learned
- Once we validated data with a parser, we don't have to do it again!
- Let the type-system do the validation
- Parsing at the edges -> we don't have to validate anything in the BL
- Validation -> Execution

---

# Shotgun "Parsing"

- Validating code is spread across the program
- Hpoing that it will catch invalid states before bad things happen
- Can be too late
- Errors can happen anywhere, so everything is more complex
- Validation -> Execution -> Validation -> Execution -> ...

---

# DDD

- We want to model the domain as close as possible
- We also want to get the data into the domain model as soon as possible:
  - Parse at the system boundary into precise types
- Segment without definition and template is invalid
- Option fields:
  - Pushing the responsibility to the consumers
  - The type doesn't guarantee much
  - Use sum-types instead

---

# References

- [Parse, Don't Validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/) - The original post
- [Parse, Don't Validate and Type-Driven-Design in Rust](https://www.harudagondi.space/blog/parse-dont-validate-and-type-driven-design-in-rust/) - With Rust
- [Making Impossible States Impossible - Richard Feldman](https://www.youtube.com/watch?v=IcgmSRJHu_8)
