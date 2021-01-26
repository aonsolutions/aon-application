package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.creators.payroll.PayrollTypes;



public class DefaultPayroll {
	
	private Optional <String> enterprise;
	private Optional <String> address;
	private Optional <String> address_2;
	private Optional <String> cif;
	private Optional <String> ccc;
	private Optional <String> employee;
	private Optional <String> nif;
	private Optional <String> nss;
	private Optional <String> professional_group;
	private Optional <String> quotation_group;
	private Optional <Date>   antiquity;
 	private Optional <Date>   liquid_period_start;
 	private Optional <Date>   liquid_period_end;
 	private Optional <Integer> total_days;
 	private Optional <Map<Integer,ArrayList<DefaultPayrollAccrual>>> accruals;
 	private Optional <Map<Integer,ArrayList<DefaultPayrollDeduction>>> deductions;
 	private Optional <Double> accrual_total;
 	private Optional <Double> deduction_total;
 	private Optional <Double> payroll_total;
 	private Optional <Contingency_bases> contingencies;
 	private Optional <PayrollTypes.Type> payroll_type;
 	
 	private DefaultPayroll() {}
 	 	
 	public Optional<String> 	getEnterprise() {return enterprise;}
	public Optional<String> 	getAddress() {return address;}
	public Optional<String> 	getAddress_2() {return address_2;}
	public Optional<String> 	getCif() {return cif;}
	public Optional<String> 	getCcc() {return ccc;}
	public Optional<String> 	getEmployee() {return employee;}
	public Optional<String> 	getNif() {return nif;}
	public Optional<String> 	getNss() {return nss;}
	public Optional<String> 	getProfessional_group() {return professional_group;}
	public Optional<String> 	getQuotation_group() {return quotation_group;}
	public Optional<Date> 		getAntiquity() {return antiquity;}
	public Optional<Date> 		getLiquid_period_start() {return liquid_period_start;}
	public Optional<Date> 		getLiquid_period_end() {return liquid_period_end;}
	public Optional<Integer> 	getTotal_days() {return total_days;}
	public Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> getAccruals() {return accruals;}
	public Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> getDeductions() {return deductions;}
	public Optional<Double> 	getAccrual_total() {return accrual_total;}
	public Optional<Double> 	getDeduction_total() {return deduction_total;}
	public Optional<Double> 	getPayroll_total() {return payroll_total;}
	public Optional<Contingency_bases> getContingencies() {return contingencies;}
	public Optional<PayrollTypes.Type> getPayrollType(){return payroll_type;}
	
	//BUILDER
 	public static class DefaultPayrollBuilder{
 		
 		private Optional <String> enterprise;
 		private Optional <String> address;
 		private Optional <String> address_2;
 		private Optional <String> cif;
 		private Optional <String> ccc;
 		private Optional <String> employee;
 		private Optional <String> nif;
 		private Optional <String> nss;
 		private Optional <String> professional_group;
 		private Optional <String> quotation_group;
 		private Optional <Date>   antiquity;
 	 	private Optional <Date>   liquid_period_start;
 	 	private Optional <Date>   liquid_period_end;
 	 	private Optional <Integer> total_days;
 	 	private Optional <Map<Integer,ArrayList<DefaultPayrollAccrual>>> accruals;
 	 	private Optional <Map<Integer,ArrayList<DefaultPayrollDeduction>>> deductions;
 	 	private Optional <Double> accrual_total;
 	 	private Optional <Double> deduction_total;
 	 	private Optional <Double> payroll_total;
 	 	private Optional <PayrollTypes.Type> payroll_type;
 	 	
 	 	private Optional <Contingency_bases> contingencies;

		public DefaultPayrollBuilder setEnterprise(Optional<String> enterprise) { 
			this.enterprise = enterprise; 
			return this;
		}
		public DefaultPayrollBuilder setAddress(Optional<String> address) {
			this.address = address; 
			return this;
		}
		public DefaultPayrollBuilder setAddress_2(Optional<String> address_2) {
			this.address_2 = address_2; 
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
			this.professional_group = professional_group;
			return this;
		}
		public DefaultPayrollBuilder setQuotation_group(Optional<String> quotation_group) {
			this.quotation_group = quotation_group; return this;
		}

		public DefaultPayrollBuilder setAntiquity(Optional<Date> antiquity) {
			this.antiquity = antiquity; 
			return this;
		}
		public DefaultPayrollBuilder setLiquid_period_start(Optional<Date> liquid_period_start) {
			this.liquid_period_start = liquid_period_start; 
			return this;
		}
		public DefaultPayrollBuilder setLiquid_period_end(Optional<Date> liquid_period_end) {
			this.liquid_period_end = liquid_period_end; 
			return this;
		}
		public DefaultPayrollBuilder setTotal_days(Optional<Integer> total_days) {
			this.total_days = total_days; 
			return this;
		}
		public DefaultPayrollBuilder setAccruals(Optional<Map<Integer, ArrayList<DefaultPayrollAccrual>>> accruals) {
			this.accruals = accruals; 
			return this;
		}
		public DefaultPayrollBuilder setDeductions(Optional<Map<Integer, ArrayList<DefaultPayrollDeduction>>> deductions) {
			this.deductions = deductions; 
			return this;
		}
		public DefaultPayrollBuilder setAccrual_total(Optional<Double> accrual_total) {
			this.accrual_total = accrual_total; 
			return this;
		}
		public DefaultPayrollBuilder setDeduction_total(Optional<Double> deduction_total) {
			this.deduction_total = deduction_total; 
			return this;
		}
		public DefaultPayrollBuilder setPayroll_total(Optional<Double> payroll_total) {
			this.payroll_total = payroll_total; 
			return this;
		}
		public DefaultPayrollBuilder setContingencies(Optional<Contingency_bases> contingencies) {
			this.contingencies = contingencies; 
			return this;
		}
		public DefaultPayrollBuilder setPayrollType(Optional<PayrollTypes.Type> payroll_type) {
			this.payroll_type = payroll_type;
			return this;
		}
		
		//BUILD A DEFAULT PAYROLL
		public DefaultPayroll build() {
			
			DefaultPayroll p = new DefaultPayroll();
			
			p.enterprise = 			this.enterprise;
			p.address = 			this.address;
			p.address_2 = 			this.address_2;
			p.cif = 				this.cif;
			p.ccc = 				this.ccc;
			p.employee = 			this.employee;
			p.nif = 				this.nif;
			p.nss = 				this.nss;
			p.professional_group = 	this.professional_group;
			p.quotation_group = 	this.quotation_group;
			p.antiquity = 			this.antiquity;
			p.liquid_period_start = this.liquid_period_start;
			p.liquid_period_end = this.liquid_period_end;
			p.total_days = 			this.total_days;
			p.accruals = 			this.accruals;
			p.deductions = 			this.deductions;
			p.accrual_total = 		this.accrual_total;
			p.deduction_total = 	this.deduction_total;
			p.payroll_total = 		this.payroll_total;
			p.contingencies = 		this.contingencies;
			
			return p;
		}
 	 	 		
 	}
}
