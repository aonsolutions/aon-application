package com.esferalia.aon.salary;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public interface ISalaryBuilder<T extends ISalary> {

	public T getSalary();

	public void createNewSalary();

	// ------------------------------------------------------------------------
	// Enperprise related data

	public void setContract(Object contract);

	public void setCcc(String ccc);

	public void setEnterpriseCity(String enterpriseCity);

	public void setEnterpriseName(String enterpriseName);

	public void setEnterpriseAddress(String enterpriseAddress);

	public void setEnterpriseDocument(String enterpriseDocument);

	// ------------------------------------------------------------------------
	// Employee related data

	public void setRegistration(Integer registration);

	public void setEmployeeCity(String employeeCity);

	public void setEmployeeAddress(String employeeAddress);

	public void setEmployeeName(String employeeName);

	public void setEmployeeDocument(String employeeDocument);

	public void setSocialSecurityNumber(String socialSecurityNumber);

	public void setCategory(String category);

	public void setQuoteGroup(String quoteGroup);

	public void setSeniorityDate(Date seniorityDate);

	// ------------------------------------------------------------------------
	// Bill related data

	public void setType(SalaryType type);

	public void setIssueDate(Date issueDate);

	public void setChargeDate(Date issueDate);

	public void setStartDate(Date startDate);

	public void setEndDate(Date endDate);

	public void setTimeUnits(Integer timeUnits);

	// ------------------------------------------------------------------------
	// Bases
	public void setItBase(Double itBase);

	public void setRawCgcBase(Double rawCgcBase);

	public void setCgcBase(Double cgcBase);

	public void setCgpBase(Double cgpBase);

	public void setRemuneration(Double remuneration);

	public void setProExtBase(Double proExtBase);

	public void setIrpfBase(Double irpfBase);

	public void setMoneyIrpfBase(Double moneyIrpfBase);

	public void setInkindIrpfBase(Double inkindIrpfBase);

	public void setHExtraBase(Double hExtraBase);

	public void setNonHExtraBase(Double nonHExtraBase);

	// ------------------------------------------------------------------------
	// Totals


	public void setTotalSS(Double totalSS);

	public void setTotalIrpf(Double totalIrpf);

	//public void setTotalOther(Double totalSS);

	//public void setTotalEmbargo(Double totalEmbargo);

	public void setTotalDeduction(Double totalDeduction);

	public void setTotalLiquid(Double totalLiquid);

	public void setTotalPayment(Double totalPayment);

	public void setTotalEnterprise(Double totalEnterprise);

	// ------------------------------------------------------------------------
	// Paymnets, deductions, embargos ...

	public void addData(String name, ITimedVariable<?> datas);

	public void addCost(Double amount, String description, IDeduction cost,
			Map<String, ITimedVariable<?>> context);

	public void addBonus(Double amount, String description, IBonus bonus,
			Map<String, ITimedVariable<?>> context);

	public void addPayment(Double amount, Double quote, Double tax,
			String description, Date start, Date end, IPayment payment,
			Map<String, ITimedVariable<?>> context);

	public void addZeroPayment(Double quote, Double tax, 
			Date startDate, Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context);

	public void addDeduction(Double amount, String description,
			Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context);

	public void addZeroDeduction(Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context);

	public void addEmbargo(Integer id, Double amount, String description,
			IDeduction embargo, Map<String, ITimedVariable<?>> context);

	public void addZeroEmbargo(Integer id, IDeduction embargo,
			Map<String, ITimedVariable<?>> context);

	// ------------------------------------------------------------------------
	// Listener

	public void setListener(ISalaryBuilderListener listener);

}
