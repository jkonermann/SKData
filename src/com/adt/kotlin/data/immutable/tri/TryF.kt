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

import com.adt.kotlin.data.immutable.tri.Try.Failure
import com.adt.kotlin.data.immutable.tri.Try.Success




object TryF {

    /**
     * Factory functions to create the base instances.
     */
    fun <A> failure(throwable: Throwable): Try<A> = Failure(throwable)
    fun <A> success(value: A): Try<A> = Success(value)

    /**
     * Constructs a Try using the parameter. This function will ensure any non-fatal
     *   exception is caught and a Failure object is returned.
     *
     * @param exp               the (lazy) expression to wrap in a Try
     * @return                  a Failure if the expression raises an exception; otherwise its value wrapped in a Success
     */
    fun <A> `try`(exp: () -> A): Try<A> {
        return try {
            Success(exp())
        } catch(ex: Exception) {
            Failure(ex)
        }
    }

}
