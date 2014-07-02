package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.YEAR_DAYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.AonConstants;
import com.esferalia.aon.payroll.calculator.AonFunctions;
import com.esferalia.aon.payroll.calculator.ContextFunctions;
import com.esferalia.aon.payroll.calculator.ExcelFunctions;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.AgreementContextKey;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class SQLAgreementContextFactory implements
		LRUCacheFactory<AgreementContextKey, ExpressionContext> {

	private static final String SYSTEM_DATA_SQL = "SELECT * "
			+ " FROM `system_data`" + " WHERE start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )"
			+ " AND domain = ? ";

	private static final String AGREEMENT_DATA_SQL = "SELECT * "
			+ " FROM `agreement_data`" + " WHERE agreement = ? "
			+ " AND start_date <= ? " + " AND ( end_date IS NULL "
			+ " OR end_date >= ? )";

	private static final String AGREEMENT_LEVEL_DATA_SQL = "SELECT * "
			+ " FROM `agreement_level_data`" + " WHERE agreement_level = ? "
			+ " AND start_date <= ? " + " AND ( end_date IS NULL "
			+ " OR end_date >= ? )";

	private Date endDate;
	private Date startDate;

	private PreparedStatement dataStmts[];
	private PreparedStatement agreementDataStmt;
	private PreparedStatement agreementLevelDataStmt;

	private ExpressionContext systemExpressionContext;

	private LRUCache<Integer, ExpressionContext> agreementDataCache;

	// private SQLContractSalaryCalculatorContext ctx;

	public SQLAgreementContextFactory(Connection conn, Date startDate,
			Date endDate, OrderByList order) throws SQLException,
			ExpressionException {
		this.endDate = endDate;
		this.startDate = startDate;
		initAgreementDataContextCache();
		initSystemCtx(conn, startDate, endDate);
		initAgreementStmt(conn, startDate, endDate, order);
	}

	public void close() throws SQLException {
		for (int i = 0; i < dataStmts.length; i++) {
			if (dataStmts[i] != null) {
				dataStmts[i].close();
				dataStmts[i] = null;
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public ExpressionContext create(Integer agreementId) {
		try {
			ExpressionContext expressionCtx = new ExpressionContext(
					systemExpressionContext);

			agreementDataStmt.setInt(1, agreementId);
			loadData(agreementDataStmt, expressionCtx);

			return expressionCtx;
		} catch (SQLException e) {
			// TODO : ¿ Deberiamos crear una excepción espefícica como
			// CreateException ?
			throw new RuntimeException(e);
		}
	}

	public ExpressionContext create(AgreementContextKey key) {

		if (key == null || key.getAgreementId() == null) {
			return this.systemExpressionContext;
		} // LRUCache<K,V> as LinkedHasMap accepts null keys and/or values.

		try {
			ExpressionContext parentContext = agreementDataCache.get(key
					.getAgreementId());
			ExpressionContext expressionCtx = new ExpressionContext(
					parentContext);

			agreementLevelDataStmt.setInt(1, key.getAgreementLevelId());
			loadData(agreementLevelDataStmt, expressionCtx);

			return expressionCtx;
		} catch (SQLException e) {
			// TODO : ¿ Deberiamos crear una excepción espefícica como
			// CreateException ?
			throw new RuntimeException(e);
		}
	};

	public ExpressionContext getSystemExpressionContext() {
		return systemExpressionContext;
	}

	public ExpressionContext getAgreementDataContext(int agreementId) {
		return agreementDataCache.get(agreementId);
	}

	// ------------------------------------------
	// La ropa interior
	// ------------------------------------------

	private void loadData(PreparedStatement stmt, ExpressionContext context)
			throws SQLException {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();
			while (rs.next()) {
				ExpressionImpl expr = new ExpressionImpl();
				expr.setScope(ExpressionScope.AGREEMENT);
				expr.setName(rs.getString(AgreementLevelDataColumns.NAME));
				expr.setExpression(rs
						.getString(AgreementLevelDataColumns.EXPRESSION));
				Date start = Period.max(
						rs.getDate(AgreementLevelDataColumns.START_DATE),
						startDate);
				Date end = Period
						.min(rs.getDate(AgreementLevelDataColumns.END_DATE),
								endDate);
				try {
					context.addExpression(expr, start, end);
				} catch (Exception e) {
					DeferredExpressionVariable<Object> variable = new DeferredExpressionVariable<Object>(
							start, end, expr);
					context.putVariable(expr.getName(), variable);
				}
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private void initAgreementStmt(Connection connection, Date startDate,
			Date endDate, OrderByList orderByList) throws SQLException {
		dataStmts = new PreparedStatement[2];

		String agreementDataSql = SQLContractSalaryCalculatorContext.orderBy(
				AGREEMENT_DATA_SQL, orderByList);

		agreementDataStmt = connection.prepareStatement(agreementDataSql);
		agreementDataStmt.setDate(2, new java.sql.Date(endDate.getTime()));
		agreementDataStmt.setDate(3, new java.sql.Date(startDate.getTime()));
		dataStmts[0] = agreementDataStmt;

		String agreementLevelDataSql = SQLContractSalaryCalculatorContext
				.orderBy(AGREEMENT_LEVEL_DATA_SQL, orderByList);
		agreementLevelDataStmt = connection
				.prepareStatement(agreementLevelDataSql);
		agreementLevelDataStmt.setDate(2, new java.sql.Date(endDate.getTime()));
		agreementLevelDataStmt.setDate(3,
				new java.sql.Date(startDate.getTime()));
		dataStmts[1] = agreementLevelDataStmt;
	}

	private void initSystemCtx(Connection connection, Date startDate,
			Date endDate) throws SQLException, ExpressionException {
		this.systemExpressionContext = new ExpressionContext();

		AonConstants.load(systemExpressionContext, startDate, endDate);
		AonFunctions.load(systemExpressionContext, startDate, endDate);

		Long yearDays = getYearDays(startDate, endDate);
		systemExpressionContext.setVariable(YEAR_DAYS, yearDays, startDate,
				endDate);

		/*
		 * Long monthDays = getMonthDays(startDate, endDate );
		 * systemExpressionContext.addVariable(MONTH_DAYS, monthDays, startDate,
		 * endDate);
		 */
		initMonthVariables(systemExpressionContext, startDate, endDate);

		loadSystemData(connection, startDate, endDate, systemExpressionContext);

		systemExpressionContext.setVariable(SALARY_START, startDate, startDate,
				endDate);
		systemExpressionContext.setVariable(SALARY_END, endDate, startDate,
				endDate);

		ExcelFunctions.load(systemExpressionContext, startDate, endDate);
		ContextFunctions.loadFunctions(systemExpressionContext, startDate,
				endDate);
	}

	private void initMonthVariables(ExpressionContext ctx, Date startDate,
			Date endDate) {

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		startCalendar.set(Calendar.DAY_OF_MONTH, 1);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);

		while (startCalendar.compareTo(endCalendar) <= 0) {
			int monthDays = startCalendar
					.getActualMaximum(Calendar.DAY_OF_MONTH);

			Date monthStart = startCalendar.getTime();

			startCalendar.set(Calendar.DAY_OF_MONTH, monthDays);
			Date monthEnd = startCalendar.getTime();

			ctx.setVariable(MONTH_DAYS, monthDays, monthStart, monthEnd);

			startCalendar.set(Calendar.DAY_OF_MONTH, 1);
			startCalendar.add(Calendar.MONTH, 1);
		}

	}

	private void loadSystemData(Connection connection, Date startDate,
			Date endDate, ExpressionContext expressionCtx) throws SQLException,
			ExpressionException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(SYSTEM_DATA_SQL);
			stmt.setDate(1, new java.sql.Date(endDate.getTime()));
			stmt.setDate(2, new java.sql.Date(startDate.getTime()));
			stmt.setInt(3, SQLPayrollConstants.DOMAIN_ZERO);
			rs = stmt.executeQuery();
			while (rs.next()) {
				ExpressionImpl expr = new ExpressionImpl();
				expr.setName(rs.getString(SystemDataColumns.NAME));
				expr.setExpression(rs.getString(SystemDataColumns.EXPRESSION));
				expr.setScope(ExpressionScope.SYSTEM);
				Date start = Period.max(
						rs.getDate(SystemDataColumns.START_DATE), startDate);
				Date end = Period.min(rs.getDate(SystemDataColumns.END_DATE),
						endDate);
				try {
					expressionCtx.addExpression(expr, start, end);
				} catch (Exception e) {
					DeferredExpressionVariable<Object> variable = new DeferredExpressionVariable<Object>(
							start, end, expr);
					expressionCtx.putVariable(expr.getName(), variable);
				}
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private Long getYearDays(Date startDate, Date endDate) {
		Date startDay = CommonUtil.getYearFirstDay(startDate);
		Date endDay = CommonUtil.getYearLastDay(endDate);
		long yearDays = CommonUtil.getDaysBetweenDates(startDay, endDay);
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

	private void initAgreementDataContextCache() {
		LRUCacheFactory<Integer, ExpressionContext> factory = new LRUCacheFactory<Integer, ExpressionContext>() {
			@Override
			public ExpressionContext create(Integer agreementId) {
				return SQLAgreementContextFactory.this.create(agreementId);
			}
		};
		this.agreementDataCache = new LRUCache<Integer, ExpressionContext>(25,
				factory);
	}

}
