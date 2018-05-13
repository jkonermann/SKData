package com.adt.kotlin.data.immutable.list

/**
 * A class hierarchy defining an immutable list collection. The algebraic data
 *   type declaration is:
 *
 * datatype List[A] = Nil
 *                  | Cons of A * List[A]
 *
 * The implementation mimics functional Lists as found in Haskell. The
 *   member functions and the extension functions mostly use primitive
 *   recursion over the List value constructors. Local tail recursive
 *   functions are commonly used.
 *
 * The documentation uses the notation [...] to represent a list instance.
 *
 * @param A                     the (covariant) type of elements in the list
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.list.List.Nil
import com.adt.kotlin.data.immutable.list.List.Cons
import com.adt.kotlin.data.immutable.option.Option

import com.adt.kotlin.fp.FunctionF

import java.util.stream.Stream
import java.util.stream.StreamSupport
import java.util.Spliterator
import java.util.Spliterators


// Contravariant extension functions:

/**
 * Return a stream over the elements of this list.
 *
 * Examples:
 *   [1, 2, 3, 4].stream().count() = 4
 */
fun <A> List<A>.stream(): Stream<A> {
    val iterator: Iterator<A> = this.iterator()
    val stream: Stream<A> = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
    return stream
}

/**
 * Return a sequence over the elements of this list.
 *
 * Examples:
 *   [1, 2, 3, 4].sequence().count() = 4
 */
fun <A> List<A>.sequence(): Sequence<A> {
    val iterator: Iterator<A> = this.iterator()
    val sequence: Sequence<A> = Sequence{ -> iterator}
    return sequence
}

/**
 * Determine if this list contains the given element.
 *
 * Examples:
 *   [1, 2, 3, 4].contains(4) = true
 *   [1, 2, 3, 4].contains(5) = false
 *   [].contains(4) = false
 *
 * @param x                 search element
 * @return                  true if search element is present, false otherwise
 */
fun <A> List<A>.contains(x: A): Boolean {
    return this.contains{y: A -> (y == x)}
}

/**
 * Count the number of times the parameter appears in this list.
 *
 * Examples:
 *   [1, 2, 3, 4].count(2) = 1
 *   [1, 2, 3, 4].count(5) = 0
 *   [].count(2) = 0
 *   [1, 2, 1, 2, 2].count(2) == 3
 *
 * @param x                 the search value
 * @return                  the number of occurrences
 */
fun <A> List<A>.count(x: A): Int {
    return this.count{y: A -> (y == x)}
}

/**
 * Append the given list on to this list. The size of the result list
 *   equals the sum of the size of this list and the list parameter.
 *   This list is a prefix of the result list and the parameter list
 *   is a suffix of the result list.
 *
 * Examples:
 *   [1, 2].concatenate([3, 4]) = [1, 2, 3, 4]
 *   [1, 2, 3, 4].concatenate([]) = [1, 2, 3, 4]
 *   [].concatenate([1, 2, 3, 4]) = [1, 2, 3, 4]
 *   [].concatenate([]) = []
 *
 * @param xs                existing list
 * @return                  new list of appended elements
 */
fun <A> List<A>.concatenate(xs: List<A>): List<A> = this.append(xs)

/**
 * Append the given list on to this list. The size of the result list
 *   will equal the sum of the sizes of this list and the parameter
 *   list. This list will be a prefix of the result list and the
 *   parameter list will be a suffix of the result list.
 *
 * Examples:
 *   [1, 2, 3].append([4, 5]) = [1, 2, 3, 4, 5]
 *   [1, 2, 3].append([]) = [1, 2, 3]
 *   [].append([3, 4]) = [3, 4]
 *   [1, 2, 3].append([4, 5]).size() = [1, 2, 3].size() + [4, 5].size()
 *   [1, 2, 3].isPrefixOf([1, 2, 3].append([4, 5])) = true
 *   [4, 5].isSuffixOf([1, 2, 3].append([4, 5])) = true
 *
 * @param xs                existing list
 * @return                  new list of appended elements
 */
