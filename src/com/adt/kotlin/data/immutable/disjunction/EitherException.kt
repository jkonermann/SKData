package com.adt.kotlin.data.immutable.disjunction

/**
 * Either[A, B] = Left of A
 *              | Right of B
 *
 * This Either type is inspired by the Haskell Either data type. The Either type represents
 *   values with two possibilities: a value of type Either[A, B] is either Left[A] or Right[B].
 *
 * The Either type is sometimes used to represent a value which is either correct or an error;
 *   by convention, the Left constructor is used to hold an error value and the Right constructor
 *   is used to hold a correct value (mnemonic: "right" also means "correct").
 *
 * This Either type is right-biased, so functions such as map and bind apply only to the Right
 *   case. This right-bias makes this Either more convenient to use in a monadic context than
 *   the either/Either type avoiding the need for a right projection.
 *
 * @param A                     the type of Left elements
 * @param B                     the type of Right elements
 *
 * @author	                    Ken Barclay
 * @since	                    January 2015
 */



class EitherException(message: String) : Exception(message)
