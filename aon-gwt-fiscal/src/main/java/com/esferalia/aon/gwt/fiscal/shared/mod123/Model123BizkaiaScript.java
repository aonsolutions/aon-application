package com.esferalia.aon.gwt.fiscal.shared.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model123BizkaiaScript implements IModelScript<Mod123Key> {
	
	 R00 ("Retenciones e ingresos a cuenta",new Mod123Key[]{Mod123Key.BZ_C01,Mod123Key.BZ_C02,Mod123Key.BZ_C03},INVOICE,DIFF_INVOICE)
	,R01 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.BZ_C04},NONE)
	,R02 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.BZ_C05},NONE)
	,R03 ("A Ingresar",new Mod123Key[]{Mod123Key.BZ_C06},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123BizkaiaScript(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
