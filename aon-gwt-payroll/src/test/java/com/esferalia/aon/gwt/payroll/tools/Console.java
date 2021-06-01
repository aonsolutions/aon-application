package com.esferalia.aon.gwt.payroll.tools;

import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.ARROW;

import java.text.DecimalFormat;

public class Console {

	long start; 
	
	public static enum Status {
		RUN("Run"),
		START("Start"),
		ERROR("Error"),
		INFO("Info"),
		WARNING("Warning"),
		SUCCESS("Success"),
		TEST("Test"),
		WAITING("Waiting"),
		COMPARING("Comparing");
		
		private String name;
		private Status(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
	}
	
	public static enum Separator {
		
		EQUAL("="),
		COLON(","),
		PLUS("+"),
		ARROW("->"),
		ARROW_REVERSE("<-"),
		;
		
		private String separator;
		
		private Separator(String separator) {
			this.separator = separator;
		}
		
		public String getSeparator() {
			return this.separator;
		}
	}
	
	public Console() {
		start = System.nanoTime();
	}
	
	/**
	 * Start a console section
	 * @param title - Title to use in header.
	 */
	public void start(String title) {
		line();
		System.out.println(" #  " + title);		
		line();	
	}
	
	/**
	 * Create a LOG
	 * @param status - The status of the log
	 * @param message - The message
	 */
	public void log(Status status, Object message) {
		System.out.println(format(status.getName(), message + ""));		
	}
	
	/**
	 * Create a LOG
	 * @param status
	 * @param title
	 * @param message
	 */
	public void log(Status status, Object title, Object message) {
		System.out.println(format(status.getName(), title + "", message + "", ARROW));		
	}
	
	/**
	 * Create a LOG
	 * @param status
	 * @param title
	 * @param message
	 */
	public void log(Status status, Object title, Object message, Separator separator) {
		System.out.println(format(status.getName(), title + "", message + "", separator));		
	}
	
	/**
	 * Create an info LOG
	 * @param message - The message
	 */
	public void info(String message) {		
		log(Status.INFO,message);
	}
	
	/**
	 * Create an info LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void info(String title, Object message) {		
		log(Status.INFO,title,message);		
	}
	
	/**
	 * Create an error LOG
	 * @param message - The message
	 */
	public void error(String message) {		
		log(Status.ERROR,message);
	}
	
	/**
	 * Create an error LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void error(String title, Object message) {		
		log(Status.ERROR,title,message);
	}
	
	
	/**
	 * Create an warning LOG
	 * @param message - The message
	 */
	public void warning(String message) {		
		log(Status.WARNING,message);		
	}
	
	/**
	 * Create an warning LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void warning(String title, Object message) {		
		log(Status.WARNING,title,message);
	}
	
	
	/**
	 * Create an success LOG
	 * @param message - The message
	 */
	public void success(String message) {		
		log(Status.SUCCESS,message);
	}
	
	/**
	 * Create an success LOG
	 * @param title - The title
	 * @param message	- The message
	 */
	public void success(String title, Object message) {		
		log(Status.SUCCESS,title,message);	
	}
	
	
	/**
	 * Format message in LOG format
	 * @param category - The category
	 * @param title - The title
	 * @param message - The message
	 * @param separator - The separator
	 * @return String with formatted message
	 */
	private String format(String category,String title, String message, Separator separator) {
		return "[" + category + "] [" + duration() + "] " + title + " " + separator.getSeparator() + " " + message; 
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
	
	
	public void jump() {
		System.out.println();
	}

	public void jump(int i) {
		for (int j = 0; j < i; j++) {
			System.out.println();
		}
	}
	
	public void line() {
		System.out.println("------------------------------------------------------------");		
	}
	
}
