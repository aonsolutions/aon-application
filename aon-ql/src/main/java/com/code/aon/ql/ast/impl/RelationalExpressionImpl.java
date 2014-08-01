package com.code.aon.ql.ast.impl;

import com.code.aon.AonVersion;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.ast.RelationalType;

/**
 * Binary expression that will be evaluated with a supported relational operator. 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 * 
 */
public class RelationalExpressionImpl extends AbstractBinaryExpressionImpl implements RelationalExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	/**
	 * Supported relational operator.
	 * 
	 */
	private RelationalType type;

	/**
	 * Constructor. indicado.
	 * 
	 * @param left
	 *            The left expression.
	 * @param rigth
	 *            The right expression.
	 * @param type
	 *            A supported relational operator.
	 * 
	 */
	public RelationalExpressionImpl(Expression left, Expression rigth, RelationalType type) {
		super(left, rigth);
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.RelationalExpression#getType()
	 */
	public RelationalType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(RelationalType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
	 */
	public void accept(CriterionVisitor visitor) {
		visitor.visitRelationalExpression(this);
	}
	
	@Override
	protected String getOperator() {
		switch ( type ) {
			case LESS_THAN:
				return "<";
			case GREATER_THAN:
				return ">";
			case EQUAL:
				return "==";
			case NOT_EQUAL:
				return "<>";
			case LIKE:
				return "like";
			case LESS_THAN_OR_EQUAL:
				return "<=";
			case GREATER_THAN_OR_EQUAL:
				return ">=";
			case IN:
				return "in";
		}
		return null;
	}

}