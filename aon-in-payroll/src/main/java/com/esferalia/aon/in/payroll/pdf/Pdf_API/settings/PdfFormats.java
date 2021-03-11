package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.start_section;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.tb;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.untab;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class PdfFormats {


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
	
	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT (OPTIONAL)
	public static Optional<String> formatDate(Optional<Date> date, String format) {
		if(date.isEmpty()) return Optional.of("");
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date.get()));
		return formattedDate;
	}
	
	//--------------HELP INFO----------------------------------
	public static void help() {
		jump(1);

		start_section("Pdf data format methods:");
		slog("to_latin_number()" 	+ tb(4) + "Converts a double into a latin format decimal String");
		slog("parseDate()" 			+ tb(5) + "Converts String to date");
		slog("formatDate()" 		+ tb(5) + "Converts date to String");

		jump(1);
		untab();
	
	}
}
