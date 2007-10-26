package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.CriterionVisitor;

/**
 * Binary expression that will be evaluated with the OR operator.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
class LogicalOrExpressionImpl extends AbstractBinaryExpressionImpl implements
        com.code.aon.ql.ast.LogicalOrExpression {

    /**
     * Constructor for the given expressions.
     * 
     * @param left
     *            The left expression.
     * @param rigth
     *            The right expression.
     */
    public LogicalOrExpressionImpl(Expression left, Expression rigth) {
        super(left, rigth);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
     */
    public void accept(CriterionVisitor visitor) {
        visitor.visitLogicalOrExpression(this);
    }

	@Override
	protected String getOperator() {
		return "or";
	}
    
}