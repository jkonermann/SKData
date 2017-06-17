package com.adt.kotlin.data.immutable.option.kleisli

/**
 * A factory object that creates an option Kleisli instance.
 *
 * A Kleisli arrow is the arrow A -> Option[B] for all option monads.
 *
 * If you have functions that return kinds of things, like Options, then you can
 *   use a Kleisli to compose those functions.
 *
 * @param A                     the function domain type
 * @param B                     the function range type is Option[B]
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.option.*


object KleisliF {

    /**
     * Factory function to create a Kleisli from a function.
     *
     * @param f                     function
     * @return                      Kleisli arrow for given function
     */
    fun <A, B> kleisli(f: (A) -> Option<B>): KleisliIF<A, B> =
            object: KleisliAB<A, B>() {
                override fun invoke(a: A): Option<B> = f(a)
            }

}
