package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION;

public interface IDefaultPayroll {

	Optional<String> getEnterprise();

	Optional<String> getAddress();

	Optional<String> getAddress2();

	Optional<String> getCif();

	Optional<String> getCcc();

	Optional<String> getEmployee();

	Optional<String> getNif();

	Optional<String> getNss();

	Optional<String> getProfessionalGroup();

	Optional<String> getQuotationGroup();

	Optional<Date> getAntiquity();

	Optional<Date> getLiquidPeriodStart();

	Optional<Date> getLiquidPeriodEnd();

	Optional<Integer> getTotalDays();

	Optional<Map<Integer, ArrayList<PDFPayment>>> getAccruals();

	Optional<Map<Integer, ArrayList<PDFDeduction>>> getCosts();

	Optional<Map<Integer, ArrayList<PDFDeduction>>> getDeductions();

	Optional<Double> getPaymentsTotal();

	Optional<Double> getDeductionTotal();

	Optional<Double> getTotalSSContributions();

	Optional<Double> getPayrollTotal();

	Optional<ContingencyBases> getContingencies();

	Optional<PayrollTypes.Type> getPayrollType();

	Optional<PartTimeParams> getPartTimeParams();

	IMPRESION getImpressionType();

}