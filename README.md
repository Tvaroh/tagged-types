# tagged-types

[![Build status](https://img.shields.io/travis/Tvaroh/tagged-types/master.svg)](https://travis-ci.org/Tvaroh/tagged-types)
[![Maven Central](https://img.shields.io/maven-central/v/io.treev/tagged-types_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/io.treev/tagged-types_2.13)

Zero-dependency boilerplate-free tagged types for Scala.

- [tagged-types](#tagged-types)
   - [Usage](#usage)
     - [`sbt`](#sbt)
     - [API](#api)
       - [Defining tagged types](#defining-tagged-types)
       - [Tagging values](#tagging-values)
       - [Tagging container values](#tagging-container-values)
       - [Tagging arbitrarily nested container values](#tagging-arbitrarily-nested-container-values)
       - [Un-tagging](#un-tagging)
       - [Adding more tags](#adding-more-tags)
       - [Auto tagging](#auto-tagging)
   - [Migrating from value classes](#migrating-from-value-classes)
   - [Integrating with libraries](#integrating-with-libraries)
     - [Circe](#circe)
     - [Slick](#slick)

## Usage

### `sbt`

Add the following to your `build.sbt` (replace `%%` with `%%%` for *Scala.js*):

```scala
libraryDependencies += "io.treev" %% "tagged-types" % "3.2"
```

Artifacts are published for *Scala* `2.12`/`2.13` and *Scala.js* `1.0-RC2`. Use `2.0` for *Scala* `2.11`/`2.12` and *Scala.js* `0.6`.

### API

#### Defining tagged types

```scala
import taggedtypes._

object Username extends TaggedType[String]
```

It's helpful to define a type alias for convenience, e.g. in package object:

```scala
object Username extends TaggedType[String]
type Username = Username.Type
```

`TaggedType` provides the following members:

* `apply` method to construct tagged type from raw values, e.g. `username("scooper")`;
* `Tag` trait to access the tag, e.g. `List("scooper").@@@[username.Tag]` (see below for container tagging);
* `Raw` type member to access raw type, e.g. to help with type inference where needed:

```scala
object Username extends TaggedType[String]
type Username = Username.Type

case class User(name: Username)

val users = List(User(Username("scooper")))
users.sortBy(_.name: Username.Raw)
```

* `Type` type member to access tagged type.

#### Tagging values

```scala
sealed trait UsernameTag

val sheldon = "scooper".@@[UsernameTag]
sheldon: String @@ UsernameTag
// or "scooper".taggedWith[UsernameTag]
```

Or, if you have `TaggedType` instance:

```scala
object Username extends TaggedType[String]

val sheldon = "scooper" @@ Username
sheldon: String @@ Username.Tag
sheldon: Username.Type
// or "scooper" taggedWith Username
// or Username("scooper")
```

#### Tagging container values

```scala
val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
val users = rawUsers.@@@[UsernameTag]
users: List[String @@ UsernameTag]
// or rawUsers.taggedWithF[UsernameTag]
```

Can also tag using `TaggedType` instance as above.

#### Tagging arbitrarily nested container values

```scala
import scala.util.Try
val arbitrarilyNested = Some(List(Try("scooper"), Try("lhofstadter"), Try("rkoothrappali")))
val taggedArbitrarilyNested = arbitrarilyNested.@@@@[UsernameTag]
taggedArbitrarilyNested: Option[List[Try[String @@ UsernameTag]]]
// or arbitrarilyNested.taggedWithG[UsernameTag]
```

Can also tag using `TaggedType` instance as above.

#### Un-tagging

Immediate value:

```scala
val rawSheldon: String = sheldon.-@ // or sheldon.unTagged
````

Container value:

```scala
val rawUsers: List[String] = users.-@@ // or users.unTaggedF
````

Arbitrarily nested container value:

```scala
val rawArbitrarilyNested: Option[List[Try[String @@ UsernameTag]]] = taggedArbitrarilyNested.-@@@@ // or taggedArbitrarilyNested.unTaggedG
````

#### Adding more tags

Immediate value:

```scala
sealed trait OwnerTag

val username = "scooper".@@[UsernameTag]
val owner = username.+@[OwnerTag]
owner: String @@ (UsernameTag with OwnerTag)
// or username.andTaggedWith[OwnerTag]
```

Container value:

```scala
val owners = users.+@@[OwnerTag]
owners: List[String @@ (UsernameTag with OwnerTag)]
// or users.andTaggedWithF[OwnerTag]
```

Arbitrarily nested container value:

```scala
val owners = taggedArbitrarilyNested.+@@@[OwnerTag]
owners: Option[List[Try[String @@ (UsernameTag with OwnerTag)]]]
// or taggedArbitrarilyNested.andTaggedWithG[OwnerTag]:
```

Can also tag using `TaggedType` instance as above.

#### Auto tagging

Sometimes it's convenient to automatically convert raw values into tagged ones, e.g. in REPL or when integrating with external APIs. To achieve this, an `import taggedtypes.auto._` is required:

```scala
import taggedtypes.auto._

val sheldon: Username = "scooper"
```

## Migrating from value classes

Suppose you have a value class:

```scala
case class Username(value: String) extends AnyVal {
  def isValid: Boolean = !value.isEmpty
}
object Username {
  val FieldName: String = "username"
}
```

Then, it's a matter of changing it to:

```scala
object Username extends TaggedType[String]
```

Any methods on original case class instance turn into implicit extensions:

```scala
object Username extends TaggedType[String] {
  implicit class UsernameExtensions(val value: Type) extends AnyVal {
    def isValid: Boolean = !value.isEmpty
  }
}
```

Any constants on original case class' companion object are merged into `Username` object:

```scala
object Username extends TaggedType[String] {
  val FieldName: String = "username"
}
```

## Integrating with libraries

### Circe

Helpers for defining Circe encoders/decoders.

```scala
import io.circe._
import taggedtypes._

def taggedDecoder[T: Decoder, U]: Decoder[T @@ U] =
  Decoder.instance(_.as[T].@@@[U])

def taggedTypeDecoder[T: Decoder](taggedType: TaggedType[T]): Decoder[taggedType.Type] =
  taggedDecoder[T, taggedType.Tag]

def taggedEncoder[T: Encoder, U]: Encoder[T @@ U] =
  Encoder[T].@@@[U]

def taggedTypeEncoder[T: Encoder](taggedType: TaggedType[T]): Encoder[taggedType.Type] =
  taggedEncoder[T, taggedType.Tag]
```

### Slick

Helpers for defining Slick column types.

```scala
import io.circe._
import scala.reflect.ClassTag
import slickProfile.api._

def taggedColumnType[T, U](implicit tColumnType: BaseColumnType[T],
                                    clsTag: ClassTag[T @@ U]): BaseColumnType[T @@ U] =
  MappedColumnType.base[T @@ U, T](identity, _.@@[U])

def taggedTypeColumnType[T](taggedType: TaggedType[T])
                           (implicit tColumnType: BaseColumnType[T],
                                     clsTag: ClassTag[taggedType.Type]): BaseColumnType[taggedType.Type] =
  taggedColumnType[T, taggedType.Tag]
```
