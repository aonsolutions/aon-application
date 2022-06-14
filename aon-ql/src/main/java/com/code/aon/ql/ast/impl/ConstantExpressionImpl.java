package com.code.aon.ql.ast.impl;

import java.util.Arrays;
import java.util.Collection;

import com.code.aon.AonVersion;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.CriterionVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Unary <code>Expression</code> that contains a constant.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public class ConstantExpressionImpl implements ConstantExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
    
    /**
     * Sets the data.
     *
     * @param data the new data
     */
    public void setData(Object data) {
		this.data = data;
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
		if ( this.data.getClass().isArray() ) {
			return Arrays.toString( (Object[]) this.data);
		} else if ( this.data.getClass().isAssignableFrom(Collection.class) ) {
			return AonStringUtils.join((Collection<?>) this.data, ",");
		}
		return this.data.toString();
	}

}