Mystery2020
===========
2019-10-10

The Mystery2020 language is a reimplementation and extension of the Mystery language
originally designed by Amer Diwan as part of the PL-Detective system:

  A. Diwan, W. Waite, M. Jackson, J. Dickerson:
  "PL-detective: A system for teaching programming language concepts", in

  Journal on Educational Resources in Computing (JERIC), Volume 4, Issue 4, Dec. 2004

This implementation will add more features once it is more complete.

Gradle Build Description
========================

The packaging and the following documentation are based on work by
Jesper Öqvist, Lund University, Sweden.

Directory Structure
-------------------

- **src/main** the main Java sources (includes StateMachine compiler)
- **src/test** the Java test sources
- **src/gen** destination for generated Java code, removed by `gradle clean`
- **build** temporary directory used by Gradle for build artifacts, removed by
  `gradle clean`
- **examples** Mystery language examples

Testing
-------

You can run the tests using the following command:

    gradle test


The above runs the tests located in the `src/test/tests` directory.
The followig creates the Jar file `mystery2020-all.jar`.

    gradle jarAll

Run the generated Jar file using this command:

    java -jar mystery2020-all.jar examples/test3.my
