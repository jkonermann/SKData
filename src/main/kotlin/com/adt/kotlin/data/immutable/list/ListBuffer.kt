package com.adt.kotlin.data.immutable.list

/**
 * A buffered implementation of a list.
 *
 * This is achieved by manipulating the list pointers. It supports constant time
 *   prepend and append operations.  Most other operations are linear.
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */



class ListBuffer<A> : ListBufferIF<A> {

    /**
     * Clear the buffer's content.
     */
    override fun clear() {
        start = List.Nil
        exported = false
        len = 0
    }

    /**
     * The current length of the buffer
     *
     * @return                   number of elements in the buffer
     */
    override fun length(): Int = len

    /**
     * Convert this buffer to a list. The operation takes constant
     *   time.
     *
     * @return                  the buffer content as a list
     */
    override fun toList(): List<A> {
        exported = !start.isEmpty()
        return start
    }

    /**
     * Determine if the buffer contains the given element.
     *
     * @param t                 search element
     * @return                  true if search element is present, false otherwise
     */
    override fun contains(t: A): Boolean {
        var found = false
        var cursor: List<A> = start
        while (!found && !cursor.isEmpty()) {
            if (t == cursor.head())
                found = true
            else
                cursor = cursor.tail()
        }
        return found
    }

    /**
     * Prepend a single element to this buffer. The operation takes
     *   constant time.
     *
     * @param t                 element to prepend
     * @return                  this buffer
     */
    override fun prepend(t: A): ListBufferIF<A> {
        if(exported)
            this.copy()
        val newStart: List.Cons<A> = List.Cons(t, start)
        if(start.isEmpty())
            lastCons = newStart
        start = newStart
        len++
        return this
    }

    /**
     * Prepend all the elements from the parameter to this buffer.
     *
     * @param xs                elements to append
     * @return                  this buffer
     */
    override fun prepend(xs: List<A>): ListBufferIF<A> {
        if (exported)
            this.copy()
        var elements: List<A> = xs.reverse()
        len += elements.length()
        while (!elements.isEmpty()) {
            val newElem: List.Cons<A> = List.Cons(elements.head(), start)
            if (start.isEmpty())
                lastCons = newElem
            start = newElem
            elements = elements.tail()
        }

        return this
    }

    /**
     * Prepend the elements of this buffer to the given list.
     *
     * @param ts                existing list
     * @return                  new list with the concatenated elements of this buffer
     */
    override fun prependTo(ts: List<A>): List<A> {
        if (start.isEmpty())
            return ts
        else if (ts.isEmpty()) {
            return this.toList()
        } else {
            if (exported)
                this.copy()
            val lastCons1: List.Cons<A> = lastCons!!
            lastCons1.tl = ts
            return this.toList()
        }
    }

    /**
     * Append a single element to this buffer. The operation takes
     *   constant time.
     *
     * @param t                 element to append
     * @return                  this buffer
     */
    override fun append(t: A): ListBufferIF<A> {
        if(exported)
            this.copy()
        if (start.isEmpty()) {
            lastCons = List.Cons(t, List.Nil)
            start = lastCons!!
        } else {
            val lastCons1: List.Cons<A> = lastCons!!
            lastCons = List.Cons(t, List.Nil)
            lastCons1.tl = lastCons!!
        }
        len++
        return this
    }

    /**
     * Remove the given element from the buffer if present. May take time linear
     *   in the size of the buffer.
     *
     * @param t                 element to remove
     * @return                  this buffer
     */
    override fun remove(t: A): ListBufferIF<A> {
        if(exported)
            this.copy()
        if (start.isEmpty()) {
            // do nothing
        } else if (t == start.head()) {
            start = start.tail()
            len--
        } else {
            var cursor: List<A> = start
            while (!cursor.tail().isEmpty() && cursor.tail().head() != t) {
                cursor = cursor.tail()
            }
            if (!cursor.tail().isEmpty()) {
                val cTail: List<A> = cursor.tail()
                val cursorCons: List.Cons<A> = cursor as List.Cons<A>
                if(cursorCons.tl == lastCons)
                    lastCons = cursorCons
                cursorCons.tl = cTail.tail()
                len--
            }
        }
        return this
    }



// ---------- implementation ------------------------------

    /**
     * Copy the contents of this buffer.
     */
    private fun copy() {
        var cursor: List<A> = start
        val limit: List<A>? = lastCons?.tl
        this.clear()
        while (cursor != limit) {
            val consCursor: List.Cons<A> = cursor as List.Cons<A>
            this.append(consCursor.hd)
            cursor = consCursor.tl
        }
    }



// ---------- properties ----------------------------------

    private var len: Int = 0                    // length of the buffer
    private var exported: Boolean = false       // has the list been copied?
    private var start: List<A> = List.Nil       // the list under construction
    private var lastCons: List.Cons<A>? = null  // the final Cons instance

}
