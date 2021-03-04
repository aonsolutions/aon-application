package com.esferalia.aon.gwt.payroll.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases.Contingency_bases_builder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
/**
 * Class containing method/s to print payrolls from a Salary and a SalaryDraft object
 */
public class DraftPayrollBuilder {
	/**
	 * Method to generate a PDF payroll from an ISalary and place it on the OutputStream passed as parameter
	 * @param outputStream The OutputStream on which will be written the PDF
	 * @param salary An ISalary object
	 * @throws CanNotCreatePdfException
	 * @throws SalaryException
	 */
	public static void generatePayroll (OutputStream outputStream, ISalary salary) throws CanNotCreatePdfException, SalaryException {
		DefaultPayrollTemplate dpt = new DefaultPayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		
		
		
			//ENTERPRISE RELATED DATA
			{
				dpb.setCcc(salary.getCcc());
				dpb.setCif(salary.getEnterpriseDocument());
				dpb.setEnterprise(salary.getEnterpriseName());
			
				//ADDRESS FITTING
				if (salary.getEnterpriseAddress() != null) {
					List<String> address = null;
					try {
						address = PDFToolkit.divide_string_to_fit(salary.getEnterpriseAddress(), 200, PdfFonts.HELVETICA, 9f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					if (address != null && address.size() > 1) {
						dpb.setAddress(address.get(0));
						String address2 = "";
						for (int i = 1; i < address.size(); i++)
							address2.concat(address.get(i));
						dpb.setAddress_2(address2);
					} else
						dpb.setAddress(salary.getEnterpriseAddress());
				}
			}
			//EMPLOYEE RELATED DATA
			{
				dpb.setAntiquity(salary.getSeniorityDate());
				dpb.setNif(salary.getEmployeeDocument());
				dpb.setNss(salary.getSocialSecurityNumber());
				dpb.setEmployee(salary.getEmployeeName());
				dpb.setQuotation_group(salary.getQuoteGroup());
				dpb.setProfessional_group(salary.getCategory());
			}
			//PAYROLL RELATED DATA
			{
				dpb.setLiquid_period_start(salary.getStartDate());
				dpb.setLiquid_period_end(salary.getEndDate());
				dpb.setTotal_days(salary.getTimeUnits());
				if (salary.getType().ordinal() == SalaryType.SALARY.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.SALARY);
				else if (salary.getType().ordinal() == SalaryType.EXTRA.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.EXTRAS);
				else if (salary.getType().ordinal() == SalaryType.SETTLE.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.SETTLEMENT);
				else if (salary.getType().ordinal() == SalaryType.DELAY.ordinal())
					dpb.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
				else
					dpb.setPayrollType(PayrollTypes.Type.SALARY);
			}
			//PAYMENTS
			{
				dpb.setAccrual_total(salary.getTotalPayment());
				Collection<IPayment> payments = salary.getPaymentS();
				Map<Integer, ArrayList<DefaultPayrollAccrual>> accruals = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
				payments.forEach(p -> {
					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(), p.getDescription());
					if (!accruals.containsKey(p.getType().ordinal()))
						accruals.put(p.getType().ordinal(), new ArrayList<DefaultPayrollAccrual>());
					accruals.get(p.getType().ordinal()).add(accrual);
				});
				dpb.setAccruals(accruals);
			}
			//DEDUCTIONS
			{
				dpb.setDeduction_total(salary.getTotalDeduction());
				Collection<IDeduction> deductions = salary.getDeductionS();
				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionsMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();
				deductions.forEach(d -> {
					Double percent = null;
					
					try {
						percent = Double.parseDouble(d.getDescription().replaceAll("\\s", "").replaceAll("%", ""));
					} catch (NumberFormatException e) {}
					
					DefaultPayrollDeduction deduction = new DefaultPayrollDeduction(d.getAmount(), d.getType().getName(new Locale("es")), percent);
					if (!deductionsMap.containsKey(d.getType().ordinal()))
						deductionsMap.put(d.getType().ordinal(), new ArrayList<DefaultPayrollDeduction>());
					deductionsMap.get(d.getType().ordinal()).add(deduction);
				});
				dpb.setDeductions(deductionsMap);
			}
			//COSTS
			{
				Contingency_bases_builder cbb = new Contingency_bases_builder();
				//INITIALIZING CONTINGENCIES, JUST IN CASE
				{
					cbb.setAt_ep_ap_enterprise(Optional.empty());
					cbb.setAt_ep_type(Optional.empty());
					cbb.setCommon_cont_ap_enterprise(Optional.empty());
					cbb.setCommon_cont_base(Optional.empty());
					cbb.setCommon_cont_type(Optional.empty());
					cbb.setExtra_proration_amount(Optional.empty());
					cbb.setFogasa_ap_enterprise(Optional.empty());
					cbb.setFogasa_type(Optional.empty());
					cbb.setForce_majeure_ap_enterprise(Optional.empty());
					cbb.setForce_majeure_base(Optional.empty());
					cbb.setForce_majeure_type(Optional.empty());
					cbb.setIrpf_esp(Optional.empty());
					cbb.setIrpf_retrib_diner(Optional.empty());
					cbb.setMonthly_amount(Optional.empty());
					cbb.setNo_struct_ap_enterprise(Optional.empty());
					cbb.setNo_struct_base(Optional.empty());
					cbb.setNo_struct_type(Optional.empty());
					cbb.setProfes_form_ap_enterprise(Optional.empty());
					cbb.setProfes_form_type(Optional.empty());
					cbb.setProfessional_cont_base(Optional.empty());
					cbb.setTotal(Optional.empty());
					cbb.setUnemployment_ap_enterprise(Optional.empty());
					cbb.setUnemployment_type(Optional.empty());
				}
				
				Collection<IDeduction> costs = salary.getCostS();
				
				cbb.setMonthly_amount(Optional.ofNullable(salary.getRemuneration()));
				cbb.setExtra_proration_amount(Optional.ofNullable(salary.getExtraPayProration()));
				cbb.setCommon_cont_base(Optional.ofNullable(salary.getCommonBase()));
				cbb.setProfessional_cont_base(Optional.ofNullable(salary.getProfessionalBase()));
				cbb.setIrpf_retrib_diner(Optional.ofNullable(salary.getIrpfBase()));
				cbb.setIrpf_esp(Optional.ofNullable(salary.getInKindIrpfBase()));
				cbb.setTotal(Optional.ofNullable(salary.getSocialSecurityContributions()));
				
				costs.forEach(c -> {
					Double percentD = null;
					
					try {
						percentD = Double.parseDouble(c.getDescription().replaceAll("\\s", "").replaceAll("%", ""));
					} catch (NumberFormatException e) {}
					
					Optional<Double> percent = Optional.ofNullable(percentD);
					
					if (c.getType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal()) {
						cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setCommon_cont_type(percent);
					} else if (c.getType().ordinal() == DeductionType.PROFESSIONAL_CONTINGENCY.ordinal()) {
						cbb.setAt_ep_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setAt_ep_type(percent);
					} else if (c.getType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal()) {
						cbb.setUnemployment_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setUnemployment_type(percent);
					} else if (c.getType().ordinal() == DeductionType.JOB_TRAINING.ordinal()) {
						cbb.setProfes_form_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setProfes_form_type(percent);
					} else if (c.getType().ordinal() == DeductionType.FOGASA.ordinal()) {
						cbb.setFogasa_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setFogasa_type(percent);
					} else if (c.getType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal()) {
						cbb.setForce_majeure_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setForce_majeure_type(percent);
					} else if (c.getType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal()) {
						cbb.setNo_struct_ap_enterprise(Optional.ofNullable(c.getAmount()));
						cbb.setNo_struct_type(percent);
					}
				});
				dpb.setContingencies(cbb.build());		
			}	
			dpb.setPayroll_total(salary.getTotalLiquid());
			
			dpt.print(outputStream, dpb.build(), Optional.empty(), Optional.ofNullable(new Locale("es")));

	}
}
