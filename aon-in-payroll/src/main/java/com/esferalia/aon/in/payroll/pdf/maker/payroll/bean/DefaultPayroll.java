package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION.DEFAULT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Payroll class for PDF print
 * 
 * @author akrck02
 *
 */
public class DefaultPayroll implements IDefaultPayroll {
	
	public static enum IMPRESION {
		DRAFT, DEFAULT
	}

	private Optional<String>								enterprise;
	private Optional<String>								address;
	private Optional<String>								address2;
	private Optional<String>								cif;
	private Optional<String>								ccc;
	private Optional<String>								employee;
	private Optional<String>								nif;
	private Optional<String>								nss;
	private Optional<String>								professionalGroup;
	private Optional<String>								quotationGroup;
	private Optional<Date>									antiquity;
	private Optional<Date>									liquidPeriodStart;
	private Optional<Date>									liquidPeriodEnd;
	private Optional<Integer>								totalDays;
	private Optional<Map<Integer, ArrayList<PDFPayment>>>	accruals;
	private Optional<Map<Integer, ArrayList<PDFDeduction>>>	deductions;
	private Optional<Map<Integer, ArrayList<PDFDeduction>>>	costs;
	private Optional<Double>								totalSSContributions;
	private Optional<Double>								accrualTotal;
	private Optional<Double>								deductionTotal;
	private Optional<Double>								payrollTotal;
	private Optional<ContingencyBases>						contingencies;
	private Optional<PayrollTypes.Type>						payrollType;
	private Optional<PartTimeParams>						partTimeParams;
	private IMPRESION										impressionType;

	private DefaultPayroll() {
	}

	@Override
	public Optional<String> getEnterprise() {
		return enterprise;
	}

	@Override
	public Optional<String> getAddress() {
		return address;
	}

	@Override
	public Optional<String> getAddress2() {
		return address2;
	}

	@Override
	public Optional<String> getCif() {
		return cif;
	}

	@Override
	public Optional<String> getCcc() {
		return ccc;
	}

	@Override
	public Optional<String> getEmployee() {
		return employee;
	}

	@Override
	public Optional<String> getNif() {
		return nif;
	}

	@Override
	public Optional<String> getNss() {
		return nss;
	}

	@Override
	public Optional<String> getProfessionalGroup() {
		return professionalGroup;
	}

	@Override
	public Optional<String> getQuotationGroup() {
		return quotationGroup;
	}

	@Override
	public Optional<Date> getAntiquity() {
		return antiquity;
	}

	@Override
	public Optional<Date> getLiquidPeriodStart() {
		return liquidPeriodStart;
	}

	@Override
	public Optional<Date> getLiquidPeriodEnd() {
		return liquidPeriodEnd;
	}

	@Override
	public Optional<Integer> getTotalDays() {
		return totalDays;
	}

	@Override
	public Optional<Map<Integer, ArrayList<PDFPayment>>> getAccruals() {
		return accruals;
	}

	@Override
	public Optional<Map<Integer, ArrayList<PDFDeduction>>> getDeductions() {
		return deductions;
	}
	
	@Override
	public Optional<Map<Integer, ArrayList<PDFDeduction>>> getCosts() {
		return costs;
	}

	@Override
	public Optional<Double> getPaymentsTotal() {
		return accrualTotal;
	}

	@Override
	public Optional<Double> getDeductionTotal() {
		return deductionTotal;
	}

	@Override
	public Optional<Double> getTotalSSContributions() {
		return totalSSContributions;
	}

	@Override
	public Optional<Double> getPayrollTotal() {
		return payrollTotal;
	}

	@Override
	public Optional<ContingencyBases> getContingencies() {
		return contingencies;
	}

	@Override
	public Optional<PayrollTypes.Type> getPayrollType() {
		return payrollType;
	}
	
	@Override
	public Optional<PartTimeParams> getPartTimeParams() {
		return partTimeParams;
	}
	
	

	@Override
	public IMPRESION getImpressionType() {
		return impressionType;
	}

	// BUILDER
	public static class DefaultPayrollBuilder {

