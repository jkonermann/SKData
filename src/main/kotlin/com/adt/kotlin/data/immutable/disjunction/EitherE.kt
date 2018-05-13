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



// Contravariant extension functions:

/**
 * Return the Right value of this disjunction or the default value if it is a Left.
 *
 * Examples:
 *   Left("Ken").getOrElse(99) = 99
 *   Right(5).getOrElse(99) = 5
 *
 * @param defaultValue      the return value if this is a Left
 * @return                  the default value if this is a Left; otherwise the Right value
 */
fun <A, B> Either<A, B>.getOrElse(defaultValue: B): B = when(this) {
    is Either.Left -> defaultValue
    is Either.Right -> this.value
}

/**
 * Bind through the Right of this disjunction.
 *
 * Examples:
 *   Left("Ken").bind{n -> Right(n % 2 == 0)} = Left("Ken")
 *   Right(5).bind{n -> Right(n % 2 == 0)} = Right(false)
 *   Right(4).bind{n -> Right(n % 2 == 0)} = Right(true)
 *
 * @param f                 the function to bind across the Right
 * @return                  new Either
 */
fun <A, B, C> Either<A, B>.bind(f: (B) -> Either<A, C>): Either<A, C> = when(this) {
    is Either.Left -> Either.Left(this.value)
    is Either.Right -> f(this.value)
}

/**
 * Bind through the Right of this disjunction. A synonym for bind.
 *
 * Examples:
 *   Left("Ken").flatMap{n -> Right(n % 2 == 0)} = Left("Ken")
 *   Right(5).flatMap{n -> Right(n % 2 == 0)} = Right(false)
 *   Right(4).flatMap{n -> Right(n % 2 == 0)} = Right(true)
 *
 * @param f                 the function to bind across the Right
 * @return                  new Either
 */
fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = this.bind(f)

/**
 * Sequentially compose two actions, discarding any value produced by the first;
 *   like sequencing operators (such as the semicolon) in imperative languages.
 *
 * Examples:
 *   Left("Ken").andThen(Right(false)) = Left("Ken")
 *   Right(5).andThen(Right(false)) = Right(false)
 *
 * @param om    		        following Either monadic context
 * @return      		        Either context
 */
fun <A, B, C> Either<A, B>.andThen(om: Either<A, C>): Either<A, C> = this.bind{_ -> om}
