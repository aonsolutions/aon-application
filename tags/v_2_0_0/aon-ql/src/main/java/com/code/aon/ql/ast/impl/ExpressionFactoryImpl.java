package com.code.aon.ql.ast.impl;

import com.code.aon.ql.ast.AbstractExpressionFactory;
import com.code.aon.ql.ast.BetweenExpression;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.LogicalAndExpression;
import com.code.aon.ql.ast.LogicalOrExpression;
import com.code.aon.ql.ast.NotNullExpression;
import com.code.aon.ql.ast.NullExpression;
import com.code.aon.ql.ast.RelationalExpression;

/**
 * Provides different implementations of <code>Expression</code>.
 * 
 * @author Consulting & Development. Raúl Trepiana - 20-nov-2003
 * @since 1.0
 *  
 */
public class ExpressionFactoryImpl extends AbstractExpressionFactory {

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newNotNullExpression(com.code.aon.ql.ast.Expression)
     */
    public NotNullExpression newNotNullExpression(Expression expression) {
        return new NotNullExpressionImpl(expression);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newNullExpression(com.code.aon.ql.ast.Expression)
     */
    public NullExpression newNullExpression(Expression expression) {
        return new NullExpressionImpl(expression);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newIdentExpression(java.lang.String)
     */
    public IdentExpression newIdentExpression(String name) {
        return new IdentExpressionImpl(name);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newCostantExpression(java.lang.Object)
     */
    public ConstantExpression newCostantExpression(Object data) {
        return new ConstantExpressionImpl(data);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newLogicalOrExpression(com.code.aon.ql.ast.Expression, com.code.aon.ql.ast.Expression)
     */
    public LogicalOrExpression newLogicalOrExpression(Expression left,
            Expression rigth) {
        return new LogicalOrExpressionImpl(left, rigth);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newLogicalAndExpression(com.code.aon.ql.ast.Expression, com.code.aon.ql.ast.Expression)
     */
    public LogicalAndExpression newLogicalAndExpression(Expression left,
            Expression rigth) {
        return new LogicalAndExpressionImpl(left, rigth);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newRelationalExpression(com.code.aon.ql.ast.Expression, com.code.aon.ql.ast.Expression, int)
     */
    public RelationalExpression newRelationalExpression(Expression left,
            Expression rigth, int type) {
        return new RelationalExpressionImpl(left, rigth, type);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.AbstractExpressionFactory#newBetweenExpression(com.code.aon.ql.ast.Expression, com.code.aon.ql.ast.Expression, com.code.aon.ql.ast.Expression)
     */
    public BetweenExpression newBetweenExpression(Expression left,
            Expression minor, Expression major) {
        return new BetweenExpressionImpl(left, minor, major);
    }

}