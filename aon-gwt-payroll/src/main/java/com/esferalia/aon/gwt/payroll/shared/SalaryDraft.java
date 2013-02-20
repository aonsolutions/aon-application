package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SalaryDraft extends SalaryPreview {

	public static enum Scope {
		SYSTEM, APPLICATION, AGREEMENT, CONTRACT, SALARY
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

	public static class StringVariable extends Variable {

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

	public static class NumberVariable extends Variable {

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

	public static class UndefinedVariable extends Variable {
		@Override
		public Object getValue() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	public static class UndefinedPaymentVariable extends UndefinedVariable implements HasPayment {
		private Payment payment;
		
		@Override
		public Payment getPayment() {
			return payment;
		}
		
		public void setPayment(Payment payment) {
			this.payment = payment;
		}
	}

	public static class UndefinedDeductionVariable extends UndefinedVariable implements HasDeduction{

		private Deduction deduction;
		
		@Override
		public Deduction getDeduction() {
			return deduction;
		}
		
		public void setDeduction(Deduction deduction) {
			this.deduction = deduction;
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
	private List<Event> events;
	private List<Payment> payments;
	private List<Deduction> deductions;

	private List<Variable> draftContext;
	private List<Payment> draftPayments;
	private List<Deduction> draftDeductions;

	public SalaryDraft() {
		context = new LinkedList<Variable>();
		events = new LinkedList<Event>();
		payments = new LinkedList<Payment>();
		deductions = new LinkedList<Deduction>();

		draftContext = new LinkedList<Variable>();
		draftPayments = new LinkedList<Payment>();
		draftDeductions = new LinkedList<Deduction>();
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	public void addDraftPayment(Payment payment) {
		int i = draftPayments.indexOf(payment);
		if (i != -1) {
			draftPayments.remove(i);
		}
		draftPayments.add(payment);
	}

	public void addDeduction(Deduction deduction) {
		deductions.add(deduction);
	}
	
	public void addDraftDeduction(Deduction deduction) {
		draftDeductions.remove(deduction);
		draftDeductions.add(deduction);
	}

	public void addVariable(String name, Object value, Date startDate,
			Date endDate) {
		Variable var;
		if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = value.toString();
		}
		var.name = name;
		var.startDate = startDate;
		var.endDate = endDate;
		var.scope = Scope.SYSTEM;
		var.implicit = true;
		if (!context.contains(var))
			context.add(var);
	}

	public void addVariable(String name, Object value, Date startDate,
			Date endDate, Scope scope, String expression) {
		Variable var;
		if (value instanceof Number) {
			var = new NumberVariable();
			((NumberVariable) var).value = (Number) value;
		} else {
			var = new StringVariable();
			((StringVariable) var).value = value.toString();
		}
		var.name = name;
		var.startDate = startDate;
		var.endDate = endDate;
		var.scope = scope;
		var.implicit = false;
		var.expression = expression;
		if (!context.contains(var))
			context.add(var);
	}

	public void addUndefinedVariable(UndefinedVariable var) {
		if (!context.contains(var))
			context.add(var);
	}

	public void addContextVariable(String name, Object value, Date startDate,
			Date endDate) {
		Variable var = new StringVariable();
		var.name = name;
		var.startDate = startDate;
		var.endDate = endDate;
		var.implicit = false;
		var.scope = Scope.SALARY; // DRAFT
		var.expression = value.toString();
		((StringVariable) var).value = value.toString();
		int i = draftContext.indexOf(var);
		if (i != -1) {
			draftContext.remove(i);
		}
		draftContext.add(var);
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

	public void clearDeductions() {
		deductions.clear();
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

	public List<Payment> getDraftPayments() {
		return draftPayments;
	}

	public List<Deduction> getDraftDeductions() {
		return draftDeductions;
	}

	public void clearContext() {
		context.clear();
	}

	public void clearEvents() {
		events.clear();
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
