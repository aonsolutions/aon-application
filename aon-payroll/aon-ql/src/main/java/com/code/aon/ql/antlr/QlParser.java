// $ANTLR : "ql.g" -> "QlParser.java"$

package com.code.aon.ql.antlr;

import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;

/**
 * ANTLR generated class.
 * 
 * @author Consulting & Development. ecastellano - 11/10/2006
 *
 */
public class QlParser extends antlr.LLkParser implements QLTokenTypes {

	/**
	 * @param tokenBuf
	 * @param k
	 */
	protected QlParser(TokenBuffer tokenBuf, int k) {
		super(tokenBuf, k);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	/**
	 * @param tokenBuf
	 */
	public QlParser(TokenBuffer tokenBuf) {
		this(tokenBuf, 2);
	}

	/**
	 * @param lexer
	 * @param k
	 */
	protected QlParser(TokenStream lexer, int k) {
		super(lexer, k);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	/**
	 * @param lexer
	 */
	public QlParser(TokenStream lexer) {
		this(lexer, 2);
	}

	/**
	 * @param state
	 */
	public QlParser(ParserSharedInputState state) {
		super(state, 2);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void expression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;

		logicalOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		expression_AST = currentAST.root;
		returnAST = expression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void logicalOrExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;

		logicalAndExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop74: do {
				if ((LA(1) == OR)) {
					{
						AST tmp17_AST = null;
						tmp17_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp17_AST);
						match(OR);
					}
					logicalAndExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop74;
				}

			} while (true);
		}
		logicalOrExpression_AST = currentAST.root;
		returnAST = logicalOrExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void logicalAndExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;

		equalityExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop78: do {
				if ((LA(1) == AND)) {
					{
						AST tmp18_AST = null;
						tmp18_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp18_AST);
						match(AND);
					}
					equalityExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop78;
				}

			} while (true);
		}
		logicalAndExpression_AST = currentAST.root;
		returnAST = logicalAndExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void equalityExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;

		switch (LA(1)) {
		case LT:
		case GT:
		case LE:
		case GE:
		case LTE:
		case GTE: {
			relationalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			equalityExpression_AST = currentAST.root;
			break;
		}
		case NOT: {
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp19_AST);
			match(NOT);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			equalityExpression_AST = currentAST.root;
			break;
		}
		case LITERAL_null:
		case 19:
		case LPAREN:
		case QUESTION:
		case ASTERISC:
		case LITERAL:
		case WS: {
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
				_loop82: do {
					if ((LA(1) == DOTS)) {
						{
							AST tmp20_AST = null;
							tmp20_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp20_AST);
							match(DOTS);
						}
						unaryExpression();
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						break _loop82;
					}

				} while (true);
			}
			equalityExpression_AST = currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = equalityExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void relationalExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;

		{
			switch (LA(1)) {
			case LT: {
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp21_AST);
				match(LT);
				break;
			}
			case GT: {
				AST tmp22_AST = null;
				tmp22_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp22_AST);
				match(GT);
				break;
			}
			case LE: {
				AST tmp23_AST = null;
				tmp23_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp23_AST);
				match(LE);
				break;
			}
			case GE: {
				AST tmp24_AST = null;
				tmp24_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp24_AST);
				match(GE);
				break;
			}
			case LTE: {
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp25_AST);
				match(LTE);
				break;
			}
			case GTE: {
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp26_AST);
				match(GTE);
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		unaryExpression();
		astFactory.addASTChild(currentAST, returnAST);
		relationalExpression_AST = currentAST.root;
		returnAST = relationalExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void unaryExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;

		postfixExpression();
		astFactory.addASTChild(currentAST, returnAST);
		unaryExpression_AST = currentAST.root;
		returnAST = unaryExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void postfixExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixExpression_AST = null;

		primaryExpression();
		astFactory.addASTChild(currentAST, returnAST);
		postfixExpression_AST = currentAST.root;
		returnAST = postfixExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void primaryExpression() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		Token q = null;
		AST q_AST = null;
		Token a = null;
		AST a_AST = null;

		switch (LA(1)) {
		case WS: {
			whitespaces();
			astFactory.addASTChild(currentAST, returnAST);
			primaryExpression_AST = currentAST.root;
			primaryExpression_AST = astFactory.make((new ASTArray(2)).add(
					astFactory.create(WHITESPACES, "WS")).add(
					primaryExpression_AST));
			currentAST.root = primaryExpression_AST;
			currentAST.child = primaryExpression_AST != null
					&& primaryExpression_AST.getFirstChild() != null ? primaryExpression_AST
					.getFirstChild()
					: primaryExpression_AST;
			currentAST.advanceChildToEnd();
			primaryExpression_AST = currentAST.root;
			break;
		}
		case LITERAL_null: {
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(LITERAL_null);
			primaryExpression_AST = currentAST.root;
			primaryExpression_AST = astFactory
					.make((new ASTArray(2))
							.add(astFactory.create(NULL, "NULL")).add(
									primaryExpression_AST));
			currentAST.root = primaryExpression_AST;
			currentAST.child = primaryExpression_AST != null
					&& primaryExpression_AST.getFirstChild() != null ? primaryExpression_AST
					.getFirstChild()
					: primaryExpression_AST;
			currentAST.advanceChildToEnd();
			primaryExpression_AST = currentAST.root;
			break;
		}
		case 19: {
			AST tmp28_AST = null;
			tmp28_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp28_AST);
			match(19);
			primaryExpression_AST = currentAST.root;
			primaryExpression_AST = astFactory.make((new ASTArray(2)).add(
					astFactory.create(NOT_NULL, "NOT NULL")).add(
					primaryExpression_AST));
			currentAST.root = primaryExpression_AST;
			currentAST.child = primaryExpression_AST != null
					&& primaryExpression_AST.getFirstChild() != null ? primaryExpression_AST
					.getFirstChild()
					: primaryExpression_AST;
			currentAST.advanceChildToEnd();
			primaryExpression_AST = currentAST.root;
			break;
		}
		case LPAREN: {
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			primaryExpression_AST = currentAST.root;
			break;
		}
		case QUESTION:
		case ASTERISC:
		case LITERAL: {
			{
				{
					int _cnt90 = 0;
					_loop90: do {
						switch (LA(1)) {
						case LITERAL: {
							constant();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case QUESTION: {
							q = LT(1);
							q_AST = astFactory.create(q);
							astFactory.addASTChild(currentAST, q_AST);
							match(QUESTION);
							break;
						}
						case ASTERISC: {
							a = LT(1);
							a_AST = astFactory.create(a);
							astFactory.addASTChild(currentAST, a_AST);
							match(ASTERISC);
							break;
						}
						default: {
							if (_cnt90 >= 1) {
								break _loop90;
							}
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						_cnt90++;
					} while (true);
				}
				primaryExpression_AST = currentAST.root;

				if (q != null || a != null)
					primaryExpression_AST = astFactory.make((new ASTArray(2))
							.add(astFactory.create(PATTERN, "PATTERN")).add(
									primaryExpression_AST));

				currentAST.root = primaryExpression_AST;
				currentAST.child = primaryExpression_AST != null
						&& primaryExpression_AST.getFirstChild() != null ? primaryExpression_AST
						.getFirstChild()
						: primaryExpression_AST;
				currentAST.advanceChildToEnd();
			}
			primaryExpression_AST = currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryExpression_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void whitespaces() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whitespaces_AST = null;

		AST tmp31_AST = null;
		tmp31_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp31_AST);
		match(WS);
		whitespaces_AST = currentAST.root;
		returnAST = whitespaces_AST;
	}

	/**
	 * @throws RecognitionException
	 * @throws TokenStreamException
	 */
	public final void constant() throws RecognitionException,
			TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;

		AST tmp32_AST = null;
		tmp32_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp32_AST);
		match(LITERAL);
		constant_AST = currentAST.root;
		returnAST = constant_AST;
	}

	/**
	 * 
	 */
	public static final String[] _tokenNames = { "<0>", "EOF", "<2>",
			"NULL_TREE_LOOKAHEAD", "NOT_NULL", "NULL", "PATTERN",
			"WHITESPACES", "OR", "AND", "NOT", "DOTS", "LT", "GT", "LE", "GE",
			"LTE", "GTE", "\"null\"", "\"not null\"", "LPAREN", "RPAREN",
			"QUESTION", "ASTERISC", "LITERAL", "WS", "LBRACKET", "RBRACKET",
			"EQ", "NOTEQ", "SR", "SL", "ESC" };

	/**
	 * 
	 */
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap = null;
	}

}
