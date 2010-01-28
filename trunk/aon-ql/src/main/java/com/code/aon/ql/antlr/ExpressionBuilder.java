// $ANTLR : "ql.g" -> "ExpressionBuilder.java"$

package com.code.aon.ql.antlr;

import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;

import com.code.aon.ql.ast.AbstractExpressionFactory;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.RelationalExpression;

/**
 * ANTLR generated class.
 * 
 * @author Consulting & Development. ecastellano - 06/10/2006
 * 
 */
public class ExpressionBuilder extends antlr.TreeParser implements QLTokenTypes {

	AbstractExpressionFactory factory = AbstractExpressionFactory
			.newExpressionFactory();

	/**
	 * 
	 */
	public ExpressionBuilder() {
		tokenNames = _tokenNames;
	}

	/**
	 * @param _t
	 * @param ident
	 * @return Expression
	 * @throws RecognitionException
	 */
	public final Expression expression(AST _t, String ident)
			throws RecognitionException {
		Expression expression;

		AST literal = null;
		AST ws = null;
		AST literalLT = null;
		AST literalGT = null;
		AST literalLTE = null;
		AST literalGTE = null;
		AST literalNOT = null;
		AST literalMINOR = null;
		AST literalMAYOR = null;

		expression = null;
		Expression r = null;
		Expression m = null;
		Expression l = factory.newIdentExpression(ident);

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case NOT_NULL: {
				AST __t123 = _t;
				match(_t, NOT_NULL);
				_t = _t.getFirstChild();
				match(_t, 19);
				_t = _t.getNextSibling();
				_t = __t123;
				_t = _t.getNextSibling();
				expression = factory.newNotNullExpression(l);
				break;
			}
			case NULL: {
				AST __t124 = _t;
				match(_t, NULL);
				_t = _t.getFirstChild();
				match(_t, LITERAL_null);
				_t = _t.getNextSibling();
				_t = __t124;
				_t = _t.getNextSibling();
				expression = factory.newNullExpression(l);
				break;
			}
			case PATTERN: {
				r = pattern(_t, ident);
				_t = _retTree;
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.LIKE);
				break;
			}
			case LITERAL: {
				literal = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literal.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.EQ);

				break;
			}
			case WHITESPACES: {
				AST __t125 = _t;
				match(_t, WHITESPACES);
				_t = _t.getFirstChild();
				ws = _t;
				match(_t, WS);
				_t = _t.getNextSibling();
				_t = __t125;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(ws.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.EQ);

				break;
			}
			case LT: {
				AST __t126 = _t;
				match(_t, LT);
				_t = _t.getFirstChild();
				literalLT = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t126;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literalLT.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.LT);

				break;
			}
			case GT: {
				AST __t127 = _t;
				match(_t, GT);
				_t = _t.getFirstChild();
				literalGT = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t127;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literalGT.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.GT);

				break;
			}
			case LTE: {
				AST __t128 = _t;
				match(_t, LTE);
				_t = _t.getFirstChild();
				literalLTE = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t128;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literalLTE.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.LTE);

				break;
			}
			case GTE: {
				AST __t129 = _t;
				match(_t, GTE);
				_t = _t.getFirstChild();
				literalGTE = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t129;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literalGTE.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.GTE);

				break;
			}
			case NOT: {
				AST __t130 = _t;
				match(_t, NOT);
				_t = _t.getFirstChild();
				literalNOT = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t130;
				_t = _t.getNextSibling();

				r = factory.newCostantExpression(literalNOT.getText());
				expression = factory.newRelationalExpression(l, r,
						RelationalExpression.NEQ);

				break;
			}
			case OR: {
				AST __t131 = _t;
				match(_t, OR);
				_t = _t.getFirstChild();
				l = expression(_t, ident);
				_t = _retTree;
				r = expression(_t, ident);
				_t = _retTree;
				_t = __t131;
				_t = _t.getNextSibling();
				expression = factory.newLogicalOrExpression(l, r);
				break;
			}
			case AND: {
				AST __t132 = _t;
				match(_t, AND);
				_t = _t.getFirstChild();
				l = expression(_t, ident);
				_t = _retTree;
				r = expression(_t, ident);
				_t = _retTree;
				_t = __t132;
				_t = _t.getNextSibling();
				expression = factory.newLogicalAndExpression(l, r);
				break;
			}
			case DOTS: {
				AST __t133 = _t;
				match(_t, DOTS);
				_t = _t.getFirstChild();
				literalMINOR = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				literalMAYOR = _t;
				match(_t, LITERAL);
				_t = _t.getNextSibling();
				_t = __t133;
				_t = _t.getNextSibling();

				m = factory.newCostantExpression(literalMINOR.getText());
				r = factory.newCostantExpression(literalMAYOR.getText());
				expression = factory.newBetweenExpression(l, m, r);

				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		_retTree = _t;
		return expression;
	}

	/**
	 * @param _t
	 * @param ident
	 * @return Expression
	 * @throws RecognitionException
	 */
	public final Expression pattern(AST _t, String ident)
			throws RecognitionException {
		Expression expression;

		AST l = null;

		expression = null;
		StringBuffer buff = new StringBuffer();

		try { // for error handling
			AST __t135 = _t;
			match(_t, PATTERN);
			_t = _t.getFirstChild();
			{
				int _cnt137 = 0;
				_loop137: do {
					if (_t == null)
						_t = ASTNULL;
					switch (_t.getType()) {
					case LITERAL: {
						l = _t;
						match(_t, LITERAL);
						_t = _t.getNextSibling();
						buff.append(l.getText());
						break;
					}
					case ASTERISC: {
						match(_t, ASTERISC);
						_t = _t.getNextSibling();
						buff.append('%');
						break;
					}
					case QUESTION: {
						match(_t, QUESTION);
						_t = _t.getNextSibling();
						buff.append('_');
						break;
					}
					default: {
						if (_cnt137 >= 1) {
							break _loop137;
						}
						throw new NoViableAltException(_t);
					}
					}
					_cnt137++;
				} while (true);
			}
			_t = __t135;
			_t = _t.getNextSibling();
			expression = factory.newCostantExpression(buff.toString());
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		_retTree = _t;
		return expression;
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

}
