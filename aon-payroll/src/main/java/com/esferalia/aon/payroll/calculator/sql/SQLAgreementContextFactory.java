package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.YEAR_DAYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class SQLAgreementContextFactory 
	implements LRUCacheFactory<Integer, ExpressionContext> {

	
	private static final String SYSTEM_DATA_SQL = 
		"SELECT * " 
		+" FROM `system_data`"
		+" WHERE start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";
	
	private static final String AGREEMENT_DATA_SQL = 
		"SELECT * " 
		+" FROM `agreement_level_data`"
		+" WHERE agreement_level = ? " 
		+" AND start_date <= ? "
		+" AND ( end_date IS NULL "
		+" OR end_date >= ? )";
	
	private PreparedStatement 		stmt;
	private Date					endDate;
	private Date					startDate;
	
	private ExpressionContext 		systemExpressionContext;
	
	
	public SQLAgreementContextFactory(Connection connection, Date startDate, Date endDate) 
	throws SQLException, ExpressionException
	{
		this.endDate = endDate;
		this.startDate = startDate;
		initSystemCtx(connection, startDate, endDate);
		initAgreementStmt(connection, startDate, endDate);
	}
	
	public void close() 
	throws SQLException {
		if ( stmt != null ) {
			stmt.close();
			stmt = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	@Override
	public ExpressionContext create(Integer agreementLevelId) {
		if ( agreementLevelId == null ) {
			return this.systemExpressionContext;
		} // LRUCache<K,V> as LinkedHasMap accepts null keys and/or values.
		
		ResultSet rs = null;
		try {
			stmt.setInt(1, agreementLevelId);
			rs = stmt.executeQuery();
			ExpressionContext expressionCtx = 
				new ExpressionContext(systemExpressionContext);
			while ( rs.next() ) {
				ExpressionImpl expr = 
					new ExpressionImpl();
				expr.setScope(ExpressionScope.APPLICATION);
				expr.setName(rs.getString(AgreementLevelDataColumns.NAME));
				expr.setExpression(rs.getString(AgreementLevelDataColumns.EXPRESSION));
				Date start = Period.max(rs.getDate(AgreementLevelDataColumns.START_DATE), startDate);
				Date end = Period.min ( rs.getDate(AgreementLevelDataColumns.END_DATE), endDate );
				expressionCtx.addExpression(expr, start, end );

			}
			return expressionCtx;
		}catch (ExpressionException e) {
			//TODO : ¿ Deberiamos crear una excepción espefícica como CreateException ? 
			throw new RuntimeException(e);
		}
		catch (SQLException e) {
			//TODO : ¿ Deberiamos crear una excepción espefícica como CreateException ? 
			throw new RuntimeException(e);
		}
		finally {
			if ( rs != null )
				try {
					rs.close();
				} catch (SQLException e) {
					//TODO : ¿ Debemos lanzar una excpeción o no ? 
				}
				rs = null; // Para el garbage collector 
		}
	}

	// ------------------------------------------
	// La ropa interior 
	// ------------------------------------------
	private void initAgreementStmt(Connection connection, Date startDate, Date endDate)
	throws SQLException {
		this.stmt  = 
			connection.prepareStatement(AGREEMENT_DATA_SQL);
		this.stmt.setDate(2, new java.sql.Date(endDate.getTime()) );
		this.stmt.setDate(3, new java.sql.Date(startDate.getTime()) );
	}

	private void initSystemCtx(Connection connection, Date startDate, Date endDate) throws SQLException, ExpressionException {
		this.systemExpressionContext = 
			new ExpressionContext();
		
		Long yearDays = getYearDays(startDate, endDate ); 
		systemExpressionContext.addVariable(YEAR_DAYS, yearDays, startDate, endDate);

		Long monthDays = getMonthDays(startDate, endDate ); 
		systemExpressionContext.addVariable(MONTH_DAYS, monthDays, startDate, endDate);
		
		loadSystemData(connection, startDate, endDate, systemExpressionContext);
	}

	private void loadSystemData(Connection connection, Date startDate, Date endDate, ExpressionContext expressionCtx) 
	throws SQLException, ExpressionException {
		ResultSet rs = null;
		PreparedStatement stmt = null; 
		try {
			stmt = 
				connection.prepareStatement(SYSTEM_DATA_SQL);
			stmt.setDate(1, new java.sql.Date( endDate.getTime() ));
			stmt.setDate(2, new java.sql.Date( startDate.getTime() ));
			rs = stmt.executeQuery();
			while ( rs.next() ) {
				ExpressionImpl expr = 
					new ExpressionImpl();
				expr.setName(rs.getString(SystemDataColumns.NAME));
				expr.setExpression(rs.getString(SystemDataColumns.EXPRESSION));
				expr.setScope(ExpressionScope.APPLICATION);
				Date start = Period.max(rs.getDate(SystemDataColumns.START_DATE), startDate);
				Date end = Period.min ( rs.getDate(SystemDataColumns.END_DATE), endDate );
				expressionCtx.addExpression(expr, start, end );
			}
		}finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}

	
	private Long getYearDays(Date startDate, Date endDate) {
		Date startDay = CommonUtil.getYearFirstDay(startDate);
		Date endDay = CommonUtil.getYearLastDay(endDate);
		long yearDays = 
			CommonUtil.getDaysBetweenDates(startDay, endDay);
		yearDays += 1;
		return yearDays;
	}

	private Long getMonthDays(Date startDate, Date endDate) {
		Date startDay = CommonUtil.getMonthFirstDay(startDate);
		Date endDay = CommonUtil.getMonthLastDay(endDate);
		Long monthDays = CommonUtil.getDaysBetweenDates(startDay, endDay);
		monthDays += 1;
		return monthDays;
	}

	

	
	
}
