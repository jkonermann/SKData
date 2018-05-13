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

import com.adt.kotlin.data.immutable.set.SetF.balance
import com.adt.kotlin.data.immutable.set.SetF.bin
import com.adt.kotlin.data.immutable.set.SetF.empty
import com.adt.kotlin.data.immutable.set.SetF.glue
import com.adt.kotlin.data.immutable.set.SetF.hedgeDifference
import com.adt.kotlin.data.immutable.set.SetF.hedgeUnion
import com.adt.kotlin.data.immutable.set.SetF.join
import com.adt.kotlin.data.immutable.set.SetF.merge
import com.adt.kotlin.data.immutable.set.SetF.splitLookup

import com.adt.kotlin.data.immutable.option.Option
import com.adt.kotlin.data.immutable.option.OptionF.none
import com.adt.kotlin.data.immutable.option.OptionF.some

import com.adt.kotlin.data.immutable.list.List
import com.adt.kotlin.data.immutable.list.ListF

import com.adt.kotlin.data.immutable.set.SetF.fromList
import com.adt.kotlin.data.immutable.set.SetF.toList

import com.adt.kotlin.fp.FunctionF.constant


sealed class Set<A : Comparable<A>>(val size: Int) {

    object Tip : Set<Nothing>(0)



    class Bin<A : Comparable<A>>(size: Int, val value: A, val left: Set<A>, val right: Set<A>) : Set<A>(size)



    /**
     * Are two sets equal?
     *
     * @param other             the other set
     * @return                  true if both sets are the same; false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherSet: Set<A> = other as Set<A>
            SetF.toList(this) == SetF.toList(otherSet)
        }
    }

    /**
     * Compose all the elements of this set as a string using the separator, prefix, postfix, etc.
     *
     * Examples:
     *   {Jessie, John, Ken}.makeString(", ", "<{", "}>", 2, "...") = <{Jessie: 22, John: 31, ...}>
     *   {Jessie, John, Ken}.makeString(", ", "<{", "}>", 2) = <{Jessie: 22, John: 31, ...}>
     *   {Jessie, John, Ken}.makeString(", ", "<{", "}>") = <{Jessie: 22, John: 31, Ken: 25}>
     *   {Jessie, John, Ken}.makeString() = <{Jessie: 22, John: 31, Ken: 25}>
     *   {}.makeString() = <{}>
     *
     * @param separator         the separator between each element
     * @param prefix            the leading content
     * @param postfix           the trailing content
     * @param limit             constrains the output to the fist limit elements
     * @param truncated         indicator that the output has been limited
     * @return                  the list content
     */
    fun makeString(separator: String = ", ", prefix: String = "", postfix: String = "", limit: Int = -1, truncated: String = "..."): String =
            SetF.toList(this).makeString(separator, prefix, postfix, limit, truncated)

    /**
     * Compose all the elements of this set as a string using the default separator, prefix, postfix, etc.
     *
     * @return                  the map content
     */
    fun makeString(): String = this.makeString(", ", "<{", "}>")

    /**
     * Produce a string representation of a Set.
     *
     * @return                  string as <{ ... }>
     */
    override fun toString(): String = this.makeString()

    /**
     * Present the set as a graph revealing the left and right subsets.
     *
     * @return                  the set as a graph
     */
    fun toGraph(): String {
        fun recToGraph(set: Set<A>, spaces: String): String {
            return when(set) {
                is Tip -> "${spaces}Tip"
                is Bin -> {
                    val binString: String = "${spaces}Bin: ${set.value}"
                    val leftString: String = recToGraph(set.left, spaces + "  ")
                    val rightString: String = recToGraph(set.right, spaces + "  ")
                    "${binString}\n${leftString}\n${rightString}"
                }
            }
        }   // recToGraph

        return recToGraph(this, "")
    }

    /**
     * Obtain the size of the set, a synonym for length.
     *
     * Examples:
     *   {Jessie, John, Ken}.size() = 3
     *   {}.size() = 0
     *
     * @return                  the number of elements in the set
     */

    fun size(): Int = size

    /**
     * Obtains the length of a set.
     *
     * Examples:
     *   {Jessie, John, Ken}.length() = 3
     *   {}.length() = 0
     *
     * @return                  the number of elements in the set
     */
    fun length(): Int = size

