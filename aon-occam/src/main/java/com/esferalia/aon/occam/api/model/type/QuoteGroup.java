package com.esferalia.aon.occam.api.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class QuoteGroup {
	
	protected QuoteGroup() {
		super();
	}
	
	private static final Map<String, String> quoteGroupTable;
	
	static {
		Map<String, String> quoteGroupTableMap = new HashMap<>();
		
		quoteGroupTableMap.put("-", "-1");
		quoteGroupTableMap.put("01. Alta direcci\u00F3n y personal no incluido en el E.T.", "01");
		quoteGroupTableMap.put("02. Ingenieros t\u00E9cnicos, peritos y ayudantes titulados", "02");
		quoteGroupTableMap.put("03. Jefes administrativos y de taller", "03");
		quoteGroupTableMap.put("04. Ayudantes no titulados", "04");
		quoteGroupTableMap.put("05. Oficiales administrativos", "05");
		quoteGroupTableMap.put("06. Subalternos", "06");
		quoteGroupTableMap.put("07. Axiliares administrativos", "07");
		quoteGroupTableMap.put("08. Oficiales de primera y segunda", "08");
		quoteGroupTableMap.put("09. Oficiales de tercera y especialista", "09");
		quoteGroupTableMap.put("10. Peones", "10");
		quoteGroupTableMap.put("11. Trabajadores menos de dieciocho a\u00F1os", "11");

		quoteGroupTable = Collections.unmodifiableMap(quoteGroupTableMap);
		
	}

	public static Collection<String> getAllEntriesCollection() {
		Collection<String> entries = new ArrayList<>();
		
		for(Entry<String, String> entry : quoteGroupTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static Map<String, String> getQuoteGroup() {
		return quoteGroupTable;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + quoteGroupTable.get(code);
	}
	
}
