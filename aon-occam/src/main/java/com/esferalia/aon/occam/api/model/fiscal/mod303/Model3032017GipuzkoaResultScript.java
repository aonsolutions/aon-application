package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017GipuzkoaResultScript implements IModelScript<Mod303Key> {
	 R000 ("Resultado",null,TITLE)
	,R001 (Mod303Key.GP_C026.getDescription(),new Mod303Key[]{Mod303Key.GP_C026},COMPUTE)
	,R002 (Mod303Key.GP_C027.getDescription(),new Mod303Key[]{Mod303Key.GP_C027},NONE)
	,R003 (Mod303Key.GP_C028.getDescription(),new Mod303Key[]{Mod303Key.GP_C028},COMPUTE)
	,R004 (Mod303Key.GP_C029.getDescription(),new Mod303Key[]{Mod303Key.GP_C029},COMPUTE_KEY)
	,R005 (Mod303Key.GP_C035.getDescription(),new Mod303Key[]{Mod303Key.GP_C035},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017GipuzkoaResultScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