    /**
     * Test whether the set is empty.
     *
     * Examples:
     *   {Jessie, John, Ken}.isEmpty() = false
     *   {}.isEmpty() = true
     *
     * @return                  true if the set contains no elements
     */
    fun isEmpty(): Boolean = (size == 0)

    /**
     * Determine if the set contains the given element.
     *
     * Examples:
     *   {Jessie, John, Ken}.contains(John) = true
     *   {Jessie, John, Ken}.contains(Irene) = false
     *   {}.contains(John) = false
     *
     * @param a                 search element
     * @return                  true if the given element is in the set
     */
    fun contains(a: A): Boolean = this.contains{b: A -> (b == a)}

    /**
     * Determine if this set contains the element determined by the predicate.
     *
     * Examples:
     *   {Jessie, John, Ken}.contains{name -> (name == John)} = true
     *   {Jessie, John, Ken}.contains{name -> (name == Irene)} = false
     *   {}.contains{name -> (name == John)} = false
     *
     * @param predicate         search predicate
     * @return                  true if search element is present, false otherwise
     */
    fun contains(predicate: (A) -> Boolean): Boolean {
        fun recContains(predicate: (A) -> Boolean, set: Set<A>): Boolean {
            return when(set) {
                is Tip -> false
                is Bin -> {
                    if (predicate(set.value))
                        true
                    else if (recContains(predicate, set.left))
                        true
                    else
                        recContains(predicate, set.right)
                }
            }
        }   // recContains

        return recContains(predicate, this)
    }

    /**
     * Is this a subset of the given set?, ie. are all the elements
     *   of this set also elements of the given set?
     *
     * Examples:
     *   {Jessie, John, Ken}.isSubsetOf({Jessie, John, Ken}) = true
     *   {Jessie, John}.isSubsetOf({Jessie, John, Ken}) = true
     *   {Jessie, John, Ken}.isSubsetOf({John, Ken}) = false
     *   {}.isSubsetOf({Jessie, John, Ken}) = true
     *   {Jessie, John, Ken}.isSubsetOf({}) = false
     *   {}.isSubsetOf({}) = true
     *
     * @param set               existing set
     * @return                  true if this set is a subset of the given set
     */
    fun isSubsetOf(set: Set<A>): Boolean {
        fun recIsSubsetOf(left: Set<A>, right: Set<A>): Boolean {
            return when(left) {
                is Tip -> true
                is Bin -> {
                    when(right) {
                        is Tip -> false
                        is Bin -> {
                            val split: Triple<Set<A>, Boolean, Set<A>> = right.splitMember(left.value)
                            split.second && recIsSubsetOf(left.left, split.first) && recIsSubsetOf(left.right, split.third)
                        }
                    }
                }
            }
        }   // recIsSubsetOf

        return recIsSubsetOf(this, set)
    }

    /**
     * Is this a proper subset of the given set? (ie. a subset but not equal).
     *
     * Examples:
     *   {Jessie, John, Ken}.isProperSubsetOf({Jessie, John, Ken}) = false
     *   {Jessie, John}.isProperSubsetOf({Jessie, John, Ken}) = true
     *   {Jessie, John, Ken}.isProperSubsetOf({John, Ken}) = false
     *   {}.isProperSubsetOf({Jessie, John, Ken}) = true
     *   {Jessie, John, Ken}.isProperSubsetOf({}) = false
     *   {}.isProperSubsetOf({}) = false
     *
     * @param set               existing set
     * @return                  true if this set is a proper subset of the given set
     */
    fun isProperSubsetOf(set: Set<A>): Boolean = (this.size() < set.size()) && this.isSubsetOf(set)



// ---------- update --------------------------------------

    /**
     * Insert a new value in the set. If the value is already present in
     *   the set, then no action is taken.
     *
     * Examples:
     *   {Jessie, John, Ken}.insert(Irene) = {Irene, Jessie, John, Ken}
     *   {Jessie, John, Ken}.insert(John) = {Jessie, John, Ken}
     *
     * @param a                 new element to be added
     * @return                  updated set
     */
    fun insert(a: A): Set<A> {
        fun recInsert(a: A, set: Set<A>): Set<A> {
            return when(set) {
                is Tip -> bin(a, empty(), empty())
                is Bin -> {
                    if (a.compareTo(set.value) < 0)
                        balance(set.value, recInsert(a, set.left), set.right)
                    else if (a.compareTo(set.value) > 0)
                        balance(set.value, set.left, recInsert(a, set.right))
                    else
                        set
                }
            }
        }   // recInsert

        return recInsert(a, this)
    }

