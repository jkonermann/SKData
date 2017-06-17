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
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.set.Set.Tip
import com.adt.kotlin.data.immutable.set.Set.Bin

import com.adt.kotlin.data.immutable.option.Option
import com.adt.kotlin.data.immutable.option.OptionF.none
import com.adt.kotlin.data.immutable.option.OptionF.some

import com.adt.kotlin.data.immutable.list.List
import com.adt.kotlin.data.immutable.list.ListF
import com.adt.kotlin.data.immutable.list.append

import kotlin.collections.List as KList



object SetF {

    /**
     * Factory binding/function to create the base instances.
     */
    val tip: Set.Tip = Tip
    fun <A : Comparable<A>> bin(a: A, left: Set<A>, right: Set<A>): Set<A> = Bin(1 + left.size + right.size, a, left, right)

    /**
     * Create an empty set.
     *
     * @return    		            an empty set
     */
    fun <A : Comparable<A>> empty(): Set<A> {
        @Suppress("UNCHECKED_CAST") val emptySet: Set<A> = Tip as Set<A>
        return emptySet
    }



    fun <A : Comparable<A>> of(a1: A): Set<A> = singleton(a1)

    fun <A : Comparable<A>> of(a1: A, a2: A): Set<A> = singleton(a1).insert(a2)

    fun <A : Comparable<A>> of(a1: A, a2: A, a3: A): Set<A> = singleton(a1).insert(a2).insert(a3)

    fun <A : Comparable<A>> of(a1: A, a2: A, a3: A, a4: A): Set<A> = singleton(a1).insert(a2).insert(a3).insert(a4)

    fun <A : Comparable<A>> of(a1: A, a2: A, a3: A, a4: A, a5: A): Set<A> = singleton(a1).insert(a2).insert(a3).insert(a4).insert(a5)

    fun <A : Comparable<A>> of(vararg a: A): Set<A> = fromSequence(*a)



    /**
     * A set with a single element.
     *
     * Examples:
     *   singleton(Jessie) = {Jessie}
     */
    fun <A : Comparable<A>> singleton(a: A): Set<A> = Bin(1, a, empty(), empty())



    /**
     * Convert a variable-length parameter series into a set.
     *
     * Examples:
     *   fromSequence(Jessie, John, Ken) = {Jessie, John, Ken}
     *   fromSequence() = {}
     *
     * @param seq                   variable-length parameter series
     * @return                      set of the given values
     */
    fun <A : Comparable<A>> fromSequence(vararg seq: A): Set<A> =
            seq.fold(empty()){set, a -> set.insert(a)}

    /**
     * Convert a variable-length Kotlin/Java list into a set.
     *
     * Examples:
     *   fromKList(Jessie, John, Ken) = {Jessie, John, Ken}
     *   fromKList() = {}
     *
     * @param xs                    variable-length list
     * @return                      set of the given values
     */
    fun <A : Comparable<A>> fromKList(xs: KList<A>): Set<A> {
        return xs.foldRight(empty<A>()){a, set -> set.insert(a)}
    }

    /**
     * Convert a variable-length immutable list into a set.
     *
     * Examples:
     *   fromList([Jessie, John, Ken]) = {Jessie, John, Ken}
     *   fromList([]) = {}
     *
     * @param ls                    variable-length list
     * @return                      map of the given values
     */
    fun <A : Comparable<A>> fromList(ls: List<A>): Set<A> {
        return ls.foldRight(empty<A>()) {a -> {set -> set.insert(a)}}
    }

    /**
     * Convert an immutable set to a Kotlin/Java array list.
     *
     * @param set                   existing immutable set
     * @return                      an array list
     */
    fun <A : Comparable<A>> toKList(set: Set<A>): KList<A> {
        fun <A : Comparable<A>> recToKList(set: Set<A>, acc: KList<A>): KList<A> {
            return when(set) {
                is Tip -> acc
                is Bin -> {
                    val leftAcc: KList<A> = recToKList(set.left, acc)
                    val rightAcc: KList<A> = recToKList(set.right, leftAcc + set.value)
                    return rightAcc
                }
            }
        }

        return recToKList(set, arrayListOf<A>())
    }

    /**
     * Convert an immutable set to an immutable list.
     *
     * @param set                   existing immutable set
     * @return                      a list
     */
    fun <A : Comparable<A>> toList(set: Set<A>): List<A> {
        fun <A : Comparable<A>> recToList(set: Set<A>): List<A> {
            return when(set) {
                is Tip -> ListF.empty()
                is Bin -> recToList(set.left).append(set.value).append(recToList(set.right))
            }
        }

        return recToList(set)
    }



// ---------- implementation ------------------------------

