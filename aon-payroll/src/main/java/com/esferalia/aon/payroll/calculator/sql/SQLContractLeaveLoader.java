package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LeaveTypeVisitor;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractLeaveLoader  {
	
	private Date startDate;
	private Date endDate;
	
	public SQLContractLeaveLoader(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public long loadContractLevae(ResultSet rs, final ExpressionContext exprCtx)
		throws SQLException 
	{
		long totalLeaveDays = 0;
		while ( rs.next() ) {
			final Date start = Period.max ( rs.getDate(ContractLeaveColumns.START_DATE), startDate );
			final Date end = Period.min( rs.getDate(ContractLeaveColumns.END_DATE), endDate );
			final long leaveDays = CommonUtil.getDaysBetweenDates(start, end) + 1 ; // Recuerda ambos inclusive
			final double regBase = rs.getDouble(ContractLeaveColumns.DAILY_REG_BASE);
			LeaveType type = LeaveType.values()[rs.getInt(ContractLeaveColumns.TYPE)]; // Los valores nulos como 0 'COMMON_SISEASE'
			
			totalLeaveDays += type.accept(new LeaveTypeVisitor<Long>() {

				@Override
				public Long visitCommonDisease(LeaveType leaveType) {
					return leaveDays;
				}

				@Override
				public Long visitOcupationalDisease(LeaveType leaveType) {
					return leaveDays;
				}

				@Override
				public Long visitMaternity(LeaveType leaveType) {
					exprCtx.addVariable(ContractVariables.MATERNITY_DAYS, leaveDays, start, end );
					exprCtx.addVariable(ContractVariables.REGULATORY_BASE, regBase, start, end );
					return leaveDays;
				}

				@Override
				public Long visitPaternity(LeaveType leaveType) {
					return visitMaternity(leaveType); // Igual que maternidad
				}

				@Override
				public Long visitPregnacyRisk(LeaveType leaveType) {
					return leaveDays;
				}

				@Override
				public Long visitBreastFeedingRisk(LeaveType leaveType) {
					return leaveDays;
				}

				@Override
				public Long visitNonOcupationalDisease(LeaveType leaveType) {
					return leaveDays;
				}
			
			});
		}
		return totalLeaveDays;
	}
}
