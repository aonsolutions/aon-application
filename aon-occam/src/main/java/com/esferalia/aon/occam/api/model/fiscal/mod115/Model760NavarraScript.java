package com.esferalia.aon.occam.api.model.fiscal.mod115;


import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;

public enum Model760NavarraScript implements IModelScript {
	
	 R00 ("Deuda tributaria a ingresar",new Mod115Key[]{Mod115Key.NF_C01},Mod115KeyInfo.NONE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private Mod115KeyInfo infoKey;
	
	private Model760NavarraScript(String label, Mod115Key[] keys,Mod115KeyInfo infoKey) {
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
	public boolean isEnabled() {
		return getInfoKey() != Mod115KeyInfo.COMPUTE;
	}
	@Override
	public Mod115KeyInfo getInfoKey() {
		return infoKey;
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
