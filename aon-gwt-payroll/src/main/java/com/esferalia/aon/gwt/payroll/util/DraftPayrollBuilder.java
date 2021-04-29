package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionPDFType;
import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionTypeDescription;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION.DEFAULT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION;
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
		PayrollTemplate dpt = new PayrollTemplate();
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
		
			//ENTERPRISE RELATED DATA
			{
				dpb.setCcc(salary.getCcc());
				dpb.setCif(salary.getEnterpriseDocument());
				dpb.setEnterprise(salary.getEnterpriseName());
			
				//ADDRESS FITTING
				if (salary.getEnterpriseAddress() != null) {
					
					Pattern zipPattern = Pattern.compile("(?<lineone>.*?)\\s*(?<zip>\\([^\\)\\(]*?\\))\\s*(?<city>.*)", Pattern.CASE_INSENSITIVE);
					Matcher matcher = zipPattern.matcher(salary.getEnterpriseAddress());
					if (matcher.matches()) {
						dpb.setAddress(matcher.group("lineone"));
						String line2 = matcher.group("zip") + " " + matcher.group("city");
						if (line2 != null)
							line2 = line2.replaceAll("\\(", "").replaceAll("\\)", "");
						dpb.setAddress2(line2);
					} else {
						List<String> address = null;
						if (salary.getEnterpriseAddress()!=null)
							address = Arrays.asList(Utilities.separateString(salary.getEnterpriseAddress(), 40));
						if (address != null && address.size() > 1) {
							dpb.setAddress(address.get(0) != null ? address.get(0).trim() : null);
							dpb.setAddress2(address.get(1));
						} else {
							dpb.setAddress(salary.getEnterpriseAddress());
						}
					}					
				}
			}
			//EMPLOYEE RELATED DATA
			{
				dpb.setAntiquity(salary.getSeniorityDate());
				dpb.setNif(salary.getEmployeeDocument());
				dpb.setNss(salary.getSocialSecurityNumber());
				//weird names check
				String employeeName = salary.getEmployeeName().trim();
				if (employeeName != null && employeeName.length() > 1 && employeeName.charAt(0) == ',')
					employeeName = employeeName.substring(1).trim();
				dpb.setEmployee(employeeName);
				dpb.setQuotationGroup(salary.getQuoteGroup());
				dpb.setProfessionalGroup(salary.getCategory());
			}
			//PAYROLL RELATED DATA
			{
				dpb.setLiquidPeriodStart(salary.getStartDate());
				dpb.setLiquidPeriodEnd(salary.getEndDate());
				dpb.setTotalDays(salary.getTimeUnits());
				dpb.setImpressionType(IMPRESION.DRAFT);
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
				
				dpb.setAccrualTotal(salary.getTotalPayment());
				HashMap<Integer, ArrayList<PDFPayment>> paymentMap = new HashMap<Integer, ArrayList<PDFPayment>>();
				Collection<IPayment> payments = salary.getPaymentS();
				payments.stream().filter(DraftPayrollBuilder::filter).sorted(Comparator.comparing(p -> {
					return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; //Nulls or empties down
				})).forEach(p -> {
					
					String description = p.getDescription().replaceAll("\\[\\d*\\]", "");
					if (description.length() > 50) {
						try {
							description = PDFToolkit.croppedString(description, 999, PdfFonts.HELVETICA, 9f);
							
						} catch (IOException ignored) {}	
					}
					
					
					PDFPayment accrual = new PDFPayment(p.getAmount(), description);
					if (!paymentMap.containsKey(p.getType().ordinal()))
						paymentMap.put(p.getType().ordinal(), new ArrayList<PDFPayment>());
					 
					if (paymentMap.get(p.getType().ordinal()).stream().anyMatch(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get()))) {
						PDFPayment repAcc = paymentMap.get(p.getType().ordinal()).stream().findFirst().get();
						repAcc.setAmount(repAcc.getAmount().orElse(0d)+p.getAmount());
					} else
						paymentMap.get(p.getType().ordinal()).add(accrual);
				});
				dpb.setAccruals(paymentMap);
			}
			//DEDUCTIONS
			{
				dpb.setDeductionTotal(salary.getTotalDeduction());
				
				ArrayList<String> inserted = new ArrayList<String>();
				
				Collection<IDeduction> deductions = salary.getDeductionS();
				HashMap<Integer, ArrayList<PDFDeduction>> deductionsMap = new HashMap<Integer, ArrayList<PDFDeduction>>();
				deductions.stream().filter(p -> p.getType() != null).sorted(Comparator.comparing(d -> d.getType().getName(new Locale("es")))).forEach(d -> {
					Double percent = null;
					System.out.println("(DraftPayrollBuilder :: 155) " + d.getDescription() + " : " + d.getAmount() + " : INDEDUCTION " + d.getName());
					
					try {
						String desc = d.getDescription().replaceAll(".*(\\d+\\.+\\d+).*","$1");
						System.out.println("(DraftPayrollBulder :: 159) " + desc);
						percent = Double.parseDouble(desc);
					} catch (NumberFormatException ignored) {}
					
					int type = getDeductionPDFType(d.getType().ordinal());
					String desc = d.getType().getName(new Locale("es"));
					
					if(desc == null || desc.isEmpty()) 
						desc = getDeductionTypeDescription(d.getType().ordinal());
					
					PDFDeduction deduction = new PDFDeduction(d.getAmount(), desc, percent);
					
					
					if (!deductionsMap.containsKey(type))
						deductionsMap.put(type, new ArrayList<PDFDeduction>());
					
					if (deductionsMap.get(type).stream()
							.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), deduction.getDescription().get()))) {
						PDFDeduction ded = deductionsMap.get(type).stream()
								.filter(p -> AonStringUtils.equalsIgnoreCase(deduction.getDescription().get(),p.getDescription().get())).findFirst().get();
						ded.setAmount(ded.getAmount().get()+deduction.getAmount().get());
					} else
						deductionsMap.get(type).add(deduction);
					inserted.add(Utilities.getDeductionType(d.getType().ordinal()));
				});
				

				if (deductionsMap.get(1)==null)
					deductionsMap.put(1, new ArrayList<PDFDeduction>());
				if (deductionsMap.get(2)==null)
					deductionsMap.put(2, new ArrayList<PDFDeduction>());
				
				if (!inserted.contains("CGC"))
					deductionsMap.get(1).add(new PDFDeduction(0d, "Contingencias comunes", 0d));
				if (!inserted.contains("DESMPL"))
					deductionsMap.get(1).add(new PDFDeduction(0d, "Desempleo", 0d));
				if (!inserted.contains("FP"))
					deductionsMap.get(1).add(new PDFDeduction(0d, "Formación profesional", 0d));
				if (!inserted.contains("IRPF"))
					deductionsMap.get(2).add(new PDFDeduction(0d, "Retribuciones dinerarias", 0d));
				
				
				//EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
				{
					salary.getEmbargoS().forEach(e -> {
						PDFDeduction emb = new PDFDeduction(e.getAmount(), e.getDescription(), null);
						if (deductionsMap.containsKey(5))
							deductionsMap.get(5).add(emb);
						else {
							ArrayList<PDFDeduction> deducts = new ArrayList<PDFDeduction>();
							deducts.add(emb);
							deductionsMap.put(5, deducts);
						}
					});
				}
				

				dpb.setDeductions(deductionsMap);
			}
			//COSTS
			{
				ContingencyBasesBuilder cbb = new ContingencyBasesBuilder();
				//INITIALIZING CONTINGENCIES, JUST IN CASE
				{
					cbb.setAtEpApEnterprise(Optional.empty());
					cbb.setAtEpType(Optional.empty());
					cbb.setCommonContApEnterprise(Optional.empty());
					cbb.setCommonContBase(Optional.empty());
					cbb.setCommonContType(Optional.empty());
					cbb.setExtraProrationAmount(Optional.empty());
					cbb.setFogasaApEnterprise(Optional.empty());
					cbb.setFogasaType(Optional.empty());
					cbb.setForceMajeureApEnterprise(Optional.empty());
					cbb.setForceMajeureBase(Optional.empty());
					cbb.setForceMajeureType(Optional.empty());
					cbb.setIrpfEsp(Optional.empty());
					cbb.setIrpfRetribDiner(Optional.empty());
					cbb.setMonthlyAmount(Optional.empty());
					cbb.setNoStructApEnterprise(Optional.empty());
					cbb.setNoStructBase(Optional.empty());
					cbb.setNoStructType(Optional.empty());
					cbb.setProfesFormApEnterprise(Optional.empty());
					cbb.setProfesFormType(Optional.empty());
					cbb.setProfessionalContBase(Optional.empty());
					cbb.setTotal(Optional.empty());
					cbb.setUnemploymentApEnterprise(Optional.empty());
					cbb.setUnemploymentType(Optional.empty());
				}
				//PICKING THEM UP
				Collection<IDeduction> costs = salary.getCostS();
				
				cbb.setMonthlyAmount(Optional.ofNullable(salary.getRemuneration()));
				cbb.setExtraProrationAmount(Optional.ofNullable(salary.getExtraPayProration()));
				cbb.setCommonContBase(Optional.ofNullable(salary.getCommonBase()));
				cbb.setProfessionalContBase(Optional.ofNullable(salary.getProfessionalBase()));
				cbb.setIrpfRetribDiner(Optional.ofNullable(salary.getIrpfBase()));
				cbb.setIrpfEsp(Optional.ofNullable(salary.getInKindIrpfBase()));
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
					
					if (c.getType().ordinal() == DeductionType.COMMON_CONTINGENCY.ordinal()) {
						common_cont_ap_enterprise += c.getAmount();
						cbb.setCommonContType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.PROFESSIONAL_CONTINGENCY.ordinal()) {
						at_ep_ap_enterprise += c.getAmount();
						cbb.setAtEpType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.UNEMPLOYMENT.ordinal()) {
						unemployment_ap_enterprise += c.getAmount();
						cbb.setUnemploymentType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.JOB_TRAINING.ordinal()) {
						profes_form_ap_enterprise += c.getAmount();
						cbb.setProfesFormType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.FOGASA.ordinal()) {
						fogasa_ap_enterprise += c.getAmount();
						cbb.setFogasaType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.STRUCTURAL_OVERTIME.ordinal()) {
						force_majeure_ap_enterprise += c.getAmount();
						cbb.setForceMajeureType(percent);
					}
					else if (c.getType().ordinal() == DeductionType.NON_STRUCTURAL_OVERTIME.ordinal()) {
						no_struct_ap_enterprise += c.getAmount();
						cbb.setNoStructType(percent);
					}
					
				}
				
				cbb.setCommonContApEnterprise(Optional.ofNullable(common_cont_ap_enterprise));
				cbb.setAtEpApEnterprise(Optional.ofNullable(at_ep_ap_enterprise));
				cbb.setUnemploymentApEnterprise(Optional.ofNullable(unemployment_ap_enterprise));
				cbb.setProfesFormApEnterprise(Optional.ofNullable(profes_form_ap_enterprise));
				cbb.setFogasaApEnterprise(Optional.ofNullable(fogasa_ap_enterprise));
				cbb.setForceMajeureApEnterprise(Optional.ofNullable(force_majeure_ap_enterprise));
				cbb.setNoStructApEnterprise(Optional.ofNullable(no_struct_ap_enterprise));
				
				
				
				dpb.setContingencies(cbb.build());		
			}	
			dpb.setPayrollTotal(salary.getTotalLiquid());
			
			Optional<InputStream> optLogo = Utilities.getSignature(domainName);
			
			
			dpt.print(outputStream, dpb.build(), optLogo, Optional.ofNullable(new Locale("es")));

	}
	
	
	

	private static boolean filter (IPayment payment) {
		return !(payment.getAmount() == 0 && !AonStringUtils.equalsIgnoreCase(payment.getName(), ContextVariable.PREST_IT));
	}
	
	


}
