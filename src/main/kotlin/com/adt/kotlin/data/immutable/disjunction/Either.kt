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



sealed class Either<out A, out B> {

    class Left<A, B>(val value: A) : Either<A, B>() {

        override fun toString(): String = "Left(${value})"

    // ---------- properties ----------------------------------

        override val isLeft: Boolean = true
        override val isRight: Boolean = false

    }   // Left



    class Right<A, B>(val value: B) : Either<A, B>() {

        override fun toString(): String = "Right(${value})"

    // ---------- properties ----------------------------------

        override val isLeft: Boolean = false
        override val isRight: Boolean = true

    }   // Right



    abstract  val isLeft: Boolean
    abstract val isRight: Boolean




    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val either: Either<A, B> = other as Either<A, B>
            when(this) {
                is Left -> {
                    when(either) {
                        is Left -> (this.value == either.value)
                        is Right -> false
                    }
                }
                is Right -> {
                    when(either) {
                        is Left -> false
                        is Right -> (this.value == either.value)
                    }
                }
            }
        }
    }

    /**
     * Return true if this disjunction is a Right value satisfying the
     *   predicate.
     *
     * Examples:
     *   Left("Ken").exists{n: Int -> (n % 2 == 0)} = false
     *   Right(5).exists{n: Int -> (n % 2 == 0)} = false
     *   Right(4).exists{n: Int -> (n % 2 == 0)} = true
     *
     * @param predicate         the criteria
     * @return                  true if this disjunction is a Right value satisfying the criteria
     */
    fun exists(predicate: (B) -> Boolean): Boolean = when(this) {
        is Left -> false
        is Right -> predicate(this.value)
    }

    /**
     * Apply function fa if this is a Left or function fb if this is a Right.
     *
     * Examples:
     *   Left("Ken").fold({str -> str.length()}, {n -> 2 * n}) = 3
     *   Right(5).fold({str -> str.length()}, {n -> 2 * n}) = 10
     *
     * @param fa                the function to apply if this is a Left
     * @param fb                the function to apply if this is a Right
     * @return                  the result of applying whichever function
     */
    fun <C> fold(fa: (A) -> C, fb: (B) -> C): C = when(this) {
        is Left -> fa(this.value)
        is Right -> fb(this.value)
    }

    /**
     * Flip the Left/Right values in this disjunction.
     *
     * Examples:
     *   Left("Ken").swap() = Right("Ken")
     *   Right(5).swap() = Left(5)
     *
     * @return                  a disjunction with values reversed
     */
    fun swap(): Either<B, A> = when(this) {
        is Left -> Right(this.value)
        is Right -> Left(this.value)
    }

    /**
     * Run the given function on the Left value.
     *
     * Examples:
     *   Left("Ken").leftMap{str -> str.length()} = Left(3)
     *   Right(5).leftMap{str -> str.length()} = Right(5)
     *
     * @param f                 the function to apply if this is a Left
     * @return                  this if a Right; or a Left wrapping the function application
     */
    fun <C> leftMap(f: (A) -> C): Either<C, B> = when(this) {
        is Left -> Left(f(this.value))
        is Right -> Right(this.value)
    }

    /**
     * Map on the right of this disjunction.
     *
     * Examples:
     *   Left("Ken").map{n -> 2 * n} = Left("Ken")
     *   Right(5).map{n -> 2 * n} = Right(10)
     *
     * @param f                 the function to apply if this is a Right
     * @return                  this if a left; otherwise a Right wrapping the function application
     */
    fun <C> map(f: (B) -> C): Either<A, C> = when(this) {
        is Left -> Left(this.value)
        is Right -> Right(f(this.value))
    }

}