    /**
     * Delete the value from the set. When the value is not a member
     *   of the set, the original set is returned.
     *
     * Examples:
     *   {Jessie, John, Ken}.delete(John) = {Jessie, Ken}
     *   {Jessie, John, Ken}.delete(Irene) = {Jessie, John, Ken}
     *
     * @param a                 existing element to remove
     * @result                  updated set
     */
    fun delete(a: A): Set<A> {
        fun recDelete(a: A, set: Set<A>): Set<A> {
            return when(set) {
                is Tip -> set
                is Bin -> {
                    if (a.compareTo(set.value) < 0)
                        balance(set.value, recDelete(a, set.left), set.right)
                    else if (a.compareTo(set.value) > 0)
                        balance(set.value, set.left, recDelete(a, set.right))
                    else
                        glue(set.left, set.right)
                }
            }
        }   // recDelete

        return recDelete(a, this)
    }



// ---------- set operations ------------------------------

    /**
     * The union of two sets, ie all the elements from this set and
     *   from the given set.
     *
     * Examples:
     *   {Jessie, John, Ken}.union({Dawn, Irene}) = {Dawn, Irene, Jessie, John, Ken}
     *   {Jessie, John, Ken}.union({Jessie, Irene}) = {Irene, Jessie, John, Ken}
     *   {Jessie, John, Ken}.union({}) = {Jessie, John, Ken}
     *   {}.union({Dawn, Irene}) = {Dawn, Irene}
     *
     * @param set               existing set
     * @return                  the union of the two sets
     */
    fun union(set: Set<A>): Set<A> {
        return when(this) {
            is Tip -> set
            is Bin -> {
                when(set) {
                    is Tip -> this
                    is Bin -> hedgeUnion(constant<Int, A>(-1), constant<Int, A>(+1), this, set)
                }
            }
        }
    }

    /**
     * The union of two sets (as an operator), ie all the elements from this set and
     *   from the given set.
     *
     * Examples:
     *   {Jessie, John, Ken} + {Dawn, Irene} = {Dawn, Irene, Jessie, John, Ken}
     *   {Jessie, John, Ken} + {Jessie, Irene} = {Irene, Jessie, John, Ken}
     *   {Jessie, John, Ken} + {} = {Jessie, John, Ken}
     *   {} + {Dawn, Irene} = {Dawn, Irene}
     *
     * @param set               existing set
     * @return                  the union of the two sets
     */
    operator fun plus(set: Set<A>): Set<A> = this.union(set)

    /**
     * The intersection of two sets, ie all the elements that are
     *   present in both sets.
     *
     * Examples:
     *   {Jessie, John, Ken}.intersection({Jessie, John, Ken}) = {Jessie, John, Ken}
     *   {Jessie, John, Ken}.intersection({Jessie, John}) = {Jessie, John}
     *   {Jessie, John, Ken}.intersection({Dawn, Irene}) = {}
     *   {Jessie, John, Ken}.intersection({}) = {}
     *   {}.intersection({Jessie, John, Ken}) = {}
     *
     * @param set               existing set
     * @return                  the intersection of the two sets
     */
    fun intersection(set: Set<A>): Set<A> {
        fun recIntersection(left: Set<A>, right: Set<A>): Set<A> {
            return when(left) {
                is Tip -> empty<A>()
                is Bin -> {
                    when(right) {
                        is Tip -> empty<A>()
                        is Bin -> {
                            if (left.value.compareTo(right.value) >= 0) {
                                val split: Triple<Set<A>, Option<A>, Set<A>> = splitLookup(right.value, left)
                                val leftSet: Set<A> = recIntersection(split.first, right.left)
                                val rightSet: Set<A> = recIntersection(split.third, right.right)
                                if (split.second.isEmpty())
                                    merge(leftSet, rightSet)
                                else
                                    join(split.second.get(), leftSet, rightSet)
                            } else {
                                val split: Triple<Set<A>, Boolean, Set<A>> = right.splitMember(left.value)
                                val leftSet: Set<A> = recIntersection(left.left, split.first)
                                val rightSet: Set<A> = recIntersection(left.right, split.third)
                                if (split.second)
                                    join(left.value, leftSet, rightSet)
                                else
                                    merge(leftSet, rightSet)
                            }
                        }
                    }
                }
            }
        }   // recIntersection

        return recIntersection(this, set)
    }

