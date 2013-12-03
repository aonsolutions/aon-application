package com.esferalia.aon.gwt.payroll.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.CompositeCollection;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.DelegateContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class SalaryDraftCalculatorContext<T extends IContractSalaryCalculatorContext>
		extends DelegateContractSalaryCalculatorContext<T> {

	static class DraftPayment extends ContractPayment {

		private String name;

		@Override
		@Transient
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.SALARY;
		}

	}

	static class DrafDeduction extends ContractDeduction {
		private String name;

		@Override
		@Transient
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		@Transient
		public ExpressionScope getScope() {
			return ExpressionScope.SALARY;
		}
	}

	private static class DraftCompositePayments extends CompositePayments implements
			Predicate {

		public DraftCompositePayments(Collection<IContractPayment>... payments) {
			super(payments);
		}

		private Set<Integer> ids = new HashSet<Integer>();
		
		
		
		// --------------------------------------------------------- Collection
		
		@Override
		@SuppressWarnings("unchecked")
		public Iterator<IContractPayment> iterator() {
			return new FilterIterator(super.iterator(), this);
		}
		
		// ---------------------------------------------------------- Predicate
		
		@Override
		public boolean evaluate(Object obj) {
			IContractPayment payment = (IContractPayment) obj;
			Integer id = payment.getId();
			return id == null || ids.add(payment.getId());
		}

	}

	static class DraftHierarchyDeductions extends HierarchyDeductions {

		private Set<Integer> ids = new HashSet<Integer>();

		public DraftHierarchyDeductions(Iterator<IContractDeduction>... childs) {
			super(childs);
		}

		@Override
		protected IContractDeduction next(IContractDeduction e) {
			Integer id = e.getId();
			// Not it's not tricky. Remember we use Set, and Set's
			// add methos return true if this Set not already contain
			// the specified element ( id )
			if (ids.add(id))
				return super.next(e);
			else
				return null;
		}
	}

	class DraftDeferredExpressionVariable extends DeferredExpressionVariable {

		public DraftDeferredExpressionVariable(ExpressionContext ctx,
				IExpression expression, Date start, Date end) {
			super(ctx, expression, start, end);
		}

		@Override
		public Object getValue(Period period) {
			try {
				return super.getValue(period);
			} catch (ExpressionExceptionWrapper wrapper) {
				try {
					throw wrapper.getExpressionException();
				} catch (UndefinedVariablesException e) {
					onUndefinedData(getExpression(), e.getMessage(),
							getPeriod().getStart(), getPeriod().getEnd(),
							e.getVariableNames());
					throw new ExpressionExceptionWrapper(
							new UndefinedVariablesException(getExpression()
									.getName()));
				} catch (ExpressionException e) {
					throw wrapper;
				}
			}
		}
	}

	private SalaryDraft draft;

	private IListener listener;

	public SalaryDraftCalculatorContext(SalaryDraft draft, T ctx)
			throws ExpressionException {
		super(ctx);
		this.draft = draft;
		loadDraftContext(getExpressionContext());
	}

	protected void loadDraftContext(ExpressionContext exprCtx)
			throws ExpressionException {

		List<Variable> draftData = draft.getDraftContext();
		for (Variable variable : draftData) {
			String name = variable.getName();
			ExpressionImpl expr = new ExpressionImpl();
			expr.setName(name);
			expr.setScope(ExpressionScope.SALARY);
			expr.setExpression(variable.getExpression());
			Date startDate = resetTime(variable.getStartDate());
			Date endDate = resetTime(variable.getEndDate());
			try {
				exprCtx.addExpression(expr, startDate, endDate);
			} catch (UndefinedVariablesException e) {
				exprCtx.addVariable(name, new DraftDeferredExpressionVariable(
						exprCtx, expr, startDate, endDate));
			}
		}

	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return new DraftHierarchyDeductions(getDraftDeductions().iterator(),
				super.getContractDeductions().iterator());
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
			return new DraftCompositePayments(getDraftPayments(),
									super.getContractPayments());
	}

	protected SalaryDraft getDraft() {
		return draft;
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext() {
		return null;
	}

	protected void onUndefinedData(IExpression expression, String message,
			Date start, Date end, String... variables) {
		if (ctx.getListener() != null)
			for (String variable : variables)
				ctx.getListener().onUndefinedData(expression, variable,
						message, start, end);

	}

	private Collection<IContractPayment> getDraftPayments() {
		Collection<IContractPayment> draftPayments = new LinkedList<IContractPayment>();
		for (Payment payment : draft.getDraftPayments()) {

			DraftPayment draftPayment = new DraftPayment();

			draftPayment.setId(payment.getId());
			draftPayment.setName(payment.getName());
			draftPayment.setType(getPaymentType(payment.getType()));
			draftPayment.setSalaryType(getSalaryType(payment.getSalaryType()));

			draftPayment.setStartDate(resetTime(payment.getStartDate()));
			draftPayment.setEndDate(resetTime(payment.getEndDate()));
			Short month = payment.getMonth();
			if (month != null) {
				draftPayment.setMonth(Month.getMonthByValue(month));
			}
			draftPayment.setDescription(payment.getDescription());

			draftPayment.setExpression(payment.getExpression());
			draftPayment.setIrpfExpression(payment.getIrpfExpression());
			draftPayment.setQuoteExpression(payment.getQuoteExpression());

			draftPayments.add(draftPayment);
		}
		return draftPayments;
	}

	private Collection<IContractDeduction> getDraftDeductions() {
		Collection<IContractDeduction> deductions = new LinkedList<IContractDeduction>();

		for (Deduction deduction : draft.getDraftDeductions()) {

			DrafDeduction draftDeduction = new DrafDeduction();

			draftDeduction.setId(deduction.getId());
			draftDeduction.setName(deduction.getName());
			draftDeduction.setEndDate(resetTime(deduction.getEndDate()));
			draftDeduction.setStartDate(resetTime(deduction.getStartDate()));
			draftDeduction.setMonth(getMonth(deduction.getMonth()));
			draftDeduction.setExpression(deduction.getExpression());
			draftDeduction.setDescription(deduction.getDescription());
			draftDeduction.setType(getDeductionType(deduction.getType()));

			deductions.add(draftDeduction);
		}

		return deductions;
	}

	private static Date resetTime(Date date) {
		if (date == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Set time fields to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// Put iterator back in the Date object
		return cal.getTime();
	}

	private static Month getMonth(Short month) {
		return month != null ? Month.getMonthByValue(month) : null;
	}

	private static SalaryType getSalaryType(Salary.Type type) {
		return type != null ? SalaryType.values()[type.ordinal()] : null;
	}

	private static PaymentType getPaymentType(Payment.Type type) {
		return type != null ? PaymentType.values()[type.ordinal()] : null;
	}

	private static DeductionType getDeductionType(Deduction.Type type) {
		return type != null ? DeductionType.values()[type.ordinal()] : null;
	}

}
