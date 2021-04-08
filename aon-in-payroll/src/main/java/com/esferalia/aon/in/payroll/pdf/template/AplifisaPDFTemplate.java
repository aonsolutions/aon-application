package com.esferalia.aon.in.payroll.pdf.template;

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
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.pdf.template.commons.Deduction;
import com.esferalia.aon.in.payroll.pdf.template.regex.AplifisaRegex;
import com.esferalia.aon.in.payroll.pdf.template.tool.PdfParsingTools;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AplifisaPDFTemplate implements SalaryPDFTemplate {

	private Date dateFrom;
	private Date dateTo;
	private Period period;
//	private static int cont = 1;

	public static final AplifisaPDFTemplate APLIFISA_PDF_TEMPLATE = new AplifisaPDFTemplate();

	private Date issueDateFinder(String text) {
		Matcher matcher = AplifisaRegex.ISSUE_DATE.matcher(text);
		if (matcher.find()) {
			return PdfParsingTools.aplifisaFullDateParser(AonStringUtils.trimToNull(matcher.group("issuedate")));
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
			Matcher matcher = find(reader, AplifisaRegex.ENTERPRISE_EMPLOYEE);

			matcher = find(reader, AplifisaRegex.ENT_EMP_NAMES);
			String entName = AonStringUtils.trimToNull(matcher.group("enterprise"));
			salaryBuilder.setEnterpriseName(entName);
			String empName = AonStringUtils.trimToNull(matcher.group("employee"));
			salaryBuilder.setEmployeeName(empName);

			matcher = find(reader, AplifisaRegex.ENTHOME_NIF);
			salaryBuilder.setEnterpriseAddress(AonStringUtils.trimToNull(matcher.group("enthome")));
			String nif = AonStringUtils.trimToNull(matcher.group("nif"));
			salaryBuilder.setEmployeeDocument(nif);

			matcher = find(reader, AplifisaRegex.ENTCITY_NSS);
			salaryBuilder.setEnterpriseCity(AonStringUtils.trimToNull(matcher.group("entcity")));
			String naf = AonStringUtils.trimToNull(matcher.group("nss").replaceAll("-", ""));
			salaryBuilder.setSocialSecurityNumber(naf);

			matcher = find(reader, AplifisaRegex.CIF_CATEGORY);
			String cif = AonStringUtils.trimToNull(matcher.group("cif"));
			salaryBuilder.setEnterpriseDocument(cif);
			salaryBuilder.setCategory(AonStringUtils.trimToNull(matcher.group("category")));

			matcher = find(reader, AplifisaRegex.CCC_QUOTEGROUP_SENIORITY);
			String ccc = AonStringUtils.trimToNull(matcher.group("ccc").replaceAll("-", ""));
			salaryBuilder.setCcc(ccc);
			String quoteGroup = matcher.group("quotegroup");
			salaryBuilder.setQuoteGroup(AonStringUtils.trimToNull(quoteGroup));

			Date seniority = PdfParsingTools.commonDateParser(AonStringUtils.trimToNull(matcher.group("seniority")));
			salaryBuilder.setSeniorityDate(seniority);
			matcher = find(reader, AplifisaRegex.LIQPERIOD_TIMEUNITS);

			if (matcher.group("liqperiod") != null) {

				try {

					if (matcher.group("salarytype") == null) {
						Integer year = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("year")));
						dateFrom = PdfParsingTools.aplifisaDateParser(matcher.group("from"), year);
						dateTo = PdfParsingTools.aplifisaDateParser(matcher.group("to"), year);
					} else {
						String strSalaryType = AonStringUtils.trimToNull(matcher.group("salarytype"));

						if (AonStringUtils.containsIgnoreCase(strSalaryType, "PAGA EXTRAORDINARIA")) {
							Integer year = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("year")));
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, 12);
							Integer month = PdfParsingTools.monthChooser(AonStringUtils.trimToNull(matcher.group("month")));
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
						} else if (AonStringUtils.containsIgnoreCase(strSalaryType, "LIQUIDACIÓN") ||
								AonStringUtils.containsIgnoreCase(strSalaryType, "FIN CONTRATO")) {
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

				salaryBuilder.setContract(new PDFContract()
						.setCcc(ccc)
						.setNaf(naf)
						.setNif(nif)
						.setCif(cif)
						.setEndDate(dateTo)
						.setStartDate(dateFrom)
						.setEmployeeName(empName)
						.setEnterpriseName(entName));

				try {
					Integer timeUnits = Integer.parseInt(AonStringUtils.trimToNull(matcher.group("timeunits")));
					salaryBuilder.setTimeUnits(timeUnits);
					salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(timeUnits, period));
				} catch (NullPointerException | IllegalArgumentException e) {
					salaryBuilder.setTimeUnits(0);
				}
			}

			matcher = find(reader, AplifisaRegex.PAYMENTS_HEADER);

			matcher = find(reader, AplifisaRegex.SALARY_PAYMENT_HEADER);
			String line = reader.readLine();
			matcher = AplifisaRegex.NON_SALARY_PAYMENT_HEADER.matcher(line);

			while (!matcher.matches()) {
				matcher = AplifisaRegex.PAYMENT.matcher(line);
				if (matcher.matches()) {
					Double payment = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					String context = (concept.length() > 25) ? concept.substring(0, 24) : concept;
					PdfParsingTools.addPayment(salaryBuilder, payment, concept, context, dateFrom, dateTo);
				}

				line = reader.readLine();
				matcher = AplifisaRegex.NON_SALARY_PAYMENT_HEADER.matcher(line);
			}

			matcher = AplifisaRegex.TOTAL_PAYMENT.matcher(line);

			while (!matcher.matches()) {
				matcher = AplifisaRegex.PAYMENT.matcher(line);
				if (matcher.matches()) {
					Double payment = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					String context = (concept.length() > 25) ? concept.substring(0, 24) : concept;
					PdfParsingTools.addPayment(salaryBuilder, payment, concept, context, dateFrom, dateTo);
				}

				line = reader.readLine();
				matcher = AplifisaRegex.TOTAL_PAYMENT.matcher(line);
			}

			try {
				Double totalPayment = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("payment")));
				if (totalPayment != null)
					salaryBuilder.setTotalPayment(totalPayment);
				else
					salaryBuilder.setTotalPayment(0d);
				salaryBuilder.addData(ContextVariable.TOTAL_PAYMENT.getName(),
						new TimedObject<Double>(totalPayment, period));
			} catch (NullPointerException e) {
				salaryBuilder.setTotalPayment(0d);
			}

			matcher = find(reader, AplifisaRegex.DEDUCTIONS_HEADER);

			matcher = find(reader, AplifisaRegex.SS_APPORTS_HEADER);

			// DEDUCTIONS

			matcher = find(reader, AplifisaRegex.COMMON_CONTINGENCY);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
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

			matcher = find(reader, AplifisaRegex.UNEMPLOYMENT);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (base != null)
					salaryBuilder.setCgpBase(base);
				else
					salaryBuilder.setCgpBase(0d);
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
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

			matcher = find(reader, AplifisaRegex.FORMACION_PROFESIONAL);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
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

			matcher = find(reader, AplifisaRegex.EXTRA_HOURS);

			matcher = find(reader, AplifisaRegex.CHUCK_NORRIS_HOURS);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
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

			matcher = find(reader, AplifisaRegex.NON_STRUCTURAL_HOURS);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.NON_STRUCTURAL_OVERTIME;
				String context = "NO_ESTR";
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

			matcher = find(reader, AplifisaRegex.TOTAL_APPORT);
			Double total_apport = 0d;
			if (matcher.group("apport") != null) {
				total_apport = PdfParsingTools.payrollDoubleParser(matcher.group("apport"));
			}
			salaryBuilder.setTotalSS(total_apport);
			matcher = find(reader, AplifisaRegex.IRPF);

			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (base != null)
					salaryBuilder.setIrpfBase(base);
				else 
					salaryBuilder.setIrpfBase(0d);
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				DeductionType dt = DeductionType.IRPF;
				String context = "IRPF";
				String description = dt.getName(new Locale("es", "ES"));
				if (deduction != null) {
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
					salaryBuilder.setTotalIrpf(deduction);
					if (base != null) {

						salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(),
								new TimedObject<Double>(base, period));
					}
					if (percent != null) {
						salaryBuilder.addData("PORCENTAJE_IRPF", new TimedObject<Double>(percent, period));
					}
				} else {
					salaryBuilder.setTotalIrpf(0d);
				}

			}

			matcher = find(reader, AplifisaRegex.ADVANCED_PAYMENTS);

			{
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				if (deduction != null) {
					DeductionType dt = DeductionType.ADVANCE_PAYMENT;
					String context = "ADELANTO";
					String description = dt.getName(new Locale("es", "ES"));
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
				}
			}

			matcher = find(reader, AplifisaRegex.IN_KIND);

			{
				Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));
				if (deduction != null) {
					DeductionType dt = DeductionType.IN_KIND;
					String context = "ESPECIE";
					String description = dt.getName(new Locale("es", "ES"));
					salaryBuilder.addDeduction(deduction, description, dateFrom, dateTo,
							new Deduction().setType(dt).setName(context), Collections.emptyMap());
				}
			}

			matcher = find(reader, AplifisaRegex.OTHER);

			line = reader.readLine();

			matcher = AplifisaRegex.TOTAL_DEDUCTION.matcher(line);

			while (!matcher.matches()) {

				matcher = AplifisaRegex.OTHER_DEDUCTION.matcher(line);
				if (matcher.matches()) {
					String concept = AonStringUtils.trimToNull(matcher.group("concept"));
					Double deduction = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deduction")));

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
				matcher = AplifisaRegex.TOTAL_DEDUCTION.matcher(line);
			}
			if (matcher.group("deduction") != null) {
				Double deduction = PdfParsingTools.payrollDoubleParser(matcher.group("deduction"));
				if (deduction != null)
					salaryBuilder.setTotalDeduction(deduction);
				else
					salaryBuilder.setTotalDeduction(0d);
			} else
				salaryBuilder.setTotalDeduction(0d);

			matcher = find(reader, AplifisaRegex.TOTAL_LIQUID);
			{
				Double totalLiquid = PdfParsingTools.payrollDoubleParser(matcher.group("totalliquid"));
				if(totalLiquid!=null)
					salaryBuilder.setTotalLiquid(totalLiquid);
				else
					salaryBuilder.setTotalLiquid(0d);
			}
			matcher = find(reader, AplifisaRegex.ISSUE_DATE);

			salaryBuilder.setIssueDate(PdfParsingTools.aplifisaFullDateParser(AonStringUtils.trimToNull(matcher.group("issuedate"))));

			matcher = find(reader, AplifisaRegex.COSTS_HEADER);

			matcher = find(reader, AplifisaRegex.REMUNERATION);

			{
				Double remuneration = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToEmpty(matcher.group("remuneration")));
				if (remuneration != null)
					salaryBuilder.setRemuneration(remuneration);
				else
					salaryBuilder.setRemuneration(0d);

				line = reader.readLine();
				matcher = AplifisaRegex.PRORATION_BASE.matcher(line);
				while (!matcher.matches()) {
					matcher = AplifisaRegex.REMUNERATION_2.matcher(line);
					if (matcher.matches()) {
						remuneration = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToEmpty(matcher.group("remuneration")));
						if (remuneration != null)
							salaryBuilder.setRemuneration(remuneration);
						else
							salaryBuilder.setRemuneration(0d);
					}
					line = reader.readLine();
					matcher = AplifisaRegex.PRORATION_BASE.matcher(line);
				}

			}

			{
				Double proExtBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToEmpty(matcher.group("proextbase")));
				if (proExtBase != null)
					salaryBuilder.setProExtBase(proExtBase);
				else
					salaryBuilder.setProExtBase(0d);
			}

			matcher = find(reader, AplifisaRegex.CGC_E);
			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

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

			matcher = find(reader, AplifisaRegex.AT_EP);
			{
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

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

			matcher = find(reader, AplifisaRegex.UNEMPLOYMENT_E);
			{
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				Double percent = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percent")));
				Double cost = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));

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

			matcher = find(reader, AplifisaRegex.FP_FOGASA_E);
			{
				Double percentfp = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfp")));
				Double costfp = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("costfp")));

				Double percentfogasa = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfogasa")));
				Double costfogasa = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("costfogasa")));

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
				matcher = AplifisaRegex.EXTRA_HOURS_E.matcher(line);
				while (!matcher.matches()) {
					matcher = AplifisaRegex.FP_FOGASA_E_2.matcher(line);
					if (matcher.matches()) {
						percentfogasa = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("percentfogasa")));
						costfogasa = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("costfogasa")));

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
					matcher = AplifisaRegex.EXTRA_HOURS_E.matcher(line);
				}

			}

			{
				Double cost = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("cost")));
				Double base = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
				if (cost != null) {
					Deduction costDeduction = new Deduction().setType(DeductionType.STRUCTURAL_OVERTIME)
							.setName("EXTRAH_E");
					salaryBuilder.addCost(cost, DeductionType.STRUCTURAL_OVERTIME.getName(new Locale("es", "ES")),
							dateFrom, dateTo, costDeduction, Collections.emptyMap());
					if (base != null) {
						salaryBuilder.addData(ContextVariable.EXTRA_HOURS.getName(),
								new TimedObject<Double>(base, period));
					}
				}
			}

			matcher = find(reader, AplifisaRegex.IRPF_E);


			matcher = find(reader, AplifisaRegex.TOTAL_ENTERPRISE);
			{
				Double total = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("total")));
				if (total != null) {
					Double total_enterprise = PdfParsingTools.payrollDoubleParser(matcher.group("total"));
					if(total_enterprise != null)
						salaryBuilder.setTotalEnterprise(total_enterprise);

				} else
					salaryBuilder.setTotalEnterprise(0d);
			}
			salaryBuilder.getSalary();
		}
//		System.err.println(cont);
//		cont++;
		return this;
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


}
