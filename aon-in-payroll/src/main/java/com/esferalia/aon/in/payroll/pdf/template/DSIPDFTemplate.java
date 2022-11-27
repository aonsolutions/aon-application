package com.esferalia.aon.in.payroll.pdf.template;

import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.payroll.SalaryBuilder;
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

public class DSIPDFTemplate implements SalaryPDFTemplate {

	public static final DSIPDFTemplate DSI_PDF_TEMPLATE = new DSIPDFTemplate();

	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

			PDFContract pdfContract = parseSalaryHeader(reader, salaryBuilder);
			Period salaryPeriod = parseSalaryPeriod(reader, salaryBuilder);

			salaryBuilder.setContract(
					pdfContract
					.setEndDate(salaryPeriod.getEnd())
					.setStartDate(salaryPeriod.getStart()));
			salaryBuilder.addData(
			"GRUPO_COTIZACION", 
			new TimedObject<String>(pdfContract.getQuoteGroup(), salaryPeriod));

			parseSalaryPayments(reader, salaryBuilder, salaryPeriod);
			Double totalSocialSecurity = parseSalaryDeductions(reader, salaryBuilder, salaryPeriod);

			parseSalaryLiquid(reader, salaryBuilder);
			parseSalaryIssueDate(reader, salaryBuilder);
			
			// Enterprise costs 
			String line;
			Matcher matcher;

			matcher = find(reader, ENTERPRISE_APPORT_HEADER_2);

			matcher = find(reader, CC_MONTHLY);

			String remuneration = AonStringUtils.trimToEmpty(matcher.group("amount"));
			try {
				if (remuneration != null) {
					salaryBuilder.setRemuneration(
							Double.parseDouble(remuneration.replace(".", "").replace(",", ".")));
				} else {
					salaryBuilder.setRemuneration(0d);
				}
			} catch (NumberFormatException e) {
			}

			matcher = find(reader, CC_EXTRA);
			{
				String extraPro = AonStringUtils.trimToEmpty(matcher.group("amount"));
				try {
					if (extraPro != null) {
						salaryBuilder.setProExtBase(
								Double.parseDouble(extraPro.replace(".", "").replace(",", ".")));
					}
				} catch (NumberFormatException e) {
				}
			}
			matcher = find(reader, IT_BASE);
			{
				String strIt = AonStringUtils.trimToEmpty(matcher.group("amount"));
				try {
					if (strIt != null) {
						salaryBuilder.setItBase(Double.parseDouble(strIt.replace(".", "").replace(",", ".")));
					} else {
						salaryBuilder.setItBase(0d);
					}
				} catch (NumberFormatException e) {
				}
			}
			matcher = find(reader, CC);

			{
				String strCcCost = AonStringUtils.trimToEmpty(matcher.group("amount3"));
				String strCcRaw = AonStringUtils.trimToEmpty(matcher.group("amount1"));
				String strCcBase = AonStringUtils.trimToEmpty(matcher.group("amount2"));
				String strCcPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
				if (strCcCost != null) {
					strCcCost = strCcCost.replace(".", "").replace(",", ".");
					try {
						Double ccCost = Double.parseDouble(strCcCost);
						String description = COMMON_CONTINGENCY.getName(new Locale("es", "ES"));
						Deduction costDeduction = new Deduction().setType(COMMON_CONTINGENCY).setName("CGC_E");
						salaryBuilder.addCost(ccCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
						totalSocialSecurity += ccCost;
					} catch (NumberFormatException e) {
					}
				}
				if (strCcRaw != null) {
					strCcRaw = strCcRaw.replace(".", "").replace(",", ".");
					try {
						salaryBuilder.addData(ContextVariable.CGC_BASE_RAW.getName(),
								new TimedObject<>(Double.parseDouble(strCcRaw), salaryPeriod));
					} catch (NumberFormatException e) {
					}
				}
				if (strCcBase != null) {
					strCcBase = strCcBase.replace(".", "").replace(",", ".");
					try {
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
								new TimedObject<>(Double.parseDouble(strCcBase), salaryPeriod));
					} catch (NumberFormatException e) {
					}
				}
				if (strCcPercent != null) {
					try {
						salaryBuilder.addData("PORCENTAJE_CGC_E",
								new TimedObject<>(dsiDoubleParser(strCcPercent), salaryPeriod));
					} catch (NullPointerException e) {
					}
				}
			}
			