fun <A> List<A>.append(xs: List<A>): List<A> {
    tailrec
    fun recAppend(ps: List<A>, qs: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.prependTo(qs)
            is Cons -> recAppend(ps.tail(), qs, acc.append(ps.head()))
        }
    }   // recAppend

    return recAppend(this, xs, ListBuffer())
}

/**
 * Append a single element on to this list. The size of the result list
 *   will be one more than the size of this list. The last element in the
 *   result list will equal the appended element. This list will be a prefix
 *   of the result list.
 *
 * Examples:
 *   [1, 2, 3, 4].append(5) = [1, 2, 3, 4, 5]
 *   [1, 2, 3, 4].append(5).size() = 1 + [1, 2, 3, 4].size()
 *   [1, 2, 3, 4].append(5).last() = 5
 *   [1, 2, 3, 4].isPrefix([1, 2, 3, 4].append(5)) = true
 *
 * @param x                 new element
 * @return                  new list with element at end
 */
fun <A> List<A>.append(x: A): List<A> {
    tailrec
    fun recAppend(a: A, ps: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.append(a).toList()
            is Cons -> recAppend(a, ps.tail(), acc.append(ps.head()))
        }
    }   // recAppend

    return recAppend(x, this, ListBuffer<A>())
}

/**
 * Remove the first occurrence of the given element from this list. The result list
 *   will either have the same size as this list (if no such element is present) or
 *   will have the size of this list less one.
 *
 * Examples:
 *   [1, 2, 3, 4].remove(4) = [1, 2, 3]
 *   [1, 2, 3, 4].remove(5) = [1, 2, 3, 4]
 *   [4, 4, 4, 4].remove(4) = [4, 4, 4]
 *   [].remove(4) = []
 *
 * @param x                 element to be removed
 * @return                  new list with element deleted
 */
fun <A> List<A>.remove(x: A): List<A> = this.remove{a: A -> (x == a)}

/**
 * Find the index of the given value, or -1 if absent.
 *
 * Examples:
 *   [1, 2, 3, 4].indexOf(1) = 0
 *   [1, 2, 3, 4].indexOf(3) = 2
 *   [1, 2, 3, 4].indexOf(5) = -1
 *   [].indexOf(2) = -1
 *
 * @param x                 the search value
 * @return                  the index position
 */
fun <A> List<A>.indexOf(x: A): Int {
    return this.indexOf{y -> (y == x)}
}

/**
 * The intersperse function takes an element and intersperses
 *   that element between the elements of this list. If this list
 *   is empty then an empty list is returned. If this list size is
 *   one then this list is returned.
 *
 * Examples:
 *   [1, 2, 3, 4].intersperse(0) = [1, 0, 2, 0, 3, 0, 4]
 *   [1].intersperse(0) = [1]
 *   [].intersperse(0) = []
 *
 * @param separator         separator
 * @return                  new list of existing elements and separators
 */
fun <A> List<A>.intersperse(separator: A): List<A> {
    tailrec
    fun recIntersperse(sep: A, ps: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.toList()
            is Cons -> recIntersperse(sep, ps.tail(), acc.append(sep).append(ps.head()))
        }
    }   // recIntersperse

    return when(this) {
        is Nil -> Nil
        is Cons -> if (this.size() == 1) Cons(this.head(), Nil) else Cons(this.head(), recIntersperse(separator, this.tail(), ListBuffer<A>()))
    }
}

