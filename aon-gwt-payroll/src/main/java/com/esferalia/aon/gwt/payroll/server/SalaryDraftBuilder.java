package com.esferalia.aon.gwt.payroll.server;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DeductionComparator;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentComparator;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.DeductionEvent;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Event;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.PaymentEvent;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.VariableComparator;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class SalaryDraftBuilder implements ISalaryBuilder,
		ContractSalaryCalculator.IListener {

	private SalaryDraft salaryDraft;

	public SalaryDraftBuilder(SalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
	}

	public void setDbSalary(ISalary dbSalary) {
		salaryDraft.setHasDbSalary(true);
		
		salaryDraft.setDbIrpfBase(dbSalary.getIrpfBase());
		salaryDraft.setDbGgcBase(dbSalary.getCommonBase());
		salaryDraft.setDbGgpBase(dbSalary.getProfessionalBase());
		salaryDraft.setDbHExtraBase(dbSalary.getOvertimeBase());
		salaryDraft.setDbNonHExtraBase(dbSalary.getNonEstructuralOvertimeBase());
		salaryDraft.setDbProrationBase(dbSalary.getExtraPayProration());

		salaryDraft.setDbRemuneration(dbSalary.getRemuneration());
		salaryDraft.setDbTotalLiquid(dbSalary.getTotalLiquid());
		salaryDraft.setDbTotalPayment(dbSalary.getTotalPayment());
	}

	@Override
	public ISalary getSalary() {
		Collections.sort(salaryDraft.getPayments(), new PaymentComparator());
		Collections
				.sort(salaryDraft.getDeductions(), new DeductionComparator());
		Collections.sort(salaryDraft.getContext(), new VariableComparator());
		return null;
	}

	@Override
	public void createNewSalary() {
		salaryDraft.clearDb();
		salaryDraft.clearContext();
		salaryDraft.clearEvents();
		salaryDraft.clearPayments();
		salaryDraft.clearDeductions();
	}


	@Override
	public void setContract(Object contract) {
		// TODO Auto-generated method stub

	}


	@Override
	public void setCcc(String ccc) {
		salaryDraft.setEnterpriseCCC(ccc);

	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryDraft.setEnterpriseName(enterpriseName);

	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryDraft.setEnterpriseAddress(enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryDraft.setEnterpriseDocument(enterpriseDocument);
	}

	@Override
	public void setRegistration(Integer registration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmployeeName(String employeeName) {
		salaryDraft.setEmployeeName(employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salaryDraft.setEmployeeDocument(employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryDraft.setEmployeeSS(socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		salaryDraft.setEmployeeAgreementCategory(category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		salaryDraft.setEmployeeQuoteGroup(quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salaryDraft.setEmployeeSeniorityDate(seniorityDate);
	}

	@Override
	public void setType(SalaryType type) {
		salaryDraft.setType(Type.values()[type.ordinal()]);
	}

	@Override
	public void setIssueDate(Date issueDate) {
		salaryDraft.setIssueDate(issueDate);
	}

	@Override
	public void setChargeDate(Date chargeDate) {
		salaryDraft.setChargeDate(chargeDate);
	}

	@Override
	public void setStartDate(Date startDate) {
		salaryDraft.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		salaryDraft.setEndDate(endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setItBase(Double itBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		salaryDraft.setCgcBase(cgcBase);
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		salaryDraft.setCgpBase(cgpBase);
	}

	@Override
	public void setRemuneration(Double remuneration) {
		salaryDraft.setRemuneration(remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		salaryDraft.setProrationBase(proExtBase);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		salaryDraft.setIrpfBase(irpfBase);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		salaryDraft.sethExtraBase(hExtraBase);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		salaryDraft.setNonHExtraBase(nonHExtraBase);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		salaryDraft.setTotalLiquid(totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		salaryDraft.setTotalPayment(totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		salaryDraft.setTotalDeduction(totalDeduction);
	}

	@Override
	public void setSocialSecurityContributions(
			Double socialSecurityContributions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBonus(String concept, Double amount, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCost(DeductionType type, String concept, Double amount,
			String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, IPayment payment,
			Map<String, ITimedVariable<?>> context) {

		addContext(context);
		
		Payment draftPayment = newPayment((IContractPayment) payment);
		// override calculated ...
		draftPayment.setName(concept);
		draftPayment.setAmount(amount);
		draftPayment.setDescription(description);
		draftPayment.setType(getPaymentType(type));
		
		salaryDraft.addPayment(draftPayment);
		
		
	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, IDeduction ideduction,
			Map<String, ITimedVariable<?>> context) {
		addContext(context);

		IContractDeduction contractDeduction = (IContractDeduction) ideduction;

		Deduction deduction = newDeduction(contractDeduction);
		// override by calculated...
		deduction.setName(concept);
		deduction.setAmount(amount);
		deduction.setDescription(description);
		deduction.setType(getDeductionType(type));

		salaryDraft.addDeduction(deduction);

	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub

	}

	// ContractSalaryCalculator.IListener methods

	@Override
	public void onCheckError(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onInvalidData(String variableName, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompileError(String variableName, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckError(IContractPayment payment, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvalidData(IContractPayment payment, String variableName,
			String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUndefinedData(IContractPayment contractPayment,
			RemovedExpressionVariable<?> var) {

		UndefinedPaymentVariable undefVar = 
				new UndefinedPaymentVariable();

		undefVar.setName(var.getName());
		undefVar.setImplicit(false);
		undefVar.setEndDate(var.getPeriod().getEnd());
		undefVar.setStartDate(var.getPeriod().getStart());
		undefVar.setScope(getScope(var.getExpression().getScope()));
		undefVar.setPayment(newPayment(contractPayment));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onCompileError(IContractPayment payment, String message) {
		
		PaymentEvent event = new PaymentEvent();
		event.setMessage(message);
		event.setType(Event.Type.ERROR);
		event.setPayment(newPayment(payment));

		salaryDraft.addPaymentError(event);
	}

	@Override
	public void onUndefinedData(IContractPayment contractPayment, String variableName,
			String message) {

		UndefinedPaymentVariable undefVar = 
				new UndefinedPaymentVariable();

		undefVar.setName(variableName);
		undefVar.setImplicit(false);
		undefVar.setEndDate(contractPayment.getEndDate());
		undefVar.setStartDate(contractPayment.getStartDate());
		undefVar.setScope(getScope(contractPayment.getScope()));
		
		undefVar.setPayment(newPayment(contractPayment));

		salaryDraft.addUndefinedVariable(undefVar);
	}

	@Override
	public void onCheckError(IContractDeduction deduction, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvalidData(IContractDeduction deduction,
			String variableName, String message) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onUndefinedData(IContractDeduction contractDeduction,
			RemovedExpressionVariable<?> var) {
		// TODO Auto-generated method stub
		UndefinedDeductionVariable undefVar = 
				new UndefinedDeductionVariable();

		undefVar.setName(var.getName());
		undefVar.setImplicit(false);
		undefVar.setEndDate(var.getPeriod().getEnd());
		undefVar.setStartDate(var.getPeriod().getStart());
		undefVar.setScope(getScope(var.getExpression().getScope()));
		
		undefVar.setDeduction(newDeduction(contractDeduction));

		salaryDraft.addUndefinedVariable(undefVar);
		
	}
	
	@Override
	public void onUndefinedData(IContractDeduction contractDeduction,
			String variableName, String message) {
		
		UndefinedDeductionVariable undefVar = 
				new UndefinedDeductionVariable();

		undefVar.setName(variableName);
		undefVar.setImplicit(false);
		undefVar.setEndDate(contractDeduction.getEndDate());
		undefVar.setStartDate(contractDeduction.getStartDate());
		undefVar.setScope(getScope(contractDeduction.getScope()));
		
		undefVar.setDeduction(newDeduction(contractDeduction));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onCompileError(IContractDeduction contractDeduction, String message) {
		// TODO Auto-generated method stub
		DeductionEvent event = new DeductionEvent();
		event.setMessage(message);
		Deduction deduction = newDeduction(contractDeduction);
		event.setDeduction(deduction);
		event.setType(Event.Type.ERROR);
		salaryDraft.addDeductionEevent(event);
	}

	@Override
	public void onCheckError(IContractBonus bonus, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompileError(IContractBonus bonus, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvalidData(IContractBonus bonus, String variableName,
			String message) {
		// TODO Auto-generated method stub

	}
	


	private void addContext(Map<String, ITimedVariable<?>> context) {
		for (Entry<String, ITimedVariable<?>> entry : context.entrySet()) {
			ITimedVariable<?> var = entry.getValue();

			ContextVariable contextVariable = ContextVariable
					.getVariableByName(entry.getKey());
			if (contextVariable != null && contextVariable.isInternal()) {
				continue;
			}

			if (var instanceof IExpressionVariable<?>) {
				IExpressionVariable<?> exprVar = (IExpressionVariable<?>) var;
				IExpression expr = exprVar.getExpression();
				Scope scope = getScope(expr.getScope());
				salaryDraft.addVariable(entry.getKey(), var.getValue(var
						.getPeriod()), var.getPeriod().getStart(), var
						.getPeriod().getEnd(), scope, expr.getExpression());

			} else {
				salaryDraft.addVariable(entry.getKey(), var.getValue(var
						.getPeriod()), var.getPeriod().getStart(), var
						.getPeriod().getEnd());
			}
		}
	}
	
	private Deduction newDeduction(IContractDeduction contractDeduction){

		Deduction deduction = new Deduction();

		deduction.setName(contractDeduction.getName());
		deduction.setEndDate(contractDeduction.getEndDate());
		deduction.setStartDate(contractDeduction.getStartDate());
		deduction.setExpression(contractDeduction.getExpression());
		deduction.setScope(getScope(contractDeduction.getScope()));
		deduction.setDescription(contractDeduction.getDescription());
		deduction.setType(getDeductionType(contractDeduction.getType()));
		
		return deduction;
	}
	private Payment newPayment(IContractPayment contractPayment) {

		Payment payment = new Payment();

		payment.setType(getPaymentType(contractPayment.getType()));
		payment.setName(contractPayment.getName());
		payment.setMonth(getMonth(contractPayment.getMonth()));
		payment.setSalaryType(getSalaryType(contractPayment.getSalaryType()));
		payment.setStartDate(contractPayment.getStartDate());
		payment.setEndDate(contractPayment.getEndDate());
		payment.setExpression(contractPayment.getExpression());
		payment.setDescription(contractPayment.getDescription());
		payment.setIrpfExpression(contractPayment.getIrpfExpression());
		payment.setQuoteExpression(contractPayment.getQuoteExpression());

		return payment;
	}
	
	
	private Integer getMonth( Month month) {
		return month == null ? null : month.getValue();
	}
	
	private Deduction.Type getDeductionType(DeductionType type){
		return type == null ? null : Deduction.Type.values()[type.ordinal()];
	}

	private Scope getScope(ExpressionScope exprScope) {
		return exprScope != null ? Scope.values()[exprScope.ordinal()]
				: Scope.SYSTEM;
	}

	private Payment.Type getPaymentType(PaymentType type) {
		return type != null ? Payment.Type.values()[type.ordinal()] : null;
	}

	private Salary.Type getSalaryType(SalaryType type) {
		return type != null ? Salary.Type.values()[type.ordinal()] : null;
	}
}
