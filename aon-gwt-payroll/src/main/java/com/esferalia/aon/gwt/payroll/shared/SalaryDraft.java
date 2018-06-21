package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.esferalia.aon.gwt.common.shared.StringUtils;

public class SalaryDraft extends SalaryPreview {

	public static enum Scope {

		SYSTEM, APPLICATION, AGREEMENT, CONTRACT, SALARY;

		public static final int NUM_VALUES = Scope.values().length;

	}

	private Integer dbId;

	private String enterpriseName;
	private String enterpriseCity;
	private String enterpriseAddress;
	private String enterpriseDocument;
	private String enterpriseCCC;

	private String employeeCity;
	private String employeeAddress;
	private String employeeName;
	private String employeeSS;
	private String employeeDocument;
	private String employeeQuoteGroup;
	private String employeeAgreementCategory;
	private Date employeeSeniorityDate;
	private String community;

	private Integer timeUnits;

	private Double cgcBase;
	private Double dbGgcBase;
	private Double rawCgcBase;
	private Double cgpBase;
	private Double dbGgpBase;
	private Double irpfBase;
	private Double dbIrpfBase;
	private Double inkindIrpfBase;
	private Double moneyIrpfBase;
	private Double dbMoneyIrpfBase;
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
	private List<Variable> dbContext;
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
	private List<Bonus> draftBonuses;

	private List<ITDataPerson> draftLeaveIts;

	public SalaryDraft() {
		context = new LinkedList<Variable>();
		dbContext = new LinkedList<Variable>();
		events = new LinkedList<Event>();
		payments = new LinkedList<Payment>();
		deductions = new LinkedList<Deduction>();
		costs = new LinkedList<Deduction>();
		bonuses = new LinkedList<Bonus>();
		embargos = new LinkedList<Deduction>();

		draftContext = new Stack<Variable>();
		draftPayments = new Stack<Payment>();
		draftDeductions = new Stack<Deduction>();
		draftEmbargos = new Stack<Deduction>();
		draftLeaveIts = new Stack<ITDataPerson>();
		draftBonuses = new Stack<Bonus>();
	}

	public SalaryDraft clear() {
		clearCosts();
		clearEvents();
		clearContext();
		clearBonuses();
		clearPayments();
		clearDeductions();
		clearEmbargos();
		clearBonuses();
		return this;
	}

	public SalaryDraft clearDb() {
		dbId = null;
		dbGgcBase = null;
		dbGgpBase = null;
		dbHExtraBase = null;
		dbIrpfBase = null;
		dbNonHExtraBase = null;
		dbProrationBase = null;
		dbRemuneration = null;
		dbTotalLiquid = null;
		dbTotalPayment = null;
		dbContext.clear();
		return this;
	}

	public SalaryDraft clearDrafts() {
		draftContext.clear();
		draftPayments.clear();
		draftDeductions.clear();
		draftBonuses.clear();
		draftEmbargos.clear();
		draftLeaveIts.clear();
		return this;
	}

	public boolean hasDrafts() {
		return (draftContext.size() > 0) || (draftPayments.size() > 0)
				|| (draftDeductions.size() > 0) || (draftEmbargos.size() > 0)
				|| (draftLeaveIts.size() > 0)
				|| (draftBonuses.size() > 0)
				;
	}

	public SalaryDraft addPayment(Payment payment) {
		payments.add(payment);
		return this;
	}

