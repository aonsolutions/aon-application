
package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionPDFType;
import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionTypeDescription;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases.ContingencyBasesBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.DefaultPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
/**
 * Class containing method/s to print payrolls from a Salary and a SalaryDraft object
 */
public class DraftPayrollBuilder {
	
	private static List<String> PRESTATION_CONCEPTS = Arrays.asList("PREST_IT", "MTNAD", "ERE");
	
	/**
	 * Method to generate a PDF payroll from an ISalary and place it on the OutputStream passed as parameter
	 * @param outputStream The OutputStream on which will be written the PDF
	 * @param salary An ISalary object
	 * @throws CanNotCreatePdfException
	 * @throws SalaryException
	 */
	@SuppressWarnings("static-access")
	public static void generatePayroll (OutputStream outputStream, String domainName, Salary salary) throws CanNotCreatePdfException, SalaryException {
		DefaultPayrollBuilder dpb = new DefaultPayrollBuilder();
			//ENTERPRISE RELATED DATA
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
			
			//EMPLOYEE RELATED DATA
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
			
			//PAYROLL RELATED DATA
			dpb.setLiquidPeriodStart(salary.getStartDate());
			dpb.setTotalDays(salary.getTimeUnits());
			dpb.setLiquidPeriodEnd(getSalaryEnd(salary) );
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
			
			
			//PAYMENTS
			
			/**
			 *  ( EDIT ) To get the extra hours base
			 */
			double nonStructBase[] = new double[]{ 0d };
			double forceMajeureBase[] = new double[]{ 0d };
			
			dpb.setAccrualTotal(salary.getTotalPayment());
			HashMap<Integer, ArrayList<PDFPayment>> paymentMap = new HashMap<Integer, ArrayList<PDFPayment>>();
			Collection<SalaryPayment> payments = salary.getPaymentS();
			payments.stream()
			.filter(DraftPayrollBuilder::filter)
			.sorted(Comparator.comparing(p -> {
				return !(p.getDescription() == null || p.getDescription().isEmpty()) ? p.getDescription() : "zzzzzz"; //Nulls or empties down
			})).forEach(p -> {
				
				String description = p.getDescription().replaceAll("\\[\\d*\\]", "");
				if (description.length() > 50) {
					try {
						description = croppedString(description, 999, PdfFonts.HELVETICA, 9f);
					} catch (IOException ignored) {}	
				}
				
				/**
				 * Getting nonStruct and forceMajeure bases
				 */
				
				//Structural
				if (p.getType() == PaymentType.CRA_0002) {						
					nonStructBase[0] += p.getAmount();
				}
				
				//Force Majeure
				if(p.getType() == PaymentType.CRA_0003) {
					forceMajeureBase[0] += p.getAmount();
				}
				
				
				PDFPayment accrual = new PDFPayment(p.getAmount(), description);
				
				int craKey = p.getType().ordinal();
				if (PaymentType.CRA_0001.equals(p.getType())) {
					//TODO: COMPROBAR PREST_IT, ERE% Y MTNAD
					try {						
						if (PRESTATION_CONCEPTS.contains(p.getName()) || AonStringUtils.equals("ERE_", AonStringUtils.substring(p.getName(), 0, 4))) {
							craKey = 100;
						}
					} catch (Exception e) {
						System.out.println("dd");
					}
				}
				if (!paymentMap.containsKey(craKey)) {
					paymentMap.put(craKey, new ArrayList<PDFPayment>());						
				}
				
				Optional<PDFPayment> repeated = paymentMap.get(craKey).stream().filter(acc -> AonStringUtils.equalsIgnoreCase(p.getDescription(), acc.getDescription().get())).findFirst();
				
				if (repeated.isPresent()) {
					PDFPayment repAcc = repeated.get();
					repAcc.setAmount(repAcc.getAmount().orElse(0d)+p.getAmount());
				} else {
					paymentMap.get(craKey).add(accrual);						
				}
			});
			dpb.setAccruals(paymentMap);
			
			
			//DEDUCTIONS						
			dpb.setDeductionTotal(salary.getTotalDeduction());
			
			ArrayList<String> inserted = new ArrayList<String>();
			
			Collection<SalaryDeduction> deductions = salary.getDeductionS();
			HashMap<Integer, ArrayList<PDFDeduction>> deductionsMap = new HashMap<Integer, ArrayList<PDFDeduction>>();
			deductions.stream().filter(p -> p != null).filter(p -> p.getType() != null).sorted(Comparator.comparing(d -> d.getType().getName(new Locale("es")))).forEach(d -> {
				Double percent = null;
				
				String dataName = "PORCENTAJE_" + d.getName();
				
				if (AonStringUtils.containsIgnoreCase(d.getName(), "fogasa")) {
					dataName = "PORCENTAJE_FOGASA";
				}
				
				try {
					
					
					final String dName = dataName;
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), dName)).findFirst();
					
					if (optPercent.isPresent()) {
						SalaryData data = optPercent.get();
						percent = AonNumberUtils.todouble(data.getExpression());
					} else {
						try {
							if(d.getDescription() != null) {
								String desc = d.getDescription().replaceAll("\\s*(\\d+\\.+\\d+).*","$1");
								percent = Double.parseDouble(desc);
							}
						} catch (NumberFormatException ignored) {
						}
					}
					
				} catch (Exception e) {
				}
				
