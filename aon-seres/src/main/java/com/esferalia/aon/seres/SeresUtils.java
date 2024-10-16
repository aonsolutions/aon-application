package com.esferalia.aon.seres;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public class SeresUtils {

	public static final String DEFAULT_CHARSET_ENC = StandardCharsets.ISO_8859_1.name();

	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddhhmm");
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static SimpleDateFormat dateTimeFormat(){
		return dateTimeFormat;
	}
	
	public static SimpleDateFormat dateFormat(){
		return dateFormat;
	}
	
	public static boolean isECI(String document) {
		return "A28017895".equalsIgnoreCase(document);
	}
	
	public static boolean isEroski(String document) {
        return "F20033361".equalsIgnoreCase(document)
        	|| "B88512975".equalsIgnoreCase(document)
        	|| "A08115032".equalsIgnoreCase(document)
        	|| "A36651313".equalsIgnoreCase(document);
    }
	
	public static boolean isDia(String document) {
		return "A80782519".equalsIgnoreCase(document);
	}

}
