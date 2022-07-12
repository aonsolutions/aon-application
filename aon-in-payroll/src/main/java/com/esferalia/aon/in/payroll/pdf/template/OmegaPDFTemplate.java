package com.esferalia.aon.in.payroll.pdf.template;

import static com.esferalia.aon.watson.util.AonNumberUtils.zeroIfNull;
import static com.esferalia.aon.watson.util.AonStringUtils.containsIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.substring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.pdf.template.commons.Deduction;
import com.esferalia.aon.in.payroll.pdf.template.commons.Payment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OmegaPDFTemplate implements SalaryPDFTemplate {

	private static final Locale SPAIN = new Locale("es", "ES");
	
	public static final  OmegaPDFTemplate OMEGA_PDF_TEMPLATE = new OmegaPDFTemplate();

	private Date startDate;
	private Date endDate;
	private Period period;


	
	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {		
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY);
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			
			List<Payment> paymentList = new LinkedList<>();
			Map<Deduction, Map<String, Double>> deductions = new LinkedHashMap<>();
			Map<Deduction, Map<String, Double>> costs = new LinkedHashMap<>();
			
			Matcher matcher = find(reader, NAMES);
			String enterpriseName = string(matcher, "enterprise");				
			String employeeName = string(matcher, "employee");

			matcher = find(reader, DOMICILIO);
			String enterpriseAddress = string(matcher, "address");				
			String employeeNif = string(matcher, "nif");
			
			matcher = find(reader, CIF);
			String enterpriseDocument = string(matcher, "cif");				
			String employeeNss = string(matcher, "nss");
			employeeNss = employeeNss != null ? employeeNss.replace("/", "") : null;
			
			matcher = find(reader, CATEGORY);				
			String employeeCategory = string(matcher, "category");
			
			matcher = find(reader, CCC);
			String enterpriseCCC = string(matcher, "ccc");
			String employeeQuoteGroup = string(matcher, "quotegroup");
			String employeeSeniority = string(matcher, "seniority");
			Date seniorityDate = employeeSeniority != null ? date(employeeSeniority, "dd/MM/yyyy") : null;
			enterpriseCCC = enterpriseCCC != null ? enterpriseCCC.replace("/", "") : null;
			
			if (string(matcher, "ss") == null) {
				matcher = find(reader, SCHUTZSTAFFEL);
				enterpriseCCC = string(matcher, "ccc");
				enterpriseCCC = enterpriseCCC != null ? enterpriseCCC.replace("/", "") : null;
			}
			
			matcher = find(reader, PERIOD);
			int days = AonNumberUtils.toint(string(matcher, "days"));
			String periodStr = string(matcher, "period");
			matcher = PERIOD_SALARY.matcher(periodStr);
			SalaryType salaryType = SalaryType.SALARY;
			boolean christmasExtra = false;
			boolean complementary = false;
			if (periodStr != null && matcher.matches()) {
				salaryType = SalaryType.SALARY;
				String dayFrom = string(matcher, "dayfrom");
				String monthFrom = string(matcher, "monthfrom");
				String dayTo= string(matcher, "dayto");
				String monthTo= string(matcher, "monthto");
				String year= string(matcher, "year");
				startDate = date(dayFrom + " de " + monthFrom + " de " + year, "dd 'de' MMMMMMMMMM 'de' yyyy");
				endDate = date(dayTo + " de " + monthTo + " de " + year, "dd 'de' MMMMMMMMMM 'de' yyyy");
			} else if (periodStr != null && AonStringUtils.containsIgnoreCase(periodStr, "f i n i q u i t o")) {
				salaryType = SalaryType.SETTLE;				
			} else if (periodStr != null && AonStringUtils.containsIgnoreCase(periodStr, "extra")) {
				salaryType = SalaryType.EXTRA;	
				christmasExtra =  AonStringUtils.containsIgnoreCase(periodStr, "navidad");
			} else if (periodStr != null && AonStringUtils.containsIgnoreCase(periodStr, "diferencias")) {
				salaryType = SalaryType.DELAY;
			} else if (periodStr != null && AonStringUtils.containsIgnoreCase(periodStr, "complementaria")) {
				complementary = true;
			}

			matcher = find(reader, IDEVENGOS);
			matcher = find(reader, PERCEPCIONES);
			
			
			//DEVENGOS
			
			int leftCategory = 0;
			int rightCategory = 0;
			
			String line = reader.readLine();
			matcher = TOTAL_PAYMENT.matcher(line);
			while (!matcher.matches()) {
				
				if (
//						containsIgnoreCase(line, "Indemnizaciones o suplidos") ||
						containsIgnoreCase(line, "Prestaciones e indemnizaciones de la Seguridad Social") ||
						containsIgnoreCase(line, "Indemnizaciones por traslados, suspensiones o despidos") ||
						containsIgnoreCase(line, "Otras percepciones no salariales")
				) {
					rightCategory++;
				}
				if (containsIgnoreCase(line, "Complementos salariales")) {
					leftCategory++;
				}
				Matcher payMatcher = PAYMENTROW.matcher(line);
				if (payMatcher.matches()) {
					if (payMatcher.group("amountone") != null) {
						Matcher conceptMatch = PAYMENTCONCEPT.matcher(payMatcher.group("conceptone"));
						String description = "";
						if (conceptMatch.matches()) {
							description = string(conceptMatch, "concept");
						}
						description = description != null ? description : "Otros";
						description = description.replaceAll("\\.{2,}", "");
						description = substring(description, 0, 25);
						description = description.trim();
						Double amount = str2Double(string(payMatcher, "amountone"));
						PaymentType paymentType = cra(salaryType, description, leftCategory, rightCategory);
						if (amount != null) {
							paymentList.add(new Payment().setAmount(amount).setDescription(description).setType(paymentType).setName(description));
						}
					}
					if (payMatcher.group("amounttwo") != null) {
						Matcher conceptMatch = PAYMENTCONCEPT.matcher(payMatcher.group("concepttwo"));
						String description = "";
						if (conceptMatch.matches()) {
							description = string(conceptMatch, "concept");
						}
						description = description != null ? description : "Otros";
						description = description.replaceAll("\\.{2,}", "");
						description = substring(description, 0, 25);
						description = description.trim();
						Double amount = str2Double(string(payMatcher, "amounttwo"));
						PaymentType paymentType = cra(salaryType, description, leftCategory, rightCategory);
						if (amount != null) {		
							paymentList.add(new Payment().setAmount(amount).setDescription(description).setType(paymentType).setName(description));
						}
						
					}
				}			
				line = reader.readLine();
				matcher = TOTAL_PAYMENT.matcher(line);
			}
			
			Double totalPayment = zeroIfNull(str2Double(string(matcher, "amount")));
			
			matcher = find(reader, IIDEDUCCIONES);
			matcher = find(reader, APORTACION);
			matcher = find(reader, IRPF);
			String irpfPercentStr = string(matcher, "percent");
			String irpfAmountStr = string(matcher, "amount");
			
			Double irpfAmount = str2Double(irpfAmountStr);
			Double irpfPercent = str2Double(irpfPercentStr);
			if (irpfAmount != null) {
				deductions.put(
						new Deduction().setAmount(irpfAmount).setDescription(DeductionType.IRPF.getName(SPAIN)).setName("IRPF").setType(DeductionType.IRPF),
						Collections.singletonMap("PORCENTAJE_IRPF", irpfPercent)
				);
			}
			Double ccBase = null;
			Double ccPercent = null;
			Double ccAmount = null;
			Double advanced = null;
			line = reader.readLine();
			matcher = ANTICIPOS.matcher(line);
			if (matcher.matches()) {
				advanced = str2Double(string(matcher, "advanced"));
				line = reader.readLine();
				matcher = CC.matcher(line);
				if (matcher.matches()) {
					ccAmount = str2Double(string(matcher, "amount"));
					ccBase= str2Double(string(matcher, "base"));
					ccPercent = str2Double(string(matcher, "percent"));
				}
			} else {
				matcher = CC.matcher(line);
				if (matcher.matches()) {
					ccAmount = str2Double(string(matcher, "amount"));
					ccBase= str2Double(string(matcher, "base"));
					ccPercent = str2Double(string(matcher, "percent"));
					advanced = str2Double(string(matcher, "advanced"));					
				}
			}
			
			if (ccAmount != null) {
				deductions.put(
						new Deduction().setAmount(ccAmount).setDescription(DeductionType.COMMON_CONTINGENCY.getName(SPAIN)).setName("CGC").setType(DeductionType.COMMON_CONTINGENCY),
						Collections.singletonMap("PORCENTAJE_CGC", ccPercent)
				);
			}
			if (advanced != null) {
				deductions.put(
						new Deduction().setAmount(advanced).setDescription("").setName("ANTICIPO").setType(DeductionType.ADVANCE_PAYMENT),
						Collections.emptyMap()
				);
			}

			matcher = find(reader, UNEMPLOYMENT);
			Double unemploymentBase = str2Double(string(matcher, "base"));
			Double unemploymentPercent = str2Double(string(matcher, "percent"));
			Double unemploymentAmount = str2Double(string(matcher, "amount"));
			if (unemploymentAmount != null) {
				deductions.put(
						new Deduction().setAmount(unemploymentAmount).setDescription(DeductionType.UNEMPLOYMENT.getName(SPAIN)).setName("DESMPL").setType(DeductionType.UNEMPLOYMENT),
						Collections.singletonMap("PORCENTAJE_DESMPL", unemploymentPercent)
				);
			}
			Double cgpBase = unemploymentBase;
			
			
			Double fpBase = null;
			Double fpPercent = null;
			Double fpAmount = null;
			Double kindBase = null;
			Double kindPercent = null;
			Double kindAmount = null;
			line = reader.readLine();
			matcher = INKIND.matcher(line);
			if (matcher.matches()) {
				kindBase = str2Double(string(matcher, "kindbase"));
				kindPercent = str2Double(string(matcher, "kindpercent"));
				kindAmount = str2Double(string(matcher, "kindamount"));
				line = reader.readLine();				
				matcher = JOB_TRAINING.matcher(line);
				if (matcher.matches()) {					
					fpBase = str2Double(string(matcher, "base"));
					fpPercent = str2Double(string(matcher, "percent"));
					fpAmount = str2Double(string(matcher, "amount"));
				}
			} else {
				matcher = JOB_TRAINING.matcher(line);
				if (matcher.matches()) {					
					fpBase = str2Double(string(matcher, "base"));
					fpPercent = str2Double(string(matcher, "percent"));
					fpAmount = str2Double(string(matcher, "amount"));
					kindBase = str2Double(string(matcher, "kindbase"));
					kindPercent = str2Double(string(matcher, "kindpercent"));
					kindAmount = str2Double(string(matcher, "kindamount"));
				}
			}
			
			cgpBase = Math.max(zeroIfNull(unemploymentBase), zeroIfNull(fpBase));
			if (fpAmount != null) {
				deductions.put(
						new Deduction().setAmount(fpAmount).setDescription(DeductionType.JOB_TRAINING.getName(SPAIN)).setName("FP").setType(DeductionType.JOB_TRAINING),
						Collections.singletonMap("PORCENTAJE_FP", fpPercent)
				);
			}
			if (kindAmount != null) {
				deductions.put(
						new Deduction().setAmount(kindAmount).setDescription("").setName("EN_ESPECIE").setType(DeductionType.IN_KIND),
						Collections.emptyMap()
				);
			}
			
			if (kindBase != null && kindPercent == null && kindAmount == null) {
				kindAmount = kindBase;
				kindBase = null;
			}
			
			String otherConcept = null;
			Double otherAmount = null;
			
			matcher = find(reader, EXTRAH);
			if (string(matcher, "otherdeductions") == null) {
				matcher = find(reader, OTHER_DEDUCTION);
				otherConcept = string(matcher, "other");
				otherAmount = str2Double(string(matcher, "otheramount"));
			}
			
			Double overBase = null;
			Double overPercent = null;
			Double overAmount = null;
			matcher = find(reader, OVERWHELMING);
			overBase = str2Double(string(matcher, "base"));
			overPercent = str2Double(string(matcher, "percent"));
			overAmount = str2Double(string(matcher, "amount"));
			
			String otherHConcept = null;
			Double otherHOther = null;
			
			if (string(matcher, "otherdeduction") != null) {
				otherConcept = string(matcher, "other");
				otherAmount = str2Double(string(matcher, "otheramount"));
			} else {
				matcher = find(reader, OTHER_DEDUCTION);
				otherHConcept = string(matcher, "other");
				otherHOther = str2Double(string(matcher, "otheramount"));
			}
			
			if (overBase != null && overPercent == null && overAmount == null) {
				overPercent = overBase;
				overBase = null;
			}
			if (overAmount != null) {
				deductions.put(
						new Deduction().setAmount(overAmount).setDescription(DeductionType.STRUCTURAL_OVERTIME.getName(SPAIN)).setName("ESTR").setType(DeductionType.STRUCTURAL_OVERTIME),
						Collections.singletonMap("PORCENTAJE_EXTR", overPercent)
				);
			}
			
			if (otherAmount != null) {
				deductions.put(
						new Deduction().setAmount(otherAmount).setDescription(substring(otherConcept, 0, 15)).setName(substring(otherConcept, 0, 15)).setType(DeductionType.OTHER),
						Collections.emptyMap()
				);
			}
			
			matcher = find(reader, OTHER_EXTRAS);
			Double otherHBase = str2Double(string(matcher, "base"));
			Double otherHPercent = str2Double(string(matcher, "percent"));
			Double otherHAmount = str2Double(string(matcher, "amount"));
			if (otherHBase != null && otherHPercent == null && otherHAmount == null) {
				otherHPercent = otherHBase;
				otherHBase = null;
			}
			if (otherHAmount != null) {
				deductions.put(
						new Deduction().setAmount(otherHAmount).setDescription(DeductionType.NON_STRUCTURAL_OVERTIME.getName(SPAIN)).setName("NESTR").setType(DeductionType.NON_STRUCTURAL_OVERTIME),
						Collections.singletonMap("PORCENTAJE_NEXTR", otherHPercent)
				);
			}
			if (string(matcher, "otherdeduction") != null) {				
				otherHConcept = string(matcher, "otherconcept");
				otherHOther = str2Double(string(matcher, "otheramount"));
			}
			if (otherHOther != null) {
				deductions.put(
						new Deduction().setAmount(otherHOther).setDescription(substring(otherHConcept, 0, 15)).setName(substring(otherHConcept, 0, 15)).setType(DeductionType.OTHER),
						Collections.emptyMap()
				);
			}

			matcher = find(reader, TOTAL_APORT);
			Double totalAport = str2Double(string(matcher, "amount"));

			matcher = find(reader, TOTAL_DEDUCTION);
			Double totalDeduction = str2Double(string(matcher, "amount"));

			matcher = find(reader, LIQUID);
			Double totalLiquid = str2Double(string(matcher, "amount"));

			matcher = find(reader, ISSUE_DATE);
			String place = string(matcher, "place");
			Date issueDate = date(matcher, "date", "dd 'de' MMMMMMMMMM 'de' yyyy");

			matcher = find(reader, MONTHLY_PAYMENT);
			Double monthlyPayment = str2Double(string(matcher, "amount"));

			matcher = find(reader, EXTRA_PR);
			Double extraPr = str2Double(string(matcher, "amount"));

			matcher = find(reader, BASE_IT);
			Double baseIt = str2Double(string(matcher, "base"));
			
			matcher = find(reader, TOTAL_CC);
			Double baseCC = str2Double(string(matcher, "base"));
			Double nbaseCC = str2Double(string(matcher, "nbase"));
			Double percentCC = str2Double(string(matcher, "percent"));
			Double amountCC = str2Double(string(matcher, "amount"));
			
			if (amountCC != null) {
				costs.put(
						new Deduction().setAmount(amountCC).setDescription(DeductionType.COMMON_CONTINGENCY.getName(SPAIN)).setName("CGC_E").setType(DeductionType.COMMON_CONTINGENCY),
						Collections.singletonMap("PORCENTAJE_CGC_E", percentCC)
				);
			}
			
			
			if (percentCC == null && nbaseCC != null) {
				percentCC = nbaseCC;
				nbaseCC = null;
			}
			
			matcher = find(reader, ATEP);
//			Double percentAT = str2Double(string(matcher, "percent"));
			Double amountAT = str2Double(string(matcher, "amount"));
			
			if (amountAT != null) {
				costs.put(
						new Deduction().setAmount(amountAT).setDescription(DeductionType.PROFESSIONAL_CONTINGENCY.getName(SPAIN)).setName("IT_E").setType(DeductionType.PROFESSIONAL_CONTINGENCY),
						Collections.emptyMap()
				);
			}
			
			Double amountUnem = null;
			Double percentUnem = null;
			Double baseCGP = null;
			
			matcher = find(reader, CC_TITLE);
			
			line = reader.readLine();
			matcher = UNEMPLOYMENT_EE_FULL.matcher(line);
			if (matcher.matches()) {
				percentUnem = str2Double(string(matcher, "percent"));
				amountUnem = str2Double(string(matcher, "amount"));
				baseCGP = str2Double(string(matcher, "base"));
				
				
				if (baseCGP != null && baseCGP < 20 && amountUnem == null && percentUnem == null) {
					percentUnem = baseCGP;
					baseCGP = null;
				}
				
			} else {
				matcher = UNEMPLOYMENT_EE.matcher(line);
				if (!matcher.matches()) {
					matcher = find(reader, UNEMPLOYMENT_EE);
				}
				percentUnem = str2Double(string(matcher, "percent"));
				amountUnem = str2Double(string(matcher, "amount"));
				
				matcher = find(reader, CGP);
				baseCGP = str2Double(string(matcher, "base"));
				if (string(matcher, "titles") == null && amountUnem == null && percentUnem == null) {
					line = reader.readLine();
					matcher = UNEMPLOYMENT_SPARE.matcher(line);
					if (matcher.matches()) {
						percentUnem = str2Double(string(matcher, "percent"));
						amountUnem = str2Double(string(matcher, "amount"));
					}
				}
			}
			
			if (amountUnem != null) {
				costs.put(
						new Deduction().setAmount(amountUnem).setDescription(DeductionType.UNEMPLOYMENT.getName(SPAIN)).setName("DESMPL_E").setType(DeductionType.UNEMPLOYMENT),
						Collections.singletonMap("PORCENTAJE_DESMPL_E", percentUnem)
				);
			}
			
			matcher = find(reader, FP_E);
			Double percentFPE = str2Double(string(matcher, "percent"));
			Double amountFPE = str2Double(string(matcher, "amount"));
			if (amountFPE != null) {
				costs.put(
						new Deduction().setAmount(amountFPE).setDescription(DeductionType.JOB_TRAINING.getName(SPAIN)).setName("FP_E").setType(DeductionType.JOB_TRAINING),
						Collections.singletonMap("PORCENTAJE_FP_E", percentFPE)
				);
			}
			
//			matcher = find(reader, RECAUDACION);
			
			matcher = find(reader, FOGASA);
			Double percentFOGASA = str2Double(string(matcher, "percent"));
			Double amountFOGASA = str2Double(string(matcher, "amount"));			
			if (amountFOGASA != null) {
				costs.put(
						new Deduction().setAmount(amountFOGASA).setDescription(DeductionType.FOGASA.getName(SPAIN)).setName("FOGASA").setType(DeductionType.FOGASA),
						Collections.singletonMap("PORCENTAJE_FOGASA", percentFOGASA)
				);
			}
			
			matcher = find(reader, EXTRAQUOTE);
			Double amountExtra = str2Double(string(matcher, "amount"));
			if (amountExtra != null) {
				costs.put(
						new Deduction().setAmount(amountExtra).setDescription(DeductionType.STRUCTURAL_OVERTIME.getName(SPAIN)).setName("ESTR").setType(DeductionType.STRUCTURAL_OVERTIME),
						Collections.emptyMap()
				);
			}
			
			matcher = find(reader, IRPF_EE);
			Double irpfBase = str2Double(string(matcher, "amount"));
			
			issueDate = adjustTime(issueDate);
			seniorityDate = adjustTime(seniorityDate);
			if (salaryType.equals(SalaryType.SETTLE)) {
				startDate = seniorityDate;
				endDate = issueDate;
			} else if (salaryType.equals(SalaryType.EXTRA) && christmasExtra) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(issueDate);
				cal.set(Calendar.MONTH, Calendar.JULY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startDate = cal.getTime();
				cal.set(Calendar.MONTH, Calendar.DECEMBER);
				cal.set(Calendar.DAY_OF_MONTH, 31);
				endDate = cal.getTime();
			} else if (salaryType.equals(SalaryType.EXTRA) && !christmasExtra) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(issueDate);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startDate = cal.getTime();
				cal.set(Calendar.MONTH, Calendar.JULY);
				cal.set(Calendar.DAY_OF_MONTH, 31);
				endDate = cal.getTime();				
			} else if (salaryType.equals(SalaryType.DELAY) ||
					  (salaryType.equals(SalaryType.SALARY) && complementary)
			) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(issueDate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startDate = cal.getTime();
				endDate = issueDate;
			}
			
			startDate = adjustTime(startDate);
			endDate = adjustTime(endDate);
			salaryBuilder.setStartDate(startDate);
			salaryBuilder.setEndDate(endDate);
			salaryBuilder.setChargeDate(endDate);
			salaryBuilder.setIssueDate(issueDate);
			if (salaryType != null) {				
				salaryBuilder.setType(salaryType);
			}
			
			salaryBuilder.setRemuneration(zeroIfNull(monthlyPayment));
			salaryBuilder.setEnterpriseCity(place);
			salaryBuilder.setTotalLiquid(zeroIfNull(totalLiquid));
			salaryBuilder.setTotalDeduction(zeroIfNull(totalDeduction));
			salaryBuilder.setTotalSS(zeroIfNull(totalAport));
			salaryBuilder.setCgcBase(zeroIfNull(ccBase));
			salaryBuilder.setTotalIrpf(zeroIfNull(irpfAmount));
			salaryBuilder.setItBase(zeroIfNull(baseIt));
			salaryBuilder.setProExtBase(zeroIfNull(extraPr));
			salaryBuilder.setEmployeeName(employeeName);
			salaryBuilder.setEnterpriseName(enterpriseName);
			salaryBuilder.setEmployeeDocument(employeeNif);
			salaryBuilder.setSocialSecurityNumber(employeeNss);
			salaryBuilder.setEnterpriseAddress(enterpriseAddress);
			salaryBuilder.setEnterpriseDocument(enterpriseDocument);
			salaryBuilder.setCategory(employeeCategory);
			salaryBuilder.setCcc(enterpriseCCC);
			salaryBuilder.setQuoteGroup(employeeQuoteGroup);
			salaryBuilder.setSeniorityDate(seniorityDate);
			salaryBuilder.setTimeUnits(zeroIfNull(days));
			salaryBuilder.setTotalPayment(zeroIfNull(totalPayment));
			salaryBuilder.setIrpfBase(zeroIfNull(irpfBase));
			
			PDFContract contract = new AltaiPDFTemplate.PDFContract()
					.setCcc(enterpriseCCC) 
					.setNaf(employeeNss)
					.setNif(employeeNif)
					.setCif(enterpriseDocument)
					.setStartDate(startDate)
					.setEndDate(endDate)
					.setEmployeeName(employeeName)
					.setEnterpriseName(enterpriseName);
			salaryBuilder.setContract(contract);
			
			period = new Period(startDate, endDate);
			
			salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<>(irpfBase, period ));
			salaryBuilder.addData(ContextVariable.CGC_BASE.getName(), new TimedObject<>(ccBase, period ));
			salaryBuilder.addData(ContextVariable.CGP_BASE.getName(), new TimedObject<>(cgpBase, period ));
			salaryBuilder.addData(ContextVariable.CGC_BASE_ENTERPRISE.getName(), new TimedObject<>(baseCC, period ));
			salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(), new TimedObject<>(baseCGP, period ));
			salaryBuilder.addData(ContextVariable.QUOTE_GROUP.getName(), new TimedObject<>(employeeQuoteGroup, period ));
			
			for (Payment payment : paymentList) {
				salaryBuilder.addPayment(
						payment.getAmount(),
						payment.getAmount(),
						payment.getAmount(),
						payment.getDescription(),
						startDate,
						endDate,
						new Payment().setType(payment.getType()).setName(payment.getName()),
						Collections.emptyMap());
			}
			final Date finalSDate = startDate;
			final Date finalEDate = endDate;
			deductions.forEach((deduction, data) -> {
				Map<String, ITimedVariable<?>> dataMap = new HashMap<>();
				data.forEach((name, percent) -> {
					if (name != null) {
						dataMap.put(name, new TimedObject<>(percent, period));
					}
				});
				salaryBuilder.addDeduction(deduction.getAmount(),
						deduction.getDescription(),
						finalSDate,
						finalEDate,
						new Deduction().setType(deduction.getType()).setName(deduction.getName()),
						dataMap);
			}
			);
			
			costs.forEach((cost, data) -> {
				Map<String, ITimedVariable<?>> dataMap = new HashMap<>();
				data.forEach((name, percent) -> {
					if (name != null) {
						dataMap.put(name, new TimedObject<>(percent, period));
					}
				});
				salaryBuilder.addCost(
						cost.getAmount(), 
						cost.getDescription(), 
						finalSDate, 
						finalEDate, 
						new Deduction().setType(cost.getType()).setName(cost.getName()), 
						dataMap);
			}
			);
			
			salaryBuilder.getSalary();
		}
		
		
		return this;
	}
	
	private static PaymentType cra(SalaryType salaryType, String description, int leftCategory, int rightCategory) {
		if (SalaryType.DELAY.equals(salaryType)) {
			return PaymentType.CRA_0009;
		}
		if (containsIgnoreCase(description, "horas extra")) {
			if (containsIgnoreCase(description, "mayor")) {
				return PaymentType.CRA_0003;
			} else {					
				return PaymentType.CRA_0002;
			}
		} else if (containsIgnoreCase(description, "p.extra")) {
			return PaymentType.CRA_0004;
		} else if (leftCategory == 0 && containsIgnoreCase(description, "especie")) {
			return PaymentType.CRA_0013;
		} else {			
			if (rightCategory == 0) {
				if (containsIgnoreCase(description, "plus t")) {
					return PaymentType.CRA_0061;
				}
			} else if (rightCategory == 2) {
				if (containsIgnoreCase(description, "falleci")) {
					return PaymentType.CRA_0051;
				} else if (containsIgnoreCase(description, "trasla")) {
					return PaymentType.CRA_0052;
				} else if (containsIgnoreCase(description, "suspe")) {
					return PaymentType.CRA_0053;
				} else if (containsIgnoreCase(description, "desp") || containsIgnoreCase(description, "cese")) {
					return PaymentType.CRA_0054;
				}
			}
		}
		return PaymentType.CRA_0001;
	}

	private static Double str2Double(String str) {
		if (str != null) {
			try {
				return Double.parseDouble(str.replace(" ", "").replace(".", "").replace(",", ".").trim());
			} catch (NullPointerException | NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	private static Integer str2Int(String str) {
		if (str != null) {
			try {
				return Integer.parseInt(str.replace(" ", "").replace(".", "").replace(",", ".").trim());
			} catch (NullPointerException | NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	private Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
//				System.out.println(line);
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}

	private static String string(Matcher matcher, String name ) {
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		return group;
	}
	
	private static Date date(Matcher matcher, String name, String pattern  ) throws UnknownPDFException{
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		try {
			return new SimpleDateFormat(pattern, SPAIN).parse(group);
		} catch (ParseException e) {
			throw new UnknownPDFException(e);
		}
	}
	
	private static Date date(String str, String pattern  ) throws UnknownPDFException{
		str = AonStringUtils.trim(str);
		try {
			return new SimpleDateFormat(pattern, SPAIN).parse(str);
		} catch (ParseException e) {
			throw new UnknownPDFException(e);
		}
	}
	
	private static Date adjustTime(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 12);
		return calendar.getTime();
	}
	
//	private static Date date(String day, String month, String year) throws UnknownPDFException{
//		Integer d = str2Int(day);
//		int m = PdfParsingTools.monthChooser(month);
//		Integer y = str2Int(year);
//		if (d == null || y == null || m < 0) {
//			return null;
//		}
//		Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
//		cal.set(Calendar.MILLISECOND, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.HOUR, 0);
//		cal.set(Calendar.DAY_OF_MONTH, d);
//		cal.set(Calendar.MONTH, m);
//		cal.set(Calendar.YEAR, y);
//		return cal.getTime();
//	}
	
	
	
	//Empresa:    REGISTRO RETRIBUTIBO    Trabajador:    LOPEZ LOPEZ, DIEGO
	private static final Pattern NAMES = 
	Pattern.compile("^\\s*Empresa:\\s*(?<enterprise>.*?)\\s*Trabajador:\\s*(?<employee>.*?)\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//Domicilio:    CL VIA, 26 1    N.I.F.:    88888888Y    Número Libro de Matrícula:
	private static final Pattern DOMICILIO = 
			Pattern.compile("^\\s*Domicilio:\\s*(?<address>.*?)\\s*N\\.I\\.F\\.:\\s*(?<nif>.*?)\\s*N.mero\\s*Libro\\s*de\\s*Matr.cula:.*$"
					, Pattern.CASE_INSENSITIVE);
	//C.I.F.:    B99102717    Nº de Afiliación a la Seguridad Social:    50/48476595/45
	private static final Pattern CIF = 
			Pattern.compile("^\\s*C\\.I\\.F\\.:\\s*(?<cif>.*?)\\s*N.\\s*de\\s*Afiliaci.n\\s*a\\s*la\\s*Seguridad\\s*Social:\\s*(?<nss>[\\d/]+)?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	//Código de Cuenta de Cotización a la    Categoría o Grupo Profesional:    MAESTRO TALLER
	private static final Pattern CATEGORY = 
			Pattern.compile("^\\s*C.digo\\s*de\\s*Cuenta\\s*de\\s*Cotizaci.n\\s*a\\s*la\\s*Categor.a\\s*o\\s*Grupo\\s*Profesional:\\s*(?<category>.*?)\\s*$"
					, Pattern.CASE_INSENSITIVE);

	//Seguridad Social:    50/1212646/66    Grupo de Cotización:    03    Fecha antigüedad:    01/01/2020
	/*
	 	Grupo de Cotización:    08    Fecha antigüedad:    03/03/2020
		Seguridad Social:    50/0025416/91
	 * */
	private static final Pattern CCC = 
			Pattern.compile("^(?<ss>\\s*Seguridad\\s*Social:\\s*(?<ccc>[\\d/]+)?)?\\s*Grupo\\s*de\\s*Cotizaci.n:\\s*(?<quotegroup>\\d+)?\\s*Fecha\\s*antig.edad:\\s*(?<seniority>(?<day>\\d{1,2})\\/(?<month>\\d{1,2})\\/(?<year>\\d{2,4}))?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	private static final Pattern SCHUTZSTAFFEL = 
			Pattern.compile("^(\\s*Seguridad\\s*Social:\\s*(?<ccc>[\\d/]+)?)\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Periodo de Liquidación:    del 1 de marzo al 31 de marzo de 2021    Total días     30
	private static final Pattern PERIOD = 
			Pattern.compile("^\\s*Periodo\\s*de\\s*Liquidaci.n:\\s*(?<period>.*?)\\s*Total\\s*d.as\\s*(?<days>\\d+)?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	private static final Pattern PERIOD_SALARY = 
			Pattern.compile("\\s*del\\s*(?<dayfrom>\\d{1,2})\\s*de\\s*(?<monthfrom>\\w{4,10})\\s*al\\s*(?<dayto>\\d{1,2})\\s*de\\s*(?<monthto>\\w{4,10})\\s*de\\s*(?<year>\\d{2,4})\\s*"
					, Pattern.CASE_INSENSITIVE);
	//I. DEVENGOS    TOTALES
	private static final Pattern IDEVENGOS = 
			Pattern.compile("^\\s*I\\.\\s*DEVENGOS\\s*(TOTALES)?\\s*$"
					, Pattern.CASE_INSENSITIVE);

	//1. Percepciones salariales    2. Percepciones no salariales
	private static final Pattern PERCEPCIONES = 
			Pattern.compile("^\\s*1\\.\\s*Percepciones\\s*salariales\\s*2\\.\\s*Percepciones\\s*no\\s*salariales\\s*$"
					, Pattern.CASE_INSENSITIVE);
	//SALARIO BASE...............................................................    1.200,00
	private static final Pattern PAYMENT = 
			Pattern.compile("(?:\\s{2,})?(?<payment>((?!\\s{2,}).)*?)\\.{2,}(?<data>\\([^()]*\\))?\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d+)\\s*"
					, Pattern.CASE_INSENSITIVE);
	
	//SALARIO BASE...............................................................    1.200,00    Indemnizaciones o suplidos
	private static final Pattern PAYMENTROW = 
			Pattern.compile("^\\s*(?<conceptone>((?![\\s\\.]{2,}\\d((?!\\.{5,})[\\d\\s\\.])*,\\d{2}).)*)?(?<amountone>[\\s\\.]{2,}\\d((?!\\.{5,})[\\d\\s\\.])*,\\d{2})?\\s*(?<concepttwo>((?![\\s\\.]{2,}\\d((?!\\.{5,})[\\d\\s\\.])*,\\d{2}).)*)?(?<amounttwo>[\\s\\.]{2,}\\d((?!\\.{5,})[\\d\\s\\.])*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);

	private static final Pattern PAYMENTCONCEPT = 
			Pattern.compile("((?:((?!\\s{3,}).)*\\s{3,})?(?<concept>.+)$)"
					, Pattern.CASE_INSENSITIVE);
	
	
	//A. TOTAL SALARIO DEVENGADO........................    1.437,68
	private static final Pattern TOTAL_PAYMENT = 
			Pattern.compile("^\\s*A\\.\\s*TOTAL\\s*(?:SALARIO\\s*)?DEVENGADO\\.{2,}\\s*(?<amount>[\\d,\\.]+)?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//II. DEDUCCIONES
	private static final Pattern IIDEDUCCIONES = 
			Pattern.compile("^\\s*II\\.\\s*DEDUCCIONES\\s*$"
					, Pattern.CASE_INSENSITIVE);

	//1. Aportación del trabajador a las cotizaciones a la     2. Impuesto sobre la renta 
	private static final Pattern APORTACION = 
			Pattern.compile("^\\s*1\\.\\s*Aportaci.n\\s*del\\s*trabajador\\s*a\\s*las\\s*cotizaciones\\s*a\\s*la\\s*2\\.\\s*Impuesto\\s*sobre\\s*la\\s*renta\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//   Seguridad Social y conceptos de recaudación conjunta    de la personas físicas.....................    4,00%    57,51 
	private static final Pattern IRPF = 
			Pattern.compile("^\\s*Seguridad\\s*Social\\s*y\\s*conceptos\\s*de\\s*recaudaci.n\\s*conjunta\\s*de\\s*la.?\\s*personas\\s*f.sicas\\.{2,}\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	
	//Contingencias Comunes    1.643,58    4,70%    77,25    3. Anticipos......................................................      
	/*
	 	3. Anticipos.......................................................     
		Contingencias Comunes    1.439,22    4,70%    67,64 
	 */
	private static final Pattern CC = 
			Pattern.compile("^\\s*Contingencias\\s*Comune.?\\s*(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?:(?<percent>\\d{1,2},\\d{2})?%)?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*(?<anticipos>3\\.\\s*Anticipos\\.{2,}\\s*(?<advanced>\\d[\\d\\.]*,\\d{2,})?\\s*)?$"
					, Pattern.CASE_INSENSITIVE);
	private static final Pattern ANTICIPOS = 
			Pattern.compile("^\\s*3\\.\\s*Anticipos\\.{2,}(?<advanced>\\d[\\d\\.]*,\\d{2,})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Desempleo    1.643,58    1,55%    25,48    4. Valor de los productos
	private static final Pattern UNEMPLOYMENT = 
			Pattern.compile("^\\s*Desempleo\\s*(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?:(?<percent>\\d{1,2},\\d{2})?%)?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*(?:4\\.\\s*Valor\\s*de\\s*los\\s*productos\\s*)?$"
					, Pattern.CASE_INSENSITIVE);
	
	//Formación Profesional    1.643,58    0,10%    1,64    recibidos en especie.................               
	/*
	recibidos en especie.................               
	Formación Profesional    1.439,22    0,10%    1,44 
	*/
	private static final Pattern JOB_TRAINING = 
			Pattern.compile("^\\s*Formaci.n\\s*Profesional\\s*(?<fp>(?<base>\\d[\\d\\.]*,\\d{2})?\\s*((?<percent>\\d{1,2},\\d{2})?%?)?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?)?(?<inspice>\\s*recibidos\\s*en\\s*especie\\.{2,}\\s*(?<kind>(?<kindbase>\\d[\\d\\.]*,\\d{2})?\\s*(?<kindpercent>\\d{1,2},\\d{2})?%?)?\\s*(?<kindamount>\\d[\\d\\.]*,\\d{2})?)?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	private static final Pattern INKIND = 
			Pattern.compile("^\\s*\\s*recibidos\\s*en\\s*especie\\.{2,}\\s*(?<kind>(?<kindbase>\\d[\\d\\.]*,\\d{2})?\\s*(?<kindpercent>\\d{1,2},\\d{2})?%?)?\\s*(?<kindamount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
		
	//Horas Extraordinarias    5. Otras deducciones               
	private static final Pattern EXTRAH = 
			Pattern.compile("^\\s*Horas\\s*Extraordinarias(?<otherdeductions>\\s*5\\.\\s*Otras\\s*deducciones)?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Fuerza Mayor         2,00%         ................................................................     
	private static final Pattern  OVERWHELMING = 
			Pattern.compile("^\\s*Fuerza\\s*Mayor\\s*(?<overwhelming>(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?:(?<percent>\\d{1,2},\\d{2})?%?)?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?)?\\s*(?<otherdeduction>(?<other>\\w.+?)?\\.{2,}\\s*(?<otheramount>\\d[\\d\\.]*,\\d{2})?\\s*)?$"
					, Pattern.CASE_INSENSITIVE);     
	
	private static final Pattern  OTHER_DEDUCTION = 
			Pattern.compile("^\\s*(?<other>[\\w-].+?)?\\.{2,}\\s*(?<otheramount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Resto Horas Extras         4,70%         ................................................................     
	private static final Pattern  OTHER_EXTRAS = 
			Pattern.compile("^\\s*Resto\\s*Horas\\s*Extras\\s*(?<otherextras>(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?:(?<percent>\\d{1,2},\\d{2})?%?)?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?)?\\s*(?<otherdeduction>(?<otherconcept>\\w.+?)?\\.{2,}\\s*(?<otheramount>\\d[\\d\\.]*,\\d{2})?\\s*)?$"
					, Pattern.CASE_INSENSITIVE);
	
	//TOTAL APORTACIONES.................................................    104,37
	private static final Pattern  TOTAL_APORT = 
			Pattern.compile("^\\s*TOTAL\\s*APORTACIONES\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)......................    161,88
	private static final Pattern  TOTAL_DEDUCTION = 
			Pattern.compile("^\\s*B((?!\\.{4,}).)*?\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//LIQUIDO TOTAL A PERCIBIR (A-B)...    1.700,00Firma y Sello de la Empresa
	//TOTAL SALARIO LIQUIDO........................    1.275,80Firma y Sello de la Empresa
	private static final Pattern  LIQUID = 
			Pattern.compile("^\\s*(?:(?!L.QUIDO).)*L.QUIDO.*?\\.{2,}\\s*(?<amount>(-\\s*)?\\d[\\d\\.]*,\\d{2})?\\s*(?:Firma.*)?$"
					, Pattern.CASE_INSENSITIVE);
	
	//ZARAGOZA, 31 de marzo de 2021
	private static final Pattern  ISSUE_DATE = 
			Pattern.compile("^\\s*(?<place>.+?)?,\\s*(?<date>(?<day>\\d{1,2}\\s*de\\s*(?<month>\\w{4,10}\\s*de\\s*(?<year>\\d{2,4}))))\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//NORMALIZADA    EMPRESAImporte remuneración mensual...............................................................    1.437,68
	//NORMALIZADA    EMPRESAImporte remuneración mensual................................................    1.450,82
	//Importe remuneración mensual................................................    2.214,70    NORMALIZADA    EMPRESA
	private static final Pattern  MONTHLY_PAYMENT = 
			Pattern.compile("^.*?Importe\\s*remuneraci.n\\s*mensual\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?.*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Prorrata pagas extraordinarias................................................................    205,90
	private static final Pattern  EXTRA_PR = 
			Pattern.compile("^\\s*Prorrata\\s*pagas\\s*extraordinarias\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Base incapacidad temporal.....................................................................     
	private static final Pattern  BASE_IT = 
			Pattern.compile("^\\s*Base\\s*incapacidad\\s*temporal\\.{2,}\\s*(?<base>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//TOTAL..............    1.643,58    1.643,58    23,60%    387,88
	private static final Pattern  TOTAL_CC = 
			Pattern.compile("^\\s*TOTAL\\.{2,}\\s*(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?<nbase>\\d[\\d\\.]*,\\d{2})?\\s*((?<percent>\\d{1,2},\\d{2})?%?)\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//AT y EP.....................    3,90%    64,10
	private static final Pattern  ATEP = 
			Pattern.compile("^\\s*AT\\s*y\\s*EP\\.{2,}\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//2. Contingencias profesionales
	private static final Pattern  CC_TITLE = 
			Pattern.compile("^\\s*2\\.\\s*Contingencias\\s*profesionales\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	/*
	 	(AT.y EP.) y conceptos de
		Desempleo......................................
		2.578,09    2.578,09
		6,70%    172,73
		recaudación conjunta 
	 * */
	
	//Desempleo............................................    5,50%    90,40
	private static final Pattern  UNEMPLOYMENT_EE = 
			Pattern.compile("^\\s*Desempleo\\.{2,}\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//(AT.y EP.) y conceptos de    Desempleo...................    1.650,83    1.650,83    5,50%    90,80
	private static final Pattern  UNEMPLOYMENT_EE_FULL = 
			Pattern.compile("^\\s*\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*Desempleo\\.{2,}\\s*(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?<nbase>\\d[\\d\\.]*,\\d{2})?\\s*((?<percent>\\d{1,2},\\d{2})?%)\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//(AT.y EP.) y conceptos de    1.643,58    1.643,58
	private static final Pattern  CGP = 
			Pattern.compile("^\\s*(?<titles>\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*)?(?<base>\\d[\\d\\.]*,\\d{2})?\\s*(?<nbase>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//6,70%    172,73
	private static final Pattern  UNEMPLOYMENT_SPARE = 
			Pattern.compile("^\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);	
	
	//Formación Profesional............................    0,60%    9,86
	//recaudación conjunta     Formación Profesional.......    0,60%    7,16
	private static final Pattern  FP_E = 
			Pattern.compile("^(\\s*recaudaci.n\\s*conjunta)?\\s*Formaci.n\\s*Profesional\\.{2,}\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//recaudación conjunta 
	private static final Pattern  RECAUDACION = 
			Pattern.compile("^\\s*recaudaci.n\\s*conjunta\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	//Fondo Garantía Salarial...........................    0,20%    3,29
	private static final Pattern  FOGASA = 
			Pattern.compile("^\\s*Fondo\\s*Garant.a\\s*Salarial\\.{2,}\\s*(?<percent>\\d{1,2},\\d{2})?%?\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?\\s*$"
					, Pattern.CASE_INSENSITIVE);
	
	
	//3. Cotización adicional por horas extraordinarias...................................................................          
	private static final Pattern  EXTRAQUOTE = 
			Pattern.compile("^\\s*3\\.\\s*Cotizaci.n\\s*adicional\\s*por\\s*horas\\s*extraordinarias\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?.*$"
					, Pattern.CASE_INSENSITIVE);
	
	//4. Base sujeta a retención del I.R.P.F..............................................................................    1.437,68
	private static final Pattern  IRPF_EE = 
			Pattern.compile("^\\s*4\\.\\s*Base\\s*sujeta\\s*a\\s*retenci.n\\s*del\\s*I\\.R\\.P\\.F\\.{2,}\\s*(?<amount>\\d[\\d\\.]*,\\d{2})?.*$"
					, Pattern.CASE_INSENSITIVE);

	
	
	private static void check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
		for ( int g = 1; g <= matcher.groupCount(); g++) 
			System.out.println(matcher.group(g));
				
	}

	public static void main(String[] args) throws ParseException {
		check(NAMES, "Empresa:    REGISTRO RETRIBUTIBO    Trabajador:    LOPEZ LOPEZ, DIEGO");
		check(DOMICILIO, "Domicilio:    CL VIA, 26 1    N.I.F.:    88888888Y    Número Libro de Matrícula:");
		check(CIF, "C.I.F.:    B99102717    Nº de Afiliación a la Seguridad Social:    50/48476595/45");
		check(CATEGORY, "Código de Cuenta de Cotización a la    Categoría o Grupo Profesional:    MAESTRO TALLER");
		check(CCC, "Seguridad Social:    50/1212646/66    Grupo de Cotización:    03    Fecha antigüedad:    01/01/2020");
		check(PERIOD, "Periodo de Liquidación:    del 1 de marzo al 31 de marzo de 2021    Total días     30");
		check(IDEVENGOS, "I. DEVENGOS    TOTALES");
		check(PERCEPCIONES, "1. Percepciones salariales    2. Percepciones no salariales");
		check(PAYMENT, "SALARIO BASE...............................................................    1.200,00");
		check(TOTAL_PAYMENT, "A. TOTAL SALARIO DEVENGADO........................    1.437,68");
		check(IIDEDUCCIONES, "II. DEDUCCIONES");
		check(APORTACION, "1. Aportación del trabajador a las cotizaciones a la     2. Impuesto sobre la renta ");
		check(IRPF, "   Seguridad Social y conceptos de recaudación conjunta    de la personas físicas.....................    4,00%    57,51 ");
		check(CC, "Contingencias Comunes    1.643,58    4,70%    77,25    3. Anticipos......................................................      ");
		check(UNEMPLOYMENT, "Desempleo    1.643,58    1,55%    25,48    4. Valor de los productos");
		check(EXTRAH, "Horas Extraordinarias    5. Otras deducciones               ");
		check(OVERWHELMING, "Fuerza Mayor         2,00%         ................................................................     ");
		check(OTHER_EXTRAS, "Resto Horas Extras         4,70%         ................................................................     ");
		check(TOTAL_DEDUCTION, "B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)......................    161,88");
		check(LIQUID, "LIQUIDO TOTAL A PERCIBIR (A-B)...    1.700,00Firma y Sello de la Empresa");
		check(LIQUID, "TOTAL SALARIO LIQUIDO........................    1.275,80Firma y Sello de la Empresa");
		check(ISSUE_DATE, "ZARAGOZA, 31 de marzo de 2021");
		check(MONTHLY_PAYMENT, "NORMALIZADA    EMPRESAImporte remuneración mensual...............................................................    1.437,68");
		check(MONTHLY_PAYMENT, "NORMALIZADA    EMPRESAImporte remuneración mensual................................................    1.450,82");
		check(EXTRA_PR, "Prorrata pagas extraordinarias................................................................    205,90");
		check(BASE_IT, "Base incapacidad temporal.....................................................................     ");
		check(TOTAL_CC, "TOTAL..............    1.643,58    1.643,58    23,60%    387,88");
		check(ATEP, "AT y EP.....................    3,90%    64,10");
		check(CC_TITLE, "2. Contingencias profesionales");
		check(UNEMPLOYMENT_EE, "Desempleo............................................    5,50%    90,40");
		check(CGP, "(AT.y EP.) y conceptos de    1.643,58    1.643,58");
		check(FP_E, "Formación Profesional............................    0,60%    9,86");
		check(RECAUDACION, "recaudación conjunta ");
		check(FOGASA, "Fondo Garantía Salarial...........................    0,20%    3,29");
		check(EXTRAQUOTE, "3. Cotización adicional por horas extraordinarias...................................................................          ");
		check(IRPF_EE, "4. Base sujeta a retención del I.R.P.F..............................................................................    1.437,68");
	}

}
