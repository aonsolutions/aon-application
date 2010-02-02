package com.code.aon.ql.ast;

/**
 * Unary expresion that contains an identifier.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public interface IdentExpression extends Expression {
    /**
     * Returns the identifier of this <code>Expression</code>.
     * 
     * @return The identifier of this <code>Expression</code>.
     */
    String getName();
}