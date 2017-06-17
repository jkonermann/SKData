package com.adt.kotlin.data.immutable.set

/**
 * A class hierarchy defining an immutable set collection. The algebraic data
 *   type declaration is:
 *
 * datatype Set[A] = Tip
 *                 | Bin of A * Set[A] * Set[A]
 *
 * Sets are implemented as size balanced binary trees. This implementation
 *   mirrors the Haskell implementation in Data.Set that, in turn, is based
 *   on an efficient balanced binary tree referenced in the sources.
 *
 * The Set class is defined generically in terms of the type parameter A.
 *
 * @param A                     the type of elements in the set
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */



class SetException(message: String) : Exception(message)