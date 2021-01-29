package com.esferalia.aon.in.payroll.pdf.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
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

public class AplifisaPDFTemplate implements SalaryPDFTemplate {

	private Date dateFrom;
	private Date dateTo;
	private Period period;
	private static int cont = 1;

	public static final AplifisaPDFTemplate APLIFISA_PDF_TEMPLATE = new AplifisaPDFTemplate();

	private Date issueDateFinder(String text) {
		Matcher matcher = ISSUE_DATE.matcher(text);
		if (matcher.find()) {
			return aplifisaFullDateParser(AonStringUtils.trimToNull(matcher.group("issuedate")));
		}
		return null;
	}

	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);

		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			Matcher matcher = find(reader, ENTERPRISE_EMPLOYEE);

			matcher = find(reader, ENT_EMP_NAMES);
			String entName = AonStringUtils.trimToNull(matcher.group("enterprise"));
			salaryBuilder.setEnterpriseName(entName);
			String empName = AonStringUtils.trimToNull(matcher.group("employee"));
			salaryBuilder.setEmployeeName(empName);

			matcher = find(reader, ENTHOME_NIF);
			salaryBuilder.setEnterpriseAddress(AonStringUtils.trimToNull(matcher.group("enthome")));
			String nif = AonStringUtils.trimToNull(matcher.group("nif"));
			salaryBuilder.setEmployeeDocument(nif);

			matcher = find(reader, ENTCITY_NSS);
			salaryBuilder.setEnterpriseCity(AonStringUtils.trimToNull(matcher.group("entcity")));
			String naf = AonStringUtils.trimToNull(matcher.group("nss").replaceAll("-", ""));
			salaryBuilder.setSocialSecurityNumber(naf);

			matcher = find(reader, CIF_CATEGORY);
			String cif = AonStringUtils.trimToNull(matcher.group("cif"));
			salaryBuilder.setEnterpriseDocument(cif);
			salaryBuilder.setCategory(AonStringUtils.trimToNull(matcher.group("category")));

			matcher = find(reader, CCC_QUOTEGROUP_SENIORITY);
			String ccc = AonStringUtils.trimToNull(matcher.group("ccc").replaceAll("-", ""));
			salaryBuilder.setCcc(ccc);
			String quoteGroup = matcher.group("quotegroup");
			salaryBuilder.setQuoteGroup(AonStringUtils.trimToNull(quoteGroup));

			Date seniority = commonDateParser(AonStringUtils.trimToNull(matcher.group("seniority")));
			salaryBuilder.setSeniorityDate(seniority);
			matcher = find(reader, LIQPERIOD_TIMEUNITS);
