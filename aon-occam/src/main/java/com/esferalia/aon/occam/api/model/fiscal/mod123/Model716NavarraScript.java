package com.esferalia.aon.occam.api.model.fiscal.mod123;


import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public enum Model716NavarraScript implements IModelScript<Mod123Key> {
	
	 R00 ("Deuda tributaria a ingresar",new Mod123Key[]{Mod123Key.NF_C01},FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model716NavarraScript(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return true;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	};
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