		private Optional<String>								enterprise;
		private Optional<String>								address;
		private Optional<String>								address2;
		private Optional<String>								cif;
		private Optional<String>								ccc;
		private Optional<String>								employee;
		private Optional<String>								nif;
		private Optional<String>								nss;
		private Optional<String>								professionalGroup;
		private Optional<String>								quotationGroup;
		private Optional<Date>									antiquity;
		private Optional<Date>									liquidPeriodStart;
		private Optional<Date>									liquidPeriodEnd;
		private Optional<Integer>								totalDays;
		private Optional<Map<Integer, ArrayList<PDFPayment>>>	accruals;
		private Optional<Map<Integer, ArrayList<PDFDeduction>>>	deductions;
		private Optional<Map<Integer, ArrayList<PDFDeduction>>>	costs;
		private Optional<Double>								accrualTotal;
		private Optional<Double>								totalSSContributions;
		private Optional<Double>								deductionTotal;
		private Optional<Double>								payrollTotal;
		private Optional<PayrollTypes.Type>						payrollType;
		private Optional<PartTimeParams>						partTimeParams;
		private IMPRESION										impressionType;

		private Optional<ContingencyBases> contingencies;

		public DefaultPayrollBuilder() {
			enterprise		  = Optional.empty();
			address			  = Optional.empty();
			address2		  = Optional.empty();
			cif				  = Optional.empty();
			ccc				  = Optional.empty();
			employee		  = Optional.empty();
			nif				  = Optional.empty();
			nss				  = Optional.empty();
			professionalGroup = Optional.empty();
			quotationGroup	  = Optional.empty();
			antiquity		  = Optional.empty();
			liquidPeriodStart = Optional.empty();
			liquidPeriodEnd	  = Optional.empty();
			totalDays		  = Optional.empty();
			accruals		  = Optional.of(new HashMap<>());
			deductions		  = Optional.of(new HashMap<>());
			costs		  	  = Optional.of(new HashMap<>());
			totalSSContributions = Optional.empty();
			accrualTotal	  = Optional.empty();
			deductionTotal	  = Optional.empty();
			payrollTotal	  = Optional.empty();
			payrollType		  = Optional.empty();
			contingencies	  = Optional.empty();
			partTimeParams	  = Optional.empty();
			impressionType	  = DEFAULT;
		}
		
		public DefaultPayrollBuilder setImpressionType(IMPRESION impressionType) {
			this.impressionType = impressionType;
			return this;
		}

		public DefaultPayrollBuilder setEnterprise(String enterprise) {
			this.enterprise = Optional.ofNullable(enterprise);
			return this;
		}

		public DefaultPayrollBuilder setAddress(String address) {
			this.address = Optional.ofNullable(address);
			return this;
		}

