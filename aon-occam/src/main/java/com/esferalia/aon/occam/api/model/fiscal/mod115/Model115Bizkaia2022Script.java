package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model115Bizkaia2022Script implements IModelScript<Mod115Key> {
	
	 R00 ("Rendimientos dinerarios",new Mod115Key[]{Mod115Key.BZ_C01,Mod115Key.BZ_C02,Mod115Key.BZ_C03},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R01 ("Rendimientos en especie",new Mod115Key[]{Mod115Key.BZ_C04,Mod115Key.BZ_C05,Mod115Key.BZ_C06},NONE)
	,R02 ("A Ingresar",new Mod115Key[]{Mod115Key.BZ_C07},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model115Bizkaia2022Script(String label, Mod115Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
