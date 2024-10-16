package net.aonsolutions.aon.bank.nordigen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.charset.StandardCharsets;



public class NordigenLog {
	
	public static void writeLog(Exception e) {
		String tomcatHome = System.getProperty("catalina.home");
	    String nordigenLogFolder = "/nordigen-logs/";
	        String directoryPath = tomcatHome + nordigenLogFolder;
	        Date date = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	        String formattedDate = sdf.format(date);
	        String filePath = tomcatHome + nordigenLogFolder + formattedDate + "-nordigen-log.log";
	        Path directory = Paths.get(directoryPath);
	        Path file = Paths.get(filePath);
	  

	        try {
	            if (!Files.exists(directory)) Files.createDirectories(directory);
	            if (!Files.exists(file)) Files.createFile(file);
	            StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        
		        // Print the stack trace to the StringWriter
		        e.printStackTrace(pw);
		        
		        // Get the stack trace as a string
		        String stackTrace = sw.toString();
	            String content ="\n" + "Date :"+ formattedDate + "\n" + "Message : " + e.getMessage() + "\n" + "Cause : " + e.getCause() + "\n" + "Class : "  + e.getClass()+ "\n" + "Stacktrace : "  + stackTrace + "\n";
	            Files.write(file, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
	        } catch (IOException e2) {
	            e.printStackTrace();
	        }
	}
	
	
	public static void visualizeLog() {
		
	}
	
	public static void deleteLog() {
		
	}
}
