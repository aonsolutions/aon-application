package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.util.Optional;

public class OptionalToolkit {

	public static Double  safeDouble  (Optional<Double> o) 	{return o.orElse(0d);}
	public static Integer safeInteger (Optional<Integer> o) {return o.orElse(0);}
	public static String  safeString  (Optional<String> o) 	{return o.orElse("");}
	
	//------------HELP INFO------------
		public static String help() {
			String info = "";
			return info;
		}	
}
