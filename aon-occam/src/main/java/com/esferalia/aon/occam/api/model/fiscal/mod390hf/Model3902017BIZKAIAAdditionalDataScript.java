package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017BIZKAIAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("Compras de bienes corrientes",null,TITLE)
	,DVG02 (null					,new Mod390Key[]{Mod390Key.BZ_C142,Mod390Key.BZ_X142,Mod390Key.BZ_C143,Mod390Key.BZ_C144},NONE)
	,DVG03 (null					,new Mod390Key[]{Mod390Key.BZ_C145,Mod390Key.BZ_X145,Mod390Key.BZ_C146,Mod390Key.BZ_C147},NONE)
	,DVG04 (null					,new Mod390Key[]{Mod390Key.BZ_C148,Mod390Key.BZ_X148,Mod390Key.BZ_C149,Mod390Key.BZ_C150},NONE)
	,DVG05 (null					,new Mod390Key[]{Mod390Key.BZ_C151,null				,Mod390Key.BZ_C152,Mod390Key.BZ_C153},NONE)
	,DVG06 (null					,new Mod390Key[]{Mod390Key.BZ_C154,null				,Mod390Key.BZ_C155,Mod390Key.BZ_C156},NONE)
	,DVG07 (null					,new Mod390Key[]{Mod390Key.BZ_C157,null				,Mod390Key.BZ_C158,Mod390Key.BZ_C159},COMPUTE)

	,DVG08 ("Gastos",null,TITLE)
	,DVG09 (null					,new Mod390Key[]{Mod390Key.BZ_C160,Mod390Key.BZ_X160,Mod390Key.BZ_C161,Mod390Key.BZ_C162},NONE)
	,DVG10 (null					,new Mod390Key[]{Mod390Key.BZ_C163,Mod390Key.BZ_X163,Mod390Key.BZ_C164,Mod390Key.BZ_C165},NONE)
	,DVG11 (null					,new Mod390Key[]{Mod390Key.BZ_C166,Mod390Key.BZ_X166,Mod390Key.BZ_C167,Mod390Key.BZ_C168},NONE)
	,DVG13 (null					,new Mod390Key[]{Mod390Key.BZ_C169,null				,Mod390Key.BZ_C170,Mod390Key.BZ_C171},NONE)
	,DVG14 (null					,new Mod390Key[]{Mod390Key.BZ_C172,null				,Mod390Key.BZ_C173,Mod390Key.BZ_C174},COMPUTE)
	
	,DVG15 ("Bienes de inversi\u00F3n",null,TITLE)
	,DVG16 (null					,new Mod390Key[]{Mod390Key.BZ_C175,Mod390Key.BZ_X175,Mod390Key.BZ_C176,Mod390Key.BZ_C177},NONE)
	,DVG17 (null					,new Mod390Key[]{Mod390Key.BZ_C178,Mod390Key.BZ_X178,Mod390Key.BZ_C179,Mod390Key.BZ_C180},NONE)
	,DVG18 (null					,new Mod390Key[]{Mod390Key.BZ_C181,Mod390Key.BZ_X181,Mod390Key.BZ_C182,Mod390Key.BZ_C183},NONE)
	,DVG19 (null					,new Mod390Key[]{Mod390Key.BZ_C184,null				,Mod390Key.BZ_C185,Mod390Key.BZ_C186},NONE)
	,DVG20 (null					,new Mod390Key[]{Mod390Key.BZ_C187,null				,Mod390Key.BZ_C188,Mod390Key.BZ_C189},COMPUTE)
	
	,DVG21 ("Totales"				,new Mod390Key[]{Mod390Key.BZ_C190,null				,Mod390Key.BZ_C191,Mod390Key.BZ_C192},COMPUTE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017BIZKAIAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return true;
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
		return (this == DVG01);
	};

	@Override
	public int getFieldSize(Mod390Key key) {
		if (key == Mod390Key.BZ_X142 || key == Mod390Key.BZ_X145 || key == Mod390Key.BZ_X148
		 || key == Mod390Key.BZ_X160 || key == Mod390Key.BZ_X163 || key == Mod390Key.BZ_X166
		 || key == Mod390Key.BZ_X175 || key == Mod390Key.BZ_X178 || key == Mod390Key.BZ_X181) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.BZ_X142 || key == Mod390Key.BZ_X145 || key == Mod390Key.BZ_X148
		 || key == Mod390Key.BZ_X160 || key == Mod390Key.BZ_X163 || key == Mod390Key.BZ_X166
		 || key == Mod390Key.BZ_X175 || key == Mod390Key.BZ_X178 || key == Mod390Key.BZ_X181) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


