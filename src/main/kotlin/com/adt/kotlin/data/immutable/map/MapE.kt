package com.adt.kotlin.data.immutable.map

/**
 * A class hierarchy defining an immutable map collection. The algebraic data
 *   type declaration is:
 *
 * datatype Map[K, V] = Tip
 *                    | Bin of K * V * Map[K, V] * Map[K, V]
 *
 * Maps are implemented as size balanced binary trees. This implementation
 *   mirrors the Haskell implementation in Data.Map that, in turn, is based
 *   on an efficient balanced binary tree referenced in the sources.
 *
 * This code duplicates much of the implementation for the immutable Set.
 *   Both are based on sized balanced binary trees and a generic Tree should
 *   be developed for both.
 *
 * The type denoted by Map[K, V] is a map of key/value pairs.
 *
 * The Map class is defined generically in terms of the type parameters K and V.
 *
 * @param K                     the type of keys in the map
 * @param V                     the type of values in the map
 *
 * @author	                    Ken Barclay
 * @since                       October 2012
 */

import com.adt.kotlin.fp.FunctionF.C
import com.adt.kotlin.fp.FunctionF.C3
import com.adt.kotlin.fp.FunctionF.constant

import com.adt.kotlin.data.immutable.option.Option
import com.adt.kotlin.data.immutable.option.Option.None
import com.adt.kotlin.data.immutable.option.Option.Some
import com.adt.kotlin.data.immutable.option.OptionF.some

import com.adt.kotlin.data.immutable.map.Map.Tip
import com.adt.kotlin.data.immutable.map.Map.Bin

import kotlin.collections.List as JList



// Contravariant extension functions:

/**
 * Look up the given key in the map. Return defaultValue if absent, otherwise
 *   return the corresponding value.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.lookUpWithDefault("Ken", 99) = 25
 *   <[Jessie: 22, John: 31, Ken: 25]>.lookUpWithDefault("Irene", 99) = 99
 *   <[]>.lookUpWithDefault("Ken", 99) = 99
 *
 * @param key               search key
 * @param defaultValue      default value to use if key is absent
 * @return                  matching value or default if key is absent
 */
fun <K : Comparable<K>, V> Map<K, V>.lookUpWithDefault(key: K, defaultValue: V): V {
    tailrec
    fun recLookUpWithDefault(key: K, defaultValue: V, map: Map<K, V>): V {
        return when(map) {
            is Tip -> defaultValue
            is Bin -> {
                if (key.compareTo(map.key) < 0)
                    recLookUpWithDefault(key, defaultValue, map.left)
                else if (key.compareTo(map.key) > 0)
                    recLookUpWithDefault(key,defaultValue,  map.right)
                else
                    map.value
            }
        }
    }

    return recLookUpWithDefault(key, defaultValue, this)
}

/**
 * Is this a sub-map of right?
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.isSubmapOf(<[Jessie: 22, John: 31, Ken: 25]>) = true
 *   <[]>.isSubmapOf(<[Jessie: 22, John: 31, Ken: 25]>) = true
 *   <[Jessie: 22, John: 31, Ken: 25]>.isSubmapOf(<[]>) = false
 *   <[]>.isSubmapOf(<[]>) = true
 *
 * @param map               existing map
 * @return                  true if this map is a submap of the given map
 */
fun <K : Comparable<K>, V> Map<K, V>.isSubmapOf(map: Map<K, V>): Boolean {
    fun recIsSubmapOf(left: Map<K, V>, right: Map<K, V>): Boolean {
        return when(left) {
            is Tip -> true
            is Bin -> {
                when(right) {
                    is Tip -> false
                    is Bin -> {
                        val split: Triple<Map<K, V>, Boolean, Map<K, V>> = MapF.splitMember(left.key, left.value, right)
                        split.second && recIsSubmapOf(left.left, split.first) && recIsSubmapOf(left.right, split.third)
                    }
                }
            }
        }
    }

    return recIsSubmapOf(this, map)
}

/**
 * Is this a proper submap? (ie. a submap but not equal).
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.isProperSubmapOf(<[Jessie: 22, John: 31, Ken: 25]>) = false
 *   <[]>.isProperSubmapOf(<[Jessie: 22, John: 31, Ken: 25]>) = true
 *   <[Jessie: 22, John: 31, Ken: 25]>.isProperSubmapOf(<[]>) = false
 *   <[]>.isProperSubmapOf(<[]>) = false
 *
 * @param map               existing map
 * @return                  true if this map is a proper submap of the given map
 */
