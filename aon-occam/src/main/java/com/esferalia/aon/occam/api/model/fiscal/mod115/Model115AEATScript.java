package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public enum Model115AEATScript implements IModelScript<Mod115Key> {
	
	 R00 ("Retenciones e ingresos a cuenta",new Mod115Key[]{Mod115Key.CT_C01,Mod115Key.CT_C02,Mod115Key.CT_C03},NONE)
	,R01 ("A deducir. Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo." 
			,new Mod115Key[]{Mod115Key.CT_C04},COMPUTE_KEY)
	,R02 ("Resultado a ingresar",new Mod115Key[]{Mod115Key.CT_C05},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model115AEATScript(String label, Mod115Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
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
