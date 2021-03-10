package com.esferalia.aon.gwt.payroll.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.jooq.DSLContext;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.DefaultPayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.Contingency_bases.Contingency_bases_builder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollAccrual;
import com.esferalia.aon.in.payroll.pdf.creators.payroll._default.beans.DefaultPayrollDeduction;
import com.esferalia.aon.in.payroll.pdf.creators.payroll.commons.PayrollTypes;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;
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
	public static void generatePayroll (OutputStream outputStream, String domainName, ISalary salary) throws CanNotCreatePdfException, SalaryException {
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
						address = PDFToolkit.divide_string_to_fit(salary.getEnterpriseAddress(), 170, PdfFonts.HELVETICA, 9f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (address != null && address.size() > 1) {
						dpb.setAddress(address.get(0) != null ? address.get(0).trim() : null);
						dpb.setAddress_2(address.get(1));
					} else {
						dpb.setAddress(salary.getEnterpriseAddress());
					}
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
				HashMap<Integer, ArrayList<DefaultPayrollAccrual>> paymentMap = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
				Collection<IPayment> payments = salary.getPaymentS();
				payments.stream().filter(DraftPayrollBuilder::filter).sorted(Comparator.comparing(p -> {
					return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; //Nulls or empties down
				})).forEach(p -> {
					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(), p.getDescription().replaceAll("\\[\\d*\\]", ""));
					if (!paymentMap.containsKey(p.getType().ordinal()))
						paymentMap.put(p.getType().ordinal(), new ArrayList<DefaultPayrollAccrual>());
					 
					if (paymentMap.get(p.getType().ordinal()).stream().anyMatch(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
						DefaultPayrollAccrual repAcc = paymentMap.get(p.getType().ordinal()).stream().findFirst().get();
						repAcc.setAmount(repAcc.getAmount().orElse(0d)+p.getAmount());
					} else
						paymentMap.get(p.getType().ordinal()).add(accrual);
				});
				dpb.setAccruals(paymentMap);
				
				
				
//				dpb.setAccrual_total(salary.getTotalPayment());
//				Collection<IPayment> payments = salary.getPaymentS();
//				Map<Integer, ArrayList<DefaultPayrollAccrual>> accruals = new HashMap<Integer, ArrayList<DefaultPayrollAccrual>>();
//				
//				Map<Integer, HashMap<String, DefaultPayrollAccrual>> accrualsSet = new HashMap<Integer, HashMap<String, DefaultPayrollAccrual>>();
//				
//				payments.stream().sorted(Comparator.comparing(p -> p.getDescription())).filter(DraftPayrollBuilder::filter).forEach(p -> {
//					DefaultPayrollAccrual accrual = new DefaultPayrollAccrual(p.getAmount(), p.getDescription().replaceAll("\\[\\d*\\]", ""));
//					if (!accrualsSet.containsKey(p.getType().ordinal()))
//						accrualsSet.put(p.getType().ordinal(), new HashMap<String, DefaultPayrollAccrual>());
//					if (accrualsSet.get(p.getType().ordinal()).containsKey(p.getDescription())) {
//						DefaultPayrollAccrual repAcc = accrualsSet.get(p.getType().ordinal()).get(p.getDescription());
//						
//						repAcc = new DefaultPayrollAccrual(repAcc.getAmount().get() + p.getAmount() , repAcc.getDescription().get());
//						accrual = repAcc;
//					}
//					accrualsSet.get(p.getType().ordinal()).put(p.getDescription(), accrual);
//				});
//				
//				accrualsSet.keySet().forEach(k -> {
//					ArrayList<DefaultPayrollAccrual> list = new ArrayList<DefaultPayrollAccrual>();
//					list.addAll(accrualsSet.get(k).values());
//					accruals.put(k, list);
//				});
//				
//				dpb.setAccruals(accruals);
			}
			//DEDUCTIONS
			{
				dpb.setDeduction_total(salary.getTotalDeduction());
				
				ArrayList<String> inserted = new ArrayList<String>();
				
				Collection<IDeduction> deductions = salary.getDeductionS();
				HashMap<Integer, ArrayList<DefaultPayrollDeduction>> deductionsMap = new HashMap<Integer, ArrayList<DefaultPayrollDeduction>>();
				deductions.stream().sorted(Comparator.comparing(d -> d.getType().getName(new Locale("es")))).forEach(d -> {
					Double percent = null;
					
					try {
						percent = Double.parseDouble(d.getDescription().replaceAll("\\s", "").replaceAll("%", ""));
					} catch (NumberFormatException e) {}
					
					int type = 0;
					switch (d.getType().ordinal()) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						type = 1;
						break;
					case 6:
						type = 2;
						break;
					case 7:
						type = 3;
						break;
					case 8:
						type = 4;
						break;
					default:
						type = 5;
					}
					
					String desc =d.getType().getName(new Locale("es"));
					
					if(desc == null || desc.isEmpty()) {
						switch (d.getType().ordinal()) {
						case 0:
							desc = "Contingencias comunes";
							break;
						case 2:
							desc = "Desempleo";
							break;
						case 3:
							desc = "Formación profesional";
							break;
						case 4:
							desc = "Horas extraordinarias (Estruc.)";
							break;
						case 5:
							desc = "Horas extraordinarias (No Estruc.)";
							break;
						case 6:
							desc = "Retribuciones dinerarias";
							break;
						case 7:
							desc = "Anticipo";
							break;
						case 8:
							desc = "En especie";
							break;
						case 10:
							desc = "Embargo";
							break;
						default:
							desc = "Otras deducciones";
					}
					}
					
					
					DefaultPayrollDeduction deduction = new DefaultPayrollDeduction(d.getAmount(), desc, percent);
					
					System.out.println(deduction.getDescription()+" : "+deduction.getAmount());
					
					if (!deductionsMap.containsKey(type))
						deductionsMap.put(type, new ArrayList<DefaultPayrollDeduction>());
					
					if (deductionsMap.get(type).stream()
							.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), deduction.getDescription().get()))) {
						DefaultPayrollDeduction ded = deductionsMap.get(type).stream()
								.filter(p -> AonStringUtils.equalsIgnoreCase(deduction.getDescription().get(),p.getDescription().get())).findFirst().get();
						ded.setAmount(ded.getAmount().get()+deduction.getAmount().get());
					} else
						deductionsMap.get(type).add(deduction);
					inserted.add(getDeductionType(d.getType().ordinal()));
				});
				

				if (deductionsMap.get(1)==null)
					deductionsMap.put(1, new ArrayList<DefaultPayrollDeduction>());
				if (deductionsMap.get(2)==null)
					deductionsMap.put(2, new ArrayList<DefaultPayrollDeduction>());
				
				if (!inserted.contains("CGC"))
					deductionsMap.get(1).add(new DefaultPayrollDeduction(0d, "Contingencias comunes", 0d));
				if (!inserted.contains("DESMPL"))
					deductionsMap.get(1).add(new DefaultPayrollDeduction(0d, "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionsMap.get(1).add(new DefaultPayrollDeduction(0d, "Formación profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionsMap.get(2).add(new DefaultPayrollDeduction(0d, "Retribuciones dinerarias", 0d));

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
				//COSTS
				Collection<IDeduction> costs = salary.getCostS();
				
				cbb.setMonthly_amount(Optional.ofNullable(salary.getRemuneration()));
				cbb.setExtra_proration_amount(Optional.ofNullable(salary.getExtraPayProration()));
				cbb.setCommon_cont_base(Optional.ofNullable(salary.getCommonBase()));
				cbb.setProfessional_cont_base(Optional.ofNullable(salary.getProfessionalBase()));
				cbb.setIrpf_retrib_diner(Optional.ofNullable(salary.getIrpfBase()));
				cbb.setIrpf_esp(Optional.ofNullable(salary.getInKindIrpfBase()));
				cbb.setTotal(Optional.ofNullable(salary.getTotalEnterprise()));
				
				
				
				
				Double common_cont_ap_enterprise = 0d;
				Double at_ep_ap_enterprise = 0d;
				Double unemployment_ap_enterprise = 0d;
				Double profes_form_ap_enterprise = 0d;
				Double fogasa_ap_enterprise = 0d;
				Double force_majeure_ap_enterprise = 0d;
				Double no_struct_ap_enterprise = 0d;
				
				for (IDeduction c : costs) {
					
					Double percentD = null;
					try {
						percentD = Double.parseDouble(c.getDescription().replaceAll("\\s", "").replaceAll("%", ""));
					} catch (NumberFormatException e) {}
					
					Optional<Double> percent = Optional.ofNullable(percentD);
					
//					totalEnterprise += (c.getAmount() != null ? c.getAmount() : 0d);
					if (c.getType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal()) {
						common_cont_ap_enterprise += c.getAmount();
						cbb.setCommon_cont_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.PROFESSIONAL_CONTINGENCY.ordinal()) {
						at_ep_ap_enterprise += c.getAmount();
						cbb.setAt_ep_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal()) {
						unemployment_ap_enterprise += c.getAmount();
						cbb.setUnemployment_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.JOB_TRAINING.ordinal()) {
						profes_form_ap_enterprise += c.getAmount();
						cbb.setProfes_form_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.FOGASA.ordinal()) {
						fogasa_ap_enterprise += c.getAmount();
						cbb.setFogasa_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal()) {
						force_majeure_ap_enterprise += c.getAmount();
						cbb.setForce_majeure_type(percent);
					}
					else if (c.getType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal()) {
						no_struct_ap_enterprise += c.getAmount();
						cbb.setNo_struct_type(percent);
					}
					
				}
				
				cbb.setCommon_cont_ap_enterprise(Optional.ofNullable(common_cont_ap_enterprise));
				cbb.setAt_ep_ap_enterprise(Optional.ofNullable(at_ep_ap_enterprise));
				cbb.setUnemployment_ap_enterprise(Optional.ofNullable(unemployment_ap_enterprise));
				cbb.setProfes_form_ap_enterprise(Optional.ofNullable(profes_form_ap_enterprise));
				cbb.setFogasa_ap_enterprise(Optional.ofNullable(fogasa_ap_enterprise));
				cbb.setForce_majeure_ap_enterprise(Optional.ofNullable(force_majeure_ap_enterprise));
				cbb.setNo_struct_ap_enterprise(Optional.ofNullable(no_struct_ap_enterprise));
				
				
				
				dpb.setContingencies(cbb.build());		
			}	
			dpb.setPayroll_total(salary.getTotalLiquid());
			
			Optional<InputStream> optLogo = getSignature(domainName);
			
			
			dpt.print(outputStream, dpb.build(), optLogo, Optional.ofNullable(new Locale("es")));

	}
	
	
	
	
	private static String getDeductionType (Integer type) {
		switch (type) {
			case 0:
				return "CGC";
			case 2:
				return "DESMPL";
			case 3:
				return "FP";
			case 4:
				return "ESTR";
			case 5:
				return "NO_ESTR";
			case 6:
				return "IRPF";
			case 7:
				return "ADELANTO";
			case 8:
				return "EN_ESPECIE";
			case 9:
				return "OTRO";
			case 10:
				return "EMBARGO";
			default:
				return null;
		}
	}

	private static boolean filter (IPayment payment) {
		return !(payment.getAmount() == 0 && !AonStringUtils.equalsIgnoreCase(payment.getName(), ContextVariable.PREST_IT));
	}
	
	private static Optional<InputStream> getSignature(String domainName) {
		
		Optional<InputStream> optLogo = Optional.empty();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, "")) {
			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			if (attach1 != null && attach1.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
		} catch (Exception e) {}
		return optLogo;
	}

}
