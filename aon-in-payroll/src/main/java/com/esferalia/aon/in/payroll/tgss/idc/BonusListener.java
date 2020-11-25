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
	static final Map<String, String> QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("01", "IT_E + IMS_E + DESMPL_E + FOGASA_E + FP_E");
			put("02", "DESMPL_E");
			put("03", "CGC_E");
			put("04", "DESMPL + DESMPL_E");
			put("05", "DESMPL");
			put("06", "DESMPL + DESMPL_E + FP + FP_E + FOGASA + FOGASA_E");
			put("07", "CGC_E + DESMPL_E + FOGASA_E + FP_E");
			put("08", "CGC + IT + IMS + DESMPL + FOGASA + FP");
			put("09", "CGC_E");
			put("10", "CGC + CGC_E + DESMPL + DESMPL_E + FP + FP_E + FOGASA + FOGASA_E");
			put("11", "");
			put("12", "DESMPL + DESMPL_E + FOGASA + FOGASA_E");
			put("13", "FOGASA + FOGASA_E");
			put("14", "");
			put("15", "");
			put("16", "");
			put("17", "");
			put("18", "");
			put("19", "");
			put("20", "");
			put("21", "");
			put("22", "");
			put("23", "");
			put("24", "CGC + CGC_E + IT + IT_E");
			put("25", "");
			put("26", "");
			put("27", "IT + IT_E + IMS + IMS_E + DESMPL + DESMPL_E + FOGASA + FOGASA_E");
			put("28", "IT + IT_E + IMS + IMS_EFOGASA + FOGASA_E");
			put("29", "");
			put("30", "");
			put("31", "");
			put("32", "");
			put("33", "");
			put("34", "");
			put("35", "");
			put("36", "");
			put("37", "");
			put("38", "IT + IT_E + IMS + IMS_E");
			put("39", "IT + IT_E");
			put("40", "CGC + CGC_E + DESMPL + DESMPL_E");
			put("41", "IT + IT_E + IMS + IMS_E");
			put("42", "");
			put("43", "CGC + CGC_E");
			put("44", "IT + IT_E + CGC + CGC_E + DESMPL + DESMPL_E + FOGASA + FOGASA_E");
			put("45", "IT + IT_E + CGC + CGC_E + FOGASA + FOGASA_E");
			put("46", "");
			put("47", "");
			put("48", "");
			put("49", "");
			put("50", "");
			put("51", "");
			put("52", "");
			put("53", "DESMPL + DESMPL_E + FOGASA + FOGASA_E + FP + FP_E");
			put("54", "CGC");
			put("55", "");
			put("56", "");
			put("57", "CGC + IT + IMS + DESMPL + FOGASA + FP + CGC_E + IT_E + IMS_E + DESMPL_E + FOGASA_E + FP_E");
			put("58", "");
			put("59", "");
			put("60", "");
			put("61", "");
			put("62", "");
			put("63", "");
			put("64", "");
			put("65", "");
			put("68", "CGC + CGC_E + IT_E + IMS_E");
			put("69", "");
			put("70", "");
			put("71", "");
			put("72", "");
			put("73", "");
			put("74", "");
			put("75", "");
			put("76", "");
			put("77", "");
			put("78", "FP + FP_E");
			put("79", "DESMPL + DESMPL_E + FP + FP_E");
			put("80", "");
			put("81", "");
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
				Bonus ssBonus = newSSBonus(nss, ccc, code, description, portTipo, quota, start, end);
				ssBonuses.add(ssBonus);
			} catch (ParseException e) {
			}
		}
	}
	
	
	
	
	// ------------------------------------------------------------------------
	
	private static Bonus newSSBonus(String nss, String ccc, String code, String description, String portTipo,
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
				QUOTA_EXPRESSION_MAP.get(quota), 
				percent
				));
		
		return ssBonus;
	}

}