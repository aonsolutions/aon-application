header{
package com.code.aon.ql;
import com.code.aon.ql.ast.*;

}


class QlParser extends Parser;
options {
	k = 2;                           // two token lookahead
	buildAST = true;
	exportVocab=QL;                	 // Call its vocabulary "QL"
	defaultErrorHandler = false;     // Don't generate parser error handlers
	codeGenMakeSwitchThreshold = 2;  // Some optimizations
	codeGenBitsetTestThreshold = 3;
}

tokens {
	NOT_NULL;NULL; PATTERN; WHITESPACES;
}





expression
	:	logicalOrExpression
	;

logicalOrExpression 					
	: logicalAndExpression ((OR^)  logicalAndExpression )*  
	;


logicalAndExpression
	:	equalityExpression ((AND^)  equalityExpression )* 
	;


// equality/inequality (==/!=)
equalityExpression
	:	relationalExpression
	| NOT^ unaryExpression 
	| unaryExpression ((DOTS^)  unaryExpression )*
	;


// boolean relational expressions
relationalExpression
	:	(	LT^
			|	GT^
			|	LE^
			|	GE^
			|	LTE^
			|	GTE^
		)
		unaryExpression
	;


unaryExpression
	:	postfixExpression
	;


// qualified names, array expressions, method invocation, post inc/dec
postfixExpression
	:	primaryExpression
 	;


	
// the basic element of an expression
primaryExpression
	:	whitespaces {#primaryExpression = #([WHITESPACES, "WS" ], #primaryExpression ); }
	| "null" {#primaryExpression = #([NULL, "NULL" ], #primaryExpression ); }
	| "not null" {#primaryExpression = #([NOT_NULL, "NOT NULL" ], #primaryExpression ); }
	|	LPAREN! expression RPAREN!
	| ( ( constant | q:QUESTION | a:ASTERISC )+ 
			{ 
				if ( q != null || a != null )				
					#primaryExpression = #([PATTERN, "PATTERN" ], #primaryExpression ); 
			}
		)			
	;



constant
	:	LITERAL
	;

whitespaces
	: WS
	;	


class QlLexer extends Lexer;
options {
	k=2;                   // four characters of lookahead
	exportVocab=QL;      // call the vocabulary "Java"
	testLiterals=true;    // don't automatically test for literals
	caseSensitiveLiterals=true;
	charVocabulary = '\3'..'\377' | '\u1000'..'\u1fff';
	// without inlining some bitset tests, couldn't do unicode;
	// I need to make ANTLR generate smaller bitsets; see
	// bottom of JavaLexer.java
	codeGenBitsetTestThreshold=20;
}

NOT									:	'!';
AND									:	'&';
OR									:	'|';

DOTS	  						:	':';

LPAREN							:	'(';
RPAREN							:	')';

LBRACKET						:	'[';
RBRACKET						:	']';

EQ									:	'=';
LT									:	'<';
GT									:	'>';
LTE									:	"<=";
GTE									:	">=";
NOTEQ								:	"!=";

SR									:	">>";
SL									:	"<<";

QUESTION						:	'?'	;
ASTERISC						:	'*'	;



// literals
LITERAL
	:	(ESC
		|~( '>' 
				| '<' 
				| '=' 
				| '|' 
				| '&' 
				| '!' 
				| '*' 
				| '?' 
				| '(' 
				| ')' 
				| '[' 
				| ']' 
				| ':' 
				| '\\'
				| '\''
				| '"'
			) 
		)+
	;

WS	:	
		( '\''!)
		(	' '
			|	'\t'
			|	'\f'
		)*
		( '\''!)
	;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
protected
ESC
	:	'\\'!
		(	'>' 
			| '<' 
			| '=' 
			| '|' 
			| '&' 
			| '!' 
			| '*' 
			| '?' 
			| '(' 
			| ')' 
			| '[' 
			| ']' 
			| ':' 
			| '\\'
			| '\''
			| '"'
		)
	;




class ExpressionBuilder extends TreeParser;
options {
	k = 2;
}
{
	AbstractExpressionFactory factory = 
	AbstractExpressionFactory.newExpressionFactory();
}
expression [ String  ident ] returns [ Expression expression ]
{
	expression = null;
	Expression r = null ;
	Expression m = null ;
	Expression l = factory.newIdentExpression( ident );
}
	:	#(NOT_NULL "not null" )									{ expression = factory.newNotNullExpression( l ); }
	|	#(NULL "null" )									{ expression = factory.newNullExpression( l ); }
	| r=pattern[ident]								    { expression = factory.newRelationalExpression( l, r, RelationalExpression.LIKE  ); }
	|	literal:LITERAL 								{ 
																			r = factory.newCostantExpression( literal.getText() ); 
																			expression = factory.newRelationalExpression( l, r, RelationalExpression.EQ  ); 
																		}
	|	#(WHITESPACES ws:WS )				{ 
																	r = factory.newCostantExpression( ws.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.EQ ); 
																}
	|	#(LT literalLT:LITERAL )		{ 
																	r = factory.newCostantExpression( literalLT.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.LT ); 
																}
	|	#(GT literalGT:LITERAL )		{ 
																	r = factory.newCostantExpression( literalGT.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.GT ); 
																}
	|	#(LTE literalLTE:LITERAL )	{ 
																	r = factory.newCostantExpression( literalLTE.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.LTE ); 
																}
	|	#(GTE literalGTE:LITERAL )	{ 
																	r = factory.newCostantExpression( literalGTE.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.GTE ); 
																}

	|	#(NOT literalNOT:LITERAL )	{ 
																	r = factory.newCostantExpression( literalNOT.getText() ); 
																	expression = factory.newRelationalExpression( l, r, RelationalExpression.NEQ   ); 
																}
	
	|	#(OR  l=expression[ident] r=expression[ident] )	{ expression = factory.newLogicalOrExpression( l, r ); }
	|	#(AND l=expression[ident] r=expression[ident] )	{ expression = factory.newLogicalAndExpression( l, r ); }
	
	| #(DOTS literalMINOR:LITERAL literalMAYOR:LITERAL ){ 
																												m = factory.newCostantExpression( literalMINOR.getText() ); 
																												r = factory.newCostantExpression( literalMAYOR.getText() ); 
																												expression = factory.newBetweenExpression ( l, m, r );
																											}
	;

pattern [ String  ident ] returns [ Expression expression ]
{
	expression = null; 
	StringBuffer buff = new StringBuffer();
}
	: #(PATTERN 
			( l:LITERAL 	{ buff.append( l.getText() ); }
				| ASTERISC 	{ buff.append( '%' ); }
				| QUESTION 	{ buff.append( '_' ); }
			)+  
		)
		{ expression = factory.newCostantExpression( buff.toString() ); }
	;