//			System.out.println(matcher.group());
//			System.out.println("\t" + matcher.group("liqperiod"));
//			System.out.println("\t\t" + matcher.group("from"));
//			System.out.println("\t\t" + matcher.group("to"));
//			System.out.println("\t\t" + matcher.group("salarytype"));
//			System.out.println("\t\t" + matcher.group("month"));
//			System.out.println("\t\t" + matcher.group("year"));
//			System.out.println("\t" + matcher.group("timeunits"));
			if (matcher.group("liqperiod") != null) {

				try {

					if (matcher.group("salarytype") == null) {
						Integer year = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("year")));
						dateFrom = aplifisaDateParser(matcher.group("from"), year);
						dateTo = aplifisaDateParser(matcher.group("to"), year);
					} else {
						String strSalaryType = AonStringUtils.trimToNull(matcher.group("salarytype"));

						if (AonStringUtils.containsIgnoreCase(strSalaryType, "PAGA EXTRAORDINARIA")) {
							Integer year = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("year")));
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, 12);
							Integer month = monthChooser(AonStringUtils.trimToNull(matcher.group("month")));
							salaryBuilder.setType(SalaryType.EXTRA);
							switch (month) {
							case 5:
								calendar.set(year, 0, 1);
								dateFrom = calendar.getTime();
								calendar.set(year, 5, 31);
								dateTo = calendar.getTime();
								break;
							case 11:
								calendar.set(year, 5, 1);
								dateFrom = calendar.getTime();
								calendar.set(year, 11, 31);
								dateTo = calendar.getTime();
								break;
							}
						} else if (AonStringUtils.containsIgnoreCase(strSalaryType, "LIQUIDACIÓN")) {
							salaryBuilder.setType(SalaryType.SETTLE);
							dateFrom = seniority;
							dateTo = issueDateFinder(text);
						}
					}
					period = new Period(dateFrom, dateTo);

					if (quoteGroup != null) {
						salaryBuilder.addData("GRUPO_COTIZACION", new TimedObject<String>(quoteGroup, period));
					}
				} catch (NullPointerException | NumberFormatException e) {
				}
				salaryBuilder.setStartDate(dateFrom);
				salaryBuilder.setEndDate(dateTo);
				salaryBuilder.setChargeDate(dateTo);

				salaryBuilder.setContract(new PDFContract().setCcc(ccc).setNaf(naf).setNif(nif).setCif(nif)
						.setEndDate(dateTo).setStartDate(dateFrom).setEmployeeName(empName).setEnterpriseName(entName));

				try {
					Integer timeUnits = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("timeunits")));
					salaryBuilder.setTimeUnits(timeUnits);
					if (timeUnits != null) {
						salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(timeUnits, period));
					}
				} catch (NullPointerException | IllegalArgumentException e) {
				}
			}

			matcher = find(reader, PAYMENTS_HEADER);

			matcher = find(reader, SALARY_PAYMENT_HEADER);
			String line = reader.readLine();
			matcher = NON_SALARY_PAYMENT_HEADER.matcher(line);

			while (!matcher.matches()) {
				matcher = PAYMENT.matcher(line);
				if (matcher.matches()) {
					Double payment = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					String context = (concept.length() > 25) ? concept.substring(0, 24) : concept;
					addPayment(salaryBuilder, payment, concept, context);
				}

				line = reader.readLine();
				matcher = NON_SALARY_PAYMENT_HEADER.matcher(line);
			}

			matcher = TOTAL_PAYMENT.matcher(line);

			while (!matcher.matches()) {
				matcher = PAYMENT.matcher(line);
				if (matcher.matches()) {
					Double payment = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					String context = (concept.length() > 25) ? concept.substring(0, 24) : concept;
					addPayment(salaryBuilder, payment, concept, context);
				}

				line = reader.readLine();
				matcher = TOTAL_PAYMENT.matcher(line);
			}

			try {
				Double totalPayment = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
				if (totalPayment != null)
					salaryBuilder.setTotalPayment(totalPayment);
				else
					salaryBuilder.setTotalPayment(0d);
				salaryBuilder.addData(ContextVariable.TOTAL_PAYMENT.getName(),
						new TimedObject<Double>(totalPayment, period));
			} catch (NullPointerException e) {
				salaryBuilder.setTotalPayment(0d);
			}

			matcher = find(reader, DEDUCTIONS_HEADER);

			matcher = find(reader, SS_APPORTS_HEADER);

			// DEDUCTIONS

			matcher = find(reader, COMMON_CONTINGENCY);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.COMMON_CONTINGENCY;
				String context = "CGC";
				String description = dt.getName(new Locale("es", "ES"));

				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_CGC", new TimedObject<Double>(percent, period));
					}
					if (base != null) {
						salaryBuilder.addData("BASE_CGC", new TimedObject<Double>(base, period));
						salaryBuilder.setCgcBase(base);
					} else {
						salaryBuilder.setCgcBase(0d);
					}
				} else {
					salaryBuilder.setCgcBase(0d);
				}
			}

			matcher = find(reader, UNEMPLOYMENT);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (base != null)
					salaryBuilder.setCgpBase(base);
				else
					salaryBuilder.setCgpBase(0d);
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.UNEMPLOYMENT;
				String context = "DESMPL";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					salaryBuilder.addData(ContextVariable.IMS_ENTERPRISE.getName(), new TimedObject<Double>(0d, period ));
					Deduction imsDeduction=new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E");
					salaryBuilder.addCost(0d, "IMS_E", dateFrom, dateTo, imsDeduction, Collections.emptyMap());
					if (base != null) {
						salaryBuilder.addData("BASE_DESMPL", new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_DESMPL", new TimedObject<Double>(percent, period));
					}
				}
			}

			matcher = find(reader, FORMACION_PROFESIONAL);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.JOB_TRAINING;
				String context = "FP";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					if (base != null) {
						salaryBuilder.addData("BASE_FP", new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_FP", new TimedObject<Double>(percent, period));
					}
				}
			}

			matcher = find(reader, EXTRA_HOURS);

			matcher = find(reader, CHUCK_NORRIS_HOURS);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.STRUCTURAL_OVERTIME;
				String context = "FUERZA_MAYOR";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					if (base != null) {
						salaryBuilder.addData(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName(),
								new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_FUERZA_MAYOR", new TimedObject<Double>(percent, period));
					}
				}
			}

			matcher = find(reader, NON_STRUCTURAL_HOURS);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.NON_STRUCTURAL_OVERTIME;
				String context = "NO_ESTRUCTURALES";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					if (base != null) {
						salaryBuilder.addData(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName(),
								new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_NO_ESTRUCTURALES", new TimedObject<Double>(percent, period));
					}
				}

			}

			matcher = find(reader, TOTAL_APPORT);
			Double total_apport = 0d;
			if (matcher.group("apport") != null) {
				total_apport = aplifisaDoubleParser(matcher.group("apport"));
			}
			salaryBuilder.setTotalSS(total_apport);
			matcher = find(reader, IRPF);

			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (base != null)
					salaryBuilder.setTotalIrpf(base);
				else
					salaryBuilder.setTotalIrpf(0d);
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.IRPF;
				String context = "IRPF";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					if (base != null) {

						salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(),
								new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_IRPF", new TimedObject<Double>(percent, period));
					}
				}

			}

			matcher = find(reader, ADVANCED_PAYMENTS);

			{
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				if (deduction != null) {
					DeductionType dt = DeductionType.ADVANCE_PAYMENT;
					String context = "ADELANTO";
					String description = dt.getName(new Locale("es", "ES"));
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
				}
			}

			matcher = find(reader, IN_KIND);

			{
				Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				if (deduction != null) {
					DeductionType dt = DeductionType.IN_KIND;
					String context = "ESPECIE";
					String description = dt.getName(new Locale("es", "ES"));
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
				}
			}

			matcher = find(reader, OTHER);

			line = reader.readLine();

			matcher = TOTAL_DEDUCTION.matcher(line);

			while (!matcher.matches()) {

				matcher = OTHER_DEDUCTION.matcher(line);
				if (matcher.matches()) {
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					Double deduction = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));

					if (deduction != null) {

						if (AonStringUtils.containsIgnoreCase(concept, "EMBARGO")) {
							DeductionType dt = DeductionType.EMBARGO;
							String context = "EMBARGO";
							String description = dt.getName(new Locale("es", "ES"));
							salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
									new Deduction().setType(dt).setName(context), Collections.emptyMap());
						} else {
							DeductionType dt = DeductionType.OTHER;
							String context = "OTRO";
							String description = dt.getName(new Locale("es", "ES"));
							salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
									new Deduction().setType(dt).setName(context), Collections.emptyMap());
						}
					}

				}

				line = reader.readLine();
				matcher = TOTAL_DEDUCTION.matcher(line);
			}
			if (matcher.group("deduction") != null) {
				Double deduction = aplifisaDoubleParser(matcher.group("deduction"));
				if (deduction != null)
					salaryBuilder.setTotalDeduction(deduction);
				else
					salaryBuilder.setTotalPayment(0d);
			}

			matcher = find(reader, TOTAL_LIQUID);

			salaryBuilder.setTotalLiquid(aplifisaDoubleParser(matcher.group("totalliquid")));

			matcher = find(reader, ISSUE_DATE);

			salaryBuilder.setIssueDate(aplifisaFullDateParser(AonStringUtils.trimToNull(matcher.group("issuedate"))));

			matcher = find(reader, COSTS_HEADER);

			matcher = find(reader, REMUNERATION);

			{
				Double remuneration = aplifisaDoubleParser(AonStringUtils.trimToEmpty(matcher.group("remuneration")));
				if (remuneration != null)
					salaryBuilder.setRemuneration(remuneration);
				else
					salaryBuilder.setRemuneration(0d);

				line = reader.readLine();
				matcher = PRORATION_BASE.matcher(line);
				while (!matcher.matches()) {
					matcher = REMUNERATION_2.matcher(line);
					if (matcher.matches()) {
						remuneration = aplifisaDoubleParser(AonStringUtils.trimToEmpty(matcher.group("remuneration")));
						if (remuneration != null)
							salaryBuilder.setRemuneration(remuneration);
						else
							salaryBuilder.setRemuneration(0d);
					}
					line = reader.readLine();
					matcher = PRORATION_BASE.matcher(line);
				}

			}

			{
				Double proExtBase = aplifisaDoubleParser(AonStringUtils.trimToEmpty(matcher.group("proextbase")));
				if (proExtBase != null)
					salaryBuilder.setProExtBase(proExtBase);
				else
					salaryBuilder.setProExtBase(0d);
			}

			matcher = find(reader, CGC_E);
			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

				if (cost != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.COMMON_CONTINGENCY)
							.setName("CGC_E");
					salaryBuilder.addCost(cost, DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")),
							dateFrom, dateTo, costDeduction, Collections.EMPTY_MAP);
				}
				if (base != null) {
					salaryBuilder.addData(ContextVariable.CGC_BASE_ENTERPRISE.getName(),
							new TimedObject<Double>(base, period));
				}
				if (percent != null) {
					salaryBuilder.addData("PORCENTAJE_CGC_E", new TimedObject<Double>(percent, period));
				}
			}

			matcher = find(reader, AT_EP);
			{
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

				if (cost != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY)
							.setName("IT_E");
					salaryBuilder.addCost(cost, DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")),
							dateFrom, dateTo, costDeduction, Collections.EMPTY_MAP);
				}
				if (percent != null) {
					salaryBuilder.addData("PORCENTAJE_IT_E", new TimedObject<Double>(percent, period));
				}
			}

			matcher = find(reader, UNEMPLOYMENT_E);
			{
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

				if (cost != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E");
					salaryBuilder.addCost(cost, DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom,
							dateTo, costDeduction, Collections.EMPTY_MAP);
				}
				if (base != null) {
					salaryBuilder.addData(ContextVariable.CGP_BASE_ENTERPRISE.getName(),
							new TimedObject<Double>(base, period));
				}
				if (percent != null) {
					salaryBuilder.addData("PORCENTAJE_DESMPL_E", new TimedObject<Double>(percent, period));
				}
			}

			matcher = find(reader, FP_FOGASA_E);
			{
				Double percentfp = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfp")));
				Double costfp = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("costfp")));

				Double percentfogasa = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfogasa")));
				Double costfogasa = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("costfogasa")));

				// fp

				if (costfp != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
					salaryBuilder.addCost(costfp, DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom,
							dateTo, costDeduction, Collections.EMPTY_MAP);
				}
				if (percentfp != null) {
					salaryBuilder.addData("PORCENTAJE_FP_E", new TimedObject<Double>(percentfp, period));
				}

				// fogasa

				if (costfogasa != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
					salaryBuilder.addCost(costfogasa, DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom,
							dateTo, costDeduction, Collections.EMPTY_MAP);
				}
				if (percentfogasa != null) {
					salaryBuilder.addData("PORCENTAJE_FOGASA_E", new TimedObject<Double>(percentfogasa, period));
				}

				line = reader.readLine();
				matcher = EXTRA_HOURS_E.matcher(line);
				while (!matcher.matches()) {
					matcher = FP_FOGASA_E_2.matcher(line);
					if (matcher.matches()) {
						percentfogasa = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfogasa")));
						costfogasa = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("costfogasa")));

						if (costfogasa != null) {
							Deduction costDeduction = new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
							salaryBuilder.addCost(costfogasa, DeductionType.FOGASA.getName(new Locale("es", "ES")),
									dateFrom, dateTo, costDeduction, Collections.EMPTY_MAP);
							if (percentfogasa != null) {
								salaryBuilder.addData("PORCENTAJE_FOGASA_E",
										new TimedObject<Double>(percentfogasa, period));
							}
						}
					}

					line = reader.readLine();
					matcher = EXTRA_HOURS_E.matcher(line);
				}

			}

			{
				Double cost = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));
				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (cost != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.STRUCTURAL_OVERTIME)
							.setName("EXTRA_HOURS_E");
					salaryBuilder.addCost(cost, DeductionType.STRUCTURAL_OVERTIME.getName(new Locale("es", "ES")),
							dateFrom, dateTo, costDeduction, Collections.EMPTY_MAP);
					if (base != null) {
						salaryBuilder.addData(ContextVariable.EXTRA_HOURS.getName(),
								new TimedObject<Double>(base, period));
					}
				}
			}

			matcher = find(reader, IRPF_E);
			// NO IDEA WHY THERE ARE TWO DIFFERENT IRPF BASES
