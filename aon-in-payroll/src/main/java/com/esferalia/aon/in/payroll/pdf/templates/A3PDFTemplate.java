package com.esferalia.aon.in.payroll.pdf.templates;

import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.templates.AltaiPDFTemplate.PDFContract;
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

public class A3PDFTemplate implements SalaryPDFTemplate {

	public static final A3PDFTemplate A3_PDF_TEMPLATE = new A3PDFTemplate();
	
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
	
	
	private static String removeSpace(String str, int... pos) {
		try {
			if(Arrays.stream(pos).anyMatch(new IntPredicate() {
				
				@Override
				public boolean test(int value) {
					if(value==-1)
						return true;
					return false;
				}
			})) {
				str=str.replaceAll(" ", "");
			}
			else {
				for (int i : pos) {
					if(str.charAt(i)==' ') {
						str=str.substring(0, i)+str.substring(i+1);
					}
				}	
			}
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			
		} finally {
			return str;
		}
	}
	
	
	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		

		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			Matcher matcher = find(reader, EMPLOYEE_NAME);
			String empName=AonStringUtils.trimToNull(matcher.group("name"));
			empName=removeSpace(empName, 13);
			
			matcher = find(reader, EMPLOYEE_ADDRESS);
			String empHome=null;
			try {
				empHome=AonStringUtils.trimToNull(matcher.group()).replaceAll("\\s{2,}", " ");
			} catch (NullPointerException e) {}
			
			
			
			matcher = find(reader, PC_AND_MUNICIPALITY);
			String empCity=AonStringUtils.trimToNull(matcher.group("municipality"));
			
			
			matcher = find(reader, NIF);
			String nif=AonStringUtils.trimToNull(matcher.group("nif"));
			
			matcher = find(reader, ENTERPRISE_HOME);
			String entName = AonStringUtils.trimToNull(matcher.group("enterprisename"));
			salaryBuilder.setEnterpriseName(entName);
			String entAddress = AonStringUtils.trimToNull(matcher.group("address"));
			entAddress = removeSpace(entAddress, 13);
			salaryBuilder.setEnterpriseAddress(entAddress);
			String ccc = AonStringUtils.trimToNull(matcher.group("nss")).replaceAll("[/]","").replaceAll("[-]", "");
			salaryBuilder.setCcc(ccc);
			
			salaryBuilder.setEmployeeName(empName);
			salaryBuilder.setEmployeeAddress(empHome);
			empCity=removeSpace(empCity, 6);
			salaryBuilder.setEmployeeCity(empCity);
			salaryBuilder.setEnterpriseDocument(nif);
			
			matcher = find(reader, WORKER_HEADER);
			
			matcher = find(reader, WORKER);
//			System.err.println(matcher.group());
//			System.err.println("JOB: "+matcher.group("job"));
//			System.err.println("OLD: "+matcher.group("old"));
//			System.err.println("NIF: "+matcher.group("nif"));
			salaryBuilder.setCategory(AonStringUtils.trimToNull(matcher.group("job")));
			//System.out.println(matcher.group("old"));
			String seniority=AonStringUtils.trimToNull(matcher.group("old"));
			salaryBuilder.setSeniorityDate(a3DateParser(seniority));
			String dni = AonStringUtils.trimToNull(matcher.group("nif"));
			salaryBuilder.setEmployeeDocument(dni);
			
