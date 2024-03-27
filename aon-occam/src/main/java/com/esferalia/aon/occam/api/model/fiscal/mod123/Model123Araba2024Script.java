package com.esferalia.aon.occam.api.model.fiscal.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;


public enum Model123Araba2024Script implements IModelScript<Mod123Key> {
	
	 R01 ("Presentaci\u00F3n fuera de plazo por requerimiento", new Mod123Key[]{Mod123Key.AR_930},NONE)
	,R02 ("\u00BFHa sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?", new Mod123Key[]{Mod123Key.AR_907},NONE)
	,R03 ("Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso",new Mod123Key[]{Mod123Key.AR_909},NONE)	
	,R04 ("Si se ha dictado auto de declaraci\u00F3n de concurso en este per\u00EDodo, indique el tipo de autoliquidaci\u00F3n", new Mod123Key[]{Mod123Key.AR_908},NONE)
	,R05 ("Dividendos y otras rentas de participaci\u00F3n en fondos propios de entidades",new Mod123Key[]{Mod123Key.AR_C21,Mod123Key.AR_C23,Mod123Key.AR_C25},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R06 ("Resto de rentas",new Mod123Key[]{Mod123Key.AR_C22,Mod123Key.AR_C24,Mod123Key.AR_C26},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R07 ("Totales",new Mod123Key[]{Mod123Key.AR_C01,Mod123Key.AR_C02,Mod123Key.AR_C03},COMPUTE)
	,R08 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.AR_C04},NONE)
	,R09 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.AR_C05},NONE)
	,R10 ("Suma de retenciones e ingresos a cuenta.",new Mod123Key[]{Mod123Key.AR_C06},COMPUTE)
	,R11 ("Ajustes",new Mod123Key[]{Mod123Key.AR_C07},COMPUTE_KEY)
	,R12 ("Recargo pr\u00F3rroga",new Mod123Key[]{Mod123Key.AR_C08},NONE)
	,R13 ("Intereses de demora",new Mod123Key[]{Mod123Key.AR_C09},NONE)
	,R14 ("Deuda tributaria a ingresar",new Mod123Key[]{Mod123Key.AR_C10},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123Araba2024Script(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod123Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	}
	@Override
	public boolean hasGraphicParticularity() {
		return (this == R01 || this == R02 || this == R03 || this == R04);
	}
	@Override
	public boolean paintHeaderBefore() {
		return (this == R05);
	};
}
