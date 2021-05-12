package com.esferalia.aon.selenium.tools;

import java.text.DecimalFormat;

public class Console {

	long start; 
	
	public Console() {
		start = System.nanoTime();
	}
	
	/**
	 * Start a console section
	 * @param title - Title to use in header.
	 */
	public void start(String title) {
		System.out.println("------------------------------------------------------------");		
		System.out.println(" #  " + title);		
		System.out.println("------------------------------------------------------------");		
	}
	
	/**
	 * Create an info LOG
	 * @param message - The message
	 */
	public void info(String message) {		
		System.out.println(format("Info", message + ""));		
	}
	
	/**
	 * Create an info LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void info(String title, Object message) {		
		System.out.println(format("Info", title, message + ""));		
	}
	
	/**
	 * Create an error LOG
	 * @param message - The message
	 */
	public void error(String message) {		
		System.out.println(format("Error", message + ""));		
	}
	
	/**
	 * Create an error LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void error(String title, Object message) {		
		System.out.println(format("Error", title, message + ""));		
	}
	
	
	/**
	 * Create an warning LOG
	 * @param message - The message
	 */
	public void warning(String message) {		
		System.out.println(format("Warning", message + ""));		
	}
	
	/**
	 * Create an warning LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void warning(String title, Object message) {		
		System.out.println(format("Warning", title, message + ""));		
	}
	
	
	/**
	 * Create an success LOG
	 * @param message - The message
	 */
	public void success(String message) {		
		System.out.println(format("Success", message + ""));		
	}
	
	/**
	 * Create an success LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void success(String title, Object message) {		
		System.out.println(format("Success", title, message + ""));		
	}
	
	
	/**
	 * Format message in LOG format
	 * @param category - The category
	 * @param title - The title
	 * @param message - The message
	 * @return String with formatted message
	 */
	private String format(String category,String title, String message) {
		return "[" + category + "] [" + duration() + "] " + title + " >> " + message; 
	}
	
	/**
	 * Format message in LOG format
	 * @param category - The category
	 * @param message - The message
	 * @return String with formatted message
	 */
	private String format(String category,String message) {
		return "[" + category + "] [" + duration() + "] "  + message; 
	}
	
	/**
	 * Current test duration
	 * @return duration
	 */
	public String duration() {
		long end = System.nanoTime();
		double difference = (end - start) / 1e6;
		DecimalFormat formatter = new DecimalFormat("#.##");
		
	    if(difference < 1000) return formatter.format(difference) + " ms";
		else return  formatter.format(difference/1000) + "s";	
	}
	
}
