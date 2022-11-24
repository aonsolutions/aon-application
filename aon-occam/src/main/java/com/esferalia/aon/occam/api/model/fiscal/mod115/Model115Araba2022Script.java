package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;


public enum Model115Araba2022Script implements IModelScript<Mod115Key> {
	
	 R01 ("\u00BFHa sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?"
			,new Mod115Key[]{Mod115Key.AR_907},NONE)
	,R02 ("Si se ha dictado auto de declaraci\u00F3n de concurso en este per\u00EDodo, indique el tipo de autoliquidaci\u00F3n"
			,new Mod115Key[]{Mod115Key.AR_908},NONE)
	,R03 ("Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso",new Mod115Key[]{Mod115Key.AR_909},NONE)
	,R04 ("Rendimientos dinerarios",new Mod115Key[]{Mod115Key.AR_C01,Mod115Key.AR_C02,Mod115Key.AR_C03},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R05 ("Rendimientos en especie",new Mod115Key[]{Mod115Key.AR_C04,Mod115Key.AR_C05,Mod115Key.AR_C06},NONE)
	,R06 ("A Ingresar",new Mod115Key[]{Mod115Key.AR_C07},COMPUTE)
	,R07 ("Ajustes",new Mod115Key[]{Mod115Key.AR_C08},COMPUTE_KEY)
	,R08 ("Recargo pr\u00F3rroga",new Mod115Key[]{Mod115Key.AR_C09},NONE)
	,R09 ("Intereses de demora",new Mod115Key[]{Mod115Key.AR_C10},NONE)
	,R10 ("Deuda tributaria a ingresar",new Mod115Key[]{Mod115Key.AR_C11},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model115Araba2022Script(String label, Mod115Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod115Key[] getKeys() {
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
