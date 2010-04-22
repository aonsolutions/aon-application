package com.code.aon.ql.ast.sql;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Visitante de expresiones destinado a obtener una clausa WHERE de una
 * sentencia SQL.
 * 
 * @author Consulting & Development. Raúl Trepiana - 19-nov-2003
 * @since 1.0
 * 
 */
public class SqlRenderer implements CriterionVisitor {

	/**
	 * SQL ORDER BY.
	 */
	private static final String ORDER_BY = " ORDER BY ";

	/**
	 * SQL DESC.
	 */
	private static final String DESC = " DESC";

	/**
	 * Obtains a suitable <code>Logger</code>.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(SqlRenderer.class);

	/**
	 * Where the result will be printed.
	 */
	private Writer out;

	/**
	 * Constructor giving a Writer where the result will be printed.
	 * 
	 * @param out
	 *            A Writer where the result will be printed.
	 */
	public SqlRenderer(Writer out) {
		this.out = out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitCriteria(com.code.aon.ql.Criteria)
	 */
	public void visitCriteria(Criteria criteria) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitOrderByList(com.code.aon.ql.OrderByList)
	 */
	public void visitOrderByList(OrderByList orderByList) {
		write(ORDER_BY);
		Iterator<Order> i = orderByList.getOrders().iterator();
		while (i.hasNext()) {
			Order order = (Order) i.next();
			order.accept(this);
			if (i.hasNext()) {
				write(", ");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitOrder(com.code.aon.ql.Order)
	 */
	public void visitOrder(Order order) {
		order.getExpression().accept(this);
		if (!order.isAscending()) {
			write(DESC);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitLogicalOrExpression(com.code.aon.ql.ast.LogicalOrExpression)
	 */
	public void visitLogicalOrExpression(LogicalOrExpression expression) {
		write("( ");
		expression.getLeftExpression().accept(this);
		write(" or ");
		expression.getRightExpression().accept(this);
		write(" )");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitLogicalAndExpression(com.code.aon.ql.ast.LogicalAndExpression)
	 */
	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		// write( " ");
		expression.getLeftExpression().accept(this);
		write(" and ");
		expression.getRightExpression().accept(this);
		// write( " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitNullExpression(com.code.aon.ql.ast.NullExpression)
	 */
	public void visitNullExpression(NullExpression expression) {
		expression.getExpression().accept(this);
		write(" is null ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitNotNullExpression(com.code.aon.ql.ast.NotNullExpression)
	 */
	public void visitNotNullExpression(NotNullExpression expression) {
		expression.getExpression().accept(this);
		write(" is not null ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitIdentExpression(com.code.aon.ql.ast.IdentExpression)
	 */
	public void visitIdentExpression(IdentExpression expression) {
		write(expression.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitConstantExpression(com.code.aon.ql.ast.ConstantExpression)
	 */
	public void visitConstantExpression(ConstantExpression expression) {
		write("\'" + expression.getData() + "\'"); //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitBetweenExpression(com.code.aon.ql.ast.BetweenExpression)
	 */
	public void visitBetweenExpression(BetweenExpression expression) {
		expression.getLeftExpression().accept(this);
		write(" between ");
		expression.getMinorExpression().accept(this);
		write(" and ");
		expression.getMajorExpression().accept(this);
		write("  ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitRelationalExpression(com.code.aon.ql.ast.RelationalExpression)
	 */
	public void visitRelationalExpression(RelationalExpression expression) {
		expression.getLeftExpression().accept(this);

		int type = expression.getType();

		if ((type & RelationalExpression.LT) > 0) {
			write(" < ");
		} else if ((type & RelationalExpression.GT) > 0) {
			write(" > ");
		} else if ((type & RelationalExpression.EQ) > 0) {
			write(" = ");
		} else if ((type & RelationalExpression.NEQ) > 0) {
			write(" <> ");
		} else if ((type & RelationalExpression.LIKE) > 0) {
			write(" LIKE ");
		} else if ((type & RelationalExpression.GTE) > 0) {
			write(" >= ");
		} else if ((type & RelationalExpression.LTE) > 0) {
			write(" <= ");
		}
		expression.getRightExpression().accept(this);
	}

	/**
	 * @param str
	 */
	private void write(String str) {
		try {
			out.write(str);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}