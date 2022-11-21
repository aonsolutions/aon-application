package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902022ARABAResultScript implements IModelScript<Mod390Key> {
	 R000 ("Resultado",null,TITLE)
	,R001 (Mod390Key.AR_C114.getDescription(),new Mod390Key[]{Mod390Key.AR_C114},COMPUTE)
	,R002 (Mod390Key.AR_C120.getDescription(),new Mod390Key[]{Mod390Key.AR_C120},NONE)
	,R003 (Mod390Key.AR_C121.getDescription(),new Mod390Key[]{Mod390Key.AR_C121},NONE)
	,R004 (Mod390Key.AR_C122.getDescription(),new Mod390Key[]{Mod390Key.AR_C122},NONE)
	,R005 (Mod390Key.AR_C123.getDescription(),new Mod390Key[]{Mod390Key.AR_C123},NONE)
	,R006 (Mod390Key.AR_C124.getDescription(),new Mod390Key[]{Mod390Key.AR_C124},NONE)
	,R007 (Mod390Key.AR_C125.getDescription(),new Mod390Key[]{Mod390Key.AR_C125},COMPUTE)
	,R008 (Mod390Key.AR_C126.getDescription(),new Mod390Key[]{Mod390Key.AR_C126},COMPUTE_KEY)
	,R009 (Mod390Key.AR_C127.getDescription(),new Mod390Key[]{Mod390Key.AR_C127},COMPUTE_KEY)
	,R010 (Mod390Key.AR_C128.getDescription(),new Mod390Key[]{Mod390Key.AR_C128},COMPUTE)
	,R011 (Mod390Key.AR_C129.getDescription(),new Mod390Key[]{Mod390Key.AR_C129},COMPUTE)
	,R012 (Mod390Key.AR_C130.getDescription(),new Mod390Key[]{Mod390Key.AR_C130},NONE)
	,R013 (Mod390Key.AR_C13X.getDescription(),new Mod390Key[]{Mod390Key.AR_C13X},COMPUTE)
	,R017 (Mod390Key.AR_C134.getDescription(),new Mod390Key[]{Mod390Key.AR_C134},NONE)
	,R018 (Mod390Key.AR_C135.getDescription(),new Mod390Key[]{Mod390Key.AR_C135},NONE)
	,R019 (Mod390Key.AR_C140.getDescription(),new Mod390Key[]{Mod390Key.AR_C140},COMPUTE)
	,R020 (Mod390Key.AR_C141.getDescription(),new Mod390Key[]{Mod390Key.AR_C141},COMPUTE)
	,R021 (Mod390Key.AR_C142.getDescription(),new Mod390Key[]{Mod390Key.AR_C142},COMPUTE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902022ARABAResultScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod390Key[] getKeys() {
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
	};
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	};

}
