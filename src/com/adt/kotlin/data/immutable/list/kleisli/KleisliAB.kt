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


abstract class KleisliAB<A, B> : KleisliIF<A, B> {

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    override fun <C> forwardCompose(k: KleisliIF<B, C>): KleisliIF<A, C> =
            KleisliF.kleisli({a: A -> this.invoke(a).bind{b: B -> k.invoke(b) } })

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param f                 subsequent arrow
     * @return                  new arrow
     */
    override fun <C> forwardCompose(f: (B) -> List<C>): KleisliIF<A, C> =
            this.forwardCompose(KleisliF.kleisli(f))

    /**
     * Composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    override fun <C> compose(k: KleisliIF<C, A>): KleisliIF<C, B> =
            k.forwardCompose(this)

    /**
     * Composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    override fun <C> compose(f: (C) -> List<A>): KleisliIF<C, B> =
            KleisliF.kleisli(f).forwardCompose(this)

}