/**
 * A variant of foldLeft that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldLeft1{m -> {n -> m + n}} = 10
 *
 * @param f                 curried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldLeft1(f: (A) -> (A) -> A): A = when(this) {
    is Nil -> throw ListException("foldLeft1: empty list")
    is Cons -> this.tail().foldLeft(this.head(), f)
}

/**
 * A variant of foldLeft that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldLeft1{m, n -> m + n} = 10
 *
 * @param f                 uncurried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldLeft1(f: (A, A) -> A): A = this.foldLeft1(FunctionF.C(f))

/**
 * A variant of foldRight that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldRight1{m -> {n -> m * n}} = 24
 *
 * @param f                 curried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldRight1(f: (A) -> (A) -> A): A = when(this) {
    is Nil -> throw ListException("foldRight1: empty list")
    is Cons -> this.tail().foldRight(this.head(), f)
}

/**
 * A variant of foldRight that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldRight1{m, n -> m * n} = 24
 *
 * @param f                 uncurried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldRight1(f: (A, A) -> A): A = this.foldRight1(FunctionF.C(f))

/**
 * scanLeft1 is a variant of scanLeft that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *  [1, 2, 3, 4].scanLeft1{m -> {n -> m + n}} = [1, 3, 6, 10]
 *  [64, 4, 2, 8].scanLeft1{m -> {n -> m / n}} = [64, 16, 8, 1]
 *  [12].scanLeft1{m -> {n -> m / n}} = [12]
 *  [3, 6, 12, 4, 55, 11].scanLeft{m -> {n -> if (m > n) m else n}} = [3, 6, 12, 12, 55, 55]
 *
 * @param f                 curried binary function
 * @return                  new list
 */
fun <A> List<A>.scanLeft1(f: (A) -> (A) -> A): List<A> = when(this) {
    is Nil -> Nil
    is Cons -> this.tail().scanLeft(this.head(), f)
}

/**
 * scanLeft1 is a variant of scanLeft that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *  [1, 2, 3, 4].scanLeft1{m, n -> m + n} = [1, 3, 6, 10]
 *  [64, 4, 2, 8].scanLeft1{m, n -> m / n} = [64, 16, 8, 1]
 *  [12].scanLeft1{m, n -> m / n} = [12]
 *  [3, 6, 12, 4, 55, 11].scanLeft{m, n -> if (m > n) m else n} = [3, 6, 12, 12, 55, 55]
 *
 * @param f                 binary function
 * @return                  new list
 */
fun <A> List<A>.scanLeft1(f: (A, A) -> A): List<A> = this.scanLeft1(FunctionF.C(f))

/**
 * scanRight1 is a variant of scanRight that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].scanRight1{m -> {n -> m + n}} = [10, 9, 7, 4]
 *   [8, 12, 24, 2].scanRight1{m -> {n -> m / n}} = [8, 1, 12, 2]
 *   [12].scanRight1{m -> {n -> m / n}} = [12]
 *   [3, 6, 12, 4, 55, 11].scanRight1{m -> {n -> if (m > n) m else n}} = [55, 55, 55, 55, 55, 11]
 *
 * @param f                 curried binary function
 * @return                  new list
 */
fun <A> List<A>.scanRight1(f: (A) -> (A) -> A): List<A> = when(this) {
    is Nil -> Nil
    is Cons -> this.init().scanRight(this.last(), f)
}

/**
 * scanRight1 is a variant of scanRight that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].scanRight1{m, n -> m + n} = [10, 9, 7, 4]
 *   [8, 12, 24, 2].scanRight1{m, n -> m / n} = [8, 1, 12, 2]
 *   [12].scanRight1{m, n -> m / n} = [12]
 *   [3, 6, 12, 4, 55, 11].scanRight1{m, n -> if (m > n) m else n} = [55, 55, 55, 55, 55, 11]
 *
 * @param f                 uncurried binary function
 * @return                  new list
 */
fun <A> List<A>.scanRight1(f: (A, A) -> A): List<A> = this.scanRight1(FunctionF.C(f))

/**
 * The stripPrefix function drops this prefix from the given list. It returns
 *   None if the list did not start with this prefix, or Some the
 *   list after the prefix, if it does.
 *
 * Examples:
 *   [1, 2].stripPrefix([1, 2, 3, 4]) = Some([3, 4])
 *   [2, 3, 4].stripPrefix([1, 2]) = None
 *   [].stripPrefix([1, 2, 3, 4]) = Some([1, 2, 3, 4])
 *   [1, 2, 3, 4].stripPrefix([]) = None
 *
 * @param xs                existing list of possible prefix
 * @return                  new list of prefix
 */
