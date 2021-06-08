package com.esferalia.aon.gwt.payroll.tools;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.parseDate;
import static org.junit.Assert.assertEquals;

import java.util.Date;

public class TestTools {

	public static Integer ERROR_INT 	= 	-314159265;
	public static Double  ERROR_DOUBLE 	= 	(1/99d);
	public static String  ERROR_STRING	= 	"=¿H4n4b1?="; 
	public static Date 	  ERROR_DATE	= 	parseDate("10-10-1970", "dd-MM-yyyy");
	
	public static <T extends Object> void assertWithLog(String title, String message,T object, T object2) {
		assertEquals("[ " + title + " ] " + message + " || Values [ " + object + " >> " + object2 + " ]", object , object2);
	}
	
	
	
}
