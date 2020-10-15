Mystery2020
===========
2020-10-15

Mystery2020 is a language with configurable semantics (and, to a
lesser extent, syntax), intended for teaching concepts of programming
languages.  It is documented in the following paper:

* C. Reichenbach:
  "The PL-Detective Revisited", in
  The SPLASH-E Symposium, ACM Digital Library, Nov. 2020

Mystery2020 is modelled after the Mystery language used in the
original PL-Detective system, as described in the following paper:

* A. Diwan, W. Waite, M. Jackson, J. Dickerson:
  "PL-detective: A system for teaching programming language concepts", in
  Journal on Educational Resources in Computing (JERIC), Volume 4, Issue 4, Dec. 2004

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

Grammar
=======
```
<Program>     ::= <Block>
                | <Block> ';'
<DeclList>    ::= <Decl>
                | <Decl> ; <DeclList>
                | ε
<Decl>        ::= 'VAR' id <OptType>
                | 'TYPE' id '=' <Type>
                | <ProcDecl>
<OptType>     ::= ε
                | ':' <Type>
<ProcDecl>    ::= 'PROCEDURE' id '(' <Formals> ')' <OptType> '=' <Block>
                | 'PROCEDURE' id '(' <Formals> ')' '=' <Block>
<Formals>     ::= <FormalList>
                | ε
<FormalList>  ::= <Formal>
                | <FormalList> ';' <Formal>
<Formal>      ::= id ':' <Type>
<Type>        ::= 'INTEGER'
                | 'UNIT'
                | <SubrTy>
                | <ArrayTy>
                | id
                | <ProcTy>
<SubrTy>      ::= '[' number 'TO' number ']'
<ArrayTy>     ::= 'ARRAY' <SubrTy> 'OF' <Type>
<ProcTy>      ::= 'PROCEDURE' '(' <Formals> ')' <OptType>
<Block>       ::= <DeclList> 'BEGIN' <StmtList> 'END'
<StmtList>    ::= <Stmt>
                | <Stmt> ';' <StmtList>
                | ε
<Stmt>        ::= <Assignment>
                | <Return>
                | <Block>
                | <Conditional>
                | <Iteration>
                | <Output>
                | <Expr>
<Assignment>  ::= <Expr> := <Expr>
<Return>      ::= 'RETURN' <Expr>
<Conditional> ::= 'IF' <Expr> 'THEN' <StmtList> 'ELSE' <StmtList> 'END'
<Iteration>   ::= 'WHILE' <Expr> 'DO' <StmtList> 'END'
<Output>      ::= 'PRINT' <Expr>
<Expr>        ::= <Operand>
                | <Expr> <Operator> <Operand>
<Operand>     ::= number
                | id
                | <Operand> '[' <Expr> ']'
                | <Operand> '(' <Actuals> ')'
                | '(' <Expr> ')'
<Operator>    ::= '+'
                | '>'
                | '=='
                | 'AND'
<Actuals>     ::= <ActualList>
                | ε
<ActualList>  ::= <Expr>
                | <Actuals> ',' <Expr>
```

