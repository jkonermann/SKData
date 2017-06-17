package com.adt.kotlin.data.immutable.option.kleisli

/**
 * A Kleisli arrow is the arrow A -> Option[B] for all option monads.
 *
 * If you have functions that return kinds of things, like Options, then you can
 *   use a Kleisli to compose those functions.
 *
 * @param A                     the function domain type
 * @param B                     the function range type is Option[B]
 *
 * @constructor                 create an initialized Kleisli for options
 * @property f                  the function mapping a simple type to an option type
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.option.*



internal class Kleisli<A, B>(val f: (A) -> Option<B>) : KleisliAB<A, B>() {

    /**
     * Execute the Kleisli arrow against the given value.
     *
     * @param a                 parameter value for the arrow
     * @return                  computed option
     */
    override operator fun invoke(a: A): Option<B> = f(a)

}
