package com.esferalia.aon.in.payroll.pdf.template.tool;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.template.commons.Payment;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PdfParsingTools {
	/**
	 * A method to choose the CRA and add the payment
	 * @param salaryBuilder
	 * @param payment
	 * @param concept
	 * @param context
	 * @param dateFrom
	 * @param dateTo
	 */
	public static void addPayment(ISalaryBuilder<?> salaryBuilder, Double payment, String concept, String context,
			Date dateFrom, Date dateTo) {
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
	
	/**
	 * Month in spanish to a 0-11 range number
	 * @param strMonth
	 * @return 0-11 number depending of the month, -1 if invalid
	 */
	public static int monthChooser(String strMonth) {
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
	
	
	/**
	 * A parser for Aplifisa payroll's header's dates
	 * @param date
	 * @param year
	 * @return the parsed java.util.Date
	 */
	public static Date aplifisaDateParser(String date, int year) {

		final Pattern DATE_FORMAT = Pattern.compile("\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*",
				Pattern.CASE_INSENSITIVE);

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
	/**
	 * Parses the issue date of the Aplifisa payrolls, such as "06 de Junio de 2020"
	 * @param date
	 * @return the parsed java.util.Date
	 */
	public static Date aplifisaFullDateParser(String date) {

		final Pattern DATE_FORMAT_YEAR = Pattern.compile(
				"\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*de\\s*(?<year>\\d+)\\s*", Pattern.CASE_INSENSITIVE);

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
	
	/**
	 * A simple date parser for simple dd-MM-yyyy dates
	 * @param date
	 * @return parsed java.util.Date
	 */
	public static Date commonDateParser(String date) {
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
	
	/**
	 * Parses the doubles with the format of most of the payrolls, e.g. "2.000,09"
	 * @param str
	 * @return the parsed double value
	 */
	public static Double payrollDoubleParser(String str) {
		str = AonStringUtils.trimToNull(str);
		try {
			str = str.replaceAll("\\.", "").replaceAll(",", ".");
			Double ret = Double.parseDouble(str);
			return ret;
		} catch (NullPointerException | NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Removes spaces from a string placed in positons passed as int or int array
	 * @param str
	 * @param pos
	 * @return The string without the chosen spaces
	 */
	public static String removeSpace(String str, int... pos) {
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
	
	
	/**
	 * A parser for A3 payroll period dates
	 * @param date
	 * @return parsed java.util.Date
	 */
	public static Date a3DateParser(final String date){
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
	

}