fun <K : Comparable<K>, V> Map<K, V>.isProperSubmapOf(map: Map<K, V>): Boolean = (this.size() < map.size()) && this.isSubmapOf(map)

/**
 * Insert a new key/value pair in the map. If the key is already present in
 *   the tree, then the value is replaced.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.insert(Irene, 99) = <[Jessie: 22, John: 31, Irene: 99, Ken: 25]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.insert(Ken, 99) = <[Jessie: 22, John: 31, Ken: 99]>
 *   <[]>.insert(Ken, 99) = <[Ken: 99]>
 *
 * @param key               new key
 * @param value             matching value
 * @return                  new map with new key/value pair
 */
fun <K : Comparable<K>, V> Map<K, V>.insert(key: K, value: V): Map<K, V> {
    fun recInsert(key: K, value: V, map: Map<K, V>): Map<K, V> {
        return when(map) {
            is Tip -> MapF.singleton(key, value)
            is Bin -> {
                if(key.compareTo(map.key) < 0)
                    MapF.balance(map.key, map.value, recInsert(key, value, map.left), map.right)
                else if (key.compareTo(map.key) > 0)
                    MapF.balance(map.key, map.value, map.left, recInsert(key, value, map.right))
                else
                    MapF.bin(map.key, value, map.left, map.right)
            }
        }
    }

    return recInsert(key, value, this)
}

