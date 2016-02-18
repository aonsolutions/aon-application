package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.NONE;

import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;

public enum Model115AEATScript implements IModelScript {
	
	 R00 ("Retenciones e ingresos a cuenta",null, null)
	,R01 ("N\u00AA perceptores",new Mod115Key[]{Mod115Key.CT_C01},INVOICE)
	,R02 ("Base de las retenciones e ingresos a cuenta",new Mod115Key[]{Mod115Key.CT_C02},INVOICE)
	,R03 ("Retenciones e ingresos a cuenta",new Mod115Key[]{Mod115Key.CT_C03},INVOICE)
	,R04 ("Rendimientos dinerarios" ,new Mod115Key[]{Mod115Key.CT_C04},NONE)
	,R05 ("Resultado a ingresar",new Mod115Key[]{Mod115Key.CT_C05},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private Mod115KeyInfo infoKey;
	
	private Model115AEATScript(String label, Mod115Key[] keys,Mod115KeyInfo infoKey) {
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
		return (getInfoKey() != null && getInfoKey() != COMPUTE);
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
