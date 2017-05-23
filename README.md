# tagged-types

Zero-dependency tagged types for Scala.

## Usage

### `sbt`

Add the following to your `build.sbt`:

```scala
resolvers += Resolver.bintrayRepo("treevio", "maven")

libraryDependencies += "io.treev" %% "tagged-types" % "1.0"
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

val usernameString = "scooper"
val username = usernameString.@@[UsernameTag] // or .taggedWith[UsernameTag]
// username: String @@ UsernameTag
```

##### Tagging container values

```scala
val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
val users = rawUsers.@@@[UsernameTag] // or .taggedWithF[UsernameTag]
// users: List[String @@ UsernameTag]
```

##### Adding more tags

```scala
sealed trait OwnerTag

val owner = username.+@[OwnerTag] // or .andTaggedWith[OwnerTag]
// owner: String @@ (UsernameTag with OwnerTag)

val owners = users.+@@[OwnerTag] // or .andTaggedWithF[OwnerTag]
// owners: List[String @@ (UsernameTag with OwnerTag)]
```
