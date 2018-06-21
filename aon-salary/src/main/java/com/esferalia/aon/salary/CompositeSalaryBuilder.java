package com.esferalia.aon.salary;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class CompositeSalaryBuilder<E extends ISalary,T extends ISalaryBuilder<E>> implements
		ISalaryBuilder<E> {

	private T builders[];

	public CompositeSalaryBuilder(T... builders) {
		this.builders = builders;
	}

	@Override
	public E getSalary() {
		for (ISalaryBuilder<E> builder : builders) {
			E salary = builder.getSalary();
			if (salary != null)
				return salary;
		}
		return null;
	}

	@Override
	public void createNewSalary() {
		for (ISalaryBuilder<E> builder : builders)
			builder.createNewSalary();
	}

	@Override
	public void setContract(Object contract) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setContract(contract);
	}

	@Override
	public void setCcc(String ccc) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setCcc(ccc);
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEnterpriseName(enterpriseName);
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEnterpriseAddress(enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEnterpriseDocument(enterpriseDocument);
	}

	@Override
	public void setRegistration(Integer registration) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setRegistration(registration);
	}

	@Override
	public void setEmployeeName(String employeeName) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEmployeeName(employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEmployeeDocument(employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setSocialSecurityNumber(socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setCategory(category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setQuoteGroup(quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setSeniorityDate(seniorityDate);
	}

	@Override
	public void setType(SalaryType type) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setType(type);
	}

	@Override
	public void setIssueDate(Date issueDate) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setIssueDate(issueDate);
	}

	@Override
	public void setChargeDate(Date issueDate) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setChargeDate(issueDate);
	}

	@Override
	public void setStartDate(Date startDate) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setEndDate(endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTimeUnits(timeUnits);
	}

	@Override
	public void setItBase(Double itBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setItBase(itBase);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setRawCgcBase(rawCgcBase);
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setCgcBase(cgcBase);
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setCgpBase(cgpBase);
	}

	@Override
	public void setRemuneration(Double remuneration) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setRemuneration(remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setProExtBase(proExtBase);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setIrpfBase(irpfBase);
	}
	
	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setInkindIrpfBase(inkindIrpfBase);
	}
	
	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setMoneyIrpfBase(moneyIrpfBase);
	}
	
	@Override
	public void setHExtraBase(Double hExtraBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setHExtraBase(hExtraBase);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setNonHExtraBase(nonHExtraBase);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalLiquid(totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalPayment(totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalDeduction(totalDeduction);
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalIrpf(totalIrpf);
	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalSS(socialSecurityContributions);

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setTotalEnterprise(totalEnterprise);
	}
	
	@Override
	public void addData(String name, ITimedVariable<?> datas) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addData(name, datas);
	}

	@Override
	public void addBonus(Double amount, String description, IBonus bonus,
			Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addBonus(amount, description, bonus, context);
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addEmbargo(id, amount, description, embargo, context);
	}

	@Override
	public void addCost(Double amount, String description, IDeduction cost,
			Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addCost(amount, description, cost, context);
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addPayment(amount, quote, tax, description, startDate,
					endDate, payment, context);
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate,
			Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
	}

	@Override
	public void addDeduction(Double amount, String description,
			Date start, Date end,IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addDeduction(amount, description, start, end, deduction, context);
	}

	@Override
	public void addZeroDeduction(Date start, Date end,IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addZeroDeduction(start, end,deduction, context);
	}
	
	
	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		for (ISalaryBuilder<E> builder : builders)
			builder.addZeroEmbargo(id, embargo, context);
		
	}
	@Override
	public void setListener(ISalaryBuilderListener listener) {
		for (ISalaryBuilder<E> builder : builders)
			builder.setListener(listener);
	}

	// -------------------------------------------------------------- Protected

	protected T[] getBuilders() {
		return builders;
	}

}
