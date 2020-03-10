package com.esferalia.aon.altai.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

import com.esferalia.aon.watson.util.AonStringUtils;

public class TSLABEMPParser {
	
	private static final Map<String, String> EMP01_MAP = 
	new HashMap<String,String>(){
		{
			put("EM01-000-T", "em01.000");

			put("EM01-001-T", "name");
			put("EM01-014-T", "document");
			
			put("EM01-002-T", "street_type");
			put("EM01-003-T", "address");
			put("EM01-004-T", "number");
			put("EM01-009-N", "zip");
			put("EM01-010-T", "city");
			put("EM01-011-N", "geozone");			 

			put("EM01-013-T", "ccc");	
			
			put("EM01-079-N", "cnae2009");			 
		}
	};
	
	private static final Map<String, String> EMP10_MAP = 
	new HashMap<String,String>(){
		{
			put("EM10-000-T", "em10.000");
			
			put("EM10-002-T", "description");
			
			put("EM10-003-T", "street_type");
			put("EM10-004-T", "address");
			put("EM10-005-T", "number");
			put("EM10-011-N", "zip");
			put("EM10-012-T", "city");
			put("EM10-013-N", "geozone");
		}
	};

	private static Pattern LINE_SPLITTER = Pattern.compile("\"([^\"]*)\"|([^,]+)|#([^#]*)#");
	
	// ------------------------------------------------------------------------

	public static void parse(Reader i, TSLABEMPHandler handler) throws IOException {
		parse(new LineNumberReader(i), handler);
	}
	
	public static void parse(InputStream in, TSLABEMPHandler handler) throws IOException {
		parse(new InputStreamReader(in), handler);
	}
	
	// ------------------------------------------------------------------------
	
	public static void main(String[] args) throws IOException {
		parse(System.in, new TSLABEMPHandler() {
			
			@Override
			public void emp10(Map<String, String> emp10) {
				// TODO Auto-generated method stub
				System.out.println(emp10);
			}
			
			@Override
			public void emp01(Map<String, String> emp01) {
				// TODO Auto-generated method stub
				System.out.println(emp01);
			}
		});
	}
	
	// ------------------------------------------------------------------------

	private static void parse( LineNumberReader i, TSLABEMPHandler handler) throws IOException{
		
		Map<String, String> emp01 = new HashMap<String, String>();
		Map<String, String> emp10 = new HashMap<String, String>();
		for ( String line = i.readLine(); line != null; line = i.readLine() ) {
			String fields [] = split(line);
			try {
				String aonField = getEMP01Field(fields);
				if ( emp01.containsKey(aonField)) {
					handler.emp01(emp01);
					emp01.clear();
				}
				emp01.put(aonField, fields[3]);
				continue;
			} catch ( NoSuchFieldException e ) {
			}

			try {
				String aonField = getEMP10Field(fields);
				if ( emp10.containsKey(aonField)) {
					handler.emp10(emp10);
					emp10.clear();
				}
				emp10.put(aonField, fields[3]);
				continue;
			} catch ( NoSuchFieldException e ) {
			}
		}

		if ( !emp01.isEmpty() )
			handler.emp01(emp01);
		if ( !emp10.isEmpty() )
			handler.emp10(emp10);
		
	}
	
	public static  String[] split(String line ) {
		List<String>  fields = new ArrayList<String>(4);  
		Matcher lineMatcher = LINE_SPLITTER.matcher(line);
		while ( lineMatcher.find() ) {
			String group0 = 
			lineMatcher.group(0)
			.replaceAll("^[#\"]", "")
			.replaceAll("[#\"]$", "")
			.replaceAll("NULL", "")
			.trim()
			;
			
			fields.add(group0);
		}
		return fields.toArray(String[]::new);
	}
	
	private static  String getEMP01Field(String fields [] ) throws NoSuchFieldException {
		String key = fields[0] + "-" + fields[1] + "-" + fields[2];
		String aonField = EMP01_MAP.get(key);
		if ( aonField != null )
			return aonField;
		throw new NoSuchFieldException(key);
	}

	private static  String getEMP10Field(String fields [] ) throws NoSuchFieldException {
		String key = fields[0] + "-" + fields[1] + "-" + fields[2];
		String aonField = EMP10_MAP.get(key);
		if ( aonField != null )
			return aonField;
		throw new NoSuchFieldException(key);
	}
}
