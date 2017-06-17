package com.adt.kotlin.data.list

/**
 * Functions on the List class. The functions aim to
 *   offer an immutable interface. Consequently, they are not
 *   especially efficient.
 *
 * Author       Ken Barclay
 * Date         September 2012
 */

import com.adt.kotlin.data.immutable.option.*
import com.adt.kotlin.fp.FunctionF.C
import com.adt.kotlin.fp.FunctionF.U



object JListF {

    /**
     * Return a list of integers starting with the given from value and
     *   ending with the given to value (exclusive). If from is greater or
     *   equal to the to value, then an empty list is delivered.
     *
     * @param from                  the minimum value for the list (inclusive)
     * @param to                    the maximum value for the list (exclusive)
     * @return                      the list of integers from => to (exclusive)
     */
    fun range(from: Int, to: Int): List<Int> =
            if (from >= to) empty<Int>() else cons(from, range(from + 1, to))

    /**
     * Produce a list with n copies of the element a. If n is zero or negative
     *   then  an empty list is delivered.
     *
     * @param n                     number of copies required
     * @param a                     element to be copied
     * @return                      list of the copied element
     */
    fun <A> replicate(n: Int, a: A): List<A> {
        val result: MutableList<A> = arrayListOf()
        for (k in 1..n) {
            result.add(a)
        }
        return result
    }

    /**
     * shallowFlatten is used to flatten a nested list structure. The
     *   shallowFlatten does not recurse into sub-lists, eg:
     *
     *   shallowFlatten([[1, 2, 3], [4, 5]]) = [1, 2, 3, 4, 5]
     *   shallowFlatten([[[1, 2], [3]], [[4, 5]]]) = [[1, 2], [3], [4, 5]]
     *
     * @param tss                   existing list of lists
     * @return                      new list of flattened list
     */
    fun <A> shallowFlatten(tss: List<List<A>>): List<A> = tss.flatten()

    /**
     * Translate a list of characters into a string.
     *
     * @param chars                 the character list
     * @return                      the resulting string
     */
    fun charsToString(chars: List<Char>): String = chars.joinToString("")

    /**
     * Apply the block to each element in the list.
     *
     * @param xs                    existing immutable list
     * @param block                 body of program block
     */
    fun <A> forEach(xs: List<A>, block: (A) -> Unit) = xs.forEach(block)

    /**
     * Create an empty list.
     *
     * @return                      empty list
     */
    fun <A> empty(): List<A> = listOf()

    /**
     * Create an empty list.
     *
     * @return                      empty list
     */
    fun <A> nil(): List<A> = listOf()

    /**
     * Affix a new element on to the front of the given list.
     *
     * @param a                     new head element
     * @param xs                    existing list
     * @return                      new list
     */
    fun <A> cons(a: A, xs: List<A>): List<A> = listOf(a) + xs

    /**
     * Make a list with one element.
     *
     * @param a                     new element
     * @return                      new list with that one element
     */
    fun <A> singleton(a: A): List<A> = listOf(a)

    /**
     * Test whether the given list is empty.
     *
     * @param xs                    existing list
     * @return                      true if list is empty
     */
    fun <A> isEmpty(xs: List<A>): Boolean = xs.isEmpty()

    /**
     * Obtain the length of the given list.
     *
     * @param xs                    existing list
     * @return                      number of elements in the list
     */
    fun <A> size(xs: List<A>): Int = xs.size

    /**
     * Obtain the length of the given list.
     *
     * @param xs                    existing list
     * @return                      number of elements in the list
     */
    fun <A> length(xs: List<A>): Int = xs.size

    /**
     * Return the element at the specified position in the given list.
     *   Throws a ListException if the index is out of bounds.
     *
     * @param xs                    existing list
     * @param index                 position in list
     * @return                      the element at the specified position in the list
     */
    fun <A> get(xs: List<A>, index: Int): A {
        return if (index < 0)
            throw ListException("List.get: negative index: ${index}")
        else if (index >= xs.size)
            throw ListException("List.get: index out of bounds: ${index}")
        else
            xs[index]
    }

    /**
     * Extract the first element of the given list, which must be non-empty.
     *   Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @return                      the element at the front of the list
     */
    fun <A> head(xs: List<A>): A = xs.first()

    /**
     * Extract the elements after the head of the given list, which must be non-empty.
     *   Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @return                      new list of the tail elements
     */
    fun <A> tail(xs: List<A>): List<A> = xs.drop(1)

    /**
     * Extract the last element of the given list, which must be non-empty.
     *   Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @return                      final element in the list
     */
    fun <A> last(xs: List<A>): A = xs.last()

    /**
     * Return all the elements of the given list except the last one. The list must be non-empty.
     *   Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @return                      new list of the initial elements
     */
    fun <A> init(xs: List<A>): List<A> = xs.take(xs.size - 1)

    /**
     * Determine if the given list contains the given element.
     *
     * @param xs                    existing list
     * @param a                     search element
     * @return                      true if search element is present, false otherwise
     */
    fun <A> contains(xs: List<A>, a: A): Boolean = xs.contains(a)

    /**
     * Determine if the given list contains the element determined by the predicate.
     *
     * @param xs                    existing list
     * @param predicate             search predicate
     * @return                      true if search element is present, false otherwise
     */
    fun <A> contains(xs: List<A>, predicate: (A) -> Boolean): Boolean {
        tailrec
        fun recContains(ps: List<A>, pred: (A) -> Boolean): Boolean {
            return if (ps.size == 0)
                false
            else if (pred(ps[0]))
                true
            else
                recContains(ps.drop(1), pred)
        }

        return recContains(xs, predicate)
    }

    fun <A> contains(predicate: (A) -> Boolean): (List<A>) -> Boolean = {xs -> contains(xs, predicate)}

    /**
     * Find the index of the search value in the given list, or -1 if absent.
     *
     * @param xs                    existing list
     * @param a                     the search value
     * @return                      the index position
     */
    fun <A> indexOf(xs: List<A>, a: A): Int = xs.indexOf(a)

