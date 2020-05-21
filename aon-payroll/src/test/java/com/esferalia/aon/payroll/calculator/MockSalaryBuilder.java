package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class MockSalaryBuilder<T extends ISalary> implements ISalaryBuilder<T> {

	private Double totalPayment;
	
	@Override
	public T getSalary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createNewSalary() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContract(Object contract) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCcc(String ccc) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setEnterpriseName(String enterpriseName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRegistration(Integer registration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmployeeName(String employeeName) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setEmployeeCity(String employeeCity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setEmployeeAddress(String employeeAddress) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setEmployeeDocument(String employeeDocument) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCategory(String category) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(SalaryType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIssueDate(Date issueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChargeDate(Date issueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStartDate(Date startDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEndDate(Date endDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setItBase(Double itBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCgpBase(Double cgpBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemuneration(Double remuneration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProExtBase(Double proExtBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		// TODO Auto-generated method stub

	}

	public Double getTotalPayment() {
		return totalPayment;
	}
	
	@Override
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}
	
	@Override
	public void setTotalDeduction(Double totalDeduction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCost(Double amount, String description, 
			Date start, Date end, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addData(String name, ITimedVariable<?> datas) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addBonus(Double amount, String description, Date startDate,
			Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date start, Date end, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate,
			Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDeduction(Double amount, String description,
			Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub

	}

}
