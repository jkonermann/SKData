package com.adt.kotlin.data.stream

/**
 * Functions on the Stream class. The functions aim to present an idiomatic
 *   Kotlin interface on to the class.
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.data.immutable.list.List
import com.adt.kotlin.data.immutable.list.ListBuffer

import java.util.stream.Stream
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Collector

import java.util.function.Function
import java.util.function.BinaryOperator
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.function.IntSupplier



object StreamF {

    /**
     * Returns a stream of integers starting with the given from value and
     *   ending with the given to value (exclusive).
     *
     * range:: Int * Int * Int -> Stream[Int]
     *
     * @param from                  the minimum value for the stream (inclusive)
     * @param to                    the maximum value for the stream (exclusive)
     * @param step                  increment
     * @return                      the stream of integers from => to (exclusive)
     */
    fun range(from: Int, to: Int, step: Int = 1): IntStream {
        if (step == 0)
            throw IllegalArgumentException("Zero step disallowed: from: $from to: $to step: $step")
        if (step > 0 && from >= to)
            throw IllegalArgumentException("Positive step requires from >= to: from: $from to: $to step: $step")
        if (step < 0 && from <= to)
            throw IllegalArgumentException("Negative step requires from <= to: from: $from to: $to step: $step")

        tailrec
        fun recRange(from: Int, to: Int, step: Int, acc: IntStream.Builder): IntStream {
            return if (step >0 && from >= to)
                acc.build()
            else if (step < 0 && from <= to)
                acc.build()
            else
                recRange(from + step, to, step, acc.add(from))
        }   // recRange

        return recRange(from, to, step, IntStream.builder())
    }

    /**
     * Returns a stream of doubles starting with the given from value and
     *   ending with the given to value (exclusive).
     *
     * range:: Double * Double * Double -> Double[Int]
     *
     * @param from                  the minimum value for the stream (inclusive)
     * @param to                    the maximum value for the stream (exclusive)
     * @param step                  increment
     * @return                      the stream of doubles from => to (exclusive)
     */
    fun range(from: Double, to: Double, step: Double = 1.0): DoubleStream {
        if (Math.abs(step) < 1e-10)
            throw IllegalArgumentException("Zero step disallowed: from: $from to: $to step: $step")
        if (step > 0 && from >= to)
            throw IllegalArgumentException("Positive step requires from >= to: from: $from to: $to step: $step")
        if (step < 0 && from <= to)
            throw IllegalArgumentException("Negative step requires from <= to: from: $from to: $to step: $step")

        tailrec
        fun recRange(from: Double, to: Double, step: Double, acc: DoubleStream.Builder): DoubleStream {
            return if (step >0 && from >= to)
                acc.build()
            else if (step < 0 && from <= to)
                acc.build()
            else
                recRange(from + step, to, step, acc.add(from))
        }   // recRange

        return recRange(from, to, step, DoubleStream.builder())
    }

    /**
     * Returns an infinite stream of integers starting with the given from value.
     *
     * @param from              the start value
     * @param step              the change
     * @return                  infinite stream on integers
     */
    fun range(from: Int, step: Int = 1): IntStream = IntStream.generate(object: IntSupplier{
        override fun getAsInt(): Int {
            val result = next
            next += step
            return result
        }
        var next: Int = from
    })

    /**
     * Convert a variable-length parameter series into an immutable stream.
     *   If no parameters are present then an empty list is produced.
     *
     * fromSequence:: A ... -> Stream[A]
     *
     * @param seq                   variable-length parameter series
     * @return                      immutable stream of the given values
     */
    fun <A> fromSequence(vararg seq: A): Stream<A> = Stream.of(*seq)

    /**
     * Create an empty stream.
     *
     * empty:: Stream[A]
     *
     * @return                      empty stream
     */
    fun <A> empty(): Stream<A> = Stream.empty()
    fun emptyDouble(): DoubleStream = DoubleStream.empty()
    fun emptyInt(): IntStream = IntStream.empty()
    fun emptyLong(): LongStream = LongStream.empty()

    /**
     * Make a stream with one element.
     *
     * singleton:: A -> Stream[A]
     *
     * @param x                     new element
     * @return                      new stream with that one element
     */
    fun <A> singleton(a: A): Stream<A> = Stream.of(a)

    /**
     * Produce a stream with n copies of the element a. Throws a
     *   IllegalArgumentException if the argument n is negative.
     *
     * @param n                     number of copies required
     * @param a                     element to be copied
     * @return                      stream of the copied element
     */
    fun <A> replicate(n: Int, a: A): Stream<A> {
        tailrec
        fun recReplicate(n: Int, a: A, acc: Stream.Builder<A>): Stream<A> {
            return if (n == 0)
                acc.build()
            else
                recReplicate(n - 1, a, acc.add(a))
        }   // recReplicate

        return if (n < 0)
            throw IllegalArgumentException("replicate: number is negative: $n")
        else
            recReplicate(n, a, Stream.builder())
    }

    /**
     * Returns a Collector that accumulates the input elements into a new List.
     */
    fun <A> listCollector(): Collector<A, ListBuffer<A>, List<A>> = object: Collector<A, ListBuffer<A>, List<A>> {
        override fun accumulator(): BiConsumer<ListBuffer<A>, A> = object: BiConsumer<ListBuffer<A>, A> {
            override fun accept(buf: ListBuffer<A>, a: A): Unit {buf.append(a)}
        }
        override fun characteristics(): Set<Collector.Characteristics> = setOf(Collector.Characteristics.UNORDERED)
        override fun combiner(): BinaryOperator<ListBuffer<A>> = object: BinaryOperator<ListBuffer<A>> {
            override fun apply(buf1: ListBuffer<A>, buf2: ListBuffer<A>): ListBuffer<A> {
                val list1: List<A> = buf1.toList()
                val list2: List<A> = buf2.toList()
                val buffer: ListBuffer<A> = list1.foldLeft(ListBuffer()){buf, a -> buf.append(a);buf}
                return list2.foldLeft(buffer){buf, a -> buf.append(a);buf}
            }
        }
        override fun finisher(): Function<ListBuffer<A>, List<A>> = object: Function<ListBuffer<A>, List<A>> {
            override fun apply(buf: ListBuffer<A>): List<A> = buf.toList()
        }
        override fun supplier(): Supplier<ListBuffer<A>> = object: Supplier<ListBuffer<A>> {
            override fun get(): ListBuffer<A> = ListBuffer()
        }
    }

}