    /**
     * Count the number of times the search parameter appears in the given list.
     *
     * @param xs                    existing list
     * @param a                     the search value
     * @return                      the number of occurrences
     */
    fun <A> count(xs: List<A>, a: A): Int = count(xs){x: A -> (x == a)}

    /**
     * Count the number of times the search value appears in the given list matching the criteria.
     *
     * @param xs                    existing list
     * @param predicate             the search criteria
     * @return                      the number of occurrences
     */
    fun <A> count(xs: List<A>, predicate: (A) -> Boolean): Int = xs.count(predicate)

    fun <A> count(predicate: (A) -> Boolean): (List<A>) -> Int = {xs -> xs.count(predicate)}

    /**
     * Append the second list on to the first list, eg:
     *   concatenate([1, 2, 3], [4, 5]) = [1, 2, 3, 4, 5]
     *
     * @param xs                    existing list
     * @param xs                    existing list to be appended
     * @return                      new list of appended elements
     */
    fun <A> concatenate(xs: List<A>, ys: List<A>): List<A> = xs + ys

    /**
     * Append the second list on to the first list, eg:
     *   append([1, 2, 3], [4, 5]) = [1, 2, 3, 4, 5]
     *
     * @param xs                    existing list
     * @param ys                    existing list to be appended
     * @return                      new list of appended elements
     */
    fun <A> append(xs: List<A>, ys: List<A>): List<A> = xs + ys

    /**
     * Append the single element on to the given list, eg:
     *   append([1, 2, 3], 4) = [1, 2, 3, 4]
     *
     * @param xs                    existing list
     * @param a                     new element
     * @return                      new list with element at end
     */
    fun <A> append(xs: List<A>, a: A): List<A> = xs + a

    /**
     * Remove the first occurrence of the given element from the given list.
     *
     * @param xs                    existing list
     * @param a                     element to be removed
     * @return                      new list with element deleted
     */
    fun <A> remove(xs: List<A>, a: A): List<A> = remove(xs){x: A -> (x == a)}

    /**
     * Remove the first occurrence of the matching element from the given list.
     *
     * @param xs                    existing list
     * @param predicate             search predicate
     * @return                      new list with element deleted
     */
    fun <A> remove(xs: List<A>, predicate: (A) -> Boolean): List<A> {
        tailrec
        fun recRemove(ps: List<A>, pred: (A) -> Boolean, acc: List<A>): List<A> {
            return if (ps.size == 0)
                acc
            else if(pred(ps[0]))
                acc + ps.drop(1)
            else
                recRemove(ps.drop(1), pred, acc + ps[0])
        }

        return recRemove(xs, predicate, listOf<A>())
    }

    fun <A> remove(predicate: (A) -> Boolean): (List<A>) -> List<A> = {xs -> remove(xs, predicate)}

    /**
     * The removeDuplicates function removes duplicate elements from the given list.
     *   In particular, it keeps only the first occurrence of each element.
     *
     * @param xs                    existing list
     * @return                      new list with all duplicates removed
     */
    fun <A> removeDuplicates(xs: List<A>): List<A> {
        fun recRemoveDuplicates(ps: List<A>, acc: List<A>): List<A> {
            return if (ps.size == 0)
                listOf<A>()
            else if (contains(acc, ps[0]))
                recRemoveDuplicates(ps.drop(1), acc)
            else
                cons(ps[0], recRemoveDuplicates(ps.drop(1), cons(ps[0], acc)))
        }

        return recRemoveDuplicates(xs, empty<A>())
    }

    /**
     * The removeAll function removes all the elements from the given list that match
     *   the given criteria.
     *
     * @param xs                    existing list
     * @param predicate		        criteria
     * @return          		    new list with all matching elements removed
     */
    fun <A> removeAll(xs: List<A>, predicate: (A) -> Boolean): List<A> = filter(xs){a: A -> !predicate(a)}

    fun <A> removeAll(predicate: (A) -> Boolean): (List<A>) -> List<A> = {xs -> removeAll(xs, predicate)}

    /**
     * Sort the elements of the given list into ascending order and deliver
     *   the resulting list. The elements are compared using the given
     *   comparator.
     *
     * @param xs                    existing list
     * @param comparator            element comparison function
     * @return                      the sorted list
     */
    fun <A> sort(xs: List<A>, comparator: (A, A) -> Int): List<A> {
        fun recSort(ps: List<A>, comparator: (A, A) -> Int): List<A> {
            if(ps.size == 0)
                return ps
            else {
                val item: A = ps[0]
                val smallerItems: List<A> = filter(ps){x -> (comparator(x, item) < 0)}
                val largerItems: List<A> = filter(ps){x -> (comparator(x, item) > 0)}
                return append(append(recSort(smallerItems, comparator), item), recSort(largerItems, comparator))
            }
        }

        return recSort(xs, comparator)
    }



    // ---------- list transformations ------------------------

    /**
     * Function map applies the function parameter to each item in the given list, delivering
     *   a new list.
     *
     * @param xs                    existing list
     * @param f                     pure function:: A -> B
     * @return                      new list of transformed values
     */
    fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> = xs.map(f)

    fun <A, B> map(f: (A) -> B): (List<A>) -> List<B> = {xs -> xs.map(f)}

    /**
     * Reverses the content of the given list into a new list.
     *
     * @param xs                    existing list
     * @return                      new list of elements reversed
     */
    fun <A> reverse(xs: List<A>): List<A> = xs.reversed()

    /**
     * The intersperse function takes an element and intersperses
     *   that element between the elements of the given list, eg:
     *   intersperse(',', abc') = ['a,' ',', 'b', ',', 'c']
     *
     * @param xs                    existing list
     * @param separator             separator
     * @return                      new list of existing elements and separators
     */
    fun <A> intersperse(xs: List<A>, separator: A): List<A> {
        tailrec
        fun recIntersperse(ps: List<A>, sep: A, acc: List<A>): List<A> {
            return if (ps.size == 0)
                acc
            else
                recIntersperse(ps.drop(1), sep, acc + sep + ps[0])
        }

        return if (xs.size == 0)
            listOf<A>()
        else
            cons(xs[0], recIntersperse(xs.drop(1), separator, listOf<A>()))
    }



// ---------- reducing lists (folds) ----------------------

