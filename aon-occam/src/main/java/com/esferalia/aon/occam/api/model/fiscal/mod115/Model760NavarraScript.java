package com.esferalia.aon.occam.api.model.fiscal.mod115;


import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public enum Model760NavarraScript implements IModelScript<Mod115Key> {
	
	 R00 ("Deuda tributaria a ingresar",new Mod115Key[]{Mod115Key.NF_C01},FiscalModelKeyInfo.NONE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model760NavarraScript(String label, Mod115Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
