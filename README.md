# SKData
Kotlin persistent data structures

SKData is a library of immutable data structures with which Kotlin developers can
experiment with persistent structures in their codebase. Truly immutable data was
the third most sought after future feature in the latest survey.


The persistent structures are in the immutable folder. The code for each stucture
follows the same convention. For example, the immutable List type is defined across
the files List, ListE and ListF. The List file defines the sealed List class, the
sub-object Nil and the concrete sub-class Cons. The ListE file contains contravariant
extension functions on the List class which would otherwise conflict with the
covariant type parameter of class List. The ListF file comprises list-related
functions that would not be a member of the List hierarchy. One such function is
replicate that delivers a List with repeated copies of a given value. The functions
are packaged in the object declaration ListF.


The libs folder contains skdata.jar and klib.jar. The former is the jar for these
sources. The latter is a small dependent library comprising various functional
capabilities such as function composition, function curry/uncurry, etc.


I have started a series of notes on Programming in Kotlin.

Chapter 13: Functional Data Structures

has been added to the series. It can be used as a user guide/implementation guide
for the persistent data structures. The reading materials can be found at: Kotlin programming notes.

[https://docs.google.com/document/d/1ZDnuTkubfvrjsht3xoZf3L3rQHAMyGt-ZB09SU5xCdk/pub]
