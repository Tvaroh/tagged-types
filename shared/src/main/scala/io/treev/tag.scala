package io.treev

import scala.annotation.implicitNotFound

/**
  * Adapted from
  * https://github.com/softwaremill/scala-common/blob/master/tagging/src/main/scala/com/softwaremill/tagging/package.scala
  * with added tagging operators, function-first-style tagging, and explicit container-types tagging.
  */
object tag {

  type Tag[+T, +U] = { type Raw <: T; type Tag <: U }
  type Tagged[+T, +U] = T with Tag[T, U]
  type @@[+T, +U] = Tagged[T, U]

  /** Base tagged type trait.
    * @tparam R raw value type */
  trait TaggedType[R] {
    /** Tagged value tag. */
    type Tag = this.type

    /** Raw value type. */
    type Raw = R
    /** Tagged value type. */
    type Type = Raw @@ Tag

    /** Create tagged value from raw value. */
    def apply(raw: Raw): Type = raw.@@[Tag]
  }

  /** Function-first-style tagging API.
    * @tparam U type to tag with
    * @return `Tagger` instance that can be used for tagging */
  def apply[T, U](t: T): T @@ U = cast(t)

  implicit class TaggingExtensions[T](val t: T) extends AnyVal {

    /** Tag with type `U`.
      * @tparam U type to tag with
      * @return value tagged with `U` */
    def taggedWith[U]: T @@ U = cast(t)
    /** Synonym operator for `taggedWith`. */
    def @@[U]: T @@ U = taggedWith[U]

    /** Tag using `TaggedType` instance.
      * Allows for `String @@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return value tagged with `taggedType.Tag` */
    def taggedWith(taggedType: TaggedType[_]): T @@ taggedType.Tag = taggedWith[taggedType.Tag]
    /** Synonym operator for `taggedWith`. */
    def @@(taggedType: TaggedType[_]): T @@ taggedType.Tag = taggedWith[taggedType.Tag]

  }
  implicit class AndTaggingExtensions[T, U](val t: T @@ U) extends AnyVal {

    /** Tag tagged value with type `V`.
      * @tparam V type to tag with
      * @return value tagged with both `U` and `V` */
    def andTaggedWith[V]: T @@ (U with V) = cast(t)
    /** Synonym operator for `andTaggedWith`. */
    def +@[V]: T @@ (U with V) = andTaggedWith[V]

    /** Tag tagged value using `TaggedType` instance.
      * Allows for `String +@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return value tagged with both `U` and `taggedType.Tag` */
    def andTaggedWith(taggedType: TaggedType[_]): T @@ (U with taggedType.Tag) = andTaggedWith[taggedType.Tag]
    /** Synonym operator for `andTaggedWith`. */
    def +@(taggedType: TaggedType[_]): T @@ (U with taggedType.Tag) = andTaggedWith[taggedType.Tag]

  }

  implicit class TaggingExtensionsF[F[_], T](val ft: F[T]) extends AnyVal {

    /** Tag intra-container value with type `U`.
      * @tparam U type to tag with
      * @return container with nested value tagged with `U` */
    def taggedWithF[U]: F[T @@ U] = cast(ft)
    /** Synonym operator for `taggedWithF`. */
    def @@@[U]: F[T @@ U] = taggedWithF[U]

    /** Tag intra-container value using `TaggedType` instance.
      * Allows for `List(...) @@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with nested value tagged with `taggedType.Tag` */
    def taggedWithF(taggedType: TaggedType[_]): F[T @@ taggedType.Tag] = taggedWithF[taggedType.Tag]
    /** Synonym operator for `taggedWith`. */
    def @@@(taggedType: TaggedType[_]): F[T @@ taggedType.Tag] = taggedWithF[taggedType.Tag]

  }
  implicit class AndTaggingExtensionsF[F[_], T, U](val ft: F[T @@ U]) extends AnyVal {

    /** Tag tagged intra-container value with type `U`.
      * @tparam V type to tag with
      * @return container with nested value tagged with both `U` and `V` */
    def andTaggedWithF[V]: F[T @@ (U with V)] = cast(ft)
    /** Synonym operator for `andTaggedWithF`. */
    def +@@[V]: F[T @@ (U with V)] = andTaggedWithF[V]

    /** Tag tagged intra-container value using `TaggedType` instance.
      * Allows for `List(...) +@@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with nested value tagged with both `U` and `taggedType.Tag` */
    def andTaggedWithF(taggedType: TaggedType[_]): F[T @@ (U with taggedType.Tag)] = andTaggedWithF[taggedType.Tag]
    /** Synonym operator for `andTaggedWithF`. */
    def +@@(taggedType: TaggedType[_]): F[T @@ (U with taggedType.Tag)] = andTaggedWithF[taggedType.Tag]

  }

