package com.esferalia.aon.salary;

import java.util.Date;

import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public abstract class AbstractSalaryBuilder implements ISalaryBuilder {

	@Override
	public ISalary getSalary() {

		return null;
	}

	@Override
	public void createNewSalary() {
	}

	@Override
	public void setContract(Object contract) {
	}

	@Override
	public void setCcc(String ccc) {
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
	}

	@Override
	public void setRegistration(Integer registration) {
	}

	@Override
	public void setEmployeeName(String employeeName) {
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
	}

	@Override
	public void setCategory(String category) {
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		

	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		

	}

	@Override
	public void setType(SalaryType type) {
		

	}

	@Override
	public void setIssueDate(Date issueDate) {
		

	}
	
	@Override
	public void setChargeDate(Date issueDate) {
	}

	@Override
	public void setStartDate(Date startDate) {
		

	}

	@Override
	public void setEndDate(Date endDate) {
		

	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		

	}

	@Override
	public void setItBase(Double itBase) {
		

	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		

	}

	@Override
	public void setCgpBase(Double cgpBase) {
		

	}

	@Override
	public void setRemuneration(Double remuneration) {
		

	}

	@Override
	public void setProExtBase(Double proExtBase) {
		

	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		

	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		

	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		

	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		

	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		

	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		

	}

	@Override
	public void setSocialSecurityContributions(
			Double socialSecurityContributions) {
		

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		

	}

	@Override
	public void addBonus(String concept, Double amount, String description) {
		

	}

	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		

	}

	@Override
	public void addCost(DeductionType type, String concept, Double amount,
			String description) {
		

	}

	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, String expression) {
		

	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, String expression) {
		

	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		

	}

}
