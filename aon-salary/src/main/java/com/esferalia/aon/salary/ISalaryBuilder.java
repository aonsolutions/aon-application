package com.esferalia.aon.salary;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public interface ISalaryBuilder {
	
	public ISalary getSalary();
	
	public void createNewSalary();
	
	// ------------------------------------------------------------------------
	// Enperprise related data

	public void setContract(Object contract); 

	public void setCcc(String ccc); 
	
	public void setEnterpriseName(String enterpriseName) ;

	public void setEnterpriseAddress( String enterpriseAddress);

	public void setEnterpriseDocument(String enterpriseDocument);
	
	// ------------------------------------------------------------------------
	// Employee related data
	
	public void setRegistration(Integer registration);
	
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
	
	public void setHExtraBase(Double hExtraBase);

	public void setNonHExtraBase(Double nonHExtraBase);

	// ------------------------------------------------------------------------
	// Totals 
	
	public void setTotalLiquid(Double totalLiquid);
	
	public void setTotalPayment(Double totalPayment);
	
	public void setTotalDeduction(Double totalDeduction);
	
	public void setTotalIrpf(Double totalIrpf);

	public void setSocialSecurityContributions(Double socialSecurityContributions);
	
	public void setTotalEnterprise(Double totalEnterprise);
	
	// ------------------------------------------------------------------------
	// Paymnets, deductions, embargos ...  


	public void addBonus(String concept, Double amount, String description );

	public void addEmbargo(Integer embargo, Double amount, String description );

	public void addCost(DeductionType type, String concept, Double amount, String description );

	public void addPayment(PaymentType type, String concept, Double amount, String description , IPayment payment, Map<String, ITimedVariable<?>> context);

	public void addZeroPayment(PaymentType type, String concept, IPayment payment, Map<String, ITimedVariable<?>> context);

	public void addDeduction(DeductionType type, String concept, Double amount, String description , IDeduction deduction, Map<String, ITimedVariable<?>> context);

	public void addZeroDeduction(DeductionType type, String concept, IDeduction deduction, Map<String, ITimedVariable<?>> context);

	// ------------------------------------------------------------------------
	// Listener  
	
	public void setListener(ISalaryBuilderListener listener);
	
	
	
}
