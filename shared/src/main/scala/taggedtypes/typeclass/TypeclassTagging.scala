package taggedtypes.typeclass

import taggedtypes.@@

trait TypeclassTagging {

  implicit def liftAnyTypeclass[Typeclass[_], T, Tag](implicit tc: Typeclass[T]): Typeclass[T @@ Tag] =
    tc.asInstanceOf[Typeclass[T @@ Tag]]

}
