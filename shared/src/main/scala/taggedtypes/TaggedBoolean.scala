package taggedtypes

trait TaggedBoolean extends TaggedType[Boolean] {

  val True: Type = apply(true)
  val False: Type = apply(false)

}