/**
 * Insert a new key/value pair in the map. If the key is absent then the
 *   the key/value pair are added to the map. If the key does exist then
 *   the pair key/f(newValue)(oldValue) is inserted.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWith(Ken, 100){newV -> {oldV -> oldV + newV}} = <[Jessie: 22, John: 31, Ken: 125]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWith(Irene, 100){newV -> {oldV -> oldV + newV}} = <[Jessie: 22, John: 31, Irene: 100, Ken: 25]>
 *
 * @param key     		    new key
 * @param value   		    new value associated with the key
 * @param f			        curried combining function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.insertWith(key: K, value: V, f: (V) -> (V) -> V): Map<K, V> =
        this.insertWithKey(key, value){_ -> {newV: V -> {oldV: V -> f(newV)(oldV)}}}

/**
 * Insert a new key/value pair in the map. If the key is absent then the
 *   the key/value pair are added to the map. If the key does exist then
 *   the pair key/f(newValue, oldValue) is inserted.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWith(Ken, 100){newV, oldV -> oldV + newV} = <[Jessie: 22, John: 31, Ken: 125]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWith(Irene, 100){newV, oldV -> oldV + newV} = <[Jessie: 22, John: 31, Irene: 100, Ken: 125]>
 *
 * @param key     		    new key
 * @param value   		    new value associated with the key
 * @param f			        combining function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.insertWith(key: K, value: V, f: (V, V) -> V): Map<K, V> = this.insertWith(key, value, C(f))

/**
 * Insert a new key/value pair in the map. If the key is absent then the
 *   the key/value pair are added to the map. If the key does exist then
 *   the pair key/f(key)(newValue)(oldValue) is inserted.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWithKey(Ken, 100){k -> {newV -> {oldV -> k.length + oldV + newV}}} = <[Jessie: 22, John: 31, Ken: 128]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWithKey(Irene, 100){k -> {newV -> {oldV -> k.length + oldV + newV}}} = <[Jessie: 22, John: 31, Irene: 100, Ken: 128]>
 *
 * @param key     		    new key
 * @param value   		    new value associated with the key
 * @param f			        curried combining function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.insertWithKey(key: K, value: V, f: (K) -> (V) -> (V) -> V): Map<K, V> {
    fun recInsertWithKey(key: K, value: V, f: (K) -> (V) -> (V) -> V, map: Map<K, V>): Map<K, V> {
        return when(map) {
            is Tip -> MapF.singleton(key, value)
            is Bin -> {
                if (key.compareTo(map.key) < 0)
                    MapF.balanceLeft(map.key, map.value, recInsertWithKey(key, value, f, map.left), map.right)
                else if (key.compareTo(map.key) > 0)
                    MapF.balanceRight(map.key, map.value, map.left, recInsertWithKey(key, value, f, map.right))
                else
                    MapF.bin(key, f(key)(value)(map.value), map.left, map.right)
            }
        }
    }   // recInsertWithKey

    return recInsertWithKey(key, value, f, this)
}

/**
 * Insert a new key/value pair in the map. If the key is absent then the
 *   the key/value pair are added to the map. If the key does exist then
 *   the pair key/f(key, newValue, oldValue) is inserted.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWithKey(Ken, 100){k, newV, oldV -> k.length + oldV + newV} = <[Jessie: 22, John: 31, Ken: 128]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.insertWithKey(Irene, 100){k, newV, oldV -> k.length + oldV + newV} = <[Jessie: 22, John: 31, Irene: 100, Ken: 128]>
 *
 * @param key     		    new key
 * @param value   		    new value associated with the key
 * @param f			        combining function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.insertWithKey(key: K, value: V, f: (K, V, V) -> V): Map<K, V> = this.insertWithKey(key, value, C3(f))

/**
 * Update the value at the key, if present. If f(v) is None then the
 *   element is deleted. If it is Some(v) then the key is bound to the
 *   new value v.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.update(Ken){v -> if (k == "Ken" && v == 25) some(100) else none} = <[Jessie: 22, John: 31, Ken: 100]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.update(Ken){v -> if (k == "Ken" && v == 99) some(100) else none} = <[Jessie: 22, John: 31]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.update(Irene){v -> if (k == "Ken" && v == 25) some(100) else none} = <[Jessie: 22, John: 31, Ken: 100]>
 *
 * @param key     		    new key
 * @param f			        update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.update(key: K, f: (V) -> Option<V>): Map<K, V> = this.updateWithKey(key){_ -> {v: V -> f(v)}}

/**
 * Update the value at the key if present. If f(k)(v) is None then the
 *   element is deleted. If it is Some(v) then the key is bound to the
 *   new value v.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Ken){k -> {v -> if (k == "Ken" && v == 25) some(100) else none}} = <[Jessie: 22, John: 31, Ken: 100]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Ken){k -> {v -> if (k == "Ken" && v == 99) some(100) else none}} = <[Jessie: 22, John: 31]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Irene){k -> {v -> if (k == "Ken" && v == 25) some(100) else none}} = <[Jessie: 22, John: 31, Ken: 100]>
 *
 * @param key     		    new key
 * @param f			        curried update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.updateWithKey(key: K, f: (K) -> (V) -> Option<V>): Map<K, V> {
    fun recUpdateWithKey(key: K, f: (K) -> (V) -> Option<V>, map: Map<K, V>): Map<K, V> {
        return when(map) {
            is Tip -> MapF.empty()
            is Bin -> {
                if (key.compareTo(map.key) < 0)
                    MapF.balanceRight(map.key, map.value, recUpdateWithKey(key, f, map.left), map.right)
                else if (key.compareTo(map.key) > 0)
                    MapF.balanceLeft(map.key, map.value, map.left, recUpdateWithKey(key, f, map.right))
                else {
                    val opt: Option<V> = f(map.key)(map.value)
                    when(opt) {
                        is None -> MapF.glue(map.left, map.right)
                        is Some -> MapF.bin(map.key, opt.get(), map.left, map.right)
                    }
                }
            }
        }
    }   // recUpdateWithKey

    return recUpdateWithKey(key, f, this)
}

/**
 * Update the value at the key if present. If f(k, v) is None then the
 *   element is deleted. If it is Some(v) then the key is bound to the
 *   new value v.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Ken){k, v -> if (k == "Ken" && v == 25) some(100) else none} = <[Jessie: 22, John: 31, Ken: 100]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Ken){k, v -> if (k == "Ken" && v == 99) some(100) else none} = <[Jessie: 22, John: 31]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.updateWithKey(Irene){k, v -> if (k == "Ken" && v == 25) some(100) else none} = <[Jessie: 22, John: 31, Ken: 100]>
 *
 * @param key     		    new key
 * @param f			        update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.updateWithKey(key: K, f: (K, V) -> Option<V>): Map<K, V> = this.updateWithKey(key, C(f))

/**
 * Update the value at the key if present. If the key is not present
 *   then the original map is returned.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjust(Ken){v -> v + v} = <[Jessie: 22, John: 31, Ken: 50]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjust(Irene){v -> v + v} = <[Jessie: 22, John: 31, Ken: 25]>
 *
 * @param key     		    new key
 * @param f			        update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.adjust(key: K, f: (V) -> V): Map<K, V> = this.adjustWithKey(key){_ -> {v -> f(v)}}

/**
 * Update the value at the key if present. If the key is not present
 *   then the original map is returned.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjustWithKey(Ken){k -> {v -> v + v}} = <[Jessie: 22, John: 31, Ken: 50]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjustWithKey(Irene){k -> {v -> v + v}} = <[Jessie: 22, John: 31, Ken: 25]>
 *
 * @param key     		    new key
 * @param f			        curried update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.adjustWithKey(key: K, f: (K) -> (V) -> V): Map<K, V> = updateWithKey(key){k: K -> {v: V -> some(f(k)(v))}}

/**
 * Update the value at the key if present. If the key is not present
 *   then the original map is returned.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjustWithKey(Ken){k, v -> v + v} = <[Jessie: 22, John: 31, Ken: 50]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.adjustWithKey(Irene){k, v -> v + v} = <[Jessie: 22, John: 31, Ken: 25]>
 *
 * @param key     		    new key
 * @param f			        update function
 * @return        		    updated map
 */
