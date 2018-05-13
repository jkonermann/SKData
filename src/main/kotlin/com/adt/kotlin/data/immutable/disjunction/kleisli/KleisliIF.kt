package com.adt.kotlin.data.immutable.disjunction.kleisli

/**
 * Either[A, B] = Left of A
 *              | Right of B
 *
 * The Either type represents values with two possibilities: a value of type
 *   Either[A, B] is either Left[A] or Right[B].
 *
 * The Either type is sometimes used to represent a value which is either correct or an error;
 *   by convention, the Left constructor is used to hold an error value and the Right constructor
 *   is used to hold a correct value (mnemonic: "right" also means "correct").
 *
 * This Either type is right-biased, so functions such as map and bind apply only to the Right
 *   case. This right-bias makes this Either more convenient to use in a monadic context than
 *   the either/Either type avoiding the need for a right projection.
 *
 * A Kleisli arrow is the arrow (C, A) -> Either[C, B] for all either monads. If you have
 *   functions that return kinds of things, like Eithers, then you can
 *   use a Kleisli to compose those functions.
 *
 * @param A                     the type of the function parameter
 * @param B                     the type of Right elements
 * @param C                     the type of Left elements
 *
 * @author	                    Ken Barclay
 * @since	                    January 2015
 */

import com.adt.kotlin.data.immutable.disjunction.*



interface KleisliIF<A, B, C> {

    /**
     * Execute the Kleisli arrow against the given value.
     *
     * @param c                 parameter value for the arrow
     * @param a                 parameter value for the arrow
     * @return                  computed either
     */
    operator fun invoke(a: A): Either<C, B>

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    fun <D> forwardCompose(k: KleisliIF<B, D, C>): KleisliIF<A, D, C>

    /**
     * Forward composition of Kleisli arrow.
     *
     * @param f                 subsequent arrow
     * @return                  new arrow
     */
    fun <D> forwardCompose(f: (B) -> Either<C, D>): KleisliIF<A, D, C>

    /**
     * Composition of Kleisli arrow.
     *
     * @param k                 subsequent arrow
     * @return                  new arrow
     */
    fun <D> compose(k: KleisliIF<D, A, C>): KleisliIF<D, B, C>

    /**
     * Composition of Kleisli arrow.
     *
     * @param f                 subsequent arrow
     * @return                  new arrow
     */
    fun <D> compose(f: (D) -> Either<C, A>): KleisliIF<D, B, C>

}
