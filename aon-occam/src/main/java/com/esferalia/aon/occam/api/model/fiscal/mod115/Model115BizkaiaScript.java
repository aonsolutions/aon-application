package com.esferalia.aon.occam.api.model.fiscal.mod115;

import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod115KeyInfo.NONE;

import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;

public enum Model115BizkaiaScript implements IModelScript {
	
	 R00 ("Rendimientos dinerarios",null, null)
	,R01 ("N\u00AA arrendadores",new Mod115Key[]{Mod115Key.BZ_C01},INVOICE)
	,R02 ("Importe de los arrendamientos",new Mod115Key[]{Mod115Key.BZ_C02},INVOICE)
	,R03 ("Importe de las retenciones",new Mod115Key[]{Mod115Key.BZ_C03},INVOICE)
	,R04 ("Rendimientos en especie",null, null)
	,R05 ("N\u00AA arrendadores",new Mod115Key[]{Mod115Key.BZ_C04},NONE)
	,R06 ("Importe de los arrendamientos",new Mod115Key[]{Mod115Key.BZ_C05},NONE)
	,R07 ("Importe de las retenciones",new Mod115Key[]{Mod115Key.BZ_C06},NONE)
	,R08 ("A Ingresar",new Mod115Key[]{Mod115Key.BZ_C07},COMPUTE)
	;
	
	private String label;
	private Mod115Key[] keys;
	private Mod115KeyInfo infoKey;
	
	private Model115BizkaiaScript(String label, Mod115Key[] keys,Mod115KeyInfo infoKey) {
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
		return getInfoKey() != COMPUTE;
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
