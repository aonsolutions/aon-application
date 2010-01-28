package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.BetweenExpression;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.CriterionVisitor;

/**
 * Ternary expression for evaluatind an <code>Expression</code> against a
 * range of <code>Expression</code>s.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 * 
 */
public class BetweenExpressionImpl implements BetweenExpression {

	private static final long serialVersionUID = -1426664682016291666L;

	/**
	 * The <code>Expression</code> that is desired to evaluate.
	 */
	private Expression left;

	/**
	 * The minor <code>Expression</code> of the range.
	 */
	private Expression minor;

	/**
	 * The major <code>Expression</code> of the range.
	 */
	private Expression major;

	/**
	 * Constructor for given expressions.
	 * 
	 * @param left
	 *            The <code>Expression</code> that is desired to evaluate.
	 * @param minor
	 *            The minor <code>Expression</code> of the range.
	 * @param major
	 *            The major <code>Expression</code> of the range.
	 */
	public BetweenExpressionImpl(Expression left, Expression minor, Expression major) {
		this.left = left;
		this.minor = minor;
		this.major = major;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.BetweenExpression#getLeftExpression()
	 */
	public Expression getLeftExpression() {
		return left;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.BetweenExpression#getMinorExpression()
	 */
	public Expression getMinorExpression() {
		return minor;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.BetweenExpression#getMajorExpression()
	 */
	public Expression getMajorExpression() {
		return major;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
	 */
	public void accept(CriterionVisitor visitor) {
		visitor.visitBetweenExpression(this);
	}

	@Override
	public String toString() {
		return left + " between " + minor + " and " + major;
	}
	
}