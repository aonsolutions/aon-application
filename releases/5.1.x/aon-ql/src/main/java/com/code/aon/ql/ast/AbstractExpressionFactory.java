package com.code.aon.ql.ast;

import com.code.aon.ql.ast.impl.ExpressionFactoryImpl;

/**
 * Provides different implementations of <code>Expression</code>.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public abstract class AbstractExpressionFactory {

    /**
     * Returns a new instance of the default factory. That is <code>ExpressionFactoryImpl</code>. 
     * 
     * @return A new instance of the default factory.
     *         
     */
    public static AbstractExpressionFactory newExpressionFactory() {
        return new ExpressionFactoryImpl();
    }

    /**
     * Returns an implementation of an <code>IdentExpression</code> for the given identifier.
     * 
     * @param name
     *            Name of the Identifier.
     * @return The implementation of an <code>IdentExpression</code>.
     */
    public abstract IdentExpression newIdentExpression(String name);

    /**
     * Returns an implementation of a <code>ConstantExpression</code> for the given constant.
     * 
     * @param data
     *            Contant.
     * @return The implementation of a <code>ConstantExpression</code>.
     */
    public abstract ConstantExpression newCostantExpression(Object data);

    /**
     * Returns an implementation of a <code>NullExpression</code> for the given expression.
     * 
     * @param expression
     *            Expression for evaluating.
     * @return The implementation of a <code>NullExpression</code>.
     */
    public abstract NullExpression newNullExpression(Expression expression);

    /**
     * Returns an implementation of a <code>NotNullExpression</code> for the given expression.
     * 
     * @param expression
     *            Expression for evaluating.
     * @return The implementation of a <code>NotNullExpression</code>.
     */
    public abstract NotNullExpression newNotNullExpression(Expression expression);

    /**
     * Returns an implementation of a <code>LogicalOrExpression</code> for the given expressions.
     * 
     * @param left
     *            Left side expression.
     * @param rigth
     *            Right side expression.
     * @return The implementation of a <code>LogicalOrExpression</code>.
     */
    public abstract LogicalOrExpression newLogicalOrExpression(Expression left,
            Expression rigth);

    /**
     * Returns an implementation of a <code>LogicalAndExpression</code> for the given expressions.
     * 
     * @param left
     *            Left side expression.
     * @param rigth
     *            Right side expression.
     * @return The implementation of a <code>LogicalAndExpression</code>.
     */
    public abstract LogicalAndExpression newLogicalAndExpression(
            Expression left, Expression rigth);

    /**
     * Returns an implementation of a <code>RelationalExpression</code> for the given elements.
     * 
     * @param left
     *            Left side expression.
     * @param rigth
     *            Right side expression.
     * @param type
     *            Valid operator supported in <code>com.code.aon.ql.ast.RelationalExpression</code>.
     * 
     * @return The implementation of a <code>RelationalExpression</code>.
     * @see com.code.aon.ql.ast.RelationalExpression
     */
    public abstract RelationalExpression newRelationalExpression(
            Expression left, Expression rigth, int type);

    /**
     * Returns an implementation of a <code>BetweenExpression</code> for the given elements.
     * 
     * @param left
     *            Left side expression.
     * @param minor
     *            Minor expression.
     * @param major
     *            Major expression.
     * 
     * @return The implementation of a <code>BetweenExpression</code>.
     */
    public abstract BetweenExpression newBetweenExpression(Expression left,
            Expression minor, Expression major);

}