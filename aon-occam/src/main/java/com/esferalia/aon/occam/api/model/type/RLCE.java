package com.esferalia.aon.occam.api.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RLCE {
	
	protected RLCE() {
		super();
	}
	
	private static final Map<String, String> rlceTable;
	
	static {
		Map<String, String> rlceTableMap = new HashMap<>();
		
		rlceTableMap.put("", "");
		rlceTableMap.put("0100", "Personal de Alta Direcci\u00F3n");
		rlceTableMap.put("0301", "Penados en Instituciones Penitenciarias. Aprendizaje/Formaci\u00F3n");
		rlceTableMap.put("0302", "Penados en Instituciones Penitenciarias. Actividad Laboral");
		rlceTableMap.put("0303", "Penados en Instituciones Penitenciarias. Beneficio Comunidad");
		rlceTableMap.put("0304", "Penados en Instituciones Penitenciarias. Menores");
		rlceTableMap.put("0409", "Deportistas profesionales");
		rlceTableMap.put("0500", "Representantes de comercio");
		rlceTableMap.put("0501", "Representantes de comercio -vendedores del cup\u00F3n de la ONCE-");
		rlceTableMap.put("0600", "Minusv\u00E1lidos en Centros Especiales de Empleo");
		rlceTableMap.put("0601", "Minusv\u00E1lido procedente enclave laboral");
		rlceTableMap.put("0602", "Discapacitado Organizaci\u00F3n Nacional de Ciegos");
		rlceTableMap.put("0700", "Estibadores portuarios");
		rlceTableMap.put("0800", "Artistas en espect\u00E1culos p\u00FAblicos");
		rlceTableMap.put("0900", "Abogados en despachos de abogados");
		rlceTableMap.put("9901", "Investigadores del Sistema Espa\u00F1ol de Ciencia y Tecnolog\u00EDa -contratos pr\u00E1cticas");
		rlceTableMap.put("9902", "M\u00E9dicos Interinos Residentes");
		rlceTableMap.put("9903", "Universidad P\u00FAblica -Profesor Ayudante");
		rlceTableMap.put("9904", "Universidad P\u00FAblica -Profesor Ayudante Doctor");
		rlceTableMap.put("9905", "Universidad P\u00FAblica -Profesor Colaborador");
		rlceTableMap.put("9906", "Universidad P\u00FAblica -Profesor Colaborador Doctor");
		rlceTableMap.put("9907", "Universidad P\u00FAblica -Profesor Asociado");
		rlceTableMap.put("9908", "Universidad P\u00FAblica -Profesor Visitante");
		rlceTableMap.put("9909", "Personal investigador en formaci\u00F3n - Beca");
		rlceTableMap.put("9910", "Personal investigador en formaci\u00F3n - Contrato pr\u00E1cticas");
		rlceTableMap.put("9911", "Alumnos - Trabajadores en programas de escuela taller");
		rlceTableMap.put("9912", "Alumnos - Trabajadores en programas de casas de oficios");
		rlceTableMap.put("9913", "Alumnos - Trabajadores en programas de talleres de empleo");
		rlceTableMap.put("9914", "Pensionista de Incapacidad Seguridad Social.");
		rlceTableMap.put("9915", "Pensionista de Incapacidad Clases Pasivas.");
		rlceTableMap.put("9916", "Personal investigador I+D+I");
		rlceTableMap.put("9917", "Agentes auxiliares Uni\u00F3n Europea");
		rlceTableMap.put("9918", "Contratos de Formaci\u00F3n - Prorroga posterior 18-06-2010");
		rlceTableMap.put("9920", "Contrato de formaci\u00F3n no bonificados. posterior 18.06.2011");
		rlceTableMap.put("9921", "Contrato predoctoral. Incentivado");
		rlceTableMap.put("9922", "Participantes en programas para la formaci\u00F3n");
		rlceTableMap.put("9923", "Pr\u00E1cticas no laborales en empresas");
		rlceTableMap.put("9924", "Socio Sociedad Laboral");
		rlceTableMap.put("9925", "No socio Sociedad Laboral");
		rlceTableMap.put("9926", "Alumno participante en Proyecto de Empleo-Formaci\u00F3n");
		rlceTableMap.put("9927", "Pr\u00E1cticas Acad\u00E9micas Externas");
		rlceTableMap.put("9928", "Pr\u00E1cticas curriculares externas Real Decreto-Ley 8/2014");
		rlceTableMap.put("9929", "Contrato predoctoral. No incentivado.");
		
		rlceTableMap.put("9935", "Contrato formaci\u00F3n en alternancia. Certificaci\u00F3n profesional nivel 3.");
		rlceTableMap.put("9936", "Contrato formaci\u00F3n en alternancia. Estudios universitarios/FP.");
		rlceTableMap.put("9937", "Contrato acceso personal Investigador Doctor.");
		rlceTableMap.put("9938", "Personal investigador I+D+i. No bonificado.");
		rlceTableMap.put("9939", "Pr\u00E1cticas Form. Remuneradas (D.A. 52 LGSS).");
		rlceTableMap.put("9999", "Declaraci\u00F3n Responsable inexistencia de RLCE.");
		
		rlceTable = Collections.unmodifiableMap(rlceTableMap);
		
	}

	public static Collection<String> getAllEntriesCollection() {
		Collection<String> entries = new ArrayList<>();
		
		for(Entry<String, String> entry : rlceTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static Map<String, String> getRLCE() {
		return rlceTable;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + rlceTable.get(code);
	}
	
}
