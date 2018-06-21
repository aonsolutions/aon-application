package com.esferalia.aon.payroll.calculator.sql;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public abstract class AbstractSQLSalaryBuilder<T extends ISalary> implements ISalaryBuilder<T> {

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
	public void setEnterpriseCity(String enterpriseCity) {
		//TODO: salary.setEnterpriseCity(enterpriseCity);
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
	public void setEmployeeCity(String employeeCity) {
		//TODO: salary.setEmployeeCity(employeeCity);
	}

	@Override
	public void setEmployeeAddress(String employeeAddress) {
		//TODO: salary.setEmployeeAddress(employeeAddress);
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
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		salary.setInkindIrpfBase(inkindIrpfBase);
	}
	
	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		salary.setMoneyIrpfBase(moneyIrpfBase);
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
	public void setTotalIrpf(Double totalIrpf) {
		salary.setTotalIrpf(totalIrpf);
	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		salary.setSocialSecurityContributions(socialSecurityContributions);
	}
	
	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		salary.setTotalEnterprise(totalEnterprise);
	}
	@Override
	public void addData(String name, ITimedVariable<?> data) {
	}
	
	@Override
	public void addBonus(Double amount, String description, IBonus bonus,
			Map<String, ITimedVariable<?>> context) {
		AbstractSQL.SalaryBonus salaryBonus = 
				new AbstractSQL.SalaryBonus();
			
			salaryBonus.setAmount(amount);
			salaryBonus.setDescription(description);
			salaryBonus.setBonusConcept(bonus.getName());
			
			salaryBonuses.add(salaryBonus);
	}
	
	@Override
	public void addCost(Double amount, String description,
			IDeduction cost, Map<String, ITimedVariable<?>> context) {
		AbstractSQL.SalaryCost salaryCost = 
			new AbstractSQL.SalaryCost();
		
		salaryCost.setType(cost.getType());
		salaryCost.setAmount(amount);
		salaryCost.setCostConcept(cost.getName());
		salaryCost.setDescription(description);
		
		salaryCosts.add(salaryCost);
	}
	
	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		AbstractSQL.SalaryEmbargo salaryEmbargo= 
			new AbstractSQL.SalaryEmbargo();
		
		salaryEmbargo.setAmount(amount);
		salaryEmbargo.setContractEmbargo(id);
		salaryEmbargo.setDescription(description);
		
		salaryEmbargos.add(salaryEmbargo);
	}
	
	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date startDate, Date endDate,  IPayment payment, Map<String, ITimedVariable<?>> context) {
		// TODO : save quote and tax ???
		AbstractSQL.SalaryPayment salaryPayment= 
			new AbstractSQL.SalaryPayment();
		salaryPayment.setType(payment.getType());
		salaryPayment.setAmount(amount);
		salaryPayment.setExpression(payment.getExpression());
		salaryPayment.setPaymentConcept(payment.getName());
		salaryPayment.setDescription(description);

		salaryPayments.add(salaryPayment);
	}
	
	@Override
	public void addZeroPayment(Double quote, Double tax,Date startDate, Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
		//TODO: No payment, so we're not going to save it. But at upcoming versions
		// we store taxes and quotes, so we'll have much more info.
	}

	@Override
	public void addDeduction(Double amount,
			String description, Date start, Date end,IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		AbstractSQL.SalaryDeduction salaryDeduction = 
			new AbstractSQL.SalaryDeduction();
		
		// TODO: start & end dates ?
		
		salaryDeduction.setType(deduction.getType());
		salaryDeduction.setAmount(amount);
		salaryDeduction.setDescription(description);
		salaryDeduction.setDeductionConcept(deduction.getName());
		salaryDeduction.setExpression(deduction.getExpression());
		
		salaryDeductions.add(salaryDeduction);
	}
	
	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub
		
	}
}
