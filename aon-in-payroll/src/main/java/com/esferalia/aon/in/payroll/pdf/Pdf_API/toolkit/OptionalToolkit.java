package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.util.Optional;

public class OptionalToolkit {

	public static Double getDouble(Optional<Double> o) {return o.orElse(0d);}
	public static Integer getInteger(Optional<Integer> o) {return o.orElse(0);}
	public static String getString(Optional<String> o) {return o.orElse("");}
	
}
