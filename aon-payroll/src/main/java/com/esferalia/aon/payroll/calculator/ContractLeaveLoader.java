package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.server.AonDateUtils.getDaysBetweenDates;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LeaveTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ContractLeaveLoader {

	public static class Leave extends Period {
		private Integer id;
		private LeaveType type;
		private int parentDays;

		public Leave(Integer id, Date start, Date end, LeaveType type, int parentDays) {
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

		public int getParentDays() {
			return parentDays;
		}

	}

	public static class DaysRange {
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
			long rangeEnd = end != null ? Math.min(parentDays + leaveDays, end) : parentDays + leaveDays;
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

	// @formatter:off
	protected static final DaysRange _COMMON_RANGES[] = { new DaysRange(1, 3), new DaysRange(4, 15),
			new DaysRange(16, 20), new DaysRange(21, 365) {
				public String getName(ContextVariable variable) {
					return String.format("%s_%d", variable, start);
				};
			}, new DaysRange(366), };
	// @formatter:on
	// @formatter:off
	protected static final DaysRange _PROFESSIONAL_RANGES[] = { new DaysRange(1, 365) {
		public String getName(ContextVariable variable) {
			return String.format("%s", variable);
		};
	}, new DaysRange(366), };
	// @formatter:on

	protected static final DaysRange[] getCommonRanges(Date start, ExpressionContext ctx) {
		
		Date directPayStart = getDirectPayStart(ctx, start);//ctx.getVariable(DIRECT_PAY_START, start, null, Date.class);
		
		if ( directPayStart == null )
			ctx.setVariable(ContextVariable.DIRECT_PAY_START, directPayStart = AonDateUtils.addDays(start, 365) , start, null);
		

		long delegatePayDays = directPayStart != null ? AonDateUtils.getDaysBetweenDates(start, directPayStart) : 365;

		DaysRange commonRanges[] = new DaysRange[5];
		commonRanges[0] = new DaysRange(1, Math.min(delegatePayDays,3));
		commonRanges[1] = new DaysRange(4, Math.min(delegatePayDays,15));
		commonRanges[2] = new DaysRange(16, Math.min(delegatePayDays,20));
		commonRanges[3] = new DaysRange(21, delegatePayDays) {
			@Override
			public String getName(ContextVariable variable) {
				return String.format("%s_%d", variable, start);
			}
		};

		long directPayStartDay = delegatePayDays + 1;
		commonRanges[4] = new DaysRange(directPayStartDay) {
			@Override
			public String getName(ContextVariable variable) {
				return String.format("%s_%d", variable, 366);
			}
		};
		return commonRanges;
	}

	protected static final DaysRange[] getProfessionalRanges(Date start, ExpressionContext ctx) {
		Date directPayStart = getDirectPayStart(ctx, start);//ctx.getVariable(DIRECT_PAY_START, start, null, Date.class);
		if ( directPayStart == null )
			ctx.setVariable(ContextVariable.DIRECT_PAY_START, directPayStart = AonDateUtils.addDays(start, 365) , start, null);
		
		long delegatePayDays = directPayStart != null ? AonDateUtils.getDaysBetweenDates(start, directPayStart) : 365;

		DaysRange professionalRanges[] = new DaysRange[2];
		professionalRanges[0] = new DaysRange(1, delegatePayDays) {
			@Override
			public String getName(ContextVariable variable) {
				return String.format("%s", variable);
			}
		};

		long directPayStartDay = delegatePayDays + 1;

		professionalRanges[1] = new DaysRange(directPayStartDay) {
			@Override
			public String getName(ContextVariable variable) {
				return String.format("%s_%d", variable, 366);
			}
		};

		return professionalRanges;
	}

	protected final class QuoteDays implements ITimedVariable<Double> {

		private final Date end;
		private final Date start;
		private final ExpressionContext exprCtx;

		public QuoteDays(ExpressionContext exprCtx, Date start, Date end) {
			this.end = end;
			this.start = start;
			this.exprCtx = exprCtx;
		}

		@Override
		public Period getPeriod() {
			return new Period(start, end);
		}

		@Override
		public Double getValue(Period period) {
			return ContractLeaveLoader.this.getQuoteDays(exprCtx, period);
		}

	}

	protected Date endDate;
	protected Date startDate;
	protected SortedSet<Leave> leaves;

	public ContractLeaveLoader(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.leaves = new TreeSet<Leave>();
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

	public void loadContractLeave(final Integer id, final Date leaveStart, final Date leaveEnd, final long parentDays,
			final LeaveType type, String dailyRegBase, final ExpressionContext exprCtx) throws ExpressionException {

		final ITimedVariable<?> contractStart = exprCtx.getVariable(ContextVariable.CONTRACT_START, startDate, endDate);
		final Date realStartDate =  Period.max(startDate, (Date) contractStart.getValue(contractStart.getPeriod()));
		final Date start = Period.max(leaveStart,  realStartDate );

		final ITimedVariable<?> contractEnd = exprCtx.getVariable(ContextVariable.CONTRACT_END, startDate, endDate);
		final Date end = Period.min(leaveEnd, (Date) contractEnd.getValue(contractEnd.getPeriod()));


		final long leaveDays = CommonUtil.getDaysBetweenDates(start, end) + 1;

		exprCtx.setVariable(ContextVariable.IT_START, leaveStart, start, end);

		ExpressionImpl exp = new ExpressionImpl();
		exp.setName(ContextVariable.REGULATORY_BASE.getName());
		if (dailyRegBase != null) {
			exp.setExpression(dailyRegBase);
		} else {
			exp.setExpression(String.format("SELF.br(%s)", ContextVariable.IT_START));
		}
		exprCtx.addLazyExpression(exp, start, end);

		exprCtx.setVariable(ContextVariable.LEAVE_DAYS, leaveDays, start, end);

		type.accept(new LeaveTypeVisitor<Void>() {

			@Override
			public Void visitCommonDisease(LeaveType leaveType) {

				//for (DaysRange range : COMMON_RANGES) {
				for (DaysRange range : getCommonRanges(leaveStart, exprCtx)) {

					String name = range.getName(ContextVariable.COMMON_DISEASE_DAYS);

					long days = range.getDays(parentDays, leaveDays);

					if (days == 0) {
						// exprCtx.setVariable(name, days, start, end);
						continue;
					}

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(start);
					calendar.add(Calendar.DATE, (int) (range.start - 1 - parentDays));
					Date rangeStart = Period.max(calendar.getTime(), start);

					calendar.setTime(rangeStart);
					calendar.add(Calendar.DATE, (int) days - 1);
					Date rangeEnd = calendar.getTime();

					Date varStart = Period.max(rangeStart, start);
					//exprCtx.setVariable(name, days, varStart, rangeEnd);
					exprCtx.putVariable(name, new ExpressionContext.TimedConstant<Object>(days, varStart, rangeEnd) {
						public Object getValue() {
							exprCtx.readVariable(DIRECT_PAY_START.getName(), varStart, rangeEnd, Date.class);
							return super.getValue();
						};
					});
					exprCtx.putVariable(QUOTE_DAYS, new QuoteDays(exprCtx, start, end));
				}
				// exprCtx.addVariable(ContextVariable.REGULATORY_BASE,
				// regBase, start, end );
				exprCtx.setVariable(ContextVariable.COMMON_DISEASE_DAYS, leaveDays, start, end);

				return null;
			}

			@Override
			public Void visitOcupationalDisease(LeaveType leaveType) {

				// for (DaysRange range : PROFESSIONAL_RANGES) {
				for (DaysRange range : getProfessionalRanges(leaveStart, exprCtx)) {

					String name = range.getName(ContextVariable.OCCUPATIONAL_DISEASE_DAYS);

					long days = range.getDays(parentDays, leaveDays);

					if (days == 0) {
						// exprCtx.setVariable(name, days, start, end);
						continue;
					}

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(start);
					calendar.add(Calendar.DATE, (int) (range.start - 1 - parentDays));
					Date rangeStart = Period.max(calendar.getTime(), start);

					calendar.setTime(rangeStart);
					calendar.add(Calendar.DATE, (int) days - 1);
					Date rangeEnd = calendar.getTime();

					Date varStart = Period.max(rangeStart, start);
					//exprCtx.setVariable(name, days, varStart, rangeEnd);
					exprCtx.putVariable(name, new ExpressionContext.TimedConstant<Object>(days, varStart, rangeEnd) {
						public Object getValue() {
							exprCtx.readVariable(DIRECT_PAY_START.getName(), varStart, rangeEnd, Date.class);
							return super.getValue();
						};
					});
					exprCtx.putVariable(QUOTE_DAYS, new QuoteDays(exprCtx, start, end));
				}

				// long days = parentDays == 0 ? leaveDays - 1 : leaveDays;
				// if (days <= 0)
				// return null;
				// exprCtx.setVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS,
				// days, start, end);
				// exprCtx.putVariable(QUOTE_DAYS,
				// new QuoteDays(exprCtx, start, end));
				return null;
			}

			@Override
			public Void visitMaternity(LeaveType leaveType) {
				visit(ContextVariable.MATERNITY_FACTOR, ContextVariable.MATERNITY_DAYS);
				return null;
			}

			@Override
			public Void visitPaternity(LeaveType leaveType) {
				visit(ContextVariable.PATERNITY_FACTOR, ContextVariable.PATERNITY_DAYS);
				return null;
			}

			@Override
			public Void visitPregnacyRisk(LeaveType leaveType) {
				visit(ContextVariable.MATERNITY_FACTOR, ContextVariable.MATERNITY_DAYS);
				return null;
			}

			@Override
			public Void visitBreastFeedingRisk(LeaveType leaveType) {
				visit(ContextVariable.MATERNITY_FACTOR, ContextVariable.MATERNITY_DAYS);
				return null;
			}

			@Override
			public Void visitNonOcupationalDisease(LeaveType leaveType) {
				return this.visitCommonDisease(leaveType);
			}

			private void visit(ContextVariable factorVariable, ContextVariable daysVariable) {
				List<ITimedVariable<Number>> factors = exprCtx.getVariables(factorVariable, start, end);
				List<Period> periods = new ArrayList<Period>();

				for (ITimedVariable<Number> factor : factors) {

					Period period = factor.getPeriod();
					Number number = factor.getValue(period);

					if (number == null)
						continue;
					Double value = number.doubleValue();
					if (value < 0.00 || value >= 1.00)
						continue;
					exprCtx.putVariable(daysVariable, new ITimedVariable<Double>() {

						@Override
						public Period getPeriod() {
							return period;
						}

						@Override
						public Double getValue(Period period) {

							ExpressionContext.getCurrentBindings().get(factorVariable, value -> value, 1.00);

							return leaveDays * value;
						}
					});

					exprCtx.putVariable(ContextVariable.WORKED_DAYS, new ITimedVariable<Double>() {

						@Override
						public Period getPeriod() {
							return period;
						}

						@Override
						public Double getValue(Period period) {

							ExpressionContext.getCurrentBindings().get(factorVariable, value -> value, 1.00);

							return getQuoteDays(exprCtx, period) * (1.00 - value);
						}
					});
					// exprCtx.setVariable(ContextVariable.WORKED_DAYS,
					// leaveDays * (1.00 - value) ,
					// period.getStart(),
					// period.getEnd());

					periods.add(period);
				}

				for (Period period : Period.sub(new Period(start, end), periods)) {
					ExpressionImpl factorExpression = new ExpressionImpl();
					factorExpression.setName(factorVariable.getName());
					factorExpression.setScope(ExpressionScope.CONTRACT);
					factorExpression.setExpression("1.00");
					try {
						exprCtx.addExpression(factorExpression, period.getStart(), period.getEnd());
					} catch (ExpressionException e) {
					}

					exprCtx.putVariable(daysVariable, new ITimedVariable<Double>() {

						@Override
						public Period getPeriod() {
							return period;
						}

						@Override
						public Double getValue(Period period) {
							ExpressionContext.getCurrentBindings().get(factorVariable, value -> 1.00, 1.00);
							return leaveDays * 1.00;
						}
					});
				}

				exprCtx.putVariable(QUOTE_DAYS, new QuoteDays(exprCtx, start, end));
			}

		});
		add(new Leave(id, start, end, type, (int) parentDays));
	}

	public void clear() {
		leaves.clear();
	}

	protected void add(Leave leave) {
		leaves.add(leave);
	}

	protected void remove(Leave leave) {
		leaves.remove(leave);
	}

	public SortedSet<Leave> getLeaves() {
		return leaves;
	}

	public void clean(ExpressionContext exprCtx, Leave leave) {
		exprCtx.removeVariable(ContextVariable.IT_START, leave.getStart(), leave.getEnd());

//		for (DaysRange range : COMMON_RANGES) {
		for (DaysRange range : getCommonRanges(leave.getStart(), exprCtx)) {

			String common = range.getName(ContextVariable.COMMON_DISEASE_DAYS);
			exprCtx.removeVariable(common, leave.getStart(), leave.getEnd());
		}

		exprCtx.removeVariable(ContextVariable.COMMON_DISEASE_DAYS, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.REGULATORY_BASE.getName(), leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.BR, leave.getStart(), leave.getEnd());

		exprCtx.removeVariable(ContextVariable.LEAVE_DAYS, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS, leave.getStart(), leave.getEnd());
		exprCtx.removeVariable(ContextVariable.MATERNITY_DAYS, leave.getStart(), leave.getEnd());

	}

	// ---------------------------------------------------------------- Private

	protected double getQuoteDays(ExpressionContext ctx, Period p) {

		Long days = getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;

		if (!leaves.last().getEnd().equals(p.getEnd()))
			return days;

		return getAdjustDays(ctx, p, days);
	}

	protected double getAdjustDays(ExpressionContext ctx, Period p, long days) {

		try {
			;
			if (!ctx.getVariable(FULL_TIME, p.getStart(), p.getEnd(), Boolean.class))
				return days;
		} catch (Exception e) {
		}

//		try {
//			Number paternityFactor =  ctx.getVariable(PATERNITY_FACTOR, p.getStart(), p.getEnd(), Number.class);
//			if ( paternityFactor != null && paternityFactor.doubleValue() < 1.00 )
//				return days;
//		} catch (Exception e) {
//		}
//		try {
//			Number paternityFactor =  ctx.getVariable(MATERNITY_FACTOR, p.getStart(), p.getEnd(), Number.class);
//			if ( paternityFactor != null && paternityFactor.doubleValue() < 1.00 )
//				return days;
//		} catch (Exception e) {
//		}

		double naturalMonthDays = getMax(p.getStart(), DAY_OF_MONTH);

		double quoteMonthDays = 0.00;
		try {
			quoteMonthDays = ctx.getVariable(MONTH_DAYS, p.getStart(), p.getEnd(), Number.class).doubleValue();
		} catch (Exception e) {
			try {
				for (ITimedResult<Number> result : ctx.eval(MONTH_DAYS.getName(), p.getStart(), p.getEnd(),
						Number.class))
					quoteMonthDays = result.getValue().doubleValue();
			} catch (ExpressionException e1) {
			}
		}

		if (naturalMonthDays == quoteMonthDays)
			return days;

		// Yes adjust... .
		// X = 30 - WORKED_DAYS
		// X = 30 - (NATURAL_DAYS - IT_DAYS)
		// X = 30 - NATURAL_DAYS + IT_DAYS
		// X = IT_DAYS + 30 - NATURAL_DAYS
		return days + (30 - naturalMonthDays);
	}
	
	private static Date getDirectPayStart(ExpressionContext ctx, Date start) {
		Date directPayStart = null;
		
		List<ITimedVariable<Object>> vars = ctx.getVariables(ContextVariable.DIRECT_PAY_START);
		Collections.sort(vars, (v1,v2) -> v1.getPeriod().getStart().compareTo(v2.getPeriod().getStart()));
		for ( ITimedVariable<Object> var : vars ){
			if ( var.getPeriod().getStart().before(start) )
				continue;
			directPayStart = (Date) var.getValue(var.getPeriod());
		}
		
		return directPayStart;
	}

}