			matcher = find(reader, SS_INFO);
			//Pattern.compile("\\s*(?<affnum>\\d+/\\d+-\\d+)\\s*(?<tarifa>\\d*)\\s*(?<codct>\\d*)\\s*(?<section>.*?)?\\s*(?<nro>\\d*)\\s*(?<period>.*\\s*a\\s*.*?)?\\s{2,}(?<days>\\d*)\\s*"

			
			String naf =AonStringUtils.trimToNull(matcher.group("affnum")).replaceAll("[/]","").replaceAll("[-]", "");
			salaryBuilder.setSocialSecurityNumber(naf);
			String quoteGroup=AonStringUtils.trimToNull(matcher.group("tarifa"));
			salaryBuilder.setQuoteGroup(quoteGroup);
			String codct=AonStringUtils.trimToNull(matcher.group("codct"));
			
			
			Pattern period=Pattern.compile("\\s*(?<type>\\w*)\\s*(?<from>\\d{1,2}\\s*\\w+\\s*\\d+)\\s*a\\s*(?<to>\\d{1,2}\\s*\\w+\\s*\\d+)\\s*", Pattern.CASE_INSENSITIVE);
			String periodo=matcher.group("period");
			//System.out.println(periodo);
			Matcher subMatcher=period.matcher(periodo);
			Date dTo=null;
			Date dFrom=null;
			Period per=null;
			if(subMatcher.matches()) {
				//charge and end dates
				String sdTo=subMatcher.group("to");
				dTo=a3DateParser(sdTo);
				salaryBuilder.setChargeDate(dTo);
				salaryBuilder.setEndDate(dTo);
				//start date
				String sdFrom=subMatcher.group("from");
				dFrom=a3DateParser(sdFrom);
				salaryBuilder.setStartDate(dFrom);
				per=new Period(dFrom, dTo);
			}
			try {
				Integer timeUnits = Integer.parseInt(matcher.group("days"));
				salaryBuilder.setTimeUnits(timeUnits);
				salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(timeUnits, per));
				
			} catch (NumberFormatException e) {
				
			}
			salaryBuilder.setContract(
					new PDFContract()
					.setCcc(ccc) 
					.setNaf(naf)
					.setNif(dni)
					.setCif(nif)
					.setEndDate(dTo)
					.setStartDate(dFrom)
					.setEmployeeName(empName)
					.setEnterpriseName(entName)
			);
			salaryBuilder.addData("TC2", new TimedObject<String>(codct, per));
			salaryBuilder.addData("GRUPO_COTIZACION", new TimedObject<String>(quoteGroup, per));
			
			matcher = find(reader, CONCEPT_HEADER);
			String concept_line = reader.readLine();
			matcher=TOTAL_HEADER.matcher(concept_line);
			//\\s*(?<cuantia>\\d+[,]\\d*)?\\s*(?<price>\\d+[,]\\d*)?\\s*(?<unknownnumber>\\d+)?\\s*(?<concept>\\*?.+?)\\s{1,2}(?<tipo>\\d+[,]\\d*)?\\s*(?<devengos>\\d+[,]\\d*)?\\s{19}?(?<deducciones>\\d+[,]\\d*)?\\s*$
			
			Double totalSS=0d;
			Integer payrollType = null;
			
			while(!matcher.matches()) {
				matcher = CONCEPT.matcher(concept_line);
				if(matcher.matches()) {
					if(matcher.group("devengos")!=null) {
						Double amount=a3DoubleParser(AonStringUtils.trimToNull(matcher.group("devengos")));
						String description=AonStringUtils.trimToNull(matcher.group("concept"));
						description=removeSpace(description, 19);	
						if(description.charAt(0)=='*' || description.charAt(0)=='-') {
							description=description.substring(1).trim();
						}
						String context=description;
						context=context.toUpperCase();
						context = context.replaceAll("\\s", "_");
						context = (context.length()>25)?context.substring(0, 24):context;
						PaymentType pt=null;
						if (payrollType ==null) {
							
							//NO FUNCIONA EL MÉTODO CONTAINSIGNORECASE
							
							pt=PaymentType.CRA_0001;
							if (AonStringUtils.containsIgnoreCase(description, "p.p.extras")
									|| AonStringUtils.containsIgnoreCase(description, "P.Pagas")) {
								//System.err.println("\t"+description);
								pt=PaymentType.CRA_0004;
								context="PAGA_EXTRA";
								salaryBuilder.setProExtBase(amount);
							}
							else if (AonStringUtils.containsIgnoreCase(description, "horas extras")) {
								pt=PaymentType.CRA_0002;
								context="HORAS_EXTRAS";
							}
							else if (AonStringUtils.containsIgnoreCase(description, "ESPECIE TRAB")) {
								pt=PaymentType.CRA_0013;
							}
							else if (AonStringUtils.containsIgnoreCase(description, "RETRI ESPECIE VEHICULO")) {
								pt=PaymentType.CRA_0016;
							}
							else if (AonStringUtils.containsIgnoreCase(description, "ATRASOS")) {
								pt = PaymentType.CRA_0008;
							}
							else if (AonStringUtils.containsIgnoreCase(description, "vacaciones")) {
//								if (payrollType == 1) {
//									pt = PaymentType.CRA_0006;
//								}
//								else {
									pt = PaymentType.CRA_0060;
//								}
							}
							else if (AonStringUtils.containsIgnoreCase(description, "estudio")) {
								pt = PaymentType.CRA_0025;
							}
							else if (AonStringUtils.containsIgnoreCase(description, "complemento i.t.")
									|| AonStringUtils.containsIgnoreCase(description,"Accidente")
									|| AonStringUtils.containsIgnoreCase(description, "enfermedad")) {
								pt = PaymentType.CRA_0000;
							}
							else if (AonStringUtils.containsIgnoreCase(description, "objetivo productividad")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 1T")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 2T")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 3T")
									|| AonStringUtils.containsIgnoreCase(description,"INCENT PRODUCTIVIDAD 4T")) {
								pt = PaymentType.CRA_0005;
							}
						}
						else if (payrollType==2) {
							pt=PaymentType.CRA_0009;
						}
						
						
						
						//añadir payment con prorr. paga
						salaryBuilder.addPayment(
								amount,
								amount,
								amount,
								description,
								dFrom,
								dTo,
								(IPayment) new Payment().setType(pt).setName(context),
								Collections.emptyMap());
						
					}
					else if(matcher.group("deducciones")!=null) {
						//public void addDeduction(Double amount, String description,
						//Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context);
						Double amount=a3DoubleParser(AonStringUtils.trimToNull(matcher.group("deducciones")));
						String description=AonStringUtils.trimToNull(matcher.group("concept"));
						description=removeSpace(description, 19);
						DeductionType dt=null;
						String context=null;
						Double type=null;
						try {
							type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("tipo")));
						} catch (NullPointerException e) {}
						if(description.contains("COTIZACION CONT.COMU")) {
							dt=DeductionType.COMMON_CONTINGENCY;
							description=dt.getName(new Locale("es", "ES"));
							totalSS+=amount;
							context="CGC";
							if (type!=null) {
								salaryBuilder.addData("PORCENTAJE_CGC", new TimedObject<Double>(type, per ));
							}
						}
						else if (description.contains("COTIZACION FORMACION")) {
							dt=DeductionType.JOB_TRAINING;
							description=dt.getName(new Locale("es", "ES"));
							totalSS+=amount;
							context="FP";
							if (type!=null) {
								salaryBuilder.addData("PORCENTAJE_FP", new TimedObject<Double>(type, per ));
							}
						}
						else if(description.contains("COTIZACION DESEMPLEO")) {
							dt=DeductionType.UNEMPLOYMENT;
							description=dt.getName(new Locale("es", "ES"));
							totalSS+=amount;
							context="DESMPL";
							if (type!=null) {
								salaryBuilder.addData("PORCENTAJE_DESMPL", new TimedObject<Double>(type, per ));
							}
						}
						else if (description.contains("TRIBUTACION I.R.P.F.")) {
							dt=DeductionType.IRPF;
							description=dt.getName(new Locale("es", "ES"));
							context="IRPF";
							if (type!=null) {
								salaryBuilder.addData("PORCENTAJE_IRPF", new TimedObject<Double>(type, per ));
							}
						}
						

						salaryBuilder.addDeduction(
								amount,
								description,
								dFrom,
								dTo,
								new Deduction().setType(dt).setName(context),
								Collections.emptyMap());
						
						
					} else {
						matcher = SETTLEMENT.matcher(concept_line);
						if(matcher.matches()) {
							salaryBuilder.setType(SalaryType.SETTLE);
							payrollType=1;
							
						}
						else {
							matcher = ATRASOS_CONV.matcher(concept_line);
							if(matcher.matches()) {
								salaryBuilder.setType(SalaryType.DELAY);
								payrollType=2;
							}
							else {
								
							}
						}
						
					}
				}
				
				
				
				
				
				
				
				concept_line = reader.readLine();
				matcher=TOTAL_HEADER.matcher(concept_line);
			}
			
			//matcher = find(reader, TOTAL_HEADER);
			String strSalary=reader.readLine();
			//System.err.println(strSalary);
			matcher=TOTAL.matcher(strSalary);
			ArrayList<String> remnbases=new ArrayList<String>();
			while(matcher.find()) {
				//System.err.println("\t"+matcher.group());
				remnbases.add(matcher.group());
			}
			//System.err.println("Bases: "+remnbases.size());
			try {
				salaryBuilder.setRemuneration(Double.parseDouble(AonStringUtils.trimToNull(remnbases.get(0)).replaceAll("\\.", "").replaceAll("[,]", ".")));
				//salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<Double>(irpfBase, period ));
			}catch (NullPointerException e) {
				salaryBuilder.setRemuneration(0d);
			}
			
			try {
				Double amount = Double.parseDouble(AonStringUtils.trimToNull(remnbases.get(1)).replaceAll("\\.", "").replaceAll("[,]", "."));
				salaryBuilder.setProExtBase(amount);
				salaryBuilder.addPayment(0d, amount, 0d, "PAGA_EXTRA", dFrom, dTo, new Payment().setType(PaymentType.CRA_0004), Collections.EMPTY_MAP);
			}catch (NullPointerException e) {
				salaryBuilder.setProExtBase(0d);
			}
			
			try {
				Double cgcBase=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(2)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setCgcBase(cgcBase);
				salaryBuilder.addData(ContextVariable.CGC_BASE.getName(), new TimedObject<Double>(cgcBase, per ));
			}catch (NullPointerException e) {
				salaryBuilder.setCgcBase(0d);
			}
			
			try {
				Double rawCgcBase=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(2)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setRawCgcBase(rawCgcBase);
				salaryBuilder.addData(ContextVariable.CGC_BASE_RAW.getName(), new TimedObject<Double>(rawCgcBase, per ));
			}catch (NullPointerException e) {
				salaryBuilder.setRawCgcBase(null);
			}
			
			try {
				Double cgpBase=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(3)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setCgpBase(cgpBase);
				salaryBuilder.addData(ContextVariable.CGP_BASE.getName(), new TimedObject<Double>(cgpBase, per ));
			}catch (NullPointerException e) {
				salaryBuilder.setCgpBase(0d);
			}
			
			try {
				Double totalIrpf=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(4)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalIrpf(totalIrpf);
				salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<Double>(totalIrpf, per ));
			}catch (NullPointerException e) {
				salaryBuilder.setTotalIrpf(0d);
			}
			
			try {
				Double totalPayment=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(5)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalPayment(totalPayment);
				salaryBuilder.addData(ContextVariable.TOTAL_PAYMENT.getName(), new TimedObject<Double>(totalPayment, per ));
			}catch (NullPointerException e) {
				salaryBuilder.setTotalPayment(0d);
			}
			
			try {
				Double totalDeduction=Double.parseDouble(removeSpace(AonStringUtils.trimToNull(remnbases.get(6)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalDeduction(totalDeduction);
			}catch (NullPointerException e) {
				salaryBuilder.setTotalDeduction(0d);
			}
			
			
			//ISSUE DATE
			matcher = find(reader, DATE);			
			String issuestr=AonStringUtils.trimToNull(matcher.group("day"))
					+" "+AonStringUtils.trimToNull(matcher.group("month"))
					+" "+AonStringUtils.trimToNull(matcher.group("year"));			
			Date issueDate = a3DateParser(issuestr);
			salaryBuilder.setIssueDate(issueDate);
			
			//PLACE
			matcher = find(reader, PLACE);
			
			
			//TOTAL LIQUID
			matcher = find(reader, TOTAL_LIQUID_HEADER);
			matcher = find(reader, TOTAL_LIQUID);
			Double totLiq=a3DoubleParser(AonStringUtils.trimToNull(matcher.group("liquid")));
			totLiq = totLiq==null?0d:totLiq;
			salaryBuilder.setTotalLiquid(totLiq);
			
			//IBAN
			matcher = find(reader, IBAN);
			//System.out.println(matcher.group("iban"));
			
			
			
			//APPORTATION
			Double totalEnterprise=0d;
			matcher = find(reader, APPORT_HEADER_BOTTOM);
			/*String apprt = reader.readLine();
			matcher = APPORT.matcher(apprt);*/
			
			
			//1. Contingencias comunes.................................................... .. .. ..    1.260,31          23,60           297,43          
			//AT y EP................................. .. .. ..    1.260,31           1,50            18,90           
			//2. Contingencias profe- Desempleo............................ .. .. ..    1.260,31           5,50            69,32          
			//sionales y conceptos de
			//recaudación conjunta Formación Profesional..........
			//.. ... .    1.260,31           0,60             7,56           
			//Fondo Garantía Salarial......... .. .. .    1.260,31           0,20             2,52           
			//3. Cotización adicional horas extraordinarias........................ .. .. .                                                       
			
			matcher = find(reader, APPORT_CC);
			Double apportBase = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			Double individualApport = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			String description=COMMON_CONTINGENCY.getName(new Locale("es", "ES"));
			{
				Double type=null;
				try {
					type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {}
				if (type!=null) {
					salaryBuilder.addData("PORCENTAJE_CGC_E", new TimedObject<Double>(type, per ));
				}
			}	
			if(apportBase!=null)
				salaryBuilder.addData(ContextVariable.CGC_ENTERPRISE.getName(), new TimedObject<Double>(apportBase, per ));
			Deduction costDeduction=new Deduction().setType(COMMON_CONTINGENCY).setName("CGC_E");
			if(individualApport!=null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise+=individualApport;
			}
			matcher = find(reader, APPORT_AT_EP);
			apportBase = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description=DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES"));
			{
				Double type=null;
				try {
					type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {}
				if (type!=null) {
					salaryBuilder.addData("PORCENTAJE_CGP_E", new TimedObject<Double>(type, per ));
				}
			}
			if(apportBase!=null) {
				salaryBuilder.addData(ContextVariable.IT_ENTERPRISE.getName(), new TimedObject<Double>(apportBase, per ));
				salaryBuilder.addData(ContextVariable.IMS_ENTERPRISE.getName(), new TimedObject<Double>(0d, per ));
				Deduction imsDeduction=new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E");
				salaryBuilder.addCost(0d, "IMS_E", dFrom, dTo, imsDeduction, Collections.emptyMap());
			}
			costDeduction=new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IT_E");
			if(individualApport!=null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise+=individualApport;
			}
			matcher = find(reader, APPORT_UNEMPLOYMENT);
			apportBase = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description=DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES"));
			{
				Double type=null;
				try {
					type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {}
				if (type!=null) {
					salaryBuilder.addData("PORCENTAJE_DESMPL_E", new TimedObject<Double>(type, per ));
				}
			}
			if(apportBase!=null)
				salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(), new TimedObject<Double>(apportBase, per ));
			costDeduction=new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESEMPL_E");
			if(individualApport!=null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise+=individualApport;
			}
			matcher = find(reader, APPORT_FP);
			apportBase = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description=DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
			{
				Double type=null;
				try {
					type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {}
				if (type!=null) {
					salaryBuilder.addData("PORCENTAJE_FP_E", new TimedObject<Double>(type, per ));
				}
			}
			if(apportBase!=null)
				salaryBuilder.addData(ContextVariable.FP_ENTERPRISE.getName(), new TimedObject<Double>(apportBase, per ));
			costDeduction=new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
			if(individualApport!=null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise+=individualApport;
			}
			matcher = find(reader, APPORT_FOGASA);
			apportBase = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description=DeductionType.FOGASA.getName(new Locale("es", "ES"));
			{
				Double type=null;
				try {
					type = a3DoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {}
				if (type!=null) {
					salaryBuilder.addData("PORCENTAJE_FOGASA", new TimedObject<Double>(type, per ));
				}
			}
			if(apportBase!=null)
				salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(), new TimedObject<Double>(apportBase, per ));
			costDeduction=new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
			if(individualApport!=null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise+=individualApport;
			}
			
			

			
			totalEnterprise=Math.round(totalEnterprise*100.0)/100.0;
			salaryBuilder.setTotalEnterprise(totalEnterprise);
			//totalSS+=totalEnterprise;
			totalSS=Math.round(totalSS*100.0)/100.0;
			salaryBuilder.setTotalSS(totalSS);
			salaryBuilder.getSalary();
		}
		
		return this;
	
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
	
	
	private static Date a3DateParser(final String date){
		Pattern dPatt=Pattern.compile("\\s*(?<day>\\d{1,2})\\s*(?<esmonth>\\w+)\\s*(?<year>\\d+)\\s*");
		try {
			Matcher m=dPatt.matcher(date);
			if(m.matches()) {
				int day=Integer.parseInt(m.group("day"));
				int year=Integer.parseInt(m.group("year"));
				if((year>50)&&(year<100)) {
					year+=1900;
				}
				else if (year<=50){
					year+=2000;
				}
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
	
	private static Double a3DoubleParser(String strNum) {
		try {
			strNum=strNum.replaceAll("\\.", "").replaceAll("[,]", ".");
			return Double.parseDouble(strNum);
		} catch (NumberFormatException | NullPointerException e) {
			return null;
		}
	}
	
	
	//    IVANOV , PETAR GEORGIEV
	private static final Pattern EMPLOYEE_NAME = 
	Pattern.compile("^(?<name>[^,]+,.*)$"
	, Pattern.CASE_INSENSITIVE);
	//    CL    ALFONSO VI             30       3  DC                 
	private static final Pattern EMPLOYEE_ADDRESS = 
	Pattern.compile("^\\s*(?<tipo>[^\\s]+)?\\s+(?<address>.+)?$"
	, Pattern.CASE_INSENSITIVE);
	//  09000  MIRANDA DE EBRO 
	private static final Pattern PC_AND_MUNICIPALITY =
	Pattern.compile("^\\s*(?<postcode>\\d{5})?\\s*(?<municipality>.+)?$"
	, Pattern.CASE_INSENSITIVE);
	//  BURGOS    
	private static final Pattern PROVINCE =
	Pattern.compile("^\\s*(?<province>.+)$"
	, Pattern.CASE_INSENSITIVE);
	//NIF. J01409838                                                      8052                        
	private static final Pattern NIF =
	Pattern.compile("^\\s*(?:NIF\\.)\\s*(?<nif>.+?)\\s*(?<enterprisecode>\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
	//EMPRESA DOMICILIO Nº INS. S.S.
	private static final Pattern HOME_HEADER =
	Pattern.compile("EMPRESA\\s*DOMICILIO\\s*Nº\\s*INS\\.\\s*S\\.S\\.", Pattern.CASE_INSENSITIVE);
	//RESTAURANTE EL VISO, S.C          CL REAL 32 BJ                     01/1034816-96               
	private static final Pattern ENTERPRISE_HOME =
	Pattern.compile("^\\s*(?<enterprisename>.+?)\\s{2,}(?<address>.+?)\\s{2,}(?<nss>\\d+/\\d+-\\d+)\\s*",
	Pattern.CASE_INSENSITIVE);
	//TRABAJADOR/A CATEGORIA NºMATRIC ANTIGUEDAD D.N.I.
	private static final Pattern WORKER_HEADER =
	Pattern.compile("TRABAJADOR/A\\s*CATEGORIA\\s*NºMATRIC\\s*ANTIGUEDAD\\s*D\\.N\\.I\\."
	, Pattern.CASE_INSENSITIVE);
	//IVANOV , PETAR GEORGIEV           FREGADOR                  1 OCT 08   X8865220P   
	//    SANCHEZ REY, LORENA               COMERCIAL                 1 MAR 20   31725099A   
	//    SANZ CASTILLA, GENOVEVA MARIA                               2 ENE 20   44963245Q   
	//"(?<name>.+?)\\s{2,}(?<job>.+?)?\\s{2,}(?<nummatric>.*?)?\\s*(?<old>\\d{1,2}\\s+\\w+\\s+\\d{1,})\\s{2,}(?<nif>(\\d|\\w)\\d{8}\\w)\\s*"
	private static final Pattern WORKER =
	Pattern.compile("\\s*(?<name>.+?)\\s{3,}(?<job>.+?)?\\s{2,}(?<old>\\d+\\s\\w{3}\\s\\d+)(?<nif>.+?)\\s*"
	, Pattern.CASE_INSENSITIVE);
	//Nº AFILIACION. S.S. TARIFA COD.CT SECCION NRO. PERIODO TOT. DIAS
	private static final Pattern SS_INFO_HEADER =
	Pattern.compile("\\s*Nº AFILIACION\\.\\s*S\\.S\\.\\s*TARIFA\\s*COD\\.CT\\s*seccion\\s*NRO\\.\\s*PERIODO\\s*TOT\\.\\s*DIAS\\s*",
	Pattern.CASE_INSENSITIVE);
	//48/10454983-40     7  200              4  MENS 01 ENE 20 a 31 ENE 20          30  
	//04/10543848-78     7  189  0102        2  MENS 01 ENE 20 a 31 ENE 20          30  
	//14/10284281-20                       191  MENS 01 ENE 20 a 31 ENE 20          30  
	private static final Pattern SS_INFO =
	//Pattern.compile("\\s*(?<affnum>\\d+/\\d+-\\d+)\\s*(?<tarifa>\\d*)\\s*(?<codct>\\d*)\\s*(?<section>.*?)?\\s*(?<nro>\\d*)\\s*(?<period>.*\\s*a\\s*.*?)?\\s{2,}(?<days>\\d*)\\s*"
			Pattern.compile("\\s*(?<affnum>\\d+/\\d+-\\d+)\\s{4,5}(?<tarifa>\\d{1,2})?\\s*(?<codct>\\d{1,3})?\\s*((?<section>[^\\s]+?)\\s+)?(?<nro>\\d+)?\\s{2}(?<period>.*\\s*a\\s*.*?)?\\s{2,}(?<days>\\d*)\\s*"
	, Pattern.CASE_INSENSITIVE);
	//CUANTIA PRECIO CONCEPTO DEVENGOS DEDUCCIONES
	private static final Pattern CONCEPT_HEADER=
	Pattern.compile("\\s*CUANTIA\\s*PRECIO\\s*CONCEPTO\\s*DEVENGOS\\s*DEDUCCIONES\\s*"
	, Pattern.CASE_INSENSITIVE);
	//30,00     26,741     1  *Salario Base                               802,24                   
	private static final Pattern CONCEPT =
	Pattern.compile("\\s*(?<cuantia>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*(?<price>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*(?<unknownnumber>\\d+)?\\s*(?<concept>\\*?.+?)\\s{1,2}(?<tipo>\\d+[,]\\d*)?\\s*(?<devengos>\\d+[,]\\d*)?\\s{19}?(?<deducciones>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//                                FINIQUITO ....................:                                     
	private static final Pattern SETTLEMENT =
	Pattern.compile("\\s*FINIQUITO\\s*\\.+:\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern ATRASOS_CONV =
			Pattern.compile("\\s*PAGA\\s*ATRASOS\\s*DE\\s*CONVENIO\\s*\\.+:\\s*"
			, Pattern.CASE_INSENSITIVE);
	//REM. TOTAL P.P.EXTRAS BASE S.S. BASE A.T. Y DES. BASE I.R.P.F. T. DEVENGADO T.  A DEDUCIR
	private static final Pattern TOTAL_HEADER =
	Pattern.compile("REM\\.\\s*TOTAL\\s*P\\.P\\.EXTRAS\\s*BASE\\s*S\\.S\\.\\s*BASE\\s*A\\.T\\.\\s*Y\\s*DES\\.\\s*BASE\\s*I\\.R\\.P\\.F\\.\\s*T\\.\\s*DEVENGADO\\s*T\\.\\s*A\\s*DEDUCIR"
	, Pattern.CASE_INSENSITIVE);
	//1.260,31                  1.260,31        1.260,31     1.260,31    1.260,31        161,61     
	//1.175,00       166,66     1.341,66        1.341,66     1.175,00    1.175,00        306,26      
    //966,24                    966,24          966,24       966,24      966,24         81,16     
	//1.108,33                  1.108,33        1.108,33     1.108,33    1.108,33         93,10     
	//1.584,97                  1.584,97        1.584,97     1.584,97    1.584,97        284,97     
	//4.500,00                                               4.500,00    4.500,00      1.125,00     
	private static final Pattern TOTAL =
			Pattern.compile("(((\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)|\\s{12,15})"
			, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern TOTAL_ROW =
	Pattern.compile("\\s*(((\\d+\\.)?\\d+[,]\\d+)|\\s{9,10})+\\s*"
	, Pattern.CASE_INSENSITIVE);
	//* Percepciones Salariales  sujetas a Cot. S.S. - Percepciones no Salariales excluídas Cot. S.S.
	private static final Pattern LEYENDA =
	Pattern.compile("\\s*\\*\\s*Percepciones\\s*Salariales\\s*sujetas\\s*a\\s*Cot\\.\\s*S\\.S\\.\\s*-\\s*Percepciones\\s*no\\s*Salariales\\s*excluídas\\s*Cot\\.\\s*S\\.S\\.\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//FECHA                                                        SELLO EMPRESA RECIBI
	private static final Pattern FECHASELLO =
	Pattern.compile("\\s*FECHA\\s*SELLO\\s*EMPRESA\\s*RECIBI\\S*"
	, Pattern.CASE_INSENSITIVE);
	//31 ENERO      2020                                                              
	private static final Pattern DATE =
	Pattern.compile("\\s*(?<day>\\d{1,2})\\s*(?<month>\\w+)\\s*(?<year>\\d+)\\s*",
	Pattern.CASE_INSENSITIVE);
	//ARMIÑON                                                                         
	private static final Pattern PLACE =
	Pattern.compile("\\s{2,}?(?<place>.+)\\s{2,}?"
	, Pattern.CASE_INSENSITIVE);
	//LIQUIDO A PERCIBIR
	private static final Pattern TOTAL_LIQUID_HEADER =
	Pattern.compile("\\s*LIQUIDO\\s*A\\s*PERCIBIR\\s*"
	, Pattern.CASE_INSENSITIVE);
	//                                         1.098,70        
	private static final Pattern TOTAL_LIQUID =
	Pattern.compile("\\s*(?<liquid>[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//IBAN:                                                                                                     
	private static final Pattern IBAN =
	Pattern.compile("\\s*IBAN:\\s*(?<iban>[^\\s]+.*[^\\s]*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//SWIFT/BIC:                                                                                                     COSTE EMPRESA:        1.656,05   
	private static final Pattern COSTE_EMPRESA =
	Pattern.compile("\\s*SWIFT/BIC:\\s*(?<swift>[^\\s]+.*[^\\s])?\\s*COSTE\\s*EMPRESA:\\s*(?<cost>[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//DETERMINACIÓN DE LAS B. DE COTIZACIÓN A LA S.S. Y CONCEPTOS DE RECAUDACIÓN CONJUNTA Y APORTACIÓN DE LA EMPRESA
	private static final Pattern APPORT_HEADER_TOP =
	Pattern.compile("\\s*DETERMINACIÓN\\s*DE\\s*LAS\\s*B\\.\\s*DE\\s*COTIZACIÓN\\s*A\\s*LA\\s*S\\.S\\.\\s*Y\\s*CONCEPTOS\\s*DE\\s*RECAUDACIÓN\\s*CONJUNTA\\s*Y\\s*APORTACIÓN\\s*DE\\s*LA\\s*EMPRESA\\s*"
	, Pattern.CASE_INSENSITIVE);
	//CONCEPTO BASE TIPO APORTACIÓN EMPRESARIAL
	private static final Pattern APPORT_HEADER_BOTTOM =
	Pattern.compile("\\s*CONCEPTO\\s*BASE\\s*TIPO\\s*APORTACIÓN\\s*EMPRESARIAL\\s*"
	, Pattern.CASE_INSENSITIVE);
	//1. Contingencias comunes.................................................... .. .. ..    1.260,31          23,60           297,43          
	//AT y EP................................. .. .. ..    1.260,31           1,50            18,90           
	//2. Contingencias profe- Desempleo............................ .. .. ..    1.260,31           5,50            69,32          
	//sionales y conceptos de
	//recaudación conjunta Formación Profesional..........
	//.. ... .    1.260,31           0,60             7,56           
	//Fondo Garantía Salarial......... .. .. .    1.260,31           0,20             2,52           
	//3. Cotización adicional horas extraordinarias........................ .. .. .                                                       
	private static final Pattern APPORT =
	Pattern.compile("\\s*(?:.+?)(?<concept>[\\w\\d\\s])[\\.\\s]{2,}\\s*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_CC =
	Pattern.compile("\\s*1\\.\\s*Contingencias\\s*comunes[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_AT_EP =
	Pattern.compile("\\s*AT\\s*y\\s*EP[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_UNEMPLOYMENT =
	Pattern.compile("\\s*2\\.\\s*Contingencias\\s*profe-\\s*Desempleo[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_FP =
	Pattern.compile("[\\.\\s]{2,}\\s*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_FOGASA =
	Pattern.compile("\\s*Fondo\\s*Garantía\\s*Salarial[\\s\\.]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern APPORT_EXTRA_H =
	Pattern.compile("\\s*3\\.\\s*Cotización\\s*adicional\\s*horas\\s*extraordinarias[\\s\\.]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	
	
	
	
	
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
	
	
	
	
	

	private static Matcher check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
//		for ( int g = 1; g <= matcher.groupCount(); g++) 
//			System.out.println(matcher.group(g));
		return matcher;		
	}
	private static Matcher find(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.find();
		return matcher;		
	}
	
//	public static void main(String[] args) {
//		Matcher matcher = check(EMPLOYEE_NAME, "    IVANOV , PETAR GEORGIEV                                     ");
//		System.out.println("name : "+matcher.group("name"));
//
//		matcher = check(EMPLOYEE_ADDRESS, "   CL    ALFONSO VI             30       3  DC                 ");
//		System.out.println("tipo : "+ matcher.group("tipo"));
//		System.out.println("address : " + matcher.group("address"));
//		
//		
//		
//		matcher = check(PC_AND_MUNICIPALITY, "    09000  MIRANDA DE EBRO ");
//		System.out.println("Post code: "+matcher.group("postcode"));
//		System.out.println("Municipality: "+ matcher.group("municipality"));
//		
//		matcher = check(PROVINCE, "    BURGOS                                                      ");
//		System.out.println("Province: "+matcher.group("province"));
//		
//		matcher = check(NIF, "NIF. J01409838                                                      8052                        ");
//		System.out.println("NIF: "+matcher.group("nif"));
//		System.out.println("Enterprise code: "+matcher.group("enterprisecode"));
//		
//		matcher = check(HOME_HEADER, "EMPRESA DOMICILIO Nº INS. S.S.");
//		System.out.println(matcher.group());
//		
//		matcher = check(ENTERPRISE_HOME, "RESTAURANTE EL VISO, S.C          CL REAL 32 BJ                     01/1034816-96               ");
//		System.out.println("Enterprise name: "+matcher.group("enterprisename"));
//		System.out.println("Enterprise address: "+matcher.group("address"));
//		System.out.println("NSS: "+matcher.group("nss"));
//		
//		matcher = check(WORKER_HEADER, "TRABAJADOR/A CATEGORIA NºMATRIC ANTIGUEDAD D.N.I.");
//		System.out.println(matcher.group());
//		
//		matcher = check(WORKER, "IVANOV , PETAR GEORGIEV           FREGADOR                  1 OCT 08   X8865220P   ");
//		System.out.println("surname:"+matcher.group("surname"));
//		System.out.println("name:"+matcher.group("name"));
//		System.out.println("job:"+matcher.group("job"));
//		System.out.println("old:"+matcher.group("old"));
//		System.out.println("NIF:"+matcher.group("nif"));
//		System.out.println("Num. matric: "+matcher.group("nummatric"));
//		
//		matcher = check(SS_INFO_HEADER, "Nº AFILIACION. S.S. TARIFA COD.CT SECCION NRO. PERIODO TOT. DIAS");
//		System.out.println(matcher.group());
//		
//		matcher = check(SS_INFO, "48/10454983-40     7  200              4  MENS 01 ENE 20 a 31 ENE 20          30  ");
//		System.out.println("Affnum: "+matcher.group("affnum"));
//		System.out.println("Fee: "+matcher.group("tarifa"));
//		System.out.println("codct: "+matcher.group("codct"));
//		System.out.println("Section: "+matcher.group("section"));
//		System.out.println("Number: "+matcher.group("nro"));
//		System.out.println("Period: "+matcher.group("period"));
//		System.out.println("Tot. days: "+matcher.group("days"));
//		
//		matcher = check(CONCEPT_HEADER, "CUANTIA PRECIO CONCEPTO DEVENGOS DEDUCCIONES");
//		System.out.println(matcher.group());
//		System.out.println();
//		matcher = check(CONCEPT, "30,00     26,741     1  *Salario Base                               802,24                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "4  *Antigüedad                                 128,36                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//
//		matcher = check(CONCEPT, "30,00      1,039    95  *Plus Manutención                            31,17                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "124  *P.p.extras                                 169,00                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "147  *Bonus octubre                               78,20                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "240  *Domingos-festiv                             51,34                   ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "789   Dcto.Conceptos en Especie                                 31,17     ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "995   COTIZACION CONT.COMU 4,70                                 59,23     ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "996   COTIZACION FORMACION 0,10                                  1,26     ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "997   COTIZACION DESEMPLEO 1,55                                 19,53     ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "999   TRIBUTACION I.R.P.F. 4,00                                 50,42     ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(CONCEPT, "Horas en Alta a Tiempo Parcial: 105,00                              ");
//		System.out.println("Cuantía: "+matcher.group("cuantia"));
//		System.out.println("Precio: "+matcher.group("price"));
//		System.out.println("No idea: "+matcher.group("unknownnumber"));
//		System.out.println("Concepto: "+matcher.group("concept"));
//		System.out.println("Tipo: "+matcher.group("tipo"));
//		System.out.println("Devengos: "+matcher.group("devengos"));
//		System.out.println("Deducciones: "+matcher.group("deducciones"));
//		
//		System.out.println();
//		
//		matcher = check(TOTAL_HEADER, "REM. TOTAL P.P.EXTRAS BASE S.S. BASE A.T. Y DES. BASE I.R.P.F. T. DEVENGADO T.  A DEDUCIR");
//		System.out.println(matcher.group());
//		
//		matcher = find(TOTAL, "1.260,31                  1.260,31        1.260,31     1.260,31    1.260,31        161,61     ");
//		System.out.println(matcher.group(0));
//		while ( matcher.find()) 
//			System.out.println(matcher.group(0));
//
//	
//		matcher = check(LEYENDA, "* Percepciones Salariales  sujetas a Cot. S.S. - Percepciones no Salariales excluídas Cot. S.S.");
//		System.out.println(matcher.group(0));
//		
//		
//		matcher = check(FECHASELLO, "FECHA                                                        SELLO EMPRESA RECIBI");
//		System.out.println(matcher.group());
//		
//		
//		matcher = check(DATE, "31 ENERO      2020                                                              ");
//		System.out.println("Day: "+matcher.group("day"));
//		System.out.println("Month: "+matcher.group("month"));
//		System.out.println("Year: "+matcher.group("year"));
//		
//		
//		
//	}


	//ARMIÑON                                                                         
	//LIQUIDO A PERCIBIR
	//                                         1.098,70        
	//IBAN:                                                                                                     
	//SWIFT/BIC:                                                                                                     COSTE EMPRESA:        1.656,05   
	//DETERMINACIÓN DE LAS B. DE COTIZACIÓN A LA S.S. Y CONCEPTOS DE RECAUDACIÓN CONJUNTA Y APORTACIÓN DE LA EMPRESA
	//CONCEPTO BASE TIPO APORTACIÓN EMPRESARIAL
	//1. Contingencias comunes.................................................... .. .. ..    1.260,31          23,60           297,43          
	//AT y EP................................. .. .. ..    1.260,31           1,50            18,90           
	//2. Contingencias profe- Desempleo............................ .. .. ..    1.260,31           5,50            69,32          
	//sionales y conceptos de
	//recaudación conjunta Formación Profesional..........
	//.. ... .    1.260,31           0,60             7,56           
	//Fondo Garantía Salarial......... .. .. .    1.260,31           0,20             2,52           
	//3. Cotización adicional horas extraordinarias........................ .. .. .                                                       

}