    /**
     * foldLeft is a higher-order function that folds a binary function into the given
     *   list of values. Effectively:
     *
     *   foldLeft(e, [x1, x2, ..., xn], f) = (...((e f x1) f x2) f...) f xn
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     curried binary function:: B -> A -> B
     * @return                      folded result
     */
    fun <A, B> foldLeft(xs: List<A>, e: B, f: (B) -> (A) -> B): B = xs.fold(e, U(f))

    ////fun <A, B> foldLeft(e: B, f: (B) -> (A) -> B): (List<A>) -> B = {xs -> xs.fold(e, U(f))}

    /**
     * foldLeft is a higher-order function that folds a binary function into the given
     *   list of values. Effectively:
     *
     *   foldLeft(e, [x1, x2, ..., xn], f) = (...((e f x1) f x2) f...) f xn
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     binary function:: B * A -> B
     * @return                      folded result
     */
    fun <A, B> foldLeft(xs: List<A>, e: B, f: (B, A) -> B): B = xs.fold(e, f)

    ////fun <A, B> foldLeft(e: B, f: (B, A) -> B): (List<A>) -> B = {xs -> xs.fold(e, f)}

    /**
     * A variant of foldLeft that has no starting value argument, and thus must
     *   be applied to non-empty lists. Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @param f                     curried binary function:: A -> A -> A
     * @return                      folded result
     */
    fun <A> foldLeft1(xs: List<A>, f: (A) -> (A) -> A): A =
            if(xs.size == 0)
                throw ListException("List.foldLeft1: empty list")
            else
                xs.drop(1).fold(xs[0], U(f))

    fun <A> foldLeft1(f: (A) -> (A) -> A): (List<A>) -> A = {xs -> foldLeft1(xs, f)}

    /**
     * A variant of foldLeft that has no starting value argument, and thus must
     *   be applied to non-empty lists. Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @param f                     binary function:: A * A -> A
     * @return                      folded result
     */
    fun <A> foldLeft1(xs: List<A>, f: (A, A) -> A): A = foldLeft1(xs, C(f))

    ////fun <A> foldLeft1(f: (A, A) -> A): (List<A>) -> A = {xs -> foldLeft1(xs, C(f))}

    /**
     * foldRight is a higher-order function that folds a binary function into the given
     *   list of values. Fold functions can be the implementation for many other
     *   functions. Effectively:
     *
     *   foldRight(e, [x1, x2, ..., xn], f) = x1 f (x2 f ... (xn f e)...)
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     curried binary function:: A -> B -> B
     * @return                      folded result
     */
    fun <A, B> foldRight(xs: List<A>, e: B, f: (A) -> (B) -> B): B  = xs.foldRight(e, U(f))

    ////fun <A, B> foldRight(e: B, f: (A) -> (B) -> B): (List<A>) -> B = {xs -> xs.foldRight(e, U(f))}

    /**
     * foldRight is a higher-order function that folds a binary function into the given
     *   list of values. Fold functions can be the implementation for many other
     *   functions. Effectively:
     *
     *   foldRight(e, [x1, x2, ..., xn], f) = x1 f (x2 f ... (xn f e)...)
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     binary function:: A * B -> B
     * @return                      folded result
     */
    fun <A, B> foldRight(xs: List<A>, e: B, f: (A, B) -> B): B = xs.foldRight(e, f)

    ////fun <A, B> foldRight(e: B, f: (A, B) -> B): (List<A>) -> B = {xs -> xs.foldRight(e, f)}

    /**
     * A variant of foldRight that has no starting value argument, and thus must
     *   be applied to non-empty lists. Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @param f                     curried binary function:: A -> A -> A
     * @return                      folded result
     */
    fun <A> foldRight1(xs: List<A>, f: (A) -> (A) -> A): A =
            if(xs.size == 0)
                throw ListException("List.foldRight1: empty list")
            else
                xs.take(xs.size - 1).foldRight(xs.last(), U(f))

    ////fun <A> foldRight1(f: (A) -> (A) -> A): (List<A>) -> A = {xs -> foldRight1(xs, f)}

    /**
     * A variant of foldRight that has no starting value argument, and thus must
     *   be applied to non-empty lists. Throws a ListException on an empty list.
     *
     * @param xs                    existing list
     * @param f                     binary function:: A * A -> A
     * @return                      folded result
     */
    fun <A> foldRight1(xs: List<A>, f: (A, A) -> A): A = foldRight1(xs, C(f))

    ////fun <A> foldRight1(f: (A, A) -> A): (List<A>) -> A = {xs -> foldRight1(xs, f)}



// ---------- special folds -------------------------------

    /**
     * All the elements of the given list meet some criteria. If the list is empty then
     *    true is returned.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      true if all elements match criteria
     */
    fun <A> forAll(xs: List<A>, predicate: (A) -> Boolean): Boolean = xs.all(predicate)

    fun <A> forAll(predicate: (A) -> Boolean): (List<A>) -> Boolean = {xs -> xs.all(predicate)}

    /**
     * There exists at least one element of the given list that meets some criteria. If
     *   the list is empty then false is returned.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      true if at least one element matches the criteria
     */
    fun <A> thereExists(xs: List<A>, predicate: (A) -> Boolean): Boolean = xs.any(predicate)

    fun <A> thereExists(predicate: (A) -> Boolean): (List<A>) -> Boolean = {xs -> xs.any(predicate)}

    /**
     * There exists only one element of the given list that meets some criteria. If the
     *   list is empty then false is returned.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      true if only one element matches the criteria
     */
    fun <A> thereExistsUnique(xs: List<A>, predicate: (A) -> Boolean): Boolean = (xs.count(predicate) == 1)

    fun <A> thereExistsUnique(predicate: (A) -> Boolean): (List<A>) -> Boolean = {xs -> thereExistsUnique(xs, predicate)}



// ---------- building lists ------------------------------

