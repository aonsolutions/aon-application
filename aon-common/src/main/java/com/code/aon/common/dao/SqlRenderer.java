package com.code.aon.common.dao;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
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
import com.code.aon.ql.ast.SubQueryExpression;

public class SqlRenderer implements CriterionVisitor {
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");  
	private static final String ORDER_BY = " ORDER BY ";
	private static final String DESC = " DESC";
	private final static Logger LOGGER = LoggerFactory.getLogger(SqlRenderer.class);
	private Writer out;
	private Map<String,String> tableMapping;
	private Map<String,Class<?>> pojoMapping;
	private boolean inSubQuery;
	
	public SqlRenderer(Writer out) {
		this.out = out;
	}

	public SqlRenderer(Writer out,Map<String,Class<?>> pojoMapping,Map<String,String> tableMapping) {
		this(out);
		this.pojoMapping = pojoMapping;
		this.tableMapping = tableMapping;
	}

	public void visitCriteria(Criteria criteria) {
		if (criteria.getExpression() != null) {
			criteria.getExpression().accept(this);
		}
		if (criteria.getOrderByList() != null) {
			criteria.getOrderByList().accept(this);
		}
	}

	public void visitProjection(Projection projection) {
		if ( projection.getExpression() != null ) {
			projection.getExpression().accept(this);
		}
	}
	
	public void visitProjectionList(ProjectionList projectionList) {
		Iterator<Projection> i = projectionList.getProjections().iterator();
		while (i.hasNext()) {
			Projection projection = i.next();
			projection.accept(this);
			if (i.hasNext()) {
				write(", ");
			}
		}		
	}

	@Override
	public void visitSubQueryExpression(SubQueryExpression subQuery) {
		boolean oldInSubQueryValue = this.inSubQuery;
		this.inSubQuery = true;
		write("( SELECT ");
		subQuery.getProjectionList().accept(this);
		write(" FROM ");
		String pojoName = ClassUtils.getShortClassName(subQuery.getPojo()); 
		write( tableMapping.get(pojoName) );
		write(" WHERE ");
		subQuery.getCriteria().accept(this);
		write(" )");
		this.inSubQuery = oldInSubQueryValue;
	}

	public void visitOrderByList(OrderByList orderByList) {
		Iterator<Order> i = orderByList.getOrders().iterator();
		boolean first = true;
		while (i.hasNext()) {
			if (first) {
				write(ORDER_BY);
				first = false;
			}
			Order order = (Order) i.next();
			order.accept(this);
			if (i.hasNext()) {
				write(", ");
			}
		}
	}

	public void visitOrder(Order order) {
		order.getExpression().accept(this);
		if (!order.isAscending()) {
			write(DESC);
		}
	}

	public void visitLogicalOrExpression(LogicalOrExpression expression) {
		write("( ");
		expression.getLeftExpression().accept(this);
		write(" or ");
		expression.getRightExpression().accept(this);
		write(" )");
	}

	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		// write( " ");
		expression.getLeftExpression().accept(this);
		write(" and ");
		expression.getRightExpression().accept(this);
		// write( " ");
	}

	public void visitNullExpression(NullExpression expression) {
		expression.getExpression().accept(this);
		write(" is null ");
	}

	public void visitNotNullExpression(NotNullExpression expression) {
		expression.getExpression().accept(this);
		write(" is not null ");
	}

	private String getDataString( Object data ) {
		String value = null;
		if ( data.getClass().isEnum() ) {
			Enum<?> en = (Enum<?>) data;
			value = "\'" + en.ordinal() + "\'";
		} else if ( data instanceof Date ) {
			Date date = (Date) data;
			value = "\'" + DATE_FORMATTER.format(date) + "\'";	
		} else if (data instanceof List<?>) {
			List<?> list = (List<?>) data;
			if (!list.isEmpty()) {
				StringBuffer buf = new StringBuffer();
				buf.append('(');
				for (Object o : list) {
					if (buf.length() > 1) {
						buf.append(',');
					}
					buf.append(getDataString(o));
				}
				buf.append(')');
				value = buf.toString();
			}
		} else {
			value = "\'" + data + "\'";	
		}
		return value;
	}
	
	public void visitConstantExpression(ConstantExpression expression) {
		write( getDataString(expression.getData()));
	}

	public void visitBetweenExpression(BetweenExpression expression) {
		expression.getLeftExpression().accept(this);
		write(" between ");
		expression.getMinorExpression().accept(this);
		write(" and ");
		expression.getMajorExpression().accept(this);
		write("  ");
	}

	public void visitRelationalExpression(RelationalExpression expression) {
		expression.getLeftExpression().accept(this);

		switch ( expression.getType() ) {
			case LESS_THAN:
				write(" < ");
				break;
			case GREATER_THAN:
				write(" > ");
				break;
			case EQUAL:
				write(" = ");
				break;
			case NOT_EQUAL:
				write(" <> ");
				break;
			case LIKE:
				write(" LIKE ");
				break;
			case GREATER_THAN_OR_EQUAL:
				write(" >= ");
				break;
			case LESS_THAN_OR_EQUAL:
				write(" <= ");
				break;
			case IN:
				write(" IN ");
				break;
		}
		expression.getRightExpression().accept(this);
	}

	public void visitIdentExpression(IdentExpression expression) {
		if (pojoMapping == null){
			write(expression.getName());	
		} else {
			String column = expression.getName();
			try {
				column = getSqlName(expression.getName());
			} catch (DAOException e) {
			}
			write(column);
		}
	}

	private void write(String str) {
		try {
			out.write(str);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private String getSqlName(String alias) throws DAOException {
		try {
			alias = alias.replace('<', '.');
			if ( this.inSubQuery && (StringUtils.countMatches(alias, ".")>1) && alias.endsWith(".id") ) {
				alias = StringUtils.substringBeforeLast(alias, ".id");
			}
			String table = alias.substring(0 , alias.lastIndexOf('.') );
			String property = alias.substring( alias.lastIndexOf('.') + 1 );

			Class<? extends ITransferObject> pojoClass = (Class<? extends ITransferObject>) pojoMapping.get(table);				
			IManagerBean bean = BeanManager.getManagerBean(pojoClass);
			ITransferObject to = bean.createNewTo();
			BeanUtilsBean bub = BeanUtilsBean.getInstance();
			PropertyDescriptor pd = bub.getPropertyUtils().getPropertyDescriptor(to, property);
			Method method = bub.getPropertyUtils().getReadMethod(pd);
			String columnName = property;
			Column columnAnnotation = method.getAnnotation(Column.class);
			if ( (columnAnnotation != null) && !StringUtils.isEmpty(columnAnnotation.name())) {
				columnName = columnAnnotation.name();	
			} else {
				JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
				if ( (joinColumn != null) && !StringUtils.isEmpty(joinColumn.name())) {
					columnName = joinColumn.name();
				}
			}
			String t = tableMapping.containsKey(table)?tableMapping.get(table):table;
			String ret = t + "." + columnName; 
			return ret;
		} catch (SecurityException e) {
			throw new DAOException(e.getMessage(),e);
		} catch (NoSuchMethodException e) {
			throw new DAOException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			throw new DAOException(e.getMessage(),e);
		} catch (IllegalAccessException e) {
			throw new DAOException(e.getMessage(),e);
		} catch (InvocationTargetException e) {
			throw new DAOException(e.getMessage(),e);
		}
	}
	
	
}