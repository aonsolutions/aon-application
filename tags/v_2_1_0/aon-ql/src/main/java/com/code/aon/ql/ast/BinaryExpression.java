package com.code.aon.ql.ast;

/**
 * Binary <code>Expression</code> between <code> Expression</code>.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public interface BinaryExpression extends Expression {
    /**
     * Returns the left side <code> Expression</code>. 
     * @return The left side <code> Expression</code>.
     */
	Expression getLeftExpression();
    /**
     * Returns the right side <code> Expression</code>. 
     * @return The right side <code> Expression</code>.
     */
	Expression getRightExpression();
}
