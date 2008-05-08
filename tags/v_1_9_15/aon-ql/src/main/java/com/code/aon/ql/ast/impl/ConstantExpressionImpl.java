package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.CriterionVisitor;

/**
 * Unary <code>Expression</code> that contains a constant.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public class ConstantExpressionImpl implements ConstantExpression {

    /**
     * The wrapped constant.
     */
    private Object data;
    
    /**
     * If this constant is a literal.
     */
    private boolean literal;

    /**
     * Constructor for the given constant.
     * 
     * @param data
     *            The constant.
     */
    public ConstantExpressionImpl(Object data) {
        this.data = data;
        this.literal = true;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.ConstantExpression#getData()
     */
    public Object getData() {
        return data;
    }
    
    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.ConstantExpression#isLiteral()
     */
    public boolean isLiteral() {
		return literal;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.ConstantExpression#setLiteral(boolean)
	 */
	public void setLiteral(boolean literal) {
		this.literal = literal;
	}

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
     */
    public void accept(CriterionVisitor visitor) {
        visitor.visitConstantExpression(this);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.data.toString();
	}

}