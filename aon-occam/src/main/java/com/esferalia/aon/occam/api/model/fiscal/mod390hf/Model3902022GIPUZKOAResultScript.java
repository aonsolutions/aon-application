package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902022GIPUZKOAResultScript implements IModelScript<Mod390Key> {
	 R000 ("Resultado",null,TITLE)
	,R001 (Mod390Key.GP_C030.getDescription(),new Mod390Key[]{Mod390Key.GP_C030},COMPUTE)
	,R002 (Mod390Key.GP_C031.getDescription(),new Mod390Key[]{Mod390Key.GP_C031},NONE)
	,R003 (Mod390Key.GP_C032.getDescription(),new Mod390Key[]{Mod390Key.GP_C032},NONE)
	,R004 (Mod390Key.GP_C033.getDescription(),new Mod390Key[]{Mod390Key.GP_C033},NONE)
	,R005 (Mod390Key.GP_C034.getDescription(),new Mod390Key[]{Mod390Key.GP_C034},NONE)
	,R006 (Mod390Key.GP_C035.getDescription(),new Mod390Key[]{Mod390Key.GP_C035},COMPUTE_KEY)
	
	,R007 (Mod390Key.GP_C036.getDescription(),new Mod390Key[]{Mod390Key.GP_C036},COMPUTE)
	,R008 (Mod390Key.GP_C037.getDescription(),new Mod390Key[]{Mod390Key.GP_C037},NONE)
	,R009 (Mod390Key.GP_C038.getDescription(),new Mod390Key[]{Mod390Key.GP_C038},NONE)
	,R010 (Mod390Key.GP_C039.getDescription(),new Mod390Key[]{Mod390Key.GP_C039},NONE)
	,R011 (Mod390Key.GP_C040.getDescription(),new Mod390Key[]{Mod390Key.GP_C040},COMPUTE)
	,R012 (Mod390Key.GP_C041.getDescription(),new Mod390Key[]{Mod390Key.GP_C041},COMPUTE)
	,R013 (Mod390Key.GP_C042.getDescription(),new Mod390Key[]{Mod390Key.GP_C042},COMPUTE)
	,R014 (Mod390Key.GP_C043.getDescription(),new Mod390Key[]{Mod390Key.GP_C043},COMPUTE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902022GIPUZKOAResultScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
