package com.esferalia.aon.altai.parser;

import static com.esferalia.aon.altai.parser.TSLABEMPParser.split;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esferalia.aon.watson.util.AonStringUtils;

public class TSLABTRABParser {

	private static final Map<String, String> TRA_MAP = 
	new HashMap<String,String>(){
		{
			put("TR01-000-T", "em01.000");

			put("TR01-011-T", "document");
			put("TR01-003-T", "fullname");
			put("TR01-017-T", "ss"); 
			put("TR01-004-T", "street_type"); 
			put("TR01-005-T", "address"); 
			put("TR01-006-T", "number"); 
			put("TR01-007-T", "zip"); 
			put("TR01-013-T", "province"); 
			put("TR01-012-T", "city"); 

			//put("TR01-025-N", "tc2"); 
			put("TR02-003-N", "tc2"); 
			put("TR01-019-T", "category"); 
			put("TR01-020-T", "categoryy"); 

			put("TR01-114-T", "cno"); 

			put("TR02-014-F", "birthdate"); 	//aaaa-mm-dd
			put("TR02-015-F", "startdate"); 	//aaaa-mm-dd
			put("TR02-016-F", "senioritydate"); //aaaa-mm-dd
			put("TR02-017-F", "enddate"); 		//aaaa-mm-dd

			put("TR02-012-N", "quote_group"); 
		}
	};
	

	// ------------------------------------------------------------------------

	public static void parse(Reader i, TSLABTRAHandler handler) throws IOException {
		parse(new LineNumberReader(i), handler);
	}
	
	public static void parse(InputStream in, TSLABTRAHandler handler) throws IOException {
		parse(new InputStreamReader(in, "ISO-8859-1"), handler);
	}
	

	private static void parse( LineNumberReader i, TSLABTRAHandler handler) throws IOException{
		
		
		Map<String, String> tra01 = new HashMap<String, String>();
		for ( String line = i.readLine(); line != null; line = i.readLine() ) {
			String fields [] = split(line);
			try {
				String aonField = getTRA01Field(fields);
				if ( tra01.containsKey(aonField)) {
					handler.tra(tra01, Collections.emptyMap());
					tra01.clear();
				}
				tra01.put(aonField, fields[3]);
				continue;
			} catch ( NoSuchFieldException e ) {
			}

		}

		if ( !tra01.isEmpty() )
			handler.tra(tra01, Collections.emptyMap());
	}

	private static  String getTRA01Field(String fields [] ) throws NoSuchFieldException {
		if ( fields.length < 3 )
			throw new NoSuchFieldException("");
		
		String key = fields[0] + "-" + fields[1] + "-" + fields[2];
		String aonField = TRA_MAP.get(key);
		if ( aonField != null )
			return aonField;
		throw new NoSuchFieldException(key);
	}

	private static String getField(String fields [] ) throws NoSuchFieldException {
		if ( fields.length < 3 )
			throw new NoSuchFieldException("");
		
		return fields[0] + "-" + fields[1] + "-" + fields[2];
	}

	
	private static  Double getAmount(String fields [] ) throws NoSuchFieldException {
		if ( fields.length < 4 )
			throw new NoSuchFieldException("");
		
		if ( !AonStringUtils.equals(fields[2], "N") )
			throw new NoSuchFieldException("");
			
		if ( !fields[3].matches("[0-9]+\\.[0-9]+"))
			throw new NoSuchFieldException("");
		
		try {
			
			Double amount = Double.parseDouble(fields[3]);
			if ( amount != 0.00 )
				return amount;
		} catch ( Exception e ) {
			
		}
		
		throw new NoSuchFieldException("");
	}
	
	private static int rows = 0; 

	private static void insight(Map<String, Double> amountsMap) {
		
		if ( !amountsMap.containsKey("TR03-004-N"))
			return;

		List<Double> amounts = 
		amountsMap.values().stream()
		.distinct()
		.sorted((d1,d2) -> (int)(d2 - d1))
		.collect(Collectors.toList());
		
		
		System.out.println(amountsMap);
		
		if ( rows++ > 100 )
			throw new RuntimeException();
	}

	// ------------------------------------------------------------------------
	
	public static void main(String[] args) throws IOException, ParseException {
		
		FileInputStream is = new FileInputStream(args[0]);
		parse( is , new TSLABTRAHandler() {
			
			@Override
			public void tra(Map<String, String> tra, Map<String, Double> amounts) {
//				tra01.entrySet().forEach(e -> System.out.printf("%s=%s, ", e.getKey(), e.getValue()));
//				System.out.println();
				insight(amounts);
			}
			
		});
		is.close();
	}
}
