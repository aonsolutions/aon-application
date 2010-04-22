package com.code.aon.ql.ast;

/**
 * Unary expression the will be evaluated with <code>NULL</code> constant.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public interface NullExpression extends Expression {
	/**
	 * Returns the expression.
	 * @return The expression.
	 */
	Expression getExpression();
}
