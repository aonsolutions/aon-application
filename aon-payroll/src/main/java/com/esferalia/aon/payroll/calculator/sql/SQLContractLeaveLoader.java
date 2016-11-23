package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.LeaveType.COMMON_DISEASE;
import static com.esferalia.aon.payroll.enumeration.LeaveType.OCCUPATIONAL_DISEASE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractLeaveLoader extends ContractLeaveLoader{


	public SQLContractLeaveLoader(Date startDate, Date endDate) {
		super(startDate, endDate);
	}


	public Long getProfessionalDiseaseDays(Period p) {
		return getLeaveDays(p, OCCUPATIONAL_DISEASE);
	}

	public Long getCommonDiseaseDays(Period p) {
		return getLeaveDays(p, COMMON_DISEASE);
	}

	public void loadContractLeave(ResultSet rs, final ExpressionContext exprCtx)
			throws SQLException, ExpressionException {

		Date leaveStart = rs.getDate(ContractLeaveColumns.START_DATE);
		final Date start = Period.max(leaveStart, startDate);
		Date leaveEnd = rs.getDate(ContractLeaveColumns.END_DATE);
		final Date end = Period.min(leaveEnd, endDate);

		final long parentDays = rs
				.getLong(SQLContractSalaryCalculatorContext.CLEAVE_SQL_PARENT_DAYS)
				+ (leaveStart.before(startDate) ? CommonUtil
						.getDaysBetweenDates(leaveStart, startDate) : 0);
		LeaveType type = LeaveType.values()[rs
				.getInt(ContractLeaveColumns.TYPE)];

		Object dailyRegBase = rs
				.getObject(ContractLeaveColumns.DAILY_REG_BASE);

		Integer id = rs.getInt(ContractLeaveColumns.ID);
		loadContractLeave(id, leaveStart, end, parentDays, type,
				(Double) dailyRegBase, exprCtx);
	}

	public void loadContractLeave(final Integer id, final Date leaveStart,
			final Date leaveEnd, final long parentDays, final LeaveType type,
			Double dailyRegBase, final ExpressionContext exprCtx)
			throws ExpressionException {
		loadContractLeave(id, leaveStart, leaveEnd, parentDays, type,
				dailyRegBase != null ? dailyRegBase.toString() : null, exprCtx);
	}	
	
	@Override
	protected double getQuoteDays(ExpressionContext ctx, Period p) {
		return super.getQuoteDays(ctx, p);
	}
	
	@Override
	protected double getAdjustDays(ExpressionContext ctx, Period p, long days) {
		return super.getAdjustDays(ctx, p, days);
	}

}