	public Payment addDraftPayment(Payment payment) {
		if (payment.getId() == null) {
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

	public SalaryDraft addDeduction(Deduction deduction) {
		deductions.add(deduction);
		return this;
	}

	public SalaryDraft addEmbargo(Deduction embargo) {
		embargos.add(embargo);
		return this;
	}

	public Deduction addDraftDeduction(Deduction deduction) {

		if (deduction.getId() == null) {
			deduction.setId((-1) * (draftDeductions.size() + 1));
		}

		Deduction oldDeduction = null;
		int i = draftDeductions.indexOf(deduction);
		if (i != -1) {
			oldDeduction = draftDeductions.remove(i);
		}
		draftDeductions.add(deduction);
		return oldDeduction;
	}

	public boolean removeDraftDeduction(Deduction deduction) {
		return draftDeductions.remove(deduction);
	}

	public Deduction addDraftEmbargo(Deduction embargo) {

		if (embargo.getId() == null) {
			embargo.setId((-1) * (draftEmbargos.size() + 1));
		}

		Deduction oldEmbargo = null;
		int i = draftEmbargos.indexOf(embargo);
		if (i != -1) {
			oldEmbargo = draftEmbargos.remove(i);
		}
		draftEmbargos.add(embargo);
		return oldEmbargo;
	}

	public boolean removeDraftEmbargo(Deduction embargo) {
		return draftEmbargos.remove(embargo);
	}

	public SalaryDraft addVariable(String name, Object value, Date startDate,
			Date endDate) {
		Variable var;
		if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = value == null ? null : value
					.toString();
		}
		var.setName(name);
		var.setStartDate(startDate);
		var.setEndDate(endDate);
		var.setScope(Scope.SYSTEM);
		var.setImplicit(true);
		if ( findVariable(name,startDate, endDate) == null )
		//if (!context.contains(var))
			context.add(var);
		return this;
	}

	public void addVariable(String name, Object value, Date startDate,
			Date endDate, Scope scope, String expression, boolean defined[]) {
		Variable var;
		if (value == null) {
			return;
		} else if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = String.valueOf(value);
		}
		var.setName(name);
		var.setStartDate(startDate);
		var.setEndDate(endDate);
		var.setScope(scope);
		var.setImplicit(false);
		var.setDefined(defined);
		var.expression = expression;
		if ( findVariable(name,startDate, endDate) == null )
		//if (!context.contains(var))
			context.add(var);
		
			
	}
	
	public Variable findVariable(String name, Date startDate, Date endDate) {
		for ( Variable var : context) {
			if ( !var.getName().equals(name) ) 
				continue;
			
			// TODO : var.getStartDate().equals(startDate) 
			if ( endDate != null && var.getStartDate().after(endDate) ) 
				continue;
			// TODO : var.getEndDate().equals(endtDate) 
			if ( var.getEndDate() != null && var.getEndDate().before(startDate) ) 
				continue;
			
			return var;
		}
		return null;
	}

	public Variable findVariable(Variable var) {
		return findVariable(var.getName(),var.getStartDate(), var.getEndDate());
	}

	public void addUndefinedVariable(UndefinedVariable var) {
		if ( findVariable(var) == null )
//		if (!context.contains(var))
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

	public SalaryDraft addDbVariable(String name, Object value, Date startDate,
			Date endDate) {
		Variable var;
		if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = value == null ? null : value
					.toString();
		}
		var.setName(name);
		var.setStartDate(startDate);
		var.setEndDate(endDate);
		var.setScope(Scope.SYSTEM);
		var.setImplicit(true);
		//if ( findVariable(name,startDate, endDate) == null )
		dbContext.add(var);
		return this;
	}

	public void addWarning(String message) {
		for (Event event : events)
			if (event.getType() == Event.Type.WARNING
					&& StringUtils.equals(message, event.getMessage()))
				return;

		Event warning = new Event();
		warning.setMessage(message);
		warning.setType(Event.Type.WARNING);
		events.add(warning);
	}

	public Bonus addDraftBonus(Bonus bonus) {

		if (bonus.getId() == null) {
			bonus.setId((-1) * (draftBonuses.size() + 1));
		}

		Bonus oldBonus = null;
		int i = draftBonuses.indexOf(bonus);
		if (i != -1) {
			oldBonus = draftBonuses.remove(i);
		}
		draftBonuses.add(bonus);
		return oldBonus;
	}

	public boolean removeDraftBonus(Bonus bonus) {
		return draftBonuses.remove(bonus);
	}


	public void addPaymentError(PaymentEvent paymentEvent) {
		events.add(paymentEvent);
	}

	public void addDeductionEevent(DeductionEvent deductionEvent) {
		events.add(deductionEvent);
	}

