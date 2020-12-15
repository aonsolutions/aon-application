package com.esferalia.aon.in.payroll.tgss.idc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

class BonusListener  implements IdcListener {
	
	private static final NumberFormat NUMBER_FORMAT = DecimalFormat.getNumberInstance(new Locale("es", "ES"));

	static final int[] ssBonusCodes = new int[] { 1, 2, 13, 15, 16, 37, 41, 46, 48, 51, 52, 54, 55 };
		
	@SuppressWarnings("serial")
	static final Map<String, String> ENTERPRISE_QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{

			put("57", "CUOTA_EMPRESARIAL"); 	// Cuota Total
			put("68", "CGC_E + IT_E + IMS_E"); 	// Contingencias Comunes y Profesionales - Cuota Total
			//put("81", "");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> EMPLOYEE_QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("68", "CGC");
			//put("57", "CUOTA_OBRERA"); 	// Cuota Total
		}
	};
	
	@SuppressWarnings("serial")
	static final Map<String, String> PEC_BONUS_MAP = new HashMap<String, String>() {
		{
			put("01", "BONIFICACIÓN INEM");
			//put("02", "BONIFICACIÓN HACIENDA EMBARCACIONES ZONA ESPECIAL DE CANARIAS");
			//put("07", "EXONERACIÓN");
			//put("10", "DECREMENTO DE CUOTAS");
			//put("13", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO-PORCENTAJE");
			put("15", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL");
			//put("19", "BONIFICACIÓN SPEE CON CARGO A HACIENDA");
			put("37", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO COMPLETO");
			put("41", "BONIFICACIÓN PROGRAMA FOMENTO DE EMPLEO. CUANTÍA DIARIA");
			//put("46", "BONIFICACIÓN SISTEMA NACIONLA GARANTÍA JUVENIL");
			//put("48", "BONIFICACIÓN - CUANTÍA - SIN HORAS COMPLEMENTARIAS");
			//put("51", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUANTÍA MENSUAL");
			//put("52", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUANTÍA DIARIA");
			//put("54", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUOTA FIJA MENSUAL");
			//put("55", "BONIFICACIÓN SEA TRANSFORMACIÓN EN INDEFINIDO. CUOTA FIJA DIARIA");

		}
	};
		
	private Collection<Bonus> ssBonuses = new LinkedList<Bonus>();
	
	public Collection<Bonus> getSSBonuses() {
		return Collections.unmodifiableCollection(ssBonuses);
	}

	// ------------------------------------------------------------ IdcListener

	@Override
	public void onEmployeeQuotePEC(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
		if ( PEC_BONUS_MAP.containsKey(code )) {
			try {
				if ( ENTERPRISE_QUOTA_EXPRESSION_MAP.containsKey(quota))
					ssBonuses.add( newEnterpriseBonus(nss, ccc, code, description, portTipo, quota, start, end)) ;
				if ( EMPLOYEE_QUOTA_EXPRESSION_MAP.containsKey(quota))
					ssBonuses.add( newEmployeeBonus(nss, ccc, code, description, portTipo, quota, start, end)) ;
			} catch (ParseException e) {
			}
		}
	}
	
	
	
	
	// ------------------------------------------------------------------------
	
	private static Bonus newEnterpriseBonus(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		
		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		
		Bonus ssBonus = new Bonus();		
		ssBonus.setCcc(ccc);
		ssBonus.setNss(nss);
		ssBonus.setStartDate(start);
		ssBonus.setEndDate(end);
		ssBonus.setDescription(String.format(new Locale("es", "ES"),"%s (%.2f%%)", description, percent));
		ssBonus.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*/" +
				"/*read-only*/( %s ) * %.2f / 100.00/**/", 
				Calendar.getInstance().getTimeInMillis(),
				code, 
				quota,  
				ENTERPRISE_QUOTA_EXPRESSION_MAP.get(quota), 
				percent
				));
		
		
		return ssBonus;
	}

	private static Bonus newEmployeeBonus(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		
		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		
		Bonus ssBonus = new Bonus();		
		ssBonus.setCcc(ccc);
		ssBonus.setNss(nss);
		ssBonus.setStartDate(start);
		ssBonus.setEndDate(end);
		ssBonus.setDescription(String.format(new Locale("es", "ES"),"%s (%.2f%%)", description, percent));
		ssBonus.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*/" +
				"/*read-only*/( %s ) * %.2f / 100.00 * -1/**/", 
				Calendar.getInstance().getTimeInMillis(),
				code, 
				quota,  
				EMPLOYEE_QUOTA_EXPRESSION_MAP.get(quota), 
				percent
				));
		ssBonus.setEmployee();
		
		return ssBonus;
	}
}