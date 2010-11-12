package com.code.aon.employee;

import java.util.Date;
import java.util.Locale;

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
	
	public String getStartDateDay(Locale locale);
	
	public String getStartDateMonth(Locale locale);

	public String getStartDateYear(Locale locale);
	
	public String getEndDateDay(Locale locale);

	public String getEndDateMonth(Locale locale);

	public String getEndDateYear(Locale locale);
	
	public Payments getPayments();

	public Deductions getDeductions();

}
