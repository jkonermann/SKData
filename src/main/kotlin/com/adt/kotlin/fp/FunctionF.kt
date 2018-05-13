package com.adt.kotlin.fp

object FunctionF {

    // Reverse engineered necessary functions for skdata

    /*

        public final val doubleAdd: (kotlin.Double) -> (kotlin.Double) -> kotlin.Double /* compiled code */

        public final val doubleMul: (kotlin.Double) -> (kotlin.Double) -> kotlin.Double /* compiled code */

        public final val idBoolean: (kotlin.Boolean) -> kotlin.Boolean /* compiled code */

        public final val idDouble: (kotlin.Double) -> kotlin.Double /* compiled code */

        public final val idInt: (kotlin.Int) -> kotlin.Int /* compiled code */

        public final val idLong: (kotlin.Long) -> kotlin.Long /* compiled code */

        public final val idString: (kotlin.String) -> kotlin.String /* compiled code */

        public final val intAdd: (kotlin.Int) -> (kotlin.Int) -> kotlin.Int /* compiled code */

        public final val intMul: (kotlin.Int) -> (kotlin.Int) -> kotlin.Int /* compiled code */

        public final val isEven: (kotlin.Int) -> kotlin.Boolean /* compiled code */

        public final val isOdd: (kotlin.Int) -> kotlin.Boolean /* compiled code */

        public final fun <A, B, C> C(f: (A, B) -> C): (A) -> (B) -> C { /* compiled code */ }

        public final fun <A, B, C> C2(f: (A, B) -> C): (A) -> (B) -> C { /* compiled code */ }

        public final fun <A, B, C, D> C3(f: (A, B, C) -> D): (A) -> (B) -> (C) -> D { /* compiled code */ }

        public final fun <A, B, C, D, E> C4(f: (A, B, C, D) -> E): (A) -> (B) -> (C) -> (D) -> E { /* compiled code */ }

        public final fun <A, B, C> U(f: (A) -> (B) -> C): (A, B) -> C { /* compiled code */ }

        public final fun <A, B, C> U2(f: (A) -> (B) -> C): (A, B) -> C { /* compiled code */ }

        public final fun <A, B, C, D> U3(f: (A) -> (B) -> (C) -> D): (A, B, C) -> D { /* compiled code */ }

        public final fun <A, B, C, D, E> U4(f: (A) -> (B) -> (C) -> (D) -> E): (A, B, C, D) -> E { /* compiled code */ }

        public final fun <A> chainLeft(fs: kotlin.Array<(A) -> A>): (A) -> A { /* compiled code */ }

        public final fun <A> chainRight(fs: kotlin.Array<(A) -> A>): (A) -> A { /* compiled code */ }

        public final fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C { /* compiled code */ }

        public final fun <A, B> constant(a: A): (B) -> A { /* compiled code */ }

        public final fun <A, B, C> flip(f: (A) -> (B) -> C): (B) -> (A) -> C { /* compiled code */ }

        public final fun <A, B, C> flip(f: (A, B) -> C): (B, A) -> C { /* compiled code */ }

        public final fun <A, B, C> forwardCompose(f: (A) -> B, g: (B) -> C): (A) -> C { /* compiled code */ }

        public final fun <A> id(): (A) -> A { /* compiled code */ }

        public final fun <A> identity(): (A) -> A { /* compiled code */ }

        public final fun <A> negate(predicate: (A) -> kotlin.Boolean): (A) -> kotlin.Boolean { /* compiled code */ }

        public final fun pow(m: kotlin.Int, n: kotlin.Int): kotlin.Int { /* compiled code */ }

        public final infix fun <A, B, C> ((A) -> B).fc(g: (B) -> C): (A) -> C { /* compiled code */ }

        public final infix fun <A, B, C> ((B) -> C).o(g: (A) -> B): (A) -> C { /* compiled code */ }

    */

    fun <A, B, C> C(f: (A, B) -> C): (A) -> (B) -> C {
        return { a: A -> { b: B -> f(a, b) } }
    }

    fun <A, B, C, D> C3(f: (A, B, C) -> D): (A) -> (B) -> (C) -> D {
        return { a: A -> { b: B -> { c: C -> f(a, b, c) } } }
    }

    fun <A, B> constant(a: A): (B) -> A {
        return { a }
    }

    fun <A, B, C> U(f: (A) -> (B) -> C): (A, B) -> C {
        return { a: A, b: B -> f(a)(b) }
    }

}