package com.code.aon.ql;

import com.code.aon.ql.ast.Criterion;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.util.ExpressionUtilities;

// TODO: Auto-generated Javadoc
/**
 * Class that represents a SQL Projection.
 * 
 * @author Consulting & Development. Aimar Tellitu - 26-mar-2007
 * @since 1.0
 *  
 */
public class Projection implements Criterion {

	private static final long serialVersionUID = -4257093769323361196L;

	/**
	 * The Enum ProjectionType.
	 */
	public enum ProjectionType {
		
		/**
		 * Averague projection.
		 */
		AVG,
		
		/**
		 * Row count projection.
		 */
		COUNT,
		
		/**
		 * Count Distinct projection.
		 */
		COUNT_DISTINCT,
		
		/**
		 * Group by projection.
		 */
		GROUP,

		/**
		 * A projected property value.
		 */
		PROPERTY,
		
		/**
		 * Maximum projection.
		 */
		MAX,
		
		/**
		 * Minimum projection.
		 */
		MIN,
		
		/**
		 * Sum projection.
		 */
		SUM;
	}
	
	private IdentExpression expression;	

	private ProjectionType type;
	
	/**
	 * The Constructor.
	 * 
	 * @param type the type
	 */
	private Projection(ProjectionType type) {
		this.type = type;
	}

	private Projection(ProjectionType type, IdentExpression expression) {
		this( type );
		setExpression(expression);
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public ProjectionType getType() {
		return type;
	}

	/**
	 * Gets the expression.
	 * 
	 * @return the expression
	 */
	public IdentExpression getExpression() {
		return expression;
	}

	/**
	 * Sets the expression.
	 * 
	 * @param expression the expression
	 */
	public void setExpression(IdentExpression expression) {
		this.expression = expression;
	}

	/**
	 * The query row count, ie. count(*)
	 * 
	 * @return the projection
	 */
	public static Projection rowCount() {
		return new Projection(ProjectionType.COUNT);
	}

	/**
	 * A property average value
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection avg( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.AVG, expression);
	}

	/**
	 * A distinct property value count.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection countDistinct( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.COUNT_DISTINCT, expression);
	}

	/**
	 * A grouping property value.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection group( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.GROUP, expression);
	}

	/**
	 * A projected property value.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection property( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.PROPERTY, expression);
	}
	
	/**
	 * A property maximum value.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection max( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.MAX, expression);
	}

	/**
	 * A property minimum value.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection min( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.MIN, expression);
	}

	/**
	 * A property value sum.
	 * 
	 * @param propertyName the property name
	 * 
	 * @return the projection
	 */
	public static Projection sum( String propertyName ) {
		IdentExpression expression = ExpressionUtilities.getIdentifierExpression(propertyName);		
		return new Projection(ProjectionType.SUM, expression);
	}
	
	public void accept(CriterionVisitor visitor) {
        visitor.visitProjection(this);
	}
	
}
