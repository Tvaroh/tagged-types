# tagged-types

[![Build status](https://img.shields.io/travis/Treev-io/tagged-types/master.svg)](https://travis-ci.org/Treev-io/tagged-types)
[![Download](https://api.bintray.com/packages/treevio/maven/tagged-types/images/download.svg)](https://bintray.com/treevio/maven/tagged-types/_latestVersion)

Zero-dependency boilerplate-free tagged types for Scala.

- [tagged-types](#tagged-types)
   - [Usage](#usage)
     - [`sbt`](#sbt)
     - [API](#api)
       - [Defining tagged types](#defining-tagged-types)
       - [Tagging](#tagging)
         - [Tagging values](#tagging-values)
         - [Tagging container values](#tagging-container-values)
         - [Adding more tags](#adding-more-tags)
   - [Migrating from value classes](#migrating-from-value-classes)
     - [Note about implicit resolution](#note-about-implicit-resolution)

## Usage

### `sbt`

Add the following to your `build.sbt`:

```scala
resolvers += Resolver.bintrayRepo("treevio", "maven")

libraryDependencies += "io.treev" %% "tagged-types" % "1.2"
```

Artifacts are published both for Scala `2.11` and `2.12`.

### API

#### Defining tagged types

```tut:silent
import io.treev.tag._

object Username extends TaggedType[String]
```

For convenience define a type alias in package object (Scala `2.12` only, with `2.11` type synonym name and object name must differ):

```scala
package object model {
  type Username = Username.Type
}
```

`TaggedType` provides the following members:

* `apply` method to construct tagged type from raw values, e.g. `Username("scooper")`;
* `Tag` trait to access the tag, e.g. `List("scooper").@@@[Username.Tag]` (see below for container tagging);
* `Raw` type member to access raw type, e.g. to help with type inference where needed:

```tut:silent
object Username extends TaggedType[String]
type Username = Username.Type

case class User(name: Username)

val users = List(User(Username("scooper")))
users.sortBy(_.name: Username.Raw)
```

* `Type` type member to access tagged type.

#### Tagging

##### Tagging values

```tut:silent
sealed trait UsernameTag

val username = "scooper".@@[UsernameTag]
// or val username = "scooper".taggedWith[UsernameTag]
// username: String @@ UsernameTag
```

Or, if you have `TaggedType` instance:

```tut:silent
object Username extends TaggedType[String]

val username = "scooper" @@ Username 
// or val username = "scooper" taggedWith Username
// or val username = Username("scooper")
// username: String @@ Username.Tag
```

##### Tagging container values

```tut:silent
val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
val users = rawUsers.@@@[UsernameTag]
// or val users = rawUsers.taggedWithF[UsernameTag]
// users: List[String @@ UsernameTag]
```

Can also tag using `TaggedType` instance as above.

##### Adding more tags

```tut:silent
sealed trait OwnerTag

val username = "scooper".@@[UsernameTag]
val owner: String @@ (UsernameTag with OwnerTag) = username.+@[OwnerTag]
// or val owner: String @@ (UsernameTag with OwnerTag) = username.andTaggedWith[OwnerTag]
// owner: String @@ (UsernameTag with OwnerTag)
```

Need explicit type specification above due to *"cyclic aliasing or subtyping involving type @@"* compiler error otherwise.

```tut:silent
val owners = users.+@@[OwnerTag]
// or val owners = users.andTaggedWithF[OwnerTag]
// owners: List[String @@ (UsernameTag with OwnerTag)]
```

Can also tag using `TaggedType` instance as above.

## Migrating from value classes

Suppose you have a value class:

```tut:silent
case class Username(value: String) extends AnyVal {
  def isValid: Boolean = ???
}
object Username {
  val FieldName: String = ???
  
  implicit val ordering: Ordering[Username] = ???
}
```

Then, it's a matter of changing it to:

```tut:silent
object Username extends TaggedType[String]
```

Any methods on original case class instance turn into implicit extensions:

```tut:silent
object Username extends TaggedType[String] {
  implicit class UsernameExtensions(val value: Type) 
    extends AnyVal { // still good application of value classes
  
    def isValid: Boolean = ???
  }
}
```

Any constants on original case class' companion object are merged into `Username` object:

```tut:silent
object Username extends TaggedType[String] {
  val FieldName: String = ???
  
  implicit val ordering: Ordering[Username] = ???
}
```

### Note about implicit resolution

Implicit resolution won't work as previously, so, to bring implicit `Ordering` instance from above into scope, need to import it explicitly:

```tut:silent
import Username._
// or import Username.ordering
```
