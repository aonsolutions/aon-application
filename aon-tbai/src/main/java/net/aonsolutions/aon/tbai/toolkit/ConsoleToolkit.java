package net.aonsolutions.aon.tbai.toolkit;

import java.util.Collection;

public class ConsoleToolkit {
	
	public static int tabs;
	
	public void start_console() {
		tabs = 0;
	}
	
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
	

}
