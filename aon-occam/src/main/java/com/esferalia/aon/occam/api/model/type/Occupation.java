package com.esferalia.aon.occam.api.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Occupation {
	
	protected Occupation() {
		super();
	}
	
	private static final Map<String, String> occupationTable;
	
	static {
		Map<String, String> occupationTableMap = new HashMap<>();
		
		occupationTableMap.put("-", "-1");
		occupationTableMap.put("a. Personal en trabajos exclusivos de oficina", "a");
		occupationTableMap.put("b. Tipo de cotizaci\u00F3n para todos los trabajadores que deban desplazarse habitalmente", "b");
		occupationTableMap.put("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci\u00F3n en general", "d");
		occupationTableMap.put("e. Conductores de veh\u00EDculo autom\u00F3vil de transporte de pasajeros en general (taxis, autom\u00F3viles, autobuses, etc)", "e");
		occupationTableMap.put("f. Conductores de veh\u00EDculo autom\u00F3vil de transporte de mercanc\u00EDas que tengan una capacidad de carga \u00FAtil superior a 3,5 Tm.", "f");
		occupationTableMap.put("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles", "g");
		occupationTableMap.put("h. Vigilantes, guardas, guardas jurados y personal de seguridad", "h");

		occupationTable = Collections.unmodifiableMap(occupationTableMap);
		
	}

	public static Collection<String> getAllEntriesCollection() {
		Collection<String> entries = new ArrayList<>();
		
		for(Entry<String, String> entry : occupationTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static Map<String, String> getOccupation() {
		return occupationTable;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + occupationTable.get(code);
	}
	
}