fun <A> List<A>.stripPrefix(xs: List<A>): Option<List<A>> {
    tailrec
    fun recStripPrefix(ps: List<A>, qs: List<A>): Option<List<A>> {
        return when(ps) {
            is Nil -> Option.Some(qs)
            is Cons -> {
                when(qs) {
                    is Nil -> Option.None
                    is Cons -> if (ps.head() != qs.head()) Option.None else recStripPrefix(ps.tail(), qs.tail())
                }
            }
        }
    }   // recStripPrefix

    return recStripPrefix(this, xs)
}

/**
 * Interleave this list and the given list, alternating elements from each list.
 *   If either list is empty then an empty list is returned. The first element is
 *   drawn from this list. The size of the result list will equal twice the size
 *   of the smaller list. The elements of the result list are in the same order as
 *   the two original.
 *
 * Examples:
 *   [].interleave([]) = []
 *   [].interleave([3, 4, 5]) = []
 *   [1, 2].interleave([]) = []
 *   [1, 2].interleave([3, 4, 5]) = [1, 3, 2, 4]
 *
 * @param xs                other list
 * @return                  result list of alternating elements
 */
fun <A> List<A>.interleave(xs: List<A>): List<A> {
    tailrec
    fun recInterleave(ps: List<A>, qs: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.toList()
            is Cons -> {
                when(qs) {
                    is Nil -> acc.toList()
                    is Cons -> recInterleave(ps.tail(), qs.tail(), acc.append(ps.head()).append(qs.head()))
                }
            }
        }
    }   // recInterleave

    return recInterleave(this, xs, ListBuffer<A>())
}

/**
 * The isPrefixOf function returns true iff this list is a prefix of the second.
 *
 * Examples:
 *   [1, 2].isPrefixOf([1, 2, 3, 4]) = true
 *   [1, 2, 3, 4].isPrefixOf([1, 2, 3, 4]) = true
 *   [1, 2].isPrefixOf([2, 3, 4]) = false
 *   [1, 2].isPrefixOf([]) = false
 *   [].isPrefixOf([1, 2]) = true
 *   [].isPrefixOf([]) = true
 *
 * @param xs                existing list
 * @return                  true if this list is prefix of given list
 */
fun <A> List<A>.isPrefixOf(xs: List<A>): Boolean {
    tailrec
    fun recIsPrefixOf(ps: List<A>, qs: List<A>): Boolean {
        return when(ps) {
            is Nil -> true
            is Cons -> {
                when(qs) {
                    is Nil -> false
                    is Cons -> if (ps.head() != qs.head()) false else recIsPrefixOf(ps.tail(), qs.tail())
                }
            }
        }
    }   // recIsPrefixOf

    return recIsPrefixOf(this, xs)
}

/**
 * The isSuffixOf function takes returns true iff the this list is a suffix of the second.
 *
 * Examples:
 *   [3, 4].isSuffixOf([1, 2, 3, 4]) = true
 *   [1, 2, 3, 4].isSuffixOf([1, 2, 3, 4]) = true
 *   [3, 4].isSuffixOf([1, 2, 3]) = false
 *   [].isSuffixOf([1, 2, 3, 4]) = true
 *   [1, 2, 3, 4].isSuffixOf([]) = false
 *
 * @param xs                existing list
 * @return                  true if this list is suffix of given list
 */
fun <A> List<A>.isSuffixOf(xs: List<A>): Boolean {
    return this.reverse().isPrefixOf(xs.reverse())
}

/**
 * The isInfixOf function returns true iff this list is a constituent of the argument.
 *
 * Examples:
 *   [2, 3].isInfixOf([]) = false
 *   [2, 3].isInfixOf([1, 2, 3, 4]) = true
 *   [1, 2].isInfixOf([1, 2, 3, 4]) = true
 *   [3, 4].isInfixOf([1, 2, 3, 4]) = true
 *   [].isInfixOf([1, 2, 3, 4]) = true
 *   [3, 2].isInfixOf([1, 2, 3, 4]) = false
 *   [1, 2, 3, 4, 5].isInfixOf([1, 2, 3, 4]) = false
 *
 * @param xs                existing list
 * @return                  true if this list is constituent of second list
 */
