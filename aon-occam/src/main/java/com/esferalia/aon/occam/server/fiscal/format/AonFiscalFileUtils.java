package com.esferalia.aon.occam.server.fiscal.format;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonFiscalFileUtils {
	
	private static char[] SEEK= new char[]{'á','é','í','ó','ú','Á','É','Í','Ó','Ú','º','ª'};
	private static char[] ALTER = new char[]{'a','e','i','o','u','A','E','I','O','U',' ',' '};

	private static final SimpleDateFormat DATE_FORMAT = 
			new SimpleDateFormat("yyyyMMdd");
	
	private static final String AEAT_MARK = "X";

	public static String changeInvalidCharacters(String token) {
		if (AonStringUtils.isNotBlank(token)) {
			for (int i = 0; i < SEEK.length ; i ++) {
				token = AonStringUtils.replaceChars(token, SEEK[i], ALTER[i]);
			}
		}
		return token;
	}

	
	public static String zeros(int size) {
		return AonStringUtils.repeat(AonStringUtils.ZERO, size);
	}
	public static String spaces(int size) {
		return AonStringUtils.repeat(AonStringUtils.SPACE, size);
	}
	public static String text(String text, int size) {
		return AonStringUtils.substring(
			AonStringUtils.rightPad(
			AonStringUtils.upperCase(
			AonStringUtils.trimToEmpty(text)), size), 0,size);
	}
	public static String text(Number number, int size) {
		String text = number==null?null:number.toString();
		return text(text,size);
	}

	public static String date(Date date) {
		return date == null ? AonStringUtils.repeat(' ', 8) : DATE_FORMAT
				.format(date);
	}
	
	public static String dateZero(Date date) {
		return date == null ? AonStringUtils.repeat('0', 8) : DATE_FORMAT
				.format(date);
	}
	
	public static String signedZero(Double value, int size) {
		return signed(value, '0', size);	
	}
	public static String signedZero(Double value, int size, int precision) {
		return signed(value, '0', size,precision);
	}

	public static String signedSpace(Double value, int size) {
		return signed(value, ' ', size);	
	}
	public static String signedSpace(Double value, int size, int precision) {
		return signed(value, ' ', size,precision);
	}
	public static String signed(Double value, char positive, int size) {
		return signed(value, positive, size,2);
	}
	public static String signed(Double value, char positive, int size, int precision) {
		if (value == null) return positive + zeros(size-1);
		return (value < 0 ? 'N' : positive) + unsigned(value,(size - 1),precision);
	}

//	public static String signed(Double value, int size) {
//		if (value == null) return zeros(size);
//		return text( (value < 0 ? 'N' : '0') + unsigned(value,(size - 1)), size);
//	}
//	public static String signed(Double value, int size, int precision) {
//		if (value == null) return zeros(size);
//		return text( (value < 0 ? 'N' : '0') + unsigned(value,(size - 1),precision), size);
//	}
	public static String unsigned(String value, int size, int precision) {
		return unsigned(AonNumberUtils.todouble(value), size, precision);
	}

	public static String unsigned(double value, int size) {
		return unsigned(value, size, 2);		
	}
	
	public static String unsigned(double value, int size, int precision) {
		Double d = Math.abs(value);
		d = AonMathUtils.round(d * Math.pow(10, precision));
		BigDecimal bg = BigDecimal.valueOf(d);
		long lng = bg.longValue();
		String l = Long.toString(lng); 
		return AonStringUtils.leftPad(l, size,'0');
		
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
	
	public static CharSequence mark(double amount) {
		return mark(amount==1);
	}
	public static CharSequence mark(boolean value) {
		return value?AEAT_MARK:AonStringUtils.SPACE;
	}

	public static void main(String[] args) {

		int length = 20;
		System.out.println( AonStringUtils.repeat("*",length) );
		System.out.println(text("AAA", length));
		System.out.println(text("       AAA", length));
		System.out.println(text("AAA   LLL", length));
		System.out.println(text("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", length));
		System.out.println(text("", length));
		System.out.println(text("  s   sd", length));
		System.out.println(spaces(length));
		System.out.println( AonStringUtils.repeat("*",length) );
		
		System.out.println("****");
		System.out.println(year(null));
		System.out.println(year(2));
		System.out.println(year(20));
		System.out.println(year(201));
		System.out.println(year(2015));
		System.out.println(year(20115));
		System.out.println("****");

		length = 13;
		System.out.println( AonStringUtils.repeat("*",length) );
		Double[] values = new Double[]{
				null
				,0.0
				,15.15
				,-15.15
				,15.1583423
				,222342.13
				,922342.13
		};
		for (Double d : values) {
			Double db = d == null ? null : d * (-1);
			System.out.println(signedZero( d, length) + " ----> " + d );	
			System.out.println(signedZero( db,length) + " ----> " + db );
			System.out.println(signedSpace( d, length) + " -S--> " + d );	
			System.out.println(signedSpace( db,length) + " -S--> " + db );
		}
		System.out.println( AonStringUtils.repeat("*",length) );
		
		System.out.println(unsigned( 1, 1, 0) + " ----> " + 1 );
		 
	}
	
	public static String getProvinceName(Integer prov, int size) {
		String name = null;
		if (prov != null) {
			Province p = Province.safeValueOf(prov);
			if (p != null) {
				name = p.getName();
			}
		}
		return text(name,size);
	}
}
