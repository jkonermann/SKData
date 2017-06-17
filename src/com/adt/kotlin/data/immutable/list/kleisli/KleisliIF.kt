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
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.list.List


interface KleisliIF<A, B> {

    operator fun invoke(a: A): List<B>

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
    fun <C> forwardCompose(f: (B) -> List<C>): KleisliIF<A, C>

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
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    fun <C> compose(f: (C) -> List<A>): KleisliIF<C, B>

}