fun <A> List<A>.isInfixOf(xs: List<A>): Boolean {
    val isPrefix: (List<A>) -> (List<A>) -> Boolean = {ps -> {qs -> ps.isPrefixOf(qs)}}
    return xs.tails().thereExists(isPrefix(this))
}

/**
 * Return true if this list has the same content as the given list, regardless
 *   of order.
 *
 * Examples:
 *   [1, 2, 3, 4].isPermutationOf([1, 2, 3, 4]) = true
 *   [].isPermutationOf([1, 2, 3, 4]) = true
 *   [].isPermutationOf([]) = true
 *   [1, 2, 3, 4].isPermutationOf([]) = false
 *   [1, 2, 3, 4].isPermutationOf([5, 4, 3, 2, 1]) = true
 *   [5, 4, 3, 2, 1].isPermutationOf([1, 2, 3, 4]) = false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> List<A>.isPermutationOf(ys: List<A>): Boolean = this.forAll{x -> ys.contains(x)}

/**
 * Return true if this list has the same content as the given list, respecting
 *   the order.
 *
 * Examples:
 *   [1, 2, 3, 4].isOrderedPermutationOf([1, 2, 3, 4]) = true
 *   [1, 2, 3, 4].isOrderedPermutationOf([]) = false
 *   [].isOrderedPermutationOf([1, 2, 3, 4]) = true
 *   [].isOrderedPermutationOf([]) = true
 *   [1, 4].isOrderedPermutationOf([1, 2, 3, 4]) = true
 *   [1, 2, 3].isOrderedPermutationOf([1, 1, 2, 1, 2, 4, 3, 4]) = true
 *   [1, 2, 3].isOrderedPermutationOf([1, 1, 3, 1, 4, 3, 3, 4]) = false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> List<A>.isOrderedPermutationOf(ys: List<A>): Boolean {
    tailrec
    fun recIsOrderedPermutationOf(xs: List<A>, ys: List<A>): Boolean {
        return when(xs) {
            is Nil -> true
            is Cons -> {
                val xHead: A = xs.head()
                val xTail: List<A> = xs.tail()
                val index: Int = ys.indexOf(xHead)
                if (index < 0)
                    false
                else
                    recIsOrderedPermutationOf(xTail, ys.drop(1 + index))
            }
        }
    }   // recIsOrderedPermutationOf

    return recIsOrderedPermutationOf(this, ys)
}



// ---------- special lists -------------------------------

/**
 * Translate a list of characters into a string
 *
 * Examples:
 *   ['a', 'b', 'c'].charsToString() = "abc"
 *   [].charsToString() = ""
 *
 * @return                      the resulting string
 */
fun List<Char>.charsToString(): String {
    val buffer: StringBuffer = this.foldLeft(StringBuffer()){res -> {ch -> res.append(ch)}}
    return buffer.toString()
}

/**
 * 'unlines' joins lines after appending a newline to each.
 *
 * Examples:
 *   ["one", "two", "three"].unlines() = "one
 *   two
 *   three"
 *
 * @return                      joined lines
 */
fun List<String>.unlines(): String {
    val buffer: StringBuffer = this.foldLeft(StringBuffer()){res -> {str -> res.append(str).append('\n')}}
    return buffer.toString()
}

/**
 * 'unwords' joins words after appending a space to each.
 *
 * Examples:
 *   ["one", "two", "three"].unwords() = "one two three"
 *
 * @return                      joined words
 */
fun List<String>.unwords(): String {
    val buf: StringBuffer = this.foldLeft(StringBuffer()){res -> {str -> res.append(str).append(' ')}}
    val buffer: StringBuffer = if (this.size() >= 1) buf.deleteCharAt(buf.length - 1) else buf
    return buffer.toString()
}

/**
 * 'and' returns the conjunction of a container of booleans.
 *
 * Examples:
 *   [true, true, true, true].and() = true
 *   [true, true, false, true].and() = false
 *
 * @return                      true, if all the elements are true
 */
