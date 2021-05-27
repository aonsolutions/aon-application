package net.aonsolutions.aon.tbai.toolkit;

import java.util.Collection;
import java.util.Date;

public class ConsoleToolkit {
	
	public static int tabs;
	
	public static void start_console() {tabs = 0;}
	public static void start_console(int t) {tabs = t;}
	
	private static String tabulate(String str) {
		for (int i = 0; i < tabs; i++) str = "  " + str;
		return str;
	}
	
	public static void log(Object name, Object o) {
				
		String value = "empty";
		if(o != null && !o.toString().trim().equals("")) 
			value = o.toString();
		
		System.out.println(tabulate(name + ": \t " + value));
	}
	
	public static void log(Object name, Object o,Date d1) {
		Date d2 = new Date();
		double seconds = (d2.getTime()-d1.getTime())/1000.00;
		
		String value = "empty";
		if(o != null && !o.toString().trim().equals("")) 
			value = o.toString();
		
		System.out.println(tabulate(seconds + "s " + name + ": " + value));
		
	}
	
	public static void log_many(Object name, Collection<Object> o) {
		int i = 0;
		for (Object object : o) {
			log(i, object);
			i ++; 
		}
	}

	public static void start_section(String title) {
		System.out.println(tabulate(title + ": {"));
		tab();
	}

	public static void end_section() {
		untab();
		System.out.println(tabulate("}"));		
	}
	
	public static void tab() {
		tabs++;
	}
	public static void untab() {
		tabs--;
	}
	
	public static void test_title(String title) {
		title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
		
		System.out.println("\n--------------------------------------------------------------------------------");
		System.out.println("   " + title + " test ");
		System.out.println("--------------------------------------------------------------------------------");
	}
	
	public static void log_info(String message) 	{System.out.println("   [Info] " + message);};
	public static void log_warning(String message) {System.out.println("   [Warning] " + message);};
	public static void log_error(String message) 	{System.err.println("   [Error] " + message);};
	
	


}
