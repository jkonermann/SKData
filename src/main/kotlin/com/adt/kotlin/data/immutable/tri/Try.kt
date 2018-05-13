package com.adt.kotlin.data.immutable.tri

/**
 * The Try type represents a computation that may return a successfully computed value
 *   or result in an exception. Instances of Try[A], are either an instance of Success[A]
 *   or Failure[A]. The code is modelled on the Scala Try.
 *
 * The algebraic data type declaration is:
 *
 * datatype Try[A] = Failure
 *                 | Success A
 *
 * @param A                     the type of element
 *
 * @author	                    Ken Barclay
 * @since                       October 2014
 */



sealed class Try<A> {

    /**
     * Return the value if this is a Success or throws the exception if this is a Failure.
     *
     * @return                  the value or an exception
     */
    abstract fun get(): A

    /**
     * Return the value if this is a Success or the given default argument if this is a Failure.
     *
     * @param defaultValue      return value if this is a Failure
     * @return                  the value wrapped by this Success or the given default
     */
    abstract fun getOrElse(defaultValue: A): A

    /**
     * Map the given function to the value from this Success or returns this if this is a Failure.
     *
     * @param f                 transformation function
     * @return                  a value of type B wrapped in a Try
     */
    abstract fun <B> map(f: (A) -> B): Try<B>

    /**
     * Convert this to a Failure if the predicate is not satisfied.
     *
     * @param predicate         test criteria
     * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
     */
    abstract fun filter(predicate: (A) -> Boolean): Try<A>

    /**
     * Apply the block to the element in the try.
     *
     * @param block                 body of program block
     */
    fun forEach(block: (A) -> Unit): Unit {
        if (this.isSuccess)
            block(this.get())
    }

    /**
     * Sequentially compose two Trys, passing any value produced by the
     *   first as an argument to the second.
     *
     * @param f    		            pure function:: A -> Try[B]
     * @return        		        wrapped result of function application
     */
    abstract fun <B> bind(f: (A) -> Try<B>): Try<B>

    /**
     * Sequentially compose two Trys, passing any value produced by the
     *   first as an argument to the second.
     *
     * @param f    		            pure function:: A -> Try[B]
     * @return        		        wrapped result of function application
     */
    fun <B> flatMap(f: (A) -> Try<B>): Try<B> = this.bind(f)

    /**
     * Sequentially compose two actions, discarding any value produced by the first,
     *   like sequencing operators (such as the semicolon) in imperative languages.
     *
     * @param om    		        following Try monadic context
     * @return      		        Try context
     */
    fun <B> andThen(om: Try<B>): Try<B> = this.bind{_: A -> om}


// ---------- properties ----------------------------------

    abstract public val isFailure: Boolean
    abstract public val isSuccess: Boolean



    class Failure<A>(val throwable: Throwable) : Try<A>() {

        /**
         * Return the value if this is a Success or throws the exception if this is a Failure.
         *
         * @return                  the value or an exception
         */
        override fun get(): A = throw throwable

        /**
         * Return the value if this is a Success or the given default argument if this is a Failure.
         *
         * @param defaultValue      return value if this is a Failure
         * @return                  the value wrapped by this Success or the given default
         */
        override fun getOrElse(defaultValue: A): A = defaultValue

        /**
         * Map the given function to the value from this Success or returns this if this is a Failure.
         *
         * @param f                 transformation function
         * @return                  a value of type B wrapped in a Try
         */
        override fun <B> map(f: (A) -> B): Try<B> = Failure<B>(throwable)

        /**
         * Convert this to a Failure if the predicate is not satisfied.
         *
         * @param predicate         test criteria
         * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
         */
        override fun filter(predicate: (A) -> Boolean): Try<A> = this

        /**
         * Sequentially compose two Trys, passing any value produced by the
         *   first as an argument to the second.
         *
         * @param f    		            pure function:: A -> Try[B]
         * @return        		        wrapped result of function application
         */
        override fun <B> bind(f: (A) -> Try<B>): Try<B> = Failure<B>(throwable)

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
                @Suppress("UNCHECKED_CAST") val otherFailure: Failure<A> = other as Failure<A>
                (this.throwable.message == otherFailure.throwable.message)
            }
        }

        /**
         * Returns a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Failure(${throwable.message})"



// ---------- properties ----------------------------------

        override public val isFailure: Boolean = true
        override public val isSuccess: Boolean = false

    }   // Failure


    class Success<A>(val value: A) : Try<A>() {

        /**
         * Return the value if this is a Success or throws the exception if this is a Failure.
         *
         * @return                  the value or an exception
         */
        override fun get(): A = value

        /**
         * Return the value if this is a Success or the given default argument if this is a Failure.
         *
         * @param defaultValue      return value if this is a Failure
         * @return                  the value wrapped by this Success or the given default
         */
        override fun getOrElse(defaultValue: A): A = value

        /**
         * Map the given function to the value from this Success or returns this if this is a Failure.
         *
         * @param f                 transformation function
         * @return                  a value of type B wrapped in a Try
         */
        override fun <B> map(f: (A) -> B): Try<B> = Success(f(value))

        /**
         * Convert this to a Failure if the predicate is not satisfied.
         *
         * @param predicate         test criteria
         * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
         */
        override fun filter(predicate: (A) -> Boolean): Try<A> =
                if (predicate(value)) this else Failure<A>(TryException("filter: predicate does not hold for ${value}"))

        /**
         * Sequentially compose two Trys, passing any value produced by the
         *   first as an argument to the second.
         *
         * @param f    		            pure function:: A -> Try[B]
         * @return        		        wrapped result of function application
         */
        override fun <B> bind(f: (A) -> Try<B>): Try<B> {
            return try {
                f(value)
            } catch(ex: Exception) {
                Failure<B>(ex)
            }
        }

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
                @Suppress("UNCHECKED_CAST") val otherSuccess: Success<A> = other as Success<A>
                (this.get() == otherSuccess.get())
            }
        }

        /**
         * Returns a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Success(${value})"



// ---------- properties ----------------------------------

        override public val isFailure: Boolean = false
        override public val isSuccess: Boolean = true

    }   // Success

}
