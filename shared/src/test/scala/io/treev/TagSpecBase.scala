package io.treev

import io.treev.tag._
import org.scalatest.FlatSpec

abstract class TagSpecBase extends FlatSpec {

  sealed trait UsernameTag
  sealed trait OwnerTag
  sealed trait AdminTag

  object Username extends TaggedType[String]
  object Owner extends TaggedType[String]
  object Admin extends TaggedType[String]

  // value tagging

  it should "support value tagging" in {
    val scooper = "scooper".@@[UsernameTag]
    assertCompiles("scooper: String")
    assertCompiles("scooper: String @@ UsernameTag")
    assertDoesNotCompile("scooper: String @@ OwnerTag")
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
    assertDoesNotCompile("useres: List[String @@ OwnerTag]")
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
    assertDoesNotCompile("useres: List[Option[List[String @@ OwnerTag]]]")
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

}
