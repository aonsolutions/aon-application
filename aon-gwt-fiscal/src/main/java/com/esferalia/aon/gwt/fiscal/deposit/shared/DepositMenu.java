package com.esferalia.aon.gwt.fiscal.deposit.shared;

public enum DepositMenu {

	HIS("Hoja Identificativa de la Sociedad"),
	BS("Balance de Situaci\u00F3n"),
	CPG("Cuenta de P\u00e9rdidas y Ganancias"),
	ECPN("Estado de Cambios en el Patrimonio Neto"),
	DM("Declaraci\u00F3n Medioambiental"),
	M("Memoria"),// MEMORIA
	D("Documentos"),
	MA("Modelo de Autocartera"),//MA MODELO AUTOCARTERA
	IP("Instancia de Presentaci\u00F3n"),
	CHD("Certificaci\u00F3n de la Huella Digital"),
	
	// MEMORIA
	AE("Actividad de la empresa"),
	BP("Bases de Presentaci\u00F3n de las Cuentas Anuales"),
	AR("Aplicaci\u00F3n de resultados"),
	AR_TL("Texto Libre"),
	AR_CN("Cuadros Normalizados"),
	NRV("Normas de Registro y Valoraci\u00F3n"),
	IMIII("Inmovilizado Material, Intangible e Inversiones Inmobiliarias"),
	IMIII_TL("Texto Libre"),
	IMIII_CN("Cuadros Normalizados"),
	AF("Activos Financieros"),
	AF_TL("Texto Libre"),
	AF_CN("Cuadros Normalizados"),
	PF("Pasivos Financieros"),
	PF_TL("Texto Libre"),
	PF_CN("Cuadros Normalizados"),
	FP("Fondos Propios"),
	SF("Situaci\u00F3n Fiscal"),
	IG("Ingresos y Gastos"),
	SDL("Subvenciones, Donaciones y Legados"),
	SDL_TL("Texto Libre"),
	SDL_CN("Cuadros Normalizados"),
	OPV("Operaciones con Partes Vinculantes"),
	OPV_TL("Texto Libre"),
	OPV_CN("Cuadros Normalizados"),
	OI("Otra Informaci\u00F3n"),
	OI_TL("Texto Libre"),
	OI_CN("Cuadros Normalizados"),
	IM("Informaci\u00F3n sobre el Medio Ambiente"),
	IM_TL("Texto Libre"),
	IM_CN("Cuadros Normalizados"),
	IA("Informaci\u00F3n sobre los Aplazamientos de Pago Efectuados a Proveedores"),

	//MA MODELO AUTOCARTERA
	MA1("P\u00e1gina A1"),
	MA11("P\u00e1gina A1.1"),
	MA2("P\u00e1gina A2"),
	MA3("P\u00e1gina A3"),
	MA4("P\u00e1gina A4"),
	MA5("P\u00e1gina A5"),
	MA6("P\u00e1gina A6"),
	MA7("P\u00e1gina A7");
	
	private String description;
	
	private DepositMenu(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
