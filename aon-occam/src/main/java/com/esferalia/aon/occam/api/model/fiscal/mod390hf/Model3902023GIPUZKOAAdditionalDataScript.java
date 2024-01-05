package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902023GIPUZKOAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("Compras de bienes corrientes",null,TITLE)
	,DVG02 (null					,new Mod390Key[]{Mod390Key.GP_C046,Mod390Key.GP_X046,Mod390Key.GP_C047,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 (null					,new Mod390Key[]{Mod390Key.GP_C048,Mod390Key.GP_X048,Mod390Key.GP_C049,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 (null					,new Mod390Key[]{Mod390Key.GP_C119,Mod390Key.GP_X119,Mod390Key.GP_C119,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)	
	,DVG05 (null					,new Mod390Key[]{Mod390Key.GP_C050,Mod390Key.GP_X050,Mod390Key.GP_C051,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 (null					,new Mod390Key[]{Mod390Key.GP_C121,Mod390Key.GP_X121,null			  ,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 (null					,new Mod390Key[]{Mod390Key.GP_C052,null				,Mod390Key.GP_C053,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 (null					,new Mod390Key[]{Mod390Key.GP_C054,null				,Mod390Key.GP_C055,Mod390Key.GP_C056},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)

	,DVG09 ("Gastos",null,TITLE)
	,DVG10 (null					,new Mod390Key[]{Mod390Key.GP_C057,Mod390Key.GP_X057,Mod390Key.GP_C058,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 (null					,new Mod390Key[]{Mod390Key.GP_C059,Mod390Key.GP_X059,Mod390Key.GP_C060,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG12 (null					,new Mod390Key[]{Mod390Key.GP_C122,Mod390Key.GP_X122,Mod390Key.GP_C123,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 (null					,new Mod390Key[]{Mod390Key.GP_C061,Mod390Key.GP_X061,Mod390Key.GP_C062,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 (null					,new Mod390Key[]{Mod390Key.GP_C124,Mod390Key.GP_X124,null			  ,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 (null					,new Mod390Key[]{Mod390Key.GP_C063,null				,Mod390Key.GP_C064,Mod390Key.GP_C065},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)

	,DVG16 ("Bienes de inversi\u00F3n",null,TITLE)
	,DVG17 (null					,new Mod390Key[]{Mod390Key.GP_C066,Mod390Key.GP_X066,Mod390Key.GP_C067,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG18 (null					,new Mod390Key[]{Mod390Key.GP_C068,Mod390Key.GP_X068,Mod390Key.GP_C069,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG19 (null					,new Mod390Key[]{Mod390Key.GP_C125,Mod390Key.GP_X125,Mod390Key.GP_C126,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG20 (null					,new Mod390Key[]{Mod390Key.GP_C070,Mod390Key.GP_X070,Mod390Key.GP_C071,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG21 (null					,new Mod390Key[]{Mod390Key.GP_C127,Mod390Key.GP_X127,null			  ,null				},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG22 (null					,new Mod390Key[]{Mod390Key.GP_C072,null				,Mod390Key.GP_C073,Mod390Key.GP_C074},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG23 ("Totales"				,new Mod390Key[]{Mod390Key.GP_C075,null				,Mod390Key.GP_C076,Mod390Key.GP_C077},COMPUTE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902023GIPUZKOAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod390Key.GP_X046 
		 || key == Mod390Key.GP_X048 
		 || key == Mod390Key.GP_X119 
		 || key == Mod390Key.GP_X050
		 || key == Mod390Key.GP_X121 
		 || key == Mod390Key.GP_X057 
		 || key == Mod390Key.GP_X059
		 || key == Mod390Key.GP_X122
		 || key == Mod390Key.GP_X061
		 || key == Mod390Key.GP_X124
		 || key == Mod390Key.GP_X066 
		 || key == Mod390Key.GP_X068 
		 || key == Mod390Key.GP_X125
		 || key == Mod390Key.GP_X070
		 || key == Mod390Key.GP_X127) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.GP_X046 
		 || key == Mod390Key.GP_X048 
		 || key == Mod390Key.GP_X119 
		 || key == Mod390Key.GP_X050
		 || key == Mod390Key.GP_X121 
		 || key == Mod390Key.GP_X057 
		 || key == Mod390Key.GP_X059
		 || key == Mod390Key.GP_X122
		 || key == Mod390Key.GP_X061
		 || key == Mod390Key.GP_X124
		 || key == Mod390Key.GP_X066 
		 || key == Mod390Key.GP_X068 
		 || key == Mod390Key.GP_X125
		 || key == Mod390Key.GP_X070
		 || key == Mod390Key.GP_X127) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


