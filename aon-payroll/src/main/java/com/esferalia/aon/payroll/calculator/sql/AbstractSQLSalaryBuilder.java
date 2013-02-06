package com.esferalia.aon.payroll.calculator.sql;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public abstract class AbstractSQLSalaryBuilder implements ISalaryBuilder {

	protected AbstractSQL.Salary salary;
	protected List<AbstractSQL.SalaryCost> salaryCosts;
	protected List<AbstractSQL.SalaryBonus> salaryBonuses;
	protected List<AbstractSQL.SalaryEmbargo> salaryEmbargos;
	protected List<AbstractSQL.SalaryPayment> salaryPayments;
	protected List<AbstractSQL.SalaryDeduction> salaryDeductions;
	
	public AbstractSQLSalaryBuilder() {
		salaryCosts = 
			new LinkedList<AbstractSQL.SalaryCost>();
		salaryBonuses = 
			new LinkedList<AbstractSQL.SalaryBonus>();
		salaryEmbargos = 
			new LinkedList<AbstractSQL.SalaryEmbargo>();
		salaryPayments = 
			new LinkedList<AbstractSQL.SalaryPayment>();
		salaryDeductions = 
			new LinkedList<AbstractSQL.SalaryDeduction>();
	}
	
	@Override
	public void createNewSalary() {
		salary = null;
		salaryCosts.clear();
		salaryBonuses.clear();
		salaryEmbargos.clear();
		salaryPayments.clear();
		salaryDeductions.clear();
		
		salary = new AbstractSQL.Salary();
	}

	@Override
	public void setContract(Object contract) {
		SQLSalaryProxy salaryProxy = 
			( SQLSalaryProxy) contract;
		salary.setDomain(salaryProxy.getDomainId());
		salary.setContract(salaryProxy.getContractId());
	}

	@Override
	public void setCcc(String ccc) {
		salary.setCcc(ccc);
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		salary.setEnterpriseName(enterpriseName);
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salary.setEnterpriseAddress(enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salary.setEnterpriseDocument(enterpriseDocument);
	}

	@Override
	public void setRegistration(Integer registration) {
		salary.setRegistration(registration);
	}

	@Override
	public void setEmployeeName(String employeeName) {
		salary.setEmployeeName(employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salary.setEmployeeDocument(employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salary.setSocialSecurityNumber(socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		salary.setCategory(category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		salary.setQuoteGroup(quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salary.setSeniorityDate(seniorityDate);
		
	}

	@Override
	public void setType(SalaryType type) {
		salary.setType(type);
	}

	@Override
	public void setIssueDate(Date issueDate) {
		salary.setIssueDate(issueDate);
	}
	
	@Override
	public void setChargeDate(Date issueDate) {
		salary.setChargeDate(issueDate);
	}

	@Override
	public void setStartDate(Date startDate) {
		salary.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		salary.setEndDate(endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		salary.setTimeUnits(timeUnits);
	}

	@Override
	public void setItBase(Double itBase) {
		salary.setItBase(itBase);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		salary.setRawCgcBase(rawCgcBase);
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		salary.setCgcBase(cgcBase);
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		salary.setCgpBase(cgpBase);
	}

	@Override
	public void setRemuneration(Double remuneration) {
		salary.setRemuneration(remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		salary.setProExtBase(proExtBase);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		salary.setIrpfBase(irpfBase);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		salary.setHextraBase(hExtraBase);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		salary.setNonHextraBase(nonHExtraBase);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		salary.setTotalLiquid(totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		salary.setTotalPayment(totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		salary.setTotalDeduction(totalDeduction);
	}

	@Override
	public void setSocialSecurityContributions(
			Double socialSecurityContributions) {
		salary.setSocialSecurityContributions(socialSecurityContributions);
	}
	
	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		salary.setTotalEnterprise(totalEnterprise);
	}
	
	
	@Override
	public void addBonus(String concept, Double amount, String description) {
		AbstractSQL.SalaryBonus salaryBonus = 
			new AbstractSQL.SalaryBonus();
		
		salaryBonus.setAmount(amount);
		salaryBonus.setBonusConcept(concept);
		salaryBonus.setDescription(description);
		
		salaryBonuses.add(salaryBonus);
	}
	
	@Override
	public void addCost(DeductionType type, String concept, Double amount, String description) {
		AbstractSQL.SalaryCost salaryCost = 
			new AbstractSQL.SalaryCost();
		
		salaryCost.setType(type);
		salaryCost.setAmount(amount);
		salaryCost.setCostConcept(concept);
		salaryCost.setDescription(description);
		
		salaryCosts.add(salaryCost);
	}

	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		AbstractSQL.SalaryEmbargo salaryEmbargo= 
			new AbstractSQL.SalaryEmbargo();
		
		salaryEmbargo.setAmount(amount);
		salaryEmbargo.setContractEmbargo(embargo);
		salaryEmbargo.setDescription(description);
		
		salaryEmbargos.add(salaryEmbargo);
	}
	
	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, String expression, Map<String, Object> context) {
		AbstractSQL.SalaryPayment salaryPayment= 
			new AbstractSQL.SalaryPayment();
		salaryPayment.setType(type);
		salaryPayment.setAmount(amount);
		salaryPayment.setExpression(expression);
		salaryPayment.setPaymentConcept(concept);
		salaryPayment.setDescription(description);

		salaryPayments.add(salaryPayment);
	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, String expression) {
		AbstractSQL.SalaryDeduction salaryDeduction = 
			new AbstractSQL.SalaryDeduction();
		
		salaryDeduction.setType(type);
		salaryDeduction.setAmount(amount);
		salaryDeduction.setExpression(expression);
		salaryDeduction.setDescription(description);
		salaryDeduction.setDeductionConcept(concept);
		
		salaryDeductions.add(salaryDeduction);
	}
	

}
