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
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.option.*



interface KleisliIF<A, B> {

    /**
     * Execute the Kleisli arrow against the given value.
     *
     * @param a                 parameter value for the arrow
     * @return                  computed option
     */
    operator fun invoke(a: A): Option<B>

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    fun <C> forwardCompose(k: KleisliIF<B, C>): KleisliIF<A, C>

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param f                 subsequent arrow
     * @return                  new arrow
     */
    fun <C> forwardCompose(f: (B) -> Option<C>): KleisliIF<A, C>

    /**
     * Composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    fun <C> compose(k: KleisliIF<C, A>): KleisliIF<C, B>

    /**
     * Composition of Kleisli arrow.
     *
     * @param f                 subsequent arrow
     * @return                  new arrow
     */
    fun <C> compose(f: (C) -> Option<A>): KleisliIF<C, B>

}