//			{
//				Double base = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
//				if (base != null) {
//					salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(),
//							new TimedObject<Double>(base, period));
//				}
//			}

			matcher = find(reader, TOTAL_ENTERPRISE);
			{
				Double total = aplifisaDoubleParser(AonStringUtils.trimToNull(matcher.group("total")));
				if (total != null) {
					Double total_enterprise = aplifisaDoubleParser(matcher.group("total"));
					salaryBuilder.setTotalEnterprise(total_enterprise);
//					Double total_ss = total_apport + total_enterprise;
//					total_ss = ((double) Math.round(total_ss * 100)) / 100;
//					salaryBuilder.setTotalSS(total_ss);
				}
			}
			salaryBuilder.getSalary();
		}
		System.err.println(cont);
		cont++;
		return this;
	}

//	EMPRESA    TRABAJADOR
	private final static Pattern ENTERPRISE_EMPLOYEE = Pattern.compile("\\s*EMPRESA\\s*TRABAJADOR\\s*",
			Pattern.CASE_INSENSITIVE);

//	TABIKET SOCIEDAD LIMITADA    RAHOUI , HICHAM
	private final static Pattern ENT_EMP_NAMES = Pattern.compile("(?<enterprise>.+?)\\s{2,}(?<employee>[^,]+\\s*,.+)",
			Pattern.CASE_INSENSITIVE);

