package taggedtypestests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import taggedtypes._

abstract class TagSpecBase extends AnyFlatSpec with Matchers {

  sealed trait UsernameTag
  sealed trait OwnerTag
  sealed trait AdminTag

  object Username extends TaggedType[String]
  type Username = Username.Type

  object Owner extends TaggedType[String]
  type Owner = Owner.Type

  object Admin extends TaggedType[String]
  type Admin = Admin.Type

  // value tagging

  it should "support value tagging" in {
    val scooper = "scooper".@@[UsernameTag]
    assertCompiles("scooper: String")
    assertCompiles("scooper: String @@ UsernameTag")
    assertDoesNotCompile("scooper: String @@ OwnerTag")
  }

  it should "support value un-tagging" in {
    val scooper = "scooper".@@[UsernameTag]
    assertCompiles("scooper.unTagged: String")
    assertCompiles("(scooper.-@): String")
    assertDoesNotCompile("scooper.unTagged: String @@ UsernameTag")
  }

  it should "support value multi-tagging" in {
    val scooper = "scooper".@@[UsernameTag]
    val owner = scooper.+@[OwnerTag]
    assertCompiles("owner: String")
    assertCompiles("owner: String @@ UsernameTag")
    assertCompiles("owner: String @@ OwnerTag")
    assertCompiles("owner: String @@ (UsernameTag with OwnerTag)")
    assertDoesNotCompile("owner: String @@ AdminTag")

    val admin = owner.+@[AdminTag]
    assertCompiles("admin: String")
    assertCompiles("admin: String @@ UsernameTag")
    assertCompiles("admin: String @@ OwnerTag")
    assertCompiles("admin: String @@ AdminTag")
    assertCompiles("admin: String @@ (UsernameTag with OwnerTag)")
    assertCompiles("admin: String @@ (UsernameTag with AdminTag)")
    assertCompiles("admin: String @@ (OwnerTag with AdminTag)")
    assertCompiles("admin: String @@ (UsernameTag with OwnerTag with AdminTag)")
  }

  it should "support value tagging with TaggedType instance" in {
    val scooper = "scooper" @@ Username
    assertCompiles("scooper: String")
    assertCompiles("scooper: Username.Type")
    assertCompiles("scooper: String @@ Username.Tag")
    assertDoesNotCompile("scooper: String @@ OwnerTag")
  }

  it should "support value multi-tagging with TaggedType instance" in {
    val scooper = "scooper" @@ Username
    val owner = scooper +@ Owner
    assertCompiles("owner: String")
    assertCompiles("owner: String @@ Username.Tag")
    assertCompiles("owner: String @@ Owner.Tag")
    assertCompiles("owner: String @@ (Username.Tag with Owner.Tag)")
    assertDoesNotCompile("owner: String @@ Admin.Tag")

    val admin = owner +@ Admin
    assertCompiles("admin: String")
    assertCompiles("admin: String @@ Username.Tag")
    assertCompiles("admin: String @@ Owner.Tag")
    assertCompiles("admin: String @@ Admin.Tag")
    assertCompiles("admin: String @@ (Username.Tag with Owner.Tag)")
    assertCompiles("admin: String @@ (Username.Tag with Admin.Tag)")
    assertCompiles("admin: String @@ (Owner.Tag with Admin.Tag)")
    assertCompiles("admin: String @@ (Username.Tag with Owner.Tag with Admin.Tag)")
  }

  // intra-container value tagging