  implicit class TaggingExtensionsG[G](val g: G) extends AnyVal {
    import TaggingExtensionsG._

    /** Tag arbitrarily nested container value with type `U`.
      * @tparam U type to tag with
      * @return container with arbitrarily nested value tagged with `U` */
    def taggedWithG[U](implicit unwrap: Unwrap[G]): unwrap.Result[U] = cast(g)
    /** Synonym operator for `taggedWithG`. */
    def @@@@[U](implicit unwrap: Unwrap[G]): unwrap.Result[U] = cast(g)

    /** Tag arbitrarily nested container value using `TaggedType` instance.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with arbitrarily nested value tagged with `taggedType.Tag` */
    def taggedWithG(taggedType: TaggedType[_])(implicit unwrap: Unwrap[G]): unwrap.Result[taggedType.Tag] = cast(g)
    /** Synonym operator for `taggedWithG`. */
    def @@@@(taggedType: TaggedType[_])(implicit unwrap: Unwrap[G]): unwrap.Result[taggedType.Tag] = cast(g)
  }
  object TaggingExtensionsG {
    @implicitNotFound("Cannot prove that ${F} is a container stack, like Option[List[Int]]")
    trait Unwrap[F] {
      type FF[_]
      type Raw
      type Result[U] = FF[Raw @@ U]
    }
    trait LowPriorityUnwrap {
      implicit def bottom[F[_], T]: Unwrap[F[T]] {
        type FF[S] = F[S]
        type Raw = T
      } = new Unwrap[F[T]] {
        type FF[S] = F[S]
        type Raw = T
      }
    }
    object Unwrap extends LowPriorityUnwrap {
      implicit def nested[F[_], G](implicit unwrap: Unwrap[G]): Unwrap[F[G]] {
        type FF[S] = F[unwrap.FF[S]]
        type Raw = unwrap.Raw
      } = new Unwrap[F[G]] {
        type FF[S] = F[unwrap.FF[S]]
        type Raw = unwrap.Raw
      }
    }
  }

  implicit class AndTaggingExtensionsG[G, T](val g: G) extends AnyVal {
    import AndTaggingExtensionsG._

    /** Tag tagged arbitrarily nested container value with type `V`.
      * @tparam V type to tag with
      * @return container with arbitrarily nested value tagged with both `U` and `V` */
    def andTaggedWithG[V](implicit unwrap: Unwrap[G, T]): unwrap.Result[V] = cast(g)
    /** Synonym operator for `andTaggedWithG`. */
    def +@@@[V](implicit unwrap: Unwrap[G, T]): unwrap.Result[V] = cast(g)

    /** Tag tagged arbitrarily nested container value using `TaggedType` instance.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with arbitrarily nested value tagged with both `U` and `taggedType.Tag` */
    def andTaggedWithG(taggedType: TaggedType[_])(implicit unwrap: Unwrap[G, T]): unwrap.Result[taggedType.Tag] = cast(g)
    /** Synonym operator for `andTaggedWithG`. */
    def +@@@(taggedType: TaggedType[_])(implicit unwrap: Unwrap[G, T]): unwrap.Result[taggedType.Tag] = cast(g)
  }
  object AndTaggingExtensionsG {
    @implicitNotFound("Cannot prove that ${F} is a container stack holding a tagged value, like Option[List[Int @@ Tag]]")
    trait Unwrap[F, T] {
      type FF[_]
      type Raw
      type Tag
      type Result[V] = FF[Raw @@ (Tag with V)]
    }
    trait LowPriorityUnwrap {
      implicit def bottom[F[_], T, T1, U](implicit ev: T <:< (T1 @@ U)): Unwrap[F[T], T] {
        type FF[S] = F[S]
        type Raw = T1
        type Tag = U
      } = new Unwrap[F[T], T] {
        type FF[S] = F[S]
        type Raw = T1
        type Tag = U
      }
    }
    object Unwrap extends LowPriorityUnwrap {
      implicit def nested[F[_], G, T](implicit unwrap: Unwrap[G, T]): Unwrap[F[G], T] {
        type FF[S] = F[unwrap.FF[S]]
        type Raw = unwrap.Raw
        type Tag = unwrap.Tag
      } = new Unwrap[F[G], T] {
        type FF[S] = F[unwrap.FF[S]]
        type Raw = unwrap.Raw
        type Tag = unwrap.Tag
      }
    }
  }

  private def cast[T, V](v: T): V = v.asInstanceOf[V]

}
