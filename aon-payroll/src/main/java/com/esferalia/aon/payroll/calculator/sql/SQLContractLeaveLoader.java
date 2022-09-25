package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.LeaveType.COMMON_DISEASE;
import static com.esferalia.aon.payroll.enumeration.LeaveType.OCCUPATIONAL_DISEASE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader.Leave;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

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

		final ITimedVariable<?> contractStartVar = 
		exprCtx.getVariable(ContextVariable.CONTRACT_START, startDate, endDate);
		Date contractStart = (Date) contractStartVar.getValue(contractStartVar.getPeriod());

		Date lastStartDate = Period.max(startDate, contractStart);
		
		final long parentDays = rs
				.getLong(SQLContractSalaryCalculatorContext.CLEAVE_SQL_PARENT_DAYS)
				+ (leaveStart.before(lastStartDate) ? CommonUtil
						.getDaysBetweenDates(leaveStart, lastStartDate) : 0)
//				+ (leaveStart.before(contractStart) ? CommonUtil
//						.getDaysBetweenDates(leaveStart, contractStart) : 0)
				;
		
		LeaveType type = LeaveType.values()[rs
				.getInt(ContractLeaveColumns.TYPE)];

		Object dailyRegBase = rs
				.getObject(ContractLeaveColumns.DAILY_REG_BASE);
		
		Integer id = rs.getInt(ContractLeaveColumns.ID);
		
		Date leaveEnd4Length = leaveEnd == null ? Period.max(endDate, new Date()) : leaveEnd;
		
		loadContractLeave(id, leaveStart, leaveEnd, parentDays, type,
				AonNumberUtils.toDouble((Number)dailyRegBase), exprCtx);
		
		Date lastDayOMonth = AonDateUtils.getLastDayOfMonth(startDate);
		
		if ( Period.compare(end , lastDayOMonth) == 0 ) 
			return;

		if ( Period.compare(end , leaveEnd) == 0 ) 
			return;
		
		Date lastLeaveStart = AonDateUtils.add(end, Calendar.DATE, 1);
		Date lastLeaveEnd = Period.min(lastDayOMonth,leaveEnd);
		if ( Period.compare(lastLeaveStart, lastLeaveEnd) <= 0)
			add(new Leave(id, 
					lastLeaveStart,
					lastLeaveEnd,
					type, 
					AonDateUtils.get(end, Calendar.DATE) - AonDateUtils.get(leaveStart, Calendar.DATE) + 1));
		
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