    private val DELTA: Int = 4      // the maximal relative difference between the sizes of two trees
    private val RATIO: Int = 2      // is the ratio between an outer and inner sibling of the
                                        // heavier subtree in an unbalanced setting

    /**
     * Balance two sets with the value a. The sizes of the trees should balance
     *   after decreasing the size of one of them (a rotation).
     */
    internal fun <A : Comparable<A>> balance(a: A, left: Set<A>, right: Set<A>): Set<A> {
        return if(left.size + right.size <= 1)
            bin(a, left, right)
        else if (right.size >= DELTA * left.size)
            rotateLeft(a, left, right)
        else if (left.size >= DELTA * right.size)
            rotateRight(a, left, right)
        else
            bin(a, left, right)
    }

    /********** UNUSED
    internal fun <A : Comparable<A>> balanceLeft(a: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(right) {
            is Tip -> {
                when(left) {
                    is Tip -> bin(a, empty(), empty())
                    is Bin -> {
                        val leftLeft: Set<A> = left.left
                        val leftRight: Set<A> = left.right
                        when(leftLeft) {
                            is Tip -> when(leftRight) {
                                is Tip -> bin(a, left, empty())
                                is Bin -> bin(leftRight.value, bin(left.value, empty(), empty()), bin(a, empty(), empty()))
                            }
                            is Bin -> when(leftRight) {
                                is Tip -> bin(left.value, leftLeft, bin(a, empty(), empty()))
                                is Bin -> {
                                    if(leftRight.size < RATIO * leftLeft.size)
                                        bin(left.value, leftLeft, bin(a, leftRight, empty()))
                                    else
                                        bin(leftRight.value, bin(left.value, leftLeft, leftRight.left), bin(a, leftRight.right, empty()))
                                }
                            }
                        }
                    }
                }
            }
            is Bin -> {
                when(left) {
                    is Tip -> bin(a, empty(), right)
                    is Bin -> {
                        if (left.size > DELTA * right.size) {
                            val leftLeft: Set<A> = left.left
                            val leftRight: Set<A> = left.right
                            if(!leftLeft.isEmpty() && !leftRight.isEmpty()) {
                                if (leftRight.size < RATIO * leftLeft.size)
                                    bin(left.value, leftLeft, bin(a, leftRight, right))
                                else {
                                    val leftRightBin: Bin<A> = leftRight as Bin<A>
                                    bin(leftRightBin.value, bin(left.value, leftLeft, leftRightBin.left), bin(a, leftRightBin.right, right))
                                }
                            } else
                                throw SetException("balanceLeft: failure")
                        } else
                            bin(a, left, right)
                    }
                }
            }
        }
    }

        internal fun <A : Comparable<A>> balanceRight(a: A, left: Set<A>, right: Set<A>): Set<A> {
            return when(left) {
                is Tip -> {
                    when(right) {
                        is Tip -> bin(a, empty(), empty())
                        is Bin -> {
                            val rightLeft: Set<A> = right.left
                            val rightRight: Set<A> = right.right
                            when(rightLeft) {
                                is Tip -> {
                                    when(rightRight) {
                                        is Tip -> bin(a, empty(), right)
                                        is Bin -> bin(right.value, bin(a, empty(), empty()), rightRight)
                                    }
                                }
                                is Bin -> {
                                    when(rightRight) {
                                        is Tip -> bin(rightLeft.value, bin(a, empty(), empty()), bin(right.value, empty(), empty()))
                                        is Bin -> {
                                            if (rightLeft.size < RATIO * rightRight.size)
                                                bin(right.value, bin(a, empty(), rightLeft.left), rightRight)
                                            else
                                                bin(rightLeft.value, bin(a, empty(), rightLeft.left), bin(right.value, rightLeft.right, rightRight))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is Bin -> {
                    when(right) {
                        is Tip -> bin(a, left, empty())
                        is Bin -> {
                            if (right.size > DELTA * left.size) {
                                val rightLeft: Set<A> = right.left
                                val rightRight: Set<A> = right.right
                                if (!rightLeft.isEmpty() && !rightRight.isEmpty()) {
                                    if (rightLeft.size < RATIO * rightRight.size)
                                        bin(right.value, bin(a, left, rightLeft), rightRight)
                                    else {
                                        val rightLeftBin: Bin<A> = rightLeft as Bin<A>
                                        bin(rightLeftBin.value, bin(a, left, rightLeftBin.left), bin(right.value, rightLeftBin.right, rightRight))
                                    }
                                } else
                                    throw SetException("balanceRight: failure")
                            } else
                                bin(a, left, right)
                        }
                    }
                }
            }
        }
    **********/

