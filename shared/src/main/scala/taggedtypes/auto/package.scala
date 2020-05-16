package taggedtypes

package object auto {

  implicit def toTaggedType[Raw, TT <: TaggedType[Raw]](raw: Raw): TT =
    raw.asInstanceOf[TT]

  implicit def toTaggedTypeF[F[_], Raw, TT <: TaggedType[Raw]](raw: F[Raw]): F[TT] =
    raw.asInstanceOf[F[TT]]

}
