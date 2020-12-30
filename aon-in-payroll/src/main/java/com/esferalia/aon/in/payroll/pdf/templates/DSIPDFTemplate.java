package com.esferalia.aon.in.payroll.pdf.templates;

import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.templates.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext.DateFormatException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DSIPDFTemplate implements SalaryPDFTemplate{
	
	public static final DSIPDFTemplate DSI_PDF_TEMPLATE = new DSIPDFTemplate();
	
	/*salaryBuilder.setContract(
	new PDFContract()
	.setCcc(ccc) 
	.setNaf(naf)
	.setNif(nif)
	.setCif(cif)
	.setEndDate(endDate)
	.setStartDate(startDate)
	.setEmployeeCode(employeeCode)
	.setEnterpriseCode(enterpriseCode)
	.setEmployeeName(employeeName)
	.setEnterpriseName(enterpriseName)
	);*/

	
	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))){
			
			Double totalSS = 0d;
			
			Matcher matcher = find(reader, ENTERPRISE_WORKER);
			String employeeName = AonStringUtils.trimToNull(matcher.group("name"));
			salaryBuilder.setEmployeeName(employeeName);
			String enterpriseName = AonStringUtils.trimToNull(matcher.group("enterprise"));
			salaryBuilder.setEnterpriseName(enterpriseName);
			matcher = find(reader, HOME_NIF);
			salaryBuilder.setEmployeeAddress(AonStringUtils.trimToNull(matcher.group("home")));
			String nif = AonStringUtils.trimToNull(matcher.group("nif"));
			salaryBuilder.setEmployeeDocument(nif);
			matcher = find(reader, CIF_NSS);
			String cif = AonStringUtils.trimToNull(matcher.group("cif"));
			salaryBuilder.setEnterpriseDocument(cif);
			String naf = AonStringUtils.trimToNull(matcher.group("nss").replaceAll("/", ""));
			salaryBuilder.setSocialSecurityNumber(naf);
			matcher = find(reader, CATEGORY);
			salaryBuilder.setCategory(AonStringUtils.trimToNull(matcher.group("category")));
			matcher = find(reader, NSS_GROUP_OLD);
			String ccc = AonStringUtils.trimToNull(matcher.group("nss"));
			if(ccc!=null) {
				ccc = ccc.replaceAll("/", "");
			}
			String seniority = AonStringUtils.trimToNull(matcher.group("seniority"));
			if(seniority!=null) {
				try {
					String[] dmy=seniority.split("/");
					if(dmy.length!=3)
						throw new Exception();
					Integer day = Integer.parseInt(dmy[0]);
					Integer month = Integer.parseInt(dmy[1]);
					Integer year = Integer.parseInt(dmy[2]);
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, day);
					calendar.set(Calendar.MONTH, month -1   );
					calendar.set(Calendar.YEAR, year   );
					
					calendar.set(Calendar.HOUR_OF_DAY, 12);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					calendar.set(Calendar.ZONE_OFFSET, 2);
					
					salaryBuilder.setSeniorityDate(calendar.getTime());
				} catch (Exception e) {}
			}
			salaryBuilder.setCcc(ccc);
			String quoteGroup = AonStringUtils.trimToNull(matcher.group("quotegroup"));
			salaryBuilder.setQuoteGroup(quoteGroup);
			//salaryBuilder.setSeniorityDate(seniorityDate);
			matcher = find(reader, LIQPERIOD_TOTDAYS);
			String liqString = AonStringUtils.trimToNull(matcher.group("liqper"));
			Date dFrom = null;
			Date dTo = null;
			String strDays = AonStringUtils.trimToNull(matcher.group("totdays"));
			if(strDays != null) {
				salaryBuilder.setTimeUnits(Integer.parseInt(strDays));
			}
			if( liqString != null && liqString.contains("del") && liqString.contains("al")) {
				matcher = LIQPERIOD.matcher(liqString);
				if(matcher.matches()) {
					
					
					String strFrom =  matcher.group("dayfrom")+" "+AonStringUtils.trimToNull(matcher.group("monthfrom"))+" "+AonStringUtils.trimToNull(matcher.group("year"));
					String strTo = matcher.group("dayto")+" "+AonStringUtils.trimToNull(matcher.group("monthto"))+" "+matcher.group("year");
					try {
						dFrom = dsiDateParser(strFrom);
						dTo = dsiDateParser(strTo);
						salaryBuilder.setStartDate(dFrom);
						salaryBuilder.setEndDate(dTo);
						salaryBuilder.setChargeDate(dTo);
					} catch (NullPointerException e) {}
					
				}
			}
			Period per = new Period(dFrom, dTo);
			salaryBuilder.setContract(
					new PDFContract()
					.setCcc(ccc) 
					.setNaf(naf)
					.setNif(nif)
					.setCif(cif)
					.setEndDate(dTo)
					.setStartDate(dFrom)
					.setEmployeeName(employeeName)
					.setEnterpriseName(enterpriseName)
					);
			salaryBuilder.addData("__EMPLOYEE_CODE", new TimedObject<String>(quoteGroup, per));
			matcher = find(reader, PAYMENTS_HEADER);
			String line = reader.readLine();
			matcher = TOTAL_PAYMENT.matcher(line);
			while(!matcher.matches()) {
				matcher = NUMBER.matcher(line);
				while(matcher.find()) {
//					System.out.println(matcher.group());
					String strAmount = AonStringUtils.trimToNull(matcher.group("amount").replaceAll("\\.", "").replaceAll(",", "."));
					String concept = matcher.group("concept");
					matcher = DOUBLE_CONCEPT.matcher(concept);
					if(matcher.matches()) {
						concept = AonStringUtils.trimToNull(matcher.group("concept"));
					}
//					System.out.println("\t"+concept);
//					System.out.println("\t"+strAmount);
					try {
						Double amount = Double.parseDouble(strAmount);
						PaymentType pt = PaymentType.CRA_0001;
						String description = null;
						switch (concept) {
							case "SALARIO BASE":
								description = "SALARIO_BASE";
								break;
							case "HORAS EXTRAORDINARIAS":
								description = "HORAS_EXTRAS";
								pt = PaymentType.CRA_0002;
								break;
						}
						
						salaryBuilder.addPayment(amount
								, amount
								, amount
								, concept
								, dFrom
								, dTo
								, new Payment().setType(pt).setName(description)
								, Collections.emptyMap());
					} catch (NumberFormatException e) {
						System.err.println("MAL");
					}
					
					
				}
				line = reader.readLine();
				matcher = TOTAL_PAYMENT.matcher(line);
				
			}
			
			if(matcher.group("totalpayment")!=null) {
				String strTotalPayment = AonStringUtils.trimToNull(matcher.group("totalpayment")).replaceAll("\\.", "").replaceAll(",", ".");
				try {
					salaryBuilder.setTotalPayment(Double.parseDouble(strTotalPayment));
				} catch (NumberFormatException e) {
					System.err.println("MAL");
				}
			}
			matcher = find(reader, DEDUCTIONS_HEADER);
			line = reader.readLine();
			matcher = TOTAL_APPORT.matcher(line);
			while (!matcher.matches()) {
				
				matcher = DEDUCTION.matcher(line);
 				while (matcher.find()) {
//					System.err.println(matcher.group());
//					System.err.println("\t"+matcher.group("concept"));
//					System.err.println("\t"+matcher.group("base"));
//					
//					System.err.println("\t"+matcher.group("percentage"));
//					System.err.println("\t"+matcher.group("deduction"));
					Double amount = null;
					
					String concept = AonStringUtils.trimToNull(matcher.group("concept")).toUpperCase();
					String strBase = AonStringUtils.trimToNull(matcher.group("base"));
					ContextVariable con = ContextVariable.NULL;
					
					String context = concept;
					DeductionType dt = DeductionType.OTHER;
					
					try {
						
						amount = Double.parseDouble(matcher.group("deduction").replaceAll("\\.", "").replaceAll(",", "."));
						Double base = Double.parseDouble(strBase.replaceAll("\\.", "").replaceAll(",", "."));
						
						if (concept.contains("PERSONAS FÍSICAS")) {
							dt = DeductionType.IRPF;
							concept=dt.getName(new Locale("es", "ES"));
							context = "IRPF";
							con = ContextVariable.IRPF_BASE;
							
						}
						
						
						else if (concept.contains("DESEMPLEO")) {
							dt = DeductionType.UNEMPLOYMENT;
							concept=dt.getName(new Locale("es", "ES"));
							context = "DESEMPL";
							con = ContextVariable.UNEMPLOY_EMPLOYEE;
							salaryBuilder.setCgpBase(base);
						}
						else if (concept.contains("CONTINGENCIAS COMUNES")) {
							dt = DeductionType.COMMON_CONTINGENCY;
							concept=dt.getName(new Locale("es", "ES"));
							context = "CGC";
							con = ContextVariable.CGC_EMPLOYEE;
							salaryBuilder.setCgcBase(base);
							salaryBuilder.setRawCgcBase(base);
						}
						else if (concept.contains("HORAS EXTRAORDINARIAS")) {
							//dt = DeductionType.NON_STRUCTURAL_OVERTIME;
							//description=dt.getName(new Locale("es", "ES"));
							con = ContextVariable.EXTRA_HOURS;
						}
						else if (concept.contains("FORMACIÓN PROFESIONAL")) {
							dt = DeductionType.JOB_TRAINING;
							concept=dt.getName(new Locale("es", "ES"));
							context = "FP";
							con = ContextVariable.FP_EMPLOYEE;
						}
						
						
						if(amount!=null) {
							salaryBuilder.addDeduction(amount
									, concept
									, dFrom
									, dTo
									, new Deduction().setType(dt).setName(context)
									, Collections.emptyMap());
						}
						if ( strBase != null ) {
							if ( con != ContextVariable.NULL )
								salaryBuilder.addData(con.getName(), new TimedObject<Double>(base, per));
							else {
								salaryBuilder.addData(concept, new TimedObject<Double>(base, per));
							}
						}
					} catch (NumberFormatException | NullPointerException e) {
					}
					
					
					
					
					
				}
				
				line = reader.readLine();
				matcher = TOTAL_APPORT.matcher(line);
			}
			{
				String stramount = AonStringUtils.trimToNull(matcher.group("deduction"));
				if(stramount!=null) {
					stramount = stramount.replaceAll("\\.", "").replaceAll(",", ".");
					try {
						totalSS+=(Double.parseDouble(stramount));
					} catch (NumberFormatException e) {
						// TODO: handle exception
					}
				}
			}
			
			matcher = find(reader, TOTAL_DEDUCTION);
			{
				String stramount = AonStringUtils.trimToNull(matcher.group("amount"));
				if(stramount!=null) {
					stramount = stramount.replaceAll("\\.", "").replaceAll(",", ".");
					try {
						salaryBuilder.setTotalDeduction(Double.parseDouble(stramount));
					} catch (NumberFormatException e) {
						// TODO: handle exception
					}
				}
			}
			
			
			matcher = find(reader, TOTAL_LIQUID);
			try {
				Double totalLiquid = Double.parseDouble(AonStringUtils.trimToNull(matcher.group("amount").replaceAll("\\.", "").replaceAll(",", ".")));
				salaryBuilder.setTotalLiquid(totalLiquid);
			} catch (NullPointerException | NumberFormatException e) {}
			
			matcher = find(reader, ISSUE_DATE);
			try {
				Integer day = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("day")));
				Integer month = Integer.parseInt(monthChooser(AonStringUtils.trimToNull(matcher.group("month"))));
				Integer year = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("year")));
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.MONTH, month -1   );
				calendar.set(Calendar.YEAR, year   );
				
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.ZONE_OFFSET, 2);
				
				salaryBuilder.setIssueDate(calendar.getTime());
			} catch (NullPointerException | DateFormatException | NumberFormatException e) {}
			
			matcher = find(reader, ENTERPRISE_APPORT_HEADER_2);

			matcher = find(reader, CC_MONTHLY);
			
			String remuneration = AonStringUtils.trimToNull(matcher.group("amount"));
			try {
				if (remuneration != null) {
					salaryBuilder.setRemuneration(Double.parseDouble(remuneration.replaceAll("\\.", "").replaceAll(",", ".")));
				}
			} catch (NumberFormatException e) {
			}
			
			matcher = find(reader, CC_EXTRA);
			{
				String extra_pro = AonStringUtils.trimToNull(matcher.group("amount"));
				try {
					if (extra_pro != null) {
						salaryBuilder.setProExtBase(Double.parseDouble(extra_pro.replaceAll("\\.", "").replaceAll(",", ".")));
					}
				} catch (NumberFormatException e) {
				}
			}
			matcher = find(reader, IT_BASE);
			{
				String str_it = AonStringUtils.trimToNull(matcher.group("amount"));
				try {
					if (str_it != null) {
						salaryBuilder.setItBase(Double.parseDouble(str_it.replaceAll("\\.", "").replaceAll(",", ".")));
					}
				} catch (NumberFormatException e) {
				}
			}
			matcher = find(reader, CC);
