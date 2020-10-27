package com.esferalia.aon.in.payroll.tgss.fie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class FieParser {

	public static class UnknownFileException extends IOException {

		public UnknownFileException() {
			super();
		}

		public UnknownFileException(String message, Throwable cause) {
			super(message, cause);
		}

		public UnknownFileException(String message) {
			super(message);
		}

		public UnknownFileException(Throwable cause) {
			super(cause);
		}

	}

	public static interface Listener {
		
	}
	
	public static void parse(File file, Listener listener) throws FileNotFoundException, IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			parse(is, listener);
		}
	}

	public static void parse(InputStream is , Listener listener) throws IOException {	
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))){
			
			
			// ETI ETIquetas de proceso
			String eti = find(reader, "ETI");
			
			do {
			
				// EMP Identificación de EMPpresa
				String emp = find(reader, "EMP");
				String regime = emp.substring(3, 7);
				String ccc = emp.substring(7, 18);
				System.out.println(regime + "/" + ccc);
				
				// RZS RaZón Social
				String rzs = find(reader, "RZS");
				String enterpriseName = rzs.substring(5,60);
				System.out.println(enterpriseName);

				// TRA TRAbajador
				String tra = find(reader, "TRA");
				String naf = tra.substring(3, 15);
				String ipf = tra.substring(19,33);
				System.out.println( naf +", " + ipf);
				
				// AYN Apellidos Y Nombre
				String ayn = find(reader, "AYN");
				String firstSurname = ayn.substring(3,23);
				String secondSurname = ayn.substring(23,43);
				String name = ayn.substring(43,58);
				System.out.println( name + " " + firstSurname + "  " + secondSurname );
				
				
				String dit = find(reader, "DIT");
				
			} while ( false );
			
			

			// ETF ETiquetas de proceso
			String etf = find(reader, "ETF");
		}
	}
	
	private static String find( BufferedReader reader, String head ) throws IOException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			if ( line.startsWith(head) ) {
				return line;
			}
			
		}
		
		throw new UnknownFileException(String.format("Segment: '%s' Not found" ,  head));
				
	}	
	
	private static Optional<String> attemp( BufferedReader reader, String head ) throws IOException {
		reader.mark(1024);
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			if ( line.startsWith(head) ) {
				return Optional.of(line);
			}
			
		}
		
		reader.reset();
		return Optional.empty();
				
	}	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		parse(new File(args[0]), new Listener() {
		});
	}
	

}
