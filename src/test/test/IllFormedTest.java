package test;

import junit.framework.TestCase;
import mystery2020.*;

import AST.*;
import mystery2020.Compiler;

public class IllFormedTest extends TestCase {
    public void
    testNoException() {
    	Compiler.parseString("BEGIN END");
    }

    public void
    testLexError() {
    	try {
    		Compiler.parseString("BEGIN\n\n #&\n\n END");
    		fail("No exn");
    	} catch (LexerException exn) {
    		assertEquals("LEXER 3", exn.toString());
    	}
    }
    
    public void
    testParserError() {
    	try {
    		Compiler.parseString("\nBEGIN\n\n PRINT PRINT\n\n END");
    		fail("No exn");
    	} catch (ParserException exn) {
    		assertEquals("PARSER 4", exn.toString());
    	}
    }
}
