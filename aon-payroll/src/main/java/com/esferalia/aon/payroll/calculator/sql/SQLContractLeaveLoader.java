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
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractLeaveLoader {

	public static class Leave extends Period {
		private Integer id;
		private LeaveType type;

		public Leave(Integer id, Date start, Date end, LeaveType type) {
			super(start, end);
			this.id = id;
			this.type = type;
		}

		public Integer getId() {
			return id;
		}

		public LeaveType getType() {
			return type;
		}

	}

	private static class DaysRange {
		public Long start;
		public Long end;

		public DaysRange(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public DaysRange(long start) {
			this.start = start;
			this.end = null;
		}

		public Long getDays(long parentDays, long leaveDays) {
			long rangeEnd = end != null ? Math.min(parentDays + leaveDays, end)
					: parentDays + leaveDays;
			long rangeStart = Math.max(start, parentDays + 1);

			return rangeEnd >= rangeStart ? (rangeEnd - rangeStart) + 1 : 0;
		}

		public String getName(ContextVariable variable) {
			if (end != null) {
				return String.format("%s_%d_%d", variable, start, end);
			} else {
				return String.format("%s_%d", variable, start);
			}
		}
	}

	private static final DaysRange RANGES[] = { new DaysRange(1, 3),
			new DaysRange(4, 15), new DaysRange(16, 20), new DaysRange(21) };

	private Date startDate;
	private Date endDate;

	private SortedSet<Leave> leaves;

	public SQLContractLeaveLoader(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		leaves = new TreeSet<Leave>();
	}

	public boolean isEmpty() {
		return leaves.isEmpty();
	}

	public boolean isLeaveDay(Calendar day) {
		Date date = day.getTime();
		for (Period leave : leaves) {
			if (leave.contains(date))
				return true;
		}
		return false;
	}

	public Long getLeavesDays(Period p) {
		long days = 0;
		for (Period leave : leaves) {
			Period intersect = leave.intersect(p);
			if (intersect != null) {
				Date start = intersect.getStart();
				Date end = intersect.getEnd();
				days += CommonUtil.getDaysBetweenDates(start, end) + 1;
			}
		}
		return days;
	}

	public Long getLeaveDays(Period p, LeaveType type) {
		long days = 0;
		for (Leave leave : leaves) {

			if (leave.type != type) {
				continue;
			}

			Period intersect = leave.intersect(p);
			if (intersect != null) {
				Date start = intersect.getStart();
				Date end = intersect.getEnd();
				days += CommonUtil.getDaysBetweenDates(start, end) + 1;
			}
		}
		return days;
	}

	public Long getProfessionalDiseaseDays(Period p) {
		return getLeaveDays(p, OCCUPATIONAL_DISEASE);
	}

	public Long getCommonDiseaseDays(Period p) {
		return getLeaveDays(p, COMMON_DISEASE);
	}

	public void loadContractLeave(ResultSet rs, final ExpressionContext exprCtx)
			throws SQLException, ExpressionException {
		this.leaves.clear();

		while (rs.next()) {
			Date leaveStart = rs.getDate(ContractLeaveColumns.START_DATE);
			final Date start = Period.max(leaveStart, startDate);
			Date leaveEnd = rs.getDate(ContractLeaveColumns.END_DATE);
			final Date end = Period.min(leaveEnd, endDate);
			/*
			 * final Object regBase =
			 * rs.getObject(ContractLeaveColumns.DAILY_REG_BASE) != null ?
			 * rs.getDouble(ContractLeaveColumns.DAILY_REG_BASE) :
			 * exprCtx.eval(String.format("%s(%s)", ContextVariable.BR,
			 * ContextVariable.IT_START) , start, end );
			 */
			final long parentDays = rs
					.getLong(SQLContractSalaryCalculatorContext.CLEAVE_SQL_PARENT_DAYS)
					+ (leaveStart.before(startDate) ? CommonUtil
							.getDaysBetweenDates(leaveStart, startDate) : 0);
			LeaveType type = LeaveType.values()[rs
					.getInt(ContractLeaveColumns.TYPE)];

			Object dailyRegBase = rs
					.getObject(ContractLeaveColumns.DAILY_REG_BASE);
			Integer id = rs.getInt(ContractLeaveColumns.ID);

			loadContractLeave(id, start, end, parentDays, type,
					(Double) dailyRegBase, exprCtx);
		}
	}

	public void loadContractLeave(final Integer id, final Date leaveStart,
			final Date leaveEnd, final long parentDays, final LeaveType type,
			Double dailyRegBase, final ExpressionContext exprCtx)
			throws ExpressionException {
		loadContractLeave(id, leaveStart, leaveEnd, parentDays, type,
				dailyRegBase != null ? dailyRegBase.toString() : null, exprCtx);
	}	

	public void loadContractLeave(final Integer id, final Date leaveStart,
			final Date leaveEnd, final long parentDays, final LeaveType type,
			String dailyRegBase, final ExpressionContext exprCtx)
			throws ExpressionException {
		final Date start = Period.max(leaveStart, startDate); 
		final Date end = Period.min(leaveEnd, endDate);
		final long leaveDays = CommonUtil.getDaysBetweenDates(start, end) + 1;

		exprCtx.setVariable(ContextVariable.IT_START, leaveStart, start, end);

		ExpressionImpl exp = new ExpressionImpl();
		exp.setName(ContextVariable.REGULATORY_BASE.getName());
		if (dailyRegBase != null) {
			exp.setExpression(dailyRegBase);
		} else {
			exp.setExpression(String.format("%s(%s)", ContextVariable.BR,
					ContextVariable.IT_START));
		}
		exprCtx.addExpression(exp, startDate, endDate);

		exprCtx.setVariable(ContextVariable.LEAVE_DAYS, leaveDays, start, end);

		type.accept(new LeaveTypeVisitor<Void>() {

			@Override
			public Void visitCommonDisease(LeaveType leaveType) {
				for (DaysRange range : RANGES) {
					long days = range.getDays(parentDays, leaveDays);

					// if ( days == 0 )
					// continue;

					String name = range
							.getName(ContextVariable.COMMON_DISEASE_DAYS);

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(leaveStart);
					calendar.add(Calendar.DATE,
							(int) (range.start - 1 - parentDays));
					Date rangeStart = Period.max(calendar.getTime(), start);

					calendar.setTime(rangeStart);
					calendar.add(Calendar.DATE, (int) days);
					Date rangeEnd = calendar.getTime();

					exprCtx.setVariable(name, days,
							Period.max(rangeStart, start), rangeEnd);
				}
				// exprCtx.addVariable(ContextVariable.REGULATORY_BASE,
				// regBase, start, end );
				exprCtx.setVariable(ContextVariable.COMMON_DISEASE_DAYS,
						leaveDays, start, end);
				return null;
			}

			@Override
			public Void visitOcupationalDisease(LeaveType leaveType) {
				long days = parentDays == 0 ? leaveDays - 1 : leaveDays;
				if (days <= 0)
					return null;
				exprCtx.setVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS, 
						days, start, end);
				// exprCtx.addVariable(ContextVariable.REGULATORY_BASE,
				// regBase, start, end );
				return null;
			}

			@Override
			public Void visitMaternity(LeaveType leaveType) {
				exprCtx.setVariable(ContextVariable.MATERNITY_DAYS, leaveDays, 
						start, end);
				// exprCtx.addVariable(ContextVariable.REGULATORY_BASE,
				// regBase, start, end );
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
		add(new Leave(id, start, end, type));
	}

	public SortedSet<Leave> getLeaves() {
		return leaves;
	}

	// -------------------------------------------------------------- Protected
	protected void add(Leave leave) {
		leaves.add(leave);
	}

	protected void remove(Leave leave) {
		leaves.remove(leave);
	}

	protected void clean(ExpressionContext exprCtx, Leave leave) {
		exprCtx.removeVariable(ContextVariable.IT_START, leave.getStart(),
				leave.getEnd());
		
		for (DaysRange range : RANGES) {
			
			String name = range.getName(ContextVariable.COMMON_DISEASE_DAYS);

			exprCtx.removeVariable(name, leave.getStart(), leave.getEnd());

		}
		
		exprCtx.removeVariable(ContextVariable.COMMON_DISEASE_DAYS,	leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.REGULATORY_BASE.getName(), leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.BR, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.LEAVE_DAYS, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.MATERNITY_DAYS, leave.getStart(), leave.getEnd());
	
		
	}

}