		public DefaultPayrollBuilder setAddress2(String address2) {
			this.address2 = Optional.ofNullable(address2);
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

		public DefaultPayrollBuilder setProfessionalGroup(String professionalGroup) {
			this.professionalGroup = Optional.ofNullable(professionalGroup);
			return this;
		}

		public DefaultPayrollBuilder setQuotationGroup(String quotationGroup) {
			this.quotationGroup = Optional.ofNullable(quotationGroup);
			return this;
		}

		public DefaultPayrollBuilder setAntiquity(Date antiquity) {
			this.antiquity = Optional.ofNullable(antiquity);
			return this;
		}

		public DefaultPayrollBuilder setLiquidPeriodStart(Date liquidPeriodStart) {
			this.liquidPeriodStart = Optional.ofNullable(liquidPeriodStart);
			return this;
		}

		public DefaultPayrollBuilder setLiquidPeriodEnd(Date liquidPeriodEnd) {
			this.liquidPeriodEnd = Optional.ofNullable(liquidPeriodEnd);
			return this;
		}

		public DefaultPayrollBuilder setTotalDays(Integer totalDays) {
			this.totalDays = Optional.ofNullable(totalDays);
			return this;
		}

		public DefaultPayrollBuilder setAccruals(Map<Integer, ArrayList<PDFPayment>> accruals) {
			this.accruals = Optional.ofNullable(accruals);
			return this;
		}

		public DefaultPayrollBuilder setDeductions(Map<Integer, ArrayList<PDFDeduction>> deductions) {
			this.deductions = Optional.ofNullable(deductions);
			return this;
		}

		public DefaultPayrollBuilder setCosts(Map<Integer, ArrayList<PDFDeduction>> costs) {
			this.costs = Optional.ofNullable(costs);
			return this;
		}

		public DefaultPayrollBuilder setAccrualTotal(Double accrualTotal) {
			this.accrualTotal = Optional.ofNullable(accrualTotal);
			return this;
		}

		public DefaultPayrollBuilder setDeductionTotal(Double deductionTotal) {
			this.deductionTotal = Optional.ofNullable(deductionTotal);
			return this;
		}
		
		public DefaultPayrollBuilder setTotalSSContributions(Double totalSSContributions) {
			this.totalSSContributions = Optional.ofNullable(totalSSContributions);
			return this;
		}

		public DefaultPayrollBuilder setPayrollTotal(Double payrollTotal) {
			this.payrollTotal = Optional.ofNullable(payrollTotal);
			return this;
		}

		public DefaultPayrollBuilder setContingencies(ContingencyBases contingencies) {
			this.contingencies = Optional.ofNullable(contingencies);
			return this;
		}

		public DefaultPayrollBuilder setPayrollType(PayrollTypes.Type payrollType) {
			this.payrollType = Optional.ofNullable(payrollType);
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

		public DefaultPayrollBuilder setAddress2(Optional<String> address2) {
			this.address2 = address2;
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

		public DefaultPayrollBuilder setProfessionalGroup(Optional<String> professionalGroup) {
			this.professionalGroup = professionalGroup;
			return this;
		}

		public DefaultPayrollBuilder setQuotationGroup(Optional<String> quotationGroup) {
			this.quotationGroup = quotationGroup;
			return this;
		}

		public DefaultPayrollBuilder setAntiquity(Optional<Date> antiquity) {
			this.antiquity = antiquity;
			return this;
		}

		public DefaultPayrollBuilder setLiquidPeriodStart(Optional<Date> liquidPeriodStart) {
			this.liquidPeriodStart = liquidPeriodStart;
			return this;
		}

		public DefaultPayrollBuilder setLiquidPeriodEnd(Optional<Date> liquidPeriodEnd) {
			this.liquidPeriodEnd = liquidPeriodEnd;
			return this;
		}

		public DefaultPayrollBuilder setTotalDays(Optional<Integer> totalDays) {
			this.totalDays = totalDays;
			return this;
		}

		public DefaultPayrollBuilder setAccruals(Optional<Map<Integer, ArrayList<PDFPayment>>> accruals) {
			this.accruals = accruals;
			return this;
		}

		public DefaultPayrollBuilder setDeductions(Optional<Map<Integer, ArrayList<PDFDeduction>>> deductions) {
			this.deductions = deductions;
			return this;
		}

		public DefaultPayrollBuilder setCosts(Optional<Map<Integer, ArrayList<PDFDeduction>>> costs) {
			this.costs = costs;
			return this;
		}

		public DefaultPayrollBuilder setAccrualTotal(Optional<Double> accrualTotal) {
			this.accrualTotal = accrualTotal;
			return this;
		}

		public DefaultPayrollBuilder setDeductionTotal(Optional<Double> deductionTotal) {
			this.deductionTotal = deductionTotal;
			return this;
		}
		
		public DefaultPayrollBuilder setTotalSSContributions(Optional<Double> totalSSContributions) {
			this.totalSSContributions = totalSSContributions;
			return this;
		}

		public DefaultPayrollBuilder setPayrollTotal(Optional<Double> payrollTotal) {
			this.payrollTotal = payrollTotal;
			return this;
		}

		public DefaultPayrollBuilder setContingencies(Optional<ContingencyBases> contingencies) {
			this.contingencies = contingencies;
			return this;
		}

		public DefaultPayrollBuilder setPayrollType(Optional<PayrollTypes.Type> payrollType) {
			this.payrollType = payrollType;
			return this;
		}
		
		public DefaultPayrollBuilder setPartTimeParams(Optional<PartTimeParams> partTimeParams) {
			this.partTimeParams = partTimeParams;
			return this;
		}

		// BUILD A DEFAULT PAYROLL
		public DefaultPayroll build() {

			DefaultPayroll p = new DefaultPayroll();

			p.impressionType 	= this.impressionType;
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
			p.costs				= this.costs;
			p.totalSSContributions = this.totalSSContributions;
			p.accrualTotal		= this.accrualTotal;
			p.deductionTotal	= this.deductionTotal;
			p.payrollTotal		= this.payrollTotal;
			p.contingencies		= this.contingencies;
			p.payrollType		= this.payrollType;
			p.partTimeParams	= this.partTimeParams;

			return p;
		}

	}
}