    /**
     * scanLeft is similar to foldLeft, but returns a list of successively
     *   reduced values from the left, eg:
     *   scanLeft(64, [4, 2, 4], {x -> {y -> x/y}}) = [64, 16, 8, 2]
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     curried binary function
     * @return                      new list
     */
    fun <A, B> scanLeft(xs: List<A>, e: B, f: (B) -> (A) -> B): List<B> {
        tailrec
        fun recScanLeft(ps: List<A>, b: B, g: (B) -> (A) -> B, acc: List<B>): List<B> {
            return if (ps.size == 0)
                acc
            else {
                val bb: B = g(b)(ps[0])
                recScanLeft(ps.drop(1), bb, g, acc + bb)
            }
        }

        return cons(e, recScanLeft(xs, e, f, listOf<B>()))
    }

    fun <A, B> scanLeft(e: B, f: (B) -> (A) -> B): (List<A>) -> List<B> = {xs -> scanLeft(xs, e, f)}

    /**
     * scanLeft is similar to foldLeft, but returns a list of successively
     *   reduced values from the left, eg:
     *   scanLeft(64, [4, 2, 4], {x, y -> x/y}) = [64, 16, 8, 2]
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     binary function
     * @return                      new list
     */
    fun <A, B> scanLeft(xs: List<A>, e: B, f: (B, A) -> B): List<B> = scanLeft(xs, e, C(f))

    fun <A, B> scanLeft(e: B, f: (B, A) -> B): (List<A>) -> List<B> = {xs -> scanLeft(xs, e, f)}

    /**
     * scanLeft1 is a variant of scanLeft that has no starting value argument,
     *   eg: scanLeft1([1, 2, 3, 4], {x -> {y -> x + y}}) = [1, 3, 6, 10]
     *
     * @param xs                    existing list
     * @param f                     curried binary function
     * @return                      new list
     */
    fun <A> scanLeft1(xs: List<A>, f: (A) -> (A) -> A): List<A> {
        return if (xs.size == 0)
            empty<A>()
        else
            scanLeft(xs.drop(1), xs[0], f)
    }

    fun <A> scanLeft1(f: (A) -> (A) -> A): (List<A>) -> List<A> = {xs -> scanLeft1(xs, f)}

    /**
     * scanLeft1 is a variant of scanLeft that has no starting value argument,
     *   eg: scanLeft1([1, 2, 3, 4], {x, y -> x + y}) = [1, 3, 6, 10]
     *
     * @param xs                    existing list
     * @param f                     binary function
     * @return                      new list
     */
    fun <A> scanLeft1(xs: List<A>, f: (A, A) -> A): List<A> = scanLeft1(xs, C(f))

    fun <A> scanLeft1(f: (A, A) -> A): (List<A>) -> List<A> = {xs -> scanLeft1(xs, f)}

    /**
     * scanRight is the right-to-left dual of scanLeft,
     *   eg: scanRight(5, [1, 2, 3, 4], {x -> {y -> x + y}}) = [15, 14, 12, 9, 5]
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     curried binary function
     * @return                      new list
     */
    fun <A, B> scanRight(xs: List<A>, e: B, f: (A) -> (B) -> B): List<B> {
        fun recScanRight(ps: List<A>, b: B, g: (A) -> (B) -> B): List<B> {
            return if (ps.size == 0)
                singleton(b)
            else {
                val qs: List<B> = recScanRight(ps.drop(1), b, g)
                val bb: B = g(ps[0])(qs[0])
                cons(bb, qs)
            }
        }

        return recScanRight(xs, e, f)
    }

    fun <A, B> scanRight(e: B, f: (A) -> (B) -> B): (List<A>) -> List<B> = {xs -> scanRight(xs, e, f)}

    /**
     * scanRight is the right-to-left dual of scanLeft,
     *   eg: scanRight(5, [1, 2, 3, 4], {x, y -> x + y}) = [15, 14, 12, 9, 5]
     *
     * @param xs                    existing list
     * @param e                     initial value
     * @param f                     binary function
     * @return                      new list
     */
    fun <A, B> scanRight(xs: List<A>, e: B, f: (A, B) -> B): List<B> = scanRight(xs, e, C(f))

    fun <A, B> scanRight(e: B, f: (A, B) -> B): (List<A>) -> List<B> = {xs -> scanRight(xs, e, f)}

    /**
     * scanRight1 is a variant of scanRight that has no starting value argument,
     *   eg: scanRight1([1, 2, 3, 4], {x -> {y -> x + y}}) = [10, 9, 7, 4]
     *       scanRight1([8, 12, 24, 2], {x -> {y -> x / y}}) = [8, 1, 12, 2]
     *       scanRight1([12], {x -> {y -> x / y}}) = [12]
     *       scanRight1([1 > 2, 3 > 2, 5 == 5], {x -> {y -> x && y}}) = [false, true, true]
     *
     * @param xs                    existing list
     * @param f                     curried binary function
     * @return                      new list
     */
    fun <A> scanRight1(xs: List<A>, f: (A) -> (A) -> A): List<A> {
        return if (xs.size == 0)
            empty<A>()
        else
            scanRight(xs.take(xs.size - 1), xs.last(), f)
    }

    fun <A> scanRight1(f: (A) -> (A) -> A): (List<A>) -> List<A> = {xs -> scanRight1(xs, f)}

    /**
     * scanRight1 is a variant of scanRight that has no starting value argument,
     *   eg: scanRight1([1, 2, 3, 4], {x, y -> x + y}) = [10, 9, 7, 4]
     *       scanRight1([8, 12, 24, 2], {x, y -> x / y}) = [8, 1, 12, 2]
     *       scanRight1([12], {x, y -> x / y}) = [12]
     *       scanRight1([1 > 2, 3 > 2, 5 == 5], {x, y -> x && y}) = [false, true, true]
     *
     * @param xs                    existing list
     * @param f                     binary function
     * @return                      new list
     */
    fun <A> scanRight1(xs: List<A>, f: (A, A) -> A): List<A> = scanRight1(xs, C(f))

    fun <A> scanRight1(f: (A, A) -> A): (List<A>) -> List<A> = {xs -> scanRight1(xs, f)}



// ---------- extracting sublists -------------------------