  it should "support intra-container value tagging" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers.@@@[UsernameTag]
    assertCompiles("users: List[String @@ UsernameTag]")
    assertDoesNotCompile("users: List[String @@ OwnerTag]")
  }

  it should "support intra-container value un-tagging" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers.@@@[UsernameTag]
    assertCompiles("users.unTaggedF: List[String]")
    assertCompiles("(users.-@@): List[String]")
    assertDoesNotCompile("users.unTaggedF: List[String @@ OwnerTag]")
  }

  it should "support intra-container value multi-tagging" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers.@@@[UsernameTag]
    val owners = users.+@@[OwnerTag]
    assertCompiles("owners: List[String]")
    assertCompiles("owners: List[String @@ UsernameTag]")
    assertCompiles("owners: List[String @@ OwnerTag]")
    assertCompiles("owners: List[String @@ (UsernameTag with OwnerTag)]")
    assertDoesNotCompile("owners: String @@ AdminTag")
    assertDoesNotCompile("owners: List[Int]")

    val admins = owners.+@@[AdminTag]
    assertCompiles("admins: List[String]")
    assertCompiles("admins: List[String @@ UsernameTag]")
    assertCompiles("admins: List[String @@ OwnerTag]")
    assertCompiles("admins: List[String @@ AdminTag]")
    assertCompiles("admins: List[String @@ (UsernameTag with OwnerTag)]")
    assertCompiles("admins: List[String @@ (UsernameTag with AdminTag)]")
    assertCompiles("admins: List[String @@ (OwnerTag with AdminTag)]")
    assertCompiles("admins: List[String @@ (UsernameTag with OwnerTag with AdminTag)]")
  }

  it should "support intra-container value tagging with TaggedType instance" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers @@@ Username
    assertCompiles("users: List[Username.Type]")
    assertCompiles("users: List[String @@ Username.Tag]")
    assertDoesNotCompile("users: List[String @@ OwnerTag]")
  }

  it should "support intra-container value multi-tagging with TaggedType instance" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers @@@ Username
    val owners = users +@@ Owner
    assertCompiles("owners: List[String]")
    assertCompiles("owners: List[String @@ Username.Tag]")
    assertCompiles("owners: List[String @@ Owner.Tag]")
    assertCompiles("owners: List[String @@ (Username.Tag with Owner.Tag)]")
    assertDoesNotCompile("owners: List[String @@ Admin.Tag]")
    assertDoesNotCompile("owners: List[Int]")

    val admins = owners +@@ Admin
    assertCompiles("admins: List[String]")
    assertCompiles("admins: List[String @@ Username.Tag]")
    assertCompiles("admins: List[String @@ Owner.Tag]")
    assertCompiles("admins: List[String @@ Admin.Tag]")
    assertCompiles("admins: List[String @@ (Username.Tag with Owner.Tag)]")
    assertCompiles("admins: List[String @@ (Username.Tag with Admin.Tag)]")
    assertCompiles("admins: List[String @@ (Owner.Tag with Admin.Tag)]")
    assertCompiles("admins: List[String @@ (Username.Tag with Owner.Tag with Admin.Tag)]")
  }

  // arbitrarily nested intra-container value tagging

  it should "support arbitrarily nested intra-container value tagging" in {
    val rawUsers = List(Some(List("scooper", "lhofstadter", "rkoothrappali")))
    val users = rawUsers.@@@@[UsernameTag]
    assertCompiles("users: List[Option[List[String @@ UsernameTag]]]")
    assertDoesNotCompile("users: List[Option[List[String @@ OwnerTag]]]")
  }

  it should "support arbitrarily nested intra-container value un-tagging" in {
    val rawUsers = List(Some(List("scooper", "lhofstadter", "rkoothrappali")))
    val users = rawUsers.@@@@[UsernameTag]
    val owners = users.+@@@[OwnerTag]
    assertCompiles("owners.unTaggedG: List[Option[List[String]]]")
    assertCompiles("(owners.-@@@@): List[Option[List[String]]]")
    assertDoesNotCompile("owners.unTaggedG: List[Option[List[String @@ OwnerTag]]]")
  }

  it should "support arbitrarily nested intra-container value multi-tagging" in {
    val rawUsers = List(Some(List("scooper", "lhofstadter", "rkoothrappali")))
    val users = rawUsers.@@@@[UsernameTag]
    val owners = users.+@@@[OwnerTag]
    assertCompiles("owners: List[Option[List[String]]]")
    assertCompiles("owners: List[Option[List[String @@ UsernameTag]]]")
    assertCompiles("owners: List[Option[List[String @@ OwnerTag]]]")
    assertCompiles("owners: List[Option[List[String @@ (UsernameTag with OwnerTag)]]]")
    assertDoesNotCompile("owners: List[Option[String @@ AdminTag]]")
    assertDoesNotCompile("owners: List[Int]")

    val admins = owners.+@@@[AdminTag]
    assertCompiles("admins: List[Option[List[String]]]")
    assertCompiles("admins: List[Option[List[String @@ UsernameTag]]]")
    assertCompiles("admins: List[Option[List[String @@ OwnerTag]]]")
    assertCompiles("admins: List[Option[List[String @@ AdminTag]]]")
    assertCompiles("admins: List[Option[List[String @@ (UsernameTag with OwnerTag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (UsernameTag with AdminTag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (OwnerTag with AdminTag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (UsernameTag with OwnerTag with AdminTag)]]]")
  }

  it should "support arbitrarily nested intra-container value tagging with TaggedType instance" in {
    val rawUsers = List(Some(List("scooper", "lhofstadter", "rkoothrappali")))
    val users = rawUsers @@@@ Username
    assertCompiles("users: List[Option[List[Username.Type]]]")
    assertCompiles("users: List[Option[List[String @@ Username.Tag]]]")
    assertDoesNotCompile("users: List[Option[List[String @@ OwnerTag]]]")
  }

  it should "support arbitrarily nested intra-container value multi-tagging with TaggedType instance" in {
    val rawUsers = List(Some(List("scooper", "lhofstadter", "rkoothrappali")))
    val users = rawUsers @@@@ Username
    val owners = users +@@@ Owner
    assertCompiles("owners: List[Option[List[String]]]")
    assertCompiles("owners: List[Option[List[String @@ Username.Tag]]]")
    assertCompiles("owners: List[Option[List[String @@ Owner.Tag]]]")
    assertCompiles("owners: List[Option[List[String @@ (Username.Tag with Owner.Tag)]]]")
    assertDoesNotCompile("owners: List[Option[String @@ Admin.Tag]]")
    assertDoesNotCompile("owners: List[Int]")

    val admins = owners +@@@ Admin
    assertCompiles("admins: List[Option[List[String]]]")
    assertCompiles("admins: List[Option[List[String @@ Username.Tag]]]")
    assertCompiles("admins: List[Option[List[String @@ Owner.Tag]]]")
    assertCompiles("admins: List[Option[List[String @@ Admin.Tag]]]")
    assertCompiles("admins: List[Option[List[String @@ (Username.Tag with Owner.Tag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (Username.Tag with Admin.Tag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (Owner.Tag with Admin.Tag)]]]")
    assertCompiles("admins: List[Option[List[String @@ (Username.Tag with Owner.Tag with Admin.Tag)]]]")
  }

  // auto-tagging

  it should "support auto-tagging" in {
    assertDoesNotCompile("val sheldon: Username = \"\"")
    assertCompiles("import taggedtypes.auto._; val sheldon: Username = \"\"")
  }

  // typeclass auto-tagging

  it should "support typeclass auto-tagging" in {
    trait Typeclass[T]

    object Typeclass {
      implicit val stringInstance: Typeclass[String] = new Typeclass[String] {}

      def apply[T](implicit typeclass: Typeclass[T]): Typeclass[T] = typeclass
    }

    assertDoesNotCompile("Typeclass[Username]")
    assertCompiles("import taggedtypes.auto.typeclass._; Typeclass[Username]")
  }

  // aux

  it should "pick ordering automatically" in {
    val rawUsers = List("scooper", "lhofstadter", "rkoothrappali")
    val users = rawUsers @@@ Username
    users.sorted should be (rawUsers.sorted)
  }

  it should "pick extension methods automatically" in {
    assertCompiles {
      """
        |object Username extends TaggedType[String] {
        |  implicit class UsernameExtensions(val username: Type) {
        |    def reverse: Type = apply(username.reverse)
        |  }
        |}
        |Username("scooper").reverse
      """.stripMargin
    }
  }

}
