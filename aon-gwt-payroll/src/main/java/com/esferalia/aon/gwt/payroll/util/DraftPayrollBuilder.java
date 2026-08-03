
package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionPDFType;
import static com.esferalia.aon.gwt.payroll.util.PayrollUtils.getDeductionTypeDescription;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NO_HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.UNPAID;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate;
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
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
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
		DefaultPayrollBuilder defaulPayrollBuilder = new DefaultPayrollBuilder();
			//ENTERPRISE RELATED DATA
			defaulPayrollBuilder.setCcc(salary.getCcc());
			defaulPayrollBuilder.setCif(salary.getEnterpriseDocument());
			defaulPayrollBuilder.setEnterprise(salary.getEnterpriseName());
		
			//ADDRESS FITTING
			if (salary.getEnterpriseAddress() != null) {
				
				Pattern zipPattern = Pattern.compile("(?<lineone>.*?)\\s*(?<zip>\\([^\\)\\(]*?\\))\\s*(?<city>.*)", Pattern.CASE_INSENSITIVE);
				Matcher matcher = zipPattern.matcher(salary.getEnterpriseAddress());
				if (matcher.matches()) {
					defaulPayrollBuilder.setAddress(matcher.group("lineone"));
					String line2 = matcher.group("zip") + " " + matcher.group("city");
					if (line2 != null)
						line2 = line2.replaceAll("\\(", "").replaceAll("\\)", "");
					defaulPayrollBuilder.setAddress2(line2);
				} else {
					List<String> address = null;
					if (salary.getEnterpriseAddress()!=null)
						address = Arrays.asList(Utilities.separateString(salary.getEnterpriseAddress(), 40));
					if (address != null && address.size() > 1) {
						defaulPayrollBuilder.setAddress(address.get(0) != null ? address.get(0).trim() : null);
						defaulPayrollBuilder.setAddress2(address.get(1));
					} else {
						defaulPayrollBuilder.setAddress(salary.getEnterpriseAddress());
					}
				}					
			}
			
			//EMPLOYEE RELATED DATA
			defaulPayrollBuilder.setAntiquity(salary.getSeniorityDate());
			defaulPayrollBuilder.setNif(salary.getEmployeeDocument());
			defaulPayrollBuilder.setNss(salary.getSocialSecurityNumber());
			//weird names check
			String employeeName = salary.getEmployeeName().trim();
			if (employeeName != null && employeeName.length() > 1 && employeeName.charAt(0) == ',')
				employeeName = employeeName.substring(1).trim();
			defaulPayrollBuilder.setEmployee(employeeName);
			defaulPayrollBuilder.setQuotationGroup(salary.getQuoteGroup());
			defaulPayrollBuilder.setProfessionalGroup(salary.getCategory());
			
			//PAYROLL RELATED DATA
			defaulPayrollBuilder.setReceivedDate(salary.getChargeDate());
			defaulPayrollBuilder.setLiquidPeriodStart(salary.getStartDate());
			defaulPayrollBuilder.setTotalDays(salary.getTimeUnits());
			defaulPayrollBuilder.setLiquidPeriodEnd(getSalaryEnd(salary) );
			defaulPayrollBuilder.setImpressionType(IMPRESION.DRAFT);
			if (salary.getType().ordinal() == SalaryType.SALARY.ordinal())
				defaulPayrollBuilder.setPayrollType(PayrollTypes.Type.SALARY);
			else if (salary.getType().ordinal() == SalaryType.EXTRA.ordinal())
				defaulPayrollBuilder.setPayrollType(PayrollTypes.Type.EXTRAS);
			else if (salary.getType().ordinal() == SalaryType.SETTLE.ordinal())
				defaulPayrollBuilder.setPayrollType(PayrollTypes.Type.SETTLEMENT);
			else if (salary.getType().ordinal() == SalaryType.DELAY.ordinal())
				defaulPayrollBuilder.setPayrollType(PayrollTypes.Type.ARREARS_WAGE);
			else
				defaulPayrollBuilder.setPayrollType(PayrollTypes.Type.SALARY);
			
			
			//PAYMENTS
			
			/**
			 *  ( EDIT ) To get the extra hours base
			 */
			double nonStructBase[] = new double[]{ 0d };
			double forceMajeureBase[] = new double[]{ 0d };
			
			defaulPayrollBuilder.setAccrualTotal(salary.getTotalPayment());
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
						description = croppedString(description, 999, PdfFonts.helvetica(), 9f);
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
				if ( Objects.equals(ContextVariable.PPE, p.getName())) {
				    craKey  = IPayrollTemplate.PPE;
				    accrual.setAmount(p.getQuote() >= 0.00 ? p.getQuote() : p.getAmount());
				} else if (Objects.equals(ContextVariable.FLEXIBLE_DISCOUNT, p.getName())) {
					craKey = IPayrollTemplate.FLEXIBLE;
					accrual.setAmount(p.getQuote() > 0 ? p.getQuote() : p.getAmount());
				} else if ( Objects.equals(ContextVariable.NOTE, p.getName())) {
				    craKey  = IPayrollTemplate.NOTE;
				} else if ( Objects.equals(ContextVariable.INFO, p.getName())) {
				    craKey  = IPayrollTemplate.INFO;
				} else if ( Objects.equals(ContextVariable.CAUTION, p.getName())) {
				    craKey  = IPayrollTemplate.WARNING;
				} else if (PaymentType.CRA_0001.equals(p.getType())) {
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
			defaulPayrollBuilder.setAccruals(paymentMap);
			
			
			//DEDUCTIONS						
			defaulPayrollBuilder.setDeductionTotal(salary.getTotalDeduction());
			
			ArrayList<String> inserted = new ArrayList<String>();
			
			Collection<SalaryDeduction> deductions = salary.getDeductionS();
			HashMap<Integer, ArrayList<PDFDeduction>> pdfDeductionsMap = new HashMap<>();
			deductions.stream().filter(p -> p != null).filter(p -> p.getType() != null).sorted(Comparator.comparing(d -> d.getType().getName(new Locale("es")))).forEach(d -> {
				Double percent = null;
				
				String deductionName = d.getName();
				deductionName = AonStringUtils.replaceOnce(deductionName, "EXCESS_", "");
				
				
				String percentName = "PORCENTAJE_" + deductionName;
				
				if (AonStringUtils.containsIgnoreCase(deductionName, "fogasa")) {
					percentName = "PORCENTAJE_FOGASA";
				}
				

				try {
					
					
					final String finalPercentName = percentName;
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), finalPercentName)).findFirst();
					
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
				
				Double base = null;
				String baseName = "BASE_" + deductionName;

				try {
					
					
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optBase = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), baseName)).findFirst();
					
					if (optBase.isPresent()) {
						SalaryData data = optBase.get();
						base = AonNumberUtils.todouble(data.getExpression());
					} 
					
				} catch (Exception e) {
				}

				int pdfType = getDeductionPDFType(d.getType().ordinal());
				String description = d.getDescription();
				
				if(description == null || description.isEmpty() || description.matches("\\s*\\d+(\\.\\d+)?\\s*%\\s*")) 
					description = Optional.ofNullable(PayrollUtils.getDeductionNameDescription(deductionName))
					.orElseGet( () -> getDeductionTypeDescription(d.getType().ordinal()));
				
				DeductionType deductionType = AonEnumUtils.enumValue(DeductionType.class, d.getType().ordinal() );
				PDFDeduction pdfDeduction = new PDFDeduction(d.getAmount(), deductionName, description, percent, base, deductionType );
				if (!pdfDeductionsMap.containsKey(pdfType)) 
					pdfDeductionsMap.put(pdfType, new ArrayList<>());
				
				if (pdfDeductionsMap.get(pdfType).stream()
				.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), pdfDeduction.getDescription().get()))) 
				{
					PDFDeduction ded = pdfDeductionsMap.get(pdfType).stream()
					.filter(p -> AonStringUtils.equalsIgnoreCase(pdfDeduction.getDescription().get(),p.getDescription().get()))
					.findFirst()
					.get();
					
					ded.setAmount(ded.getAmount().get() + pdfDeduction.getAmount().get());
				} else
					pdfDeductionsMap.get(pdfType).add(pdfDeduction);
				
				inserted.add(Utilities.getDeductionType(d.getType().ordinal()));
			});
			

			if (pdfDeductionsMap.get(1) == null)
				pdfDeductionsMap.put(1, new ArrayList<PDFDeduction>());
			if (pdfDeductionsMap.get(2) == null)
				pdfDeductionsMap.put(2, new ArrayList<PDFDeduction>());
			
			if (!inserted.contains("CGC"))
				pdfDeductionsMap.get(1).add(new PDFDeduction(0d, "CGC", "Contingencias Comunes", 0d));
			if (!inserted.contains("DESMPL"))
				pdfDeductionsMap.get(1).add(new PDFDeduction(0d, "DESMPL", "Desempleo", 0d));
			if (!inserted.contains("FP"))
				pdfDeductionsMap.get(1).add(new PDFDeduction(0d, "FP", "Formación Profesional", 0d));
			if (!inserted.contains("IRPF"))
				pdfDeductionsMap.get(2).add(new PDFDeduction(0d, "IRPF", "Retribuciones Dinerarias", 0d));
			if (!inserted.contains("MEI"))
				pdfDeductionsMap.get(1).add(new PDFDeduction(0d, "MEI", "Mecanismo de Equidad Intergeneracional (MEI)", 0d, DeductionType.MEI));
			
			
			//EMBARGOS (placed at 'Other deductions' -type 5- field on 'Deductions')
			salary.getEmbargoS().forEach(e -> {
				PDFDeduction emb = new PDFDeduction(e.getAmount(), e.getName(), e.getDescription(), null);
				if (pdfDeductionsMap.containsKey(5))
					pdfDeductionsMap.get(5).add(emb);
				else {
					ArrayList<PDFDeduction> deducts = new ArrayList<PDFDeduction>();
					deducts.add(emb);
					pdfDeductionsMap.put(5, deducts);
				}
			});

			defaulPayrollBuilder.setDeductions(pdfDeductionsMap);

			//COSTS
			ContingencyBasesBuilder contingencyBasesBuilder = new ContingencyBasesBuilder();
			//INITIALIZING CONTINGENCIES, JUST IN CASE
			contingencyBasesBuilder.setAtEpApEnterprise(Optional.empty());
			contingencyBasesBuilder.setAtEpType(Optional.empty());
			contingencyBasesBuilder.setCommonContApEnterprise(Optional.empty());
			contingencyBasesBuilder.setCommonContBase(Optional.empty());
			contingencyBasesBuilder.setCommonContType(Optional.empty());
			contingencyBasesBuilder.setExtraProrationAmount(Optional.empty());
			contingencyBasesBuilder.setFogasaApEnterprise(Optional.empty());
			contingencyBasesBuilder.setFogasaType(Optional.empty());
			contingencyBasesBuilder.setForceMajeureApEnterprise(Optional.empty());
			contingencyBasesBuilder.setForceMajeureBase(Optional.empty());
			contingencyBasesBuilder.setForceMajeureType(Optional.empty());
			contingencyBasesBuilder.setIrpfEsp(Optional.empty());
			contingencyBasesBuilder.setIrpfRetribDiner(Optional.empty());
			contingencyBasesBuilder.setMonthlyAmount(Optional.empty());
			contingencyBasesBuilder.setNoStructApEnterprise(Optional.empty());
			contingencyBasesBuilder.setNoStructBase(Optional.empty());
			contingencyBasesBuilder.setNoStructType(Optional.empty());
			contingencyBasesBuilder.setProfesFormApEnterprise(Optional.empty());
			contingencyBasesBuilder.setProfesFormType(Optional.empty());
			contingencyBasesBuilder.setProfessionalContBase(Optional.empty());
			contingencyBasesBuilder.setTotal(Optional.empty());
			contingencyBasesBuilder.setUnemploymentApEnterprise(Optional.empty());
			contingencyBasesBuilder.setUnemploymentType(Optional.empty());

			//PICKING THEM UP
			Collection<SalaryCost> costs = salary.getCostS();
			
			contingencyBasesBuilder.setMonthlyAmount(Optional.ofNullable(salary.getRemuneration()));
			contingencyBasesBuilder.setExtraProrationAmount(Optional.ofNullable(salary.getExtraPayProration()));
			contingencyBasesBuilder.setCommonContBase(Optional.ofNullable(salary.getCommonBase()));
			contingencyBasesBuilder.setProfessionalContBase(Optional.ofNullable(salary.getProfessionalBase()));
			contingencyBasesBuilder.setIrpfRetribDiner(Optional.ofNullable(salary.getInMoneyIrpfBase()));
			contingencyBasesBuilder.setIrpfEsp(Optional.ofNullable(salary.getInKindIrpfBase()));
			contingencyBasesBuilder.setTotal(Optional.ofNullable(salary.getTotalEnterprise()));
			
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

			HashMap<Integer, ArrayList<PDFDeduction>> pdfCostsMap = new HashMap<>();
			for (IDeduction cost : costs) {
				
				String costName = cost.getName();
				costName = AonStringUtils.replaceOnce(costName, "EXCESS_", "");

				Double percentD = null;
				try {
					
					String dataName = "PORCENTAJE_" + costName;
					
					if (AonStringUtils.containsIgnoreCase(costName, "fogasa")) {
						dataName = "PORCENTAJE_FOGASA";
					}
					
					final String dName = dataName;
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optPercent = datas.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), dName)).findFirst();
					
					if (optPercent.isPresent()) {
						SalaryData data = optPercent.get();
						percentD = AonNumberUtils.todouble(data.getExpression());
					} else {							
						percentD = Double.parseDouble(cost.getDescription().replaceAll("\\s*(\\d+\\.+\\d+).*","$1"));
					}
					
				} catch (Exception e) {

				}
				
				Optional<Double> percent = Optional.ofNullable(percentD);
				 
				Double baseD = null;
				
				try {
					
					String baseNameE = "BASE_" + costName;
					String baseName = AonStringUtils.removeEnd(baseNameE, "_E");
					Set<SalaryData> datas = salary.getSalaryDatas();
					Optional<SalaryData> optBase = 
					datas.stream()
					.filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), baseNameE) || AonStringUtils.equalsIgnoreCase(data.getName(), baseName) )
					.sorted( (c1, c2) -> AonStringUtils.compare(c2.getName(), c1.getName()) ).findFirst();
					
					if (optBase.isPresent()) {
						SalaryData data = optBase.get();
						baseD = AonNumberUtils.todouble(data.getExpression());
					} 
					
				} catch (Exception e) {
				}
				
				Optional<Double> base = Optional.ofNullable(baseD);
				

				if (costName.equals("CGC_E")) {
					common_cont_ap_enterprise += cost.getAmount();
					setPercent(percent, common_cont_ap_enterprise, salary.getCommonBase(), (p) -> contingencyBasesBuilder.setCommonContType(p));
				}
				else if (costName.equals("MEI_E")) {
					mei_ap_enterprise += cost.getAmount();
					setPercent(percent, mei_ap_enterprise, salary.getCommonBase(), (p) -> contingencyBasesBuilder.setMeiType(p));
				}
				else if (costName.equals("IMS_E")) {
					at_ep_ap_enterprise += cost.getAmount();   
					
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
					
					
					setPercent(percent, at_ep_ap_enterprise, salary.getProfessionalBase(), (p) -> contingencyBasesBuilder.setAtEpType(p));
				}
				else if(costName.equals("IT_E")) {
					at_ep_ap_enterprise += cost.getAmount();
					
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
					
					setPercent(percent, at_ep_ap_enterprise, salary.getProfessionalBase(), (p) -> contingencyBasesBuilder.setAtEpType(p));
				}
				else if (costName.equals("DESMPL_E")) {
					unemployment_ap_enterprise += cost.getAmount();
					setPercent(percent, unemployment_ap_enterprise, salary.getProfessionalBase(), (p) -> contingencyBasesBuilder.setUnemploymentType(p));
				}
				else if (costName.equals("FP_E")) {
					profes_form_ap_enterprise += cost.getAmount();
					setPercent(percent, profes_form_ap_enterprise, salary.getProfessionalBase(), (p) -> contingencyBasesBuilder.setProfesFormType(p));
				}
				else if (costName.equals("FOGASA_E")) { 
					fogasa_ap_enterprise += cost.getAmount();
					
					if(percentD == null){
						
						//Saving life here... :v
						double professionalBase = salary.getProfessionalBase() == null ? 0 : salary.getProfessionalBase();
						if(professionalBase == 0) 
							professionalBase = 1;
							
						double value = fogasa_ap_enterprise / professionalBase * 100;
						percent = Optional.of(value);
						contingencyBasesBuilder.setFogasaType(percent);
					} else {
						setPercent(percent, fogasa_ap_enterprise, salary.getProfessionalBase(), (p) -> contingencyBasesBuilder.setFogasaType(p));
					}
					
				}
				else if (costName.equals("EXTR_E")) {
					force_majeure_ap_enterprise += cost.getAmount();
					setPercent(percent, force_majeure_ap_enterprise, salary.getOvertimeBase(), (p) -> contingencyBasesBuilder.setForceMajeureType(p));						
				}
				else if (costName.equals("NEXTR_E")) {
					no_struct_ap_enterprise += cost.getAmount();
					setPercent(percent, no_struct_ap_enterprise, salary.getNonEstructuralOvertimeBase(), (p) -> contingencyBasesBuilder.setNoStructType(p));
				}					

				if ( cost.getType() != null  ) {
					int pdfType = getDeductionPDFType(cost.getType().ordinal());
					DeductionType costType = AonEnumUtils.enumValue(DeductionType.class, cost.getType().ordinal() );
					PDFDeduction pdfCost = new PDFDeduction(cost.getAmount(), costName, cost.getDescription(), percent.orElse(0d), base.orElse(0d), costType );
					if (!pdfCostsMap.containsKey(pdfType)) 
						pdfCostsMap.put(pdfType, new ArrayList<>());
					
					if (pdfCostsMap.get(pdfType).stream()
					.anyMatch(p -> AonStringUtils.equalsIgnoreCase(p.getDescription().get(), pdfCost.getDescription().get()))) 
					{
						pdfCostsMap.get(pdfType).stream()
						.filter(p -> AonStringUtils.equalsIgnoreCase(pdfCost.getDescription().orElse(""),p.getDescription().orElse("")))
						.findFirst().ifPresent(prevPdfCost -> prevPdfCost.setAmount(prevPdfCost.getAmount().get() + pdfCost.getAmount().get()));
					} else {
						pdfCostsMap.get(pdfType).add(pdfCost);
					}
				}
			
			}
			
			defaulPayrollBuilder.setCosts(pdfCostsMap);
			
			if(atEp[0] != -1 || atEp[1] != -1)
				contingencyBasesBuilder.setAtEpType(Optional.ofNullable(atEp[0] + atEp[1] == -2 ? -1 : atEp[0] + atEp[1]));
			
			contingencyBasesBuilder.setCommonContApEnterprise(Optional.ofNullable(common_cont_ap_enterprise));
			contingencyBasesBuilder.setMeiApEnterprise(Optional.ofNullable(mei_ap_enterprise));
			contingencyBasesBuilder.setAtEpApEnterprise(Optional.ofNullable(at_ep_ap_enterprise));
			contingencyBasesBuilder.setUnemploymentApEnterprise(Optional.ofNullable(unemployment_ap_enterprise));
			contingencyBasesBuilder.setProfesFormApEnterprise(Optional.ofNullable(profes_form_ap_enterprise));
			contingencyBasesBuilder.setFogasaApEnterprise(Optional.ofNullable(fogasa_ap_enterprise));
			contingencyBasesBuilder.setForceMajeureApEnterprise(Optional.ofNullable(force_majeure_ap_enterprise));
			contingencyBasesBuilder.setNoStructApEnterprise(Optional.ofNullable(no_struct_ap_enterprise));
			
			contingencyBasesBuilder.setNoStructBase(Optional.of(nonStructBase[0]));
			contingencyBasesBuilder.setForceMajeureBase(Optional.of(forceMajeureBase[0]));
			
			defaulPayrollBuilder.setContingencies(contingencyBasesBuilder.build());
			
			defaulPayrollBuilder.setPayrollTotal(salary.getTotalLiquid());
			
			Optional<InputStream> optLogo = Utilities.getSignature(domainName);
			PayrollTemplate dpt = new PayrollTemplate(defaulPayrollBuilder.build(), optLogo, Optional.ofNullable(new Locale("es")));
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
		List<String> logConcepts = Arrays.asList(ContextVariable.LOGS);
		List<String> excludedDescriptionWords = Arrays.asList("VACACIONES");
		List<String> excludedConcepts = Arrays.asList(PREST_IT, UNPAID.getName());
		
		return !(payment.getAmount() == 0 && payment.getQuote() == 0)
			|| logConcepts.contains(payment.getName())
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
	    Date startDate = salary.getStartDate();
	    return salary.getSalaryDatas().stream()
		    .filter( d -> d.getName().equals(NO_HOLIDAYS.getName()))
		    .map( SalaryData::getStartDate )
		    .filter( d -> d.after(startDate) )
		    .collect(Collectors.minBy(Date::compareTo))
		    .map( d -> AonDateUtils.addDays(d,-1))
		    .orElse(salary.getEndDate());
	}
	


}