    /**
     * Return a new list containing the first n elements from the given list. If n
     *    exceeds the size of the given list, then a copy is returned. If n is
     *    negative or zero, then an empty list is delivered.
     *
     * @param xs                    existing list
     * @param n                     number of elements to extract
     * @return                      new list of first n elements
     */
    fun <A> take(xs: List<A>, n: Int): List<A> = xs.take(n)

    /**
     * Drop the first n elements from the given list and return a list containing the
     *   remainder. If n is negative or zero then the given list is returned. If n is
     *   negative or zero, then an empty list is delivered.
     *
     * @param xs                    existing list
     * @param n                     number of elements to skip
     * @return                      new list of remaining elements
     */
    fun <A> drop(xs: List<A>, n: Int): List<A> = xs.drop(n)

    /**
     * Return a new list that is a sub-list of the given list. The sub-list begins at
     *   the specified from and extends to the element at index to - 1. Thus the
     *   length of the sub-list is to-from. Degenerate slice indices are handled
     *   gracefully: an index that is too large is replaced by the list size, an
     *   upper bound smaller than the lower bound returns an empty list.
     *
     * @param xs                    existing list
     * @param from                  the start index, inclusive
     * @param to                    the end index, exclusive
     * @return                      the sub-list of this list
     */
    fun <A> slice(xs: List<A>, from: Int, to: Int): List<A> = if (to < from) listOf() else xs.drop(from).take(to - from)

    /**
     * Collates the given list into sub-lists of length size. Example:
     *
     * [1, 2, 3, 4, 5, 6].collate(3, 2) == [[1, 2, 3], [3, 4, 5]]
     *
     * @param xs                    existing list
     * @param size                  the length of the sub-lists
     * @param step                  stepping length
     * @return                      list of sub-lists each of the same size
     */
    fun <A> collate(xs: List<A>, size: Int, step: Int = 1): List<List<A>> {
        tailrec
        fun recCollate(ps: List<A>, sz: Int, st: Int, acc: List<List<A>>): List<List<A>> {
            return if (ps.size < sz)
                acc
            else
                recCollate(ps.drop(st), sz, st, acc.plus<List<A>>(ps.take(sz)))
        }

        return recCollate(xs, size, step, listOf<List<A>>())
    }

    /**
     * Delivers a tuple where first element is prefix of the given list of length n and
     *   second element is the remainder of the given list.
     *
     * @param xs                    existing list
     * @param n                     number of elements into first result list
     * @return                      pair of two new lists
     */
    fun <A> splitAt(xs: List<A>, n: Int): Pair<List<A>, List<A>> = Pair(xs.take(n), xs.drop(n))

    /**
     * Function takeWhile takes the leading elements from the given list that matches
     *   some predicate.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      new list of leading elements matching criteria
     */
    fun <A> takeWhile(xs: List<A>, predicate: (A) -> Boolean): List<A> = xs.takeWhile(predicate)

    fun <A> takeWhile(predicate: (A) -> Boolean): (List<A>) -> List<A> = {xs -> xs.takeWhile(predicate)}

    /**
     * Function dropWhile removes the leading elements from the given list that matches
     *   some predicate.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      new list of remaining elements
     */
    fun <A> dropWhile(xs: List<A>, predicate: (A) -> Boolean): List<A> = xs.dropWhile(predicate)

    fun <A> dropWhile(predicate: (A) -> Boolean): (List<A>) -> List<A> = {xs -> xs.dropWhile(predicate)}

    /**
     * span applied to the predicate and the given list xs, returns a tuple where
     *   the first element is longest prefix (possibly empty) of xs of elements
     *   that satisfy predicate and second element is the remainder of the list,
     *   eg: span({ x -> (x < 3) }, [1, 2, 3, 4, 1, 2, 3, 4]) == ([1, 2], [3, 4 ,1, 2, 3, 4])
     *       span({ x -> (x < 9) }, [1, 2, 3]) == ([1, 2, 3], [])
     *       span({ x -> (x < 0) }, [1, 2, 3]) == ([], [1,2,3])
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      pair of two new lists
     */
    fun <A> span(xs: List<A>, predicate: (A) -> Boolean): Pair<List<A>, List<A>> {
        tailrec
        fun recSpan(ps: List<A>, pred: (A) -> Boolean, acc: List<A>): Pair<List<A>, List<A>> {
            return if (ps.size == 0)
                Pair(acc, ps)
            else if (!pred(ps[0]))
                Pair(acc, ps)
            else
                recSpan(ps.drop(1), pred, acc + ps[0])
        }

        return recSpan(xs, predicate, listOf<A>())
    }

    fun <A> span(predicate: (A) -> Boolean): (List<A>) -> Pair<List<A>, List<A>> = {xs -> span(xs, predicate)}

    /**
     * The inits function returns all initial segments of the given list,
     *   shortest first, eg: inits([1, 2, 3]) = [[], [1], [1, 2], [1, 2, 3]]
     *
     * @param xs                    existing list
     * @return                      new list of initial segment sub-lists
     */
    fun <A> inits(xs: List<A>): List<List<A>> {
        tailrec
        fun recInits(ps: List<A>, buf: List<A>, acc: List<List<A>>): List<List<A>> {
            return if (ps.size == 0)
                cons(listOf<A>(), acc)
            else {
                val buff: List<A> = buf + ps[0]
                recInits(ps.drop(1), buff, acc.plus<List<A>>(buff))
            }
        }

        return recInits(xs, listOf<A>(), listOf<List<A>>())
    }

    /**
     * The tails function returns all final segments of the given list,
     *   longest first, eg: tails([1, 2, 3]) = [[1, 2, 3], [2, 3], [3], []]
     *
     * @param xs                    existing list
     * @return                      new list of final segments sub-lists
     */
    fun <A> tails(xs: List<A>): List<List<A>> {
        tailrec
        fun recTails(ps: List<A>, acc: List<List<A>>): List<List<A>> {
            return if (ps.size == 0)
                acc.plus<List<A>>(listOf<A>())
            else
                recTails(ps.drop(1), acc.plus<List<A>>(ps))
        }

        return recTails(xs, listOf<List<A>>())
    }

