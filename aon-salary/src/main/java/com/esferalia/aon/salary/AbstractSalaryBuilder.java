package com.esferalia.aon.salary;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public abstract class AbstractSalaryBuilder<T extends ISalary> implements ISalaryBuilder<T> {

	@Override
	public T getSalary() {

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
	public void setEnterpriseCity(String enterpriseCity) {
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
	public void setEmployeeCity(String employeeCity) {
	}
	
	@Override
	public void setEmployeeName(String employeeName) {
	}

	@Override
	public void setEmployeeAddress(String employeeAddress) {
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
	}

	@Override
	public void setRegime(String regime) {
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
	public void setMoneyIrpfBase(Double inkindIrpfBase) {
	}
	
	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
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
	public void setTotalIrpf(Double totalIrpf) {
	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {

	}
	
	@Override
	public void addData(String name, ITimedVariable<?> datas) {
	}

	@Override
	public void addBonus(Double amount, String description, Date startDate,
			Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
	}
	
	
	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context) {
	}

	@Override
	public void addCost(Double amount, String description, 
			Date startDate, Date endDate, IDeduction cost, Map<String, ITimedVariable<?>> context) {

	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
	}
	
	
	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate,
			Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
	}

	@Override
	public void addDeduction(Double amount, String description,
			Date start, Date end,IDeduction deduction, Map<String, ITimedVariable<?>> context) {

	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
	}
	
	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
	}
	
	@Override
	public void setListener(ISalaryBuilderListener listener) {

	}

}
