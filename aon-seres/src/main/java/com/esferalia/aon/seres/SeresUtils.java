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

}
