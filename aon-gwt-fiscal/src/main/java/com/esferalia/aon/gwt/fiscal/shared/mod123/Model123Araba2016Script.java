package com.esferalia.aon.gwt.fiscal.shared.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;


public enum Model123Araba2016Script implements IModelScript<Mod123Key> {
	
	 R01 ("\u00BFHa sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?"
			,new Mod123Key[]{Mod123Key.AR_907},NONE)
	,R02 ("Si se ha dictado auto de declaraci\u00F3n de concurso en este per\u00EDodo, indique el tipo de autoliquidaci\u00F3n"
			,new Mod123Key[]{Mod123Key.AR_908},NONE)
	,R03 ("Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso",new Mod123Key[]{Mod123Key.AR_909},NONE)
	,R04 ("Retenciones e ingresos a cuenta",new Mod123Key[]{Mod123Key.AR_C01,Mod123Key.AR_C02,Mod123Key.AR_C03},INVOICE,DIFF_INVOICE)
	,R05 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.AR_C04},NONE)
	,R06 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.AR_C05},NONE)
	,R07 ("Suma de retenciones e ingresos a cuenta.",new Mod123Key[]{Mod123Key.AR_C06},COMPUTE)
	,R08 ("Ajustes",new Mod123Key[]{Mod123Key.AR_C07},NONE)
	,R09 ("Recargo pr\u00F3rroga",new Mod123Key[]{Mod123Key.AR_C08},NONE)
	,R10 ("Intereses de demora",new Mod123Key[]{Mod123Key.AR_C09},NONE)
	,R11 ("Deuda tributaria a ingresar",new Mod123Key[]{Mod123Key.AR_C10},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123Araba2016Script(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == R01 || this == R02 || this == R03);
	}
	@Override
	public boolean paintHeaderBefore() {
		return (this == R04);
	};
}
