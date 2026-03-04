package com.esferalia.aon.occam.api.model.fiscal.mod421;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public enum Model4212026ATCPrintScript implements IModelScript<Mod421Key> {
	
	  RES06(Mod421Key.C06.getDescription(), new Mod421Key[]{Mod421Key.C06})
	 ,RES07(Mod421Key.C07.getDescription(), new Mod421Key[]{Mod421Key.C07})
	 ,RES08(Mod421Key.C08.getDescription(), new Mod421Key[]{Mod421Key.C08})
	 ,RES09(Mod421Key.C09.getDescription(), new Mod421Key[]{Mod421Key.C09})
	 ,RES10(Mod421Key.C10.getDescription(), new Mod421Key[]{Mod421Key.C10})
	 ,RES11(Mod421Key.C11.getDescription(), new Mod421Key[]{Mod421Key.C11})
	 ,RES12(Mod421Key.C12.getDescription(), new Mod421Key[]{Mod421Key.C12})
	 ,RES13(Mod421Key.C13.getDescription(), new Mod421Key[]{Mod421Key.C13})
	 ,RES14(Mod421Key.C14.getDescription(), new Mod421Key[]{Mod421Key.C14})
	 ,RES15(Mod421Key.C15.getDescription(), new Mod421Key[]{Mod421Key.C15})
	 ,RES16(Mod421Key.C16.getDescription(), new Mod421Key[]{Mod421Key.C16})
	 ,RES17(Mod421Key.C17.getDescription(), new Mod421Key[]{Mod421Key.C17})
	 ,RES18(Mod421Key.C18.getDescription(), new Mod421Key[]{Mod421Key.C18})
	 ,RES19(Mod421Key.C19.getDescription(), new Mod421Key[]{Mod421Key.C19})
	;
	
	private String label;
	private Mod421Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model4212026ATCPrintScript(String label, Mod421Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

//	private static String getDescription(Mod421Key key) {
//		return AonStringUtils.trim(AonStringUtils.substringBefore(key.getDescription(), AonStringUtils.HYPHEN));
//	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod421Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] == TITLE;
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
		return (this == RES06);
	}
}