	public void addBonusEvent(BonusEvent bonusEvent) {
		events.add(bonusEvent);
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

	public List<Variable> getDbContext() {
		return dbContext;
	}

	public List<Variable> getDraftContext() {
		return draftContext;
	}

	public SalaryDraft setDraftContext(List<Variable> draftContext) {
		this.draftContext = draftContext;
		return this;
	}

	public List<Payment> getDraftPayments() {
		return draftPayments;
	}

	public SalaryDraft setDraftPayments(List<Payment> draftPayments) {
		this.draftPayments = draftPayments;
		return this;
	}

	public List<Deduction> getDraftDeductions() {
		return draftDeductions;
	}

	public SalaryDraft setDraftDeductions(List<Deduction> draftDeductions) {
		this.draftDeductions = draftDeductions;
		return this;
	}


	public List<Deduction> getDraftEmbargos() {
		return draftEmbargos;
	}
	
	public SalaryDraft setDraftEmbargos(List<Deduction> draftEmbargos) {
		this.draftEmbargos = draftEmbargos;
		return this;
	}

	public List<Bonus> getDraftBonuses() {
		return draftBonuses;
	}

	public void setDraftBonuses(List<Bonus> draftBonuses) {
		this.draftBonuses = draftBonuses;
	}
	
	public SalaryDraft setDraftLeaveIts(List<ITDataPerson> draftLeaveIts) {
		this.draftLeaveIts = draftLeaveIts;
		return this;
	}

	public List<ITDataPerson> getDraftLeaveIts() {
		return draftLeaveIts;
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

	public Integer getDbId() {
		return dbId;
	}

	public String getEmployeeCity() {
		return employeeCity;
	}

	public SalaryDraft setEmployeeCity(String employeeCity) {
		this.employeeCity = employeeCity;
		return this;
	}

	public String getEmployeeAddress() {
		return employeeSS;
	}

	public SalaryDraft setEmployeeAddress(String employeeAddress) {
		this.employeeAddress = employeeAddress;
		return this;
	}
	
	public String getEmployeeSS() {
		return employeeSS;
	}

	public SalaryDraft setEmployeeSS(String employeeSS) {
		this.employeeSS = employeeSS;
		return this;
	}

	public Date getEmployeeSeniorityDate() {
		return employeeSeniorityDate;
	}

	public SalaryDraft setEmployeeSeniorityDate(Date employeeSeniorityDate) {
		this.employeeSeniorityDate = employeeSeniorityDate;
		return this;
	}

	public Double getIrpfBase() {
		return irpfBase;
	}

	public SalaryDraft setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
		return this;
	}

	public Double getInkindIrpfBase() {
		return inkindIrpfBase;
	}

	public SalaryDraft setInkindIrpfBase(Double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
		return this;
	}
	
	public Double getMoneyIrpfBase() {
		return moneyIrpfBase;
	}
	
	public SalaryDraft setMoneyIrpfBase(Double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
		return this;
	}

	public Double getProrationBase() {
		return prorationBase;
	}

	public String getCommunity() {
		return community;
	}

	public SalaryDraft setCommunity(String community) {
		this.community = community;
		return this;
	}

	public SalaryDraft setProrationBase(Double prorationBase) {
		this.prorationBase = prorationBase;
		return this;
	}

	public String getEmployeeDocument() {
		return employeeDocument;
	}

	public SalaryDraft setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
		return this;
	}
	
	public Integer getTimeUnits() {
		return timeUnits;
	}

	public void setTimeUnits(Integer timeUnits) {
		this.timeUnits = timeUnits;
	}
	
	public Double getCgcBase() {
		return cgcBase;
	}

	public SalaryDraft setCgcBase(Double cgcBase) {
		this.cgcBase = cgcBase;
		return this;
	}

	public Double getDbGgcBase() {
		return dbGgcBase;
	}

	public SalaryDraft setDbGgcBase(Double dbGgcBase) {
		this.dbGgcBase = dbGgcBase;
		return this;
	}

	public Double getRawCgcBase() {
		return rawCgcBase;
	}

	public SalaryDraft setRawCgcBase(Double rawCgcBase) {
		this.rawCgcBase = rawCgcBase;
		return this;
	}

	public Double getDbGgpBase() {
		return dbGgpBase;
	}

	public SalaryDraft setDbGgpBase(Double dbGgpBase) {
		this.dbGgpBase = dbGgpBase;
		return this;
	}

	public Double getDbIrpfBase() {
		return dbIrpfBase;
	}

	public SalaryDraft setDbIrpfBase(Double dbIrpfBase) {
		this.dbIrpfBase = dbIrpfBase;
		return this;
	}

