package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public enum Model715NavarraScript implements IModelScript {
	
	 R00 ("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.NF_A1},NONE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private Mod111KeyInfo infoKey;
	
	private Model715NavarraScript(String label, Mod111Key[] keys,Mod111KeyInfo infoKey) {
		this.label = label;
		this.keys = keys;
		this.infoKey = infoKey;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod111Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKey() != COMPUTE;
	}
	@Override
	public Mod111KeyInfo getInfoKey() {
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
