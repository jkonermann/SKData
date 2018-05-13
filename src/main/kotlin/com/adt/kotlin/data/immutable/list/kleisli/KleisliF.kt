package com.adt.kotlin.data.immutable.list.kleisli

/**
 * A factory object that creates a list Kleisli instance.
 *
 * A Kleisli arrow is the arrow A -> List[B] for all list monads.
 *
 * If you have functions that return kinds of things, like Lists then you can
 *   use a Kleisli to compose those functions.
 *
 * @param A                     the function domain type
 * @param B                     the function range type is List[B]
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.list.List


object KleisliF {

    fun <A, B> kleisli(f: (A) -> List<B>): KleisliIF<A, B> =
            object: KleisliAB<A, B>() {
                override fun invoke(a: A): List<B> = f(a)
            }

}
