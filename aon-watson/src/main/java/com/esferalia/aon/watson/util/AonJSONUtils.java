package com.esferalia.aon.watson.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AonJSONUtils {
	
	public static String start(){
		return "{";
	}
	
	public static String start(String key){
		return "'"+ key + "':{";
	}
	
	public static String end(){
		return "}";
	}
	
	public static String startArray() {
		return "[";
	}
	
	public static String endArray() {
		return "]";
	}
	
	public static String intToJSON(String key, Integer integer, Boolean last){
		if(integer != null){
			return "'"+ key + "':" + integer 
					+ (!last ? "," : "");
		}
		return "";
	}
	
	public static String dblToJSON(String key, Double doubl, Boolean last){
		if(doubl != null){
			return "'"+ key + "':" + doubl 
					+ (!last ? "," : "");
		}
		return "";
	}
	
	public static String strToJSON(String key, String string, Boolean last){
		if(string != null){
			return "'"+ key + "':'" + string + "'"
					+ (!last ? "," : "");
		}
		return "";
	}
	
	public static String dateToJSON(String key, Date date, Boolean last){
		if(date != null){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return "'"+ key + "':'" + dateFormat.format(date) + "'"
					+ (!last ? "," : "");
		}
		return "";
	}
	
	public static String boolToJSON(String key, Boolean bool, Boolean last){
		if(bool != null){
			return "'"+ key + "':" + bool
					+ (!last ? "," : "");
		}
		return "";
	}
}
