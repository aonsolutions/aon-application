package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class PdfFormats {

	public final static DecimalFormat two_digit_decimal = new DecimalFormat("0.00");
	
	
	//FORMAT A DOUBLE
	public static String format_two_digit_decimal(double num) {
		return two_digit_decimal.format(num);
	}
	
	
	//RETURN LATIN VERSION OF A NUMBER WITH . AND , (STRING)
	public static String to_latin_number(Double number){
		if(number == null) return "";
		DecimalFormat formater = new DecimalFormat("###,##0.00");
		return formater.format(number);
	}
	
	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Date formattedDate;

		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e) {
			return null;
		}
	}
	
	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}
}
