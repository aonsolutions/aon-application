package com.esferalia.aon.gwt.fiscal.shared.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public enum Model123GipuzkoaScript implements IModelScript<Mod123Key> {
	
	 R00 ("Rentas o rendimientos dinerarios",new Mod123Key[]{Mod123Key.GP_C01,Mod123Key.GP_C02,Mod123Key.GP_C03},INVOICE,DIFF_INVOICE)
	,R01 ("Rentas o rendimientos en especie",new Mod123Key[]{Mod123Key.GP_C04,Mod123Key.GP_C05,Mod123Key.GP_C06},NONE)
	,R02 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.GP_C07},NONE)
	,R03 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.GP_C08},NONE)
	,R04 ("A Ingresar",new Mod123Key[]{Mod123Key.GP_C09},COMPUTE)
	,X00 ("NIF del Presentador telem\u00E1tico (en caso de ser diferente del declarante)",new Mod123Key[]{Mod123Key.GP_X00},NONE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123GipuzkoaScript(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == X00);
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
