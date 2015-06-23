package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

public class MemoryUtils {

	
	public static boolean isNumeric(Character cchar) {
	    switch (cchar) {
	      case '1':
	      case '2':
	      case '3':
	      case '4':
	      case '5':
	      case '6':
	      case '7':
	      case '8':
	      case '9':
	      case '0':
	      case '.':
	      case ',':
	        return true;
	      default:
	        return false;
	    }
	  }
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
}
