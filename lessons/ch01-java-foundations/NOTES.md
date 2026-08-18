# Chapter 1 — Java Version Notes

This optional reference keeps language history out of the main lesson flow. Java 8 is the realistic legacy baseline, Java 5 is the deeper compatibility tier, and Java 1.x is historical context.

## Records

Records became final in Java 16 after previews in Java 14 and 15.

Before records, a value carrier required fields, a constructor, accessors, `equals`, `hashCode`, and `toString`. On Java 8, write that class explicitly or generate it in the IDE.

Records were added because data-only classes repeated mechanical code and mistakes in equality methods were common. Their restricted shape communicates that the values are the identity.

## Switch expressions and arrows

Switch expressions became final in Java 14 after previews in Java 12 and 13.

Traditional Java 8 code uses colon labels, `break`, and a variable assigned inside each branch. Arrow cases prevent accidental fall-through and can return a value directly.

The Chapter 1 menu may use either statement form, but every selection must dispatch once and accidental fall-through is rejected by Checkstyle.

## Text blocks

Text blocks became final in Java 15 after previews in Java 13 and 14.

Before them, multiline output used concatenated string literals with explicit `\n` characters. Java 8 code must use that form.

Text blocks improve readable exact-output contracts, but their indentation and trailing newline still matter in tests.

## Pattern matching for switch

Pattern matching for switch became final in Java 21.

The Chapter 1 menu does not require type-pattern dispatch. Keeping menu dispatch to integer cases makes the boundary easier to read and backport.

## Stream `toList`

`Stream.toList()` arrived in Java 16 and returns an unmodifiable list.

Java 8 uses `.collect(Collectors.toList())`, which commonly returns a mutable list. That mutability difference matters when callers expect to append values.

## Optional, streams, lambdas, and `java.time`

All arrived in Java 8.

Before `Optional`, missing results usually returned null. Before streams and lambdas, collection transformations used loops or anonymous classes. Before `java.time`, applications used mutable `Date`, `Calendar`, and non-thread-safe `SimpleDateFormat`.

Java kept the older APIs for compatibility, so modern code gained new packages and types instead of silently changing old behavior.

## URI

`java.net.URI` has existed since Java 1.4.

The class parses URI syntax but does not decide an application's policy. The capstone additionally requires an absolute `http` or `https` scheme because syntactically valid values such as `mailto:x@example.com` are not job links.

## Standard charsets

`StandardCharsets` arrived in Java 7.

Before it, code passed charset names such as `"UTF-8"` and handled the checked `UnsupportedEncodingException`. Named constants removed spelling mistakes and made guaranteed JVM charsets explicit.

## Try-with-resources

Try-with-resources arrived in Java 7 and was enhanced in Java 9 to accept effectively final variables.

Before it, cleanup lived in `finally` blocks and often lost or masked exceptions. Tests that replace global streams still need `finally` because restoring `System.in` or `System.out` is not a resource close operation.

## Enums, generics, annotations, enhanced for, and autoboxing

These arrived in Java 5.

Before enums, code used integer constants or a hand-built typesafe-enum pattern. Before generics, collections stored `Object` and callers cast values. Before annotations, frameworks relied more heavily on naming, marker interfaces, and XML.

Autoboxing removed explicit wrapper construction but retained identity traps. Compare wrapper values with `equals`, not `==`.

## Default interface methods

Default methods arrived in Java 8 so interfaces could evolve without immediately breaking every implementation.

Chapter 1 repository methods remain abstract because storage behavior belongs to the implementation and the test's foreign repository must provide it explicitly.

## Maven compiler `release`

The `--release` compiler option arrived in Java 9.

Earlier builds combined `source` and `target`, which could produce old bytecode while accidentally calling newer library APIs. `release` constrains syntax, bytecode, and the available platform API together.

## JShell

JShell arrived in Java 9.

Before it, trying one Java expression required a scratch class, an IDE feature, or a third-party shell. A newer JDK's JShell can still inspect classes compiled for Java 8.

## Quick reference

| Feature                                                | Introduced | Java 8 equivalent                                             |
|--------------------------------------------------------|------------|---------------------------------------------------------------|
| records                                                | 16         | explicit final value class                                    |
| text blocks                                            | 15         | concatenated strings with `\n`                                |
| switch expressions                                     | 14         | colon cases, assignment, and `break`                          |
| `Stream.toList()`                                      | 16         | `collect(Collectors.toList())`                                |
| pattern matching for switch                            | 21         | `if`/`else` plus casts                                        |
| JShell                                                 | 9          | scratch `main` class or third-party shell                     |
| compiler `release`                                     | 9          | `source` plus `target`                                        |
| Optional, streams, lambdas, `java.time`                | 8          | nulls, loops, anonymous classes, legacy date APIs             |
| default interface methods                              | 8          | abstract base class or breaking interface change              |
| StandardCharsets                                       | 7          | charset names plus checked exception handling                 |
| try-with-resources                                     | 7          | `finally` cleanup                                             |
| enums, generics, annotations, enhanced for, autoboxing | 5          | patterns, raw types, naming/XML, iterators, explicit wrappers |
| URI                                                    | 1.4        | URL or manual string handling                                 |

**Chapter:** [Chapter 1 — Java Foundations](README.md) · **Terms:** [GLOSSARY.md](GLOSSARY.md)
