package com.esferalia.aon.occam.api.model.fiscal.mod202;


import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public enum Model2022025AddInfoAEATScript implements IModelScript<Mod202Key> {
	
	 AI 	("Informaci\u00F3n adicional",null,TITLE)
	,AIA01 	(Mod202Key.A01.getDescription(),new Mod202Key[]{Mod202Key.A01},NONE)
	,AIA02 	(Mod202Key.A02.getDescription(),new Mod202Key[]{Mod202Key.A02},NONE)
	,AIA03	(Mod202Key.A03.getDescription(),new Mod202Key[]{Mod202Key.A03},NONE)
	,AIA04	(Mod202Key.A04.getDescription(),new Mod202Key[]{Mod202Key.A04},NONE)
	,AIA05	(Mod202Key.A05.getDescription(),new Mod202Key[]{Mod202Key.A05},NONE)
	,AIA06	(Mod202Key.A06.getDescription(),new Mod202Key[]{Mod202Key.A06},NONE)
	,AIA07	(Mod202Key.A07.getDescription(),new Mod202Key[]{Mod202Key.A07},NONE)
	,AIA08	(Mod202Key.A08.getDescription(),new Mod202Key[]{Mod202Key.A08},NONE)
	,AIA09	(Mod202Key.A09.getDescription(),new Mod202Key[]{Mod202Key.A09},NONE)
	,AIA10	(Mod202Key.A10.getDescription(),new Mod202Key[]{Mod202Key.A10},NONE)
	,AIA11	(Mod202Key.A11.getDescription(),new Mod202Key[]{Mod202Key.A11},NONE)
	,AIA12	(Mod202Key.A12.getDescription(),new Mod202Key[]{Mod202Key.A12},NONE)
	,AIA13	(Mod202Key.A13.getDescription(),new Mod202Key[]{Mod202Key.A13},NONE)
	;
	
	private String label;
	private Mod202Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model2022025AddInfoAEATScript(String label, Mod202Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod202Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	};
	
	@Override
	public boolean hasGraphicParticularity() {
		return (this == AIA01 || this == AIA02);
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	};
}
