package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model115GipuzkoaScript implements IModelScript<Mod115Key> {
	
	 R00 ("Rendimientos dinerarios",new Mod115Key[]{Mod115Key.GP_C01,Mod115Key.GP_C02,Mod115Key.GP_C03},INVOICE,DIFF_INVOICE)
	,R01 ("Rendimientos en especie",new Mod115Key[]{Mod115Key.GP_C04,Mod115Key.GP_C05,Mod115Key.GP_C06},NONE)
	,R02 ("A Ingresar",new Mod115Key[]{Mod115Key.GP_C07},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model115GipuzkoaScript(String label, Mod115Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
