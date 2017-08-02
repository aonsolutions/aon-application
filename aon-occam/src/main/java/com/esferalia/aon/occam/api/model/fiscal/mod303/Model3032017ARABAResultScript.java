package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017ARABAResultScript implements IModelScript<Mod303Key> {
	 R000 ("Resultado",null,TITLE)
	,R001 (Mod303Key.AR_C040.getDescription(),new Mod303Key[]{Mod303Key.AR_C040},NONE)
	,R002 (Mod303Key.AR_C041.getDescription(),new Mod303Key[]{Mod303Key.AR_C041},NONE)
	,R003 (Mod303Key.AR_C042.getDescription(),new Mod303Key[]{Mod303Key.AR_C042},NONE)
	,R004 (Mod303Key.AR_C043.getDescription(),new Mod303Key[]{Mod303Key.AR_C043},NONE)
	,R005 (Mod303Key.AR_C044.getDescription(),new Mod303Key[]{Mod303Key.AR_C044},COMPUTE)
	,R006 (Mod303Key.AR_C045.getDescription(),new Mod303Key[]{Mod303Key.AR_C045},COMPUTE_KEY)
	,R007 (Mod303Key.AR_C060.getDescription(),new Mod303Key[]{Mod303Key.AR_C060},COMPUTE)
	,R008 (Mod303Key.AR_C061.getDescription(),new Mod303Key[]{Mod303Key.AR_C061},NONE)
	,R009 (Mod303Key.AR_C062.getDescription(),new Mod303Key[]{Mod303Key.AR_C062},NONE)
	,R010 (Mod303Key.AR_C063.getDescription(),new Mod303Key[]{Mod303Key.AR_C063},COMPUTE_KEY)
	,R011 (Mod303Key.AR_C080.getDescription(),new Mod303Key[]{Mod303Key.AR_C080},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017ARABAResultScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod303Key[] getKeys() {
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