    /**
     * The stripPrefix function drops the given prefix list from the given list. It returns
     *   None if the list did not start with the prefix given, or Some the
     *   list after the prefix, if it does.
     *
     * @param xs                    existing list
     * @param ys                    existing prefix list
     * @return                      new list of prefix
     */
    /**********fun <A> stripPrefix(xs: List<A>, ys: List<A>): OptionIF<List<A>> {
        tailrec
        fun recStripPrefix(ps: List<A>, qs: List<A>): OptionIF<List<A>> {
            return if (ps.size == 0)
                Option.Some(qs)
            else if (qs.size == 0)
                Option.None
            else if (ps[0] != qs[0])
                Option.None
            else
                recStripPrefix(ps.drop(1), qs.drop(1))
        }

        return recStripPrefix(xs, ys)
    }
    **********/

    /**
     * Interleave the given list and the other list, alternating elements from each list.
     *   If either list is empty then an empty list is returned.
     *   The first element is drawn from the given list.
     *
     * @param xs                    existing list
     * @param ys                    other list
     * @return                      result list
     */
    fun <A> interleave(xs: List<A>, ys: List<A>): List<A> {
        tailrec
        fun recInterleave(ps: List<A>, qs: List<A>, acc: List<A>): List<A> {
            return if (ps.size == 0)
                acc
            else if (qs.size == 0)
                acc
            else
                recInterleave(ps.drop(1), qs.drop(1), acc + ps[0] + qs[0])
        }

        return recInterleave(xs, ys, listOf<A>())
    }

    /**
     * Sorts all the given list members into groups determined by the supplied mapping
     *   function and counts the group size.  The function should return the key that each
     *   item should be grouped by.  The returned List of Pairs will have an entry
     *   for each distinct key returned from the function, with each value being the frequency of
     *   items occurring for that group.
     *
     * @param xs                    existing list
     * @param mapping      		    the mapping function
     * @return			            list of sub-lists as the counted groups
     */
    /**********fun <A, K> countBy(xs: List<A>, mapping: (A) -> K): List<Pair<K, Int>> {
        fun count(k: K, bs: List<Pair<K, Int>>): List<Pair<K, Int>> {
            val opt: OptionIF<Pair<K, Int>> = find(bs){pr: Pair<K, Int> -> (k == pr.first)}
            if(opt.isEmpty())
                return cons(Pair(k, 1), bs)
            else {
                val pr: Pair<K, Int> = opt.get()
                val bss: List<Pair<K, Int>> = bs.filter{pr: Pair<K, Int> -> (k != pr.first)}
                return cons(Pair(k, 1 + pr.second), bss)
            }
        }

        tailrec
        fun recCountBy(mapping: (A) -> K, ps: List<A>, bs: List<Pair<K, Int>>): List<Pair<K, Int>> {
            return if (ps.size == 0)
                bs
            else {
                val x: A = ps[0]
                val k: K = mapping(x)
                val counted: List<Pair<K, Int>> = count(k, bs)
                recCountBy(mapping, ps.drop(1), counted)
            }
        }

        return recCountBy(mapping, xs, listOf<Pair<K, Int>>())
    }
    **********/

    /**
     * Function groupBy chops the given list into chunks where each chunk is a contiguous
     *   run of elements having the same key. Strictly, the predicate should be an
     *   equivalence relation.
     *
     * @param xs                    existing list
     * @param condition		        curried grouping condition
     * @return			            list of sub-lists as the groups
     */
    fun <A> groupBy(xs: List<A>, condition: (A) -> (A) -> Boolean): List<List<A>> {
        fun recGroupBy(ps: List<A>, cond: (A) -> (A) -> Boolean): List<List<A>> {
            return if (ps.size == 0)
                listOf<List<A>>()
            else {
                val pr: Pair<List<A>, List<A>> = span(ps.drop(1), cond(ps[0]))
                return cons(cons(ps[0], pr.first), recGroupBy(pr.second, cond))
            }
        }   // recGroupBy

        return recGroupBy(xs, condition)
    }

    /**
     * Function groupBy chops the given list into chunks where each chunk is a contiguous
     *   run of elements having the same key. Strictly, the predicate should be an
     *   equivalence relation.
     *
     * @param xs                    existing list
     * @param condition		        grouping condition
     * @return			            list of sub-lists as the groups
     */
    fun <A> groupBy(xs: List<A>, condition: (A, A) -> Boolean): List<List<A>> = groupBy(xs, C(condition))

    /**
     * The group function takes the given list and returns a list of lists such that the
     *   concatenation of the result is equal to the given list.  Moreover, each
     *   sublist in the result contains only equal elements.
     *
     * @param xs                    existing list
     * @return			            list of sub-lists as the groups
     */
    fun <A> group(xs: List<A>): List<List<A>> = groupBy(xs){x: A -> {y: A -> (x == y)}}



// ---------- predicates ----------------------------------

    /**
     * The isPrefixOf function returns true iff the first list is a prefix of the second list.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @return                      true if the first list is prefix of second list
     */
    fun <A> isPrefixOf(xs: List<A>, ys: List<A>): Boolean {
        tailrec
        fun recIsPrefixOf(ps: List<A>, qs: List<A>): Boolean {
            return if (ps.size == 0)
                true
            else if (qs.size == 0)
                false
            else if (ps[0] != qs[0])
                false
            else
                recIsPrefixOf(ps.drop(1), qs.drop(1))
        }

        return recIsPrefixOf(xs, ys)
    }

    /**
     * The isSuffixOf function takes returns true iff the first list is a suffix of the second list.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @return                      true if this list is suffix of given list
     */
    fun <A> isSuffixOf(xs: List<A>, ys: List<A>): Boolean = isPrefixOf(reverse(xs), reverse(ys))

    /**
     * The isInfixOf function returns true iff the first list is a constituent of the second list.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @return                      true if the first list is constituent of second list
     */
    fun <A> isInfixOf(xs: List<A>, ys: List<A>): Boolean {
        val isPrefixC: (List<A>) -> (List<A>) -> Boolean = {ps -> {qs -> isPrefixOf(ps, qs)}}
        return thereExists(tails(ys), isPrefixC(xs))
    }



// ---------- searching with a predicate ------------------

