package com.code.aon.ql.ast;

/**
 * Unary <code>Expression</code> that contains a constant.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public interface ConstantExpression extends Expression {

	/**
     * Returns the wrapped constant. 
     * @return The wrapped constant.
     */
	Object getData();
	
    /** 
     * Returns if this constant is a literal.
     * 
     * @return <code>True</code>, if this constant is a literal.
     */
    boolean isLiteral();

	/** 
	 * Sets the literal flag to this constant.
	 * 
     * @param literal <code>True</code>, if this constant is a literal. 
	 */
	void setLiteral(boolean literal);

}
