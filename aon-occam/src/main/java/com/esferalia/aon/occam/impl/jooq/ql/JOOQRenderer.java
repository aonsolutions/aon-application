package com.esferalia.aon.occam.impl.jooq.ql;

import static com.esferalia.aon.jooq.AonMaster.AON_MASTER;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;

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
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class JOOQRenderer implements CriterionVisitor {
	
	private Condition condition;
	
	private Condition currentCondition;
	
	private Field<?> identifier;
	private String data;
	private List<Pair<String,String>> conditions;
			
	public JOOQRenderer() {
	}
	
	public Condition getCondition() throws ExpressionException {
		final Criteria c = new Criteria();
		getConditions().stream().forEach(p -> {
			try {
				c.addExpression( p.getLeft(), p.getRight() );
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
		});
		visitCriteria(c);
		return condition;
	}
	
	public List<Pair<String,String>> getConditions() {
		if (conditions == null) {
			conditions = new LinkedList<Pair<String,String>>();
		}
		return conditions;
	}

	public JOOQRenderer put(Field<?> field, String string) {
		getConditions().add( new Pair<>(AonStringUtils.remove(field.getQualifiedName().toString(), "\""), string));
		return this;
	}
	

	public void visitCriteria(Criteria criteria) {
		if (criteria.getExpression() != null) {
			criteria.getExpression().accept(this);
			if ( condition == null) {
				condition = currentCondition;
			} else {
				condition = condition.and(currentCondition);
			}
		}
	}

	@Override
	public void visitProjection(Projection projection) {
		throw new UnsupportedOperationException( "Projection not supported" );
	}
	
	@Override
	public void visitProjectionList(ProjectionList projectionList) {
		throw new UnsupportedOperationException( "Projection not supported" );
	}

	@Override
	public void visitSubQueryExpression(SubQueryExpression expression) {
		throw new UnsupportedOperationException( "Projection not supported" );
	}

	@Override
	public void visitOrderByList(OrderByList orderByList) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void visitOrder(Order order) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void visitIdentExpression(IdentExpression expression) {
		String tableName = AonStringUtils.substringBefore(expression.getName(), ".");
		if (AonStringUtils.isBlank(tableName)) {
			throw new IllegalArgumentException("Table name is mandatory.");	
		}
		String column = AonStringUtils.substringAfter(expression.getName(), ".");
		if (AonStringUtils.isBlank(column)) {
			throw new IllegalArgumentException("Column name is mandatory.");	
		}
		Table<?> table = AON_MASTER.getTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException(MessageFormat.format("Table {0} not found in AON MASTER schema.",tableName));
		}
		identifier = table.field(column);
		if (identifier == null) {
			throw new IllegalArgumentException(MessageFormat.format("Column {0} not found in table {1}.",column,tableName));
		}
		
	}

	public void visitConstantExpression(ConstantExpression expression) {
		data = expression.getData()!=null?expression.getData().toString():null;
		System.out.println( "data ..: " + data);
	}

	public void visitRelationalExpression(RelationalExpression expression) {
		expression.getLeftExpression().accept(this);
		expression.getRightExpression().accept(this);
		switch ( expression.getType() ) {
			case LESS_THAN:
				currentCondition = lessThan(identifier, data );
				break;
			case GREATER_THAN:
				currentCondition = greaterThan(identifier, data );
				break;
			case EQUAL:
				currentCondition = equal(identifier, data );
				break;
			case NOT_EQUAL:
				currentCondition = notEqual(identifier, data );
				break;
			case LIKE:
				currentCondition = like(identifier, data );
				break;
			case GREATER_THAN_OR_EQUAL:
				currentCondition = greaterOrEqual(identifier, data );
				break;
			case LESS_THAN_OR_EQUAL:
				currentCondition = lessOrEqual(identifier, data );
				break;
			case IN:
				throw new UnsupportedOperationException("IN operator not implemented");
		}
	}

	public void visitLogicalOrExpression(LogicalOrExpression expression) {
		expression.getLeftExpression().accept(this);
		Condition left = currentCondition;
		expression.getRightExpression().accept(this);
		Condition right = currentCondition;
		currentCondition = left.or(right);
	}

	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		expression.getLeftExpression().accept(this);
		Condition left = currentCondition;
		expression.getRightExpression().accept(this);
		Condition right = currentCondition;
		currentCondition = left.and(right);
	}

	public void visitNullExpression(NullExpression expression) {
		expression.getExpression().accept(this);
		currentCondition = identifier.isNull();
	}

	public void visitNotNullExpression(NotNullExpression expression) {
		expression.getExpression().accept(this);
		currentCondition = identifier.isNotNull();
	}

	public void visitBetweenExpression(BetweenExpression expression) {
		expression.getLeftExpression().accept(this);
		expression.getMinorExpression().accept(this);
		String minor = data;
		expression.getMajorExpression().accept(this);
		String major = data;
		currentCondition = between(identifier,minor, major);
	}
	
	private static <T> Condition lessThan(Field<T> field, String value ) {
		return field.lessThan( fromString(field, value) );
	}
	private static <T> Condition lessOrEqual(Field<T> field, String value ) {
		return field.lessOrEqual( fromString(field, value) );
	}
	private static <T> Condition greaterThan(Field<T> field, String value ) {
		return field.greaterThan( fromString(field, value) );
	}
	private static <T> Condition greaterOrEqual(Field<T> field, String value ) {
		return field.greaterOrEqual( fromString(field, value) );
	}
	private static <T> Condition equal(Field<T> field, String value ) {
		return field.equal( fromString( field, value) );
	}
	private static <T> Condition notEqual(Field<T> field, String value ) {
		return field.notEqual( fromString( field, value) );
	}
	private static <T> Condition like(Field<T> field, String value ) {
		return field.like( value );
	}
	private static <T> Condition between(Field<T> field, String minor, String major ) {
		return field.between( fromString( field, minor), fromString( field, major ) );
	}
	
	private static <T> T fromString(Field<T> field, String value) {
		if (field== null) return null;
		ConsoleTableFieldType type = getConsoleTableFieldType(field);
		if (type== null) return null;
		if (value == null) return null;
		return type.visit(new ConsoleTableFieldType.Visitor<T>() {
			@Override public T visitString() { return field.getType().cast( value ); }
			@Override public T visitByte() { return  field.getType().cast( AonNumberUtils.toByte( value)); }
			@Override public T visitShort() { return  field.getType().cast( AonNumberUtils.toShort( value)); }
			@Override public T visitInteger() { return  field.getType().cast( AonNumberUtils.toInteger( value)); }
			@Override public T visitDouble(){ return  field.getType().cast( AonNumberUtils.toDouble( value)); }
			@Override public T visitDecimal(){ return  field.getType().cast( BigDecimal.valueOf( AonNumberUtils.toDouble( value) )); }
			@Override public T visitDate() {return field.getType().cast( AonDateUtils.toSql( AonDateUtils.simpleParse( value ) )); }
			@Override public T visitTimestamp() {return field.getType().cast( AonDateUtils.dateTimeParse( value )); }
			@Override public T visitBinary() { return null; }
		});
	}
	
	private static ConsoleTableFieldType getConsoleTableFieldType(Field<?> field) {
		if (field.getDataType() == null ) return null;
		if (field.getDataType().getSQLDataType().isString()) return ConsoleTableFieldType.STRING;
		else if (field.getDataType().getSQLDataType() == SQLDataType.DOUBLE) return ConsoleTableFieldType.DOUBLE;
		else if (field.getDataType().getSQLDataType() == SQLDataType.DECIMAL) return ConsoleTableFieldType.DECIMAL;
		else if (field.getDataType().getSQLDataType() == SQLDataType.TINYINT) return ConsoleTableFieldType.BYTE;
		else if (field.getDataType().getSQLDataType() == SQLDataType.SMALLINT) return ConsoleTableFieldType.SHORT;
		else if (field.getDataType().getSQLDataType().isInteger()) return ConsoleTableFieldType.INTEGER;
		else if (field.getDataType().getSQLDataType().isDate()) return ConsoleTableFieldType.DATE;
		else if (field.getDataType().getSQLDataType().isTimestamp()) return ConsoleTableFieldType.TIMESTAMP;
		else if (field.getDataType().getSQLDataType().isBinary()) return ConsoleTableFieldType.BINARY;
		
		throw new AonCoreException("El tipo SQL [" + field.getDataType().getSQLDataType().getName() + "] no está soportado.");
	}
}