fun List<Boolean>.and(): Boolean =
        this.forAll{bool -> (bool == true)}

/**
 * 'and' returns the disjunction of a container of booleans.
 *
 * Examples:
 *   [false, false, true, false].or() = true
 *   [false, false, false, false].or() = false
 *
 * @return                      true, if any of the elements is true
 */
fun List<Boolean>.or(): Boolean =
        this.thereExists{bool -> (bool == true)}

/**
 * The sum function computes the sum of the integers in a list.
 *
 * Examples:
 *   [1, 2, 3, 4].sum() = 10
 *   [].sum() = 0
 *
 * @return                      the sum of all the elements
 */
fun List<Int>.sum(): Int =
        this.foldLeft(0){n, m -> n + m}

/**
 * The sum function computes the sum of the doubles in a list.
 *
 * Examples:
 *   [1.0, 2.0, 3.0, 4.0].sum() = 10.0
 *   [].sum() = 0.0
 *
 * @return                      the sum of all the elements
 */
fun List<Double>.sum(): Double =
        this.foldLeft(0.0){x, y -> x + y}

/**
 * The product function computes the product of the integers in a list.
 *
 * Examples:
 *   [1, 2, 3, 4].product() = 24
 *   [].product() = 1
 *
 * @return                      the product of all the elements
 */
fun List<Int>.product(): Int =
        this.foldLeft(1){n, m -> n * m}

/**
 * The product function computes the product of the doubles in a list.
 *
 * Examples:
 *   [1.0, 2.0, 3.0, 4.0].product() = 24.0
 *   [].product() = 1.0
 *
 * @return                      the product of all the elements
 */
fun List<Double>.product(): Double =
        this.foldLeft(1.0){x, y -> x * y}

/**
 * Find the largest integer in a list of integers. Throws a
 *   ListException if the list is empty.
 *
 * Examples:
 *   [1, 2, 3, 4].max() = 4
 *
 * @return                      the maximum integer in the list
 */
fun List<Int>.max(): Int {
    tailrec
    fun recMax(ps: List<Int>, acc: Int): Int {
        return when(ps) {
            is Nil -> acc
            is Cons -> recMax(ps.tail(), Math.max(acc, ps.head()))
        }
    }   // recMax

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMax(this.tail(), this.head())
}

/**
 * Find the smallest integer in a list of integers. Throws a
 *   ListException if the list is empty.
 *
 * Examples:
 *   [1, 2, 3, 4].min() = 1
 *
 * @return                      the minumum integer in the list
 */
fun List<Int>.min(): Int {
    tailrec
    fun recMin(ps: List<Int>, acc: Int): Int {
        return when(ps) {
            is Nil -> acc
            is Cons -> recMin(ps.tail(), Math.min(acc, ps.head()))
        }
    }   // recMin

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMin(this.tail(), this.head())
}

/**
 * Find the largest double in a list of doubles. Throws a
 *   ListException if the list is empty.
 *
 * Examples:
 *   [1.0, 2.0, 3.0, 4.0].max() = 4.0
 *
 * @return                      the maximum double in the list
 */
fun List<Double>.max(): Double {
    tailrec
    fun recMax(ps: List<Double>, acc: Double): Double {
        return when(ps) {
            is Nil -> acc
            is Cons -> recMax(ps.tail(), Math.max(acc, ps.head()))
        }
    }   // recMax

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMax(this.tail(), this.head())
}

/**
 * Find the smallest double in a list of doubles. Throws a
 *   ListException if the list is empty.
 *
 * Examples:
 *   [1.0, 2.0, 3.0, 4.0].min() = 1.0
 *
 * @return                      the minumum double in the list
 */
fun List<Double>.min(): Double {
    tailrec
    fun recMin(ps: List<Double>, acc: Double): Double {
        return when(ps) {
            is Nil -> acc
            is Cons -> recMin(ps.tail(), Math.min(acc, ps.head()))
        }
    }   // recMin

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMin(this.tail(), this.head())
}
