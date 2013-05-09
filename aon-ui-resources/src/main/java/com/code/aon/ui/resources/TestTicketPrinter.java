package com.code.aon.ui.resources;

import jzebra.Base64;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class TestTicketPrinter {
	
	private static String INITIALIZE_PRINTER = "27,64";
	
	private static String FEET_LINES_COMMAND = "27,100,1";
	
	private static String CUT_COMMAND = "29,86,49"; 
	
	private int width = 48;
	
	private String getInitializePrinter() {
		return INITIALIZE_PRINTER;
	}
	
	private String getFeedLines( int lines ) {
		return StringUtils.repeat(FEET_LINES_COMMAND, ",", lines);
	}
	
	private String getCutTicket() {
		return CUT_COMMAND;
	}
		
	public String getTicketCommands() {
		StringBuffer sb = new StringBuffer();
		append( sb, getInitializePrinter() );

		append( sb, "29,42,18,5" );
		
		append( sb, "AA,AA,AA,AA,AA,55,55,55,55,54,80,00,00,00,02" );
		append( sb, "40,00,00,00,04,80,00,00,00,02,40,00,00,00,04" );
		append( sb, "8A,AA,AA,AA,A2,45,55,55,55,44,8A,AA,AA,AA,A2" );
		append( sb, "45,55,55,55,44,8A,AA,AA,AA,A2,45,00,50,01,44" );
		append( sb, "8A,80,A8,02,A2,45,00,50,01,44,8A,80,A8,02,A2" );
		append( sb, "45,00,50,01,44,8A,80,A8,02,A2,45,00,50,01,44" );
		append( sb, "8A,80,A8,02,A2,45,00,00,01,44,8A,80,00,02,A2" );
		append( sb, "40,00,00,00,04,80,00,00,00,02,40,00,00,00,04" );
		append( sb, "80,AA,00,02,A2,41,55,00,01,44,82,AA,80,02,A2" );
		append( sb, "45,55,40,01,44,8A,AA,A0,02,A2,45,45,50,01,44" );
		append( sb, "8A,82,A8,02,A2,45,01,54,01,44,8A,80,AA,02,A2" );
		append( sb, "45,00,55,01,44,8A,80,2A,82,A2,45,00,15,55,44" );
		append( sb, "8A,80,0A,AA,A2,45,00,05,55,44,8A,80,02,AA,82" );
		append( sb, "40,00,01,55,04,80,00,00,00,02,40,00,00,00,04" );
		append( sb, "80,00,00,00,02,40,15,55,50,04,80,2A,AA,A8,02" );
		append( sb, "40,55,55,54,04,80,AA,AA,AA,02,41,55,55,55,04" );
		append( sb, "82,A8,00,2A,82,45,50,00,15,44,8A,A0,00,0A,A2" );
		append( sb, "45,40,00,05,44,8A,80,00,02,A2,45,00,00,01,44" );
		append( sb, "8A,80,00,02,A2,45,00,00,01,44,8A,80,00,02,A2" );
		append( sb, "45,00,00,01,44,8A,80,00,02,A2,40,00,00,00,04" );
		append( sb, "80,00,00,00,02,40,00,00,00,04,80,00,00,00,62" );
		append( sb, "40,00,00,03,84,80,00,00,1C,02,40,00,00,60,04" );
		append( sb, "80,00,03,80,02,40,00,1C,00,04,80,00,60,00,02" );
		append( sb, "40,03,80,00,04,80,0C,00,00,02,40,70,00,00,04" );
		append( sb, "83,80,00,00,02,4C,00,00,00,04,80,00,00,00,02" );
		append( sb, "40,00,00,00,04,80,00,00,00,02,4A,AA,AA,AA,A4" );
		append( sb, "85,55,55,55,42,4A,AA,AA,AA,A4,85,55,55,55,42" );
		append( sb, "4A,AA,AA,AA,A4,85,00,05,00,02,4A,08,0A,80,04" );
		append( sb, "85,00,05,00,02,4A,80,0A,80,04,85,00,05,00,02" );
		append( sb, "4A,80,0A,80,04,85,00,05,00,02,4A,80,0A,80,04" );
		append( sb, "85,55,55,00,02,42,AA,AA,00,04,81,55,54,00,02" );
		append( sb, "40,AA,A8,00,04,80,55,50,00,02,40,00,00,00,04" );
		append( sb, "80,00,00,00,02,40,00,00,00,04,80,2A,AA,A8,02" );
		append( sb, "40,55,55,54,04,80,AA,AA,AA,02,41,55,55,55,04" );
		append( sb, "82,AA,AA,AA,82,45,40,00,05,44,8A,80,00,02,A2" );
		append( sb, "45,00,00,01,44,8A,80,00,02,A2,45,00,00,01,44" );
		append( sb, "8A,80,00,02,A2,45,00,00,01,44,8A,80,00,02,A2" );
		append( sb, "45,00,00,01,44,8A,80,00,02,A2,45,40,00,05,44" );
		append( sb, "82,AA,AA,AA,82,41,55,55,55,04,80,AA,AA,AA,02" );
		append( sb, "40,55,55,54,04,80,2A,AA,A8,02,40,00,00,00,04" );
		append( sb, "80,00,00,00,02,40,00,00,00,04,80,AA,00,02,A2" );
		append( sb, "41,55,00,01,44,82,AA,80,02,A2,45,55,40,01,44" );
		append( sb, "8A,AA,A0,02,A2,45,45,50,01,44,8A,82,A8,02,A2" );
		append( sb, "45,01,54,01,44,8A,80,AA,02,A2,45,00,55,01,44" );
		append( sb, "8A,80,2A,82,A2,45,00,15,55,44,8A,80,0A,AA,A2" );
		append( sb, "45,00,05,55,44,8A,80,02,AA,82,40,00,01,55,04" );
		append( sb, "80,00,00,00,02,40,00,00,00,04,80,00,00,00,02" );
		append( sb, "40,00,00,00,04,AA,AA,AA,AA,AA,55,55,55,55,54" );		
		append( sb, "27,85,1" );
		
		append( sb, "29,47,0,10" );

		append( sb, getFeedLines(4) );
		append( sb, getCutTicket() );
		append( sb, getInitializePrinter() );
		return sb.toString();
	}
	
	private void append( StringBuffer sb, String commands ) {
		sb.append( getCommands(commands) );
	}
	
	private String getCommands( String commands ) {
		StringBuffer sb = new StringBuffer();
		String _commands = StringUtils.deleteWhitespace(commands);
		String[] list = StringUtils.split(_commands, ",");
		for( String value : list ) {
			if ( NumberUtils.isDigits(value) ) {
				sb.append( (char) NumberUtils.toInt(value) );
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			TestTicketPrinter tp = new TestTicketPrinter();
			String rawCmds = tp.getTicketCommands();
			byte[] bytes = rawCmds.getBytes();
			System.out.println( "->" + Base64.encodeBytes(bytes) + "<-" );
			/*
			String printer = "zebra"; 
			PrintService ps = PrintServiceMatcher.findPrinter(printer);
			if (ps != null) {
				PrintRaw p = new PrintRaw(ps, rawCmds);
				// p.print();
			} else {
				System.err.println("Could not find printer!");
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
