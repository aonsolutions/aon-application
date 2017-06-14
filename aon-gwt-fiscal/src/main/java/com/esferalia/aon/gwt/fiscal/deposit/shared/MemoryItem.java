package com.esferalia.aon.gwt.fiscal.deposit.shared;

import java.util.HashMap;

public class MemoryItem {
	public static MemoryItem getInstance() {
		return new MemoryItem();
	}
	
	public static final String  ACTIVIDAD_EMPRESA = "Actividad de la empresa";
	public static final String  BASES_PRESENTACION = "Bases de Presentaci\u00F3n de las Cuentas Anuales";
	public static final String  APLICACION_RESULTADOS = "Aplicaci\u00F3n de Resultados";
	public static final String  NORMAS_REGISTRO = "Normas de Registro y Valoraci\u00F3n";
	public static final String  INMOVILIZADO = "Inmovilizado Material, Intangible e Inversiones Inmobiliarias";
	public static final String  ACTIVOS_FINANCIEROS = "Activos Financieros";
	public static final String  PASIVOS_FINANCIEROS = "Pasivos Financieros";
	public static final String  FONDOS_PROPIOS = "Fondos Propios";
	public static final String  SITUACION_FISCAL = "Situaci\u00F3n Fiscal";
	public static final String  INGRESOS_GASTOS = "Ingresos y Gastos";
	public static final String  SUBVENCIONES = "Subvenciones, Donaciones y Legados";
	public static final String  PARTES_VINCULANTES = "Operaciones con Partes Vinculantes";
	public static final String  OTRA_INFORMACION = "Otra Informaci\u00F3n";
	public static final String  MEDIOAMBIENTE = "Informaci\u00F3n sobre el Medio Ambiente";
	public static final String  APLAZAMIENTOS = "Informaci\u00F3n sobre los Aplazamientos de Pago Efectuados a Proveedores";

	@SuppressWarnings("serial")
	private final HashMap<Integer, Integer> apartados = new HashMap<Integer, Integer>() {{
	    put(2014, 15);
	    put(2015, 15);
	    put(2016, 10);
	}};
	
	/**
	 * Description de los apartados en los ejercios 2014 & 2015
	 */
	private final String[] apartadosName2014 = new String[]{
    		ACTIVIDAD_EMPRESA,
    		BASES_PRESENTACION,
    		APLICACION_RESULTADOS,
    		NORMAS_REGISTRO,
    		INMOVILIZADO,
    		ACTIVOS_FINANCIEROS,
    		PASIVOS_FINANCIEROS,
    		FONDOS_PROPIOS,
    		SITUACION_FISCAL,
    		INGRESOS_GASTOS,
    		SUBVENCIONES,
    		PARTES_VINCULANTES,
    		OTRA_INFORMACION,
    		MEDIOAMBIENTE,
    		APLAZAMIENTOS
    };
	
	/**
	 * Description de los apartados en los ejercios 2016
	 */
	private final String[] apartadosName2016 = new String[]{
    		ACTIVIDAD_EMPRESA,
    		BASES_PRESENTACION,
    		NORMAS_REGISTRO,
    		INMOVILIZADO,
    		ACTIVOS_FINANCIEROS,
    		PASIVOS_FINANCIEROS,
    		FONDOS_PROPIOS,
    		SITUACION_FISCAL,
    		PARTES_VINCULANTES,
    		OTRA_INFORMACION,
    };
	
	@SuppressWarnings("serial")
	public final HashMap<Integer,  String[]> apartadosName = new HashMap<Integer, String[]>() {{
	    put(2014, apartadosName2014);
	    put(2015, apartadosName2014);
	    put(2016, apartadosName2016);
	}};
	
	public Integer getApartadosSize(Integer year) {
		if(year > 2016) return apartados.get(2016);
		return apartados.get(year);
	}
	
	public String getApartadoName(Integer year, Integer pos){
		return "Apartado " + (pos + 1) + ": " + apartadosName.get(year)[pos];
	}
}
