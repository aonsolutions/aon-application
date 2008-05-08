package com.code.aon.test.ql;

/*
 * Created on 19-nov-2003
 *
 */
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import junit.framework.TestCase;

import com.code.aon.ql.antlr.ExpressionBuilder;
import com.code.aon.ql.antlr.QlLexer;
import com.code.aon.ql.antlr.QlParser;
import com.code.aon.ql.ast.AbstractExpressionFactory;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.sql.SqlRenderer;

/**
 * @author Raúl Trepiana
 *  
 */
public class ToqlParserTest extends TestCase {

	private static final String IDENT = "item.ident"; 
	
	private static final String EXPS[] = {
	// item.ident <> "1"
			"12/12/200|>12/12/2001",
			// item.ident <> "1"
			"!1",
			// (item.ident = "1" or item.ident = "2" ) or item.ident is null )
			"1|2|null",
			// item.ident is null
			"null",
			// item.ident is not null
			"not null",
			// item.ident = "NULL"
			"NULL",
			// item.ident = "NOT NULL"
			"NOT NULL",
			// item.ident LIKE "%"
			"*",
			// item.ident = ""
			"''",
			// item.ident LIKE "%a"
			"*a",
			// item.ident LIKE "a%"
			"a*",
			// item.ident = " "
			"' '",
			// item.ident LIKE "%a%a"
			"*a*a",
			// item.ident LIKE "%a_a"
			"*a?a",
			// item.ident = "0xff"
			"0xff",
			// item.ident = "1000"
			"1000",
			// item.ident = "1000.50"
			"1000.50",
			// item.ident < "1000.50"
			"<1000.50",
			// item.ident <= "1000"
			"<=1000",
			// item.ident >= "1000"
			">=1000",
			// item.ident between "1000" and "1001"
			"1000:1001",
			// item.ident = "(String Literal"
			"\\(String Literal",
			// item.ident = "String Literal"
			"String Literal",
			// item.ident > "String Literal"
			">String Literal",
			// item.ident <> "String Literal"
			"!String Literal",
			// item.ident = "First Value" or item.ident = "Second Value"
			"First Value|Second Value",
			// item.ident between "First Value" and "Second Value"
			"First Value:Second Value",
			// item.ident < "First Value" and item.ident > "Second Possible
			// Value"
			"<First Value&>Second Value", };

	/**
	 * Constructor for ToqlParserTest.
	 * 
	 * @param arg0
	 */
	public ToqlParserTest(String arg0) {
		super(arg0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ToqlParserTest test = new ToqlParserTest("");
		test.testExpressions();
	}

	/**
	 * 
	 */
	public final void testExpressions() {
		AbstractExpressionFactory f = AbstractExpressionFactory
				.newExpressionFactory();
		Expression left = null, rigth = null;
		for (int i = 0; i < EXPS.length; i++) {
			rigth = getExpression( EXPS[i] );
			testExpressionWithSqlRenderer( rigth );
			left = (left == null) ? rigth : f.newLogicalAndExpression(left, rigth);
		}
		testExpressionWithSqlRenderer( left );
	}

	/**
	 * @param expression
	 */
	public final void testExpressionWithSqlRenderer( Expression expression ) {
		StringWriter out = new StringWriter();
		SqlRenderer sqlRenderer = new SqlRenderer(out);
		expression.accept(sqlRenderer);
		System.out.println(out.toString());
	}

	private final Expression getExpression(String expressionString) {
		Reader reader = new StringReader(expressionString);
		QlLexer lexer = new QlLexer(reader);
		QlParser parser = new QlParser(lexer);
		try {
			parser.expression();
		} catch (RecognitionException e) {
			fail( e.getMessage() + " con la expression " + expressionString );
		} catch (TokenStreamException e) {
			fail( e.getMessage() + " con la expression " + expressionString );
		}
		ExpressionBuilder builder = new ExpressionBuilder();
		Expression expression = null;
		try {
			expression = builder.expression(parser.getAST(), IDENT);
		} catch (RecognitionException e1) {
			fail( e1.getMessage() + " con la expression " + expressionString );
		}
		return expression;
	}

}