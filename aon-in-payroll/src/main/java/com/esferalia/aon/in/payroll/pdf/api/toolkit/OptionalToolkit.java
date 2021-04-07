package com.esferalia.aon.in.payroll.pdf.api.toolkit;

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
	 * <p><b>Description:</b> <i>Gets a double or 0.00.</i></p>
	 * @return Double or 0.00
	 */
	public static Double  safeDouble  (Double optional) 	{return (double) safeValue(optional, 0.00);}
	
	/**
	 * <p><b>Description:</b> <i>Gets a double or default value.</i></p>
	 * @return Double or 0.00
	 */
	public static Double  safeDouble  (Double optional, double def) 	{return (double) safeValue(optional, def);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an integer or 0 for an Optional.</i></p>
	 * @return Integer or 0
	 */
	public static Integer safeInteger (Optional<Integer> optional) {return optional.orElse(0);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an integer or 0.</i></p>
	 * @return Integer or 0
	 */
	public static Integer safeInteger (Integer optional) {return (int) safeValue(optional, 0);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an integer or default value.</i></p>
	 * @return Integer or 0
	 */
	public static Integer safeInteger (Integer optional, int def) {return (int) safeValue(optional, def);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or "" for an Optional.</i></p>
	 * @return String or ""
	 */
	public static String  safeString  (Optional<String> optional) 	{return optional.orElse("");}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or "".</i></p>
	 * @return String or ""
	 */
	public static String  safeString  (String optional) 	{return (String) safeValue(optional, "");}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or default value.</i></p>
	 * @return String or ""
	 */
	public static String  safeString  (String optional, String def) 	{return (String) safeValue(optional, def);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or "" for an Optional.</i></p>
	 * @return String or ""
	 */
	public static float  safeFloat  (Optional<Float> optional) 	{return optional.orElse(0f);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or "".</i></p>
	 * @return String or ""
	 */
	public static float  safeFloat  (Float optional) 	{return (float) safeValue(optional, 0);}
	
	/**
	 * <p><b>Description:</b> <i>Gets an String or default value.</i></p>
	 * @return String or ""
	 */
	public static float  safeFloat (Float optional, float def) 	{return (float) safeValue(optional, def);}
	
	
	/**
	 * <p><b>Description:</b> <i>Gets an object or default value for non optional variables.</i></p>
	 * @return String or ""
	 */
	public static <T> Object safeValue(T obj, T def) {
		Optional<T> opt = Optional.ofNullable(obj);
		return opt.orElse(def);		
	}
		
}
