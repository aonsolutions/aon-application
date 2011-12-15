package com.esferalia.aon.payroll;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.enumeration.AbstractDeductionTypeVisitor;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.DeductionTypeVisitor;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SalaryBuilder implements ISalaryBuilder {

	
	protected Salary salary;
	private ISalaryBuilderListener listener;
	
	@Override
	public ISalary getSalary() {
		return this.salary;
	}

	@Override
	public void createNewSalary() {
		this.salary = new Salary();
		
		// default ones 
		salary.setTotalIrpf(0.00);
	}

	@Override
	public void setContract(Object contract) {
	}

	@Override
	public void setCcc(String ccc) {
		this.salary.setCcc(ccc);
		
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		this.salary.setEnterpriseName(enterpriseName);
		
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		this.salary.setEnterpriseAddress(enterpriseAddress);
		
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.salary.setEnterpriseDocument(enterpriseDocument);
		
	}

	@Override
	public void setRegistration(Integer registration) {
		this.salary.setRegistration(registration);
		
	}

	@Override
	public void setEmployeeName(String employeeName) {
		this.salary.setEmployeeName(employeeName);
		
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		this.salary.setEmployeeDocument(employeeDocument);
		
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.salary.setSocialSecurityNumber(socialSecurityNumber);
		
	}

	@Override
	public void setCategory(String category) {
		this.salary.setCategory(category);
		
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		this.salary.setQuoteGroup(quoteGroup);
		
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		this.salary.setSeniorityDate(seniorityDate);
	}

	@Override
	public void setType(SalaryType type) {
		this.salary.setType(type);
		
	}

	@Override
	public void setIssueDate(Date issueDate) {
		this.salary.setIssueDate(issueDate);
		
	}
	
	@Override
	public void setChargeDate(Date chargeDate) {
		this.salary.setChargeDate  ( chargeDate );
		
	}

	@Override
	public void setStartDate(Date startDate) {
		this.salary.setStartDate(startDate);
		
	}

	@Override
	public void setEndDate(Date endDate) {
		this.salary.setEndDate(endDate);
		
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		this.salary.setTimeUnits(timeUnits);
		
	}

	@Override
	public void setCgcBase(Double commonBase) {
		this.salary.setCommonBase(commonBase);
		
	}

	@Override
	public void setCgpBase(Double professionalBase) {
		this.salary.setProfessionalBase(professionalBase);
		
	}

	@Override
	public void setRemuneration(Double remuneration) {
		this.salary.setRemuneration(remuneration);
		
	}

	@Override
	public void setProExtBase(Double extraPayProration) {
		this.salary.setExtraPayProration(extraPayProration);
		
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		this.salary.setIrpfBase(irpfBase);
		
	}

	@Override
	public void setNonHExtraBase(Double overtimeBase) {
		this.salary.setNonEstructuralOvertimeBase(overtimeBase);
	}
	
	@Override
	public void setItBase(Double itBase) {
		this.salary.setIrpfBase(itBase);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		this.salary.setRawCommonBase(rawCgcBase);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		this.salary.setOvertimeBase(hExtraBase);
	}
	
	

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		this.salary.setTotalLiquid(totalLiquid);
		
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		this.salary.setTotalPayment(totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		this.salary.setTotalDeduction(totalDeduction);
	}
	
	@Override
	public void setSocialSecurityContributions(Double socialSecurityContributions){
		this.salary.setSocialSecurityContributions(socialSecurityContributions);
	}
	
	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		this.salary.setTotalEnterprise(totalEnterprise);
	}
	
	
	
	@Override
	public void addBonus(String concept, Double amount, String description) {
		SalaryBonus salaryBonus  = new SalaryBonus();
		
		salaryBonus.setSalary(salary);
		salaryBonus.setBonusConcept(concept);
		salaryBonus.setAmount(amount);
		salaryBonus.setDescription(description);
		
		this.salary.getSalaryBonus().add(salaryBonus);
	}
	
	@Override
	public void addCost(DeductionType type, String concept, Double amount, String description) {
		SalaryCost salaryCost = new SalaryCost();
		
		salaryCost.setSalary(salary);
		salaryCost.setType(type);
		salaryCost.setAmount(amount);
		salaryCost.setCostConcept(concept);
		salaryCost.setDescription(description);
		
		this.salary.getSalaryCosts().add(salaryCost);
	}
	
	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		
		SalaryEmbargo salaryEmbargo = new SalaryEmbargo() ;

		salaryEmbargo.setSalary(salary);
		// TODO setContractEmbargo(null)
		salaryEmbargo.setAmount(amount);
		salaryEmbargo.setDescription(description);
		
		ContractEmbargo contractEmbargo= 
				getContractEmbargo(embargo);
		salaryEmbargo.setContractEmbargo(contractEmbargo);

		
		this.salary.getSalaryEmbargos().add(salaryEmbargo);

	}
	
	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, String expression) {
		
		SalaryPayment payment = new SalaryPayment();
		
		payment.setSalary(salary);
		payment.setType(type);
		payment.setPaymentConcept(concept);
		payment.setAmount(amount);
		payment.setDescription(description);
		payment.setExpression(expression);

		this.salary.getSalaryPayments().add(payment);
		
	}

	@Override
	public void addDeduction(DeductionType type, String concept, final Double amount,
			String description, String expression) {
		
		SalaryDeduction deduction = new SalaryDeduction();

		deduction.setSalary(salary);
		deduction.setType(type);
		deduction.setAmount(amount);
		deduction.setDescription(description);
		deduction.setExpression(expression);
		deduction.setDeductionConcept(concept);
		
		this.salary.getSalaryDeductions().add(deduction);
		
		if ( type == DeductionType.IRPF ) {
			Double totalIrpf = salary.getTotalIrpf();
			if ( totalIrpf == null ) {  
				salary.setTotalIrpf(amount);
			}
			else {
				salary.setTotalIrpf(totalIrpf+amount);
			}
		}
		
		
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;
	}
	public ISalaryBuilderListener getListener( ) {
		return listener;
	}
	
	private ContractEmbargo getContractEmbargo(Integer id) {
		ContractEmbargo contractEmbargo = 
				new ContractEmbargo();
		contractEmbargo.setId(id);
		contractEmbargo.setContract(salary.getContract());
		return contractEmbargo;
	}
}