    /**
     * The intersection of two sets (as an operator), ie all the elements that are
     *   present in both sets.
     *
     * Examples:
     *   {Jessie, John, Ken} * {Jessie, John, Ken} = {Jessie, John, Ken}
     *   {Jessie, John, Ken} * {Jessie, John} = {Jessie, John}
     *   {Jessie, John, Ken} * {Dawn, Irene} = {}
     *   {Jessie, John, Ken} * {} = {}
     *   {} * {Jessie, John, Ken} = {}
     *
     * @param set               existing set
     * @return                  the intersection of the two sets
     */
    operator fun times(set: Set<A>): Set<A> = this.intersection(set)

    /**
     * Difference two sets, ie all the elements in this set that are
     *   not present in the given set.
     *
     * Examples:
     *   {Jessie, John, Ken}.difference({Jessie, John, Ken}) = {}
     *   {Jessie, John, Ken}.difference({John, Ken}) = {Jessie}
     *   {Jessie, John, Ken}.difference({}) = {Jessie, John, Ken}
     *   {}.difference({Jessie, John, Ken}) = {}
     *
     * @param set               existing set
     * @return                  the difference of this set and the given set
     */
    fun difference(set: Set<A>): Set<A> {
        return when(this) {
            is Tip -> empty<A>()
            is Bin -> {
                when(set) {
                    is Tip -> this
                    is Bin -> hedgeDifference(constant<Int, A>(-1), constant<Int, A>(+1), this, set)
                }
            }
        }
    }

    /**
     * Difference two sets (as an operator), ie all the elements in this set that are
     *   not present in the given set.
     *
     * Examples:
     *   {Jessie, John, Ken} - {Jessie, John, Ken} = {}
     *   {Jessie, John, Ken} - {John, Ken} = {Jessie}
     *   {Jessie, John, Ken} - {} = {Jessie, John, Ken}
     *   {} - {Jessie, John, Ken} = {}
     *
     * @param set               existing set
     * @return                  the difference of this set and the given set
     */
    operator fun minus(set: Set<A>): Set<A> = this.difference(set)



// ---------- transformations -----------------------------

    /**
     * Function map applies the function parameter to each item in the set, delivering
     *   a new set.
     *
     * Examples:
     *   {Jessie, John, Ken}.map{name -> name.charAt(0)} = {J, K}
     *   {}.map{name -> charAt(0)} = {}
     *
     * @param f                 transformation function
     * @return                  set with the elements transformed
     */
    fun <B : Comparable<B>> map(f: (A) -> B): Set<B> {
        val treeList: List<A> = toList(this)
        val mappedList: List<B> = treeList.map(f)
        return fromList(mappedList)
    }



// ---------- filter --------------------------------------

    /**
     * The find function takes a predicate and a set and returns the first
     *   element in the set matching the predicate, or none if there is no
     *   such element.
     *
     * Examples:
     *   {Jessie, John, Ken}.find{name -> name.startsWith(J)} = some(John)
     *   {Jessie, John, Ken}.find{name -> name.charAt(0) >= A} = some(John)
     *   {Jessie, John, Ken}.find{name -> name.charAt(0) >= Z} = none
     *   {}.find{name -> name.startsWith(J)} = none
     *
     * @param predicate         criteria
     * @return                  matching element, if found
     */
    fun find(predicate: (A) -> Boolean): Option<A> {
        fun recFind(predicate: (A) -> Boolean, set: Set<A>): Option<A> {
            return when(set) {
                is Tip -> none
                is Bin -> {
                    if (predicate(set.value))
                        some(set.value)
                    else {
                        val opt: Option<A> = recFind(predicate, set.left)
                        if (opt.isEmpty())
                            recFind(predicate, set.right)
                        else
                            opt
                    }
                }
            }
        }   // recFind

        return recFind(predicate, this)
    }

