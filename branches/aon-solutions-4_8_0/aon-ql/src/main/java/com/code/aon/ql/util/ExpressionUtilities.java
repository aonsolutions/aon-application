package com.code.aon.ql.util;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

import com.code.aon.ql.antlr.ExpressionBuilder;
import com.code.aon.ql.antlr.QlLexer;
import com.code.aon.ql.antlr.QlParser;
import com.code.aon.ql.ast.AbstractExpressionFactory;
import com.code.aon.ql.ast.BetweenExpression;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.NotNullExpression;
import com.code.aon.ql.ast.NullExpression;
import com.code.aon.ql.ast.RelationalExpression;

/**
 * Clase que facilita la formación de objectos del tipo <code>Expression</code>.
 * 
 * @author Consulting & Development. Eugenio Castellano - 04-feb-2005
 * @since 1.0
 * 
 */
public class ExpressionUtilities {

	private static final AbstractExpressionFactory EXPRESSION_FACTORY = AbstractExpressionFactory
			.newExpressionFactory();

	private static final ExpressionBuilder EXPRESSION_BUILDER = new ExpressionBuilder();

	/**
	 * Returns an implementation of a <code>RelationalExpression</code> for
	 * the given elements.
	 * 
	 * @param left
	 *            Left side expression.
	 * @param rigth
	 *            Right side expression.
	 * @param type
	 *            Valid operator supported in
	 *            <code>com.code.aon.ql.ast.RelationalExpression</code>.
	 * 
	 * @return The implementation of a <code>RelationalExpression</code>.
	 * @see com.code.aon.ql.ast.RelationalExpression
	 */
	private static RelationalExpression getRelationalExpression(String identifier, Expression expression, int type) {
		IdentExpression ie = getIdentifierExpression(identifier);
		return EXPRESSION_FACTORY.newRelationalExpression(ie, expression, type);
	}

	/**
	 * Returns an implementation of a <code>RelationalExpression</code> for
	 * the given elements.
	 * 
	 * @param left
	 *            Left side expression.
	 * @param rigth
	 *            Right side expression.
	 * @param type
	 *            Valid operator supported in
	 *            <code>com.code.aon.ql.ast.RelationalExpression</code>.
	 * 
	 * @return The implementation of a <code>RelationalExpression</code>.
	 * @see com.code.aon.ql.ast.RelationalExpression
	 */
	private static RelationalExpression getRelationalExpression(String identifier, Object data, int type) {
		ConstantExpression ce = getConstantExpression(data);
		IdentExpression ie = getIdentifierExpression(identifier);
		return EXPRESSION_FACTORY.newRelationalExpression(ie, ce, type);
	}

	/**
	 * Returns an equal expression for the given identifier and data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getEqualExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.EQ);
	}

	/**
	 * Returns an equal expression for the given identifier and expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getEqualExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.EQ);
	}

	/**
	 * Returns an not equal expression for the given identifier and data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getNotEqualExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.NEQ);
	}

	/**
	 * Returns an not equal expression for the given identifier and expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getNotEqualExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.NEQ);
	}

	/**
	 * Returns an less than expression for the given identifier and data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLessThanExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.LT);
	}

	/**
	 * Returns an less than expression for the given identifier and expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLessThanExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.LT);
	}

	/**
	 * Returns an less than or equal expression for the given identifier and
	 * data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLessThanOrEqualExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.LTE);
	}

	/**
	 * Returns an less than or equal expression for the given identifier and
	 * expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLessThanOrEqualExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.LTE);
	}

	/**
	 * Returns an greather than expression for the given identifier and data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getGreaterThanExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.GT);
	}

	/**
	 * Returns an greather than expression for the given identifier and
	 * expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getGreaterThanExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.GT);
	}

	/**
	 * Returns an greather than or equal expression for the given identifier and
	 * data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getGreaterThanOrEqualExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.GTE);
	}

	/**
	 * Returns an greather than or equal expression for the given identifier and
	 * expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getGreaterThanOrEqualExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.GTE);
	}

	/**
	 * Returns a like expression for the given identifier and data.
	 * 
	 * @param identifier
	 * @param data
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLikeExpression(String identifier, Object data) {
		return getRelationalExpression(identifier, data, RelationalExpression.LIKE);
	}

	/**
	 * Returns a like expression for the given identifier and expression.
	 * 
	 * @param identifier
	 * @param expression
	 * @return a <code>RelationalExpression</code>.
	 */
	public static RelationalExpression getLikeExpression(String identifier, Expression expression) {
		return getRelationalExpression(identifier, expression, RelationalExpression.LIKE);
	}

	/**
	 * Returns a null expression for the given identifier.
	 * 
	 * @param identifier
	 * @return a <code>NullExpression</code>.
	 */
	public static NullExpression getNullExpression(String identifier) {
		IdentExpression id = getIdentifierExpression(identifier);
		return EXPRESSION_FACTORY.newNullExpression(id);
	}

