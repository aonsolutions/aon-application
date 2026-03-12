package com.esferalia.aon.occam.api.model.fiscal.mod421;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public enum Model4212026ATCResultT4Script implements IModelScript<Mod421Key> {
	
	 R00 ("AUTOLIQUIDACI\u00D3N CORRESPONDIENTE AL \u00DALTIMO PER\u00CDODO DE LIQUIDACI\u00D3N TRIMESTRAL DEL EJERCICIO",null,TITLE)
	,RES07(Mod421Key.C07.getDescription(), new Mod421Key[]{null, null, null, Mod421Key.C07}, COMPUTE)
	,RES08(Mod421Key.C08.getDescription(), new Mod421Key[]{null, null, null, Mod421Key.C08}, COMPUTE)
	,RES09(Mod421Key.C09.getDescription(), new Mod421Key[]{null, null, null, Mod421Key.C09}, COMPUTE)
	,RES10(Mod421Key.C10.getDescription(), new Mod421Key[]{Mod421Key.C10T1, Mod421Key.C10T2, Mod421Key.C10T3, Mod421Key.C10}, COMPUTE_KEY)
	,RES11(Mod421Key.C11.getDescription(), new Mod421Key[]{null, null, null, Mod421Key.C11}, COMPUTE)
	;
	
	private String label;
	private Mod421Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model4212026ATCResultT4Script(String label, Mod421Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	public boolean isEnabled(Mod421Key key) {
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE && key != Mod421Key.C10;
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
