package com.code.aon.ql.ast;

/**
 * Binary expression that will be evaluated with a supported relational operator.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public interface RelationalExpression extends BinaryExpression {

    /**
     * Returns the supported type.
     * 
     * @return Returns the supported type.
     */
    RelationalType getType();

}