				int type = getDeductionPDFType(d.getType().ordinal());
				String desc = d.getDescription(); //d.getType().getName(new Locale("es"));
				
				if(desc == null || desc.isEmpty() || desc.matches("\\s*\\d+(\\.\\d+)?\\s*%\\s*")) 
					desc = Optional.ofNullable(PayrollUtils.getDeductionNameDescription(d.getName()))
					.orElseGet( () -> getDeductionTypeDescription(d.getType().ordinal()));
				
				PDFDeduction deduction = new PDFDeduction(d.getAmount(), d.getName(), desc, percent );
				if (!deductionsMap.containsKey(type)) 
					deductionsMap.put(type, new ArrayList<PDFDeduction>());
				
				if (deductionsMap.get(type).stream()
				.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), deduction.getDescription().get()))) 
				{
					PDFDeduction ded = deductionsMap.get(type).stream()
					.filter(p -> AonStringUtils.equalsIgnoreCase(deduction.getDescription().get(),p.getDescription().get()))
					.findFirst()
					.get();
					
					ded.setAmount(ded.getAmount().get() + deduction.getAmount().get());
				} else
					deductionsMap.get(type).add(deduction);
				
				inserted.add(Utilities.getDeductionType(d.getType().ordinal()));
			});
			

			if (deductionsMap.get(1) == null)
				deductionsMap.put(1, new ArrayList<PDFDeduction>());
			if (deductionsMap.get(2) == null)
				deductionsMap.put(2, new ArrayList<PDFDeduction>());
			
			if (!inserted.contains("CGC"))
				deductionsMap.get(1).add(new PDFDeduction(0d, "CGC", "Contingencias Comunes", 0d));
			if (!inserted.contains("DESMPL"))
				deductionsMap.get(1).add(new PDFDeduction(0d, "DESMPL", "Desempleo", 0d));
			if (!inserted.contains("FP"))
				deductionsMap.get(1).add(new PDFDeduction(0d, "FP", "Formación Profesional", 0d));
			if (!inserted.contains("IRPF"))
				deductionsMap.get(2).add(new PDFDeduction(0d, "IRPF", "Retribuciones Dinerarias", 0d));
			if (!inserted.contains("MEI"))
				deductionsMap.get(1).add(new PDFDeduction(0d, "MEI", "Mecanismo de Equidad Intergeneracional (MEI)", 0d, DeductionType.MEI));
			
			
			//EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
			salary.getEmbargoS().forEach(e -> {
				PDFDeduction emb = new PDFDeduction(e.getAmount(), e.getName(), e.getDescription(), null);
				if (deductionsMap.containsKey(5))
					deductionsMap.get(5).add(emb);
				else {
					ArrayList<PDFDeduction> deducts = new ArrayList<PDFDeduction>();
					deducts.add(emb);
					deductionsMap.put(5, deducts);
				}
			});

			dpb.setDeductions(deductionsMap);

			//COSTS
			ContingencyBasesBuilder cbb = new ContingencyBasesBuilder();
			//INITIALIZING CONTINGENCIES, JUST IN CASE
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

			//PICKING THEM UP
			Collection<SalaryCost> costs = salary.getCostS();
			
			cbb.setMonthlyAmount(Optional.ofNullable(salary.getRemuneration()));
			cbb.setExtraProrationAmount(Optional.ofNullable(salary.getExtraPayProration()));
			cbb.setCommonContBase(Optional.ofNullable(salary.getCommonBase()));
			cbb.setProfessionalContBase(Optional.ofNullable(salary.getProfessionalBase()));
			cbb.setIrpfRetribDiner(Optional.ofNullable(salary.getInMoneyIrpfBase()));
			cbb.setIrpfEsp(Optional.ofNullable(salary.getInKindIrpfBase()));
			cbb.setTotal(Optional.ofNullable(salary.getTotalEnterprise()));
			
			Double common_cont_ap_enterprise_percent = 0d;
			Double common_cont_ap_enterprise = 0d;
			Double mei_ap_enterprise = 0d;
			Double at_ep_ap_enterprise = 0d;
			Double unemployment_ap_enterprise = 0d;
			Double profes_form_ap_enterprise = 0d;
			Double fogasa_ap_enterprise = 0d;
			Double force_majeure_ap_enterprise = 0d;
			Double no_struct_ap_enterprise = 0d;
			
			double atEp[] = new double[] {-1,-1};
			