	public Double getDbInkindIrpfBase() {
		return dbInkindIrpfBase;
	}
	
	public Double getDbMoneyIrpfBase() {
		return dbMoneyIrpfBase;
	}
	
	public SalaryDraft setDbMoneyIrpfBase(Double dbMoneyIrpfBase) {
		this.dbMoneyIrpfBase = dbMoneyIrpfBase;
		return this;
	}
	
	public SalaryDraft setDbInkindIrpfBase(Double dbInkindIrpfBase) {
		this.dbInkindIrpfBase = dbInkindIrpfBase;
		return this;
	}

	public Double getDbHExtraBase() {
		return dbHExtraBase;
	}

	public SalaryDraft setDbHExtraBase(Double dbHExtraBase) {
		this.dbHExtraBase = dbHExtraBase;
		return this;
	}

	public Double getDbNonHExtraBase() {
		return dbNonHExtraBase;
	}

	public SalaryDraft setDbNonHExtraBase(Double dbNonHExtraBase) {
		this.dbNonHExtraBase = dbNonHExtraBase;
		return this;
	}

	public Double getDbProrationBase() {
		return dbProrationBase;
	}

	public SalaryDraft setDbProrationBase(Double dbProrationBase) {
		this.dbProrationBase = dbProrationBase;
		return this;
	}

	public Double getDbRemuneration() {
		return dbRemuneration;
	}

	public SalaryDraft setDbRemuneration(Double dbRemuneration) {
		this.dbRemuneration = dbRemuneration;
		return this;
	}

	public Double getDbTotalLiquid() {
		return dbTotalLiquid;
	}

	public SalaryDraft setDbTotalLiquid(Double dbTotalLiquid) {
		this.dbTotalLiquid = dbTotalLiquid;
		return this;
	}

	public Double getDbTotalPayment() {
		return dbTotalPayment;
	}

	public SalaryDraft setDbTotalPayment(Double dbTotalPayment) {
		this.dbTotalPayment = dbTotalPayment;
		return this;
	}

	public SalaryDraft setDbTotalDeduction(Double dbTotalDeduction) {
		this.dbTotalDeduction = dbTotalDeduction;
		return this;
	}

	public Double getDbTotalDeduction() {
		return dbTotalDeduction;
	}

	public Double getCgpBase() {
		return cgpBase;
	}

	public SalaryDraft setCgpBase(Double cgpBase) {
		this.cgpBase = cgpBase;
		return this;
	}

	public Double gethExtraBase() {
		return hExtraBase;
	}

	public SalaryDraft sethExtraBase(Double hExtraBase) {
		this.hExtraBase = hExtraBase;
		return this;
	}

	public Double getNonHExtraBase() {
		return nonHExtraBase;
	}

	public SalaryDraft setNonHExtraBase(Double nonHExtraBase) {
		this.nonHExtraBase = nonHExtraBase;
		return this;
	}

	public Double getRemuneration() {
		return remuneration;
	}

	public SalaryDraft setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
		return this;
	}

	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public SalaryDraft setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
		return this;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public SalaryDraft setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
		return this;
	}

	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public SalaryDraft setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
		return this;
	}

	public String getEnterpriseCity() {
		return enterpriseCity;
	}

	public SalaryDraft setEnterpriseCity(String enterpriseCity) {
		this.enterpriseCity = enterpriseCity;
		return this;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public SalaryDraft setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}

	public SalaryDraft setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public SalaryDraft setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}

	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public SalaryDraft setEnterpriseCCC(String enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
		return this;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public SalaryDraft setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}

	public String getEmployeeQuoteGroup() {
		return employeeQuoteGroup;
	}

	public SalaryDraft setEmployeeQuoteGroup(String employeeQuoteGroup) {
		this.employeeQuoteGroup = employeeQuoteGroup;
		return this;
	}

	public String getEmployeeAgreementCategory() {
		return employeeAgreementCategory;
	}

	public SalaryDraft setEmployeeAgreementCategory(String employeeAgreementCategory) {
		this.employeeAgreementCategory = employeeAgreementCategory;
		return this;
	}

	public boolean hasDbSalary() {
		return dbId != null;
	}

	public SalaryDraft setDbId(Integer dbId) {
		this.dbId = dbId;
		return this;
	}
	
	// ------------------------------------------------------------------------

}
