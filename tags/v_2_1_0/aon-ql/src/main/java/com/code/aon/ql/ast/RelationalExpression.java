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
     * EQUAL
     */
    int EQ = 1;

    /**
     * NOT EQUAL
     */
    int NEQ = 2;

    /**
     * LESS THAN
     */
    int LT = 4;

    /**
     * GREATER THAN
     */
    int GT = 8;

    /**
     * LIKE
     */
    int LIKE = 16;

    /**
     * LESS THAN OR EQUAL
     */
    int LTE = 32;

    /**
     * GREATER THAN OR EQUAL
     */
    int GTE = 64;
    
    /**
     * Returns the supported type.
     * 
     * @return Returns the supported type.
     */
    int getType();

}