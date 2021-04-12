package com.esferalia.aon.in.payroll.pdf.maker.settlement.beans;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;

public class Settlement {

	private String enterpriseName;
	private String enterpriseNIF;
	private String enterpriseAddress;

	private String employeeName;
	private String employeeNIF;
	private String employeeCategory;
	private Date   employeeAntiquity;

	private Date	endDate;
	private String	endCause;
	private Boolean	existRepresentative;

	private Map<Integer, ArrayList<PDFPayment>>   accruals;
	private Map<Integer, ArrayList<PDFDeduction>> deductions;

	private Double accrualTotal;
	private Double deductionTotal;
	private Double total;

	private Date   date;
	private String location;
	private InputStream logo;

	private Settlement() {
	}

	private static Settlement instance() {
		return new Settlement();
	}

	public Optional<String> getEnterpriseName() {
		return Optional.ofNullable(enterpriseName);
	}

	public Settlement setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public Optional<String> getEnterpriseNif() {
		return Optional.ofNullable(enterpriseNIF);
	}

	public Settlement setEnterpriseNIF(String enterpriseNIF) {
		this.enterpriseNIF = enterpriseNIF;
		return this;
	}

	public Optional<String> getEnterpriseAddress() {
		return Optional.ofNullable(enterpriseAddress);
	}

