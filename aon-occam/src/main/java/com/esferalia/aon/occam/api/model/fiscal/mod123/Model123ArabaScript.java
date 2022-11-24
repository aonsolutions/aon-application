package com.esferalia.aon.occam.api.model.fiscal.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public enum Model123ArabaScript implements IModelScript<Mod123Key> {
	
	 R00 ("Retenciones e ingresos a cuenta",new Mod123Key[]{Mod123Key.AR_C01,Mod123Key.AR_C02,Mod123Key.AR_C03},NONE)
	,R01 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.AR_C04},NONE)
	,R02 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.AR_C05},NONE)
	,R03 ("Suma de retenciones e ingresos a cuenta.",new Mod123Key[]{Mod123Key.AR_C06},COMPUTE)
	,R04 ("Ajustes",new Mod123Key[]{Mod123Key.AR_C07},NONE)
	,R05 ("Recargo pr\u00F3rroga",new Mod123Key[]{Mod123Key.AR_C08},NONE)
	,R06 ("Intereses de demora",new Mod123Key[]{Mod123Key.AR_C09},NONE)
	,R07 ("Deuda tributaria a ingresar",new Mod123Key[]{Mod123Key.AR_C10},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123ArabaScript(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
