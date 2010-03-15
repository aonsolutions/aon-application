package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.LogicalAndExpression;

/**
 * Binary expression that will be evaluated with the AND operator.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
class LogicalAndExpressionImpl extends AbstractBinaryExpressionImpl implements
        LogicalAndExpression {

	private static final long serialVersionUID = 4254468602868315742L;

	/**
     * Constructor for the given expressions.
     * 
     * @param left
     *            The left expression.
     * @param rigth
     *            The right expression.
     */
    public LogicalAndExpressionImpl(Expression left, Expression rigth) {
        super(left, rigth);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
     */
    public void accept(CriterionVisitor visitor) {
        visitor.visitLogicalAndExpression(this);
    }

	@Override
	protected String getOperator() {
		return "and";
	}

}