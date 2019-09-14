package AST;

import AST.ProgramParser.Terminals; // Terminals are implicitly defined in the parser
import mystery2020.LexerException;

%%

// define the signature for the generated scanner
%public
%final
%class ProgramScanner
%extends beaver.Scanner

// the interface between the scanner and the parser is the nextToken() method
%type beaver.Symbol
%function nextToken
%yylexthrow beaver.Scanner.Exception

// store line and column information in the tokens
%line
%column

// this code will be inlined in the body of the generated scanner class
%{
  private beaver.Symbol sym(short id) {
    return new beaver.Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());
  }
%}

NonZeroDigit = [1-9]
Digit = 0 | {NonZeroDigit}
Digits = {Digit}+

WhiteSpace = [ ] | \t | \f | \n | \r | \r\n
Identifier = [:jletter:][:jletterdigit:]*
DecimalNumeral = 0 | {NonZeroDigit} {Digits}?

%%

// discard whitespace information
{WhiteSpace}  { }

// token definitions
";"			{ return sym(Terminals.SEMICOLON); }
":"			{ return sym(Terminals.COLON); }
","			{ return sym(Terminals.COMMA); }
"="			{ return sym(Terminals.EQ); }
"("			{ return sym(Terminals.OPAREN); }
")"			{ return sym(Terminals.CPAREN); }
"["			{ return sym(Terminals.OBRACKET); }
"]"			{ return sym(Terminals.CBRACKET); }
">"			{ return sym(Terminals.GT); }
"+"			{ return sym(Terminals.PLUS); }
":="			{ return sym(Terminals.COLONEQ); }
"=="			{ return sym(Terminals.EQEQ); }
"AND"			{ return sym(Terminals.AND); }
"BEGIN"			{ return sym(Terminals.BEGIN); }
"END"			{ return sym(Terminals.END); }
"VAR"			{ return sym(Terminals.VAR); }
"TYPE"			{ return sym(Terminals.TYPE); }
"PROCEDURE"		{ return sym(Terminals.PROCEDURE); }
"INTEGER"		{ return sym(Terminals.INTEGER); }
"UNIT"			{ return sym(Terminals.UNIT); }
"TO"			{ return sym(Terminals.TO); }
"ARRAY"			{ return sym(Terminals.ARRAY); }
"RETURN"		{ return sym(Terminals.RETURN); }
"IF"			{ return sym(Terminals.IF); }
"THEN"			{ return sym(Terminals.THEN); }
"ELSE"			{ return sym(Terminals.ELSE); }
"WHILE"			{ return sym(Terminals.WHILE); }
"DO"			{ return sym(Terminals.DO); }
"PRINT"			{ return sym(Terminals.PRINT); }
{Identifier}		{ return sym(Terminals.IDENTIFIER); }
-?{DecimalNumeral}	{ return sym(Terminals.INT_LITERAL); }
<<EOF>>			{ return sym(Terminals.EOF); }

[^]			{ throw new LexerException(yyline + 1, yycolumn); }
