package com.esferalia.aon.occam.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UnemployedStatus {

	protected UnemployedStatus() {
		super();
	}
	
	private static final Map<String,String> unemployedTable;

	static{
		Map<String,String> unemployedMap =new HashMap<>();
		
		unemployedMap.put("","");
		unemployedMap.put("1","Desempleado inscrito en la oficina de empleo");
		unemployedMap.put("2","Desempleado inscrito en la oficina de empleo durante más de 12 meses");
		unemployedMap.put("3","Desempleado subsidio REA");
		unemployedMap.put("4","Beneficiario prestación desempleo -contributiva o asistencial- durante más de un año");
		unemployedMap.put("5","Desempleado inscrito en la oficina de empleo durante más de 6 meses");
		unemployedMap.put("6","Beneficiario prestación desempleo -contributiva o asistencial- al que falta un año o más de percepción de la prestación");
		unemployedMap.put("7","Beneficiario subsidio desempleo. Mayor de 52 años");
		unemployedMap.put("8","Desempleado excedente sector textil falta 1 año de percepción de la prestación / BAJA");
		unemployedMap.put("9","No inscrito en la oficina de empleo");
		unemployedMap.put("T","Desempleado excedente sector textil / BAJA");
		unemployedMap.put("U","Desempleado + 6 meses excedente sector textil / BAJA");
		unemployedMap.put("F","Desempleado inscrito en la oficina de empleo. Carga familiar");
		unemployedMap.put("G","Desempleado inscrito en la oficina de empleo meses. Carga familiar");
		unemployedMap.put("A","Beneficiario Prestación Desempleo");
		unemployedMap.put("M","Desempleado contrato de trabajo CTP <333");
		unemployedMap.put("B","Desempleado con problemas de empleabilidad");
		unemployedMap.put("C","Desempleado 01.01.2011");
		unemployedMap.put("D","Desempleado 01.01.2011 12 meses en 18 meses");
		unemployedMap.put("E","Desempleado 16.08.2011");
		unemployedMap.put("H","Desempleado sin experiencia laboral o inferior a 3 meses");
		unemployedMap.put("I","Desempleado procedente de otro sector de actividad");
		unemployedMap.put("J","Desempleado sin experiencia laboral o inferior a 3 meses. Empleo joven");
		unemployedMap.put("K","Desempleado sin título oficial enseñanza");
		unemployedMap.put("L","Desempleado inscrito 12 meses en 18 meses. RDL 8/2019");
		unemployedMap.put("P","Tarifa Plana");	
		unemployedMap.put("R","Tarifa Reducida");
		unemployedMap.put("V","Transformación contrato eventual S.E. Agrario en indefinido. RDL 8/2019");
		unemployedMap.put("W","Colectivo excluido de inscripción");
		
		
		unemployedTable = Collections.unmodifiableMap(unemployedMap);
	
	}
	
	public static Collection<String> getAllEntriesCollection() {
		Collection<String> entries = new ArrayList<>();
		
		for(Entry<String, String> entry : unemployedTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static Map<String, String> getUnemployedStatus() {
		return unemployedTable;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + unemployedTable.get(code);
	}
}
