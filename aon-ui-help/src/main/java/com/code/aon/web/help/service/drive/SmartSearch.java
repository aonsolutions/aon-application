package com.code.aon.web.help.service.drive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class SmartSearch {

	final static HashMap<String, String> DEPARTURES = new HashMap<String, String>();
	final static String SEPARATOR = "-";
	final static String DEPARTURE_SEPARATOR = "\\.";
	static {
		
		DEPARTURES.put("LAB", "LABORAL");
		DEPARTURES.put("PROC", "PROCESOS");
		
		DEPARTURES.put("CONT", "CONTABILIDAD");
		DEPARTURES.put("REGMERC", "REGISTRO MERCANTIL");
	
	}
	
	/**
	 * Get the departures of a given name using the AON help file naming convention
	 * @param path The path to examine
	 * @return a list of aon modules
	 */
	public static List<String> getAonModules(String path) {
		
		List<String> departures = new ArrayList<String>();
		if(path.indexOf(SEPARATOR) == -1) {
			return departures;
		}
		
		String[] parts = path.split(SEPARATOR);
		if(parts == null || parts.length < 2) {
			return departures;
		}
		
		
		String[] items = parts[0].split(DEPARTURE_SEPARATOR);
		if(items == null) {
			return departures;
		}
			
		departures.addAll(Arrays.asList(items));
		return departures;
		
	}
	
	/**
	 * Get a name from the AON help file naming convention
	 * @param path The path to use
	 * @return the name of the file
	 */
	public static List<String> getAonFileName(String path) {
		List<String> departures = new ArrayList<String>();
		if(path.indexOf(SEPARATOR) == -1) {
			return departures;
		}
		
		String[] parts = path.split(SEPARATOR);
		if(parts == null || parts.length < 2) {
			return departures;
		}
		
		return Arrays.asList(parts);	
	}
	

}
