package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.orderBy;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.YEAR_DAYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import javax.xml.ws.handler.MessageContext.Scope;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.AonConstants;
import com.esferalia.aon.payroll.calculator.AonFunctions;
import com.esferalia.aon.payroll.calculator.ContextFunctions;
import com.esferalia.aon.payroll.calculator.ExcelFunctions;
import com.esferalia.aon.payroll.calculator.LRUCacheFactory;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.CCCContextKey;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class SQLSystemExpressionContextFactory implements
		LRUCacheFactory<CCCContextKey, ExpressionContext> {

	public static final double DEFAULT_DAY_HOURS = 8;
	public static final double DEFAULT_AGRREEMENT_HOURS = 40;
	
	private static final String SYSTEM_DATA_SQL = "SELECT * "
			+ " FROM `system_data`" + " WHERE start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )"
			+ " AND domain IN (0,?,?) " 
			+ " ORDER BY domain DESC, start_date ASC";

	private static Long getYearDays(Date startDate, Date endDate) {
		Date startDay = CommonUtil.getYearFirstDay(startDate);
		Date endDay = CommonUtil.getYearLastDay(endDate);
		long yearDays = CommonUtil.getDaysBetweenDates(startDay, endDay);
		yearDays += 1;
		return yearDays;
	}

	private static void initMonthVariables(ExpressionContext ctx,
			Date startDate, Date endDate) {

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
			ctx.setVariable(PAY_DAYS, monthDays, monthStart, monthEnd);
			ctx.setVariable(NATURAL_MONTH_DAYS, monthDays, monthStart, monthEnd);

			startCalendar.set(Calendar.DAY_OF_MONTH, 1);
			startCalendar.add(Calendar.MONTH, 1);
		}

	}

	private static void loadSystemData(Connection connection, Date startDate,
			Date endDate, ExpressionContext expressionCtx, CCCContextKey key,
			OrderByList order) throws SQLException, ExpressionException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			String sql = orderBy(SYSTEM_DATA_SQL, order);
			stmt = connection.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(endDate.getTime()));
			stmt.setDate(2, new java.sql.Date(startDate.getTime()));

			if (key.getSSRegime() != null)
				stmt.setInt(3, SQLContractSalaryCalculatorContext.getDomain(key
						.getSSRegime()));
			else
				stmt.setNull(3, Types.INTEGER);

			if (key.getCCC() != null)
				stmt.setInt(4, SQLContractSalaryCalculatorContext.getDomain(key
						.getCCC()));
			else
				stmt.setNull(3, Types.INTEGER);

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

	private static ExpressionContext newSystemCtx(Connection connection,
			Date startDate, Date endDate, CCCContextKey cccCtxKey,
			OrderByList order) {
		ExpressionContext systemExpressionContext = new ExpressionContext();

		// TODO: Defaults. Must they be at `system_data` instead of hardcoded
		// here?
//		systemExpressionContext.setVariable(MONDAY_HOURS,
//				DEFAULT_DAY_HOURS, startDate, endDate);
//		systemExpressionContext.setVariable(TUESDAY_HOURS,
//				DEFAULT_DAY_HOURS, startDate, endDate);
//		systemExpressionContext.setVariable(WEDNESDAY_HOURS,
//				DEFAULT_DAY_HOURS, startDate, endDate);
//		systemExpressionContext.setVariable(THURSDAY_HOURS,
//				DEFAULT_DAY_HOURS, startDate, endDate);
//		systemExpressionContext.setVariable(FRIDAY_HOURS,
//				DEFAULT_DAY_HOURS, startDate, endDate);
//		systemExpressionContext.setVariable(SATURDAY_HOURS,
//				0, startDate, endDate);
//		systemExpressionContext.setVariable(SUNDAY_HOURS,
//				0, startDate, endDate);
		systemExpressionContext.setVariable(AGREEMENT_HOURS,
				DEFAULT_AGRREEMENT_HOURS, startDate, endDate);

		AonConstants.load(systemExpressionContext, startDate, endDate);
		AonFunctions.load(systemExpressionContext, startDate, endDate);

		Long yearDays = getYearDays(startDate, endDate);
		systemExpressionContext.setVariable(YEAR_DAYS, yearDays, startDate,
				endDate);

		initMonthVariables(systemExpressionContext, startDate, endDate);

		systemExpressionContext.setVariable(SALARY_START, startDate, startDate,
				endDate);
		systemExpressionContext.setVariable(SALARY_END, endDate, startDate,
				endDate);

		ExcelFunctions.load(systemExpressionContext, startDate, endDate);
		try {
			ContextFunctions.loadFunctions(systemExpressionContext, startDate,
					endDate);
		} catch (ExpressionException e) {
			// TODO:
		}

		try {
			loadSystemData(connection, startDate, endDate,
					systemExpressionContext, cccCtxKey, order);
		} catch (ExpressionException | SQLException e) {
			// TODO:
		}
		return systemExpressionContext;
	}

	private Date endDate;
	private Date startDate;
	private Connection connection;
	private OrderByList order;

	public SQLSystemExpressionContextFactory(Connection connection,
			Date startDate, Date endDate, OrderByList order) {
		this.connection = connection;
		this.startDate = startDate;
		this.endDate = endDate;
		this.order = order;
	}

	// ------------------------------------------------------------------------
	//

	@Override
	public ExpressionContext create(CCCContextKey key) {
		return newSystemCtx(connection, startDate, endDate, key, order);
	}
}