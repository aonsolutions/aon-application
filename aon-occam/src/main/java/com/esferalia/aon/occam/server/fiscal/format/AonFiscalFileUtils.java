package com.esferalia.aon.occam.server.fiscal.format;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonFiscalFileUtils {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	public static String spaces(int size) {
		return AonStringUtils.repeat(AonStringUtils.SPACE, size);
	}
	public static String text(String text, int size) {
		return AonStringUtils.substring(
			AonStringUtils.rightPad(
			AonStringUtils.upperCase(
			AonStringUtils.trimToEmpty(text)), size), 0,size);
	}

	public static String date(Date date) {
		return date == null ? AonStringUtils.repeat(' ', 8) : DATE_FORMAT
				.format(date);
	}

	public static String signed(Double value, int size) {
		if (value == null) value = 0.0;
		return text(
				(value < 0 ? 'N' : ' ')
				 + AonStringUtils.leftPad(
						Long.toString(
								BigDecimal.valueOf( 
										AonMathUtils.round(Math.abs(value) * 100)
										).longValue()) , size,
						'0'),size);
	}

	public static String unsigned(Double value, int size) {
		if (value == null)
			value = 0.0;
		int val = (int) AonMathUtils.round(value * 100);
		return AonStringUtils.leftPad(Integer.toString(val), size, '0');
	}

	public static String signed(Integer value, int size) {
		if (value == null)
			value = 0;
		int val = value;
		return text( 
			(value < 0 ? 'N' : ' ')+ AonStringUtils.leftPad(Integer.toString(Math.abs(val)), size,'0')
				,size);
	}

	public static String unsigned(Integer value, int size) {
		if (value == null)
			value = 0;
		return text( AonStringUtils.leftPad(Integer.toString(value), size, '0'), size);
	}

	public static String document(String document) {
		return AonStringUtils.leftPad(document, 9, '0');
	}

	public static String fullName(String name, String surname, int length) {
		String n = null;
		if ( AonStringUtils.isBlank(surname) && AonStringUtils.isBlank(name)) {
			n = AonStringUtils.EMPTY;
		} else if ( AonStringUtils.isBlank(surname) ) {
			n = name;
		} else if ( AonStringUtils.isBlank(name) ) {
			n = surname;
		} else {
			n = surname + AonStringUtils.COMMA + AonStringUtils.SPACE + name; 
		}
		return text(n,length);
	}
	public static String year(Integer year) {
		return unsigned(year, 4);
	}
	
	public static void main(String[] args) {

		System.out.println("X------------------X");
		System.out.println(text("AAA", 20) + "*");
		System.out.println(text("       AAA", 20) + "*");
		System.out.println(text("AAA   LLL", 20) + "*");
		System.out.println(text("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 20) + "*");
		System.out.println(text(null, 20) + "*");
		System.out.println(text("", 20) + "*");
		System.out.println(text("  s   sd", 20) + "*");
		System.out.println(spaces(20) + "*");
		System.out.println("X------------------X");
		
		System.out.println("X--X");
		System.out.println(year(null) + "*");
		System.out.println(year(2) + "*");
		System.out.println(year(20) + "*");
		System.out.println(year(201) + "*");
		System.out.println(year(2015) + "*");
		System.out.println(year(20115) + "*");
		System.out.println("X--X");

		System.out.println("X--------X");
		System.out.println(signed((Double)null, 10));		
		System.out.println(signed(15.15, 10));
		System.out.println(signed(15.1523423, 10));
		System.out.println(signed(22342342.13, 10));
		System.out.println(signed(922342342.13, 10));
		System.out.println(signed(15.1523423, 10));
		System.out.println("X--------X");
		 
	}
}
