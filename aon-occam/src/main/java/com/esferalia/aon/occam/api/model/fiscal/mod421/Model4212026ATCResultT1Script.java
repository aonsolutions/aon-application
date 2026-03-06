package com.esferalia.aon.occam.api.model.fiscal.mod421;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public enum Model4212026ATCResultT1Script implements IModelScript<Mod421Key> {
	
	 R00 ("AUTOLIQUIDACIONES ANTERIORES A LA \u00DALTIMA DEL EJERCICIO",null,TITLE)
	,RES06(Mod421Key.C06.getDescription(), new Mod421Key[]{null, null, null, Mod421Key.C06}, COMPUTE)
	;
	
	private String label;
	private Mod421Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model4212026ATCResultT1Script(String label, Mod421Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod421Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
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
		return false;
	}

}
