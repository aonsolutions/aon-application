package com.esferalia.aon.gwt.payroll.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TRL {
	
	private static final Map<String, String> trlTable;
	static {
		Map<String, String> trlTableMap = new HashMap<String, String>();
		 
		trlTableMap.put("000", "Tipo relaci" + String.valueOf("\u00F3") + "n com" + String.valueOf("\u00FA") + "n");
		trlTableMap.put("064", "Contrato a tiempo parcial reducido 087 Contrato de aprendizaje");
		trlTableMap.put("300", "Asistencia Sanitaria Concertada");
		trlTableMap.put("301", "Asistencia Sanitaria Concertada menos farmacia");
		trlTableMap.put("302", "Asistencia Sanitaria Medicina General, Pediatr" + String.valueOf("\u00ED") + "a, Puericultura, Servicio de Urgencia Ambulatoria");
		trlTableMap.put("303", "Asistencia Sanitaria Servicio de Urgencia");
		trlTableMap.put("304", "Asistencia Sanitaria Ambulatoria");
		trlTableMap.put("305", "Asistencia Sanitaria Concertada con AT y EP, menos farmacia");
		trlTableMap.put("306", "Asistencia Sanitaria Emigrantes");
		trlTableMap.put("307", "Asistencia Sanitaria Concertada m" + String.valueOf("\u00E1") + "s AT y EP");
		trlTableMap.put("308", "Asistencia Sanitaria Pensionistas clases pasivas");
		trlTableMap.put("309", "Asistencia Sanitaria Concertada MUNPAL");
		trlTableMap.put("751", "Prestaci" + String.valueOf("\u00F3") + "n Desempleo. Extinci" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("752", "Prestaci" + String.valueOf("\u00F3") + "n Desempleo. Suspensi" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("753", "Subsidio desempleo >52/55 a" + String.valueOf("\u00F1") + "os o fijos discontinuos Extinci" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("754", "Subsidio desempleo >52/55 a" + String.valueOf("\u00F1") + "os o fijos discontinuos. Suspensi" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("755", "Subsidio desempleo. Extinci" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("756", "Subsidio desempleo. Suspensi" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("900", "Funcionarios Interinos Administraci" + String.valueOf("\u00F3") + "n Central de Estado");
		trlTableMap.put("901", "Funcionarios y Personal Estatutario");
		trlTableMap.put("902", "Funcionarios Interinos");
		trlTableMap.put("903", "Altos cargos, alcaldes. Excluidos de FOGASA");
		trlTableMap.put("904", "Altos cargos, alcaldes. Excluidos de FOGASA y desempleo");
		trlTableMap.put("905", "Trabajadores en Colaboraci" + String.valueOf("\u00F3") + "n Social");
		trlTableMap.put("906", "Alumnos Escuelas Formaci" + String.valueOf("\u00F3") + "n Profesional");
		trlTableMap.put("907", "Trabajadores extranjeros de pa" + String.valueOf("\u00ED") + "ses sin convenio bilateral");
		trlTableMap.put("909", "Reclusos que realizan trabajos de aprendizaje o formaci" + String.valueOf("\u00F3") + "n profesional");
		trlTableMap.put("910", "Funcionario procedente de MUNPAL - no SNS -");
		trlTableMap.put("911", "Interino procedente de MUNPAL - no SNS -");
		trlTableMap.put("912", "Funcionario Interino R" + String.valueOf("\u00E9") + "gimen General situaciones especiales, cumplimiento del servicio militar o prestaci" + String.valueOf("\u00F3") + "n del servicio social");
		trlTableMap.put("914", "Agentes Auxiliares Uni" + String.valueOf("\u00F3") + "n Europea, Funcionarios espa" + String.valueOf("\u00F1") + "oles excluidos de desempleo");
		trlTableMap.put("915", "Parlamentarios de Comunidades Aut" + String.valueOf("\u00F3") + "nomas procedentes de R" + String.valueOf("\u00E9") + "gimen Especial de Trabajadores Aut" + String.valueOf("\u00F3") + "nomos");
		trlTableMap.put("916", "Parlamentarios Cortes espa" + String.valueOf("\u00F1") + "olas, europeas y comunidades aut" + String.valueOf("\u00F3") + "nomas");
		trlTableMap.put("917", "Ministerio de Defensa. Patronato Militar");
		trlTableMap.put("918", "Ministerio de Interior. Patronato Militar-Mexc");
		trlTableMap.put("919", "Mutualidad Previsi" + String.valueOf("\u00F3") + "n Sanitaria Nacional");
		trlTableMap.put("920", "Funcionarios procedentes de la extinguida Administraci" + String.valueOf("\u00F3") + "n del Movimiento");
		trlTableMap.put("922", "Ministerio de Interior. Patronato Militar excluido de IT");
		trlTableMap.put("923", "Convenio de amistad hispano-USA");
		trlTableMap.put("924", "Trabajador trasladado al extranjero sin beneficiarios en Espa" + String.valueOf("\u00F1") + "a");
		trlTableMap.put("925", "Trabajador en extranjero con beneficiarios en Espa" + String.valueOf("\u00F1") + "a");
		trlTableMap.put("926", "Trabajadores Administraci" + String.valueOf("\u00F3") + "n General Compa" + String.valueOf("\u00F1") + String.valueOf("\u00ED") + "a Filipinas");
		trlTableMap.put("927", "Maestranza a" + String.valueOf("\u00E9") + "rea de Sevilla");
		trlTableMap.put("928", "Hospital Universitario Valladolid. Estatutarios");
		trlTableMap.put("929", "Hospital Universitario Valladolid. Interinos");
		trlTableMap.put("930", "Socios Trabajadores Cooperativas (trabajo asociado y explotaci" + String.valueOf("\u00F3") + "n de la tierra)");
		trlTableMap.put("931", "Reclusos con actividad laboral");
		trlTableMap.put("932", "Personal estatutario temporal org. salud");
		trlTableMap.put("933", "Becarios de investigaci" + String.valueOf("\u00F3") + "n");
		trlTableMap.put("934", "Funcionarios procedentes de MUNPAL -SNS-");
		trlTableMap.put("935", "Interinos procedentes de MUNPAL -SNS-");
		trlTableMap.put("936", "Penados en beneficio de la Comunidad");
		trlTableMap.put("937", "Funcionarios nuevo ingreso - RDL 13/2010");
		trlTableMap.put("938", "Alumno nuevo ingreso Ministerio de Defensa - RDL 13/2010");
		trlTableMap.put("939", "Clero diocesano de la iglesia cat" + String.valueOf("\u00F3") + "lica");
		trlTableMap.put("941", "Ministros culto de la iglesia adventista y ferede");
		trlTableMap.put("942", "Religiosos comunidades israelitas de Espa" + String.valueOf("\u00F1") + "a");
		trlTableMap.put("950", "Armadores asimilados a trabajadores cuenta ajena R" + String.valueOf("\u00F3") + "gimen Especial del Mar");
		trlTableMap.put("951", "Consejero-administrador SMC / Situaci" + String.valueOf("\u00F3") + "n laboral asimilada a cuenta ajena");
		trlTableMap.put("953", "Interinos de la Administraci" + String.valueOf("\u00F3") + "n de Justicia");
		trlTableMap.put("954", "Pr" + String.valueOf("\u00E1") + "cticos de puerto asimilados a cuenta ajena R" + String.valueOf("\u00F3") + "gimen Especial de Trabajadores del Mar");
		trlTableMap.put("960", "Trabajadores Extranjeros. Exclusi" + String.valueOf("\u00F3") + "n Cotizaci" + String.valueOf("\u00F3") + "n por Desempleo");
		trlTableMap.put("961", "Convenio Jap" + String.valueOf("\u00F3") + "n AT/EP");
		trlTableMap.put("962", "Convenio Corea AT/EP");
		trlTableMap.put("963", "Convenio S.S. China");
		trlTableMap.put("970", "Reconversi" + String.valueOf("\u00F3") + "n industrial cotizaci" + String.valueOf("\u00F3") + "n adicional al desempleo");
		trlTableMap.put("971", "Reconversi" + String.valueOf("\u00F3") + "n industrial ayu equi jubilaci" + String.valueOf("\u00F3") + "n anticipada");
		trlTableMap.put("972", "Promoci" + String.valueOf("\u00F3") + "n ind cotiz adicional al desempleo");
		trlTableMap.put("983", "Cotizaci" + String.valueOf("\u00F3") + "n Desempleo R" + String.valueOf("\u00F3") + "gimen Especial Agrario Cuenta Ajena Fijos IT/Maternidad");
		trlTableMap.put("984", "Mesas electorales");
		trlTableMap.put("985", "Trabajadores cuota obrera IT/Maternidad");
		trlTableMap.put("986", "Programas de formaci" + String.valueOf("\u00F3") + "n"); 
		
		trlTable = Collections.unmodifiableMap(trlTableMap);
		
	}

	public static Collection<String> getAllEntriesCollection() {
		Collection<String> entries = new ArrayList<String>();
		
		for(Entry<String, String> entry : trlTable.entrySet())
			entries.add(entry.getKey() + " - " + entry.getValue());
		
		return entries;
	}
	
	public static String getEntryByCode(String code) {
		if(null == code) return "";
		
		return code + " - " + trlTable.get(code);
	}
	
	
}
