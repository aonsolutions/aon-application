package com.esferalia.aon.gwt.codemirror.showcase.client;

import com.esferalia.aon.gwt.codemirror.client.addon.AONFilter;
import com.esferalia.aon.gwt.codemirror.client.addon.AONMarker;
import com.esferalia.aon.gwt.codemirror.client.mode.CLikeConfiguration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeArea;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class Showcase implements EntryPoint {

	interface Binder extends UiBinder<Widget, Showcase> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	interface ShowcaseResources extends ClientBundle {
		@NotStrict
		@Source("showcase.css")
		CssResource css();
	}
	
	@UiField
	CodeArea javaCodeArea;

	@UiField(provided = true)
	CodeArea payrollCodeArea;
	
	@UiField
	RadioButton markerButton;
	@UiField
	RadioButton filterButton;

	AONFilter aonFilter = new AONFilter();
	AONMarker aonMarker = new AONMarker();

	public void onModuleLoad() {
		GWT.<ShowcaseResources> create(
				ShowcaseResources.class).css().ensureInjected();

		payrollCodeArea = new CodeArea();
		payrollCodeArea.setMode(CLikeConfiguration.create()
				.setKeywords(getKeywords()).setBuiltin(getBuiltin()).setName("text/x-java"));
		

		// Create the UI defined in Showcase.ui.xml.
		RootLayoutPanel.get().add(binder.createAndBindUi(this));
		//javaCodeArea.addKeyMap("Ctrl-Space", "autocomplete");

		payrollCodeArea.addDocumentChangeHandler(filterButton.getValue()  ? aonFilter : aonMarker );

		payrollCodeArea.setValue("( COTIZACION_IT == \"MENSUAL\" ? 30 - ( /*re1ad-only*/DIAS_MES - DIAS_PATERNIDAD/**/) : /*user*/DIAS_PATERNIDAD/**/ ) * BASE_REGULADORA");

		//CodeMirror.defineMode("aon", AONOverlayMode.create() );
		//payrollCodeArea.addOverlay("aon");
		
	}
	
	@UiHandler({"markerButton"})
	void onMarkerChanged(ValueChangeEvent<Boolean> event) {
		onButtonChanged(aonMarker, markerButton.getValue());
	}
	
	@UiHandler({"filterButton"})
	void onFilterChanged(ValueChangeEvent<Boolean> event) {
		onButtonChanged(aonFilter, filterButton.getValue());
	}
	
	void onButtonChanged(AONMarker marker, boolean checked ) {
		if ( checked ) {
			payrollCodeArea.addDocumentChangeHandler(marker);
			marker.init(payrollCodeArea);
		}
		else {
			payrollCodeArea.removeDocumentChangeHandler(marker);
		}
		
	}
	
	private static String [] getKeywords() {
		return new String []{
		"FECHA_INICIO",
		"FECHA_FINAL",
		"INICIO_NOMINA",
		"FIN_NOMINA",
		"INICIO_CONTRATO",
		"FIN_CONTRATO",
		
		"HORAS_LUNES",
		"HORAS_MARTES",
		"HORAS_MIERCOLES",
		"HORAS_JUEVES",
		"HORAS_VIERNES",
		"HORAS_SABADO",
		"HORAS_DOMINGO",

		"BASE_CGC",
		"BASE_CGP",
		"BASE_IRPF",
		"BASE_IPREM",
		"BIPREM",
		"BASE_CGC_MIN",
		"BASE_CGC_MAX",
		
		"BASE_CGP_MIN",
		"BASE_CGP_MAX",
		"BASE_ANTIGUEDAD",
		"BASE_REGULADORA",
		"BASE_ESTR",
		"BASE_NESTR",
		"BASE_MTNAD",
		
		"CGC",
		"CGC_E",
		"IT_E",
		"IT_E",
		"FP",
		"FP_E",
		"DESMPL",
		"DESMPL_E",
		"FOGASA_E",
		"CUOTA_EMPRESARIAL",
		"CUOTA_TRABAJADOR",
		
		"NOMINA",
		"ATRASOS",
		"FINIQUITO",
		"EXTRA",
		
		"TOTAL_LIQUIDO",
		"TOTAL_DEVENGADO",
		
		"ACTUAL",

		"BONIFICADO",
		
		// Excel
		"O",
		"Y",
		"SI",
		"NO",
		"FALSO",
		"VERDADERO",
		"ABS",
		"POTENCIA",
		"RAIZ",
		"ENTERO",
		"COCIENTE",
		"DIAS",

		// AON's
		"MIN",
		"MAX",
		"AÑO",
		"BIENIO",
		"TRIENIO",
		"CUATRIENIO",
		"QUINQUENIO",
		"SEXENIO",
		"SEPTENIO",
		"UNDEFINED",
		
		"ANTIGÜEDAD",
		"EXCESO",
		
		"INICIO_IT",

		"PROFESION",
		"VISIONADOE",
		
		"_P",
		
		"NADA",
		"IMPROCEDENTE",
		"PROCEDENTE",
		"FIN",
		"FIN_OBRA",
		"FIN_TEMPORAL",
		"CAMBIO_CONDICIONES",
		
		"PREST_IT",
		
		"_P",
		"SELF",
		"REMOVE",
		"CONTEXT",

		"BR",
		"BRUTO",
		"NETO",
		"SISTEMA",
		"GTZDO",
		"CONVENIO",
		"ANTICIPO_ATRASOS",

		// Old 
		"CHECK",
		"MESES",
		"AVISO",
		"DEFINIDA",
		"CHECK_VAR",
		
		"ANTIGUEDAD",
		"SALARIO_BASE",
		"GARANTIZADO_IT",
		
		
		};
	}

	private static String [] getBuiltin() {
		return new String []{
				// Datos de la persona
				"EDAD",
				"SEXO",
				"HOMBRE",
				"MUJER",

				// Dias
				"DIAS_AÑO",
				"DIAS_MES",
				"DIAS_VACACIONES",
				"DIAS_VACACIONES_NO_DISFRUTADOS",
				"DIAS_TRABAJADOS",
				"DIAS_SEMANA",
				"DIAS_CANONTRATO",
				"DIAS_NOMINA",
				"DIAS_PAGA",
				"DIAS_BONIFICACION",
				"DIAS_COTIZADOS",
				"DIAS_EFECTIVOS",
				"DIAS_IT",
				"DIAS_ESPECIALES",
				"DIAS_PATERNIDAD",
				"DIAS_MATERNIDAD",
				"DIAS_ENFERMEDAD_COMUN",
				"DIAS_ENFERMEDAD_PROFESIONAL",
				"NUM_PAGAS",

				"FECHA_PREAVISO",
				"DIAS_PREAVISO",
				
				"DIAS_INDEMNIZACION",
				"CAUSA_INDEMNIZACION",
				"AÑOS_TRABAJADOS",

				"MESES_NOMINA",
				"MESES_PAGA",
				"SEMANAS_TRABAJADAS",
				"SEMANAS_NOMINA",
				"SEMANAS_PAGA",
				
				// Horas ( contratos  a tiempo parcial )
				"HORAS_SEMANA",
				"HORAS_NOMINA",

				"NOCTURNO",

				"IMPORTE_DIA_VACACIONES",
				"IMPORTE_INDEMNIZACION",
				
				"TC2",
				"CNO",
				"IPREM",
				"CATEGORIA",
				"INDEFINIDO",
				"OCUPACION",
				"GARANTIZADO",
				"IRREGULAR",
				"TIEMPO_COMPLETO",
				"PORCENTAJE_IRPF",
				"GRUPO_COTIZACION",
				"EXENTO_IPREM",
				"XIPREM",
				"TARIFA_IT",
				"TARIFA_IMS",
				"CONTRATO_CORTA_DURACION",
				"AÑOS_ANTIGUEDAD",
				"COLECT_PECULIAR_COTIZACION",
				"COD_FIN_CONTRATO",
				"DESC_FIN_CONTRATO",

				"MAYOR_65",
				"ASIMILADO_REGIMEN_GRAL",
				"INGRESO_AC_EMPRESA",
				
				// Embargos 
				"PENDIENTE",
				"EMBARGADO",
				"EMBARGABLE",
				"MAX_EMBARGABLE",
		};
	}
}
