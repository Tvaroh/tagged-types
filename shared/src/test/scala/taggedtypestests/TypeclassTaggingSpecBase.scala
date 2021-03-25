package taggedtypestests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import taggedtypes._

abstract class TypeclassTaggingSpecBase extends AnyFlatSpec with Matchers {
  import TypeclassTaggingSpecBase._

  it should "lift any typeclass to a tagged type typeclass" in {
    assertCompiles {
      "import taggedtypes.typeclass._; Parser[Username].parse(\"scooper\")".stripMargin
    }
  }

  it should "not lift typeclass to a tagged type typeclass without import" in {
    assertDoesNotCompile("Parser[Username].parse(\"scooper\")")
  }

}

object AnotherNumber extends TaggedType[Int]

private object TypeclassTaggingSpecBase {

  object Username extends TaggedType[String]
  type Username = Username.Type

  trait Parser[T] {

    def parse(s: String): T

  }

  object Parser {

    implicit val stringParser: Parser[String] = new Parser[String] {
      override def parse(s: String): String = s
    }

    def apply[T](implicit typeClass: Parser[T]): Parser[T] = typeClass

  }

}