//			String raw_cgc_base = AonStringUtils.trimToNull(matcher.group("amount1"));
//			if(raw_cgc_base != null) {
//				raw_cgc_base = raw_cgc_base.replaceAll("\\.", "").replaceAll(",", ".");
//				try {
//					salaryBuilder.setRawCgcBase(Double.parseDouble(raw_cgc_base));
//				} catch (NumberFormatException e) {}
//			}
//			String cgcBase = AonStringUtils.trimToNull(matcher.group("amount2"));
//			if(raw_cgc_base != null) {
//				cgcBase = cgcBase.replaceAll("\\.", "").replaceAll(",", ".");
//				try {
//					salaryBuilder.setCgcBase(Double.parseDouble(cgcBase));
//				} catch (NumberFormatException e) {}
//			}
			{
				//String str_cc_percent = AonStringUtils.trimToNull(matcher.group("percent"));
				String str_cc_cost = AonStringUtils.trimToNull(matcher.group("amount3"));
				String str_cc_raw = AonStringUtils.trimToNull(matcher.group("amount1"));
				String str_cc_base = AonStringUtils.trimToNull(matcher.group("amount2"));
				if(str_cc_cost!=null) {
					str_cc_cost = str_cc_cost.replaceAll("\\.", "").replaceAll(",", ".");
					try {
						Double cc_cost = Double.parseDouble(str_cc_cost);
						String description=COMMON_CONTINGENCY.getName(new Locale("es", "ES"));
						Deduction costDeduction=new Deduction().setType(COMMON_CONTINGENCY).setName("CGC_E");
						salaryBuilder.addCost(cc_cost, description, dFrom, dTo, costDeduction, Collections.emptyMap());
						totalSS+=cc_cost;
					} catch (NumberFormatException e) {}
				}
				if(str_cc_raw != null) {
					str_cc_raw = str_cc_raw.replaceAll("\\.", "").replaceAll(".", ",");
					try {
						salaryBuilder.addData(ContextVariable.CGC_BASE_RAW.getName(), new TimedObject<Double>(Double.parseDouble(str_cc_raw), per));
					} catch (NumberFormatException e) {}
				}
				if(str_cc_base != null) {
					str_cc_base = str_cc_base.replaceAll("\\.", "").replaceAll(".", ",");
					try {
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(), new TimedObject<Double>(Double.parseDouble(str_cc_base), per));
					} catch (NumberFormatException e) {}
				}
			}
			matcher = find(reader, AT_EP);
			{
				//String str_at_ep_percent = AonStringUtils.trimToNull(matcher.group("percent"));
				String str_at_ep_cost = AonStringUtils.trimToNull(matcher.group("cost"));
				if (str_at_ep_cost!=null) {
					str_at_ep_cost= str_at_ep_cost.replaceAll("\\.", "").replaceAll(",", ".");
					try {
						Double at_ep_cost = Double.parseDouble(str_at_ep_cost);
						String description=DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES"));
						Deduction costDeduction=new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IT_E");
						salaryBuilder.addCost(at_ep_cost, description, dFrom, dTo, costDeduction, Collections.emptyMap());
						totalSS+=at_ep_cost;
					} catch (NumberFormatException e) {}
				}
			}
			matcher = find(reader, UNEMPLOYMENT_HEADER);
			matcher = find(reader, PROF_CONT_BASES);
			{
				//String str_raw_prof_cont_base = AonStringUtils.trimToNull(matcher.group("raw"));
				String str_prof_cont_base = AonStringUtils.trimToNull(matcher.group("normal"));
				/*if (str_raw_prof_cont_base != null) {
					str_raw_prof_cont_base = str_prof_cont_base.replaceAll("\\.", "").replaceAll(",", ".");
					Double raw_prof_cont_base = Double.parseDouble(str_raw_prof_cont_base);
					salaryBuilder.addData(ContextVariable.CGP_BASE.getName(), new TimedObject<Double> (raw_prof_cont_base, per));
				}*/
				if(str_prof_cont_base != null) {
					try {
						str_prof_cont_base = str_prof_cont_base.replaceAll("\\.", "").replaceAll(",", ".");
						Double prof_cont_base = Double.parseDouble(str_prof_cont_base);
						salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(), new TimedObject<Double> (prof_cont_base, per));
						salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(), new TimedObject<Double> (prof_cont_base, per));
						salaryBuilder.addData(ContextVariable.FP_ENTERPRISE.getName(), new TimedObject<Double> (prof_cont_base, per));
						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(), new TimedObject<Double> (prof_cont_base, per));
					} catch (NumberFormatException e) {}
				}
			}
			matcher = find(reader, UNEMPLOYMENT_COST);
			{
				//String str_unem_percent = AonStringUtils.trimToNull(matcher.group("percent"));
				String str_unem_cost = AonStringUtils.trimToNull(matcher.group("cost"));
				if (str_unem_cost!=null) {
					str_unem_cost= str_unem_cost.replaceAll("\\.", "").replaceAll(",", ".");
					try {
						Double unem_cost = Double.parseDouble(str_unem_cost);
						String description=DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES"));
						Deduction costDeduction=new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESEMPL_E");
						salaryBuilder.addCost(unem_cost, description, dFrom, dTo, costDeduction, Collections.emptyMap());
						totalSS+=unem_cost;
					} catch (NumberFormatException e) {}
				}
			}
			matcher = find(reader, FP_COST);
			//String str_fp_percent = AonStringUtils.trimToNull(matcher.group("percent"));
			String str_fp_cost = AonStringUtils.trimToNull(matcher.group("cost"));
			if (str_fp_cost!=null) {
				str_fp_cost= str_fp_cost.replaceAll("\\.", "").replaceAll(",", ".");
				try {
					Double fp_cost = Double.parseDouble(str_fp_cost);
					String description=DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
					Deduction costDeduction=new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
					salaryBuilder.addCost(fp_cost, description, dFrom, dTo, costDeduction, Collections.emptyMap());
					totalSS+=fp_cost;
				} catch (NumberFormatException e) {}
			}
			matcher = find(reader, FOGASA_COST);
			//String str_fogasa_percent = AonStringUtils.trimToNull(matcher.group("percent"));
			String str_fogasa_cost = AonStringUtils.trimToNull(matcher.group("cost"));
			if (str_fogasa_cost!=null) {
				str_fogasa_cost= str_fogasa_cost.replaceAll("\\.", "").replaceAll(",", ".");
				try {
					Double fogasa_cost = Double.parseDouble(str_fogasa_cost);
					String description=DeductionType.FOGASA.getName(new Locale("es", "ES"));
					Deduction costDeduction=new Deduction().setType(DeductionType.FOGASA).setName("FOGASA");
					salaryBuilder.addCost(fogasa_cost, description, dFrom, dTo, costDeduction, Collections.emptyMap());
					totalSS+=fogasa_cost;
				} catch (NumberFormatException e) {}
			}
			
			matcher = find(reader, EXTRA_H);
			{
				String str_h_extra = AonStringUtils.trimToNull(matcher.group("base"));
				if (str_h_extra != null) {
					try {
						Double h_extra = Double.parseDouble(str_h_extra.replaceAll("\\.", "").replaceAll(",", "."));
						salaryBuilder.addData(ContextVariable.EXTRA_HOURS.getName(), new TimedObject<Double>(h_extra, per));
						salaryBuilder.setHExtraBase(h_extra);
					} catch (NumberFormatException e) {
					}
				}
			}
			matcher = find(reader, IRPF);
			{
				String str_irpf = AonStringUtils.trimToNull(matcher.group("base"));
				if (str_irpf != null) {
					try {
						Double irpf = Double.parseDouble(str_irpf.replaceAll("\\.", "").replaceAll(",", "."));
						salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<Double>(irpf, per));
						salaryBuilder.setIrpfBase(irpf);
					} catch (NumberFormatException e) {
					}
				}
			}
			totalSS = Math.round(totalSS*100.0)/100.0;
			salaryBuilder.setTotalSS(totalSS);
			salaryBuilder.getSalary();
		}
		return this;
	}
	
	
	private String monthChooser(String mes) {
		switch (mes.toUpperCase()) {
			case "ENERO":
				return "01";
			case "FEBRERO":
				return "02";
			case "MARZO":
				return "03";
			case "ABRIL":
				return "04";
			case "MAYO":
				return "05";
			case "JUNIO":
				return "06";
			case "JULIO":
				return "07";
			case "AGOSTO":
				return "08";
			case "SEPTIEMBRE":
				return "09";
			case "OCTUBRE":
				return "10";
			case "NOVIEMBRE":
				return "11";
			case "DICIEMBRE":
				return "12";
		}
		return null;
	}
	
	
	
	private static Date dsiDateParser(final String date){
		Pattern dPatt=Pattern.compile("\\s*(?<day>\\d{1,2})\\s*(?<esmonth>\\w+)\\s*(?<year>\\d+)\\s*");
		try {
			Matcher m=dPatt.matcher(date);
			if(m.matches()) {
				int day=Integer.parseInt(m.group("day"));
				int year=Integer.parseInt(m.group("year"));
				int month;
				String strMonth=m.group("esmonth");
				if((strMonth.equalsIgnoreCase("ENE"))||(strMonth.equalsIgnoreCase("ENERO"))) {
					month=1;
				}
				else if ((strMonth.equalsIgnoreCase("FEB"))||(strMonth.equalsIgnoreCase("FEBRERO"))) {
					month=2;
				}
				else if ((strMonth.equalsIgnoreCase("MAR"))||(strMonth.equalsIgnoreCase("MARZO"))) {
					month=3;
				}
				else if ((strMonth.equalsIgnoreCase("ABR"))||(strMonth.equalsIgnoreCase("ABRIL"))) {
					month=4;
				}
				else if ((strMonth.equalsIgnoreCase("MAY"))||(strMonth.equalsIgnoreCase("MAYO"))) {
					month=5;
				}
				else if ((strMonth.equalsIgnoreCase("JUN"))||(strMonth.equalsIgnoreCase("JUNIO"))) {
					month=6;
				}
				else if ((strMonth.equalsIgnoreCase("JUL"))||(strMonth.equalsIgnoreCase("JULIO"))) {
					month=7;
				}
				else if ((strMonth.equalsIgnoreCase("AGO"))||(strMonth.equalsIgnoreCase("AGOSTO"))) {
					month=8;
				}
				else if ((strMonth.equalsIgnoreCase("SEP"))||(strMonth.equalsIgnoreCase("SEPTIEMBRE"))) {
					month=9;
				}
				else if ((strMonth.equalsIgnoreCase("OCT"))||(strMonth.equalsIgnoreCase("OCTUBRE"))) {
					month=10;
				}
				else if ((strMonth.equalsIgnoreCase("NOV"))||(strMonth.equalsIgnoreCase("NOVIEMBRE"))) {
					month=11;
				}
				else if ((strMonth.equalsIgnoreCase("DIC"))||(strMonth.equalsIgnoreCase("DICIEMBRE"))) {
					month=12;
				}
				else {
					return null;
				}
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.MONTH, month -1   );
				calendar.set(Calendar.YEAR, year   );
				
				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.ZONE_OFFSET, 2);
				
				return calendar.getTime();
			}
			else {
				return null;
			}
			
		} catch (Exception e) {
			System.err.println(e.getClass());
			return null;
		}
	}
	
	
	
	//	Empresa: EMPRESA S.L. Trabajador: ANDREA ANDREA, MARIA
	private static Pattern ENTERPRISE_WORKER =
	Pattern.compile("^\\s*Empresa:\\s*(?<enterprise>.+?)\\s*Trabajador:\\s*(?<name>[\\w\\s,]+)\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//	Domicilio: CL VIA, 12 N.I.F.: 37723953C Número Libro de Matrícula:
	private static Pattern HOME_NIF =
	Pattern.compile("\\s*Domicilio:\\s*(?<home>.+?)\\s*N\\.I\\.F\\.:\\s*(?<nif>[\\d\\w]\\d{7}\\w)\\s*Número\\s*Libro\\s*de\\s*Matrícula:(?<matricula>.*)"
	, Pattern.CASE_INSENSITIVE);
	//	C.I.F.: B50671908 Nº de Afiliación a la Seguridad Social: 08/02983860/69
	private static Pattern CIF_NSS =
	Pattern.compile("\\s*C\\.I\\.F\\.:\\s*(?<cif>[\\w\\d]+)\\s*Nº\\s*de\\s*Afiliación\\s*"
			+ "a\\s*la\\s*Seguridad\\s*Social:\\s*(?<nss>[\\d/]+)\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Código de Cuenta de Cotización a la Categoría o Grupo Profesional:
	private static Pattern CATEGORY =
	Pattern.compile("\\s*Código\\s*de\\s*Cuenta\\s*de\\s*Cotización\\s*a\\s*la\\s*Categoría\\s*o\\s*"
			+ "Grupo\\s*Profesional:(?<category>.*)"
	, Pattern.CASE_INSENSITIVE);
//	Seguridad Social: 50/8745111/93 Grupo de Cotización: 02 Fecha antigüedad: 14/09/1972
	private static Pattern NSS_GROUP_OLD =
	Pattern.compile("\\s*Seguridad\\s*Social:\\s*(?<nss>[\\d/]+)\\s*Grupo\\s*de\\s*Cotización:\\s*(?<quotegroup>\\d+)?\\s*Fecha\\s*antigüedad:\\s*(?<seniority>[\\d/]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Periodo de Liquidación:    del 1 de febrero al 29 de febrero de 2020 Total días 30
	private static Pattern LIQPERIOD_TOTDAYS =
	Pattern.compile("\\s*Periodo\\s*de\\s*Liquidación:(?<liqper>.+?)Total\\s*días\\s*(?<totdays>\\d+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static Pattern LIQPERIOD =		
	Pattern.compile("\\s*del\\s*(?<dayfrom>\\d+)\\s*de\\s*(?<monthfrom>\\w+)\\s*al\\s*(?<dayto>\\d+)\\s*de\\s*(?<monthto>\\w+)\\s*de\\s*(?<year>\\d+)\\s*"
	, Pattern.CASE_INSENSITIVE);
//	I. DEVENGOS TOTALES
	private static Pattern DEVENGOS_HEADER =
	Pattern.compile("\\s*I\\.\\s*DEVENGOS\\s*TOTALES\\s*"
	, Pattern.CASE_INSENSITIVE);
//	1. Percepciones salariales 2. Percepciones no salariales
	private static Pattern PAYMENTS_HEADER =
	Pattern.compile("\\s*1\\.\\s*Percepciones\\s*salariales\\s*2\\.\\s*Percepciones\\s*no\\s*salariales\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	/*public static final Pattern PAYMENTS =
			//"\\s*(?<salaryconcept>\\w[\\w\\s\\.,]*?)?[\\.]{2,}?(?:(.+?))?[\\.]{2,}?\\s*(?<salaryamount>\\d[\\.\\d,]+)?\\s*(?<nonsalaryconcept>[^\\d\\s\\.][\\w\\s\\.,]*?)?\\s*(?<nonsalaryamount>\\d[\\.\\d,]+)?\\s*"
	Pattern.compile("\\s*(?<salaryconcept>^(\\.{2,})).*"
	, Pattern.CASE_INSENSITIVE);*/
//	A. TOTAL SALARIO DEVENGADO........................ 758,84
	public static final Pattern TOTAL_PAYMENT =
	Pattern.compile("\\s*A\\.\\s*TOTAL\\s*SALARIO\\s*DEVENGADO\\.*\\s*(?<totalpayment>\\d[\\.\\d,]*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	SALARIO BASE............................................................... 389,35 Indemnizaciones o suplidos
//	HORAS EXTRAORDINARIAS........................................... ...................................................................................
//	GRATIF.EXTRAORDINARIAS........................................... Prestaciones e indemnizaciones de la Seguridad Social
//	SALARIO EN ESPECIE................................................... ...................................................................................
//	Complementos salariales ...................................................................................
//	ANTIGUEDAD.................................................................. 118,16 Indemnizaciones por traslados, suspensiones o despidos
//	COMPL............................................................................ 8,99 ...................................................................................
//	NOCTURNO.................................................................... 95,03 Otras percepciones no salariales
//	ESTUDIOS....................................................................... 147,31 ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
	public static Pattern NUMBER =
	Pattern.compile("(?<concept>.+?)(?:\\.{2,}([^\\.]+\\.*)?)\\s+(?<amount>\\d+[\\.\\d]+,\\d{2})");
	public static Pattern DOUBLE_CONCEPT =
	Pattern.compile(".+?\\.{2,}\\s(?<concept>.+)"
	, Pattern.CASE_INSENSITIVE);
//	II. DEDUCCIONES
	public static Pattern DEDUCTIONS_HEADER =
	Pattern.compile("\\s*II\\.\\s*DEDUCCIONES\\s*"
	, Pattern.CASE_INSENSITIVE);
//	1. Aportación del trabajador a las cotizaciones a la 2. Impuesto sobre la renta 
//	   Seguridad Social y conceptos de recaudación conjunta de la personas físicas..................... 16,00% 121,41
//	Contingencias Comunes 1.215,90 4,70% 57,15 3. Anticipos......................................................  
//	Desempleo 1.050,00 1,55% 16,28 4. Valor de los productos
//	Formación Profesional 1.050,00 0,10% 1,05 recibidos en especie.................    
//	Horas Extraordinarias 5. Otras deducciones
//	Fuerza Mayor  2,00%  DESCUENTO 1........................................ 25,00
//	Resto Horas Extras  4,70%  ................................................................  
	public static Pattern DEDUCTION =
	Pattern.compile("(?<concept>[^%]+?)(?:\\.{2,})?\\s*(?<base>\\d[\\.\\d]*,\\d*)?\\s*(?<percentage>\\d[\\.\\d]*,\\d*%)?\\s*(?<deduction>\\d[\\.\\d,]*,\\d*)(?:\\s|$)"
	, Pattern.CASE_INSENSITIVE);
//	TOTAL APORTACIONES................................................. 74,48	
	public static Pattern TOTAL_APPORT =
	Pattern.compile("\\s*TOTAL\\s*APORTACIONES\\.{2,}\\s*(?<deduction>\\d+[\\.\\d]*,\\d*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)...................... 220,89
	public static Pattern TOTAL_DEDUCTION =
	Pattern.compile("\\s*B\\.\\s*TOTAL\\s*DEDUCCIONES\\s*\\(S\\.SOCIAL-IRPF-\\.+\\)\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Firma y Sello de la Empresa TOTAL SALARIO LIQUIDO........................ 537,95
	public static Pattern TOTAL_LIQUID =
	Pattern.compile("\\s*Firma\\s*y\\s*Sello\\s*de\\s*la\\s*Empresa\\s*TOTAL\\s*SALARIO\\s*LIQUIDO\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
//	ZARAGOZA, 29 de febrero de 2020
	public static Pattern ISSUE_DATE =
	Pattern.compile("\\s*(?<place>[^,]+)?,?\\s*(?<date>(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+?)\\s*de\\s*(?<year>\\d{4}))\\s*"
	, Pattern.CASE_INSENSITIVE);
//	RECIBI,
//	INFORMACION ADICIONAL:
//	Texto de informacion adicional
	
//	DETERMINACION DE LAS BASES DE COTIZACION A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACION CONJUNTA Y DE LA BASE 
	public static Pattern ENTERPRISE_APPORT_HEADER_1 =
	Pattern.compile("\\s*DETERMINACION\\s*DE\\s*LAS\\s*BASES\\s*DE\\s*COTIZACION\\s*A\\s*LA\\s*SEGURIDAD\\s*SOCIAL\\s*Y\\s*CONCEPTOS\\s*DE\\s*RECAUDACION\\s*CONJUNTA\\s*Y\\s*DE\\s*LA\\s*BASE\\s*"
	, Pattern.CASE_INSENSITIVE);
//	SUJETA A RETENCION DEL I.R.P.F. Y APORTACIÓN DE LA EMPRESA:
	public static Pattern ENTERPRISE_APPORT_HEADER_2 =
	Pattern.compile("\\s*SUJETA\\s*A\\s*RETENCION\\s*DEL\\s*I\\.R\\.P\\.F\\.\\s*Y\\s*APORTACIÓN\\s*DE\\s*LA\\s*EMPRESA:\\s*"
	, Pattern.CASE_INSENSITIVE);
//	1. Contingencias comunes BASE BASE TIPO APORTACIÓN
	public static Pattern CC_HEADER =
	Pattern.compile("\\s*1\\.\\s*Contingencias\\s*comunes\\s*BASE\\s*BASE\\s*TIPO\\s*APORTACIÓN\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Importe remuneración mensual............................................................... 758,84 NORMALIZADA EMPRESA
	public static Pattern CC_MONTHLY =
	Pattern.compile("\\s*Importe\\s*remuneración\\s*mensual\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*NORMALIZADA\\s*EMPRESA\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Prorrata pagas extraordinarias................................................................ 126,47
	public static Pattern CC_EXTRA =
	Pattern.compile("\\s*Prorrata\\s*pagas\\s*extraordinarias\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Base incapacidad temporal.....................................................................  
//	Base incapacidad temporal..................................................................... 455,00
	public static Pattern IT_BASE =
	Pattern.compile("\\s*Base\\s*incapacidad\\s*temporal\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*"
	, Pattern.CASE_INSENSITIVE);	
//	TOTAL.............. 885,31 1.215,90 23,60% 286,95
//TOTAL..............  746,00 23,60% 176,06
	public static Pattern CC =
	Pattern.compile("\\s*TOTAL\\.{2,}\\s*((((?<amount1>\\d[\\.\\d,]*,\\d+)?\\s(?<amount2>\\d[\\.\\d,]*,\\d+))?\\s*(?<percent>\\d[\\.\\d,]*,\\d+)%\\s*(?<amount3>\\d[\\.\\d,]*,\\d+)?)|%)\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	AT y EP..................... 1,50% 15,75
	public static Pattern AT_EP =
	Pattern.compile("\\s*AT\\s*y\\s*EP\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	2. Contingencias profesionales
	public static Pattern CP_HEADER_1 =
	Pattern.compile("\\s*2\\.\\s*Contingencias\\s*profesionales\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	(AT.y EP.) y conceptos de
	public static Pattern CP_HEADER_2 =
	Pattern.compile("\\s*\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	Desempleo............................................
	public static Pattern UNEMPLOYMENT_HEADER =
	Pattern.compile("\\s*Desempleo\\.{2,}\\s*"
	, Pattern.CASE_INSENSITIVE);
//	885,31 1.050,00
	public static Pattern PROF_CONT_BASES=
	Pattern.compile("\\s*((?<raw>\\d[\\.\\d]*,\\d+)?\\s*(?<normal>\\d[\\.\\d]*,\\d+))?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	5,50% 57,75
	public static Pattern UNEMPLOYMENT_COST=
	Pattern.compile("\\s*(?<percent>\\d[\\d\\.]*,\\d+)?%?\\s*(?<cost>\\d[\\d\\.]*,\\d+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
//	recaudación conjunta 
	public static Pattern CP_HEADER_3=
	Pattern.compile("\\s*recaudación\\s*conjunta\\s*"
	, Pattern.CASE_INSENSITIVE);
//	Formación Profesional............................ 0,60% 6,30
	public static Pattern FP_COST=
	Pattern.compile("\\s*Formación\\s*Profesional\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	Fondo Garantía Salarial........................... 0,20% 2,10
	public static Pattern FOGASA_COST=
	Pattern.compile("\\s*Fondo\\s*Garantía\\s*Salarial\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$"
	, Pattern.CASE_INSENSITIVE);
//	3. Cotización adicional por horas extraordinarias...................................................................   
	public static Pattern EXTRA_H=
	Pattern.compile("\\s*3\\.\\s*Cotización\\s*adicional\\s*por\\s*horas\\s*extraordinarias\\.{2,}\\s*(?<base>\\d[\\.\\d,]*,\\d+)?.*$"
	, Pattern.CASE_INSENSITIVE);
//	4. Base sujeta a retención del I.R.P.F.............................................................................. 758,84
	public static Pattern IRPF=
	Pattern.compile("\\s*4\\.\\s*Base\\s*sujeta\\s*a\\s*retención\\s*del\\s*I\\.R\\.P\\.F\\.{2,}\\s*(?<base>\\d[\\.\\d,]*,\\d+)?.*$"
	, Pattern.CASE_INSENSITIVE);



	

	
	

//	SALARIO BASE..........(29,2000 x 17,00 días)...................... 496,40 Indemnizaciones o suplidos
//	HORAS EXTRAORDINARIAS........................................... ...................................................................................
//	GRATIF.EXTRAORDINARIAS........................................... Prestaciones e indemnizaciones de la Seguridad Social
//	SALARIO EN ESPECIE................................................... ACCID./ENF.PROF del 16 al 29.................................... 341,25
//	Complementos salariales ...................................................................................
//	INCENTIVOS............(6,6667 x 17,00 días)......................... 113,33 Indemnizaciones por traslados, suspensiones o despidos
//	........................................................................................ ...................................................................................
//	........................................................................................ Otras percepciones no salariales
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	A. TOTAL SALARIO DEVENGADO........................ 950,98
//	II. DEDUCCIONES
//	1. Aportación del trabajador a las cotizaciones a la 2. Impuesto sobre la renta 
//	   Seguridad Social y conceptos de recaudación conjunta de la personas físicas..................... 2,00% 19,02
//	Contingencias Comunes 1.206,40 4,70% 56,70 3. Anticipos......................................................  
//	Desempleo 1.206,40 1,55% 18,70 4. Valor de los productos
//	Formación Profesional 1.206,40 0,10% 1,21 recibidos en especie.................    
//	Horas Extraordinarias 5. Otras deducciones
//	Fuerza Mayor  2,00%  ................................................................  
//	Resto Horas Extras  4,70%  ................................................................  
//	TOTAL APORTACIONES................................................. 76,61
//	B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)...................... 95,63
//	Firma y Sello de la Empresa TOTAL SALARIO LIQUIDO........................ 855,35
//	ZARAGOZA, 30 de junio de 2020
//	RECIBI,
//	INFORMACION ADICIONAL:
//	DETERMINACION DE LAS BASES DE COTIZACION A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACION CONJUNTA Y DE LA BASE 
//	SUJETA A RETENCION DEL I.R.P.F. Y APORTACIÓN DE LA EMPRESA:
//	1. Contingencias comunes BASE BASE TIPO APORTACIÓN
//	Importe remuneración mensual............................................................... 609,73 NORMALIZADA EMPRESA
//	Prorrata pagas extraordinarias................................................................ 141,67
//	Base incapacidad temporal..................................................................... 455,00
//	TOTAL.............. 1.206,40 1.206,40 23,60% 284,71
//	AT y EP..................... 1,50% 18,10
//	2. Contingencias profesionales
//	(AT.y EP.) y conceptos de
//	Desempleo............................................
//	1.206,40 1.206,40
//	5,50% 66,35
//	recaudación conjunta 
//	Formación Profesional............................ 0,60% 7,24
//	Fondo Garantía Salarial........................... 0,20% 2,41
//	3. Cotización adicional por horas extraordinarias...................................................................   
//	4. Base sujeta a retención del I.R.P.F.............................................................................. 950,98	
	
	
private static final class Payment implements IPayment {
		
		String name;
		double amount;
		PaymentType type;
		String description;
		
		@Override
		public PaymentType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return null;
		}
		
		public Payment setName(String name) {
			this.name = name;
			return this;
		}

		public Payment setAmount(double amount) {
			this.amount = amount;
			return this;
		}

		public Payment setType(PaymentType type) {
			this.type = type;
			return this;
		}

		public Payment setDescription(String description) {
			this.description = description;
			return this;
		}
		
		
	}

	private static class Deduction implements IDeduction {
	
		String name;
		double amount;
		DeductionType type;
		String description;
	
		@Override
		public DeductionType getType() {
			return type;
		}
	
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}
	
		@Override
		public double getAmount() {
			return amount;
		}
	
		@Override
		public String getDescription() {
			return description;
		}
	
		@Override
		public String getExpression() {
			return null;
		}
		
		public Deduction setName(String name) {
			this.name = name;
			return this;
		}
	
		public Deduction setAmount(double amount) {
			this.amount = amount;
			return this;
		}
	
		public Deduction setType(DeductionType type) {
			this.type = type;
			return this;
		}
	
		public Deduction setDescription(String description) {
			this.description = description;
			return this;
		}		
		
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

}
