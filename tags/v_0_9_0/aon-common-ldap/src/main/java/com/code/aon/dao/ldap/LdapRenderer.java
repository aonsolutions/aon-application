package com.code.aon.dao.ldap;

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
 * Visitante de expresiones destinado a obtener una expresion LDAP.
 * 
 * @author Consulting & Development. Aimar Tellitu - 25-abril-2008
 * @since 1.0
 * 
 */
public class LdapRenderer implements CriterionVisitor {

	/**
	 * Where the result will be printed.
	 */
	private StringBuffer out;
	
	private boolean forceLike;

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
		out.append("(| ");
		expression.getLeftExpression().accept(this);
		expression.getRightExpression().accept(this);
		out.append(" )");
	}

	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		out.append("(& ");
		expression.getLeftExpression().accept(this);
		expression.getRightExpression().accept(this);
		out.append(" )");
	}

	public void visitNullExpression(NullExpression expression) {
		out.append("(! ");
		expression.getExpression().accept(this);
		out.append("=*)");
	}

	public void visitNotNullExpression(NotNullExpression expression) {
		out.append("(");
		expression.getExpression().accept(this);
		out.append("=*)");
	}

	public void visitIdentExpression(IdentExpression expression) {
		out.append(expression.getName());
	}

	public void visitConstantExpression(ConstantExpression expression) {
		String value = expression.getData().toString();
		if ( value.indexOf("_") != -1 ) {
			this.forceLike = true;
		}
		if ( value.indexOf("%") != -1 ) {
			value = value.replace('%', '*');
		}
		out.append( value );
	}

	public void visitBetweenExpression(BetweenExpression expression) {
		/*
		expression.getLeftExpression().accept(this);
		out.append(" between ");
		expression.getMinorExpression().accept(this);
		out.append(" and ");
		expression.getMajorExpression().accept(this);
		out.append("  ");
		*/
	}

	public void visitRelationalExpression(RelationalExpression expression) {
		out.append("(");
		expression.getLeftExpression().accept(this);

		StringBuffer currentOut = out;
		this.out = new StringBuffer();

		expression.getRightExpression().accept(this);		
		
		int type = expression.getType();
		if ((type & RelationalExpression.LT) > 0) {
			currentOut.append("<");
		} else if ((type & RelationalExpression.GT) > 0) {
			currentOut.append(">");
		} else if ((type & RelationalExpression.EQ) > 0) {
			currentOut.append("=");
		} else if ((type & RelationalExpression.NEQ) > 0) {
			currentOut.append(" <> ");
		} else if ((type & RelationalExpression.LIKE) > 0) {
			if ( this.forceLike ) {
				currentOut.append("~=");
				this.forceLike = false;
			} else {
				currentOut.append("=");				
			}
		} else if ((type & RelationalExpression.GTE) > 0) {
			currentOut.append(">=");
		} else if ((type & RelationalExpression.LTE) > 0) {
			currentOut.append("<=");
		}

		this.out = currentOut.append(this.out);
		out.append(")");
	}

}