//				for (Entry<String, List<IData>> ent : salary.getDataS().entrySet()) {
//					if (AonStringUtils.containsIgnoreCase(ent.getKey(), "porcentaje")) {
//						System.out.println("|||||" + ent.getKey());
//						try {
//							System.out.println("\t" + ent.getValue().get(0).getValue());
//						} catch (NullPointerException e) {}
//					}
//				}
			for (IDeduction c : costs) {
				
				Double percentD = null;
				try {
					
					String dataName = "PORCENTAJE_" + c.getName();
					
					if (AonStringUtils.containsIgnoreCase(c.getName(), "fogasa")) {
						dataName = "PORCENTAJE_FOGASA";
					}
					
					final String dName = dataName;
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), dName)).findFirst();
					
					if (optPercent.isPresent()) {
						SalaryData data = optPercent.get();
						percentD = AonNumberUtils.todouble(data.getExpression());
					} else {							
						percentD = Double.parseDouble(c.getDescription().replaceAll("\\s*(\\d+\\.+\\d+).*","$1"));
					}
					
				} catch (Exception e) {

				}
				
				Optional<Double> percent = Optional.ofNullable(percentD);
				 
				if (c.getName().equals("CGC_E")) {
					common_cont_ap_enterprise += c.getAmount();
					setPercent(percent, common_cont_ap_enterprise, salary.getCommonBase(), (p) -> cbb.setCommonContType(p));
				}
				else if (c.getName().equals("MEI_E")) {
					mei_ap_enterprise += c.getAmount();
					setPercent(percent, mei_ap_enterprise, salary.getCommonBase(), (p) -> cbb.setMeiType(p));
				}
				else if (c.getName().equals("IMS_E")) {
					at_ep_ap_enterprise += c.getAmount();   
					
					if(percentD != null)
						atEp[0] = percentD; 
					
					String dataName = "TARIFA_IMS";
					
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), dataName)).findFirst();
					
					if (optPercent.isPresent()) {
						SalaryData data = optPercent.get();
						percentD = AonNumberUtils.todouble(data.getExpression());
						common_cont_ap_enterprise_percent += percentD;
						percent = Optional.ofNullable(common_cont_ap_enterprise_percent);
					}
					
					
//					List<IData> percentList = salary.getDataS().getOrDefault(dataName, Collections.emptyList());
//					if (percentList.size() > 0 && percentList.get(0) != null) {
//						IData percentData = percentList.get(0);
//						if (percentData.getValue() != null) {
//							percentD = AonNumberUtils.toDouble(percentData.getValue());
//							common_cont_ap_enterprise_percent += percentD;
//							percent = Optional.ofNullable(common_cont_ap_enterprise_percent);
//						}
//					}
					
					
					setPercent(percent, at_ep_ap_enterprise, salary.getProfessionalBase(), (p) -> cbb.setAtEpType(p));
				}
				else if(c.getName().equals("IT_E")) {
					at_ep_ap_enterprise += c.getAmount();
					
					if(percentD != null)
						atEp[1] = percentD;
					
					String dataName = "TARIFA_IT";
					
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), dataName)).findFirst();
					
					if (optPercent.isPresent()) {
						SalaryData data = optPercent.get();
						percentD = AonNumberUtils.todouble(data.getExpression());
						common_cont_ap_enterprise_percent += percentD;
						percent = Optional.ofNullable(common_cont_ap_enterprise_percent);
					}
					
