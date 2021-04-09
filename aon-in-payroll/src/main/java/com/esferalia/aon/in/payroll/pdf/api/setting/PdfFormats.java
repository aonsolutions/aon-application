package com.esferalia.aon.in.payroll.pdf.api.setting;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class PdfFormats {

	/**
	 * <p>
	 * <b>Description:</b> <i>Convert double to Latin number. </i>
	 * </p>
	 * 
	 * @return the latin number String [Format: ###,##0.00]
	 */
	public static String toLatinNumber(Double number) {
		if (number == null)
			return "";
		DecimalFormat formater = new DecimalFormat("###,##0.00");
		return formater.format(number);
	}
	
	/**
	 * <p>
	 * <b>Description:</b> <i>Convert double to Decimal number. </i>
	 * </p>
	 * 
	 * @return the latin number String [Format: ###,##0.00]
	 */
	public static String toDecimal(Double number) {
		if (number == null)
			return "";
		DecimalFormat formater = new DecimalFormat("0.00");
		return formater.format(number);
	}
	

	/**
	 * <p>
	 * <b>Description:</b> <i>Parse a date with specific format. </i>
	 * </p>
	 * 
	 * @return a date
	 */
	public static Date parseDate(String date_string, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Date formattedDate;

		try {
			formattedDate = dateFormatter.parse(date_string);
			return formattedDate;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Format a date with specific format. </i>
	 * </p>
	 * 
	 * @return formatted date String (Optional)
	 */
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Format a date with specific format. </i>
	 * </p>
	 * 
	 * @param date (Optional)
	 * @return formatted date String (Optional)
	 */
	public static Optional<String> formatDate(Optional<Date> date, String format) {
		if (date.isEmpty())
			return Optional.of("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date.get()));
		return formattedDate;
	}
}