//	Domicilio :    PZ JESÚS DE MEDINACELLI, 6   22    N.I.F.:    X7379673P
//	Domicilio :    CL POETA MAS Y ROS, 104       N.I.F.:    044515153X
//	Domicilio :    CL TOMAS BRETON, 9       N.I.F.:    0X9265678J
	private final static Pattern ENTHOME_NIF = Pattern.compile(
			"\\s*Domicilio\\s*:\\s*(?<enthome>.+?)\\s*N\\.I\\.F\\.:\\s*(?<nif>\\d?(\\w|\\d)\\d{7,8}\\w)\\s*",
			Pattern.CASE_INSENSITIVE);

//	C.P.: 46024 VALENCIA    Número de afiliación a la Seguridad Social:    44-10043688-89
	private final static Pattern ENTCITY_NSS = Pattern.compile(
			"(?:\\s*C\\.P\\.:\\s*\\d+)?\\s*(?<entcity>.+?)\\s*Número\\s*de\\s*afiliación\\s*a\\s*la\\s*Seguridad\\s*Social:\\s*(?<nss>\\d{2}-\\d{8}-\\d{2})",
			Pattern.CASE_INSENSITIVE);

//	C.I.F. :    B40589533    Cat. Profesional :    OFICIAL 1ª
//	C.I.F. :    B98812092    Cat. Profesional :
	private final static Pattern CIF_CATEGORY = Pattern.compile(
			"\\s*C\\.I\\.F\\.\\s*:\\s*(?<cif>[^\\s]+)\\s*Cat\\.\\s*Profesional\\s*:\\s*(?<category>.+?)?\\s*$",
			Pattern.CASE_INSENSITIVE);

