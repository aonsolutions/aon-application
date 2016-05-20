package com.esferalia.aon.payroll;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class SalaryBuilder implements ISalaryBuilder<Salary> {

	protected Salary salary;
	private ISalaryBuilderListener listener;

	@Override
	public Salary getSalary() {
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
		this.salary.setChargeDate(chargeDate);

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
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		this.salary.setMoneyIrpfBase(moneyIrpfBase);
	}

	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		this.salary.setInkindIrpfBase(inkindIrpfBase);
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
	public void setTotalIrpf(Double totalIrpf) {
		this.salary.setTotalIrpf(totalIrpf);
	}

	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		this.salary.setSocialSecurityContributions(socialSecurityContributions);
	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		this.salary.setTotalEnterprise(totalEnterprise);
	}

	@Override
	public void addData(String name, ITimedVariable<?> data) {
		Object value = data.getValue(data.getPeriod());
		SalaryData salaryData = new SalaryData();
		salaryData.setName(name);
		salaryData.setStartDate(data.getPeriod().getStart());
		salaryData.setEndDate(data.getPeriod().getEnd());
		salaryData.setExpression(String.valueOf(value));
		this.salary.getSalaryDatas().add(salaryData);
	}

	@Override
	public void addBonus(Double amount, String description, IBonus bonus,
			Map<String, ITimedVariable<?>> context) {
		SalaryBonus salaryBonus = new SalaryBonus();

		salaryBonus.setSalary(salary);
		salaryBonus.setAmount(amount);
		salaryBonus.setDescription(description);
		salaryBonus.setBonusConcept(bonus.getName());

		this.salary.getSalaryBonus().add(salaryBonus);
	}

	@Override
	public void addCost(Double amount, String description, IDeduction cost,
			Map<String, ITimedVariable<?>> context) {
		SalaryCost salaryCost = new SalaryCost();

		salaryCost.setSalary(salary);
		salaryCost.setType(cost.getType());
		salaryCost.setAmount(amount);
		salaryCost.setCostConcept(cost.getName());
		salaryCost.setDescription(description);

		this.salary.getSalaryCosts().add(salaryCost);
	}

	@Override
	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context) {

		SalaryEmbargo salaryEmbargo = new SalaryEmbargo();

		salaryEmbargo.setSalary(salary);
		// TODO setContractEmbargo(null)
		salaryEmbargo.setAmount(amount);
		salaryEmbargo.setDescription(description);

		ContractEmbargo contractEmbargo = getContractEmbargo(id);
		salaryEmbargo.setContractEmbargo(contractEmbargo);

		this.salary.getSalaryEmbargos().add(salaryEmbargo);

	}

	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {

		SalaryPayment sPayment = new SalaryPayment();

		sPayment.setSalary(salary);
		sPayment.setQuote(quote);
		sPayment.setAmount(amount);
		sPayment.setType(payment.getType());
		sPayment.setPaymentConcept(payment.getName());
		sPayment.setDescription(description);
		sPayment.setExpression(payment.getExpression());

		this.salary.getSalaryPayments().add(sPayment);

	}

	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate,
			Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		// TODO: No payment, so we're not going to save it. But at upcoming
		// versions
		// we store taxes and quotes, so we'll have much more info.
	}

	@Override
	public void addDeduction(final Double amount, String description,
			Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {

		// TODO: start & end dates ????
		
		SalaryDeduction salaryDeduction = new SalaryDeduction();

		DeductionType type = deduction.getType();

		salaryDeduction.setSalary(salary);
		salaryDeduction.setType(type);
		salaryDeduction.setAmount(amount);
		salaryDeduction.setDescription(description);
		salaryDeduction.setDeductionConcept(deduction.getName());
		salaryDeduction.setExpression(deduction.getExpression());
		
		this.salary.getSalaryDeductions().add(salaryDeduction);

		if (type == DeductionType.IRPF) {
			Double totalIrpf = salary.getTotalIrpf();
			if (totalIrpf == null) {
				salary.setTotalIrpf(amount);
			} else {
				salary.setTotalIrpf(totalIrpf + amount);
			}
		}

	}

	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;
	}

	public ISalaryBuilderListener getListener() {
		return listener;
	}

	// ------------------------------------------------------------------------

	private ContractEmbargo getContractEmbargo(Integer id) {
		ContractEmbargo contractEmbargo = new ContractEmbargo();
		contractEmbargo.setId(id);
		contractEmbargo.setContract(salary.getContract());
		return contractEmbargo;
	}

}