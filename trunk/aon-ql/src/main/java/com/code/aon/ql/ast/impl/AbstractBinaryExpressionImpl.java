package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.BinaryExpression;
import com.code.aon.ql.ast.Expression;

/**
 * Abstract class for support binary expressions.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
abstract class AbstractBinaryExpressionImpl implements BinaryExpression {

	private static final long serialVersionUID = 291169328179498002L;

	/**
     * The left expression.
     */
    private Expression left;

    /**
     * The right expression.
     */
    private Expression right;

    /**
     * Constructor.
     * 
     * @param left
     *            The left expression.
     * @param rigth
     *            The right expression.
     */
    public AbstractBinaryExpressionImpl(Expression left, Expression rigth) {
        this.left = left;
        this.right = rigth;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.BinaryExpression#getLeftExpression()
     */
    public Expression getLeftExpression() {
        return left;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.BinaryExpression#getRightExpression()
     */
    public Expression getRightExpression() {
        return right;
    }

    /**
     * Gets the operator.
     * 
     * @return the operator
     */
    protected abstract String getOperator();

	@Override
	public String toString() {
		return left + " " + getOperator() + " " + right;
	}
    
}