//	Cta. cotización S.S. :    46-1515177-41    Grupo de cotización:    8    Fecha Antigüedad :    14/09/2020
	private static final Pattern CCC_QUOTEGROUP_SENIORITY = Pattern.compile(
			"\\s*Cta\\.\\s*cotización\\s*S\\.S\\.\\s*:\\s*(?<ccc>\\d{2}-\\d{7}-(\\d{2})?)\\s*Grupo\\s*de\\s*cotización:\\s*(?<quotegroup>\\d+)?\\s*Fecha\\s*Antigüedad\\s*:\\s*(?<seniority>\\d{1,2}/\\d{1,2}/\\d{2,4})\\s*",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern DATE_FORMAT = Pattern.compile("\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*",
			Pattern.CASE_INSENSITIVE);

//	Periodo de liquidación :      del    01    de    Diciembre    al    02    de    Diciembre    de    2020    Total días / horas    2
//	Periodo de liquidación :      PAGA EXTRAORDINARIA DE Diciembre 2020    Total días / horas    184
//	Periodo de liquidación :      LIQUIDACIÓN DE VACACIONES Y PAGAS EXTRAORDINARIAS

	// (?<liqperiod>del\s*(?<from>\d+\s*de\s*\w+)\s*al\s*(?<to>\d+\s*de\s*\w+)\s*de|(?<salarytype>.+?)(\s*de\s*(?<month>\w+)\)?)\s*(?<year>\d+)?\\s*(Total\s*días\s*/\s*horas\s*(?<timeunits>\d+)?)?
	// \s*Periodo\s*de\s*liquidación\s*:\s*(?<liqperiod>del\s*(?<from>\d+\s*de\s*\w+)\s*al\s*(?<to>\d+\s*de\s*\w+)\s*de|(?<salarytype>.+?))(\s*de\s*(?<month>\w+)\)?\s*(?<year>\d+)?\s*(?<year>\d+)?\\s*(Total\s*días\s*/\s*horas\s*(?<timeunits>\d+)?)?
	private static final Pattern LIQPERIOD_TIMEUNITS = Pattern.compile(
			"\\s*Periodo\\s*de\\s*liquidación\\s*:\\s*(?<liqperiod>del\\s*(?<from>\\d+\\s*de\\s*\\w+)\\s*al\\s*(?<to>\\d+\\s*de\\s*\\w+)\\s*de|(?<salarytype>.+?))(\\s*de\\s*(?<month>\\w{4,10}))?\\s*(?<year>\\d+)?\\s*(Total\\s*días\\s*/\\s*horas\\s*(?<timeunits>\\d+)?)?",
			Pattern.CASE_INSENSITIVE);

//	I. DEVENGOS    TOTALES
	private static final Pattern PAYMENTS_HEADER = Pattern.compile("\\s*I\\.\\s*DEVENGOS\\s*TOTALES\\s*",
			Pattern.CASE_INSENSITIVE);

//	I. Percepciones salariales    Devengo
	private static final Pattern SALARY_PAYMENT_HEADER = Pattern
			.compile("\\s*I\\.\\s*Percepciones\\s*salariales\\s*Devengo\\s*", Pattern.CASE_INSENSITIVE);

//	SALARIO BASE    57,98
//	PLUS DE TRANSPORTE    6,56
//	COMPL. ACTIVIDAD    34,66
	private static final Pattern PAYMENT = Pattern.compile("(?<concept>.+?)\\s*(?<payment>\\d+(\\.\\d+)?,\\d+)\\s*",
			Pattern.CASE_INSENSITIVE);

//	2. Percepciones no salariales
	private static final Pattern NON_SALARY_PAYMENT_HEADER = Pattern
			.compile("\\s*2\\.\\s*Percepciones\\s*no\\s*salariales\\s*", Pattern.CASE_INSENSITIVE);

//	A. TOTAL DEVENGADO ...........................................................................    99,20
//	A. TOTAL DEVENGADO ...........................................................................
	private static final Pattern TOTAL_PAYMENT = Pattern.compile(
			"\\s*A\\.\\s*TOTAL\\s*DEVENGADO\\s*\\.{2,}\\s*(?<payment>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	II. DEDUCCIONES
	private static final Pattern DEDUCTIONS_HEADER = Pattern.compile("\\s*II\\.\\s*DEDUCCIONES\\s*",
			Pattern.CASE_INSENSITIVE);

//	I. Aportación del trabajador a las cotizaciones a la Seguridad Social y conceptos de recaudación conjunta
	private static final Pattern SS_APPORTS_HEADER = Pattern.compile(
			"\\s*I\\.\\s*Aportación\\s*del\\s*trabajador\\s*a\\s*las\\s*cotizaciones\\s*a\\s*la\\s*Seguridad\\s*Social\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*",
			Pattern.CASE_INSENSITIVE);

//	Contingencias Comunes    115,50    4,7000    %    5,43
//	Contingencias Comunes    1.050,00    %    8,49
//	Contingencias Comunes    4,7000    %
	private static final Pattern COMMON_CONTINGENCY = Pattern.compile(
			"\\s*Contingencias\\s*Comunes\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Desempleo    115,50    1,60    %    1,85
	private static final Pattern UNEMPLOYMENT = Pattern.compile(
			"\\s*Desempleo\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Formación Profesional    115,50    0,10    %    0,12
	private static final Pattern FORMACION_PROFESIONAL = Pattern.compile(
			"\\s*Formación\\s*Profesional\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Horas Extraordinarias
	private static final Pattern EXTRA_HOURS = Pattern.compile("\\s*Horas\\s*Extraordinarias\\s*",
			Pattern.CASE_INSENSITIVE);

//	Fuerza mayor o estructurales    2,00    %
	private static final Pattern CHUCK_NORRIS_HOURS = Pattern.compile(
			"\\s*Fuerza\\s*mayor\\s*o\\s*estructurales\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	No estructurales    4,70    %
	private static final Pattern NON_STRUCTURAL_HOURS = Pattern.compile(
			"\\s*No\\s*estructurales\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	TOTAL APORTACIONES ....................    7,40
	private static final Pattern TOTAL_APPORT = Pattern.compile(
			"\\s*TOTAL\\s*APORTACIONES\\s*\\.{2,}\\s*(?<apport>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);

//	2. Impuesto sobre la renta de las personas físicas    99,20    2,00    %    1,98
//	2. Impuesto sobre la renta de las personas físicas    300,21    %
	private static final Pattern IRPF = Pattern.compile(
			"\\s*2\\.\\s*Impuesto\\s*sobre\\s*la\\s*renta\\s*de\\s*las\\s*personas\\s*físicas\\s*(((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	3. Anticipos
//	3. Anticipos    -21,11
	private static final Pattern ADVANCED_PAYMENTS = Pattern
			.compile("\\s*3\\.\\s*Anticipos\\s*-?(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);
//	4. Valor de los productos recibidos en especie
	private static final Pattern IN_KIND = Pattern.compile(
			"\\s*4\\.\\s*Valor\\s*de\\s*los\\s*productos\\s*recibidos\\s*en\\s*especie\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	5. Otras deducciones
	private static final Pattern OTHER = Pattern.compile("\\s*5\\.\\s*Otras\\s*deducciones\\s*",
			Pattern.CASE_INSENSITIVE);

//	EMBARGO DE SALARIOS    102,00
	private static final Pattern OTHER_DEDUCTION = Pattern
			.compile("\\s*(?<concept>.+?)\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);

//	B. TOTAL A DEDUCIR ..............................................................................    9,38
	private static final Pattern TOTAL_DEDUCTION = Pattern.compile(
			"\\s*B\\.\\s*TOTAL\\s*A\\s*DEDUCIR\\s*\\.{2,}\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?",
			Pattern.CASE_INSENSITIVE);

//	LIQUIDO TOTAL A PERCIBIR (A - B) ...........................................................................    89,82
	private static final Pattern TOTAL_LIQUID = Pattern.compile(
			"\\s*LIQUIDO\\s*TOTAL\\s*A\\s*PERCIBIR\\s*\\(A\\s*-\\s*B\\)\\s*\\.{2,}\\s*(?<totalliquid>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Firma y sello de la Empresa    VALENCIA    ,    02    de    Diciembre    de    2020RECIBI,
//	Firma y sello de la Empresa    RIBAMONTÁN AL MONTE    ,    31    de    Enero    de    2020
	private static final Pattern ISSUE_DATE = Pattern.compile(
			"\\s*Firma\\s*y\\s*sello\\s*de\\s*la\\s*Empresa\\s*(?<city>.+?)\\s*,\\s*(?<issuedate>(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*de\\s*(?<year>\\d+))(?:\\s*RECIBI,)?\\s*",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern DATE_FORMAT_YEAR = Pattern.compile(
			"\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*de\\s*(?<year>\\d+)\\s*", Pattern.CASE_INSENSITIVE);

//	Determinación de las Bases de Cotización a la Seg.Social y conceptos de recaudación conjunta de la base sujeta a ret. del IRPF
	private static final Pattern COSTS_HEADER = Pattern.compile(
			"\\s*Determinación\\s*de\\s*las\\s*Bases\\s*de\\s*Cotización\\s*a\\s*la\\s*Seg\\.Social\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*de\\s*la\\s*base\\s*sujeta\\s*a\\s*ret\\.\\s*del\\s*IRPF\\s*",
			Pattern.CASE_INSENSITIVE);

//	1. Contingencias comunes    Base    Tipo    Aportación Importe remuneración mensual ...................    99,20    Empresarial
//	1. Contingencias comunes    Base    Tipo    Aportación 
	private static final Pattern REMUNERATION = Pattern.compile(
			"\\s*1\\.\\s*Contingencias\\s*comunes\\s*Base\\s*Tipo\\s*Aportación\\s*(Importe\\s*remuneración\\s*mensual\\s*\\.{2,}\\s*-?(?<remuneration>\\d+(\\.\\d+)*,\\d+)?\\s*Empresarial\\s*)?",
			Pattern.CASE_INSENSITIVE);

//	Importe remuneración mensual ...................    Empresarial
	private static final Pattern REMUNERATION_2 = Pattern.compile(
			"Importe\\s*remuneración\\s*mensual\\s*\\.{2,}\\s*(?<remuneration>\\d+(\\.\\d+)*,\\d+)?\\s*Empresarial\\s*",
			Pattern.CASE_INSENSITIVE);

//	Importe prorrata pagas extraordinarias ......    16,30
	private static final Pattern PRORATION_BASE = Pattern.compile(
			"\\s*Importe\\s*prorrata\\s*pagas\\s*extraordinarias\\s*\\.{2,}\\s*(?<proextbase>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	TOTAL .................................    115,50    23,60    %    27,26
	private static final Pattern CGC_E = Pattern.compile(
			"\\s*TOTAL\\s*\\.{2,}\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
	// ((?<base>\d+(\.\d+)?,\d+)?\s*(?<percent>\d+,\d+)\s{0,5}%|%)?\s*(?<cost>\d+(\.\d+)?,\d+)?\s*

//	AT y EP ......................................    6,70    %    7,74
	private static final Pattern AT_EP = Pattern.compile(
			"\\s*AT\\s*y\\s*EP\\s*\\.{2,}\\s*(?<percent>\\d+,\\d+)?\\s*%?\\s*(?<cost>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	2. Contingencias profesionales    Desempleo ..................................    115,50    6,70    %    7,74
//	2. Contingencias profesionales    Desempleo ..................................    1.050,00    %    57,75
	private static final Pattern UNEMPLOYMENT_E = Pattern.compile(
			"\\s*2\\.\\s*Contingencias\\s*profesionales\\s*Desempleo\\s*\\.{2,}\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	y conceptos de recaudación conjunta    Formación Profesional ................    0,60    %    0,69Fondo de Garantía Salarial .........    0,20    %    0,23
//	y conceptos de recaudación conjunta    Formación Profesional ................    0,60    %	
	private static final Pattern FP_FOGASA_E = Pattern.compile(
			"\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*Formación\\s*Profesional\\s*\\.{2,}\\s*(?<percentfp>\\d+,\\d+)?\\s*%?\\s*(?<costfp>\\d+(\\.\\d+)?,\\d+)?\\s*(Fondo\\s*de\\s*Garantía\\s*Salarial\\s*\\.{2,}\\s*(?<percentfogasa>\\d+,\\d+)?\\s*%?\\s*(?<costfogasa>\\d+(\\.\\d+)?,\\d+)?\\s*)?",
			Pattern.CASE_INSENSITIVE);

//	Fondo de Garantía Salarial .........    0,20    %
	private static final Pattern FP_FOGASA_E_2 = Pattern.compile(
			"\\s*Fondo\\s*de\\s*Garantía\\s*Salarial\\s*\\.{2,}\\s*(?<percentfogasa>\\d+,\\d+)?\\s*%?\\s*(?<costfogasa>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	3. Cotización adicional horas extraordinarias .........................................................
//	3. Cotización adicional horas extraordinarias .........................................................    33,44    7,89
	private static final Pattern EXTRA_HOURS_E = Pattern.compile(
			"\\s*3\\.\\s*Cotización\\s*adicional\\s*horas\\s*extraordinarias\\s*\\.{2,}\\s*(?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	4. Base sujeta a retención del I.R.P.F. .....................................................................    99,20
	private static final Pattern IRPF_E = Pattern.compile(
			"\\s*4\\.\\s*Base\\s*sujeta\\s*a\\s*retención\\s*del\\s*I\\.R\\.P\\.F\\.\\s*\\.{2,}\\s*(?<base>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	5. Coste total mensual (TOTAL DEVENGADO+SEG.SOC. EMPRESA) ....................    142,86
	private static final Pattern TOTAL_ENTERPRISE = Pattern.compile(
			"\\s*5\\.\\s*Coste\\s*total\\s*mensual\\s*\\(TOTAL\\s*DEVENGADO\\+SEG\\.SOC\\.\\s*EMPRESA\\)\\s*\\.{2,}\\s*(?<total>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

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

	private void addPayment(ISalaryBuilder<?> salaryBuilder, Double payment, String concept, String context) {
		PaymentType pt = PaymentType.CRA_0001;
		;
		if (AonStringUtils.containsIgnoreCase(concept, "p.p.extras")
				|| AonStringUtils.containsIgnoreCase(concept, "P.Pagas")) {
			pt = PaymentType.CRA_0004;
			context = "PAGA_EXTRA";
			salaryBuilder.setProExtBase(payment);
		} else if (AonStringUtils.containsIgnoreCase(concept, "horas extras")) {
			pt = PaymentType.CRA_0002;
			context = "HORAS_EXTRAS";
		} else if (AonStringUtils.containsIgnoreCase(concept, "transporte")) {
			pt = PaymentType.CRA_0032;
		} else if (AonStringUtils.containsIgnoreCase(concept, "ESPECIE TRAB")) {
			pt = PaymentType.CRA_0013;
		} else if (AonStringUtils.containsIgnoreCase(concept, "RETRI ESPECIE VEHICULO")) {
			pt = PaymentType.CRA_0016;
		} else if (AonStringUtils.containsIgnoreCase(concept, "ATRASOS")) {
			pt = PaymentType.CRA_0008;
		} else if (AonStringUtils.containsIgnoreCase(concept, "P.P. VACACIONES")) {
			pt = PaymentType.CRA_0006;
		} else if (AonStringUtils.containsIgnoreCase(concept, "vacaciones")) {
			pt = PaymentType.CRA_0060;
		} else if (AonStringUtils.containsIgnoreCase(concept, "estudio")) {
			pt = PaymentType.CRA_0025;
		} else if (AonStringUtils.containsIgnoreCase(concept, "complemento i.t.")
				|| AonStringUtils.containsIgnoreCase(concept, "Accidente")
				|| AonStringUtils.containsIgnoreCase(concept, "enfermedad")) {
			pt = PaymentType.CRA_0000;
		} else if (AonStringUtils.containsIgnoreCase(concept, "objetivo productividad")
				|| AonStringUtils.containsIgnoreCase(concept, "INCENT PRODUCTIVIDAD 1T")
				|| AonStringUtils.containsIgnoreCase(concept, "INCENT PRODUCTIVIDAD 2T")
				|| AonStringUtils.containsIgnoreCase(concept, "INCENT PRODUCTIVIDAD 3T")
				|| AonStringUtils.containsIgnoreCase(concept, "INCENT PRODUCTIVIDAD 4T")) {
			pt = PaymentType.CRA_0005;
		}

		salaryBuilder.addPayment(payment, payment, payment, concept, dateFrom, dateTo,
				(IPayment) new Payment().setType(pt).setName(context), Collections.emptyMap());
	}

	private Matcher find(BufferedReader reader, Pattern pattern) throws IOException, UnknownPDFException {

		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
//				System.out.println(line);
				continue;
			}

			return matcher;
		}

		throw new UnknownPDFException(String.format("Pattern: '%s' Not found", pattern.pattern()));

	}

	private static Date aplifisaDateParser(String date, int year) {
		try {
			if (date != null) {
				Matcher matcher = DATE_FORMAT.matcher(date);
				if (matcher.matches()) {
					int day = Integer.parseInt(matcher.group("day"));
					String strMonth = matcher.group("month");
					int month = monthChooser(strMonth);
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, day);
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.HOUR_OF_DAY, 12);
					return calendar.getTime();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	private static Date aplifisaFullDateParser(String date) {
		try {
			if (date != null) {
				Matcher matcher = DATE_FORMAT_YEAR.matcher(date);
				if (matcher.matches()) {
					int day = Integer.parseInt(matcher.group("day"));
					String strMonth = matcher.group("month");
					int year = Integer.parseInt(matcher.group("year"));
					int month = monthChooser(strMonth);
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, day);
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.HOUR_OF_DAY, 12);
					return calendar.getTime();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	private static int monthChooser(String strMonth) {
		if (AonStringUtils.containsIgnoreCase(strMonth, "ENERO")) {
			return 0;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "FEBRERO")) {
			return 1;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "MARZO")) {
			return 2;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "ABRL")) {
			return 3;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "MAYO")) {
			return 4;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "JUNIO")) {
			return 5;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "JULIO")) {
			return 6;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "AGOSTO")) {
			return 7;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "SEPTIEMBRE")) {
			return 8;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "OCTUBRE")) {
			return 9;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "NOVIEMBRE")) {
			return 10;
		} else if (AonStringUtils.containsIgnoreCase(strMonth, "DICIEMBRE")) {
			return 11;
		}
		return -1;
	}

	private static Date commonDateParser(String date) {
		try {
			String[] splittedDate = date.split("/");
			int day = Integer.parseInt(splittedDate[0]);
			int month = Integer.parseInt(splittedDate[1]) - 1;
			int year = Integer.parseInt(splittedDate[2]);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			return calendar.getTime();
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}

	}

	private static Double aplifisaDoubleParser(String str) {
		str = AonStringUtils.trimToNull(str);
		try {
			str = str.replaceAll("\\.", "").replaceAll(",", ".");
			Double ret = Double.parseDouble(str);
			return ret;
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}

}
