package com.esferalia.aon.occam.api.model.fiscal.mod421;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public enum Model4212026ATCResultScript implements IModelScript<Mod421Key> {
	
	 RES12(Mod421Key.C12.getDescription(), new Mod421Key[]{Mod421Key.C12}, MODEL_INVOICE_VAT_BREAKDOWN)
	,RES13(Mod421Key.C13.getDescription(), new Mod421Key[]{Mod421Key.C13}, NONE)
	,RES14(Mod421Key.C14.getDescription(), new Mod421Key[]{Mod421Key.C14}, NONE)
	,RES15(Mod421Key.C15.getDescription(), new Mod421Key[]{Mod421Key.C15}, MODEL_INVOICE_VAT_BREAKDOWN)
	,RES16(Mod421Key.C16.getDescription(), new Mod421Key[]{Mod421Key.C16}, NONE)
	,RES17(Mod421Key.C17.getDescription(), new Mod421Key[]{Mod421Key.C17}, COMPUTE_KEY)
	,RES18(Mod421Key.C18.getDescription(), new Mod421Key[]{Mod421Key.C18}, COMPUTE_KEY)
	,RES19(Mod421Key.C19.getDescription(), new Mod421Key[]{Mod421Key.C19}, COMPUTE)
	;
	
	private String label;
	private Mod421Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model4212026ATCResultScript(String label, Mod421Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

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
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
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
		return false;
	}

}
