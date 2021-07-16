package com.esferalia.aon.in.payroll.tgss.idc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

class PECListener  implements IdcListener {
	
	@FunctionalInterface
	private static interface CostProvider {
		PEC.Cost  newCost(			
				String nss, 
				String ccc, 
				String pec, 
				String quota, 
				String porTipo,
				String description, 
				Date start, 
				Date end);
	}
	
	@FunctionalInterface
	private static interface DeductionProvider {
		PEC.Deduction  newDeduction(			
				String nss, 
				String ccc, 
				String pec, 
				String quota, 
				String porTipo,
				String description, 
				Date start, 
				Date end);
	}

	private static final NumberFormat NUMBER_FORMAT = DecimalFormat.getNumberInstance(new Locale("es", "ES"));

	static final int[] ssBonusCodes = new int[] { 1, 2, 13, 15, 16, 37, 41, 46, 48, 51, 52, 54, 55 };
		
	@SuppressWarnings("serial")
	static final Map<String, String> BONUS_QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("01", "CUOTA_EMPRESARIAL"); 	// 
			put("03", "CGC_E"); 				// Cuota empresarial por Contingencias Comunes
			put("51", "CUOTA_EMPRESARIAL"); 	// Cuota Empresarial - Horas extras			
			put("57", "CUOTA_EMPRESARIAL"); 	// Cuota Total
			put("68", "CGC_E + IT_E + IMS_E"); 	// Contingencias Comunes y Profesionales - Cuota Total
			//put("81", "");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, DeductionProvider> DEDUCTION_QUOTA_PROVIDER_MAP = new HashMap<String, DeductionProvider>() {
		{
			put("68", newRemoveDeduction(ContextVariable.CGC_EMPLOYEE));
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, Collection<CostProvider>> COST_QUOTA_PROVIDERS_MAP = new HashMap<String, Collection<CostProvider>>() {
		{
			put("62", collection(newRemoveCost(ContextVariable.FP_ENTERPRISE),newRemoveCost(ContextVariable.FOGASA_ENTERPRISE)));
		}
	};
	
	@SuppressWarnings("serial")
	static final Map<String, String> PEC_TYPE_T_49_MAP = new HashMap<String, String>() {
		{
			put("01", "BONIFICACIÓN INEM");
			//put("02", "BONIFICACIÓN HACIENDA EMBARCACIONES ZONA ESPECIAL DE CANARIAS");
			//put("07", "EXONERACIÓN");
			//put("10", "DECREMENTO DE CUOTAS");
			put("13", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO-PORCENTAJE");
			put("16", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO. CUANTÍA");
			put("15", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL");
			//put("19", "BONIFICACIÓN SPEE CON CARGO A HACIENDA");
			put("37", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO COMPLETO");
			put("40", "TIPO COTIZACIÓN ESPECIAL.SEA");
			put("41", "BONIFICACIÓN PROGRAMA FOMENTO DE EMPLEO. CUANTÍA DIARIA");
			put("42", "RED.CUOTA SS-CUANTÍA");
			//put("46", "BONIFICACIÓN SISTEMA NACIONLA GARANTÍA JUVENIL");
			//put("48", "BONIFICACIÓN - CUANTÍA - SIN HORAS COMPLEMENTARIAS");
			//put("51", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUANTÍA MENSUAL");
			//put("52", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUANTÍA DIARIA");
			//put("54", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUOTA FIJA MENSUAL");
			//put("55", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUOTA FIJA DIARIA");

		}
	};
		
	@SuppressWarnings("serial")
	static final Map<String, String> PEC_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("01", "( %s ) * %.2f / 100.00"); 															// BONIFICACIÓN INEM
			put("13", "( %s ) * %.2f / 100.00"); 															// BONIFICACIÓN INEM
			put("16",  "MIN(%s, %.2f)"); 																	// 
//			put("16",  String.format(Locale.ROOT,"%%2$.2f * %s * %s / %s", ContextVariable.PARTIAL_FACTOR, ContextVariable.QUOTE_DAYS, ContextVariable.MONTH_DAYS )); 															// 
			put("15", String.format(Locale.ROOT,"(%%s) * %%.2f / 100.00 * %1$s",ContextVariable.ERE_FACTOR_FORCE_OFF, ContextVariable.ERE_FACTOR_FORCE, ContextVariable.ERE_FACTOR )); 	// EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL
			put("37", "( %s ) * %.2f / 100.00");
			put("41", "( %s ) * %.2f / 100.00");

			put("42",  "MIN(%s, %.2f)"); 																	// 

			put("51",  String.format(Locale.ROOT,"%%2$.2f * %s * %s / %s", ContextVariable.PARTIAL_FACTOR, ContextVariable.QUOTE_DAYS, ContextVariable.MONTH_DAYS )); 															// 

		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_DESCRIPTION_MAP = new HashMap<String, String>() {
		{
			put("16", "%s (%.2f)"); 															// 
		}
	};

	private Collection<PEC> ssPECs = new LinkedList<PEC>();
	
	public Collection<PEC> getSSBonuses() {
		return Collections.unmodifiableCollection(ssPECs);
	}

	// ------------------------------------------------------------ IdcListener

	@Override
	public void onEmployeeQuotePEC(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
		if ( PEC_TYPE_T_49_MAP.containsKey(code )) {
			try {
				if ( BONUS_QUOTA_EXPRESSION_MAP.containsKey(quota))
					ssPECs.add( newBonus(nss, ccc, code, description, portTipo, quota, start, end)) ;
				if ( DEDUCTION_QUOTA_PROVIDER_MAP.containsKey(quota))
					ssPECs.add( DEDUCTION_QUOTA_PROVIDER_MAP.get(quota).newDeduction(nss, ccc, code, quota, portTipo, description, start, end)) ;
				if ( COST_QUOTA_PROVIDERS_MAP.containsKey(quota))
					COST_QUOTA_PROVIDERS_MAP.get(quota).forEach( f -> ssPECs.add(f.newCost(nss, ccc, code, quota, portTipo, description, start, end))) ;
			} catch (ParseException e) {
			}
		}
	}
	
	
	
	
	// ------------------------------------------------------------------------
	
	private static PEC newBonus(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		
		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		
		PEC ssBonus = new PEC.Bonus();		
		ssBonus.setCcc(ccc);
		ssBonus.setNss(nss);
		ssBonus.setStartDate(start);
		ssBonus.setEndDate(end);
		ssBonus.setDescription(String.format(new Locale("es", "ES"),PEC_DESCRIPTION_MAP.getOrDefault(code, "%s (%.2f%%)"), description, percent));
		ssBonus.setFormula(getEnterpriseFormula(code, portTipo, quota, start, end));
		
		
		return ssBonus;
	}

	private static String getEnterpriseFormula(String code, String portTipo,
			String quota, Date start, Date end) throws ParseException {

		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		String formula ;
		
		formula = String.format(Locale.ROOT,
			"/*epoch:%d,pec:%s,quota:%s*//*read-only*/%s/**/",
			//(percent == 100.00 ? "/*read-only*/%s/**/" : "/*read-only*/( %s ) * %.2f /**/"), 
			Calendar.getInstance().getTimeInMillis(),
			code, 
			quota,
			
			String.format(Locale.ROOT, PEC_EXPRESSION_MAP.get(code), BONUS_QUOTA_EXPRESSION_MAP.get(quota), percent), 
			
			(percent / 100.00)
			
			);
		
		return formula;
	}
	
	
	private static DeductionProvider newRemoveDeduction( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newRemoveDeduction(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static CostProvider newRemoveCost( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newRemoveCost(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static PEC.Deduction newRemoveDeduction(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		PEC.Deduction deduction =  new PEC.Deduction();	
		return newRemovePEC(deduction, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static PEC.Cost newRemoveCost(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		PEC.Cost cost =  new PEC.Cost();	
		return newRemovePEC(cost, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static <T extends PEC> T newRemovePEC(
			T t,
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		
		t.setCcc(ccc);
		t.setNss(nss);
		t.setStartDate(start);
		t.setEndDate(end);
		t.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/REMOVE()/**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota
				));
		t.setDescription(String.format(new Locale("es", "ES"),"%s (%s)", description, portTipo));
		t.setName(var.getName());
		
		return t;
	}
	
	private static  <T> Collection<T> collection (T ...ts) {
		ArrayList<T> list = new ArrayList<T>(ts.length);
		
		for (T t : ts)
			list.add(t);

		return list;
	}

}