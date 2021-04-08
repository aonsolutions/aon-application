package com.esferalia.aon.in.payroll.pdf.template;

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
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.pdf.template.commons.Deduction;
import com.esferalia.aon.in.payroll.pdf.template.commons.Payment;
import com.esferalia.aon.in.payroll.pdf.template.regex.A3Regex;
import com.esferalia.aon.in.payroll.pdf.template.tool.PdfParsingTools;
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

	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);

		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			boolean isProExtBaseSet = false;
			Matcher matcher = find(reader, A3Regex.EMPLOYEE_NAME);
			String empName = AonStringUtils.trimToNull(matcher.group("name"));
			empName = PdfParsingTools.removeSpace(empName, 13);

			matcher = find(reader, A3Regex.EMPLOYEE_ADDRESS);
			String empHome = null;
			try {
				empHome = AonStringUtils.trimToNull(matcher.group()).replaceAll("\\s{2,}", " ");
			} catch (NullPointerException e) {
			}

			matcher = find(reader, A3Regex.PC_AND_MUNICIPALITY);
			String empCity = AonStringUtils.trimToNull(matcher.group("municipality"));

			matcher = find(reader, A3Regex.NIF);
			String nif = AonStringUtils.trimToNull(matcher.group("nif"));

			matcher = find(reader, A3Regex.ENTERPRISE_HOME);
			String entName = AonStringUtils.trimToNull(matcher.group("enterprisename"));
			salaryBuilder.setEnterpriseName(entName);
			String entAddress = AonStringUtils.trimToNull(matcher.group("address"));
			entAddress = PdfParsingTools.removeSpace(entAddress, 13);
			salaryBuilder.setEnterpriseAddress(entAddress);
			String ccc = AonStringUtils.trimToNull(matcher.group("nss")).replaceAll("[/]", "").replaceAll("[-]", "");
			salaryBuilder.setCcc(ccc);

			salaryBuilder.setEmployeeName(empName);
			salaryBuilder.setEmployeeAddress(empHome);
			empCity = PdfParsingTools.removeSpace(empCity, 6);
			salaryBuilder.setEmployeeCity(empCity);
			salaryBuilder.setEnterpriseDocument(nif);

			matcher = find(reader, A3Regex.WORKER_HEADER);

			matcher = find(reader, A3Regex.WORKER);

			salaryBuilder.setCategory(AonStringUtils.trimToNull(matcher.group("job")));

			String seniority = AonStringUtils.trimToNull(matcher.group("old"));
			salaryBuilder.setSeniorityDate(PdfParsingTools.a3DateParser(seniority));
			String dni = AonStringUtils.trimToNull(matcher.group("nif"));
			salaryBuilder.setEmployeeDocument(dni);

			matcher = find(reader, A3Regex.SS_INFO);

			String naf = AonStringUtils.trimToNull(matcher.group("affnum")).replaceAll("[/]", "").replaceAll("[-]", "");
			salaryBuilder.setSocialSecurityNumber(naf);
			String quoteGroup = AonStringUtils.trimToNull(matcher.group("tarifa"));
			salaryBuilder.setQuoteGroup(quoteGroup);
			String codct = AonStringUtils.trimToNull(matcher.group("codct"));

			Pattern period = Pattern.compile(
					"\\s*(?<type>\\w*)\\s*(?<from>\\d{1,2}\\s*\\w+\\s*\\d+)\\s*a\\s*(?<to>\\d{1,2}\\s*\\w+\\s*\\d+)\\s*",
					Pattern.CASE_INSENSITIVE);
			String periodo = matcher.group("period");

			Matcher subMatcher = period.matcher(periodo);
			Date dTo = null;
			Date dFrom = null;
			Period per = null;
			if (subMatcher.matches()) {
				// charge and end dates
				String sdTo = subMatcher.group("to");
				dTo = PdfParsingTools.a3DateParser(sdTo);
				salaryBuilder.setChargeDate(dTo);
				salaryBuilder.setEndDate(dTo);
				// start date
				String sdFrom = subMatcher.group("from");
				dFrom = PdfParsingTools.a3DateParser(sdFrom);
				salaryBuilder.setStartDate(dFrom);
				per = new Period(dFrom, dTo);
			}
			try {
				Integer timeUnits = Integer.parseInt(matcher.group("days"));
				salaryBuilder.setTimeUnits(timeUnits);
				salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(timeUnits, per));

			} catch (NumberFormatException e) {

			}
			salaryBuilder.setContract(new PDFContract().setCcc(ccc).setNaf(naf).setNif(dni).setCif(nif).setEndDate(dTo)
					.setStartDate(dFrom).setEmployeeName(empName).setEnterpriseName(entName));
			salaryBuilder.addData("TC2", new TimedObject<String>(codct, per));
			salaryBuilder.addData("GRUPO_COTIZACION", new TimedObject<String>(quoteGroup, per));

			matcher = find(reader, A3Regex.CONCEPT_HEADER);
			String concept_line = reader.readLine();
			matcher = A3Regex.TOTAL_HEADER.matcher(concept_line);

			Double totalSS = 0d;
			Integer payrollType = null;

			while (!matcher.matches()) {
				matcher = A3Regex.CONCEPT.matcher(concept_line);
				if (matcher.matches()) {
					if (matcher.group("devengos") != null) {
						Double amount = PdfParsingTools
								.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("devengos")));
						String description = AonStringUtils.trimToNull(matcher.group("concept"));
						description = PdfParsingTools.removeSpace(description, 19);
						if (description.charAt(0) == '*' || description.charAt(0) == '-') {
							description = description.substring(1).trim();
						}
						String context = description;
						context = context.toUpperCase();
						context = context.replaceAll("\\s", "_");
						context = (context.length() > 25) ? context.substring(0, 24) : context;
						PaymentType pt = null;
						if (payrollType == null) {

							pt = PaymentType.CRA_0001;
							if (AonStringUtils.containsIgnoreCase(description, "p.p.extras")
									|| AonStringUtils.containsIgnoreCase(description, "P.Pagas")) {
								// System.err.println("\t"+description);
								pt = PaymentType.CRA_0004;
								context = "PAGA_EXTRA";
								salaryBuilder.setProExtBase(amount);
								isProExtBaseSet = true;
							} else if (AonStringUtils.containsIgnoreCase(description, "horas extras")) {
								pt = PaymentType.CRA_0002;
								context = "HORAS_EXTRAS";
							} else if (AonStringUtils.containsIgnoreCase(description, "ESPECIE TRAB")) {
								pt = PaymentType.CRA_0013;
							} else if (AonStringUtils.containsIgnoreCase(description, "RETRI ESPECIE VEHICULO")) {
								pt = PaymentType.CRA_0016;
							} else if (AonStringUtils.containsIgnoreCase(description, "ATRASOS")) {
								pt = PaymentType.CRA_0008;
							} else if (AonStringUtils.containsIgnoreCase(description, "vacaciones")) {
								pt = PaymentType.CRA_0060;
							} else if (AonStringUtils.containsIgnoreCase(description, "estudio")) {
								pt = PaymentType.CRA_0025;
							} else if (AonStringUtils.containsIgnoreCase(description, "complemento i.t.")
									|| AonStringUtils.containsIgnoreCase(description, "Accidente")
									|| AonStringUtils.containsIgnoreCase(description, "enfermedad")) {
								pt = PaymentType.CRA_0000;
							} else if (AonStringUtils.containsIgnoreCase(description, "objetivo productividad")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 1T")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 2T")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 3T")
									|| AonStringUtils.containsIgnoreCase(description, "INCENT PRODUCTIVIDAD 4T")) {
								pt = PaymentType.CRA_0005;
							}
						} else if (payrollType == 2) {
							pt = PaymentType.CRA_0009;
						}

						salaryBuilder.addPayment(amount, amount, amount, description, dFrom, dTo,
								(IPayment) new Payment().setType(pt).setName(context), Collections.emptyMap());

					} else if (matcher.group("deducciones") != null) {
						Double amount = PdfParsingTools
								.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("deducciones")));
						String description = AonStringUtils.trimToNull(matcher.group("concept"));
						description = PdfParsingTools.removeSpace(description, 19);
						DeductionType dt = null;
						String context = null;
						Double type = null;
						try {
							type = PdfParsingTools
									.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("tipo")));
						} catch (NullPointerException e) {
						}
						if (description.contains("COTIZACION CONT.COMU")) {
							dt = DeductionType.COMMON_CONTINGENCY;
							description = dt.getName(new Locale("es", "ES"));
							totalSS += amount;
							context = "CGC";
							if (type != null) {
								salaryBuilder.addData("PORCENTAJE_CGC", new TimedObject<Double>(type, per));
							}
						} else if (description.contains("COTIZACION FORMACION")) {
							dt = DeductionType.JOB_TRAINING;
							description = dt.getName(new Locale("es", "ES"));
							totalSS += amount;
							context = "FP";
							if (type != null) {
								salaryBuilder.addData("PORCENTAJE_FP", new TimedObject<Double>(type, per));
							}
						} else if (description.contains("COTIZACION DESEMPLEO")) {
							dt = DeductionType.UNEMPLOYMENT;
							description = dt.getName(new Locale("es", "ES"));
							totalSS += amount;
							context = "DESMPL";
							if (type != null) {
								salaryBuilder.addData("PORCENTAJE_DESMPL", new TimedObject<Double>(type, per));
							}
						} else if (description.contains("TRIBUTACION I.R.P.F.")) {
							dt = DeductionType.IRPF;
							description = dt.getName(new Locale("es", "ES"));
							context = "IRPF";
							if (type != null) {
								salaryBuilder.addData("PORCENTAJE_IRPF", new TimedObject<Double>(type, per));
							}
						}

						salaryBuilder.addDeduction(amount, description, dFrom, dTo,
								new Deduction().setType(dt).setName(context), Collections.emptyMap());

					} else {
						matcher = A3Regex.SETTLEMENT.matcher(concept_line);
						if (matcher.matches()) {
							salaryBuilder.setType(SalaryType.SETTLE);
							payrollType = 1;

						} else {
							matcher = A3Regex.ATRASOS_CONV.matcher(concept_line);
							if (matcher.matches()) {
								salaryBuilder.setType(SalaryType.DELAY);
								payrollType = 2;
							} else {

							}
						}

					}
				}

				concept_line = reader.readLine();
				matcher = A3Regex.TOTAL_HEADER.matcher(concept_line);
			}

			String strSalary = reader.readLine();

			matcher = A3Regex.TOTAL.matcher(strSalary);
			ArrayList<String> remnbases = new ArrayList<String>();
			while (matcher.find())
				remnbases.add(matcher.group());

			try {
				salaryBuilder.setRemuneration(Double.parseDouble(
						AonStringUtils.trimToNull(remnbases.get(0)).replaceAll("\\.", "").replaceAll("[,]", ".")));

			} catch (NullPointerException e) {
				salaryBuilder.setRemuneration(0d);
			}

			try {
				Double amount = Double.parseDouble(
						AonStringUtils.trimToNull(remnbases.get(1)).replaceAll("\\.", "").replaceAll("[,]", "."));
				salaryBuilder.setProExtBase(amount);
				salaryBuilder.addPayment(0d, amount, 0d, "PAGA_EXTRA", dFrom, dTo,
						new Payment().setType(PaymentType.CRA_0004), Collections.emptyMap());
			} catch (NullPointerException e) {
				if (!isProExtBaseSet)
					salaryBuilder.setProExtBase(0d);
			}

			try {
				Double cgcBase = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(2)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setCgcBase(cgcBase);
				salaryBuilder.addData(ContextVariable.CGC_BASE.getName(), new TimedObject<Double>(cgcBase, per));
			} catch (NullPointerException e) {
				salaryBuilder.setCgcBase(0d);
			}

			try {
				Double rawCgcBase = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(2)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setRawCgcBase(rawCgcBase);
				salaryBuilder.addData(ContextVariable.CGC_BASE_RAW.getName(), new TimedObject<Double>(rawCgcBase, per));
			} catch (NullPointerException e) {
				salaryBuilder.setRawCgcBase(null);
			}

			try {
				Double cgpBase = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(3)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setCgpBase(cgpBase);
				salaryBuilder.addData(ContextVariable.CGP_BASE.getName(), new TimedObject<Double>(cgpBase, per));
			} catch (NullPointerException e) {
				salaryBuilder.setCgpBase(0d);
			}

			try {
				Double totalIrpf = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(4)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalIrpf(totalIrpf);
				salaryBuilder.addData(ContextVariable.IRPF_BASE.getName(), new TimedObject<Double>(totalIrpf, per));
			} catch (NullPointerException e) {
				salaryBuilder.setTotalIrpf(0d);
			}

			try {
				Double totalPayment = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(5)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalPayment(totalPayment);
				salaryBuilder.addData(ContextVariable.TOTAL_PAYMENT.getName(),
						new TimedObject<Double>(totalPayment, per));
			} catch (NullPointerException e) {
				salaryBuilder.setTotalPayment(0d);
			}

			try {
				Double totalDeduction = Double.parseDouble(PdfParsingTools.removeSpace(
						AonStringUtils.trimToNull(remnbases.get(6)).replaceAll("\\.", "").replaceAll("[,]", "."), -1));
				salaryBuilder.setTotalDeduction(totalDeduction);
			} catch (NullPointerException e) {
				salaryBuilder.setTotalDeduction(0d);
			}

			// ISSUE DATE
			matcher = find(reader, A3Regex.DATE);
			String issuestr = AonStringUtils.trimToNull(matcher.group("day")) + " "
					+ AonStringUtils.trimToNull(matcher.group("month")) + " "
					+ AonStringUtils.trimToNull(matcher.group("year"));
			Date issueDate = PdfParsingTools.a3DateParser(issuestr);
			salaryBuilder.setIssueDate(issueDate);

			// PLACE
			matcher = find(reader, A3Regex.PLACE);

			// TOTAL LIQUID
			matcher = find(reader, A3Regex.TOTAL_LIQUID_HEADER);
			matcher = find(reader, A3Regex.TOTAL_LIQUID);
			Double totLiq = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("liquid")));
			totLiq = totLiq == null ? 0d : totLiq;
			salaryBuilder.setTotalLiquid(totLiq);

			// IBAN
			matcher = find(reader, A3Regex.IBAN);

			// APPORTATION
			Double totalEnterprise = 0d;
			matcher = find(reader, A3Regex.APPORT_HEADER_BOTTOM);

			matcher = find(reader, A3Regex.APPORT_CC);
			Double apportBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			Double individualApport = PdfParsingTools
					.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			String description = COMMON_CONTINGENCY.getName(new Locale("es", "ES"));
			{
				Double type = null;
				try {
					type = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {
				}
				if (type != null) {
					salaryBuilder.addData("PORCENTAJE_CGC_E", new TimedObject<Double>(type, per));
				}
			}
			if (apportBase != null)
				salaryBuilder.addData(ContextVariable.CGC_ENTERPRISE.getName(),
						new TimedObject<Double>(apportBase, per));
			Deduction costDeduction = new Deduction().setType(COMMON_CONTINGENCY).setName("CGC_E");
			if (individualApport != null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise += individualApport;
			}
			matcher = find(reader, A3Regex.APPORT_AT_EP);
			apportBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description = DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES"));
			{
				Double type = null;
				try {
					type = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {
				}
				if (type != null) {
					salaryBuilder.addData("PORCENTAJE_CGP_E", new TimedObject<Double>(type, per));
				}
			}
			if (apportBase != null) {
				salaryBuilder.addData(ContextVariable.IT_ENTERPRISE.getName(),
						new TimedObject<Double>(apportBase, per));
				salaryBuilder.addData(ContextVariable.IMS_ENTERPRISE.getName(), new TimedObject<Double>(0d, per));
				Deduction imsDeduction = new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY)
						.setName("IMS_E");
				salaryBuilder.addCost(0d, "IMS_E", dFrom, dTo, imsDeduction, Collections.emptyMap());
			}
			costDeduction = new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IT_E");
			if (individualApport != null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise += individualApport;
			}
			matcher = find(reader, A3Regex.APPORT_UNEMPLOYMENT);
			apportBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description = DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES"));
			{
				Double type = null;
				try {
					type = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {
				}
				if (type != null) {
					salaryBuilder.addData("PORCENTAJE_DESMPL_E", new TimedObject<Double>(type, per));
				}
			}
			if (apportBase != null)
				salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE.getName(),
						new TimedObject<Double>(apportBase, per));
			costDeduction = new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESEMPL_E");
			if (individualApport != null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise += individualApport;
			}
			matcher = find(reader, A3Regex.APPORT_FP);
			apportBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description = DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
			{
				Double type = null;
				try {
					type = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {
				}
				if (type != null) {
					salaryBuilder.addData("PORCENTAJE_FP_E", new TimedObject<Double>(type, per));
				}
			}
			if (apportBase != null)
				salaryBuilder.addData(ContextVariable.FP_ENTERPRISE.getName(),
						new TimedObject<Double>(apportBase, per));
			costDeduction = new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
			if (individualApport != null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise += individualApport;
			}
			matcher = find(reader, A3Regex.APPORT_FOGASA);
			apportBase = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("base")));
			individualApport = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("apport")));
			description = DeductionType.FOGASA.getName(new Locale("es", "ES"));
			{
				Double type = null;
				try {
					type = PdfParsingTools.payrollDoubleParser(AonStringUtils.trimToNull(matcher.group("type")));
				} catch (NullPointerException e) {
				}
				if (type != null) {
					salaryBuilder.addData("PORCENTAJE_FOGASA", new TimedObject<Double>(type, per));
				}
			}
			if (apportBase != null)
				salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
						new TimedObject<Double>(apportBase, per));
			costDeduction = new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
			if (individualApport != null) {
				salaryBuilder.addCost(individualApport, description, dFrom, dTo, costDeduction, Collections.emptyMap());
				totalEnterprise += individualApport;
			}

			totalEnterprise = Math.round(totalEnterprise * 100.0) / 100.0;
			salaryBuilder.setTotalEnterprise(totalEnterprise);
			// totalSS+=totalEnterprise;
			totalSS = Math.round(totalSS * 100.0) / 100.0;
			salaryBuilder.setTotalSS(totalSS);
			salaryBuilder.getSalary();
		}
		return this;

	}

	private Matcher find(BufferedReader reader, Pattern pattern) throws IOException, UnknownPDFException {

		String line;
		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				// System.out.println(line);
				continue;
			}

			return matcher;
		}

		throw new UnknownPDFException(String.format("Pattern: '%s' Not found", pattern.pattern()));

	}

}