fun <K : Comparable<K>, V> Map<K, V>.adjustWithKey(key: K, f: (K, V) -> V): Map<K, V> = this.adjustWithKey(key, C(f))

/**
 * The union of two maps. It delivers a left-biased union of this
 *   and right, ie it prefers this when duplicate keys are encountered.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.union(<[Irene: 25, Dawn: 31]>) = <[Dawn: 31, Irene: 25, Jessie: 22, John: 31, Ken: 25]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.union(<[Ken: 35, John: 41]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.union(<[]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *   <[]>.union(<[Jessie: 22, John: 31, Ken: 25]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *
 * @param map               existing map
 * @return                  the union of this map and the given map
 */
fun <K : Comparable<K>, V> Map<K, V>.union(map: Map<K, V>): Map<K, V> {
    return when(this) {
        is Tip -> map
        is Bin -> MapF.hedgeUnion(constant(-1), constant(+1), this, map)
    }
}

/**
 * The intersection of two maps. Returns data from this for keys
 *   existing in both maps.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.intersection(<[Irene: 25, Dawn: 31]>) = <[]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.intersection(<[John: 41, Ken: 35]>) = <[John: 31, Ken: 25]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.intersection(<[]>) = <[]>
 *   <[]>.intersection(<[]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *
 * @param map               existing map
 * @return                  the intersection of this map and the given map
 */
fun <K : Comparable<K>, V> Map<K, V>.intersection(map: Map<K, V>): Map<K, V> {
    fun recIntersection(left: Map<K, V>, right: Map<K, V>): Map<K, V> {
        return when(left) {
            is Tip -> MapF.empty()
            is Bin -> when(right) {
                is Tip -> MapF.empty()
                is Bin -> {
                    if (left.key.compareTo(right.key) >= 0) {
                        val split: Triple<Map<K, V>, Option<Pair<K, V>>, Map<K, V>> = MapF.splitLookup(right.key, right.value, left)
                        val leftSet: Map<K, V> = recIntersection(split.first, right.left)
                        val rightSet: Map<K, V> = recIntersection(split.third, right.right)
                        when(split.second) {
                            is None -> MapF.merge(leftSet, rightSet)
                            is Some -> MapF.join(split.second.get().first, split.second.get().second, leftSet, rightSet)
                        }
                    } else {
                        val split: Triple<Map<K, V>, Boolean, Map<K, V>> = MapF.splitMember(left.key, left.value, right)
                        val leftSet: Map<K, V> = recIntersection(left.left, split.first)
                        val rightSet: Map<K, V> = recIntersection(left.right, split.third)
                        if (split.second)
                            MapF.join(left.key, left.value, leftSet, rightSet)
                        else
                            MapF.merge(leftSet, rightSet)
                    }
                }
            }
        }
    }

    return recIntersection(this, map)
}

/**
 * The difference of two maps. Returns elements from this not in the
 *   right map.
 *
 * Examples:
 *   <[Jessie: 22, John: 31, Ken: 25]>.difference(<[Irene: 25, Dawn: 31]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.difference(<[John: 41, Ken: 35]>) = <[Jessie: 22]>
 *   <[Jessie: 22, John: 31, Ken: 25]>.difference(<[]>) = <[Jessie: 22, John: 31, Ken: 25]>
 *   <[]>.difference(<[Jessie: 22, John: 31, Ken: 25]>) = <[]>
 *
 * @param map               existing map
 * @return                  the difference of this map and the given map
 */
fun <K : Comparable<K>, V> Map<K, V>.difference(map: Map<K, V>): Map<K, V> {
    return when(this) {
        is Tip -> MapF.empty()
        is Bin -> when(map) {
            is Tip -> this
            is Bin -> MapF.hedgeDifference(constant(-1), constant(+1), this, map)
        }
    }
}
