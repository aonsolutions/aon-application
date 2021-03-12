package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.util.Optional;


/**
 * <p><b>Description:</b> <i>This toolkit contains the basic methods for safe data getting</i></p>
 * @author akrck02
 */
public class OptionalToolkit {

	/**
	 * <p><b>Description:</b> <i>Gets a double or 0.00 for an Optional.</i></p>
	 * @return Double or 0.00
	 */
	public static Double  safeDouble  (Optional<Double> optional) 	{return optional.orElse(0d);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an integer or 0 for an Optional.</i></p>
	 * @return Integer or 0
	 */
	public static Integer safeInteger (Optional<Integer> optional) {return optional.orElse(0);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or "" for an Optional.</i></p>
	 * @return String or ""
	 */
	public static String  safeString  (Optional<String> optional) 	{return optional.orElse("");}
	
}