	public Settlement setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}

	public Optional<String> getEmployeeName() {
		return Optional.ofNullable(employeeName);
	}

	public Settlement setEmployee_name(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}

	public Optional<String> employeeNIF() {
		return Optional.ofNullable(employeeNIF);
	}

	public Settlement setEmployeeNIF(String employeeNIF) {
		this.employeeNIF = employeeNIF;
		return this;
	}

	public Optional<String> employeeCategory() {
		return Optional.ofNullable(employeeCategory);
	}

	public Settlement setEmployeeCategory(String employeeCategory) {
		this.employeeCategory = employeeCategory;
		return this;
	}

	public Optional<Date> getEmployeeAntiquity() {
		return Optional.ofNullable(employeeAntiquity);
	}

	public Settlement setEmployeeAntiquity(Date employeeAntiquity) {
		this.employeeAntiquity = employeeAntiquity;
		return this;
	}

	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}

	public Settlement setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Optional<String> endCause() {
		return Optional.ofNullable(endCause);
	}

	public Settlement setEndCause(String endCause) {
		this.endCause = endCause;
		return this;
	}

	public Optional<Boolean> existRepresentative() {
		return Optional.ofNullable(existRepresentative);
	}

	public Settlement setExistRepresentative(boolean existRepresentative) {
		this.existRepresentative = existRepresentative;
		return this;
	}

	public Map<Integer, ArrayList<PDFPayment>> getAccruals() {
		return accruals;
	}

	public Settlement setAccruals(Map<Integer, ArrayList<PDFPayment>> accruals) {
		this.accruals = accruals;
		return this;
	}

	public Map<Integer, ArrayList<PDFDeduction>> getDeductions() {
		return deductions;
	}

	public Settlement setDeductions(Map<Integer, ArrayList<PDFDeduction>> deductions) {
		this.deductions = deductions;
		return this;
	}

	public Optional<Double> getAccrualTotal() {
		return Optional.ofNullable(accrualTotal);
	}

	public Settlement setAccrualTotal(double accrual_total) {
		this.accrualTotal = accrual_total;
		return this;
	}

	public Optional<Double> deductionTotal() {
		return Optional.ofNullable(deductionTotal);
	}

	public Settlement setDeductionTotal(double deduction_total) {
		this.deductionTotal = deduction_total;
		return this;
	}

	public Optional<Double> total() {
		return Optional.ofNullable(total);
	}

	public Settlement setTotal(double total) {
		this.total = total;
		return this;
	}

	public Optional<Date> date() {
		return Optional.ofNullable(date);
	}

	public Settlement setDate(Date date) {
		this.date = date;
		return this;
	}

	public Optional<String> location() {
		return Optional.ofNullable(location);
	}

	public Settlement setLocation(String location) {
		this.location = location;
		return this;
	}

	public InputStream getLogo() {
		return logo;
	}

	public Settlement setLogo(InputStream logo) {
		this.logo = logo;
		return this;
	}



	public static class SettlementBuilder {

		private String enterpriseName;
		private String enterpriseNIF;
		private String enterpriseAddress;

		private String employeeName;
		private String employeeNIF;
		private String employeeCategory;
		private Date   employeeAntiquity;

		private Date	endDate;
		private String	endCause;
		private boolean	existRepresentative;

		private Map<Integer, ArrayList<PDFPayment>>   accruals;
		private Map<Integer, ArrayList<PDFDeduction>> deductions;

		private double accrualTotal;
		private double deductionTotal;
		private double total;
		private Date   date;
		private String location;
		private InputStream logo;
		

		public SettlementBuilder() {
			this.accruals = new HashMap<>();
			this.deductions = new HashMap<>();
		}

		public SettlementBuilder setEnterpriseName(String enterpriseName) {
			this.enterpriseName = enterpriseName;
			return this;
		}

		public SettlementBuilder setEnterpriseNIF(String enterpriseNIF) {
			this.enterpriseNIF = enterpriseNIF;
			return this;
		}

		public SettlementBuilder setEnterpriseAddress(String enterpriseAddress) {
			this.enterpriseAddress = enterpriseAddress;
			return this;
		}

		public SettlementBuilder setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
			return this;
		}

		public SettlementBuilder setEmployeeNIF(String employeeNIF) {
			this.employeeNIF = employeeNIF;
			return this;
		}

		public SettlementBuilder setEmployeeCategory(String employeeCategory) {
			this.employeeCategory = employeeCategory;
			return this;
		}

		public SettlementBuilder setEmployeeAntiquity(Date employeeAntiquity) {
			this.employeeAntiquity = employeeAntiquity;
			return this;
		}

		public SettlementBuilder setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public SettlementBuilder setEndCause(String endCause) {
			this.endCause = endCause;
			return this;
		}

		public SettlementBuilder setExistRepresentative(boolean existRepresentative) {
			this.existRepresentative = existRepresentative;
			return this;
		}

		public SettlementBuilder setPayments(Map<Integer, ArrayList<PDFPayment>> accruals) {
			this.accruals = accruals;
			return this;
		}

		public SettlementBuilder setDeductions(Map<Integer, ArrayList<PDFDeduction>> deductions) {
			this.deductions = deductions;
			return this;
		}

		public SettlementBuilder setAccrualTotal(double accrualTotal) {
			this.accrualTotal = accrualTotal;
			return this;
		}

		public SettlementBuilder setDeductionTotal(double deductionTotal) {
			this.deductionTotal = deductionTotal;
			return this;
		}

		public SettlementBuilder setTotal(double total) {
			this.total = total;
			return this;
		}

		public SettlementBuilder setDate(Date date) {
			this.date = date;
			return this;
		}

		public SettlementBuilder setLocation(String location) {
			this.location = location;
			return this;
		}
		
		public SettlementBuilder setLogo(InputStream logo) {
			this.logo = logo;
			return this;
		}

		public Settlement build() {
			return instance().setEnterpriseName(this.enterpriseName).setEnterpriseNIF(this.enterpriseNIF)
					.setEnterpriseAddress(this.enterpriseAddress).setEmployee_name(this.employeeName)
					.setEmployeeNIF(this.employeeNIF).setEmployeeCategory(this.employeeCategory)
					.setEmployeeAntiquity(this.employeeAntiquity).setEndDate(this.endDate).setEndCause(this.endCause)
					.setExistRepresentative(this.existRepresentative).setAccruals(this.accruals)
					.setDeductions(this.deductions).setAccrualTotal(this.accrualTotal)
					.setDeductionTotal(this.deductionTotal).setLocation(this.location).setDate(this.date)
					.setTotal(this.total).setLogo(this.logo);
		};

	}
}
