package com.esferalia.aon.occam.api.model;

import java.util.List;

public class Settle extends Salary {

	private String representativeName;
	private String representativeDocument;
	private String cause;
	private String location;

	public Settle() {
		Salary s = new Salary();
		fillWithSalary(s);
	}
	
	public static Settle instance() {return new Settle();}
	
	public void fillWithSalary(Salary salary) {

		this.setEmployeeName(salary.getEmployeeName());
		this.setEmployeeDocument(salary.getEmployeeDocument());
		this.setEmployeeCategory(salary.getEmployeeCategory());
		this.setEmployeeQuoteGroup(salary.getEmployeeQuoteGroup());
		this.setEmployeeSeniorityDate(salary.getEmployeeSeniorityDate());
		this.setEmployeeSSNumber(salary.getEmployeeSSNumber());

		this.setEnterpriseAddress(salary.getEnterpriseAddress());
		this.setEnterpriseCCC(salary.getEnterpriseCCC());
		this.setEnterpriseDocument(salary.getEnterpriseDocument());
		this.setEnterpriseName(salary.getEnterpriseName());

		this.setEndDate(salary.getEndDate());
		this.setIssueDate(salary.getIssueDate());

		for (Deduction deduction : salary.getDeductions()) {
			this.addDeduction(
				(byte) deduction.getDeductionType().ordinal(), 
				deduction.getDescription(),
				deduction.getAmount(), 
				(byte) deduction.getDeductionType().ordinal()
			);
		}

		for (Payment payment : salary.getPayments()) {
			this.addPayment(
				payment.getName(),
				payment.getExpression(),
				payment.getDescription(),
				payment.getAmount(),
				payment.getQuote(),
				payment.getPaymentType()
			);
		}
		
		for (String key : salary.getContextData().keySet())
		{
			List<ContextData> dataList = salary.getContextData().get(key);
			for (ContextData data : dataList)
			{
				this.addContextData(key, data.getExpression(), data.getStartDate(), data.getEndDate());
			}			
		}
		
		this.setId(salary.getId());
		this.setStartDate(salary.getStartDate());

		this.setTotalDeduction(salary.getTotalDeduction());
		this.setTotalEnterprise(salary.getTotalEnterprise());
		this.setTotalIrpf(salary.getTotalIrpf());
		this.setTotalLiquid(salary.getTotalLiquid());
		this.setTotalPayment(salary.getTotalPayment());
		this.setTotalSSContributions(salary.getTotalSSContributions());

	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public Settle setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Settle setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public String getCause() {
		return cause;
	}

	public Settle setCause(String cause) {
		this.cause = cause;
		return this;
	}

	public String getLocation() {
		return location;
	}

	public Settle setLocation(String location) {
	    this.location = location;
	    return this;
	}





}
