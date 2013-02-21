package com.esferalia.aon.gwt.payroll.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.DelegateContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SalaryDraftCalculatorContext extends
		DelegateContractSalaryCalculatorContext {

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

	private SalaryDraft draft;

	public SalaryDraftCalculatorContext(SalaryDraft draft,
			IContractSalaryCalculatorContext ctx) throws ExpressionException {
		super(ctx);
		this.draft = draft;
		loadDraftContext();
	}

	private void loadDraftContext() throws ExpressionException {

		ExpressionContext exprCtx = getExpressionContext();

		List<Variable> draftData = draft.getDraftContext();
		for (Variable variable : draftData) {
			ExpressionImpl expr = new ExpressionImpl();
			expr.setName(variable.getName());
			expr.setScope(ExpressionScope.SALARY);
			expr.setExpression(variable.getExpression());
			exprCtx.addExpression(expr, variable.getStartDate(),
					variable.getEndDate());
		}

	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return new HierarchyDeductions(getDraftDeductions().iterator(), super
				.getContractDeductions().iterator());
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return new CompositePayments(getDraftPayments(),
				super.getContractPayments());
	}

	private Collection<IContractPayment> getDraftPayments() {
		Collection<IContractPayment> draftPayments = new LinkedList<IContractPayment>();
		for (Payment payment : draft.getDraftPayments()) {

			DraftPayment draftPayment = new DraftPayment();

			draftPayment.setName(payment.getName());
			draftPayment.setType(getPaymentType(payment.getType()));
			draftPayment.setSalaryType(getSalaryType(payment.getSalaryType()));

			draftPayment.setStartDate(payment.getStartDate());
			draftPayment.setEndDate(payment.getEndDate());
			Integer month = payment.getMonth();
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
			
			draftDeduction.setName(deduction.getName());
			draftDeduction.setEndDate(deduction.getEndDate());
			draftDeduction.setStartDate(deduction.getStartDate());
			draftDeduction.setMonth(getMonth(deduction.getMonth()));
			draftDeduction.setExpression(deduction.getExpression());
			draftDeduction.setDescription(deduction.getDescription());
			draftDeduction.setType(getDeductionType(deduction.getType()));
			
			deductions.add(draftDeduction);
		}

		return deductions;
	}

	private static Month getMonth(Integer month) {
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