    internal fun <A : Comparable<A>> rotateLeft(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(right) {
            is Tip -> throw SetException("rotateLeft: right is Tip")
            is Bin -> {
                if (right.left.size < RATIO * right.right.size)
                    singleLeft(value, left, right)
                else
                    doubleLeft(value, left, right)
            }
        }
    }

    internal fun <A : Comparable<A>> rotateRight(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> throw SetException("rotateRight: left is Tip")
            is Bin -> {
                if (left.right.size < RATIO * left.left.size)
                    singleRight(value, left, right)
                else
                    doubleRight(value, left, right)
            }
        }
    }

    /**
     * Compose a new Set:
     *
     * singleLeft(x1, t1, Bin(_, x2, t2, t3)) = bin(x2, bin(x1, t1, t2), t3)
     */
    internal fun <A : Comparable<A>> singleLeft(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(right) {
            is Tip -> throw SetException("singleLeft: right is Tip")
            is Bin -> bin(right.value, bin(value, left, right.left), right.right)
        }
    }

    /**
     * Compose a new Set:
     *
     * singleRight(x1, Bin(_, x2, t1, t2), t3) = bin(x2, t1, bin(x1, t2, t3))
     */
    internal fun <A : Comparable<A>> singleRight(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> throw SetException("singleRight: left is Tip")
            is Bin -> bin(left.value, left.left, bin(value, left.right, right))
        }
    }

    /**
     * Compose a new Set:
     *
     * doubleLeft(x1, t1, Bin(_, x2, Bin(_, x3, t2, t3), t4)) = bin(x3, bin(x1, t1, t2), bin(x2, t3, t4))
     */
    internal fun <A : Comparable<A>> doubleLeft(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(right) {
            is Tip -> throw SetException("doubleLeft: right is Tip")
            is Bin -> {
                val rightLeft: Set<A> = right.left
                when(rightLeft) {
                    is Tip -> throw SetException("doubleLeft: right.left is Tip")
                    is Bin -> bin(rightLeft.value, bin(value, left, rightLeft.left), bin(right.value, rightLeft.right, right.right))
                }
            }
        }
    }

    /**
     * Compose a new Set:
     *
     * doubleRight(x1, Bin(_, x2, t1, Bin(_, x3, t2, t3)), t4) = bin(x3, bin(x2, t1, t2), bin(x1, t3, t4))
     */
    internal fun <A : Comparable<A>> doubleRight(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> throw SetException("doubleRight: left is Tip")
            is Bin -> {
                val leftRight: Set<A> = left.right
                when(leftRight) {
                    is Tip -> throw SetException("doubleRight: left.right is Tip")
                    is Bin -> bin(leftRight.value, bin(left.value, left.left, leftRight.left), bin(value, leftRight.right, right))
                }
            }
        }
    }

    /**
     * Glue the two sets together. Assumes that left and right are already balanced with respect to each other.
     *
     * glue Tip r = r
     * glue l Tip = l
     * glue l r
     *   | size l > size r = let (m,l') = deleteFindMax l in balanceR m l' r
     *   | otherwise       = let (m,r') = deleteFindMin r in balanceL m l r'
     */
    internal fun <A : Comparable<A>> glue(left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> right
            is Bin -> {
                when(right) {
                    is Tip -> left
                    is Bin -> {
                        if (left.size > right.size) {
                            val dfm: Pair<A, Set<A>> = deleteFindMax(left)
                            balance(dfm.first, dfm.second, right)
                        } else {
                            val dfm: Pair<A, Set<A>> = deleteFindMin(right)
                            balance(dfm.first, left, dfm.second)
                        }
                    }
                }
            }
        }
    }

    /**
     * Delete and find the minimal element.
     *
     * deleteFindMin t
     *   = case t of
     *       Bin _ x Tip r -> (x,r)
     *       Bin _ x l r   -> let (xm,l') = deleteFindMin l in (xm,balance x l' r)
     *       Tip           -> error
     */
    internal fun <A : Comparable<A>> deleteFindMin(set: Set<A>): Pair<A, Set<A>> {
        return when(set) {
            is Tip -> throw SetException("deleteFindMin: empty tree")
            is Bin -> {
                val setLeft: Set<A> = set.left
                when(setLeft) {
                    is Tip -> Pair(set.value, set.right)
                    is Bin -> {
                        val dfm: Pair<A, Set<A>> = deleteFindMin(set.left)
                        Pair(dfm.first, balance(set.value, dfm.second, set.right))
                    }
                }
            }
        }
    }

    /**
     * Delete and find the maximal element.
     *
     * deleteFindMax t
     *   = case t of
     *       Bin _ x l Tip -> (x,l)
     *       Bin _ x l r   -> let (xm,r') = deleteFindMax r in (xm,balance x l r')
     *       Tip           -> error
     */
    internal fun <A : Comparable<A>> deleteFindMax(set: Set<A>): Pair<A, Set<A>> {
        return when(set) {
            is Tip -> throw SetException("deleteFindMax: empty tree")
            is Bin -> {
                val setRight: Set<A> = set.right
                when(setRight) {
                    is Tip -> Pair(set.value, set.left)
                    is Bin -> {
                        val dfm: Pair<A, Set<A>> = deleteFindMax(set.right)
                        Pair(dfm.first, balance(set.value, set.left, dfm.second))
                    }
                }
            }
        }
    }

    /**
     * Restore balance and size.
     *
     * join x Tip r  = insertMin x r
     * join x l Tip  = insertMax x l
     * join x l@(Bin sizeL y ly ry) r@(Bin sizeR z lz rz)
     *   | delta*sizeL < sizeR  = balanceL z (join x l lz) rz
     *   | delta*sizeR < sizeL  = balanceR y ly (join x ry r)
     *   | otherwise            = bin x l r
     */
    internal fun <A : Comparable<A>> join(value: A, left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> insertMin(value, right)
            is Bin -> {
                when(right) {
                    is Tip -> insertMax(value, left)
                    is Bin -> {
                        if (DELTA * left.size <= right.size)
                            balance(right.value, join(value, left, right.left), right.right)
                        else if (DELTA * right.size <= left.size)
                            balance(left.value, left.left, join(value, left.right, right))
                        else
                            bin(value, left, right)
                    }
                }
            }
        }
    }

    /**
     * insertMax x t
     *   = case t of
     *       Tip -> singleton x
     *       Bin _ y l r
     *           -> balance y l (insertMax x r)
     */
    internal fun <A : Comparable<A>> insertMax(value: A, set: Set<A>): Set<A> {
        return when(set) {
            is Tip -> singleton(value)
            is Bin -> balance(set.value, set.left, insertMax(value, set.right))
        }
    }

    /**
     * insertMin x t
     *   = case t of
     *       Tip -> singleton x
     *       Bin _ y l r
     *           -> balance y (insertMin x l) r
     */
    internal fun <A : Comparable<A>> insertMin(value: A, set: Set<A>): Set<A> {
        return when(set) {
            is Tip -> singleton(value)
            is Bin -> balance(set.value, insertMin(value, set.left), set.right)
        }
    }

    /**
     * Merge two trees.
     *
     * merge Tip r   = r
     * merge l Tip   = l
     * merge l@(Bin sizeL x lx rx) r@(Bin sizeR y ly ry)
     *   | delta*sizeL < sizeR = balanceL y (merge l ly) ry
     *   | delta*sizeR < sizeL = balanceR x lx (merge rx r)
     *   | otherwise           = glue l r
     */
    internal fun <A : Comparable<A>> merge(left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> right
            is Bin -> {
                when(right) {
                    is Tip -> left
                    is Bin -> {
                        if (DELTA * left.size <= right.size)
                            balance(right.value, merge(left, right.left), right.right)
                        else if (DELTA * right.size <= left.size)
                            balance(left.value, left.left, merge(left.right, right))
                        else
                            glue(left, right)
                    }
                }
            }
        }
    }

    /**
     * The union of two sets, preferring the first set when equal elements are encountered.
     *   The implementation uses the efficient 'hedge-union' algorithm.
     */
    internal fun <A : Comparable<A>> hedgeUnion(cmpLo: (A) -> Int, cmpHi: (A) -> Int, left: Set<A>, right: Set<A>): Set<A> {
        return when(right) {
            is Tip -> left
            is Bin -> {
                when(left) {
                    is Tip -> join(right.value, filterGT(cmpLo, right.left), filterLT(cmpHi, right.right))
                    is Bin -> {
                        val cmpT: (A) -> Int = {a: A -> left.value.compareTo(a)}
                        join(left.value, hedgeUnion(cmpLo, cmpT, left.left, trim(cmpLo, cmpT, right)), hedgeUnion(cmpT, cmpHi, left.right, trim(cmpT, cmpHi, right)))
                    }
                }
            }
        }
    }

    /**
     * Difference of two sets. The implementation uses an efficient 'hedge' algorithm comparable
     *   with 'hedge-union'.
     */
    internal fun <A : Comparable<A>> hedgeDifference(cmpLo: (A) -> Int, cmpHi: (A) -> Int, left: Set<A>, right: Set<A>): Set<A> {
        return when(left) {
            is Tip -> empty()
            is Bin -> {
                when(right) {
                    is Tip -> join(left.value, filterGT(cmpLo, left.left), filterLT(cmpHi, left.right))
                    is Bin -> {
                        val cmpT: (A) -> Int = {a: A -> right.value.compareTo(a)}
                        merge(hedgeDifference(cmpLo, cmpT, trim(cmpLo, cmpT, left), right.left), hedgeDifference(cmpT, cmpHi, trim(cmpT, cmpHi, left), right.right))
                    }
                }
            }
        }
    }

    /**
     * Filter all values that deliver +1 when applied to the cmp from the given set.
     */
    internal fun <A : Comparable<A>> filterGT(cmp: (A) -> Int, set: Set<A>): Set<A> {
        return when(set) {
            is Tip -> empty()
            is Bin -> {
                if (cmp(set.value) < 0)
                    join(set.value, filterGT(cmp, set.left), set.right)
                else if (cmp(set.value) > 0)
                    filterGT(cmp, set.right)
                else
                    set.right
            }
        }
    }

    /**
     * Filter all values that deliver -1 when applied to the cmp from the given set.
     */
    internal fun <A : Comparable<A>> filterLT(cmp: (A) -> Int, set: Set<A>): Set<A> {
        return when(set) {
            is Tip -> empty()
            is Bin -> {
                if (cmp(set.value) < 0)
                    filterLT(cmp, set.left)
                else if (cmp(set.value) > 0)
                    join(set.value, set.left, filterLT(cmp, set.right))
                else
                    set.left
            }
        }
    }

    /**
     * Trim away all subtrees that surely contain no values between the range lo to hi as delivered
     *   by the cmpLo and cmpHi functions. The returned tree is either empty or the key of the root
     *   is between lo and hi.
     */
    internal fun <A : Comparable<A>> trim(cmpLo: (A) -> Int, cmpHi: (A) -> Int, set: Set<A>): Set<A> {
        return when(set) {
            is Tip -> empty()
            is Bin -> {
                if (cmpLo(set.value) < 0) {
                    if(cmpHi(set.value) > 0)
                        set
                    else
                        trim(cmpLo, cmpHi, set.left)
                } else
                    trim(cmpLo, cmpHi, set.right)
            }
        }
    }

    /**
     * Perform a split but also returns the pivot element that was found in the original set.
     *
     * splitLookup _ Tip = (Tip,Nothing,Tip)
     * splitLookup x (Bin _ y l r)
     *    = case compare x y of
     *        LT -> let (lt,found,gt) = splitLookup x l in (lt,found,join y gt r)
     *        GT -> let (lt,found,gt) = splitLookup x r in (join y l lt,found,gt)
     *        EQ -> (l,Just y,r)
     */
    internal fun <A : Comparable<A>> splitLookup(value: A, set: Set<A>): Triple<Set<A>, Option<A>, Set<A>> {
        return when(set) {
            is Tip -> Triple(empty(), none, empty())
            is Bin -> {
                if(value.compareTo(set.value) < 0) {
                    val split: Triple<Set<A>, Option<A>, Set<A>> = splitLookup(value, set.left)
                    Triple(split.first, split.second, join(set.value, split.third, set.right))
                } else if (value.compareTo(set.value) > 0) {
                    val split: Triple<Set<A>, Option<A>, Set<A>> = splitLookup(value, set.right)
                    Triple(join(set.value, set.left, split.first), split.second, split.third)
                } else
                    Triple(set.left, some(set.value), set.right)
            }
        }
    }

}
