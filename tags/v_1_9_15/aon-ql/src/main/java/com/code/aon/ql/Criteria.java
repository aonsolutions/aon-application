package com.code.aon.ql;

import com.code.aon.ql.ast.Criterion;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * Provides methods for manage <code>Expression</code> objects.
 * 
 * @author Consulting & Development. Aimar Tellitu - 20-jul-2005
 * @since 1.0
 * 
 */
public class Criteria implements Criterion {

	private Expression expression;

	private OrderByList orderByList;

	/**
	 * Returns the configured expression. Null if no operation was performed.
	 * 
	 * @return The configured expression.
	 */
	public Expression getExpression() {
		return this.expression;
	}

	/**
	 * Returns the order list. Null if no operation was performed.
	 * 
	 * @return The order list.
	 */
	public OrderByList getOrderByList() {
		return this.orderByList;
	}
	
	/**
	 * Sets the order by list.
	 * 
	 * @param orderByList the new order by list
	 */
	public void setOrderByList(OrderByList orderByList) {
		this.orderByList = orderByList;
	}

	/**
	 * Adds a equal expression to this Criteria (a=b).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param data
	 *            The right side of the operation.
	 */
	public void addEqualExpression(String identifier, Object data) {
		Expression expression = ExpressionUtilities.getEqualExpression(identifier, data);
		addExpression(expression);
	}

	/**
	 * Adds a null-equal expression to this Criteria (a=null).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 */
	public void addNullExpression(String identifier) {
		Expression expression = ExpressionUtilities.getNullExpression(identifier);
		addExpression(expression);
	}

	/**
	 * Adds a not null expression to this Criteria (a!=null).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 */
	public void addNotNullExpression(String identifier) {
		Expression expression = ExpressionUtilities.getNotNullExpression(identifier);
		addExpression(expression);
	}

	/**
	 * Adds a greather than expression to this Criteria (a>b).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param data
	 *            The right side of the operation.
	 */
	public void addGreaterThanExpression(String identifier, Object data) {
		Expression expression = ExpressionUtilities.getGreaterThanExpression(identifier, data);
		addExpression(expression);
	}

	/**
	 * Adds a greather than expression to this Criteria (a>=b).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param data
	 *            The right side of the operation.
	 */
	public void addGreaterThanOrEqualExpression(String identifier, Object data) {
		Expression expression = ExpressionUtilities.getGreaterThanOrEqualExpression(identifier, data);
		addExpression(expression);
	}

	/**
	 * Adds a less than expression to this Criteria (a<b).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param data
	 *            The right side of the operation.
	 */
	public void addLessThanExpression(String identifier, Object data) {
		Expression expression = ExpressionUtilities.getLessThanExpression(identifier, data);
		addExpression(expression);
	}

	/**
	 * Adds a less than expression to this Criteria (a<=b).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param data
	 *            The right side of the operation.
	 */
	public void addLessThanOrEqualExpression(String identifier, Object data) {
		Expression expression = ExpressionUtilities.getLessThanOrEqualExpression(identifier, data);
		addExpression(expression);
	}

	/**
	 * Adds a between expression to this Criteria (a>=minor or a<=mayor).
	 * 
	 * @param identifier
	 *            The left side of the operation.
	 * @param minor
	 *            The minor value of the range.
	 * @param mayor
	 *            The mayor value of the range.
	 */
	public void addBetweenExpression(String identifier, Object minor, Object mayor) {
		Expression expression = ExpressionUtilities.getBetweenExpression(identifier, minor, mayor);
		addExpression(expression);
	}

	/**
	 * Adds the given <code>Expression</code> to this <code>Criteria</code>
	 * using AND logical operator.
	 * 
	 * @param expression
	 *            The expression to add.
	 */
	public void addExpression(Expression expression) {
		this.expression = ExpressionUtilities.getAndExpression(this.expression, expression);
	}

	/**
	 * Compute a expression by parsing <code>value</code> and adds it to this
	 * <code>Criteria</code> using AND logical operator.
	 * 
	 * @param identifier
	 *            The identifier to apply the value.
	 * @param value
	 *            A valid QL-Expression.
	 * @throws ExpressionException
	 * 
	 * @see com.code.aon.ql.util.ExpressionUtilities#getExpression(String,String)
	 */
	public void addExpression(String identifier, String value) throws ExpressionException {
		Expression expression = ExpressionUtilities.getExpression(value, identifier);
		addExpression(expression);
	}

	/**
	 * Adds the given <code>Expression</code> to this <code>Criteria</code>
	 * using OR logical operator.
	 * 
	 * @param expression
	 *            The expression to add.
	 */
	public void addOrExpression(Expression expression) {
		this.expression = ExpressionUtilities.getOrExpression(this.expression, expression);
	}

	/**
	 * Computes a expression by parsing <code>value</code> and adds it to this
	 * <code>Criteria</code> using OR logical operator.
	 * 
	 * @param identifier
	 *            The identifier to apply the value.
	 * @param value
	 *            A valid QL-Expression.
	 * @throws ExpressionException
	 * 
	 * @see com.code.aon.ql.util.ExpressionUtilities#getExpression(String,String)
	 */
	public void addOrExpression(String identifier, String value) throws ExpressionException {
		Expression expression = ExpressionUtilities.getExpression(value, identifier);
		addOrExpression(expression);
	}

	/**
	 * Adds this identifier to the order list.
	 * 
	 * @param identifier
	 * @param ascending
	 *            <code>true</code> if the sort by this column must be
	 *            ascending.
	 */
	public void addOrder(String identifier, boolean ascending) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(identifier);
		Order order = new Order(expression, ascending);
		if (this.orderByList == null) {
			this.orderByList = new OrderByList();
		}
		this.orderByList.addOrder(order);
	}

	/**
	 * Adds this identifier to the order list. "Ascending".
	 * 
	 * @param identifier
	 */
	public void addOrder(String identifier) {
		this.addOrder(identifier, true);
	}
	
    /**
     * Returns <tt>true</tt> if this criteria is empty.
     *
     * @return <tt>true</tt> if this criteria contains no elements.
     */
	public boolean isEmpty() {
		return ( getOrderByList() == null ) && ( getExpression() == null );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
	 */
	public void accept(CriterionVisitor visitor) {
		visitor.visitCriteria(this);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Criteria[");
		if ( this.expression != null ) {
			buf.append("expression=");
			buf.append( expression );			
			if ( this.orderByList != null ) {
				buf.append(",");
			}
		}
		if ( this.orderByList != null ) {
			buf.append("orderList=");
			buf.append( orderByList );
		}
		buf.append("]");
		return buf.toString();
	}
	
}
