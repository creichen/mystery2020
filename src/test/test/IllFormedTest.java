package test;

import junit.framework.TestCase;
import mystery2020.*;

import AST.*;
import mystery2020.Interpreter;

public class IllFormedTest extends TestCase {
    public void
    testNoException() {
    	Interpreter.parseString("BEGIN END");
    }

    public void
    testLexError() {
    	try {
    		Interpreter.parseString("BEGIN\n\n #&\n\n END");
    		fail("No exn");
    	} catch (LexerException exn) {
    		assertEquals("LEXER 3", exn.toString());
    	}
    }
    
    public void
    testParserError() {
    	try {
    		Interpreter.parseString("\nBEGIN\n\n PRINT PRINT\n\n END");
    		fail("No exn");
    	} catch (ParserException exn) {
    		assertEquals("PARSER 4", exn.toString());
    	}
    }
}
