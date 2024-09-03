package com.esferalia.aon.in.payroll.pdf.maker;

import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Separator.STANDARD;

import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {
	private static final java.util.logging.Logger LOGGER =  java.util.logging.Logger.getLogger(Logger.class.getName());
	
	final static int MAX_CHARACTERS = 100;
	final static int MAX_STATUS_CHARACTERS = 8;

	/**
	 * Status list for console log.
	 * @author akrck02
	 *
	 */
	public static enum Status {
		
		/** System status*/
		START(1,"Start"),
		END(0,"End"),
		
		/** Actions */
		TEST(100,"Test"),
		COMPARE(101,"Compare"),
		GENERATE(102,"Generate"),
		GET(103,"Get"),
		
		
		/** Log severety */
		INFO(200,"Info"),
		WARNING(201,"Warning"),
		ERROR(202,"Error"),
		FAIL(203,"Fail"),
		SUCCESS(204,"Success"),
		;
		
		private String name;
		private int code;
		
		Status(int code, String name){
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public int getCode() {
			return this.code;
		}
	}
	
	/**
	 * Separators for console log
	 * @author akrck02
	 */
	public static enum Separator {
		EQUAL("="),
		DOT("."),
		COLUMN(","),
		ARROW("->"),
		REVERSE_ARROW("<-"),
		QUESTION("?"),
		STANDARD(":"),
		;
		
		private String expression;
		
		private Separator(String expression) {
			this.expression = expression;
		}
		
		public String getExpression() {
			return this.expression;
		}
		
	}
	
	
	/**
	 * Log a message on Console
	 * @param status - The log status
	 * @param title - The title of the log
	 * @param message - The message of the log
	 */
	public static void log(Status status,String title, String message) {
		log(status, STANDARD, title, message);
	}
	
	/**
	 * Log a message on Console
	 * @param status - The log status
	 * @param separator - The title / message separator
	 * @param title - The title of the log
	 * @param message - The message of the log
	 */
	public static void log(Status status, Separator separator, String title, String message) {
		String statusName = status.getName().toUpperCase();
		String out = "[" + statusName + "] " + fill(" ", MAX_STATUS_CHARACTERS - statusName.length()) + title + " " + separator.getExpression() + " ";
		out += crop(MAX_CHARACTERS,message);
		LOGGER.finest(out);
	}
	
	/**
	 * Log a message on Console
	 * @param status - The log status
	 * @param separator - The title / message separator
	 * @param title - The title of the log
	 * @param message - The message of the log
	 */
	public static void log(Status status, String message) {
		String statusName = status.getName().toUpperCase();
		String out = "[" + statusName + "] " + fill(" ", MAX_STATUS_CHARACTERS - statusName.length());
		out += crop(MAX_CHARACTERS,message);
		LOGGER.finest(out);
	}
	
	
	private static String crop(int max, String message) {
		if(message == null) return "";
		if(message.length() > max) {
			try {
				message = message.substring(0, max) + "...";
			}catch(Exception e) {}
		}
		return message;
	}
	
	/**
	 * Starts a section on console
	 * @param title
	 */
	public static void start(String title) {
		
		jump();
		line();
		LOGGER.finest("  " + title);
		line();
		
	}
	
	/**
	 * Creates a new line on the Console
	 */
	public static void line() {
		line('-');
	}
	
	/**
	 * Jumps onces on console
	 */
	public static void jump() {
		jump(1);
	}
	
	/**
	 * Jump on console
	 * @param times - Times to jump
	 */
	public static void jump(int times) {
//		for (int j = 0; j < times; j++)
//			LOGGER.finest();
	}
	
	/**
	 * Creates a new line on the Console
	 * @param character - The caracter the line is made of
	 */
	public static void line(char character) {
		String line = "";
		for (int i = 0; i < MAX_CHARACTERS; i++) {
			line += character;
		}	
		LOGGER.finest(line);
	}
	
	public static String fill(String text, int times) {
		String out = "";
		for (int i = 0; i < times; i++) {
			out += text;
		}
		return out;
	}
	
}