			matcher = find(reader, AT_EP);
			{

				String strAtEpCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
				String strCgpPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
				if (strAtEpCost != null) {
					strAtEpCost = strAtEpCost.replace(".", "").replace(",", ".");
					try {
						Double atEpCost = Double.parseDouble(strAtEpCost);
						String description = DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES"));
						Deduction costDeduction = new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY)
								.setName("IT_E");
						salaryBuilder.addCost(atEpCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction,
								Collections.emptyMap());
						totalSocialSecurity += atEpCost;
					} catch (NumberFormatException e) {
					}
				}
				if (strCgpPercent != null) {
					try {
						salaryBuilder.addData("PORCENTAJE_CGP_E",
								new TimedObject<>(dsiDoubleParser(strCgpPercent), salaryPeriod));
					} catch (NullPointerException e) {
					}
				}
			}
			
			
			matcher = find(reader, CP_HEADER_1);
			line = reader.readLine();
			matcher = UNEMPLOYMENT_AND_ATEP.matcher(line);
			if (matcher.matches()) {
				Double profContBase = str2double(matcher.group("normal"));
				salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(),
						new TimedObject<>(profContBase, salaryPeriod));
				salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(),
						new TimedObject<>(profContBase, salaryPeriod));
				salaryBuilder.addData(ContextVariable.FP_ENTERPRISE.getName(),
						new TimedObject<>(profContBase, salaryPeriod));
				salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
						new TimedObject<>(profContBase, salaryPeriod));
				
				Double fpCost = str2double(matcher.group("cost"));
				Double fpPercent = str2double(matcher.group("percent"));
				
				if (fpCost != null) {					
					String description = DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
					Deduction costDeduction = new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
					salaryBuilder.addCost(fpCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
					totalSocialSecurity += fpCost;
				}
				if (fpPercent != null) {
					salaryBuilder.addData("PORCENTAJE_FP_E",
							new TimedObject<>(fpPercent, salaryPeriod));
					
				}
				
			} else {
				matcher = find(reader, UNEMPLOYMENT_HEADER);
				matcher = find(reader, PROF_CONT_BASES);
				{

					String strProfContBase = AonStringUtils.trimToEmpty(matcher.group("normal"));

					if (strProfContBase != null) {
						try {
							strProfContBase = strProfContBase.replace(".", "").replace(",", ".");
							Double profContBase = Double.parseDouble(strProfContBase);
							salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(),
									new TimedObject<>(profContBase, salaryPeriod));
							salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(),
									new TimedObject<>(profContBase, salaryPeriod));
							salaryBuilder.addData(ContextVariable.FP_ENTERPRISE.getName(),
									new TimedObject<>(profContBase, salaryPeriod));
							salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
									new TimedObject<>(profContBase, salaryPeriod));
						} catch (NumberFormatException e) {
						}
					}
				}
				matcher = find(reader, UNEMPLOYMENT_COST);
				{
					String strUnemPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
					String strUnemCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
					if (strUnemCost != null) {
						strUnemCost = strUnemCost.replace(".", "").replace(",", ".");
						try {
							Double unemCost = Double.parseDouble(strUnemCost);
							String description = DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES"));
							Deduction costDeduction = new Deduction().setType(DeductionType.UNEMPLOYMENT)
									.setName("DESMPL_E");
							salaryBuilder.addCost(unemCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction,
									Collections.emptyMap());
							totalSocialSecurity += unemCost;
						} catch (NumberFormatException e) {
						}
						if (strUnemPercent != null) {
							try {
								salaryBuilder.addData("PORCENTAJE_DESMPL_E",
										new TimedObject<>(dsiDoubleParser(strUnemPercent), salaryPeriod));
							} catch (NullPointerException e) {
							}
						}
					}
				}
			}
			
			matcher = find(reader, FP_COST);
			String strFpPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
			String strFpCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
			if (strFpCost != null) {
				strFpCost = strFpCost.replace(".", "").replace(",", ".");
				try {
					Double fpCost = Double.parseDouble(strFpCost);
					String description = DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
					Deduction costDeduction = new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
					salaryBuilder.addCost(fpCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
					totalSocialSecurity += fpCost;
				} catch (NumberFormatException e) {
				}
			}
			if (strFpPercent != null) {
				try {
					salaryBuilder.addData("PORCENTAJE_FP_E",
							new TimedObject<>(dsiDoubleParser(strFpPercent), salaryPeriod));
				} catch (NullPointerException e) {
				}
			}
			matcher = find(reader, FOGASA_COST);
			String strFogasaPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
			String strFogasaCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
			if (strFogasaCost != null) {
				strFogasaCost = strFogasaCost.replace(".", "").replace(",", ".");
				try {
					Double fogasaCost = Double.parseDouble(strFogasaCost);
					String description = DeductionType.FOGASA.getName(new Locale("es", "ES"));
					Deduction costDeduction = new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
					salaryBuilder.addCost(fogasaCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
					totalSocialSecurity += fogasaCost;
				} catch (NumberFormatException e) {
				}
				if (strFogasaPercent != null) {
					try {
						salaryBuilder.addData("PORCENTAJE_FOGASA",
								new TimedObject<>(dsiDoubleParser(strFogasaPercent), salaryPeriod));
					} catch (NullPointerException e) {
					}
				}

				matcher = find(reader, EXTRA_H);
				{
					String strHExtra = AonStringUtils.trimToEmpty(matcher.group("base"));
					if (strHExtra != null) {
						try {
							Double hExtra = Double.parseDouble(strHExtra.replace(".", "").replace(",", "."));
							salaryBuilder.addData(ContextVariable.EXTRA_HOURS.getName(),
									new TimedObject<>(hExtra, salaryPeriod));
							salaryBuilder.setHExtraBase(hExtra);
						} catch (NumberFormatException e) {
						}
					}
				}
				matcher = find(reader, IRPF);
				{
					String strIrpf = AonStringUtils.trimToEmpty(matcher.group("base"));
					if (strIrpf != null) {
						try {
							Double irpf = Double.parseDouble(strIrpf.replace(".", "").replace(",", "."));
							salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(),
									new TimedObject<>(irpf, salaryPeriod));
							salaryBuilder.setIrpfBase(irpf);
						} catch (NumberFormatException e) {
						}
					}
				}
				totalSocialSecurity = Math.round(totalSocialSecurity * 100.0) / 100.0;
				salaryBuilder.setTotalSS(totalSocialSecurity);
				salaryBuilder.getSalary();
			}
			return this;
		}
	}

	protected static void parseSalaryIssueDate(BufferedReader reader, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		Matcher matcher;
		matcher = find(reader, ISSUE_DATE);
		try {
			Integer day = Integer.parseInt(AonStringUtils.trimToEmpty(matcher.group("day")));
			Integer month = Integer.parseInt(monthChooser(AonStringUtils.trimToEmpty(matcher.group("month"))));
			Integer year = Integer.parseInt(AonStringUtils.trimToEmpty(matcher.group("year")));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.YEAR, year);

			calendar.set(Calendar.HOUR_OF_DAY, 12);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.ZONE_OFFSET, 2);

			salaryBuilder.setIssueDate(calendar.getTime());
		} catch (NullPointerException | DateFormatException | NumberFormatException e) {
		}
	}

	protected static void parseSalaryLiquid(BufferedReader reader, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		Matcher matcher;
		matcher = find(reader, TOTAL_LIQUID);
		try {
			Double totalLiquid = Double.parseDouble(
					AonStringUtils.trimToEmpty(matcher.group("amount").replace(".", "").replace(",", ".")));
			salaryBuilder.setTotalLiquid(totalLiquid);
		} catch (NullPointerException | NumberFormatException e) {
		}
	}

	protected static Double parseSalaryDeductions(BufferedReader reader, ISalaryBuilder<?> salaryBuilder, Period salaryPeriod)
			throws IOException, UnknownPDFException {
		String line;
		Matcher matcher;
		Double totalSS = 0d;
		matcher = find(reader, DEDUCTIONS_HEADER);
		line = reader.readLine();
		matcher = TOTAL_APPORT.matcher(line);
		while (!matcher.matches()) {

			matcher = DEDUCTION.matcher(line);
			while (matcher.find()) {
				
				
				/*
				 * 
				 * CORREGIR PROBLEMA CON IRPF
				 * 
				 * 
				 */

				Double amount = null;

				String concept = AonStringUtils.trimToEmpty(matcher.group("concept")).toUpperCase();
				String strBase = AonStringUtils.trimToEmpty(matcher.group("base"));
				ContextVariable con = ContextVariable.NULL;

				String context = null;// concept;
				DeductionType dt = DeductionType.OTHER;

				String percentConcept = null;
				Double percent = null;

				try {

					amount = Double
							.parseDouble(matcher.group("deduction").replace(".", "").replace(",", "."));
					Double base;
					try {
						base = Double.parseDouble(strBase.replace(".", "").replace(",", "."));
					} catch (NullPointerException e) {
						base = null;
					}

					if (concept.contains("FÍSICAS")) {
						dt = DeductionType.IRPF;
						concept = dt.getName(new Locale("es", "ES"));
						context = "IRPF";
						con = ContextVariable.IRPF_BASE;
						try {
							percent = dsiDoubleParser(AonStringUtils.trimToEmpty(matcher.group("percent")));
							percentConcept = "PORCENTAJE_IRPF";
						} catch (NullPointerException e) {
						}
					} else if (concept.contains("DESEMPLEO")) {
						dt = DeductionType.UNEMPLOYMENT;
						concept = dt.getName(new Locale("es", "ES"));
						context = "DESMPL";
						con = ContextVariable.UNEMPLOY_EMPLOYEE;
						if(base!=null)
							salaryBuilder.setCgpBase(base);
						try {
							percent = dsiDoubleParser(AonStringUtils.trimToEmpty(matcher.group("percent")));
							percentConcept = "PORCENTAJE_DESMPL";
						} catch (NullPointerException e) {
						}
					} else if (concept.contains("CONTINGENCIAS COMUNES")) {
						dt = DeductionType.COMMON_CONTINGENCY;
						concept = dt.getName(new Locale("es", "ES"));
						context = "CGC";
						con = ContextVariable.CGC_EMPLOYEE;
						if(base!=null) {
							salaryBuilder.setCgcBase(base);
							salaryBuilder.setRawCgcBase(base);
						} else {
							
						}
						try {
							percent = dsiDoubleParser(AonStringUtils.trimToEmpty(matcher.group("percent")));
							percentConcept = "PORCENTAJE_CGC";
						} catch (NullPointerException e) {
						}
					} else if (concept.contains("HORAS EXTRAORDINARIAS")) {
						// dt = DeductionType.NON_STRUCTURAL_OVERTIME;
						// description=dt.getName(new Locale("es", "ES"));
						con = ContextVariable.EXTRA_HOURS;
					} else if (concept.contains("FORMACIÓN PROFESIONAL")) {
						dt = DeductionType.JOB_TRAINING;
						concept = dt.getName(new Locale("es", "ES"));
						context = "FP";
						con = ContextVariable.FP_EMPLOYEE;
						try {
							percent = dsiDoubleParser(AonStringUtils.trimToEmpty(matcher.group("percent")));
							percentConcept = "PORCENTAJE_FP";
						} catch (NullPointerException e) {
						}
					}

					if (amount != null) {
						salaryBuilder.addDeduction(amount, concept, salaryPeriod.getStart(), salaryPeriod.getEnd(),
								new Deduction().setType(dt).setName(context), Collections.emptyMap());
					}
					if (strBase != null) {
						if (con != ContextVariable.NULL)
							salaryBuilder.addData(con.getName(), new TimedObject<Double>(base, salaryPeriod));
						else {
							salaryBuilder.addData(concept, new TimedObject<Double>(base, salaryPeriod));
						}
					}
					if (percent != null) {
						salaryBuilder.addData(percentConcept, new TimedObject<Double>(percent, salaryPeriod));
					}
				} catch (NumberFormatException | NullPointerException e) {
				}

			}

			line = reader.readLine();
			matcher = TOTAL_APPORT.matcher(line);
		}
		{
			String stramount = AonStringUtils.trimToEmpty(matcher.group("deduction"));
			if (stramount != null) {
				stramount = stramount.replace(".", "").replace(",", ".");
				try {
					totalSS += (Double.parseDouble(stramount));
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
			}
		}

		matcher = find(reader, TOTAL_DEDUCTION);
		{
			String stramount = AonStringUtils.trimToEmpty(matcher.group("amount"));
			if (stramount != null) {
				stramount = stramount.replace(".", "").replace(",", ".");
				try {
					salaryBuilder.setTotalDeduction(Double.parseDouble(stramount));
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
			}
		}
		return totalSS;
	}

	protected static Double parseSalaryPayments(BufferedReader reader, ISalaryBuilder<?> salaryBuilder, Period salaryPeriod)
			throws IOException, UnknownPDFException {
		Matcher matcher;
		matcher = find(reader, PAYMENTS_HEADER);
		String line = reader.readLine();
		matcher = TOTAL_PAYMENT.matcher(line);
		while (!matcher.matches()) {
			matcher = NUMBER.matcher(line);
			while (matcher.find()) {
//					System.out.println(matcher.group());
				String strAmount = AonStringUtils
						.trimToEmpty(matcher.group("amount").replace(".", "").replace(",", "."));
				String concept = matcher.group("concept");
				matcher = DOUBLE_CONCEPT.matcher(concept);
				if (matcher.matches()) {
					concept = AonStringUtils.trimToEmpty(matcher.group("concept"));
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

					salaryBuilder.addPayment(amount, amount, amount, concept, salaryPeriod.getStart(), salaryPeriod.getEnd(),
							new Payment().setType(pt).setName(description), Collections.emptyMap());
				} catch (NumberFormatException e) {}

			}
			line = reader.readLine();
			try {
				matcher = TOTAL_PAYMENT.matcher(line);
			} catch (Exception e) {
				throw new UnknownPDFException();
			}
			

		}

		if (matcher.group("totalpayment") != null) {
			String strTotalPayment = AonStringUtils.trimToEmpty(matcher.group("totalpayment")).replace(".", "")
					.replace(",", ".");
			try {
				double totalPayment = Double.parseDouble(strTotalPayment );
				salaryBuilder.setTotalPayment(totalPayment);
				return totalPayment;
			} catch (NumberFormatException e) {}
		}
		
		return null;
	}

	protected static Period parseSalaryPeriod(BufferedReader reader, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		Matcher matcher = find(reader, LIQPERIOD_TOTDAYS);
		String liqString = AonStringUtils.trimToEmpty(matcher.group("liqper"));
		Date dFrom = null;
		Date dTo = null;
		Integer timeUnits = null;
		{
			String strDays = AonStringUtils.trimToEmpty(matcher.group("totdays"));
			if (strDays != null) {
				try {
					timeUnits = Integer.parseInt(strDays);
				} catch (NumberFormatException e) {
				}
				salaryBuilder.setTimeUnits(timeUnits);
			}
		}
		if (liqString != null && liqString.contains("del") && liqString.contains("al")) {
			matcher = LIQPERIOD.matcher(liqString);
			if (matcher.matches()) {

				String strFrom = matcher.group("dayfrom") + " "
						+ AonStringUtils.trimToEmpty(matcher.group("monthfrom")) + " "
						+ AonStringUtils.trimToEmpty(matcher.group("year"));
				String strTo = matcher.group("dayto") + " " + AonStringUtils.trimToEmpty(matcher.group("monthto"))
						+ " " + matcher.group("year");
				try {
					dFrom = dsiDateParser(strFrom);
					dTo = dsiDateParser(strTo);
					salaryBuilder.setStartDate(dFrom);
					salaryBuilder.setEndDate(dTo);
					salaryBuilder.setChargeDate(dTo);
				} catch (NullPointerException e) {
				}

			}
		}
		Period per = new Period(dFrom, dTo);
		if (timeUnits != null)
			salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(timeUnits, per));
		return per;
	}
	
	protected static PDFContract parseSalaryHeader( BufferedReader reader, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		
		Matcher matcher = find(reader, ENTERPRISE_WORKER);
		String employeeName = AonStringUtils.trimToEmpty(matcher.group("name"));
		salaryBuilder.setEmployeeName(employeeName);
		String enterpriseName = AonStringUtils.trimToEmpty(matcher.group("enterprise"));
		salaryBuilder.setEnterpriseName(enterpriseName);
		matcher = find(reader, HOME_NIF);
		salaryBuilder.setEnterpriseAddress(AonStringUtils.trimToEmpty(matcher.group("home")));
		String nif = AonStringUtils.trimToEmpty(matcher.group("nif"));
		salaryBuilder.setEmployeeDocument(nif);
		matcher = find(reader, CIF_NSS);
		String cif = AonStringUtils.trimToEmpty(matcher.group("cif"));
		salaryBuilder.setEnterpriseDocument(cif);
		String naf = AonStringUtils.trimToEmpty(matcher.group("nss").replace("/", ""));
		salaryBuilder.setSocialSecurityNumber(naf);
		matcher = find(reader, CATEGORY);
		salaryBuilder.setCategory(AonStringUtils.trimToEmpty(matcher.group("category")));
		matcher = find(reader, NSS_GROUP_OLD);
		String ccc = AonStringUtils.trimToEmpty(matcher.group("nss"));
		if (ccc != null) {
			ccc = ccc.replace("/", "");
		}
		
		Date seniorityDate = null; 
		String seniority = AonStringUtils.trimToEmpty(matcher.group("seniority"));
		if (seniority != null) {
			try {
				String[] dmy = seniority.split("/");
				if (dmy.length != 3)
					throw new Exception();
				Integer day = Integer.parseInt(dmy[0]);
				Integer month = Integer.parseInt(dmy[1]);
				Integer year = Integer.parseInt(dmy[2]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.YEAR, year);

				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.ZONE_OFFSET, 2);
				
				seniorityDate = calendar.getTime();
				salaryBuilder.setSeniorityDate(seniorityDate);
			} catch (Exception e) {
			}
		}
		salaryBuilder.setCcc(ccc);
		String quoteGroup = AonStringUtils.trimToEmpty(matcher.group("quotegroup"));
		salaryBuilder.setQuoteGroup(quoteGroup);
		// salaryBuilder.setSeniorityDate(seniorityDate);
		
		return new PDFContract()
				.setCcc(ccc)
				.setNaf(naf)
				.setNif(nif)
				.setCif(cif)
				// Later outside of this method
				//.setEndDate(dTo)
				//.setStartDate(dFrom)
				.setEmployeeName(employeeName)
				.setEnterpriseName(enterpriseName)
				.setSeniorityDate(seniorityDate)
				;
	}
	
	private static Double dsiDoubleParser(String strNum) {
		strNum = strNum.replace(".", "").replace(",", ".");
		try {
			return Double.parseDouble(strNum);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String monthChooser(String mes) {
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
	
	private static Double str2double(String str) {
		if (AonStringUtils.isEmpty(str))
			return null;
		String numstr = str.replace(".", "").replace(",", ".");
		try {
			return Double.parseDouble(numstr);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Date dsiDateParser(final String date) {
		Pattern dPatt = Pattern.compile("\\s*(?<day>\\d{1,2})\\s*(?<esmonth>\\w+)\\s*(?<year>\\d+)\\s*");
		try {
			Matcher m = dPatt.matcher(date);
			if (m.matches()) {
				int day = Integer.parseInt(m.group("day"));
				int year = Integer.parseInt(m.group("year"));
				int month;
				String strMonth = m.group("esmonth");
				if ((strMonth.equalsIgnoreCase("ENE")) || (strMonth.equalsIgnoreCase("ENERO"))) {
					month = 1;
				} else if ((strMonth.equalsIgnoreCase("FEB")) || (strMonth.equalsIgnoreCase("FEBRERO"))) {
					month = 2;
				} else if ((strMonth.equalsIgnoreCase("MAR")) || (strMonth.equalsIgnoreCase("MARZO"))) {
					month = 3;
				} else if ((strMonth.equalsIgnoreCase("ABR")) || (strMonth.equalsIgnoreCase("ABRIL"))) {
					month = 4;
				} else if ((strMonth.equalsIgnoreCase("MAY")) || (strMonth.equalsIgnoreCase("MAYO"))) {
					month = 5;
				} else if ((strMonth.equalsIgnoreCase("JUN")) || (strMonth.equalsIgnoreCase("JUNIO"))) {
					month = 6;
				} else if ((strMonth.equalsIgnoreCase("JUL")) || (strMonth.equalsIgnoreCase("JULIO"))) {
					month = 7;
				} else if ((strMonth.equalsIgnoreCase("AGO")) || (strMonth.equalsIgnoreCase("AGOSTO"))) {
					month = 8;
				} else if ((strMonth.equalsIgnoreCase("SEP")) || (strMonth.equalsIgnoreCase("SEPTIEMBRE"))) {
					month = 9;
				} else if ((strMonth.equalsIgnoreCase("OCT")) || (strMonth.equalsIgnoreCase("OCTUBRE"))) {
					month = 10;
				} else if ((strMonth.equalsIgnoreCase("NOV")) || (strMonth.equalsIgnoreCase("NOVIEMBRE"))) {
					month = 11;
				} else if ((strMonth.equalsIgnoreCase("DIC")) || (strMonth.equalsIgnoreCase("DICIEMBRE"))) {
					month = 12;
				} else {
					return null;
				}

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.YEAR, year);

				calendar.set(Calendar.HOUR_OF_DAY, 12);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.ZONE_OFFSET, 2);

				return calendar.getTime();
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	// Empresa: EMPRESA S.L. Trabajador: ANDREA ANDREA, MARIA
	protected static Pattern ENTERPRISE_WORKER = Pattern.compile(
			"^\\s*Empresa:\\s*(?<enterprise>.+?)\\s*Trabajador:\\s*(?<name>.+)$", Pattern.CASE_INSENSITIVE);
	// Domicilio: CL VIA, 12 N.I.F.: 37723953C Número Libro de Matrícula:
	// Domicilio:    C MADRE VEDRUNA, 9  2 IZ    N.I.F.:    0X8422836Y    Número Libro de Matrícula:
	protected static Pattern HOME_NIF = Pattern.compile(
			"\\s*Domicilio:\\s*(?<home>.+?)\\s*N\\.I\\.F\\.:\\s*(?<nif>[^\\s]+)\\s*Número\\s*Libro\\s*de\\s*Matrícula:(?<matricula>.*)?",
			Pattern.CASE_INSENSITIVE);
	// C.I.F.: B50671908 Nº de Afiliación a la Seguridad Social: 08/02983860/69
	protected static Pattern CIF_NSS = Pattern
			.compile("\\s*C\\.I\\.F\\.:\\s*(?<cif>[\\w\\d]+)\\s*Nº\\s*de\\s*Afiliación\\s*"
					+ "a\\s*la\\s*Seguridad\\s*Social:\\s*(?<nss>[\\d/]+)\\s*", Pattern.CASE_INSENSITIVE);
//	Código de Cuenta de Cotización a la Categoría o Grupo Profesional:
	protected static Pattern CATEGORY = Pattern
			.compile("\\s*Código\\s*de\\s*Cuenta\\s*de\\s*Cotización\\s*a\\s*la\\s*Categoría\\s*o\\s*"
					+ "Grupo\\s*Profesional:(?<category>.*)", Pattern.CASE_INSENSITIVE);
//	Seguridad Social: 50/8745111/93 Grupo de Cotización: 02 Fecha antigüedad: 14/09/1972
	protected static Pattern NSS_GROUP_OLD = Pattern.compile(
			"\\s*Seguridad\\s*Social:\\s*(?<nss>[\\d/]+)\\s*Grupo\\s*de\\s*Cotización:\\s*(?<quotegroup>\\d+)?\\s*Fecha\\s*antigüedad:\\s*(?<seniority>[\\d/]+)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	Periodo de Liquidación:    del 1 de febrero al 29 de febrero de 2020 Total días 30
	protected static Pattern LIQPERIOD_TOTDAYS = Pattern.compile(
			"\\s*Periodo\\s*de\\s*Liquidación:(?<liqper>.+?)Total\\s*días\\s*(?<totdays>\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

	protected static Pattern LIQPERIOD = Pattern.compile(
			"\\s*del\\s*(?<dayfrom>\\d+)\\s*de\\s*(?<monthfrom>\\w+)\\s*al\\s*(?<dayto>\\d+)\\s*de\\s*(?<monthto>\\w+)\\s*de\\s*(?<year>\\d+)\\s*",
			Pattern.CASE_INSENSITIVE);
//	I. DEVENGOS TOTALES
	protected static Pattern DEVENGOS_HEADER = Pattern.compile("\\s*I\\.\\s*DEVENGOS\\s*TOTALES\\s*",
			Pattern.CASE_INSENSITIVE);
//	1. Percepciones salariales 2. Percepciones no salariales
	protected static Pattern PAYMENTS_HEADER = Pattern.compile(
			"\\s*1\\.\\s*Percepciones\\s*salariales\\s*2\\.\\s*Percepciones\\s*no\\s*salariales\\s*",
			Pattern.CASE_INSENSITIVE);

	/*
	 * public static final Pattern PAYMENTS = //
	 * "\\s*(?<salaryconcept>\\w[\\w\\s\\.,]*?)?[\\.]{2,}?(?:(.+?))?[\\.]{2,}?\\s*(?<salaryamount>\\d[\\.\\d,]+)?\\s*(?<nonsalaryconcept>[^\\d\\s\\.][\\w\\s\\.,]*?)?\\s*(?<nonsalaryamount>\\d[\\.\\d,]+)?\\s*"
	 * Pattern.compile("\\s*(?<salaryconcept>^(\\.{2,})).*" ,
	 * Pattern.CASE_INSENSITIVE);
	 */
//	A. TOTAL SALARIO DEVENGADO........................ 758,84
//	A. TOTAL DEVENGADO.........    963,83
	public static final Pattern TOTAL_PAYMENT = Pattern.compile(
			"\\s*A\\.\\s*TOTAL(\\s*SALARIO)?\\s*DEVENGADO\\.*\\s*(?<totalpayment>\\d[\\.\\d,]*)?\\s*",
			Pattern.CASE_INSENSITIVE);
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
	public static Pattern NUMBER = Pattern
			.compile("(?<concept>.+?)(?:\\.{2,}([^\\.]+\\.*)?)\\s+(?<amount>\\d+[\\.\\d]+,\\d{2})");
	public static Pattern DOUBLE_CONCEPT = Pattern.compile(".+?\\.{2,}\\s(?<concept>.+)", Pattern.CASE_INSENSITIVE);
//	II. DEDUCCIONES
	public static Pattern DEDUCTIONS_HEADER = Pattern.compile("\\s*II\\.\\s*DEDUCCIONES\\s*", Pattern.CASE_INSENSITIVE);
//	1. Aportación del trabajador a las cotizaciones a la 2. Impuesto sobre la renta 
//	   Seguridad Social y conceptos de recaudación conjunta de la personas físicas..................... 16,00% 121,41
//	Contingencias Comunes 1.215,90 4,70% 57,15 3. Anticipos......................................................  
//	Desempleo 1.050,00 1,55% 16,28 4. Valor de los productos
//	Formación Profesional 1.050,00 0,10% 1,05 recibidos en especie.................    
//	Horas Extraordinarias 5. Otras deducciones
//	Fuerza Mayor  2,00%  DESCUENTO 1........................................ 25,00
//	Resto Horas Extras  4,70%  ................................................................  
	public static Pattern DEDUCTION = Pattern.compile(
			"(?<concept>[^%]+?)(?:\\.{2,})?\\s*(?<base>\\d[\\.\\d]*,\\d*)?\\s*((?<percent>\\d[\\.\\d]*,\\d*)%)?\\s*(?<deduction>\\d[\\.\\d,]*,\\d*)(?:\\s|$)",
			Pattern.CASE_INSENSITIVE);
//	TOTAL APORTACIONES................................................. 74,48	
	public static Pattern TOTAL_APPORT = Pattern.compile(
			"\\s*TOTAL\\s*APORTACIONES\\.{2,}\\s*(?<deduction>\\d+[\\.\\d]*,\\d*)?\\s*", Pattern.CASE_INSENSITIVE);

//	B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)...................... 220,89
//	B. TOTAL A DEDUCIR.........    133,07
	public static Pattern TOTAL_DEDUCTION = Pattern.compile(
			"\\s*B\\.\\s*TOTAL(?:(\\s*A\\s*DEDUCIR)|(\\s*DEDUCCIONES\\s*\\(S\\.SOCIAL-IRPF-\\.+\\)))\\s*\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	Firma y Sello de la Empresa TOTAL SALARIO LIQUIDO........................ 537,95
//	LIQUIDO TOTAL A PERCIBIR (A-B)...    830,76Firma y Sello de la Empresa
	public static Pattern TOTAL_LIQUID = Pattern.compile(
			"(?:\\s*Firma\\s*y\\s*Sello\\s*de\\s*la\\s*Empresa)?\\s*(?:(TOTAL\\s*SALARIO\\s*LIQUIDO)|(LIQUIDO\\s*TOTAL\\s*A\\s*PERCIBIR\\s*\\([^\\)]*\\)\\s*))\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*(?:Firma\\s*y\\s*Sello\\s*de\\s*la\\s*Empresa\\s*)?",
			Pattern.CASE_INSENSITIVE);

//	ZARAGOZA, 29 de febrero de 2020
	public static Pattern ISSUE_DATE = Pattern.compile(
			"\\s*(?<place>[^,]+)?,?\\s*(?<date>(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+?)\\s*de\\s*(?<year>\\d{4}))\\s*",
			Pattern.CASE_INSENSITIVE);
//	RECIBI,
//	INFORMACION ADICIONAL:
	public static Pattern ADITIONAL_INFO = Pattern.compile("\\s*INFORMACION\\s*ADICIONAL:\\s*",
			Pattern.CASE_INSENSITIVE);
//	Texto de informacion adicional

//	DETERMINACION DE LAS BASES DE COTIZACION A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACION CONJUNTA Y DE LA BASE 
	public static Pattern ENTERPRISE_APPORT_HEADER_1 = Pattern.compile(
			"\\s*DETERMINACION\\s*DE\\s*LAS\\s*BASES\\s*DE\\s*COTIZACION\\s*A\\s*LA\\s*SEGURIDAD\\s*SOCIAL\\s*Y\\s*CONCEPTOS\\s*DE\\s*RECAUDACION\\s*CONJUNTA\\s*Y\\s*DE\\s*LA\\s*BASE\\s*",
			Pattern.CASE_INSENSITIVE);
//	SUJETA A RETENCION DEL I.R.P.F. Y APORTACIÓN DE LA EMPRESA:
//	DE LA BASE SUJETA A RETENCION DEL I.R.P.F. Y APORTACIÓN DE LA EMPRESA:
	public static Pattern ENTERPRISE_APPORT_HEADER_2 = Pattern.compile(
			"\\s*(DE\\s*LA\\s*BASE\\s*)?SUJETA\\s*A\\s*RETENCION\\s*DEL\\s*I\\.R\\.P\\.F\\.\\s*Y\\s*APORTACIÓN\\s*DE\\s*LA\\s*EMPRESA:\\s*",
			Pattern.CASE_INSENSITIVE);
//	1. Contingencias comunes BASE BASE TIPO APORTACIÓN
	public static Pattern CC_HEADER = Pattern.compile(
			"\\s*1\\.\\s*Contingencias\\s*comunes\\s*BASE\\s*BASE\\s*TIPO\\s*APORTACIÓN\\s*", Pattern.CASE_INSENSITIVE);
//	Importe remuneración mensual............................................................... 758,84 NORMALIZADA EMPRESA
	public static Pattern CC_MONTHLY = Pattern.compile(
			"\\s*(NORMALIZADA\\s*EMPRESA\\s*)?Importe\\s*remuneración\\s*mensual\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*(NORMALIZADA\\s*EMPRESA\\s*)?",
			Pattern.CASE_INSENSITIVE);
//	Prorrata pagas extraordinarias................................................................ 126,47
	public static Pattern CC_EXTRA = Pattern.compile(
			"\\s*Prorrata\\s*pagas\\s*extraordinarias\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	Base incapacidad temporal.....................................................................  
//	Base incapacidad temporal..................................................................... 455,00
	public static Pattern IT_BASE = Pattern.compile(
			"\\s*Base\\s*incapacidad\\s*temporal\\.{2,}\\s*(?<amount>\\d[\\.\\d,]*,\\d*)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	TOTAL.............. 885,31 1.215,90 23,60% 286,95
//	TOTAL..............  746,00 23,60% 176,06
//	TOTAL..............    1.192,50    1.192,50    23,60%    281,43
	public static Pattern CC = Pattern.compile(
			"\\s*TOTAL\\.{2,}\\s*((((?<amount1>\\d[\\.\\d,]*,\\d+)?\\s*(?<amount2>\\d[\\.\\d,]*,\\d+))?\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%\\s*(?<amount3>\\d[\\.\\d,]*,\\d+)?)|%)\\s*$",
			Pattern.CASE_INSENSITIVE);
//	AT y EP..................... 1,50% 15,75
	public static Pattern AT_EP = Pattern.compile(
			"\\s*AT\\s*y\\s*EP\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$",
			Pattern.CASE_INSENSITIVE);
//	2. Contingencias profesionales
	public static Pattern CP_HEADER_1 = Pattern.compile("\\s*2\\.\\s*Contingencias\\s*profesionales\\s*$",
			Pattern.CASE_INSENSITIVE);
	
//	Desempleo...................    5,50%    65,59(AT.y EP.) y conceptos de    1.192,50    1.192,50
	public static Pattern UNEMPLOYMENT_AND_ATEP = Pattern.compile("\\s*Desempleo\\.{2,}\\s*(?<percent>\\d[\\d\\.]*,\\d+)?%?\\s*(?<cost>\\d[\\d\\.]*,\\d+)?\\s*\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*((?<raw>\\d[\\.\\d]*,\\d+)?\\s*(?<normal>\\d[\\.\\d]*,\\d+))?\\s*",
			Pattern.CASE_INSENSITIVE);
	
	
//	(AT.y EP.) y conceptos de
	public static Pattern CP_HEADER_2 = Pattern.compile("\\s*\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*$",
			Pattern.CASE_INSENSITIVE);
//	Desempleo............................................
	public static Pattern UNEMPLOYMENT_HEADER = Pattern.compile("\\s*Desempleo\\.{2,}\\s*", Pattern.CASE_INSENSITIVE);
//	885,31 1.050,00
	public static Pattern PROF_CONT_BASES = Pattern
			.compile("\\s*((?<raw>\\d[\\.\\d]*,\\d+)?\\s*(?<normal>\\d[\\.\\d]*,\\d+))?\\s*", Pattern.CASE_INSENSITIVE);
//	5,50% 57,75
	public static Pattern UNEMPLOYMENT_COST = Pattern.compile(
			"\\s*(?<percent>\\d[\\d\\.]*,\\d+)?%?\\s*(?<cost>\\d[\\d\\.]*,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);
//	recaudación conjunta 
	public static Pattern CP_HEADER_3 = Pattern.compile("\\s*recaudación\\s*conjunta\\s*", Pattern.CASE_INSENSITIVE);
//	Formación Profesional............................ 0,60% 6,30
//	Formación Profesional.......    0,60%    7,16recaudación conjunta 
	public static Pattern FP_COST = Pattern.compile(
			"\\s*Formación\\s*Profesional\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*(recaudaci.n\\s*conjunta\\s*)?$",
			Pattern.CASE_INSENSITIVE);
//	Fondo Garantía Salarial........................... 0,20% 2,10
	public static Pattern FOGASA_COST = Pattern.compile(
			"\\s*Fondo\\s*Garantía\\s*Salarial\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$",
			Pattern.CASE_INSENSITIVE);
//	3. Cotización adicional por horas extraordinarias...................................................................   
	public static Pattern EXTRA_H = Pattern.compile(
			"\\s*3\\.\\s*Cotización\\s*adicional\\s*por\\s*horas\\s*extraordinarias\\.{2,}\\s*(?<base>\\d[\\.\\d,]*,\\d+)?.*$",
			Pattern.CASE_INSENSITIVE);
//	4. Base sujeta a retención del I.R.P.F.............................................................................. 758,84
	public static Pattern IRPF = Pattern.compile(
			"\\s*4\\.\\s*Base\\s*sujeta\\s*a\\s*retención\\s*del\\s*I\\.R\\.P\\.F\\.{2,}\\s*(?<base>\\d[\\.\\d,]*,\\d+)?.*$",
			Pattern.CASE_INSENSITIVE);

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

	protected static Matcher find(BufferedReader reader, Pattern pattern) throws IOException, UnknownPDFException {

		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				//System.out.println(line);
				continue;
			}

			return matcher;
		}

		throw new UnknownPDFException(String.format("Pattern: '%s' Not found", pattern.pattern()));

	}
	
	
	public static void main(String[] args) throws UnknownPDFException {
		Pattern homeNif = Pattern.compile(
				"\\s*Domicilio:\\s*(?<home>.+?)\\s*N\\.I\\.F\\.:\\s*(?<nif>0?[\\d\\w]\\d{7}\\w)\\s*Número\\s*Libro\\s*de\\s*Matrícula:(?<matricula>.*)?",
				Pattern.CASE_INSENSITIVE);
		if ( !homeNif.matcher("Domicilio:    C MADRE VEDRUNA, 9  2 IZ    N.I.F.:    0X8422836Y    Número Libro de Matrícula:").matches() )
			throw new UnknownPDFException();
	}
}
