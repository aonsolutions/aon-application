package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SalaryDraft extends SalaryPreview {

	
	public static enum Scope {
		
		SYSTEM, APPLICATION, AGREEMENT, CONTRACT, SALARY;
		
		public static final int NUM_VALUES= Scope.values().length;
		
	}
	
	public static class Event implements Serializable {
		public static enum Type {
			ERROR, WARNING, INFO, DEBUG
		};

		Type type;
		String message;

		public Type getType() {
			return type;
		}
		
		public void setType(Type type) {
			this.type = type;
		}

		public String getMessage() {
			return message;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}

	}

	public static class PaymentEvent extends Event implements HasPayment {
		Payment payment;
		
		@Override
		public Payment getPayment() {
			return payment;
		}
		
		public void setPayment(Payment payment) {
			this.payment = payment;
		}
		
	}
	
	public static class DeductionEvent extends Event implements HasDeduction{
		Deduction deduction;
		
		@Override
		public Deduction getDeduction() {
			return deduction;
		}
		
		public void setDeduction(Deduction deduction) {
			this.deduction = deduction;
		}
	}

	private boolean hasDbSalary;
	

	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;
	private String enterpriseCCC;

	private String employeeName;
	private String employeeSS;
	private String employeeDocument;
	private String employeeQuoteGroup;
	private String employeeAgreementCategory;
	private Date employeeSeniorityDate;
	private String community;

	private Double cgcBase;
	private Double dbGgcBase;
	private Double rawCgcBase;
	private Double cgpBase;
	private Double dbGgpBase;
	private Double irpfBase;
	private Double dbIrpfBase;
	private Double inkindIrpfBase;
	private Double dbInkindIrpfBase;
	private Double hExtraBase;
	private Double dbHExtraBase;
	private Double nonHExtraBase;
	private Double dbNonHExtraBase;
	private Double prorationBase;
	private Double dbProrationBase;

	private Double remuneration;
	private Double dbRemuneration;
	private Double totalLiquid;
	private Double dbTotalLiquid;
	private Double totalPayment;
	private Double dbTotalPayment;
	private Double totalDeduction;
	private Double dbTotalDeduction;

	private List<Variable> context;
	private List<Event> events;
	private List<Payment> payments;
	private List<Deduction> deductions;
	private List<Deduction> embargos;

	private List<Bonus> bonuses;
	private List<Deduction> costs;

	private List<Variable> draftContext;
	private List<Payment> draftPayments;
	private List<Deduction> draftDeductions;
	private List<Deduction> draftEmbargos;
	
	public SalaryDraft() {
		context = new LinkedList<Variable>();
		events = new LinkedList<Event>();
		payments = new LinkedList<Payment>();
		deductions = new LinkedList<Deduction>();
		costs = new LinkedList<Deduction>();
		bonuses = new LinkedList<Bonus>();
		embargos = new LinkedList<Deduction>();

		draftContext = new LinkedList<Variable>();
		draftPayments = new LinkedList<Payment>();
		draftDeductions = new LinkedList<Deduction>();
		draftEmbargos = new LinkedList<Deduction>();
	}

	public void clear() {
		clearDb();
		clearCosts();
		clearEvents();
		clearContext();
		clearBonuses();
		clearPayments();
		clearDeductions();
	}

	public void clearDb() {
		hasDbSalary = false ;
		dbGgcBase = null;
		dbGgpBase = null;
		dbHExtraBase = null;
		dbIrpfBase = null;
		dbNonHExtraBase = null;
		dbProrationBase = null;
		dbRemuneration = null;
		dbTotalLiquid = null;
		dbTotalPayment = null;
	}

	public void clearDrafts(){
		draftContext.clear();
		draftPayments.clear();
		draftDeductions.clear();
		draftEmbargos.clear();
	}
	
	
	public boolean hasDrafts(){
		return  (draftContext.size() > 0) ||
				(draftPayments.size() > 0) ||
				(draftDeductions.size() > 0 )||
				(draftEmbargos.size() > 0);
	}
	
	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	
	public Payment addDraftPayment(Payment payment) {
		if ( payment.getId() == null ) {
			payment.setId((-1) * (draftPayments.size() + 1));
		}

		Payment oldPaymnet = null;
		
		int i = draftPayments.indexOf(payment);
		if (i != -1) {
			oldPaymnet = draftPayments.remove(i);
		}
		
		draftPayments.add(payment);
		return oldPaymnet;
	}
	
	public boolean removeDraftPayment(Payment payment) {
		return draftPayments.remove(payment);
	}
	
	
	public void addDeduction(Deduction deduction) {
		deductions.add(deduction);
	}
	
	public void addEmbargo(Deduction embargo) {
		deductions.add(embargo);
	}
	
	public Deduction addDraftDeduction(Deduction deduction) {
		
		
		
		if ( deduction.getId() == null ) {
			deduction.setId((-1) * ( draftDeductions.size() + 1));
		}
		
		Deduction oldDeduction = null;
		int i = draftDeductions.indexOf(deduction);
		if ( i != -1 ) {
			oldDeduction = draftDeductions.remove(i);
		}
		draftDeductions.add(deduction);
		return oldDeduction;
	}
	
	

	public boolean removeDraftDeduction(Deduction deduction) {
		return draftDeductions.remove(deduction);
	}

	public Deduction addDraftEmbargo(Deduction embargo) {
		
		if ( embargo.getId() == null ) {
			embargo.setId((-1) * ( draftEmbargos.size() + 1));
		}
		
		Deduction oldEmbargo = null;
		int i = draftEmbargos.indexOf(embargo);
		if ( i != -1 ) {
			oldEmbargo = draftEmbargos.remove(i);
		}
		draftEmbargos.add(embargo);
		return oldEmbargo;
	}
	
	public boolean removeDraftEmbargo(Deduction embargo) {
		return draftEmbargos.remove(embargo);
	}
	

	public void addVariable(String name, Object value, Date startDate,
			Date endDate) {
		Variable var;
		if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = value == null ? null : value.toString();
		}
		var.setName( name );
		var.setStartDate (startDate);
		var.setEndDate(endDate);
		var.setScope(Scope.SYSTEM);
		var.setImplicit(true);
		if (!context.contains(var))
			context.add(var);
	}

	public void addVariable(String name, Object value, Date startDate,
			Date endDate, Scope scope, String expression, boolean defined []) {
		Variable var;
		if (value == null ) {
			return;
		}else if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = String.valueOf(value);
		}
		var.setName( name );
		var.setStartDate (startDate);
		var.setEndDate(endDate);
		var.setScope(scope);
		var.setImplicit(false);
		var.setDefined(defined);
		var.expression = expression;
		if (!context.contains(var))
			context.add(var);
	}

	public void addUndefinedVariable(UndefinedVariable var) {
		if (!context.contains(var))
			context.add(var);
	}
	
	public Variable addDraftVariable(Variable var) {
		Variable oldVariable = null;

		int i = draftContext.indexOf(var);
		if (i != -1) {
			oldVariable = draftContext.remove(i);
		}
		
		draftContext.add(var);
		return oldVariable;
	}

	
	public boolean removeDraftVariable(Variable variable) {
			return draftContext.remove(variable);
	}
	

	public void addPaymentError(PaymentEvent paymentEvent) {

		events.add(paymentEvent);
	}
	
	public void addDeductionEevent(DeductionEvent deductionEvent) {
		events.add(deductionEvent);
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void clearPayments() {
		payments.clear();
	}

	public List<Deduction> getDeductions() {
		return deductions;
	}
	
	public List<Deduction> getEmbargos() {
		return embargos;
	}
	
	public void clearDeductions() {
		deductions.clear();
	}

	public void clearEmbargos() {
		embargos.clear();
	}

	public List<Event> getEvents() {
		return events;
	}

	public List<Variable> getContext() {
		return context;
	}

	public List<Variable> getDraftContext() {
		return draftContext;
	}
	
	public void setDraftContext(List<Variable> draftContext) {
		this.draftContext = draftContext;
	}

	public List<Payment> getDraftPayments() {
		return draftPayments;
	}
	
	public void setDraftPayments(List<Payment> draftPayments) {
		this.draftPayments = draftPayments;
	}

	public List<Deduction> getDraftDeductions() {
		return draftDeductions;
	}
	
	public void setDraftDeductions(List<Deduction> draftDeductions) {
		this.draftDeductions = draftDeductions;
	}
	
	public List<Deduction> getDraftEmbargos() {
		return draftEmbargos;
	}
	
	public void setDraftEmbargos(List<Deduction> draftEmbargos) {
		this.draftEmbargos = draftEmbargos;
	}

	public void clearContext() {
		context.clear();
	}

	public void clearEvents() {
		events.clear();
	}
	
	public void addCost(Deduction cost) {
		costs.add(cost);
	}
	
	public List<Deduction> getCosts() {
		return costs;
	}
	
	public void clearCosts() {
		costs.clear();
	}

	public void addBonus(Bonus bonus) {
		bonuses.add(bonus);
	}
	
	public List<Bonus> getBonuses() {
		return bonuses;
	}
	
	public void clearBonuses() {
		bonuses.clear();
	}

	public String getEmployeeSS() {
		return employeeSS;
	}

	public void setEmployeeSS(String employeeSS) {
		this.employeeSS = employeeSS;
	}

	public Date getEmployeeSeniorityDate() {
		return employeeSeniorityDate;
	}

	public void setEmployeeSeniorityDate(Date employeeSeniorityDate) {
		this.employeeSeniorityDate = employeeSeniorityDate;
	}

	public Double getIrpfBase() {
		return irpfBase;
	}

	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}
	
	public Double getInkindIrpfBase() {
		return inkindIrpfBase;
	}
	
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
	}

	public Double getProrationBase() {
		return prorationBase;
	}
	
	public String getCommunity() {
		return community;
	}
	
	public void setCommunity(String community) {
		this.community = community;
	}
	
	public void setProrationBase(Double prorationBase) {
		this.prorationBase = prorationBase;
	}

	public String getEmployeeDocument() {
		return employeeDocument;
	}

	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}

	public Double getCgcBase() {
		return cgcBase;
	}

	public void setCgcBase(Double cgcBase) {
		this.cgcBase = cgcBase;
	}

	public Double getDbGgcBase() {
		return dbGgcBase;
	}

	public void setDbGgcBase(Double dbGgcBase) {
		this.dbGgcBase = dbGgcBase;
	}
	
	public Double getRawCgcBase() {
		return rawCgcBase;
	}
	
	public void setRawCgcBase(Double rawCgcBase) {
		this.rawCgcBase = rawCgcBase;
	}
	
	public Double getDbGgpBase() {
		return dbGgpBase;
	}

	public void setDbGgpBase(Double dbGgpBase) {
		this.dbGgpBase = dbGgpBase;
	}
	
	public Double getDbIrpfBase() {
		return dbIrpfBase;
	}

	public void setDbIrpfBase(Double dbIrpfBase) {
		this.dbIrpfBase = dbIrpfBase;
	}
	
	public Double getDbInkindIrpfBase() {
		return dbInkindIrpfBase;
	}
	
	public void setDbInkindIrpfBase(Double dbInkindIrpfBase) {
		this.dbInkindIrpfBase = dbInkindIrpfBase;
	}

	public Double getDbHExtraBase() {
		return dbHExtraBase;
	}

	public void setDbHExtraBase(Double dbHExtraBase) {
		this.dbHExtraBase = dbHExtraBase;
	}

	public Double getDbNonHExtraBase() {
		return dbNonHExtraBase;
	}

	public void setDbNonHExtraBase(Double dbNonHExtraBase) {
		this.dbNonHExtraBase = dbNonHExtraBase;
	}

	public Double getDbProrationBase() {
		return dbProrationBase;
	}

	public void setDbProrationBase(Double dbProrationBase) {
		this.dbProrationBase = dbProrationBase;
	}

	public Double getDbRemuneration() {
		return dbRemuneration;
	}

	public void setDbRemuneration(Double dbRemuneration) {
		this.dbRemuneration = dbRemuneration;
	}

	public Double getDbTotalLiquid() {
		return dbTotalLiquid;
	}

	public void setDbTotalLiquid(Double dbTotalLiquid) {
		this.dbTotalLiquid = dbTotalLiquid;
	}

	public Double getDbTotalPayment() {
		return dbTotalPayment;
	}

	public void setDbTotalPayment(Double dbTotalPayment) {
		this.dbTotalPayment = dbTotalPayment;
	}
	
	public void setDbTotalDeduction(Double dbTotalDeduction) {
		this.dbTotalDeduction = dbTotalDeduction;
	}
	
	public Double getDbTotalDeduction() {
		return dbTotalDeduction;
	}

	public Double getCgpBase() {
		return cgpBase;
	}

	public void setCgpBase(Double cgpBase) {
		this.cgpBase = cgpBase;
	}

	public Double gethExtraBase() {
		return hExtraBase;
	}

	public void sethExtraBase(Double hExtraBase) {
		this.hExtraBase = hExtraBase;
	}

	public Double getNonHExtraBase() {
		return nonHExtraBase;
	}

	public void setNonHExtraBase(Double nonHExtraBase) {
		this.nonHExtraBase = nonHExtraBase;
	}

	public Double getRemuneration() {
		return remuneration;
	}

	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}


	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}

	public void setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}

	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public void setEnterpriseCCC(String enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeQuoteGroup() {
		return employeeQuoteGroup;
	}

	public void setEmployeeQuoteGroup(String employeeQuoteGroup) {
		this.employeeQuoteGroup = employeeQuoteGroup;
	}

	public String getEmployeeAgreementCategory() {
		return employeeAgreementCategory;
	}

	public void setEmployeeAgreementCategory(String employeeAgreementCategory) {
		this.employeeAgreementCategory = employeeAgreementCategory;
	}

	public boolean hasDbSalary() {
		return hasDbSalary;
	}

	public void setHasDbSalary(boolean hasDbSalary) {
		this.hasDbSalary = hasDbSalary;
	}
	
	
}
