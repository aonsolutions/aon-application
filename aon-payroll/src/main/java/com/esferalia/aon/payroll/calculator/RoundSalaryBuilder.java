package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class RoundSalaryBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {
	
	protected Function<Double, Double> f;
	protected ISalaryBuilder<T> salaryBuilder;
	
	public RoundSalaryBuilder(ISalaryBuilder<T> salaryBuilder, Function<Double, Double> f) {
		this.f = f;
		this.salaryBuilder = salaryBuilder;
	}
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------

	public void setContract(Object contract) {
		salaryBuilder.setContract(contract);
	}

	public void setCcc(String ccc) {
		salaryBuilder.setCcc(ccc);
	}

	public void setEnterpriseName(String enterpriseName) {
		salaryBuilder.setEnterpriseName(enterpriseName);
	}

	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryBuilder.setEnterpriseAddress(enterpriseAddress);
	}

	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryBuilder.setEnterpriseDocument(enterpriseDocument);
	}

	public void setRegistration(Integer registration) {
		salaryBuilder.setRegistration(registration);
	}

	public void setEmployeeName(String employeeName) {
		salaryBuilder.setEmployeeName(employeeName);
	}

	public void setEmployeeDocument(String employeeDocument) {
		salaryBuilder.setEmployeeDocument(employeeDocument);
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryBuilder.setSocialSecurityNumber(socialSecurityNumber);
	}

	public void setCategory(String category) {
		salaryBuilder.setCategory(category);
	}

	public void setQuoteGroup(String quoteGroup) {
		salaryBuilder.setQuoteGroup(quoteGroup);
	}

	public void setSeniorityDate(Date seniorityDate) {
		salaryBuilder.setSeniorityDate(seniorityDate);
	}

	public void setType(SalaryType type) {
		salaryBuilder.setType(type);
	}

	public void setIssueDate(Date issueDate) {
		salaryBuilder.setIssueDate(issueDate);
	}

	public void setChargeDate(Date issueDate) {
		salaryBuilder.setChargeDate(issueDate);
	}

	public void setStartDate(Date startDate) {
		salaryBuilder.setStartDate(startDate);
	}

	public void setEndDate(Date endDate) {
		salaryBuilder.setEndDate(endDate);
	}

	public void setTimeUnits(Integer timeUnits) {
		salaryBuilder.setTimeUnits(timeUnits);
	}

	public void setListener(ISalaryBuilderListener listener) {
		salaryBuilder.setListener(listener);
	}

	public void setRawCgcBase(Double rawCgcBase) {
		salaryBuilder.setRawCgcBase(f.apply(rawCgcBase));
	}

	// ------------------------------------------------------------------------
	
	private double remuneration;
	private double proExtBase;
	private double cgpBase;
	private double cgcBase;
	private double itBase;
	private double irpfBase;
	private double moneyIrpfBase;
	private double inkindIrpfBase;
	private double hExtraBase;
	private double nonHExtraBase;
	private double totalLiquid;
	private double totalPayment;
	private double totalDeduction;
	private double totalIrpf;
	private double totalSS;
	private double totalEnterprise;
	
	
	public void createNewSalary() {
		
		this.remuneration = 0;
		this.proExtBase = 0;
		this.cgcBase = 0;
		this.cgpBase = 0;
		this.itBase = 0;
		this.irpfBase = 0;
		this.moneyIrpfBase = 0;
		this.inkindIrpfBase = 0;
		this.hExtraBase = 0;
		this.nonHExtraBase = 0;
		this.totalLiquid = 0;
		this.totalPayment = 0;
		this.totalDeduction = 0;
		this.totalIrpf = 0;
		this.totalSS = 0;
		this.totalEnterprise = 0;
		
		
		salaryBuilder.createNewSalary();
	}
	
	

	public T getSalary() {
		round();
		return salaryBuilder.getSalary();
	}


	public void setCgcBase(Double cgcBase) {
		this.cgcBase = cgcBase;
	}

	public void setCgpBase(Double cgpBase) {
		this.cgpBase = cgpBase;
	}


	public void setItBase(Double itBase) {
		this.itBase = itBase;
	}

	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	public void setProExtBase(Double proExtBase) {
		this.proExtBase = proExtBase;
	}

	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}

	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
	}

	public void setInkindIrpfBase(Double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
	}

	public void setHExtraBase(Double hExtraBase) {
		this.hExtraBase = hExtraBase;
	}

	public void setNonHExtraBase(Double nonHExtraBase) {
		this.nonHExtraBase = nonHExtraBase;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	public void setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
	}

	public void setTotalSS(Double socialSecurityContributions) {
		this.totalSS = socialSecurityContributions;
	}

	public void setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise = totalEnterprise;
	}

	// ------------------------------------------------------------------------

	public void addData(String name, ITimedVariable<?> datas) {
		salaryBuilder.addData(name, datas);
	}

	public void addCost(Double amount, String description, IDeduction cost, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addCost(amount, description, cost, context);
	}

	public void addBonus(Double amount, String description, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addBonus(amount, description, bonus, context);
	}

	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context);
	}

	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
	}

	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addDeduction(amount, description, start, end, deduction, context);
	}

	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroDeduction(start, end, deduction, context);
	}

	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addEmbargo(id, amount, description, embargo, context);
	}

	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroEmbargo(id, embargo, context);
	}

	// ------------------------------------------------------------------------
	
	private void round() {
		
		double totalOther = totalDeduction - ( totalIrpf + totalSS);
		totalSS = f.apply(totalSS);
		totalIrpf = f.apply(totalIrpf);
		totalOther = f.apply(totalOther);
		totalDeduction = totalIrpf + totalSS + totalOther;
		salaryBuilder.setTotalIrpf(totalIrpf);
		salaryBuilder.setTotalSS(totalSS);
		salaryBuilder.setTotalDeduction(totalDeduction);
		
		double totalEmbargo = totalPayment - totalLiquid  - totalDeduction;
		totalEmbargo = f.apply(totalEmbargo);
		totalPayment = f.apply(totalPayment);
		totalLiquid =  totalPayment - totalDeduction - totalEmbargo;
		salaryBuilder.setTotalPayment(totalPayment);
		salaryBuilder.setTotalLiquid(totalLiquid);
		
		moneyIrpfBase = f.apply(moneyIrpfBase);
		inkindIrpfBase = f.apply(inkindIrpfBase);
		remuneration = moneyIrpfBase;
		irpfBase =  moneyIrpfBase + inkindIrpfBase;
		salaryBuilder.setIrpfBase(irpfBase);
		salaryBuilder.setMoneyIrpfBase(moneyIrpfBase);
		salaryBuilder.setInkindIrpfBase(inkindIrpfBase);
		salaryBuilder.setRemuneration(remuneration);
		

		//---
		itBase = f.apply(itBase);
		proExtBase = f.apply(proExtBase);
		salaryBuilder.setItBase(itBase);
		salaryBuilder.setProExtBase(proExtBase);

		cgcBase = f.apply(cgcBase);
		hExtraBase = f.apply(hExtraBase);
		nonHExtraBase = f.apply(nonHExtraBase);
		salaryBuilder.setHExtraBase(hExtraBase);
		salaryBuilder.setCgcBase(cgcBase);
		salaryBuilder.setNonHExtraBase(nonHExtraBase);
		
		cgpBase = Math.min(cgcBase + nonHExtraBase + hExtraBase, f.apply(cgcBase));
		salaryBuilder.setCgpBase(cgpBase);

		totalEnterprise = f.apply(totalEnterprise);
		salaryBuilder.setTotalEnterprise(totalEnterprise);
		
	}
	
}
