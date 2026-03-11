- parse, don't validate:
  - https://www.harudagondi.space/blog/parse-dont-validate-and-type-driven-design-in-rust/
  - make illegal states unrepresentable
  - proving invariants should be done as early as possible
  - Shotgun parsing

- Validating: Data -> Unit
- Parsing: Data -> Option[StructuredData]


- Option fields:
  - Pushing the responsibility to the consumers
  - The type doesn't guarantee much

- DDD:
  - We want to model the domain as close as possible
  - Segment without definition and template is invalid
