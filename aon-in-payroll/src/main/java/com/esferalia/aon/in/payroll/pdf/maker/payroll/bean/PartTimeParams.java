package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class PartTimeParams {
	public static class PartTimeParamsException extends RuntimeException {
		private static final long serialVersionUID = -4145323437748002417L;
		public PartTimeParamsException() {
			super();
		}
		public PartTimeParamsException(String msg) {
			super(msg);
		}
		
	}
	public static class PartTimeEntry {
		private Double ordinary;
		private Double complementary;
		private boolean holiday;
		private boolean notWorkingDay;
		
		public Double getOrdinary() {
			return ordinary;
		}
		public PartTimeEntry setOrdinary(Double ordinary) {
			this.ordinary = ordinary;
			return this;
		}
		public Double getComplementary() {
			return complementary;
		}
		public PartTimeEntry setComplementary(Double complementary) {
			this.complementary = complementary;
			return this;
		}
		public boolean isHoliday() {
			return holiday;
		}
		public PartTimeEntry setHoliday(boolean holiday) {
			this.holiday = holiday;
			return this;
		}
		public boolean isNotWorkingDay() {
			return notWorkingDay;
		}
		public PartTimeEntry setNotWorkingDay(boolean notWorkingDay) {
			this.notWorkingDay = notWorkingDay;
			return this;
		}
		
		
	}
	
	//-----ENTERPRISE-----
	private String enterpriseName;
	private String enterpriseCCC;
	private String enterpriseDocument;
	//--------------------
	//-----EMPLOYEE-------
	private String employeeName;
	private Double contractHours;
	private Date payrollDate;
	private Date paymentDate;
	//--------------------
	//-----DATA-----------
	private Map<Integer, PartTimeEntry> entries; 
	//--------------------
	//-----ASSETS---------
	private byte[] enterpriseSignature;
	//--------------------
	
	//-----OTHERS--------
	private int maxEntriesNumber;
	//-------------------
	
	
	//-----CONSTRUCTOR------------
	public PartTimeParams(Date payrollDate) {
		if (payrollDate == null) {
			throw new PartTimeParamsException("Payroll date cannot be null");
		}
		this.payrollDate = payrollDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(payrollDate);
		this.maxEntriesNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.entries = new LinkedHashMap<>(this.maxEntriesNumber);
		for (int day = 1; day <= this.maxEntriesNumber; day++) {
			entries.put(day, new PartTimeEntry());
		}
	}
	//----------------------------
	
	//-----GETTERS/SETTERS---------
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public PartTimeParams setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}
	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}
	public PartTimeParams setEnterpriseCCC(String enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
		return this;
	}
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public PartTimeParams setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public PartTimeParams setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}
	public Double getContractHours() {
		return contractHours;
	}
	public PartTimeParams setContractHours(Double contractHours) {
		this.contractHours = contractHours;
		return this;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public PartTimeParams setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
		return this;
	}
	public Date getPayrollDate() {
		return payrollDate;
	}
	public PartTimeParams addEntry(int day, PartTimeEntry entry) {
		this.entries.put(day, entry);
		return this;
	}
	public PartTimeEntry getEntry(int day) {
		return this.entries.get(day);
	}
	public PartTimeEntry clearEntry(int day) {
		return this.entries.get(day).setComplementary(null).setOrdinary(null);
	}
	public Map<Integer, PartTimeEntry> getEntries() {
		return entries;
	}
	public PartTimeParams setEntries(Map<Integer, PartTimeEntry> entries) {
		this.entries = entries;
		return this;
	}
	public byte[] getEnterpriseSignature() {
		return enterpriseSignature;
	}
	public PartTimeParams setEnterpriseSignature(byte[] enterpriseSignature) {
		this.enterpriseSignature = enterpriseSignature;
		return this;
	}
	//-----------------------------
	
	
	
}
