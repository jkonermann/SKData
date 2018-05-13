package com.adt.kotlin.data.sequence

/**
 * Functions on the Sequence class. The functions aim to present an idiomatic
 *   Kotlin interface on to the class.
 *
 * @author	                    Ken Barclay
 * @since                       January 2016
 */

import com.adt.kotlin.data.immutable.list.*
import com.adt.kotlin.data.immutable.list.List
import com.adt.kotlin.data.immutable.list.ListF


/**
 * Return an immutable list over the elements of this sequence.
 */
fun <A> Sequence<A>.toKList(): List<A> =
        this.fold(ListF.empty()){list, elem -> list.append(elem)}