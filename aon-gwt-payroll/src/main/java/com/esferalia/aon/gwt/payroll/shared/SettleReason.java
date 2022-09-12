package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SettleReason {
	
	private Map<Integer, String> settleReasonMap;
	
	public SettleReason() {
		this.settleReasonMap = new HashMap<Integer, String>();
		fillSettleReasonMap();
	}

	private void fillSettleReasonMap() {
		this.settleReasonMap.put(51, "Baja voluntaria/Dimisi\u00F3n");
		this.settleReasonMap.put(53, "Baja despido disciplinario individual");
		this.settleReasonMap.put(54, "Baja no voluntaria por otras causas");
		this.settleReasonMap.put(55, "Baja por fusi\u00F3n-absorci\u00F3n empresa");
		this.settleReasonMap.put(56, "Baja por fallecimiento");
		this.settleReasonMap.put(58, "Baja por pase a la situaci\u00F3n de pensionista");
		this.settleReasonMap.put(63, "Baja por excedencia voluntaria/forzosa");
		this.settleReasonMap.put(65, "Baja por agotamiento I.T.");
		this.settleReasonMap.put(67, "Baja por paro estacional");
		this.settleReasonMap.put(68, "Baja por excedenciamaternal/cuidado de hijos");
		this.settleReasonMap.put(69, "Baja por suspensi\u00F3n temporal ERE");
		this.settleReasonMap.put(73, "Baja por cuidado de familiares");
		this.settleReasonMap.put(74, "Baja por otras causas de suspensi\u00F3n");
		this.settleReasonMap.put(76, "Baja por excedencia violencia de g\u00E9nero");
		this.settleReasonMap.put(77, "Baja por despido colectivo");
		this.settleReasonMap.put(80, "Suspensi\u00F3n por violencia de g\u00E9nero");
		this.settleReasonMap.put(85, "Baja por no superar el periodo de prueba");
		this.settleReasonMap.put(89, "Baja por cambio de CCC. Datos por trabajador");
		this.settleReasonMap.put(91, "Baja por despido por causas objetivas empresa");
		this.settleReasonMap.put(92, "Baja por despido por causas objetivas trabajador");
		this.settleReasonMap.put(93, "Baja por fin contrato temporal o de duraci\u00F3n determinada");
		this.settleReasonMap.put(94, "Baja por pase a inactividad fijos discontinuos");
		this.settleReasonMap.put(99, "Otras causas de baja");
	}
	
	public Map<Integer, String> getSettleReasonMap() {
		return this.settleReasonMap;
	}
	
	public Set<Entry<Integer, String>> getSettleReasonEntries() {
		return this.settleReasonMap.entrySet();
	}

}