    /**
     * Filter all elements that satisfy the predicate.
     *
     * Examples:
     *   {Jessie, John, Ken}.filter{name -> name.startsWith(J)} = {Jessie, John}
     *   {Jessie, John, Ken}.filter{name -> name.charAt(0) >= A} = {Jessie, John, Ken}
     *   {Jessie, John, Ken}.filter{name -> name.charAt(0) >= Z} = {}
     *   {}.filter{name -> name.startsWith(J)} = {}
     *
     * @param predicate         criteria
     * @return                  set comprising those elements from this set that match criteria
     */
    fun filter(predicate: (A) -> Boolean): Set<A> {
        fun recFilter(predicate: (A) -> Boolean, set: Set<A>): Set<A> {
            return when(set) {
                is Tip -> set
                is Bin -> {
                    if (predicate(set.value))
                        join(set.value, recFilter(predicate, set.left), recFilter(predicate, set.right))
                    else
                        merge(recFilter(predicate, set.left), recFilter(predicate, set.right))
                }
            }
        }   // recFilter

        return recFilter(predicate, this)
    }

    /**
     * Partition the set into two sets, one with all elements that satisfy
     *   the predicate and one with all elements that don't satisfy the predicate.
     *
     * Examples:
     *   {Jessie, John, Ken}.partition{name -> name.startsWith(J)} = ({Jessie, John}, {Ken})
     *
     * @param predicate         criteria
     * @return                  pair of sets
     */
    fun partition(predicate: (A) -> Boolean): Pair<Set<A>, Set<A>> {
        fun recPartition(predicate: (A) -> Boolean, set: Set<A>): Pair<Set<A>, Set<A>> {
            return when(set) {
                is Tip -> Pair(set, set)
                is Bin -> {
                    if (predicate(set.value)) {
                        val leftPair: Pair<Set<A>, Set<A>> = recPartition(predicate, set.left)
                        val rightPair: Pair<Set<A>, Set<A>> = recPartition(predicate, set.right)
                        Pair(join(set.value, leftPair.first, rightPair.first), merge(leftPair.second, rightPair.second))
                    } else {
                        val leftPair: Pair<Set<A>, Set<A>> = recPartition(predicate, set.left)
                        val rightPair: Pair<Set<A>, Set<A>> = recPartition(predicate, set.right)
                        Pair(merge(leftPair.first, rightPair.first), join(set.value, leftPair.second, rightPair.second))
                    }
                }
            }
        }   // recPartition

        return recPartition(predicate, this)
    }

    /**
     * The expression split x set is a pair (set1, set2) where set1 comprises
     *   the elements of set less than x and set2 comprises the elements of
     *   set greater than x.
     *
     * Examples:
     *   {Jessie, John, Ken}.split(John) = ({Jessie}, {Ken})
     *   {Jessie, John, Ken}.split(Linda) = ({Jessie, John, Ken}, {})
     *
     * @param a                 the pivot element
     * @return                  pair of sets
     */
    fun split(a: A): Pair<Set<A>, Set<A>> {
        fun recSplit(a: A, set: Set<A>): Pair<Set<A>, Set<A>> {
            return when(set) {
                is Tip -> Pair(set, set)
                is Bin -> {
                    if (a.compareTo(set.value) < 0) {
                        val leftSplit: Pair<Set<A>, Set<A>> = recSplit(a, set.left)
                        Pair(leftSplit.first, join(set.value, leftSplit.second, set.right))
                    } else if (a.compareTo(set.value) > 0) {
                        val rightSplit: Pair<Set<A>, Set<A>> = recSplit(a, set.right)
                        Pair(join(set.value, set.left, rightSplit.first), rightSplit.second)
                    } else
                        Pair(set.left, set.right)
                }
            }
        }   // recSplit

        return recSplit(a, this)
    }

    /**
     * Performs a split but also returns whether the pivot element was found
     *   in the original set.
     *
     * Examples:
     *   {Jessie, John, Ken}.splitMember(John) = ({Jessie}, true, {Ken})
     *   {Jessie, John, Ken}.splitMember(Linda) = ({Jessie, John, Ken}, false, {})
     *
     * @param a                 the pivot element
     * @return                  triple of the two sets and if the pivot was present
     */
    fun splitMember(a: A): Triple<Set<A>, Boolean, Set<A>> {
        val split: Triple<Set<A>, Option<A>, Set<A>> = splitLookup(a, this)
        return Triple(split.first, split.second.fold(false){_ -> true}, split.third)
    }

}
