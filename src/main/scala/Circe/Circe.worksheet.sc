//  _____ _              
// /  __ (_)             
// | /  \/_ _ __ ___ ___ 
// | |   | | '__/ __/ _ \
// | \__/\ | | | (_|  __/
//  \____/_|_|  \___\___|
//
import java.util.UUID
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

// Scala datatype -- Encoder -> io.circe.Json <- io.circe.parser -- Json String
// Scala datatype <- Decoder -- io.circe.Json ----- _.spaces2 ----> Json String

// Verbose
val jsonString = Json.fromString("Hello!")
val jsonNumber = Json.fromInt(12)
val jsonArray = Json.arr(jsonString, jsonNumber)
val jsonObject = Json.obj("foo" -> jsonString, "bar" -> jsonNumber)

// Using syntax
val jsonString2 = "Hello!".asJson
val jsonNumber2 = 12.asJson
val jsonArray2 = List(jsonString, jsonNumber).asJson
val jsonObject2 = Map("foo" -> jsonString, "bar" -> jsonNumber).asJson

jsonString.mapString(_.map(_.toUpper))
jsonString.mapBoolean(_ && true)
jsonArray.mapArray(_.map(_.mapString(_.toUpperCase)))

// Cursors can be used to traverse the tree
// They are similar to zippers

val obj = Map("baz" -> jsonObject).asJson

obj.hcursor
  .downField("baz")
  .downField("foo")
  .withFocus(_.mapString(_.toUpperCase))
  .top
  .map(_.spaces2)

// Encoders
case class Author(name: String, bio: Option[String])

case class Article(id: UUID, title: String, content: String, author: Author)

val bela = Author("Kiss Bela", None)
val article = Article(
  UUID.randomUUID(),
  title = "Something",
  content = "Very good article",
  author = bela
)

object Author:
  given Encoder[Author] with
    def apply(a: Author): Json = Json.obj(
      "name" -> a.name.asJson,
      "bio" -> a.bio.asJson
    )

// You get a bunch of decoders for free after defining one for your type
summon[Encoder[Option[Author]]]
summon[Encoder[List[Author]]]
summon[Encoder[Set[Author]]]
summon[Encoder[Map[String, Author]]]

object Article:
  given Encoder[Article] with
    def apply(a: Article): Json = Map(
      "id" -> a.id.asJson,
      "title" -> a.title.asJson,
      "content" -> a.content.asJson,
      "author" -> a.author.asJson
    ).asJson

bela.asJson
article.asJson
List.fill(4)(article).asJson

// Decoders
// While encoders cannot fail, decoders can

implicit val authorDecoder: Decoder[Author] =
  Decoder.forProduct2("name", "bio")(Author.apply)

// Result[A] = Either[DecodingFailure, A]
authorDecoder(bela.asJson.hcursor)
bela.asJson.as[Author]

val invalidAuthor = Map("name" -> "Jani".asJson, "bio" -> 1.asJson).asJson
authorDecoder(invalidAuthor.hcursor)
authorDecoder.decodeAccumulating(invalidAuthor.hcursor)

decode[Author](bela.asJson.noSpaces)
decode[Author](invalidAuthor.noSpaces)
decodeAccumulating[Author](invalidAuthor.noSpaces)

// By using the Result Monad, you can create decoders, where
// decoding some field can depend on some other field's value,
// but you can't accumulate the errors
implicit val sequentialArticleDecoder: Decoder[Article] = cursor =>
  for
    title <- cursor.get[String]("title")
    id = UUID.nameUUIDFromBytes(title.getBytes)
    content <- cursor.get[String]("content")
    author <- cursor.get[Author]("author")
  yield Article(id, title, content, author)

// This uses an applicative instance, so accumulating errors is possible
implicit val articleDecoder: Decoder[Article] = Decoder.forProduct4(
  "id",
  "title",
  "content",
  "author"
)(Article.apply)

sequentialArticleDecoder.decodeAccumulating(bela.asJson.hcursor)
articleDecoder.decodeAccumulating(bela.asJson.hcursor)

// Parsing string into Json
parse(bela.asJson.noSpaces).flatMap(_.as[Author]) // Same as decode

object AutoDerivation:
  // This will generate generic instances of encoders and decoders for everything in scope
  // This is usually a bad idea, as it whill shadow other encoders and decoders we may have in scope
  import io.circe.generic.auto.*

  case class Cheese(age: Int)
  case class Shop(cheeses: List[Cheese])

  implicitly[Encoder[Cheese]]
  implicitly[Decoder[Cheese]]

  implicitly[Encoder[Shop]]
  implicitly[Decoder[Shop]]

object SemiAutoDerivation:
  import io.circe.generic.semiauto.*

  case class User(firstName: String, lastName: String)

  val user = User("Pista", "Kiss")

  // user.asJson
  val s = "{\"firstName\": \"Pista\", \"lastName\": \"Kiss\"}"

  object User:
    implicit val userCodec: Codec[User] = deriveCodec[User]
    // implicit val userEncoder: Encoder[User] = deriveEncoder[User]
    // implicit val userDecoder: Decoder[User] = deriveDecoder[User]

import SemiAutoDerivation.*

decode[User](s)
user.asJson.noSpaces

// Decoding sum types
object SumTypes:
  import io.circe.generic.auto.*
  import cats.implicits._          // for widen

  case class Person(name: String, age: Int)

  sealed trait Animal
  case class Dog(name: String, barkSound: String) extends Animal
  case class Cat(name: String, lives: Int) extends Animal
  case class Donkey(name: String, owner: Person) extends Animal

  object Animal:
    implicit val decoder: Decoder[Animal] = Decoder[Dog].widen.or(Decoder[Cat].widen).or(Decoder[Donkey].widen)

  val donkeyJson = Donkey("Eeyore", Person("Christopher Robin", 9)).asJson.spaces2

import SumTypes.*

decode[Animal](donkeyJson)

import cats.Functor
val some = Some(42)
val option = Option(42)
Functor[Option].widen(some)


// Optional values
object OptionalValues:
  import io.circe.generic.semiauto.*
  implicit val nullLessEncoder: Encoder[Author] = deriveEncoder[Author].mapJson(_.dropNullValues)
  implicit val deepNullLessEncoder: Encoder[Author] = deriveEncoder[Author].mapJson(_.deepDropNullValues)

bela.asJson
bela.asJson.dropNullValues
bela.asJson(OptionalValues.nullLessEncoder)
