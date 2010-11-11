package com.code.aon.employee;

import java.util.Date;

public interface ISalary {

	public Contract getContract();
	
	public Date getStartDate();

	public Date getEndDate();

	public String getAddress();

	public String getEmployee();

	public String getCategory();

	public Integer getRegistration();
	
	public Integer getTotalDaysHours();
	
	public Double getTotalPayment();

	public Double getTotalDeduction();

	public Double getTotalLiquid();

	public Date getBroadcastDate();

	public Double getRemuneration();

	public Double getExtraPayProration();

	public Double getTotal();

	public Double getCommonBase();

	public Double getProfessionalBase();

	public Double getOvertimeBase();

	public Double getIrpfBase();
	
	public ISalary getSalary();
	
	public String getStartDateDay();
	
	public String getStartDateMonth();

	public String getStartDateYear();
	
	public String getEndDateDay();

	public String getEndDateMonth();

	public String getEndDateYear();
	
	public Payments getPayments();

	public Deductions getDeductions();

}
