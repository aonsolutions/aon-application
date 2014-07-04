package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.AbstractIterator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractExtraCalculatorContext extends
		SQLContractSalaryCalculatorContext {

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		super(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria, OrderByList orderByList) throws SQLException,
			ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria,
				OLDER);
	}

	// -------------------------------------------------------------------------
	
	

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.EXTRA;
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {

		addSalaryContractPayments();

		int chargeMonth = getChargeMonth();
		Collection<IContractPayment> extraPayments = new FilterCollection<IContractPayment>(
				new ExtraPaymentFilter(chargeMonth),
				super.getContractPayments());
		return extraPayments;
	}
	
	@Override
	protected void initContractExpressionCtx() throws SQLException,
			ExpressionException {
		super.initContractExpressionCtx();
		initMonthVariables(getExpressionContext());
	}
	
	
	// -------------------------------------------------------------------------

	private int getChargeMonth() {
		Date chargeDate = getChargeDate();
		return CommonUtil.getMonth(chargeDate);
	}


	private void addSalaryContractPayments() throws ExpressionException,
			AonException {
		ExpressionContext ctx = super.getExpressionContext();
		for (IContractPayment p : super.getContractPayments())
			if (!StringUtils.isBlank(p.getName())
					&& p.getSalaryType() == SalaryType.SALARY) {
				ctx.addLazyExpression(new SimpleContractPayment(p),
						p.getStartDate(), p.getEndDate());
			}

	}

	private void initMonthVariables(ExpressionContext ctx ) {
		List<ITimedVariable<?>> monthDaysList = 
				ctx.getTimedVariables(ContextVariable.MONTH_DAYS.getName());
		int months = monthDaysList.size();
		for (ITimedVariable<?> monthDays : monthDaysList) {
			Period month = monthDays.getPeriod();
			Number days = (Number) monthDays.getValue( month);
			ctx.setVariable(ContextVariable.MONTH_DAYS, days.doubleValue() * months, month.getStart(), month.getEnd() );
		}

	}

	// -------------------------------------------
	//
	// -------------------------------------------

	public static class DateFormatException extends IllegalArgumentException {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	}

	public static interface AgreementExtraCallback {
		public void sqlContractExtraCalculatorContext(
				IContractSalaryCalculatorContext ctx) throws AonException;
	}

	private static class FilterCollection<E> extends AbstractIterator<E> {

		private interface Filter<E> {
			boolean accept(E e);
		}

		private Filter<E> filter;
		private Iterator<E> iterator;

		private E next;

		public FilterCollection(Filter<E> filter, Collection<E> collection) {
			this.filter = filter;
			this.iterator = collection.iterator();
		}

		@Override
		public boolean hasNext() {
			if (next == null) {
				next = _next();
			}
			return next != null;
		}

		@Override
		public E next() {
			if (hasNext()) {
				E e = next;
				next = null;
				return e;
			} else {
				throw new NoSuchElementException();
			}
		}

		private E _next() {
			while (iterator.hasNext()) {
				E e = (E) iterator.next();
				if (filter.accept(e)) {
					return e;
				}
			}
			return null;
		}

	}

	private static class ExtraPaymentFilter implements
			FilterCollection.Filter<IContractPayment> {
		private Month month;

		public ExtraPaymentFilter(int month) {
			this.month = Month.getMonthByValue(month);
		}

		@Override
		public boolean accept(IContractPayment e) {
			return e.getSalaryType() == SalaryType.EXTRA
					&& e.getMonth() == this.month;
		}
	}

}