//					List<IData> percentList = salary.getDataS().getOrDefault(dataName, Collections.emptyList());
//					if (percentList.size() > 0 && percentList.get(0) != null) {
//						IData percentData = percentList.get(0);
//						if (percentData.getValue() != null) {
//							percentD = AonNumberUtils.toDouble(percentData.getValue());
//							common_cont_ap_enterprise_percent += percentD;
//							percent = Optional.ofNullable(common_cont_ap_enterprise_percent);
//						}
//					}
					
					setPercent(percent, at_ep_ap_enterprise, salary.getProfessionalBase(), (p) -> cbb.setAtEpType(p));
				}
				else if (c.getName().equals("DESMPL_E")) {
					unemployment_ap_enterprise += c.getAmount();
					setPercent(percent, unemployment_ap_enterprise, salary.getProfessionalBase(), (p) -> cbb.setUnemploymentType(p));
				}
				else if (c.getName().equals("FP_E")) {
					profes_form_ap_enterprise += c.getAmount();
					setPercent(percent, profes_form_ap_enterprise, salary.getProfessionalBase(), (p) -> cbb.setProfesFormType(p));
				}
				else if (c.getName().equals("FOGASA_E")) { 
					fogasa_ap_enterprise += c.getAmount();
					
					if(percentD == null){
						
						//Saving life here... :v
						double professionalBase = salary.getProfessionalBase() == null ? 0 : salary.getProfessionalBase();
						if(professionalBase == 0) 
							professionalBase = 1;
							
						double value = fogasa_ap_enterprise / professionalBase * 100;
						percent = Optional.of(value);
						cbb.setFogasaType(percent);
					} else {
						setPercent(percent, fogasa_ap_enterprise, salary.getProfessionalBase(), (p) -> cbb.setFogasaType(p));
					}
					
				}
				else if (c.getName().equals("EXTR_E")) {
					force_majeure_ap_enterprise += c.getAmount();
					setPercent(percent, force_majeure_ap_enterprise, salary.getOvertimeBase(), (p) -> cbb.setForceMajeureType(p));						
				}
				else if (c.getName().equals("NEXTR_E")) {
					no_struct_ap_enterprise += c.getAmount();
					setPercent(percent, no_struct_ap_enterprise, salary.getNonEstructuralOvertimeBase(), (p) -> cbb.setNoStructType(p));
				}					
			}
			
			if(atEp[0] != -1 || atEp[1] != -1)
				cbb.setAtEpType(Optional.ofNullable(atEp[0] + atEp[1] == -2 ? -1 : atEp[0] + atEp[1]));
			
			cbb.setCommonContApEnterprise(Optional.ofNullable(common_cont_ap_enterprise));
			cbb.setMeiApEnterprise(Optional.ofNullable(mei_ap_enterprise));
			cbb.setAtEpApEnterprise(Optional.ofNullable(at_ep_ap_enterprise));
			cbb.setUnemploymentApEnterprise(Optional.ofNullable(unemployment_ap_enterprise));
			cbb.setProfesFormApEnterprise(Optional.ofNullable(profes_form_ap_enterprise));
			cbb.setFogasaApEnterprise(Optional.ofNullable(fogasa_ap_enterprise));
			cbb.setForceMajeureApEnterprise(Optional.ofNullable(force_majeure_ap_enterprise));
			cbb.setNoStructApEnterprise(Optional.ofNullable(no_struct_ap_enterprise));
			
			cbb.setNoStructBase(Optional.of(nonStructBase[0]));
			cbb.setForceMajeureBase(Optional.of(forceMajeureBase[0]));
			
			dpb.setContingencies(cbb.build());
			
			dpb.setPayrollTotal(salary.getTotalLiquid());
			
			Optional<InputStream> optLogo = Utilities.getSignature(domainName);
			PayrollTemplate dpt = new PayrollTemplate(dpb.build(), optLogo, Optional.ofNullable(new Locale("es")));
			dpt.print(outputStream);
	}

	@FunctionalInterface
	private interface SetPercentCallback {
		void setPercent(Optional<Double> percent);
	}
	
	private static void setPercent(Optional<Double> percent, Double amount, Double base, SetPercentCallback callback) {
		if (percent.isPresent() && AonNumberUtils.zeroIfNull(percent.get()) != 0) {
			callback.setPercent(percent);
		} /*else {
			double cost = AonNumberUtils.zeroIfNull(amount);
			double safeBase = AonNumberUtils.zeroIfNull(base);
			if (safeBase != 0) {
				callback.setPercent(Optional.ofNullable(cost / safeBase * 100));
			}
		}*/
	}
	
	private static boolean filter(SalaryPayment payment) {
		List<String> excludedConcepts = Arrays.asList("PREST_IT");
		List<String> excludedDescriptionWords = Arrays.asList("vacaciones");
		
		return !(payment.getAmount() == 0 && payment.getQuote() == 0)
				|| excludedConcepts.contains(payment.getName())
				|| excludedDescriptionWords.stream().anyMatch(word -> AonStringUtils.containsIgnoreCase(payment.getDescription(), word));
	}

//	private static boolean filter (SalaryPayment payment) {
//		payment.get
//		//Igual que el de JooqPayrollBuilder
//		return !(payment.getAmount() == 0 && payment.getQuote() == 0);
////		return !(payment.getAmount() == 0 && !AonStringUtils.equalsIgnoreCase(payment.getName(), ContextVariable.PREST_IT));
//	}
	
	
	private static Date getSalaryEnd(Salary salary) {
	    return salary.getSalaryDatas().stream()
		    .filter( d -> d.getName().equals(WORKED_DAYS.getName()))
		    .map( SalaryData::getEndDate )
		    .collect(Collectors.maxBy(Date::compareTo))
		    .orElse(salary.getEndDate());
	}
	


}
