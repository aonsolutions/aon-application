package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.util.Utilities.formatDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.MissingResourceException;
import java.util.stream.Stream;

import javax.faces.el.PropertyNotFoundException;

import org.mvel2.ast.FunctionInstance;
import org.mvel2.ast.PrototypalFunctionInstance;
import org.mvel2.util.MethodStub;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.BonusEvent;
import com.esferalia.aon.gwt.payroll.shared.CompositeDeduction;
import com.esferalia.aon.gwt.payroll.shared.CompositePayment;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DeductionEvent;
import com.esferalia.aon.gwt.payroll.shared.Event;
import com.esferalia.aon.gwt.payroll.shared.InvalidVariable;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.gwt.payroll.shared.ItemComparator;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.PaymentEvent;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.UndefinedDeductionVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedPaymentVariable;
import com.esferalia.aon.gwt.payroll.shared.UndefinedVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.VariableComparator;
import com.esferalia.aon.gwt.payroll.util.Utilities;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryDraftBuilder
		implements ISalaryBuilder<ISalary>, ContractSalaryCalculator.IListener,
		IContractSalaryCalculatorContext.IListener {

	private SalaryDraft salaryDraft;

	private Map<String, boolean[]> defined;
	
	private Runnable sed ;

	public SalaryDraftBuilder(SalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
		defined = Collections.emptyMap();
		clearSalaryDraft();
		this.sed = () -> {};
	}

	public void setDefined(Map<String, boolean[]> defined) {
		this.defined = defined;
	}
	
	public void setAgreementPayments(Collection<IContractPayment> payments) {
		for (IContractPayment contractPayment : payments) {
			salaryDraft.addAgreementPayment(newAgreementPayment(contractPayment));
		}
	}
	
	public void clearDb() {
		salaryDraft.clearDb();
	}
	
	private static String formatItemDescription(Item<?> item, Date draftStart, Date draftEnd) {

		Date itemStart = item.getStartDate();
		Date itemEnd = item.getEndDate();
		
		StringBuffer description = new StringBuffer(); 
		description.append(AonStringUtils.isNotBlank(item.getDescription()) ? item.getDescription() : item.getDescriptionTemplate());
		
		
		
		if ( itemStart == null  || itemEnd == null ) {
			return description.toString();
		}

		if ( itemStart.equals(draftStart)
				&&  itemEnd.equals(draftEnd) ) {
			return description.toString();
		}
		
		if (itemStart.equals(itemEnd)) {
			String format = "dd/MM " + (draftStart.getYear() == draftEnd.getYear() ? "" : "/yyyy");
			String formatted = description.append(" ").append(formatDate(itemStart, format)).toString();
			return formatted;
		}

		if (itemStart.getMonth() == itemEnd.getMonth()) {
			String format = "dd";
			String formatted = 
					description.append(" ")
					.append(formatDate(itemStart, format).orElse(""))
					.append(" - ")
					.append(formatDate(itemEnd, format).orElse(""))
					.append(" de " + formatDate(itemStart, "MMMM").orElse(""))
					.toString();
			
			return formatted;
		}

		String format = "dd/MM" + (draftStart.getYear() == draftEnd.getYear() ? "" : "/yyyy");
		String formatted = 	description.append(" ")
							.append(formatDate(itemStart, format).orElse(""))
							.append(" - ")
							.append(formatDate(itemEnd, format).orElse(""))
							.toString();
		
		return formatted;
	}

	public void setDbSalary(ISalary dbSalary) throws SalaryException {

		salaryDraft.setDbId(dbSalary.getId());

		salaryDraft.setDbIrpfBase(dbSalary.getIrpfBase());
		salaryDraft.setDbInkindIrpfBase(dbSalary.getInKindIrpfBase());
		salaryDraft.setDbGgcBase(dbSalary.getCommonBase());
		salaryDraft.setDbGgpBase(dbSalary.getProfessionalBase());
		salaryDraft.setDbHExtraBase(dbSalary.getOvertimeBase());
		salaryDraft
				.setDbNonHExtraBase(dbSalary.getNonEstructuralOvertimeBase());
		salaryDraft.setDbProrationBase(dbSalary.getExtraPayProration());

		salaryDraft.setDbRemuneration(dbSalary.getRemuneration());
		salaryDraft.setDbTotalLiquid(dbSalary.getTotalLiquid());
		salaryDraft.setDbTotalPayment(dbSalary.getTotalPayment());
		salaryDraft.setDbTotalDeduction(dbSalary.getTotalDeduction());

		// match up draft payments & db payments
		
		List<IPayment> dbPayments;
		dbPayments = new ArrayList<IPayment>(dbSalary.getPaymentS());
		for (Payment payment : salaryDraft.getPayments()) {
			List<IPayment> dbCounterParts = getDbItemCounterParts(dbPayments,
					payment);
			if (dbCounterParts.size() == 0)
				continue;
			double amount = 0.00;
			for ( IPayment dbPayment: dbCounterParts )
				amount += dbPayment.getAmount();

			payment.setDbAmount(amount);
			// Remove it from the list to avoid processing later.
			dbPayments.removeAll(dbCounterParts);
		}

		for (Payment draftPayment : salaryDraft.getDraftPayments()) {
			List<IPayment> dbCounterParts = getDbItemCounterParts(dbPayments,
					draftPayment);
			if (dbCounterParts.size() == 0)
				continue;
			// Found almost one counterpart. Gets first of them.
			IPayment dbPayment = dbCounterParts.get(0);

			Payment payment = new Payment();
			payment.setScope(Scope.SALARY);
			payment.setName(dbPayment.getName());
			payment.setDbAmount(dbPayment.getAmount());
			payment.setExpression(dbPayment.getExpression());
			payment.setDescription(dbPayment.getDescription());

			payment.setId(draftPayment.getId());
			payment.setType(draftPayment.getType());
			payment.setEndDate(draftPayment.getEndDate());
			payment.setStartDate(draftPayment.getStartDate());
			payment.setConceptId(draftPayment.getConceptId());
			payment.setSalaryType(draftPayment.getSalaryType());
			payment.setIrpfExpression(draftPayment.getIrpfExpression());
			payment.setQuoteExpression(draftPayment.getQuoteExpression());

			salaryDraft.addPayment(payment);
			// Remove it from the list to avoid processing later.
			dbPayments.remove(dbPayment);
		}

		for (IPayment dbPayment : dbPayments) {
			Payment payment = new Payment();
			payment.setScope(Scope.SALARY);
			payment.setName(dbPayment.getName());
			payment.setDbAmount(dbPayment.getAmount());
			payment.setExpression(dbPayment.getExpression());
			payment.setDescription(dbPayment.getDescription());

			salaryDraft.addPayment(payment);
		}

		// match up draft deductions & db deductions
		List<IDeduction> dbDeductions;
		dbDeductions = new ArrayList<IDeduction>(dbSalary.getDeductionS());
		for (Deduction deduction : salaryDraft.getDeductions()) {
			List<IDeduction> dbCounterParts = getDbDeductionCounterParts(
					dbDeductions, deduction);
			if (dbCounterParts.size() == 0)
				continue;
			double amount = 0.00;
			for ( IDeduction dbDeduction: dbCounterParts )
				amount += dbDeduction.getAmount();
			deduction.setDbAmount(amount);
			dbDeductions.removeAll(dbCounterParts);
		}

		for (IDeduction dbPayment : dbDeductions) {
			Deduction deduction = new Deduction();
			deduction.setName(dbPayment.getName());
			deduction.setDbAmount(dbPayment.getAmount());
			deduction.setExpression(dbPayment.getExpression());
			deduction.setDescription(dbPayment.getDescription());
			salaryDraft.addDeduction(deduction);
		}

		// match up draft costs & db costs
		List<IDeduction> dbCosts;
		dbCosts = new ArrayList<IDeduction>(dbSalary.getCostS());
		for (Deduction cost : salaryDraft.getCosts()) {
			List<IDeduction> dbCounterParts = getDbDeductionCounterParts(
					dbCosts, cost);
			if (dbCounterParts.size() == 0)
				continue;
			double amount = 0.00;
			for ( IDeduction dbcost: dbCounterParts )
				amount += dbcost.getAmount();

			cost.setDbAmount(amount);
			dbCosts.removeAll(dbCounterParts);
		}
		
		for (IDeduction dbCost : dbCosts) {
			Deduction cost = new Deduction();
			cost.setName(dbCost.getName());
			cost.setDbAmount(dbCost.getAmount());
			cost.setExpression(dbCost.getExpression());
			cost.setDescription(dbCost.getDescription());
			salaryDraft.addCost(cost);
		}
		
		// match up draft embargos & db embargos*
		List<IDeduction> dbEmbargos;
		dbEmbargos = new ArrayList<IDeduction>(dbSalary.getEmbargoS());
		for (Deduction embargo : salaryDraft.getEmbargos()) {
			List<IDeduction> dbCounterParts = getDbEmbargoCounterParts(
					dbEmbargos, embargo);
			if (dbCounterParts.size() == 0)
				continue;
			double amount = 0.00;
			for ( IDeduction dbEmbargo: dbCounterParts )
				amount += dbEmbargo.getAmount();
			embargo.setDbAmount(amount);
			dbEmbargos.removeAll(dbCounterParts);
		}

		for (IDeduction dbEmbargo : dbEmbargos) {
			Deduction embargo = new Deduction();
			embargo.setName(dbEmbargo.getName());
			embargo.setDbAmount(dbEmbargo.getAmount());
			embargo.setExpression(dbEmbargo.getExpression());
			embargo.setDescription(dbEmbargo.getDescription());
			salaryDraft.addDeduction(embargo);
		}		
		
	}

	public void setDbSalaryData(List<Variable> data) throws SalaryException {
		for ( Variable var: data )
			salaryDraft.addDbVariable(
					var.getName(), 
					var.getValue(), 
					var.getStartDate(),
					var.getEndDate());
	}

	@Override
	public ISalary getSalary() {
		sed.run();
		Collections.sort(salaryDraft.getPayments(),
				new ItemComparator<Payment.Type>());
		Collections.sort(salaryDraft.getDeductions(),
				new ItemComparator<Deduction.Type>());
		Collections.sort(salaryDraft.getContext(), new VariableComparator());
		return null;
	}

	@Override
	public void createNewSalary() {
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
	public void setRegime(String regime) {
		salaryDraft.setRegime(regime);
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryDraft.setEnterpriseName(enterpriseName);

	}
	
	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		salaryDraft.setEnterpriseCity(enterpriseCity);
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
	public void setEmployeeCity(String employeeCity) {
		salaryDraft.setEmployeeCity(employeeCity);
	}
	
	@Override
	public void setEmployeeAddress(String employeeAddress) {
		salaryDraft.setEmployeeAddress(employeeAddress);
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
		salaryDraft.setTimeUnits(timeUnits);
	}

	@Override
	public void setItBase(Double itBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		salaryDraft.setRawCgcBase(rawCgcBase);

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
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		salaryDraft.setInkindIrpfBase(inkindIrpfBase);
	}

	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		salaryDraft.setMoneyIrpfBase(moneyIrpfBase);
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
	public void setTotalIrpf(Double totalIrpf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		salaryDraft.setTotalEnterprise(totalEnterprise);
	}

	@Override
	public void addData(String name, ITimedVariable<?> var) {
		addVariable(name, var);
	}

	@Override
	public void addBonus(Double amount, String description, Date startDate,
			Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		addContext(context);

		IContractBonus contractBonus = (IContractBonus) bonus;

		Bonus myBonus = newBonus(contractBonus);
		myBonus.setAmount(amount);
		myBonus.setDescription(description);
		myBonus.setDescription(formatItemDescription(myBonus, salaryDraft.getStartDate(), salaryDraft.getEndDate()));
		myBonus.setDescriptionTemplate(myBonus.getDescription());
		myBonus.setStartDate(startDate);
		myBonus.setEndDate(endDate);


		salaryDraft.addBonus(myBonus);

	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction iembargo, Map<String, ITimedVariable<?>> context) {
		addContext(context);

		IContractEmbargo contractEmbargo = (IContractEmbargo) iembargo;

		Deduction embargo = newEmbargo(contractEmbargo);
		// override by calculated...
		// embargo.setName(iembargo.getName());
		embargo.setAmount(amount);
		embargo.setDescription(description);
		embargo.setType(Deduction.Type.EMBARGO);
		embargo.setDescription(embargo.getDescription());
		embargo.setDescription(formatItemDescription(embargo, salaryDraft.getStartDate(), salaryDraft.getEndDate()));
		
		salaryDraft.addEmbargo(embargo);
	}

	@Override
	public void addCost(Double amount, String description
			, Date startDate, Date endDate,  IDeduction cost, Map<String, ITimedVariable<?>> context) {

		addContext(context);

		IContractDeduction contractCost = (IContractDeduction) cost;
		
		Deduction draftCost = newDeduction(contractCost);
		
		draftCost.setAmount(amount);
		draftCost.setEndDate(endDate);
		draftCost.setStartDate(startDate);
		draftCost.setDescription(description);
		draftCost.setDescription(formatItemDescription(draftCost, salaryDraft.getStartDate(), salaryDraft.getEndDate()));

		CompositeDeduction compositeCost = getCost(contractCost.getId());

		if (compositeCost != null)
			compositeCost.addChild(draftCost);
		else
			salaryDraft.addCost(draftCost);
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {

		addContext(context);

		Payment draftPayment = newPayment((IContractPayment) payment);
		// override calculated ...
		draftPayment.setAmount(amount);
		draftPayment.setIrpf(tax);
		draftPayment.setQuote(quote);
		draftPayment.setDescription(description );
		draftPayment.setStartDate(startDate);
		draftPayment.setEndDate(endDate);
		draftPayment.setDescription(formatItemDescription(draftPayment, salaryDraft.getStartDate(), salaryDraft.getEndDate()) );
		

		CompositePayment compositePayment = getPayment(draftPayment.getId());
		
		// TODO: por que falla el type??
		draftPayment.setType(getPaymentType(payment.getType()));

		if (compositePayment != null)
			compositePayment.addChild(draftPayment);
		else
			salaryDraft.addPayment(draftPayment);

	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate,
			Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		addPayment(0.00, quote, tax, payment.getDescription(), startDate,
				endDate, payment, context);
	}

	@Override
	public void addDeduction(Double amount, String description, Date start,
			Date end, IDeduction ideduction,
			Map<String, ITimedVariable<?>> context) {
		addContext(context);

		IContractDeduction contractDeduction = (IContractDeduction) ideduction;

		Deduction deduction = newDeduction(contractDeduction);
		// override by calculated...
		deduction.setAmount(amount);
		deduction.setStartDate(start);
		deduction.setEndDate(end);
		deduction.setDescription(description);
		deduction.setDescription(formatItemDescription(deduction, salaryDraft.getStartDate(), salaryDraft.getEndDate()));

		CompositeDeduction compositeDeduction = getDeduction(deduction.getId());

		if (compositeDeduction != null)
			compositeDeduction.addChild(deduction);
		else
			salaryDraft.addDeduction(deduction);

	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		addDeduction(0.00, deduction.getDescription(), start, end, deduction,
				context);
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		addEmbargo(id, 0.00, embargo.getDescription(), embargo, context);

	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub

	}

	// ContractSalaryCalculator.IListener methods

	@Override
	public void onCheckError(String message) {
		salaryDraft.addWarning(message);
	}

	@Override
	public void onInvalidData(String variableName, String message) {

		InvalidVariable invalidVariable = new InvalidVariable();

		invalidVariable.setName(variableName);
		invalidVariable.setImplicit(isImplicit(invalidVariable.getName()));
		invalidVariable.setEndDate(salaryDraft.getStartDate());
		invalidVariable.setStartDate(salaryDraft.getEndDate());
		invalidVariable.setScope(Scope.SYSTEM);

		salaryDraft.addUndefinedVariable(invalidVariable);

	}

	@Override
	public void onCompileError(String variableName, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove(IContractPayment payment) {
		Payment draftPayment = newPayment(payment);
		draftPayment.setDescription(payment.getDescription());
		salaryDraft.addPayment(draftPayment);
	}

	@Override
	public void onCheckError(IContractPayment payment, String message) {
		PaymentEvent paymentEvent = new PaymentEvent();
		paymentEvent.setMessage(message);
		paymentEvent.setType(Event.Type.WARNING);
		paymentEvent.setPayment(newPayment(payment));

		salaryDraft.addPaymentError(paymentEvent);

	}

	@Override
	public void onInvalidData(IContractPayment payment, String variableName,
			String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUndefinedData(IContractPayment contractPayment,
			RemovedExpressionVariable<?> var) {

		UndefinedPaymentVariable undefVar = new UndefinedPaymentVariable();
		undefVar.setName(var.getName());
		undefVar.setImplicit(isImplicit(var.getName()));
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
	public void onUndefinedData(IContractPayment contractPayment,
			String variableName, String message) {

		UndefinedPaymentVariable undefVar = new UndefinedPaymentVariable();

		undefVar.setName(variableName);
		undefVar.setImplicit(isImplicit(variableName));
		undefVar.setEndDate(contractPayment.getEndDate());
		undefVar.setStartDate(contractPayment.getStartDate());
		undefVar.setScope(getScope(contractPayment.getScope()));

		undefVar.setPayment(newPayment(contractPayment));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onRemove(IContractDeduction deduction) {
		Deduction draftDeduction = newDeduction(deduction);
		salaryDraft.addDeduction(draftDeduction);
	}

	@Override
	public void onCheckError(IContractDeduction deduction, String message) {
		DeductionEvent deductionEvent = new DeductionEvent();
		deductionEvent.setMessage(message);
		deductionEvent.setType(Event.Type.WARNING);
		deductionEvent.setDeduction(newDeduction(deduction));

		salaryDraft.addDeductionEevent(deductionEvent);

	}

	@Override
	public void onInvalidData(IContractDeduction deduction, String variableName,
			String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUndefinedData(IContractDeduction contractDeduction,
			RemovedExpressionVariable<?> var) {
		// TODO Auto-generated method stub
		UndefinedDeductionVariable undefVar = new UndefinedDeductionVariable();

		undefVar.setName(var.getName());
		undefVar.setImplicit(isImplicit(var.getName()));
		undefVar.setEndDate(var.getPeriod().getEnd());
		undefVar.setStartDate(var.getPeriod().getStart());
		undefVar.setScope(getScope(var.getExpression().getScope()));

		undefVar.setDeduction(newDeduction(contractDeduction));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onUndefinedData(IContractDeduction contractDeduction,
			String variableName, String message) {

		UndefinedDeductionVariable undefVar = new UndefinedDeductionVariable();

		undefVar.setName(variableName);
		undefVar.setImplicit(isImplicit(variableName));
		undefVar.setEndDate(contractDeduction.getEndDate());
		undefVar.setStartDate(contractDeduction.getStartDate());
		undefVar.setScope(getScope(contractDeduction.getScope()));

		undefVar.setDeduction(newDeduction(contractDeduction));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onCompileError(IContractDeduction contractDeduction,
			String message) {
		DeductionEvent event = new DeductionEvent();
		event.setMessage(message);
		Deduction deduction = newDeduction(contractDeduction);
		event.setDeduction(deduction);
		event.setType(Event.Type.ERROR);
		salaryDraft.addDeductionEevent(event);
	}

	@Override
	public void onCheckError(IContractBonus bonus, String message) {
		BonusEvent bonusEvent = new BonusEvent();
		bonusEvent.setMessage(message);
		bonusEvent.setType(Event.Type.WARNING);
		bonusEvent.setBonus(newBonus(bonus));
		salaryDraft.addBonusEvent(bonusEvent);

	}

	@Override
	public void onCompileError(IContractBonus bonus, String message) {
		BonusEvent bonusEvent = new BonusEvent();
		bonusEvent.setMessage(message);
		bonusEvent.setType(Event.Type.ERROR);
		bonusEvent.setBonus(newBonus(bonus));
		salaryDraft.addBonusEvent(bonusEvent);
	}

	@Override
	public void onInvalidData(IContractBonus bonus, String variableName,
			String message) {
		// TODO Auto-generated method stub
	}

	// -------------------------------------------------------------------------

	@Override
	public void onIrpf(IrpfOutcome irpfOutcome) {
		salaryDraft.setCommunity(irpfOutcome.getComunidadAutonoma());
	}
	
	@Override
	public void onLiquid(ISalary salary) {
		double totalPayment = salary.getTotalPayment();
		this.sed = () -> { replaceNETO(totalPayment); };

	}
	
	
	
	@Override
	public void onUndefinedData(IExpression expression, String variableName,
			String message, Date start, Date end) {
		UndefinedVariable undefVar = new UndefinedVariable();

		undefVar.setName(variableName);
		undefVar.setEndDate(end);
		undefVar.setStartDate(start);
		undefVar.setImplicit(isImplicit(variableName));
		undefVar.setScope(getScope(expression.getScope()));

		salaryDraft.addUndefinedVariable(undefVar);

	}

	@Override
	public void onRedefinedImplicit(String name, ITimedVariable<?> redefined,
			ITimedVariable<?> implicit) {
		
		ContextVariable var = ContextVariable.getVariableByName(name);
		
		if ( var == ContextVariable.IRPF_PERCENT )
			return;
		
		String restoreButton = String.format(Locale.ENGLISH
		,"Pulse el bot\u00F3n para restaurar el valor del sistema <button class='aon-icon aon-button-goto' onclick='javascript:addDraftVariable(\"%1$s\",\"SISTEMA(\\x27%1$s\\x27)\");javascript:calculate();'></button>"
		, name
		);
		
		
		if ( var != null ) {
			
			String description = null; 
			try {
				description = var.getDescription( new Locale("es"));
			} catch ( MissingResourceException e){
				
			}
			
			if ( AonStringUtils.isNotBlank(description) ) {
				try {
					salaryDraft.addWarning(String.format(
							"%s con valor %.2f esta redefinida con el valor %.2f." + restoreButton,
							description, 
							((Number)implicit.getValue(implicit.getPeriod())).doubleValue(),
							((Number)redefined.getValue(redefined.getPeriod())).doubleValue()
							));
				} catch ( ClassCastException e){
					salaryDraft.addWarning(String.format(
							"%s con valor '%s' esta redefinida con el valor '%s'." + restoreButton,
							description, 
							implicit.getValue(implicit.getPeriod()),
							redefined.getValue(redefined.getPeriod())
							));
				} catch ( Throwable e){
					salaryDraft.addWarning(String.format(
							"%s esta redefinida." + restoreButton,
							description
							));
				}
				return;
			}
		}
		
		try {
			salaryDraft.addWarning(String.format(
					"La variable del sistema '%s' con valor %.2f esta redefinida con el valor %.2f." + restoreButton,
					name, 
					((Number)implicit.getValue(implicit.getPeriod())).doubleValue(),
					((Number)redefined.getValue(redefined.getPeriod())).doubleValue()
					));
		} catch ( ClassCastException e ){
			salaryDraft.addWarning(String.format(
					"La variable del sistema '%s' con valor '%s' esta redefinida con el valor '%s'." + restoreButton,
					name, 
					implicit.getValue(implicit.getPeriod()),
					redefined.getValue(redefined.getPeriod())
					));
		} catch ( Throwable e){
			salaryDraft.addWarning(String.format(
					"La variable del sistema '%s' esta redefinida."  + restoreButton
					, name ));
		}
			
	}
	
	@Override
	public <T> T onConstantParameter(String func, T constant, ExpressionContext ctx) {
		
		salaryDraft.addWarning(String.format(
				"Revise el %1$s <span style='color:orange;'>%2$.2f</span>."
				+"<ul style='margin-left:1em;'>"
				+"<li>Si el %1s es proporcional a los d\u00edas trabajados deber\u00eda ser: <span style='color:orange;'>%1$s(%2$.2f * DIAS_TRABAJADOS / DIAS_MES) </span></li>" 
				+"<li>Si quiere garantizarlo. Debe utilizar un nuevo concepto <span style='color:orange;'>CRA 0055</span>"
				+"<ul style='margin-left:1em;'>"
				+"<li>GTZDO( TODO )</li>"
				+"<li>GTZDO( %1$s( %2$.2f * DIAS_TRABAJADOS / DIAS_MES ) )</li>"
				+"</ul>"
				+"</li> "
				+"</ul>"
				,func, ((Number)constant).doubleValue()) );
		
		return IListener.super.onConstantParameter(func, constant, ctx);
	}
	
	@Override
	public void onMistakenPartialFactor(double monthHours, double workedHours, double factor) {
		salaryDraft.addWarning(
				String.format(
				"Mes incompleto, el coeficente de parcialidad <span style='color:orange;'>%1$.2f</span>"
				+" no coincide con el coeficiente informado <span style='color:orange;'>%4$.2f</span>. </br>"
				+ "Ha trabajado <span style='color:orange;'>%3$.2f</span> horas sobre un total de "
				+"<span style='color:orange;'>%2$.2f</span>, asi que el coeficiente real es %3$.2f / %2$.2f = <span style='color:orange;'>%1$.2f</span></br>" 
				+ "Si desea mantener el coeficiente original <span style='color:orange;'>%4$.2f</span> "
				, workedHours / monthHours
				, monthHours
				, workedHours
				, factor) 
				+ String.format(Locale.ENGLISH
				,"pulse el bot\u00F3n <button class='aon-icon aon-button-goto' onclick='javascript:addDraftVariable(\"COEFICIENTE_PARCIALIDAD\",\"%f\");javascript:calculate();'></button>"
				,factor)
				);

	}
	
	@Override
	public void onRemove(IContractBonus contractBonus) {
		Bonus draftBonus = newBonus(contractBonus);
		salaryDraft.addBonus(draftBonus);
	}

	// -------------------------------------------------------------------------

	private void clearSalaryDraft() {
		salaryDraft.clear();

	}
	private void addContext(Map<String, ITimedVariable<?>> context) {
		for (Entry<String, ITimedVariable<?>> entry : context.entrySet()) {

			String name = entry.getKey();
			ITimedVariable<?> var = entry.getValue();
			try {
				addVariable(name, var);
			} catch ( Throwable t ) {
				System.err.print( "TODO [SalaryDraftBuilder]: Impossible get value of '" + entry + "'. " );
			}
		}
	}

	private void addVariable(String name, ITimedVariable<?> var) {

		ContextVariable contextVariable = ContextVariable
				.getVariableByName(name);
		if (contextVariable != null && contextVariable.isInternal()) {
			return;
		}

		Period period = var.getPeriod();
		Object value = var.getValue(period);

		if (value instanceof MethodStub)
			return;
		if (value instanceof FunctionInstance)
			return;
		if ( isPayment(name) )
			return;

		if (var instanceof IExpressionVariable<?>) {
			IExpressionVariable<?> exprVar = (IExpressionVariable<?>) var;
			IExpression expr = exprVar.getExpression();
			Scope scope = getScope(expr.getScope());
			addVariable(name, value, period.getStart(),
					period.getEnd(), scope, expr.getExpression(),
					defined.get(name));
			addContext(exprVar.getContext());
		} else {
			
			addVariable(name, value, period.getStart(),
					period.getEnd());
		}

	}

	private Deduction newDeduction(IContractDeduction contractDeduction) {

		Deduction deduction = new Deduction();

		deduction.setId(contractDeduction.getId());
		deduction.setName(contractDeduction.getName());
		deduction.setEndDate(contractDeduction.getEndDate());
		deduction.setStartDate(contractDeduction.getStartDate());
		deduction.setExpression(contractDeduction.getExpression());
		deduction.setScope(getScope(contractDeduction.getScope()));
		deduction.setType(getDeductionType(contractDeduction.getType()));
		deduction.setDescriptionTemplate(contractDeduction.getDescription());

		return deduction;
	}

	private Deduction newEmbargo(IContractEmbargo contractEmbargo) {

		Deduction embargo = new Deduction();

		embargo.setId(contractEmbargo.getId());
		embargo.setEndDate(contractEmbargo.getEndDate());
		embargo.setStartDate(contractEmbargo.getStartDate());
		embargo.setExpression(contractEmbargo.getExpression());
		embargo.setScope(getScope(contractEmbargo.getScope()));
		embargo.setType(getDeductionType(contractEmbargo.getType()));
		embargo.setDescriptionTemplate(contractEmbargo.getDescription());

		return embargo;
	}


	private Payment newAgreementPayment(IContractPayment contractPayment) {

		Payment payment = new Payment();

		payment.setId(contractPayment.getId());
		payment.setConceptId(contractPayment.getConceptId());
		payment.setType(getPaymentType(contractPayment.getType()));
		payment.setName(contractPayment.getName());
		payment.setMonth(getMonth(contractPayment.getMonth()));
		payment.setSalaryType(getSalaryType(contractPayment.getSalaryType()));
		payment.setStartDate(contractPayment.getStartDate());
		payment.setEndDate(contractPayment.getEndDate());
		payment.setExpression(contractPayment.getExpression());
		payment.setScope(getScope(contractPayment.getScope()));
		payment.setIrpfExpression(contractPayment.getIrpfExpression());
		payment.setQuoteExpression(contractPayment.getQuoteExpression());
		payment.setDescriptionTemplate(contractPayment.getDescription());

		return payment;
	}

	private Payment newPayment(IContractPayment contractPayment) {

		Payment payment = new Payment();

		payment.setId(contractPayment.getId());
		payment.setConceptId(contractPayment.getConceptId());
		payment.setType(getPaymentType(contractPayment.getType()));
		payment.setName(contractPayment.getName());
		payment.setMonth(getMonth(contractPayment.getMonth()));
		payment.setSalaryType(getSalaryType(contractPayment.getSalaryType()));
		payment.setStartDate(contractPayment.getStartDate());
		payment.setEndDate(contractPayment.getEndDate());
		payment.setExpression(contractPayment.getExpression());
		payment.setScope(getScope(contractPayment.getScope()));
		payment.setIrpfExpression(contractPayment.getIrpfExpression());
		payment.setQuoteExpression(contractPayment.getQuoteExpression());
		payment.setDescriptionTemplate(contractPayment.getDescription());

		if (!StringUtils.isBlank(contractPayment.getName())
				&& defined.containsKey(contractPayment.getName()))
			payment.setDefined(defined.get(contractPayment.getName()));

		return payment;
	}

	private Bonus newBonus(IContractBonus contractBonus) {
		Bonus bonus = new Bonus();
		bonus.setId(contractBonus.getId());
		bonus.setScope(getScope(contractBonus.getScope()));
		bonus.setName(bonus.getName());
		bonus.setExpression(contractBonus.getExpression());
		bonus.setType(getBonusType(contractBonus.getType()));
		bonus.setDescriptionTemplate(contractBonus.getDescription());
		return bonus;
	}

	private CompositePayment getPayment(Integer id) {
		List<Payment> payments = salaryDraft.getPayments();
		for (int i = 0; i < payments.size(); i++) {
			Payment payment = payments.get(i);
			if (AonNumberUtils.equals(payment.getId(), id)) {
				if (payment instanceof CompositePayment)
					return (CompositePayment) payment;

				CompositePayment composite = new CompositePayment();
				
				composite.setConceptId(payment.getConceptId());
				//composite.setDescription(payment.getDescription());
				composite.setExpression(payment.getExpression());
				composite.setIrpfExpression(payment.getIrpfExpression());
				composite.setQuoteExpression(payment.getQuoteExpression());
				composite.addChild(payment);
				payments.set(i, composite);
				return composite;

			}
		}
		return null;
	}

	private CompositeDeduction getDeduction(Integer id) {
		List<Deduction> deductions = salaryDraft.getDeductions();
		for (int i = 0; i < deductions.size(); i++) {
			Deduction deduction = deductions.get(i);
			if (deduction.getId().equals(id)) {
				if (deduction instanceof CompositeDeduction)
					return (CompositeDeduction) deduction;

				CompositeDeduction composite = new CompositeDeduction();
				composite.addChild(deduction);
				deductions.set(i, composite);
				return composite;

			}
		}
		return null;
	}

	private CompositeDeduction getCost(Integer id) {
		List<Deduction> costs = salaryDraft.getCosts();
		for (int i = 0; i < costs.size(); i++) {
			Deduction cost = costs.get(i);
			if (cost.getId().equals(id)) {
				if (cost instanceof CompositeDeduction)
					return (CompositeDeduction) cost;

				CompositeDeduction composite = new CompositeDeduction();
				composite.addChild(cost);
				costs.set(i, composite);
				return composite;

			}
		}
		return null;
	}
	// ------------------------------------------------------------------------

	private Short getMonth(Month month) {
		return month == null ? null : (short) month.getValue();
	}

	private Bonus.Type getBonusType(BonusType type) {
		return type == null ? null : Bonus.Type.values()[type.ordinal()];
	}

	private Deduction.Type getDeductionType(DeductionType type) {
		return type == null ? null : Deduction.Type.values()[type.ordinal()];
	}

	private Scope getScope(ExpressionScope exprScope) {
		return exprScope != null ? Scope.values()[exprScope.ordinal()]
				: Scope.SYSTEM;
	}

	private boolean isPayment(String name) {
		for (Payment payment : salaryDraft.getPayments())
			if (StringUtils.equals(name, payment.getName()))
				return true;
		return false;
	}

	private Stream<Variable> findVariable(String name, Object value, Date startDate, Date endDate){
		Date _startDate = AonDateUtils.addDays(startDate, -1);
		Date _endDate = AonDateUtils.addDays(endDate, 1);
		return
		salaryDraft
		.getContext()
		.stream()
		.filter(v -> AonUtils.equals(v.getName(),name))
		.filter(v -> AonUtils.equals(v.getValue(),value))
		.filter(v -> Period.compare(v.getEndDate(),_startDate) >= 0)
		.filter(v -> Period.compare(v.getStartDate(),_endDate) <= 0);
	}


	private void addVariable(String name, Object value, Date startDate, Date endDate ) {
		
		Variable vars [] = findVariable(name, value, startDate, endDate).toArray(Variable[]::new);
		for ( Variable var : vars ){ 
			salaryDraft.getContext().remove(var);
			endDate = Period.max(endDate,var.getEndDate());
			startDate = Period.min(startDate,var.getStartDate());
		}
		
		salaryDraft.addVariable(
				name, 
				value, 
				startDate, 
				endDate);
	}
	
	private void addVariable(String name, Object value, Date startDate, Date endDate,Scope scope, String expression, boolean defined[] ) {
		class Dates {
			Date start, end; 
		}
		Dates dates = new Dates();
		dates.start = startDate;
		dates.end = endDate;
		
		Variable vars [] = findVariable(name, value, startDate, endDate)
		.filter(var->var.getScope().equals(scope))
		.filter(var->AonStringUtils.equals(var.getExpression(),expression))
		.toArray(Variable[]::new);
		for ( Variable var : vars ){ 
			salaryDraft.getContext().remove(var);
			endDate = Period.max(endDate,var.getEndDate());
			startDate = Period.min(startDate,var.getStartDate());
		}
		salaryDraft.addVariable(
				name, 
				value, 
				dates.start, 
				dates.end,
				scope, 
				expression,
				defined);
	}
	
	private void replaceNETO(Double totalPayment) {
		salaryDraft.getPayments().stream()
		.filter(p -> p.getExpression() != null )
		.forEach(p -> replaceNETO(p, totalPayment))
		;
		salaryDraft.getDraftPayments().stream()
		.filter(p -> p.getExpression() != null )
		.forEach(p -> replaceNETO(p, totalPayment))
		;
	}

	private static void replaceNETO(Payment p, Double totalPayment) {
		p.setExpression(p.getExpression().replaceAll("NETO\\s*\\(", String.format(Locale.ROOT,"NETO(%.2f,", totalPayment)));
	}

	private static Payment.Type getPaymentType(PaymentType type) {
		return type != null ? Payment.Type.values()[type.ordinal()] : null;
	}

	private static Salary.Type getSalaryType(SalaryType type) {
		return type != null ? Salary.Type.values()[type.ordinal()] : null;
	}

	private static <T extends ISalaryItem<?>> List<T> getDbItemCounterParts(
			Collection<T> dbItems, Item<?> item) {

		List<T> nameMatchDbItems = new LinkedList<T>();
		List<T> fullMatchDbItems = new LinkedList<T>();

		String name = item.getName();
		Enum<?> type = item.getType();
		String description = item.getDescription();
		for (T dbPayment : dbItems) {
			if (!StringUtils.equals(name, dbPayment.getName())) {
				continue;
			}
			if (StringUtils.isBlank(name)
					&& !equals(type, dbPayment.getType())) {
				continue;
			}
			nameMatchDbItems.add(dbPayment);
			if (!StringUtils.equals(description, dbPayment.getDescription())) {
				continue;
			}
			fullMatchDbItems.add(dbPayment);
		}

		return fullMatchDbItems.size() > 0 ? fullMatchDbItems
				: nameMatchDbItems;
	}

	private static <T extends ISalaryItem<DeductionType>> List<T> getDbDeductionCounterParts(
			Collection<T> dbDeductions, Item<?> deduction) {
		List<T> matchDbItems = getDbItemCounterParts(dbDeductions, deduction);
		if (matchDbItems.size() > 0)
			return matchDbItems;

		List<T> typeMatchDbItems = new LinkedList<T>();
		for (T dbDeduction : dbDeductions)
			if (deduction.getType() != null && dbDeduction.getType() != null)
				if (deduction.getType().ordinal() == dbDeduction.getType()
						.ordinal())
					typeMatchDbItems.add(dbDeduction);

		return typeMatchDbItems;

	}

	private static <T extends ISalaryItem<DeductionType>> List<T> getDbEmbargoCounterParts(
			Collection<T> dbEmbargos, Item<?> embargo) {
		List<T> descriptionMatchDbItems = new LinkedList<T>();
		for (T dbEmbargo : dbEmbargos)
			if (AonStringUtils.equalsIgnoreCase(dbEmbargo.getDescription(), embargo.getDescription()))
				descriptionMatchDbItems.add(dbEmbargo);

		return descriptionMatchDbItems;

	}

	private static boolean equals(Enum<?> type1, Enum<?> type2) {
		if (type1 == type2)
			return true;
		if (type1 == null)
			return false;
		if (type2 == null)
			return false;

		return type1.ordinal() == type2.ordinal();
	}


	private static boolean isImplicit(String name) {
		ContextVariable var = ContextVariable.getVariableByName(name);
		if (var != null)
			return true;

		// TODO : Very, very ugly...
		return name
				.matches(String.format("%s_\\d+_\\d+",
						ContextVariable.COMMON_DISEASE_DAYS))
				|| name.matches(String.format("%s_\\d+_\\d+",
						ContextVariable.OCCUPATIONAL_DISEASE_DAYS))
				|| name.matches(String.format("%s",
						ContextVariable.COMMON_DISEASE_LACK_DAYS))
				;

	}
	

}
