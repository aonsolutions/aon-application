package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.NullExpression;

/**
 * Unary expression the will be evaluated with <code>NULL</code> constant.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public class NullExpressionImpl implements NullExpression {
	
	private static final long serialVersionUID = 7146386156582127736L;

	/**
	 * The expression.
	 */
	private Expression expression;
	
    /**
     * Constructor for the given expression.
     * 
     * @param expression 
     *            The expression.
     */
	public NullExpressionImpl ( Expression expression ){
		this.expression = expression;
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.NullExpression#getExpression()
	 */
	public Expression getExpression() {
		return expression;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
	 */
	public void accept(CriterionVisitor visitor) {
		visitor.visitNullExpression( this );
	}

	@Override
	public String toString() {
		return expression + " is null";
	}
	
}
