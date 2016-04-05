package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class SQLContractExtraCalculatorContext extends SQLContractSalaryCalculatorContext {

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, issueDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, OrderByList orderByList) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, OLDER);
	}

	// -------------------------------------------------------------------------

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.EXTRA;
	}

	@Override
	public Collection<IContractPayment> getContractPayments() throws AonException {

		addSalaryContractPayments();

		int issueMonth = getIssueMonth();
		Collection<IContractPayment> extraPayments = new FilterCollection<IContractPayment>(
				new ExtraPaymentFilter(issueMonth), super.getContractPayments());
		return extraPayments;
	}

	@Override
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {
		super.initContractExpressionCtx(hook);
		initMonthVariables(getExpressionContext());
	}

	@Override
	protected ITimedVariable<Number> getExtraDays(ITimedVariable<Number> monthDays) {
		return monthDays;
	}

	// -------------------------------------------------------------------------

	private int getIssueMonth() {
		Date issueDate = getIssueDate();
		return CommonUtil.getMonth(issueDate);
	}

	private void addSalaryContractPayments() throws ExpressionException, AonException {
		ExpressionContext expressionContext = super.getExpressionContext();

		for (IContractPayment p : super.getContractPayments()) {

			Date paymentStart = Period.max(p.getStartDate(), getStart());
			Date paymentEnd = Period.min(p.getEndDate(), getEnd());
			
			if ( Period.compare(paymentStart, paymentEnd)> 0)
				continue;

			if (StringUtils.isNotBlank(p.getName()) && p.getSalaryType() == SalaryType.SALARY) {
				try {
					addSalaryPayment(expressionContext, p, paymentStart, paymentEnd);
				} catch (com.esferalia.aon.salary.expression.InterruptedException e) {
					throw e;
				}
				catch (ExpressionException e) {
					System.err.println(String.format("ERROR [%s]: %s", p.getName(), e.getLocalizedMessage()));
				}
			}
		}
	}

	private void addSalaryPayment(ExpressionContext expressionContext, IContractPayment payment, Date paymentStart,
			Date paymentEnd) throws ExpressionException, UndefinedVariablesException {
		List<ITimedResult<Double>> results = expressionContext.eval(payment.getExpression(), paymentStart, paymentEnd,
				Double.class);

		for (ITimedResult<Double> result : results) {

			Date resultStart = result.getPeriod().getStart();
			Date resultEnd = result.getPeriod().getEnd();

			Double resultDouble = result.getValue();
			double resultValue = resultDouble != null ? resultDouble : 0.00;

			Date valueStart = resultStart;
			List<ITimedVariable<Number>> prevs = expressionContext.getVariables(payment.getName(), resultStart,
					resultEnd);

			for (ITimedVariable<Number> prev : prevs) {
				Date prevStart = prev.getPeriod().getStart();
				Date prevEnd = prev.getPeriod().getEnd();
				try {
					Number prevValue = prev.getValue(prev.getPeriod());
					if (valueStart.compareTo(prevStart) < 0)
						expressionContext.setVariable(payment.getName(), resultValue, valueStart, prev(prevStart));
					expressionContext.setVariable(payment.getName(), resultValue + prevValue.doubleValue(), prevStart,
							prevEnd);
					valueStart = next(prevEnd);
				} catch (Exception e) {
					System.err.println(String.format("ERROR [%s]: %s", payment.getName(), e.getLocalizedMessage()));
				}
			}
			if (valueStart.compareTo(resultEnd) <= 0) {
				expressionContext.setVariable(payment.getName(), resultValue, valueStart, resultEnd);
			}
		}
	}

	private void initMonthVariables(ExpressionContext ctx) throws UndefinedVariablesException, ExpressionException {
		List<ITimedVariable<?>> monthDaysList = new ArrayList<ITimedVariable<?>>(
				ctx.getTimedVariables(NATURAL_MONTH_DAYS.getName()));

		int months = monthDaysList.size();

		for (ITimedVariable<?> monthDays : monthDaysList) {

			Period month = monthDays.getPeriod();
			List<ITimedVariable<Object>> vars = ctx.getVariables(WORKED_DAYS, month.getStart(), month.getEnd());
			for (ITimedVariable<Object> var : vars) {
				List<ITimedResult<Double>> workedDays = ctx.eval(String.format("%s/%d", WORKED_DAYS, months),
						var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class);
				for (ITimedResult<Double> workedDay : workedDays) {
					ctx.setVariable(WORKED_DAYS, workedDay.getValue(), workedDay.getPeriod().getStart(),
							workedDay.getPeriod().getEnd());

				}
				// Number days = (Number) var.getValue(var.getPeriod());
				// ctx.setVariable(WORKED_DAYS, days.doubleValue() / months,
				// var.getPeriod().getStart(), var.getPeriod().getEnd());
			}

			// ctx.setVariable(MONTH_DAYS, days.doubleValue() * months,
			// month.getStart(), month.getEnd());
			// ctx.setVariable(PAY_DAYS, days.doubleValue() * months,
			// month.getStart(), month.getEnd());
		}

	}

	// -------------------------------------------
	//
	// -------------------------------------------

	public static class DateFormatException extends IllegalArgumentException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	}

	public static interface AgreementExtraCallback {
		public void sqlContractExtraCalculatorContext(IContractSalaryCalculatorContext ctx) throws AonException;
	}

	private static class ExtraPaymentFilter implements FilterCollection.Filter<IContractPayment> {
		private Month month;

		public ExtraPaymentFilter(int month) {
			this.month = Month.getMonthByValue(month);
		}

		@Override
		public boolean accept(IContractPayment e) {
			return e.getSalaryType() == SalaryType.EXTRA && e.getMonth() == this.month;
		}
	}

}