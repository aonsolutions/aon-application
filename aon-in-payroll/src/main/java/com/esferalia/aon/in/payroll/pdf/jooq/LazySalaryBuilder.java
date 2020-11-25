package com.esferalia.aon.in.payroll.pdf.jooq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class LazySalaryBuilder<S extends ISalaryBuilder<T>, T extends ISalary> implements ISalaryBuilder<T> {
	
	private static interface Call {
		void call();
	}
	
	
	S salaryBuilder;

	List<Call> calls ;
	
	
	public LazySalaryBuilder(S salaryBuilder) {
		calls = new ArrayList<Call>();
		this.salaryBuilder = salaryBuilder;
	}
	
	public void call() {
		calls.forEach(c -> c.call());
	}
	
	public S getSalaryBuilder() {
		return salaryBuilder;
	}
	

	@Override
	public T getSalary() {
		calls.add( () -> salaryBuilder.getSalary() );
		return null;
	}

	@Override
	public void createNewSalary() {
		calls.clear();
		calls.add( () -> salaryBuilder.createNewSalary() );
	}

	@Override
	public void setContract(Object contract) {
		calls.add( () -> salaryBuilder.setContract(contract) );
	}

	@Override
	public void setCcc(String ccc) {
		calls.add( () -> salaryBuilder.setCcc(ccc));
	}
	
	@Override
	public void setRegime(String regime) {
		calls.add( () -> salaryBuilder.setRegime(regime));
	}

	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		calls.add( () -> salaryBuilder.setEnterpriseCity(enterpriseCity) );
	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		calls.add( () -> salaryBuilder.setEnterpriseName(enterpriseName) );
	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		calls.add( () -> salaryBuilder.setEnterpriseAddress(enterpriseAddress) );
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		calls.add( () -> salaryBuilder.setEnterpriseDocument(enterpriseDocument) );
	}

	@Override
	public void setRegistration(Integer registration) {
		calls.add( () -> salaryBuilder.setRegistration(registration) );
	}

	@Override
	public void setEmployeeCity(String employeeCity) {
		calls.add( () -> salaryBuilder.setEmployeeCity(employeeCity) );
	}

	@Override
	public void setEmployeeAddress(String employeeAddress) {
		calls.add( () -> salaryBuilder.setEmployeeAddress(employeeAddress) );
	}

	@Override
	public void setEmployeeName(String employeeName) {
		calls.add( () -> salaryBuilder.setEmployeeName(employeeName) );
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		calls.add( () -> salaryBuilder.setEmployeeDocument(employeeDocument) );
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		calls.add( () -> salaryBuilder.setSocialSecurityNumber(socialSecurityNumber) );
	}

	@Override
	public void setCategory(String category) {
		calls.add( () -> salaryBuilder.setCategory(category) );
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		calls.add( () -> salaryBuilder.setQuoteGroup(quoteGroup) );
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		calls.add( () -> salaryBuilder.setSeniorityDate(seniorityDate) );
	}

	@Override
	public void setType(SalaryType type) {
		calls.add( () -> salaryBuilder.setType(type) );
	}

	@Override
	public void setIssueDate(Date issueDate) {
		calls.add( () -> salaryBuilder.setIssueDate(issueDate) );
	}

	@Override
	public void setChargeDate(Date issueDate) {
		calls.add( () -> salaryBuilder.setChargeDate(issueDate) );
	}

	@Override
	public void setStartDate(Date startDate) {
		calls.add( () -> salaryBuilder.setStartDate(startDate) );
	}

	@Override
	public void setEndDate(Date endDate) {
		calls.add( () -> salaryBuilder.setEndDate(endDate) );
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		calls.add( () -> salaryBuilder.setTimeUnits(timeUnits) );
	}

	@Override
	public void setItBase(Double itBase) {
		calls.add( () -> salaryBuilder.setItBase(itBase) );
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		calls.add( () -> salaryBuilder.setRawCgcBase(rawCgcBase) );
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		calls.add( () -> salaryBuilder.setCgcBase(cgcBase) );
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		calls.add( () -> salaryBuilder.setCgpBase(cgpBase) );
	}

	@Override
	public void setRemuneration(Double remuneration) {
		calls.add( () -> salaryBuilder.setRemuneration(remuneration) );
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		calls.add( () -> salaryBuilder.setProExtBase(proExtBase) );
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		calls.add( () -> salaryBuilder.setIrpfBase(irpfBase) );
	}

	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		calls.add( () -> salaryBuilder.setMoneyIrpfBase(moneyIrpfBase) );
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		calls.add( () -> salaryBuilder.setInkindIrpfBase(inkindIrpfBase) );
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		calls.add( () -> salaryBuilder.setHExtraBase(hExtraBase) );
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		calls.add( () -> salaryBuilder.setNonHExtraBase(nonHExtraBase) );
	}

	@Override
	public void setTotalSS(Double totalSS) {
		calls.add( () -> salaryBuilder.setTotalSS(totalSS) );
	}

	@Override
	public void setTotalIrpf(Double totalIrpf) {
		calls.add( () -> salaryBuilder.setTotalIrpf(totalIrpf) );
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		calls.add( () -> salaryBuilder.setTotalDeduction(totalDeduction) );
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		calls.add( () -> salaryBuilder.setTotalLiquid(totalLiquid) );
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		calls.add( () -> salaryBuilder.setTotalPayment(totalPayment) );
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		calls.add( () -> salaryBuilder.setTotalEnterprise(totalEnterprise) );
	}

	@Override
	public void addData(String name, ITimedVariable<?> datas) {
		calls.add( () -> salaryBuilder.addData(name, datas) );
	}

	@Override
	public void addCost(Double amount, String description, Date startDate, Date endDate, IDeduction cost,
			Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addCost(amount, description, startDate, endDate, cost, context) );
	}

	@Override
	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addBonus(amount, description, null, null, bonus, context) );
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context) );
	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context) );
	}

	@Override
	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addDeduction(amount, description, start, end, deduction, context) );
	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addZeroDeduction(start, end, deduction, context) );
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addEmbargo(id, amount, description, embargo, context) );
	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		calls.add( () -> salaryBuilder.addZeroEmbargo(id, embargo, context) );
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		calls.add( () -> salaryBuilder.setListener(listener) );
	}

}
