package com.esferalia.aon.in.payroll.pdf.maker.payroll.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultPayroll {

	private Optional<String>							 enterprise;
	private Optional<String>							 address;
	private Optional<String>							 address2;
	private Optional<String>							 cif;
	private Optional<String>							 ccc;
	private Optional<String>							 employee;
	private Optional<String>							 nif;
	private Optional<String>							 nss;
	private Optional<String>							 professionalGroup;
	private Optional<String>							 quotationGroup;
	private Optional<Date>								 antiquity;
	private Optional<Date>								 liquidPeriodStart;
	private Optional<Date>								 liquidPeriodEnd;
	private Optional<Integer>							 totalDays;
	private Optional<Map<Integer, ArrayList<Accrual>>>	 accruals;
	private Optional<Map<Integer, ArrayList<Deduction>>> deductions;
	private Optional<Double>							 accrualTotal;
	private Optional<Double>							 deductionTotal;
	private Optional<Double>							 payrollTotal;
	private Optional<ContingencyBases>					 contingencies;
	private Optional<PayrollTypes.Type>					 payrollType;

	private DefaultPayroll() {
	}

	public Optional<String> getEnterprise() {
		return enterprise;
	}

	public Optional<String> getAddress() {
		return address;
	}

	public Optional<String> getAddress_2() {
		return address2;
	}

	public Optional<String> getCif() {
		return cif;
	}

	public Optional<String> getCcc() {
		return ccc;
	}

	public Optional<String> getEmployee() {
		return employee;
	}

	public Optional<String> getNif() {
		return nif;
	}

	public Optional<String> getNss() {
		return nss;
	}

	public Optional<String> getProfessionalGroup() {
		return professionalGroup;
	}

	public Optional<String> getQuotationGroup() {
		return quotationGroup;
	}

	public Optional<Date> getAntiquity() {
		return antiquity;
	}

	public Optional<Date> getLiquidPeriodStart() {
		return liquidPeriodStart;
	}

	public Optional<Date> getLiquidPeriodEnd() {
		return liquidPeriodEnd;
	}

	public Optional<Integer> getTotalDays() {
		return totalDays;
	}

	public Optional<Map<Integer, ArrayList<Accrual>>> getAccruals() {
		return accruals;
	}

	public Optional<Map<Integer, ArrayList<Deduction>>> getDeductions() {
		return deductions;
	}

	public Optional<Double> getAccrualTotal() {
		return accrualTotal;
	}

	public Optional<Double> getDeductionTotal() {
		return deductionTotal;
	}

	public Optional<Double> getPayrollTotal() {
		return payrollTotal;
	}

	public Optional<ContingencyBases> getContingencies() {
		return contingencies;
	}

	public Optional<PayrollTypes.Type> getPayrollType() {
		return payrollType;
	}

	// BUILDER
	public static class DefaultPayrollBuilder {

		private Optional<String>							 enterprise;
		private Optional<String>							 address;
		private Optional<String>							 address2;
		private Optional<String>							 cif;
		private Optional<String>							 ccc;
		private Optional<String>							 employee;
		private Optional<String>							 nif;
		private Optional<String>							 nss;
		private Optional<String>							 professionalGroup;
		private Optional<String>							 quotationGroup;
		private Optional<Date>								 antiquity;
		private Optional<Date>								 liquidPeriodStart;
		private Optional<Date>								 liquidPeriodEnd;
		private Optional<Integer>							 totalDays;
		private Optional<Map<Integer, ArrayList<Accrual>>>	 accruals;
		private Optional<Map<Integer, ArrayList<Deduction>>> deductions;
		private Optional<Double>							 accrualTotal;
		private Optional<Double>							 deductionTotal;
		private Optional<Double>							 payrollTotal;
		private Optional<PayrollTypes.Type>					 payrollType;

		private Optional<ContingencyBases> contingencies;

		public DefaultPayrollBuilder() {
			enterprise			= Optional.empty();
			address				= Optional.empty();
			address2			= Optional.empty();
			cif					= Optional.empty();
			ccc					= Optional.empty();
			employee			= Optional.empty();
			nif					= Optional.empty();
			nss					= Optional.empty();
			professionalGroup	= Optional.empty();
			quotationGroup		= Optional.empty();
			antiquity			= Optional.empty();
			liquidPeriodStart	= Optional.empty();
			liquidPeriodEnd	= Optional.empty();
			totalDays			= Optional.empty();
			accruals			= Optional.of(new HashMap<>());
			deductions			= Optional.of(new HashMap<>());
			accrualTotal		= Optional.empty();
			deductionTotal		= Optional.empty();
			payrollTotal		= Optional.empty();
			payrollType		= Optional.empty();
			contingencies		= Optional.empty();
		}

		public DefaultPayrollBuilder setEnterprise(String enterprise) {
			this.enterprise = Optional.ofNullable(enterprise);
			return this;
		}

		public DefaultPayrollBuilder setAddress(String address) {
			this.address = Optional.ofNullable(address);
			return this;
		}

		public DefaultPayrollBuilder setAddress_2(String address_2) {
			this.address2 = Optional.ofNullable(address_2);
			return this;
		}

		public DefaultPayrollBuilder setCif(String cif) {
			this.cif = Optional.ofNullable(cif);
			return this;
		}

		public DefaultPayrollBuilder setCcc(String ccc) {
			this.ccc = Optional.ofNullable(ccc);
			return this;
		}

		public DefaultPayrollBuilder setEmployee(String employee) {
			this.employee = Optional.ofNullable(employee);
			return this;
		}

		public DefaultPayrollBuilder setNif(String nif) {
			this.nif = Optional.ofNullable(nif);
			return this;
		}

		public DefaultPayrollBuilder setNss(String nss) {
			this.nss = Optional.ofNullable(nss);
			return this;
		}

		public DefaultPayrollBuilder setProfessional_group(String professional_group) {
			this.professionalGroup = Optional.ofNullable(professional_group);
			return this;
		}

		public DefaultPayrollBuilder setQuotation_group(String quotation_group) {
			this.quotationGroup = Optional.ofNullable(quotation_group);
			return this;
		}

		public DefaultPayrollBuilder setAntiquity(Date antiquity) {
			this.antiquity = Optional.ofNullable(antiquity);
			return this;
		}

		public DefaultPayrollBuilder setLiquid_period_start(Date liquid_period_start) {
			this.liquidPeriodStart = Optional.ofNullable(liquid_period_start);
			return this;
		}

		public DefaultPayrollBuilder setLiquid_period_end(Date liquid_period_end) {
			this.liquidPeriodEnd = Optional.ofNullable(liquid_period_end);
			return this;
		}

		public DefaultPayrollBuilder setTotal_days(Integer total_days) {
			this.totalDays = Optional.ofNullable(total_days);
			return this;
		}

		public DefaultPayrollBuilder setAccruals(Map<Integer, ArrayList<Accrual>> accruals) {
			this.accruals = Optional.ofNullable(accruals);
			return this;
		}

		public DefaultPayrollBuilder setDeductions(Map<Integer, ArrayList<Deduction>> deductions) {
			this.deductions = Optional.ofNullable(deductions);
			return this;
		}

		public DefaultPayrollBuilder setAccrual_total(Double accrual_total) {
			this.accrualTotal = Optional.ofNullable(accrual_total);
			return this;
		}

		public DefaultPayrollBuilder setDeduction_total(Double deduction_total) {
			this.deductionTotal = Optional.ofNullable(deduction_total);
			return this;
		}

		public DefaultPayrollBuilder setPayroll_total(Double payroll_total) {
			this.payrollTotal = Optional.ofNullable(payroll_total);
			return this;
		}

		public DefaultPayrollBuilder setContingencies(ContingencyBases contingencies) {
			this.contingencies = Optional.ofNullable(contingencies);
			return this;
		}

		public DefaultPayrollBuilder setPayrollType(PayrollTypes.Type payroll_type) {
			this.payrollType = Optional.ofNullable(payroll_type);
			return this;
		}

		public DefaultPayrollBuilder setEnterprise(Optional<String> enterprise) {
			this.enterprise = enterprise;
			return this;
		}

		public DefaultPayrollBuilder setAddress(Optional<String> address) {
			this.address = address;
			return this;
		}

		public DefaultPayrollBuilder setAddress_2(Optional<String> address_2) {
			this.address2 = address_2;
			return this;
		}

		public DefaultPayrollBuilder setCif(Optional<String> cif) {
			this.cif = cif;
			return this;
		}

		public DefaultPayrollBuilder setCcc(Optional<String> ccc) {
			this.ccc = ccc;
			return this;
		}

		public DefaultPayrollBuilder setEmployee(Optional<String> employee) {
			this.employee = employee;
			return this;
		}

		public DefaultPayrollBuilder setNif(Optional<String> nif) {
			this.nif = nif;
			return this;
		}

		public DefaultPayrollBuilder setNss(Optional<String> nss) {
			this.nss = nss;
			return this;
		}

		public DefaultPayrollBuilder setProfessional_group(Optional<String> professional_group) {
			this.professionalGroup = professional_group;
			return this;
		}

		public DefaultPayrollBuilder setQuotation_group(Optional<String> quotation_group) {
			this.quotationGroup = quotation_group;
			return this;
		}

		public DefaultPayrollBuilder setAntiquity(Optional<Date> antiquity) {
			this.antiquity = antiquity;
			return this;
		}

		public DefaultPayrollBuilder setLiquid_period_start(Optional<Date> liquid_period_start) {
			this.liquidPeriodStart = liquid_period_start;
			return this;
		}

		public DefaultPayrollBuilder setLiquid_period_end(Optional<Date> liquid_period_end) {
			this.liquidPeriodEnd = liquid_period_end;
			return this;
		}

		public DefaultPayrollBuilder setTotal_days(Optional<Integer> total_days) {
			this.totalDays = total_days;
			return this;
		}

		public DefaultPayrollBuilder setAccruals(Optional<Map<Integer, ArrayList<Accrual>>> accruals) {
			this.accruals = accruals;
			return this;
		}

		public DefaultPayrollBuilder setDeductions(Optional<Map<Integer, ArrayList<Deduction>>> deductions) {
			this.deductions = deductions;
			return this;
		}

		public DefaultPayrollBuilder setAccrual_total(Optional<Double> accrual_total) {
			this.accrualTotal = accrual_total;
			return this;
		}

		public DefaultPayrollBuilder setDeduction_total(Optional<Double> deduction_total) {
			this.deductionTotal = deduction_total;
			return this;
		}

		public DefaultPayrollBuilder setPayroll_total(Optional<Double> payroll_total) {
			this.payrollTotal = payroll_total;
			return this;
		}

		public DefaultPayrollBuilder setContingencies(Optional<ContingencyBases> contingencies) {
			this.contingencies = contingencies;
			return this;
		}

		public DefaultPayrollBuilder setPayrollType(Optional<PayrollTypes.Type> payroll_type) {
			this.payrollType = payroll_type;
			return this;
		}

		// BUILD A DEFAULT PAYROLL
		public DefaultPayroll build() {

			DefaultPayroll p = new DefaultPayroll();

			p.enterprise		= this.enterprise;
			p.address			= this.address;
			p.address2			= this.address2;
			p.cif				= this.cif;
			p.ccc				= this.ccc;
			p.employee			= this.employee;
			p.nif				= this.nif;
			p.nss				= this.nss;
			p.professionalGroup	= this.professionalGroup;
			p.quotationGroup	= this.quotationGroup;
			p.antiquity			= this.antiquity;
			p.liquidPeriodStart	= this.liquidPeriodStart;
			p.liquidPeriodEnd	= this.liquidPeriodEnd;
			p.totalDays			= this.totalDays;
			p.accruals			= this.accruals;
			p.deductions		= this.deductions;
			p.accrualTotal		= this.accrualTotal;
			p.deductionTotal	= this.deductionTotal;
			p.payrollTotal		= this.payrollTotal;
			p.contingencies		= this.contingencies;
			p.payrollType		= this.payrollType;

			return p;
		}

	}
}
