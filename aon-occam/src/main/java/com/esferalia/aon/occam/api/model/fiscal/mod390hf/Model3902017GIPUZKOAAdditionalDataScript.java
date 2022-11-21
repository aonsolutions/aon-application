package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017GIPUZKOAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("Compras de bienes corrientes",null,TITLE)
	,DVG02 (null					,new Mod390Key[]{Mod390Key.GP_C046,Mod390Key.GP_X046,Mod390Key.GP_C047,null				},NONE)
	,DVG03 (null					,new Mod390Key[]{Mod390Key.GP_C048,Mod390Key.GP_X048,Mod390Key.GP_C049,null				},NONE)
	,DVG04 (null					,new Mod390Key[]{Mod390Key.GP_C050,Mod390Key.GP_X050,Mod390Key.GP_C051,null				},NONE)
	,DVG05 (null					,new Mod390Key[]{Mod390Key.GP_C052,null				,Mod390Key.GP_C053,null				},NONE)
	,DVG06 (null					,new Mod390Key[]{Mod390Key.GP_C054,null				,Mod390Key.GP_C055,Mod390Key.GP_C056},NONE)

	,DVG08 ("Gastos",null,TITLE)
	,DVG09 (null					,new Mod390Key[]{Mod390Key.GP_C057,Mod390Key.GP_X057,Mod390Key.GP_C058,null				},NONE)
	,DVG10 (null					,new Mod390Key[]{Mod390Key.GP_C059,Mod390Key.GP_X059,Mod390Key.GP_C060,null				},NONE)
	,DVG11 (null					,new Mod390Key[]{Mod390Key.GP_C061,Mod390Key.GP_X061,Mod390Key.GP_C062,null				},NONE)
	,DVG13 (null					,new Mod390Key[]{Mod390Key.GP_C063,null				,Mod390Key.GP_C064,Mod390Key.GP_C065},NONE)

	,DVG15 ("Bienes de inversi\u00F3n",null,TITLE)
	,DVG16 (null					,new Mod390Key[]{Mod390Key.GP_C066,Mod390Key.GP_X066,Mod390Key.GP_C067,null				},NONE)
	,DVG17 (null					,new Mod390Key[]{Mod390Key.GP_C068,Mod390Key.GP_X068,Mod390Key.GP_C069,null				},NONE)
	,DVG18 (null					,new Mod390Key[]{Mod390Key.GP_C070,Mod390Key.GP_X070,Mod390Key.GP_C071,null				},NONE)
	,DVG19 (null					,new Mod390Key[]{Mod390Key.GP_C072,null				,Mod390Key.GP_C073,Mod390Key.GP_C074},NONE)
	,DVG21 ("Totales"				,new Mod390Key[]{Mod390Key.GP_C075,null				,Mod390Key.GP_C076,Mod390Key.GP_C077},COMPUTE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017GIPUZKOAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod390Key.GP_X046 || key == Mod390Key.GP_X048 || key == Mod390Key.GP_X050
		 || key == Mod390Key.GP_X057 || key == Mod390Key.GP_X059 || key == Mod390Key.GP_X061
		 || key == Mod390Key.GP_X066 || key == Mod390Key.GP_X068 || key == Mod390Key.GP_X070 ) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.GP_X046 || key == Mod390Key.GP_X048 || key == Mod390Key.GP_X050
		 || key == Mod390Key.GP_X057 || key == Mod390Key.GP_X059 || key == Mod390Key.GP_X061
		 || key == Mod390Key.GP_X066 || key == Mod390Key.GP_X068 || key == Mod390Key.GP_X070 ) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


