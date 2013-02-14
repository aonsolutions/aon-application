package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class SalaryDraft extends SalaryPreview {

	public abstract static class Item implements Serializable {

		protected Double amount;
		protected String description;

		public Double getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}

	}

	public static class Payment extends Item {

		public static enum Type {
			BASE_SALARY, SALARY_SUPPLEMENTS, STRUCTURAL_HOURS, NON_STRUCTURAL_HOURS, SPECIAL_BONUSES, SALARY_IN_KIND, COMPENSATION_OR_PREPAID_EXPENSES, SOCIAL_SECURITY_BENEFITS, MOVING_COMPENSATION, OTHER_NON_WAGE;

		}

		private Type type;

		public Type getType() {
			return type;
		}


	}

	public static class Deduction extends Item {

		public static enum Type {
			COMMON_CONTINGENCY, PROFESSIONAL_CONTINGENCY, UNEMPLOYMENT, JOB_TRAINING, STRUCTURAL_OVERTIME, NON_STRUCTURAL_OVERTIME, IRPF, ADVANCE_PAYMENT, IN_KIND, OTHER, FOGASA // TODO:
			;

		}

		private Type type;

		public Type getType() {
			return type;
		}


	}

	public static class Event implements Serializable{
		private String message;

		public Event(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}
	
	

	public abstract static class Variable implements Serializable {
		
		private String name;
		private Date startDate;
		private Date endDate;


		public String getName() {
			return name;
		}


		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public abstract Object getValue();
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Variable))
				return false;
			Variable var = (Variable) obj;
			return ((name == var.name) || ( ( name != null ) && name.equals(var.name))) ;
		}
		
		
	}
	
	public static class StringVariable extends Variable  {
		
		private String value;
		
		
		
		@Override
		public Object getValue() {
			return value;
		}
		
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}

	public static class NumberVariable extends Variable  {
		
		private Number value;
		

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
		
	}

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

	private Double cgcBase;
	private Double cgpBase;
	private Double irpfBase;
	private Double hExtraBase;
	private Double nonHExtraBase;
	private Double prorationBase;

	private Double remuneration;
	private Double totalLiquid;
	private Double totalPayment;

	private List<Variable> context;
	private List<Event> errors;
	private List<Payment> payments;
	private List<Deduction> deductions;

	private List<Variable> draftContext;

	public SalaryDraft() {
		context = new LinkedList<Variable>();
		errors = new LinkedList<Event>();
		payments = new LinkedList<Payment>();
		deductions = new LinkedList<Deduction>();
		
		draftContext = new LinkedList<Variable>();
	}

	public void addError(String message) {
		errors.add(new Event(message));
	}
	

	public void addPayment(Payment.Type type, String description, Double amount) {
		Payment payment = new Payment();
		payment.type =  type;
		payment.amount = amount;
		payment.description = description;
		payments.add(payment);
	}

	public void addDeduction(Deduction.Type type, String description,
			Double amount) {
		Deduction deduction = new Deduction();
		deduction.type = type;
		deduction.amount = amount;
		deduction.description = description;
		deductions.add(deduction);
	}

	public void addVariable(String name, Object value, Date startDate, Date endDate){
		Variable var;
		if ( value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable)var).value = (Number)value;
		}
		else {
			var = new StringVariable();
			((StringVariable)var).value = value.toString();
		}
		var.name = name;
		var.startDate = startDate;
		var.endDate = endDate;
		if ( !context.contains(var))
			context.add(var);
	}

	public void addContextVariable(String name, Object value, Date startDate, Date endDate){
		Variable var = new StringVariable();
		var.name = name;
		var.startDate = startDate;
		var.endDate = endDate;
		((StringVariable)var).value = value.toString();
		int i = draftContext.indexOf(var);
		if ( i != -1 ) {
			draftContext.remove(i);
		}
		draftContext.add(var);
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

	public void clearDeductions() {
		deductions.clear();
	}

	public List<Event> getErrors() {
		return errors;
	}
	
	public List<Variable> getContext() {
		return context;
	}
	
	public List<Variable> getDraftContext() {
		return draftContext;
	}

	public void clearContext() {
		context.clear();
	}

	public void clearErrors() {
		errors.clear();
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

	public Double getProrationBase() {
		return prorationBase;
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

	private Double totalDeduction;

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

}
