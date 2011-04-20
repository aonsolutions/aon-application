package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LeaveTypeVisitor;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractLeaveLoader  {
	
	
	private static class DaysRange {
		public Long start;
		public Long end ;
	
		public DaysRange(long start, long end) {
			this.start = start;
			this.end = end;
		}
		
		public DaysRange(long start) {
			this.start = start;
			this.end = null;
		}

		public Long getDays(long parentDays, long leaveDays) {
			long rangeEnd = end != null ? Math.min(parentDays + leaveDays, end ) :
				parentDays + leaveDays;
			long rangeStart =	Math.max(start, parentDays + 1);
			
			return rangeEnd >= rangeStart ? ( rangeEnd - rangeStart ) + 1 : 0 ;
		}

		public String getName(ContractVariables variable) {
			if ( end != null ) {
				return String.format("%s_%d_%d", variable, start, end );
			}
			else {
				return String.format("%s_%d", variable, start);
			}
		}
	}
	
	
	
	private static final DaysRange RANGES [] = {
		new DaysRange(1,3),
		new DaysRange(4,15),
		new DaysRange(16,20),
		new DaysRange(21)
	};
	
	private Date startDate;
	private Date endDate;
	
	
	private Long leavesDays;
	private Collection<Period> leaves;
	private long commonDiseaseDays;
	private long professionalDiseaseDays;
	
	public SQLContractLeaveLoader(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		leaves = new LinkedList<Period>();
	}
	
	
	
	public Long getLeavesDays() {
		return this.leavesDays;
	}
	
	public Long getCommonDiseaseDays() {
		return commonDiseaseDays;
	}
	
	public Long getProfessionalDiseaseDays() {
		return professionalDiseaseDays;
	}
	
	public boolean isLeaveDay(Calendar day) {
		Date date = day.getTime();
		for (Period leave : leaves) {
			if ( leave.contains(date) )
				return true;
		}
		return false;
	}

	public Long getLeavesDays(Period p) {
		long days = 0;
		for (Period leave : leaves) {
			Period intersect = leave.intersect(p);
			if ( intersect != null ) {
				Date start = intersect.getStart();
				Date end = intersect.getEnd();
				days += CommonUtil.getDaysBetweenDates(start, end) +1;
			}
		}
		return days;
	}
	
	public void loadContractLevae(ResultSet rs, final ExpressionContext exprCtx)
		throws SQLException 
	{
		this.leaves.clear();
		this.leavesDays = 0L;
		this.commonDiseaseDays = 0L;
		this.professionalDiseaseDays = 0L;

		while ( rs.next() ) {
			
			Date leaveStart = rs.getDate(ContractLeaveColumns.START_DATE);
			final Date start = Period.max (leaveStart , startDate );
			Date leaveEnd = rs.getDate(ContractLeaveColumns.END_DATE);
			final Date end = Period.min( leaveEnd, endDate );
			final long leaveDays = CommonUtil.getDaysBetweenDates(start, end) + 1 ; // Recuerda ambos inclusive
			final double regBase = rs.getDouble(ContractLeaveColumns.DAILY_REG_BASE);
			final long parentDays = rs.getLong(SQLContractSalaryCalculatorContext.CLEAVE_SQL_PARENT_DAYS) 
				+ (leaveStart.before(startDate ) ? CommonUtil.getDaysBetweenDates(leaveStart, startDate): 0 ) ;
			LeaveType type = LeaveType.values()[rs.getInt(ContractLeaveColumns.TYPE)]; // Los valores nulos como 0 'COMMON_SISEASE'
			
			
			this.leavesDays += type.accept(new LeaveTypeVisitor<Long>() {

				@Override
				public Long visitCommonDisease(LeaveType leaveType) {
					for (DaysRange range : RANGES) {
						long days = range.getDays(parentDays, leaveDays);
						String name = range.getName ( ContractVariables.COMMON_DISEASE_DAYS);
						exprCtx.addVariable( name , days, start, end );
					}
					exprCtx.addVariable(ContractVariables.REGULATORY_BASE, regBase, start, end );
					exprCtx.addVariable(ContractVariables.COMMON_DISEASE_DAYS, leaveDays, start, end );
					addCommonDiseaseDays(leaveDays);
					return leaveDays;
				}

				@Override
				public Long visitOcupationalDisease(LeaveType leaveType) {
					long days = parentDays ==  0 ? 
							leaveDays -1 : leaveDays; 	// Enfermedad profesional o accidente de trabajo: 
														// Desde el día siguiente al de la baja en el trabajo.
					if ( days <= 0 )
						return 0L;
					exprCtx.addVariable(ContractVariables.OCCUPATIONAL_DISEASE_DAYS, days, start, end );
					exprCtx.addVariable(ContractVariables.REGULATORY_BASE, regBase, start, end );
					addProfessionalDiseaseDays(days);
					return days;
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
			leaves.add(new Period(start, end));
		}
	}
	
	private void addCommonDiseaseDays(long days ) {
		this.commonDiseaseDays += days;
	}

	private void addProfessionalDiseaseDays(long days ) {
		this.professionalDiseaseDays += days;
	}
}
