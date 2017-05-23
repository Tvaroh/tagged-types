# tagged-types

[ ![Download](https://api.bintray.com/packages/treevio/maven/tagged-types/images/download.svg) ](https://bintray.com/treevio/maven/tagged-types/_latestVersion)

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

libraryDependencies += "io.treev" %% "tagged-types" % "1.1"
```

Artifacts are published both for Scala `2.11` and `2.12`.

### API

#### Defining tagged types

```scala
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

    ```scala
    case class User(name: Username)
    val users = List(User(Username("scooper")))
    users.sortBy(_.name: Username.Raw)
    ```

* `Type` type member to access tagged type.

#### Tagging

##### Tagging values

```scala
import io.treev.tag._

sealed trait UsernameTag

val username = "scooper".@@[UsernameTag]
// or val username = "scooper".taggedWith[UsernameTag]
// username: String @@ UsernameTag
```

Or, if you have `TaggedType` instance:

```scala
import io.treev.tag._

object Username extends TaggedType[String]

val username = "scooper" @@ Username 
// or val username = "scooper" taggedWith Username
// or val username = Username("scooper")
// username: String @@ Username.Tag
```

##### Tagging container values

```scala
val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
val users = rawUsers.@@@[UsernameTag]
// or val users = rawUsers.taggedWithF[UsernameTag]
// users: List[String @@ UsernameTag]
```

##### Adding more tags

```scala
sealed trait OwnerTag

val owner = username.+@[OwnerTag]
// or val owner = username.andTaggedWith[OwnerTag]
// owner: String @@ (UsernameTag with OwnerTag)

val owners = users.+@@[OwnerTag]
// or val owners = users.andTaggedWithF[OwnerTag]
// owners: List[String @@ (UsernameTag with OwnerTag)]
```

## Migrating from value classes

Suppose you have a value class:

```scala
case class Username(value: String) extends AnyVal {
  def isValid: Boolean = ???
}
object Username {
  val FieldName: String = ???
  
  implicit val ordering: Ordering[Username] = ???
}
```

Then, it's a matter of changing it to:

```
import io.treev.tag._

object Username extends TaggedType[String]
```

Any methods on original case class instance turn into implicit extensions:

```scala
object Username extends TaggedType[String] {
  implicit class UsernameExtensions(val value: Type) 
    extends AnyVal { // still good application of value classes
  
    def isValid: Boolean = ???
  }
}
```

Any constants on original case class' companion object are merged into `Username` object:

```scala
object Username extends TaggedType[String] {
  val FieldName: String = ???
  
  implicit val ordering: Ordering[Username] = ???
}
```

### Note about implicit resolution

Implicit resolution won't work as previously, so, to bring implicit `Ordering` instance from above into scope, need to import it explicitly:

```scala
import Username._
// or import Username.ordering
```
