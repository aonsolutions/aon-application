package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.NONE;

import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;


public enum Model115Araba2016Script implements IModelScript {
	
	 R01 ("\u00BFHa sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?"
			,new Mod115Key[]{Mod115Key.AR_907},NONE)
	,R02 ("Si se ha dictado auto de declaraci\u00F3n de concurso en este per\u00EDodo, indique el tipo de autoliquidaci\u00F3n"
			,new Mod115Key[]{Mod115Key.AR_908},NONE)
	,R03 ("Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso",new Mod115Key[]{Mod115Key.AR_909},NONE)
	,R04 ("Rendimientos dinerarios",null, null)
	,R05 ("N\u00AA arrendadores",new Mod115Key[]{Mod115Key.AR_C01},INVOICE)
	,R06 ("Importe de los arrendamientos",new Mod115Key[]{Mod115Key.AR_C02},INVOICE)
	,R07 ("Importe de las retenciones",new Mod115Key[]{Mod115Key.AR_C03},INVOICE)
	,R08 ("Rendimientos en especie",null, null)
	,R09 ("N\u00AA arrendadores",new Mod115Key[]{Mod115Key.AR_C04},NONE)
	,R10 ("Importe de los arrendamientos",new Mod115Key[]{Mod115Key.AR_C05},NONE)
	,R11 ("Importe de las retenciones",new Mod115Key[]{Mod115Key.AR_C06},NONE)
	,R12 ("A Ingresar",new Mod115Key[]{Mod115Key.AR_C07},COMPUTE)
	,R13 ("Ajustes",new Mod115Key[]{Mod115Key.AR_C08},NONE)
	,R14 ("Recargo pr\u00F3rroga",new Mod115Key[]{Mod115Key.AR_C09},NONE)
	,R15 ("Intereses de demora",new Mod115Key[]{Mod115Key.AR_C10},NONE)
	,R16 ("Deuda tributaria a ingresar",new Mod115Key[]{Mod115Key.AR_C11},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private Mod115KeyInfo infoKey;
	
	private Model115Araba2016Script(String label, Mod115Key[] keys,Mod115KeyInfo infoKey) {
		this.label = label;
		this.keys = keys;
		this.infoKey = infoKey;
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
	public boolean hasGraphicParticularity() {
		return (this == R01 || this == R02 || this == R03);
	}
	
	@Override
	public boolean isEnabled() {
		return getInfoKey() != COMPUTE;
	}
	@Override
	public Mod115KeyInfo getInfoKey() {
		return infoKey;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R04);
	};
}