	/**
	 * Returns a not null expression for the given identifier.
	 * 
	 * @param identifier
	 * @return a <code>NotNullExpression</code>.
	 */
	public static NotNullExpression getNotNullExpression(String identifier) {
		IdentExpression id = getIdentifierExpression(identifier);
		return EXPRESSION_FACTORY.newNotNullExpression(id);
	}

	/**
	 * Returns a between expression for the given objects.
	 * 
	 * @param identifier
	 *            The identifier.
	 * @param left
	 *            The minor value.
	 * @param right
	 *            The major value.
	 * @return BetweenExpression
	 */
	public static BetweenExpression getBetweenExpression(String identifier, Object left, Object right) {
		ConstantExpression leftExpression = getConstantExpression(left);
		ConstantExpression rightExpression = getConstantExpression(right);
		return getBetweenExpression(identifier, leftExpression, rightExpression);
	}

	/**
	 * Returns a between expression for the given objects.
	 * 
	 * @param identifier
	 *            The identifier.
	 * @param minor
	 *            The minor value.
	 * @param major
	 *            The major value.
	 * @return BetweenExpression
	 */
	public static BetweenExpression getBetweenExpression(String identifier, Expression minor, Expression major) {
		IdentExpression ie = getIdentifierExpression(identifier);
		return EXPRESSION_FACTORY.newBetweenExpression(ie, minor, major);
	}

	/**
	 * Returns a constant expression for the given object.
	 * 
	 * @param data
	 *            The Constant.
	 * @return ConstantExpression
	 */
	public static ConstantExpression getConstantExpression(Object data) {
		ConstantExpression ce = EXPRESSION_FACTORY.newCostantExpression(data);
		ce.setLiteral(false);
		return ce;
	}

	/**
	 * Returns a ident expression for the given object.
	 * 
	 * @param identifier
	 *            The identifier.
	 * @return IdentExpression
	 */
	public static IdentExpression getIdentifierExpression(String identifier) {
		return EXPRESSION_FACTORY.newIdentExpression(identifier);
	}

	/**
	 * Reurns a Expression using QL language.
	 * 
	 * @param string
	 *            Ql String.
	 * @param identifier
	 *            The identifier of the expression.
	 * @return A Expression.
	 * @throws ExpressionException
	 */
	public static Expression getExpression(String string, String identifier) throws ExpressionException {
		try {
			return EXPRESSION_BUILDER.expression(getQlAST(string), identifier);
		} catch (RecognitionException e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	/**
	 * Util method for construct <code>com.code.aon.ql.ast.Expression</code>
	 * from a <code>java.util.Map</code>. The different entries of the map
	 * will be linked with th AND operator.
	 * 
	 * @param entries
	 *            The map.
	 * @return A expression.
	 * @throws ExpressionException
	 *             If something was wrong.
	 */
	public static Expression getExpression(Map<String, String> entries) throws ExpressionException {
		try {
			Expression left = null, right = null;
			Iterator<String> iter = entries.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String str = entries.get(key);
				if (str != null && str.trim().length() > 0) {
					right = EXPRESSION_BUILDER.expression(getQlAST(str), key);
					left = getAndExpression(left, right);
				}
			}
			return left;
		} catch (RecognitionException e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	/**
	 * Util method for construct <code>com.code.aon.ql.ast.Expression</code>
	 * from a <code>java.util.Map</code>. The different entries of the map
	 * will be linked with th OR operator.
	 * 
	 * @param entries
	 *            The map.
	 * @return A expression.
	 * @throws ExpressionException
	 *             If something was wrong.
	 */
	public static Expression getExpressionWithOr(Map<String, String> entries) throws ExpressionException {
		try {
			Expression left = null, right = null;
			Iterator<String> iter = entries.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String str = entries.get(key);
				if (str != null && str.trim().length() > 0) {
					right = EXPRESSION_BUILDER.expression(getQlAST(str), key);
					left = getOrExpression(left, right);
				}
			}
			return left;
		} catch (RecognitionException e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	/**
	 * Method for link two expression with the AND operator.
	 * 
	 * @param expr1
	 *            First expression.
	 * @param expr2
	 *            Second expression.
	 * @return Expression,
	 */
	public static Expression getAndExpression(Expression expr1, Expression expr2) {
		return (expr1 == null) ? expr2 : EXPRESSION_FACTORY.newLogicalAndExpression(expr1, expr2);
	}

	/**
	 * Method for link two expression with the OR operator.
	 * 
	 * @param expr1
	 *            First expression.
	 * @param expr2
	 *            Second expression.
	 * @return Expression,
	 */
	public static Expression getOrExpression(Expression expr1, Expression expr2) {
		return (expr1 == null) ? expr2 : EXPRESSION_FACTORY.newLogicalOrExpression(expr1, expr2);
	}

	/**
	 * Document Me!
	 * 
	 * @param str
	 * @return AST
	 * @throws ExpressionException
	 */
	private static AST getQlAST(String str) throws ExpressionException {
		try {
			StringReader in = new StringReader(str);
			QlLexer lexer = new QlLexer(in);
			QlParser parser = new QlParser(lexer);
			parser.expression();
			return parser.getAST();
		} catch (RecognitionException e) {
			throw new ExpressionException(e.getMessage(), e);
		} catch (TokenStreamException e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

}