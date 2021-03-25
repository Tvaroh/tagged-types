package taggedtypes.typeclass

import org.scalatest.flatspec.AnyFlatSpec
import TypeClass._
import org.scalatest.matchers.should.Matchers
import taggedtypes._

class TaggedTypeClassCompatSpec extends AnyFlatSpec with Matchers {
  object Username extends TaggedType[String]
  type Username = Username.Type

  object ANumber extends TaggedType[Int]
  type ANumber = ANumber.Type

  it should "lift any typeclass to a tagged type class" in {
    TypeClass.of[Username].read("bobby") shouldBe "bobby"
    TypeClass.of[ANumber].read("123") shouldBe 123
    TypeClass.of[AOtherNumber].read("123") shouldBe 113
  }
}

object AOtherNumber extends TaggedType[Int]

trait TypeClass[T] {
  def write(t: T): String

  def read(s: String): T
}

object TypeClass {
  type AOtherNumber = AOtherNumber.Type
  implicit val stringReadWriter: TypeClass[String] = new TypeClass[String] {
    override def write(t: String): String = t

    override def read(s: String): String = s
  }

  implicit val intReadWriter: TypeClass[Int] = new TypeClass[Int] {
    override def write(t: Int): String = t.toString

    override def read(s: String): Int = s.toInt
  }

  implicit val otherNumberReadWriter: TypeClass[AOtherNumber] = new TypeClass[AOtherNumber] {
    override def write(t: AOtherNumber): String = t.toString

    override def read(s: String): AOtherNumber = AOtherNumber(s.toInt - 10)
  }

  def of[T](implicit typeClass: TypeClass[T]): TypeClass[T] = typeClass
}