    /**
     * The find function takes a predicate and returns the first
     *   element in the given list matching the predicate, or None if there is no
     *   such element.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      matching element, if found
     */
    /**********fun <A> find(xs: List<A>, predicate: (A) -> Boolean): OptionIF<A> {
        val a: A? = xs.find(predicate)
        return if (a == null) Option.None else Option.Some(a)
    }
    **********/

    //////////fun <A> find(predicate: (A) -> Boolean): (List<A>) -> OptionIF<A> = {xs -> find(xs, predicate)}

    /**
     * Function filter selects the items from the given list that match the criteria specified
     *   by the function parameter. This is known as a predicate function, and
     *   delivers a boolean result.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      new list of matching elements
     */
    fun <A> filter(xs: List<A>, predicate: (A) -> Boolean): List<A> = xs.filter(predicate)

    fun <A> filter(predicate: (A) -> Boolean): (List<A>) -> List<A> = {xs -> xs.filter(predicate)}

    /**
     * The partition function takes a predicate and returns the pair
     *   of lists of elements which do and do not satisfy the predicate.
     *
     * @param xs                    existing list
     * @param predicate             criteria
     * @return                      pair of new lists
     */
    fun <A> partition(xs: List<A>, predicate: (A) -> Boolean): Pair<List<A>, List<A>> {
        val select: ((A) -> Boolean) -> (A) -> (Pair<List<A>, List<A>>) -> Pair<List<A>, List<A>> = {predicate ->
            {x ->
                {pair ->
                    if (predicate(x)) {
                        val cons: List<A> = cons(x, pair.first)
                        Pair(cons, pair.second)
                    } else {
                        val cons: List<A> = cons(x, pair.second)
                        Pair(pair.first, cons)
                    }
                }
            }
        }
        return foldRight(xs, Pair(listOf<A>(), listOf<A>()), select(predicate))
    }

    fun <A> partition(predicate: (A) -> Boolean): (List<A>) -> Pair<List<A>, List<A>> = {xs -> partition(xs, predicate)}



// ---------- zipping -------------------------------------

    /**
     * zip returns a list of corresponding pairs from the given list and the second list.
     *   If one input list is shorter, excess elements of the longer list are discarded.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @return                      new list of pairs
     */
    fun <A, B> zip(xs: List<A>, ys: List<B>): List<Pair<A, B>> {
        tailrec
        fun recZip(ps: List<A>, qs: List<B>, acc: List<Pair<A, B>>): List<Pair<A, B>> {
            return if (ps.size == 0)
                acc
            else if (qs.size == 0)
                acc
            else
                recZip(ps.drop(1), qs.drop(1), acc + Pair(ps[0], qs[0]))
        }

        return recZip(xs, ys, listOf<Pair<A, B>>())
    }

    /**
     * zipWith generalises zip by zipping with the function given as the first argument,
     *   instead of a tupling function. For example, zipWith (+) is applied to two lists
     *   to produce the list of corresponding sums.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @param f                     curried binary function
     * @return                      new list of function results
     */
    fun <A, B, C> zipWith(xs: List<A>, ys: List<B>, f: (A) -> (B) -> C): List<C> {
        tailrec
        fun recZipWith(ps:List<A>, qs: List<B>, acc: List<C>, g: (A) -> (B) -> C): List<C> {
            return if (ps.size == 0)
                acc
            else if (qs.size == 0)
                acc
            else
                recZipWith(ps.drop(1), qs.drop(1), acc + g(ps[0])(qs[0]), g)
        }

        return recZipWith(xs, ys, listOf<C>(), f)
    }

    /**
     * zipWith generalises zip by zipping with the function given as the first argument,
     *   instead of a tupling function. For example, zipWith (+) is applied to two lists
     *   to produce the list of corresponding sums.
     *
     * @param xs                    existing list
     * @param ys                    existing second list
     * @param f                     binary function
     * @return                      new list of function results
     */
    fun <A, B, C> zipWith(xs: List<A>, ys: List<B>, f: (A, B) -> C): List<C> = zipWith(xs, ys, C(f))

    /**
     * Zips the given list with the index of its element as a pair.
     *
     * @param xs                    existing list
     * @return                      a new list with the same length as this list
     */
    fun <A> zipWithIndex(xs: List<A>): List<Pair<A, Int>> = zip(xs, range(0, xs.size))

    /**
     * Transform a list of pairs into a list of first components and a list of second components.
     *
     * @param xs                    list of pairs
     * @return                      pair of lists
     */
    fun <A, B> unzip(xs: List<Pair<A, B>>): Pair<List<A>, List<B>> =
            foldRight(xs, Pair(listOf<A>(), listOf<B>()), {pr: Pair<A, B> -> {prs: Pair<List<A>, List<B>> -> Pair(cons(pr.first, prs.first), cons(pr.second, prs.second))}})



// ---------- typeclass functions ---------------------------

    /**
     * Function fmap applies the function parameter to each item in the list,
     *   delivering a new list.
     *
     * @param f                     pure function:: A -> B
     * @param xs                    existing list
     * @return                      new list of transformed values
     */
    fun <A, B> fmap(xs: List<A>, f: (A) -> B): List<B> = xs.map(f)

    /**
     * Put the value into a List computational context.
     *
     * @param a                     simple value to be wrapped
     * @return                      wrapped value
     */
    fun <A> pure(a: A): List<A> = listOf(a)

    /** (applicative functor)
     * Function application in a List computational context.
     *
     * @param xs                    existing list
     * @param fs                    function in a list computational context
     * @return                      apply wrapped function to the elements of the list
     */
    fun <A, B> ap(xs: List<A>, fs: List<(A) -> B>): List<B> =
            bind(fs){f -> bind(xs){x -> singleton(f(x))}}

    /**
     * Put the value into a List computational context.
     *
     * @param a                     simple value to be wrapped
     * @return                      wrapped value
     */
    fun <A> inject(a: A): List<A> = listOf(a)

    /**
     * Sequentially compose two actions, passing any value produced by the
     *   first as an argument to the second.
     *
     * @param xs                    existing list
     * @param f   	                list of functions:: A -> List[B]
     * @return        	            wrapped result of function application
     */
    fun <A, B> bind(xs: List<A>, f: (A) -> List<B>): List<B> {
        return shallowFlatten(xs.map(f))
    }

