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

import java.util.Optional

import java.util.stream.Stream
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Collector

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.ToDoubleFunction
import java.util.function.ToIntFunction
import java.util.function.ToLongFunction
import java.util.function.BinaryOperator



// ---------- extension functions -------------------------

fun <A> Stream<A>.allMatch(predicate: (A) -> Boolean): Boolean = this.allMatch(Predicate {a -> predicate(a)})
fun <A> Stream<A>.forAll(predicate: (A) -> Boolean): Boolean = this.allMatch(Predicate {a -> predicate(a)})

fun <A> Stream<A>.anyMatch(predicate: (A) -> Boolean): Boolean = this.anyMatch(Predicate {a -> predicate(a)})
fun <A> Stream<A>.thereExists(predicate: (A) -> Boolean): Boolean = this.anyMatch(Predicate {a -> predicate(a)})

fun <A> Stream<A>.thereExistsUnique(predicate: (A) -> Boolean): Boolean = (this.filter(Predicate {a -> predicate(a)}).count() == 1L)

fun <A> Stream<A>.filter(predicate: (A) -> Boolean): Stream<A> = this.filter(Predicate {a -> predicate(a)})

fun <A, B> Stream<A>.flatMap(f: (A) -> Stream<B>): Stream<B> = this.flatMap(Function<A, Stream<B>> {a -> f(a)})
fun <A, B> Stream<A>.bind(f: (A) -> Stream<B>): Stream<B> = this.flatMap(Function<A, Stream<B>> {a -> f(a)})

fun <A> Stream<A>.flatMapToDouble(f: (A) -> DoubleStream): DoubleStream = this.flatMapToDouble(Function<A, DoubleStream> {a -> f(a)})

fun <A> Stream<A>.flatMapToInt(f: (A) -> IntStream): IntStream = this.flatMapToInt(Function<A, IntStream> {a -> f(a)})

fun <A> Stream<A>.flatMapToLong(f: (A) -> LongStream): LongStream = this.flatMapToLong(Function<A, LongStream> {a -> f(a)})

fun <A> Stream<A>.forEach(block: (A) -> Unit): Unit = this.forEach(Consumer {a -> block(a)})

fun <A> Stream<A>.forEachOrdered(block: (A) -> Unit): Unit = this.forEachOrdered(Consumer {a -> block(a)})

fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> = this.map(Function<A, B> {a -> f(a)})

fun <A> Stream<A>.mapToDouble(f: (A) -> Double): DoubleStream = this.mapToDouble(ToDoubleFunction {a -> f(a)})

fun <A> Stream<A>.mapToInt(f: (A) -> Int): IntStream = this.mapToInt(ToIntFunction {a -> f(a)})

fun <A> Stream<A>.mapToLong(f: (A) -> Long): LongStream = this.mapToLong(ToLongFunction {a -> f(a)})

fun <A> Stream<A>.noneMatch(predicate: (A) -> Boolean): Boolean = this.noneMatch(Predicate {a -> predicate(a)})

fun <A> Stream<A>.reduce(f: (A, A) -> A): Optional<A> = this.reduce(BinaryOperator {a1, a2 -> f(a1, a2)})
fun <A> Stream<A>.reduce(f: (A) -> (A) -> A): Optional<A> = this.reduce(BinaryOperator {a1, a2 -> f(a1)(a2)})

fun <A> Stream<A>.reduce(identity: A, f: (A, A) -> A): A = this.reduce(identity, BinaryOperator {a1, a2 -> f(a1, a2)})
fun <A> Stream<A>.reduce(identity: A, f: (A) -> (A) -> A): A = this.reduce(identity, BinaryOperator {a1, a2 -> f(a1)(a2)})



fun <A> Stream<A>.toKList(): List<A> {
    val collector: Collector<A, ListBuffer<A>, List<A>> = StreamF.listCollector()

    return this.collect(collector)
}
