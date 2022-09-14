package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.codemirror.client.addon.AONFilter;
import com.esferalia.aon.gwt.codemirror.client.addon.AONMarker;
import com.esferalia.aon.gwt.codemirror.client.mode.CLikeConfiguration;
import com.esferalia.aon.gwt.codemirror.client.mode.CLikeMode;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeArea;

public class ExpressionCodeArea extends CodeArea {

	private AONMarker marker;
	private AONFilter filter;
	private boolean advancedMode;

	public ExpressionCodeArea() {
		this(false);
	}

	public ExpressionCodeArea(boolean advancedMode) {
		super();
		marker = new AONMarker();
		filter = new AONFilter();
		setTheme("eclipse");
		this.advancedMode = advancedMode;
		setMatchBrackets(true);
		setMode(CLikeConfiguration.create().setKeywords(getKeywords())
				.setBuiltin(getBuiltin()).setName("text/x-java"));
	}

	public void setBuiltin(String builtin[]) {
		CLikeMode mode = getMode();
		mode.setBuiltin(builtin);
	}

	public void setKeywords(String keywords[]) {
		CLikeMode mode = getMode();
		mode.setKeywords(keywords);
	}

	public final void setAdvancedMode(boolean advancedMode) {
		if (advancedMode)
			enableAdvancedMode();
		else
			disableAdvancedMode();
		this.advancedMode = advancedMode;
	}
	
	public boolean getAdvancedMode(){
		return advancedMode;
	}

	private final void enableAdvancedMode() {
		removeDocumentChangeHandler(filter);
		addDocumentChangeHandler(marker);
		marker.init(this);
	}

	private final void disableAdvancedMode() {
		removeDocumentChangeHandler(marker);
		addDocumentChangeHandler(filter);
		filter.init(this);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		setAdvancedMode(advancedMode);
	}

	// ------------------------------------------------------------------------

	private static String[] getKeywords() {
		return new String[] { "FECHA_INICIO", "FECHA_FINAL", "INICIO_NOMINA",
				"FIN_NOMINA", "INICIO_CONTRATO", "FIN_CONTRATO",

				"HORAS_LUNES", "HORAS_MARTES", "HORAS_MIERCOLES",
				"HORAS_JUEVES", "HORAS_VIERNES", "HORAS_SABADO",
				"HORAS_DOMINGO",

				"BASE_CGC", "BASE_CGP", "BASE_IRPF", "BASE_IPREM", "BIPREM",
				"BASE_CGC_MIN", "BASE_CGC_MAX",

				"BASE_CGP_MIN", "BASE_CGP_MAX", "BASE_ANTIGUEDAD",
				"BASE_REGULADORA", "BASE_ESTR", "BASE_NESTR", "BASE_MTNAD",

				"CGC", "CGC_E", "IT_E", "IT_E", "FP", "FP_E", "DESMPL",
				"DESMPL_E", "FOGASA_E", "CUOTA_EMPRESARIAL",
				"CUOTA_TRABAJADOR",

				"NOMINA", "ATRASOS", "FINIQUITO", "EXTRA",

				"TOTAL_LIQUIDO",
				"TOTAL_DEVENGADO",

				"ACTUAL",

				"BONIFICADO",

				// Excel
				"O", "Y", "SI", "NO", "FALSO", "VERDADERO", "ABS", "POTENCIA",
				"RAIZ", "ENTERO",
				"COCIENTE",
				"DIAS",

				// AON's
				"MIN", "MAX", "AÑO", "BIENIO", "TRIENIO", "CUATRIENIO",
				"QUINQUENIO", "SEXENIO", "SEPTENIO", "UNDEFINED",

				"ANTIGÜEDAD", "EXCESO",

				"INICIO_IT",

				"PROFESION", "VISIONADOE",

				"_P",

				"NADA", "IMPROCEDENTE", "PROCEDENTE", "FIN", "FIN_OBRA",
				"FIN_TEMPORAL", "CAMBIO_CONDICIONES", "BAJA_PERIODO_PRUEBA",

				"PREST_IT",

				"_P", "SELF", "REMOVE", "CONTEXT",

				"BR", "BRUTO", "NETO", "SISTEMA", "GTZDO", "CONVENIO",
				"ANTICIPO_ATRASOS",

				// Old
				"CHECK", "MESES", "AVISO", "DEFINIDA", "CHECK_VAR",

				"ANTIGUEDAD", "SALARIO_BASE", "GARANTIZADO_IT",

		};
	}

	private static String[] getBuiltin() {
		return new String[] {
				// Datos de la persona
				"EDAD",
				"SEXO",
				"HOMBRE",
				"MUJER",

				// Dias
				"DIAS_AÑO", "DIAS_MES", "DIAS_VACACIONES",
				"DIAS_VACACIONES_NO_DISFRUTADOS", "DIAS_TRABAJADOS",
				"DIAS_SEMANA", "DIAS_CANONTRATO", "DIAS_NOMINA", "DIAS_PAGA",
				"DIAS_BONIFICACION", "DIAS_COTIZADOS", "DIAS_EFECTIVOS",
				"DIAS_IT", "DIAS_ESPECIALES", "DIAS_PATERNIDAD",
				"DIAS_MATERNIDAD", "DIAS_ENFERMEDAD_COMUN",
				"DIAS_ENFERMEDAD_PROFESIONAL", "NUM_PAGAS",

				"FECHA_PREAVISO", "DIAS_PREAVISO",

				"DIAS_INDEMNIZACION", "CAUSA_INDEMNIZACION", "AÑOS_TRABAJADOS",

				"MESES_NOMINA",
				"MESES_PAGA",
				"SEMANAS_TRABAJADAS",
				"SEMANAS_NOMINA",
				"SEMANAS_PAGA",

				// Horas ( contratos a tiempo parcial )
				"HORAS_SEMANA", "HORAS_NOMINA",

				"NOCTURNO",

				"IMPORTE_DIA_VACACIONES", "IMPORTE_INDEMNIZACION",

				"TC2", "CNO", "IPREM", "CATEGORIA", "INDEFINIDO", "OCUPACION",
				"GARANTIZADO", "IRREGULAR", "TIEMPO_COMPLETO",
				"PORCENTAJE_IRPF", "GRUPO_COTIZACION", "EXENTO_IPREM",
				"XIPREM", "TARIFA_IT", "TARIFA_IMS", "CONTRATO_CORTA_DURACION",
				"AÑOS_ANTIGUEDAD", "COLECT_PECULIAR_COTIZACION",
				"COD_FIN_CONTRATO", "DESC_FIN_CONTRATO",

				"MAYOR_65", "ASIMILADO_REGIMEN_GRAL", "INGRESO_AC_EMPRESA",

				// Embargos
				"PENDIENTE", "EMBARGADO", "EMBARGABLE", "MAX_EMBARGABLE", };
	}

}