    fun <A, B> flatMap(xs: List<A>, f: (A) -> List<B>): List<B> = bind(xs, f)

    /**
     * Sequentially compose two actions, discarding any value produced by the first;
     *   like sequencing operators (such as the semicolon) in imperative languages.
     *
     * @param xs    		        existing list
     * @param vs    		        existing list
     * @return      		        wrapped result of function application
     */
    fun <A, B> andThen(xs: List<A>, vs: List<B>): List<B> = bind(xs){x -> vs}



// ---------- utility functions ---------------------------

    /**
     * Promote a pure unary function to a function over list functors.
     *
     * @param f    		            pure unary function
     * @return      		        the function over monadic options
     */
    fun <A, B> lift(f: (A) -> B): (List<A>) -> List<B> = {xs -> fmap(xs, f)}

    /**
     * Promote a pure unary function to a function over list applicatives.
     *
     * @param f    		            pure unary function
     * @return      		        the function over monadic options
     */
    fun <A, B> liftA(f: (A) -> B): (List<A>) -> List<B> = {xs -> ap(xs, pure(f))}

    /**
     * Promote a pure binary function to a function over two list applicatives.
     *
     * @param f     		        pure binary function
     * @return      		        the function over monadic options
     */
    fun <A, B, C> liftA2(f: (A) -> (B) -> C): (List<A>) -> (List<B>) -> List<C> = {xs -> {ys -> ap(ys, fmap(xs, f))}}

    /**
     * Promote a pure ternary function to a function over three list applicatives.
     *
     * @param f     		        pure binary function
     * @return      		        the function over monadic options
     */
    fun <A, B, C, D> liftA3(f: (A) -> (B) -> (C) -> D): (List<A>) -> (List<B>) -> (List<C>) -> List<D> = {xs -> {ys -> {zs -> ap(zs, ap(ys, fmap(xs, f)))}}}

    /**
     * sequenceA combines a list of applicative lists into one applicative
     *    that has a list of the results inside it.
     *
     * @param bs    		        list of lists
     * @return      		        list of lists
     */
    fun <A> sequenceA(bs: List<List<A>>): List<List<A>> {
        return if (bs.isEmpty())
            pure(listOf<A>())
        else {
            val consC: (A) -> (List<A>) -> List<A> = {x -> {xs -> cons(x, xs)}}
            foldRight(bs, pure(listOf<A>()), liftA2(consC))
        }
    }

    /**
     * Promote a pure unary function to a function over list monads.
     *
     * @param f    		            pure unary function
     * @return      		        the function over monadic options
     */
    fun <A, B> liftM(f: (A) -> B): (List<A>) -> List<B> = {xs -> fmap(xs, f)}

    /**
     * Promote a pure binary function to a function over two list monads.
     *
     * @param f     		        pure binary function
     * @return      		        the function over monadic options
     */
    fun <A, B, C> liftM2(f: (A) -> (B) -> C): (List<A>) -> (List<B>) -> List<C> = {xs -> {ys -> bind(xs){x -> bind(ys){y -> inject(f(x)(y))}}}}

    /**
     * This generalizes the list-based 'filter' function.
     *
     * @param predicate			    predicate
     * @param xs			        list of values
     * @return			            monadic list of results
     */
    fun <A> filterM(xs: List<A>, predicate: (A) -> List<Boolean>): List<List<A>> {
        return if(xs.isEmpty())
            inject(listOf<A>())
        else
            bind(predicate(xs[0])){b -> bind(filterM(tail(xs), predicate)){ls -> inject(if (b) cons(xs[0], ls) else ls)}}
    }

    /**
     * The foldM function is analogous to foldLeft, except that its result is
     *    encapsulated in a monad.
     *
     */
    fun <A, B> foldM(e: B, xs: List<A>, f: (B) -> (A) -> List<B>): List<B> {
        return if (xs.isEmpty())
            inject(e)
        else
            bind(f(e)(xs[0])){y -> foldM(y, tail(xs), f)}
    }

    /**
     * mapM takes a monadic function mf and applies it to each element in list xs; the result is a list inside a list.
     *
     * @param mf                    monadic function
     * @param xs                    list of values
     * @return                      monadic list of differing values
     */
    fun <A, B> mapM(xs: List<A>, mf: (A) -> List<B>): List<List<B>> = sequenceM(xs.map(mf))

    /**
     * sequenceM combines a list of options into one applicative
     *    that has a list of the results of those options inside it.
     *
     * sequenceM:: [[X]] -> [[X]]
     *
     * @param bs    		        list of lists
     * @return      		        list of lists
     */
    fun <A> sequenceM(bs: List<List<A>>): List<List<A>> {
        return if (bs.isEmpty())
            throw Exception("sequenceM: empty list")
        else {
            val mconsC: (List<A>) -> (List<List<A>>) -> List<List<A>> = {p -> {q -> bind(p){x -> bind(q){y -> inject(cons(x, y))}}}}
            foldRight(bs, inject(listOf<A>()), mconsC)
        }
    }



// ---------- implementation ------------------------------

    /**
     * Make a copy of the list. The sub-list begins at the specified start index (inclusive)
     * and extends to the given end index (exclusive).
     *
     * @param start                 the beginning index
     * @param end                   the last index
     * @return                      the specified sub-list
     */
    private fun <A> sublist(list: List<A>, start: Int, end: Int): List<A> {
        if (start < 0)
            throw ListException("sublist: negative start index: ${start}")
        //if (end <= start)
        //    throw ListException("sublist: empty list")
        val result: MutableList<A> = arrayListOf()
        return if (end <= start)
            result
        else {
            val size: Int = list.size
            for(k in start..(end - 1))
                if (k < size)
                    result.add(list.get(k))
            result
        }
    }

    /**
     * Make a copy of the list. The sub-list begins at the specified start index (inclusive)
     * and extends to the end of the given list.
     *
     * @param start                 the beginning index
     * @return                      the specified sub-list
     */
    private fun <A> sublist(list: List<A>, start: Int): List<A> = sublist(list, start, list.size)

}

