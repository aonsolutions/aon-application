package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.LeaveType.COMMON_DISEASE;
import static com.esferalia.aon.payroll.enumeration.LeaveType.OCCUPATIONAL_DISEASE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
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

		public String getName(ContextVariable variable) {
			if ( end != null ) {
				return String.format("%s_%d_%d", variable, start, end );
			}
			else {
				return String.format("%s_%d", variable, start);
			}
		}
	}
	
	private static class Leave extends Period{
		private LeaveType type;
		
		public Leave(Date start, 
				Date end, 
				LeaveType type) {
			super ( start, end );
			this.type = type;
		}
		
		public LeaveType getType() {
			return type;
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
	
	
	private SortedSet<Leave> leaves;
	
	public SQLContractLeaveLoader(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		leaves = new TreeSet<Leave>();
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
	
	public Long getLeaveDays(Period p, LeaveType type ) {
		long days = 0;
		for (Leave leave : leaves) {
			
			if ( leave.type != type ) {
				continue ; 
			}
			
			Period intersect = leave.intersect(p);
			if ( intersect != null ) {
				Date start = intersect.getStart();
				Date end = intersect.getEnd();
				days += CommonUtil.getDaysBetweenDates(start, end) +1;
			}
		}
		return days;
	}

	public Long getProfessionalDiseaseDays(Period p ) {
		return getLeaveDays(p, OCCUPATIONAL_DISEASE);
	}

	public Long getCommonDiseaseDays(Period p ) {
		return getLeaveDays(p, COMMON_DISEASE);
	}

	public void loadContractLevae(ResultSet rs, final ExpressionContext exprCtx)
		throws SQLException 
	{
		this.leaves.clear();

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
			
			
			type.accept(new LeaveTypeVisitor<Void>() {

				@Override
				public Void visitCommonDisease(LeaveType leaveType) {
					for (DaysRange range : RANGES) {
						long days = range.getDays(parentDays, leaveDays);
						String name = range.getName ( ContextVariable.COMMON_DISEASE_DAYS);
						exprCtx.addVariable( name , days, start, end );
					}
					exprCtx.addVariable(ContextVariable.REGULATORY_BASE, regBase, start, end );
					exprCtx.addVariable(ContextVariable.COMMON_DISEASE_DAYS, leaveDays, start, end );
					return null;
				}

				@Override
				public Void visitOcupationalDisease(LeaveType leaveType) {
					long days = parentDays ==  0 ? 
							leaveDays -1 : leaveDays; 	// Enfermedad profesional o accidente de trabajo: 
														// Desde el día siguiente al de la baja en el trabajo.
					if ( days <= 0 )
						return null;
					exprCtx.addVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS, days, start, end );
					exprCtx.addVariable(ContextVariable.REGULATORY_BASE, regBase, start, end );
					return null;
				}

				@Override
				public Void visitMaternity(LeaveType leaveType) {
					exprCtx.addVariable(ContextVariable.MATERNITY_DAYS, leaveDays, start, end );
					exprCtx.addVariable(ContextVariable.REGULATORY_BASE, regBase, start, end );
					return null;
				}

				@Override
				public Void visitPaternity(LeaveType leaveType) {
					return null;
				}

				@Override
				public Void visitPregnacyRisk(LeaveType leaveType) {
					return null;
				}

				@Override
				public Void visitBreastFeedingRisk(LeaveType leaveType) {
					return null;
				}

				@Override
				public Void visitNonOcupationalDisease(LeaveType leaveType) {
					return null;
				}
			
			});
			add( new Leave(start, end, type ) );
		}
	}
	
	private void add ( Leave leave ) {
		leaves.add(leave);
	}
	
	
	
}
