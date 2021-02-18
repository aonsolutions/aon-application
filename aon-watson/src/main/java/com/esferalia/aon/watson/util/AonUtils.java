package com.esferalia.aon.watson.util;

public class AonUtils {
	
	
	/**
	 * 
	 * @param t
	 * @param def
	 * @return
	 */
	public static <T> T ifnull(T t, T def){ 
		return t != null ? t : def; 
	}

	/**
	 * Compares two objects for equality, where either one or both objects may
	 * be null.
	 * 
	 * ObjectUtils.equals(null, null)                  = true
 	 * ObjectUtils.equals(null, "")                    = false
 	 * ObjectUtils.equals("", null)                    = false
 	 * ObjectUtils.equals("", "")                      = true
 	 * ObjectUtils.equals(Boolean.TRUE, null)          = false
 	 * ObjectUtils.equals(Boolean.TRUE, "true")        = false
 	 * ObjectUtils.equals(Boolean.TRUE, Boolean.TRUE)  = true
 	 * ObjectUtils.equals(Boolean.TRUE, Boolean.FALSE) = false
	 * 
	 * @param obj1
	 *            the first object, may be null
	 * @param obj2
	 *            the second object, may be null
	 * @return true if the values of both objects are the same
	 */
	public static boolean equals(final Object obj1, final Object obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		return obj1.equals(obj2);
	}
	public static boolean notEquals(final Object obj1, final Object obj2) {
		return !equals(obj1,obj2);
	}
	
	public static boolean equals(final Number n1, final Number n2) {
		return AonNumberUtils.equals(n1, n2);
	}
	public static boolean notEquals(final Number n1, final Number n2) {
		return !equals(n1,n2);
	}

	/**
	 * Gets the hash code of an object returning zero when the object is null.
	 * 
	 * ObjectUtils.hashCode(null)   = 0
	 * ObjectUtils.hashCode(obj)    = obj.hashCode()
	 * 
	 * @param obj the object to obtain the hash code of, may be null
	 * @return the hash code of the object, or zero if null
	 */
	public static int hashCode(Object obj){
		return obj == null ? 0 : obj.hashCode();
	}
	
	

}
