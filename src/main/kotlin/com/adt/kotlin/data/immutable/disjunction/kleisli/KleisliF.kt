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
 * @author	                    Ken Barclay
 * @since	                    January 2015
 */

import com.adt.kotlin.data.immutable.disjunction.*



object KleisliF {

    /**
     * Factory function to create a Kleisli from a function.
     *
     * @param f                     function
     * @return                      Kleisli arrow for given function
     */
    fun <A, B, C> kleisli(f: (A) -> Either<C, B>): KleisliIF<A, B, C> =
            object: KleisliAB<A, B, C>() {
                override fun invoke(a: A): Either<C, B> = f(a)
            }

}
