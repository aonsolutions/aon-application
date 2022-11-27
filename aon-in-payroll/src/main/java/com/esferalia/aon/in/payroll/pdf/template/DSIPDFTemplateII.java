package com.esferalia.aon.in.payroll.pdf.template;

import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;

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
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DSIPDFTemplateII implements SalaryPDFTemplate {

	public static final DSIPDFTemplateII DSI_PDF_TEMPLATE_II = new DSIPDFTemplateII();

	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder)
			throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

			PDFContract pdfContract = DSIPDFTemplate.parseSalaryHeader(reader, salaryBuilder);
			Period salaryPeriod = DSIPDFTemplate.parseSalaryPeriod(reader, salaryBuilder);

			salaryBuilder.setContract(
					pdfContract
					.setEndDate(salaryPeriod.getEnd())
					.setStartDate(salaryPeriod.getStart()));
			salaryBuilder.addData(
			"GRUPO_COTIZACION", 
			new TimedObject<String>(pdfContract.getQuoteGroup(), salaryPeriod));

			DSIPDFTemplate.parseSalaryPayments(reader, salaryBuilder, salaryPeriod);
			Double totalSS = DSIPDFTemplate.parseSalaryDeductions(reader, salaryBuilder, salaryPeriod);

			DSIPDFTemplate.parseSalaryLiquid(reader, salaryBuilder);
			DSIPDFTemplate.parseSalaryIssueDate(reader, salaryBuilder);
	
			// Enterprise costs 
			String line;
			Matcher matcher;

			matcher = DSIPDFTemplate.find(reader, DSIPDFTemplate.CC_MONTHLY);

			String remuneration = AonStringUtils.trimToEmpty(matcher.group("amount"));
			try {
				if (remuneration != null) {
					salaryBuilder.setRemuneration(
							Double.parseDouble(remuneration.replace(".", "").replace(",", ".")));
				} 
			} catch (NumberFormatException e) {
			}

			matcher = DSIPDFTemplate.find(reader, DSIPDFTemplate.CC_EXTRA);
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
			matcher = DSIPDFTemplate.find(reader, DSIPDFTemplate.IT_BASE);
			{
				String strIt = AonStringUtils.trimToEmpty(matcher.group("amount"));
				try {
					if (strIt != null) {
						salaryBuilder.setItBase(Double.parseDouble(strIt.replace(".", "").replace(",", ".")));
					} 
				} catch (NumberFormatException e) {
				}
			}
			matcher = DSIPDFTemplate.find(reader, DSIPDFTemplate.CC);

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
						totalSS += ccCost;
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
			
			
			matcher = DSIPDFTemplate.find(reader, AT_EP);
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
						totalSS += atEpCost;
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
			
			
			matcher = DSIPDFTemplate.find(reader, UNEMPLOYMENT_AND_ATEP);
			{
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
					totalSS += fpCost;
				}
				if (fpPercent != null) {
					salaryBuilder.addData("PORCENTAJE_FP_E",
							new TimedObject<>(fpPercent, salaryPeriod));
					
				}
				
			} 
			
			matcher = DSIPDFTemplate.find(reader, FP_COST);
			String strFpPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
			String strFpCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
			if (strFpCost != null) {
				strFpCost = strFpCost.replace(".", "").replace(",", ".");
				try {
					Double fpCost = Double.parseDouble(strFpCost);
					String description = DeductionType.JOB_TRAINING.getName(new Locale("es", "ES"));
					Deduction costDeduction = new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E");
					salaryBuilder.addCost(fpCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
					totalSS += fpCost;
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
			matcher = DSIPDFTemplate.find(reader, FOGASA_COST);
			String strFogasaPercent = AonStringUtils.trimToEmpty(matcher.group("percent"));
			String strFogasaCost = AonStringUtils.trimToEmpty(matcher.group("cost"));
			if (strFogasaCost != null) {
				strFogasaCost = strFogasaCost.replace(".", "").replace(",", ".");
				try {
					Double fogasaCost = Double.parseDouble(strFogasaCost);
					String description = DeductionType.FOGASA.getName(new Locale("es", "ES"));
					Deduction costDeduction = new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E");
					salaryBuilder.addCost(fogasaCost, description, salaryPeriod.getStart(), salaryPeriod.getEnd(), costDeduction, Collections.emptyMap());
					totalSS += fogasaCost;
				} catch (NumberFormatException e) {
				}
				if (strFogasaPercent != null) {
					try {
						salaryBuilder.addData("PORCENTAJE_FOGASA",
								new TimedObject<>(dsiDoubleParser(strFogasaPercent), salaryPeriod));
					} catch (NullPointerException e) {
					}
				}

				matcher = DSIPDFTemplate.find(reader, EXTRA_H);
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
				matcher = DSIPDFTemplate.find(reader, IRPF);
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
				totalSS = Math.round(totalSS * 100.0) / 100.0;
				salaryBuilder.setTotalSS(totalSS);
				salaryBuilder.getSalary();
			}
			return this;
		}
	}

	private static Double dsiDoubleParser(String strNum) {
		strNum = strNum.replace(".", "").replace(",", ".");
		try {
			return Double.parseDouble(strNum);
		} catch (NumberFormatException e) {
			return null;
		}
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


	
//	2. Contingencias profesionales    AT y EP.....................    %
//	2. Contingencias profesionales    AT y EP.....................    1,65%    10,30
	public static Pattern AT_EP = Pattern.compile(
			"\\s*2\\.\\s*Contingencias\\s*profesionales\\s*\\s*AT\\s*y\\s*EP\\.{2,}\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$",
			Pattern.CASE_INSENSITIVE);
	
	
//  (AT.y EP.) y conceptos de    Desempleo...................         %   
//	(AT.y EP.) y conceptos de    Desempleo...................    624,45    624,45    5,50%    34,34
	public static Pattern UNEMPLOYMENT_AND_ATEP = Pattern.compile("\\s*\\(AT\\.y\\s*EP\\.\\)\\s*y\\s*conceptos\\s*de\\s*Desempleo\\.{2,}\\s*((?<raw>\\d[\\.\\d]*,\\d+)?\\s*(?<normal>\\d[\\.\\d]*,\\d+))?\\s*(?<percent>\\d[\\d\\.]*,\\d+)?%?\\s*(?<cost>\\d[\\d\\.]*,\\d+)?\\s*",
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

//	recaudación conjunta     Formación Profesional.......    %      
	public static Pattern FP_COST = Pattern.compile(
			"\\s*(recaudaci.n\\s*conjunta\\s*)?\\s*Formación\\s*Profesional\\.{2,}\\s*((?<raw>\\d[\\.\\d]*,\\d+)?\\s*(?<normal>\\d[\\.\\d]*,\\d+))?\\s*(?<percent>\\d[\\.\\d,]*,\\d+)?%?\\s*(?<cost>\\d[\\.\\d,]*,\\d+)?\\s*$",
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

}
