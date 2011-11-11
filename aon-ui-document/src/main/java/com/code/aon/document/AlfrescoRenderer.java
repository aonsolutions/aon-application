package com.code.aon.document;

import java.util.Date;

import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.lang.StringUtils;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.BetweenExpression;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.LogicalAndExpression;
import com.code.aon.ql.ast.LogicalOrExpression;
import com.code.aon.ql.ast.NotNullExpression;
import com.code.aon.ql.ast.NullExpression;
import com.code.aon.ql.ast.RelationalExpression;

/**
 * Visitante de expresiones destinado a obtener una expresion Lucene.
 * 
 * @author Consulting & Development. Aimar Tellitu - 20-oct-2011
 * @since 1.0
 * 
 */
public class AlfrescoRenderer implements CriterionVisitor {

	/**
	 * Where the result will be printed.
	 */
	private StringBuffer out;
	
	private boolean isLeftIdentifier;
	
	public String getExpression() {
		return out.toString();
	}
	
	public void visitCriteria(Criteria criteria) {
		out = new StringBuffer();
		if (criteria.getExpression() != null) {
			criteria.getExpression().accept(this);
		}
		if (criteria.getOrderByList() != null) {
			criteria.getOrderByList().accept(this);
		}
	}

	public void visitProjection(Projection projection) {
		throw new UnsupportedOperationException( "Projection not supported" );
	}
	
	public void visitProjectionList(ProjectionList projectionList) {
		throw new UnsupportedOperationException( "Projection not supported" );
	}

	public void visitOrderByList(OrderByList orderByList) {
		// Nothing to do
	}

	public void visitOrder(Order order) {
		// Nothing to do
	}

	public void visitLogicalOrExpression(LogicalOrExpression expression) {
		out.append("(");
		expression.getLeftExpression().accept(this);
		out.append(" OR ");
		expression.getRightExpression().accept(this);
		out.append(")");
	}

	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		expression.getLeftExpression().accept(this);
		out.append(" AND ");
		expression.getRightExpression().accept(this);
	}

	public void visitNullExpression(NullExpression expression) {
		out.append("ISNULL:\"");
		expression.getExpression().accept(this);
		out.append("\"");
	}

	public void visitNotNullExpression(NotNullExpression expression) {
		out.append("ISNOTNULL:\"");
		expression.getExpression().accept(this);
		out.append("\"");
	}

	public void visitIdentExpression(IdentExpression expression) {
		String id = expression.getName();
		if ( isLeftIdentifier && (id.indexOf(":") != -1) ) {
			id = "@" + BasicAlfresco.formatId(id);	
		}
		out.append(id);
	}

	public void visitConstantExpression(ConstantExpression expression) {
		Object data = expression.getData();
		if ( data instanceof Date ) {
			out.append( ISO8601DateFormat.format( (Date) data) );
		} else {
			String value = data.toString();
			if ( value.indexOf("%") != -1 ) {
				value = "\"" + StringUtils.replace(value, "%", "*") + "\""; 
			}
			out.append( value );			
		}
	}

	public void visitBetweenExpression(BetweenExpression expression) {
		this.isLeftIdentifier = true;
		expression.getLeftExpression().accept(this);
		this.isLeftIdentifier = false;
		out.append(":[");
		expression.getMinorExpression().accept(this);
		out.append(" TO ");
		expression.getMajorExpression().accept(this);
		out.append("]");
	}

	public void visitRelationalExpression(RelationalExpression expression) {
		this.isLeftIdentifier = true;
		expression.getLeftExpression().accept(this);
		this.isLeftIdentifier = false;
		
		StringBuffer currentOut = out;
		this.out = new StringBuffer();

		expression.getRightExpression().accept(this);		
		
		switch ( expression.getType() ) {
			case LESS_THAN:
				currentOut.append("<");
				break;
			case GREATER_THAN:
				currentOut.append(">");
				break;
			case EQUAL:
			case LIKE:
				currentOut.append(":");
				break;
			case NOT_EQUAL:
				currentOut.append(" <> ");
				break;
			case GREATER_THAN_OR_EQUAL:
				currentOut.append(">=");
				break;
			case LESS_THAN_OR_EQUAL:
				currentOut.append("<=");
				break;
		}

		this.out = currentOut.append(this.out);
	}

}