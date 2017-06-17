package com.adt.kotlin.data.immutable.list.kleisli

/**
 * A Kleisli arrow is the arrow A -> List[B] for all list monads.
 *
 * If you have functions that return kinds of things, like Lists then you can
 *   use a Kleisli to compose those functions.
 *
 * @param A                     the function domain type
 * @param B                     the function range type is List[B]
 *
 * @constructor                 create a Kleisli arraow for lists
 * @property f                  the function mapping a simple type to a list type
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.list.List


class Kleisli<A, B>(val f: (A) -> List<B>) : KleisliAB<A, B>() {

    override operator fun invoke(a: A): List<B> = f(a)

}
