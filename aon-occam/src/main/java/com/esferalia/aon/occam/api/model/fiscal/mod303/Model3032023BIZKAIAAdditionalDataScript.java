package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032023BIZKAIAAdditionalDataScript implements IModelScript<Mod303Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("Compras de bienes corrientes",null,TITLE)
	,DVG02 (null ,new Mod303Key[]{Mod303Key.BZ_C050,Mod303Key.BZ_X050,Mod303Key.BZ_C051,Mod303Key.BZ_C052},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 (null ,new Mod303Key[]{Mod303Key.BZ_C112,Mod303Key.BZ_X112,Mod303Key.BZ_C113,Mod303Key.BZ_C114},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 (null ,new Mod303Key[]{Mod303Key.BZ_C053,Mod303Key.BZ_X053,Mod303Key.BZ_C054,Mod303Key.BZ_C055},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG05 (null ,new Mod303Key[]{Mod303Key.BZ_C056,Mod303Key.BZ_X056,Mod303Key.BZ_C057,Mod303Key.BZ_C058},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 (null ,new Mod303Key[]{Mod303Key.BZ_C059,null			 ,Mod303Key.BZ_C060,Mod303Key.BZ_C061},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 (null ,new Mod303Key[]{Mod303Key.BZ_C062,null			 ,Mod303Key.BZ_C063,Mod303Key.BZ_C064},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 (null ,new Mod303Key[]{Mod303Key.BZ_C065,null			 ,Mod303Key.BZ_C066,Mod303Key.BZ_C067},COMPUTE)

	,DVG09 ("Gastos",null,TITLE)
	,DVG10 (null ,new Mod303Key[]{Mod303Key.BZ_C068,Mod303Key.BZ_X068,Mod303Key.BZ_C069,Mod303Key.BZ_C070},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 (null ,new Mod303Key[]{Mod303Key.BZ_C115,Mod303Key.BZ_X115,Mod303Key.BZ_C116,Mod303Key.BZ_C117},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG12 (null ,new Mod303Key[]{Mod303Key.BZ_C071,Mod303Key.BZ_X071,Mod303Key.BZ_C072,Mod303Key.BZ_C073},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 (null ,new Mod303Key[]{Mod303Key.BZ_C074,Mod303Key.BZ_X074,Mod303Key.BZ_C075,Mod303Key.BZ_C076},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 (null ,new Mod303Key[]{Mod303Key.BZ_C077,null			 ,Mod303Key.BZ_C078,Mod303Key.BZ_C079},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 (null ,new Mod303Key[]{Mod303Key.BZ_C080,null			 ,Mod303Key.BZ_C081,Mod303Key.BZ_C082},COMPUTE)
	
	,DVG16 ("Bienes de inversi\u00F3n",null,TITLE)
	,DVG17 (null					,new Mod303Key[]{Mod303Key.BZ_C083,Mod303Key.BZ_X083,Mod303Key.BZ_C084,Mod303Key.BZ_C085},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG18 (null					,new Mod303Key[]{Mod303Key.BZ_C086,Mod303Key.BZ_X086,Mod303Key.BZ_C087,Mod303Key.BZ_C088},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG19 (null					,new Mod303Key[]{Mod303Key.BZ_C089,Mod303Key.BZ_X089,Mod303Key.BZ_C090,Mod303Key.BZ_C091},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG20 (null					,new Mod303Key[]{Mod303Key.BZ_C092,null				,Mod303Key.BZ_C093,Mod303Key.BZ_C094},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG21 (null					,new Mod303Key[]{Mod303Key.BZ_C095,null				,Mod303Key.BZ_C096,Mod303Key.BZ_C097},COMPUTE)
	
	,DVG22 ("Totales"				,new Mod303Key[]{Mod303Key.BZ_C098,null				,Mod303Key.BZ_C099,Mod303Key.BZ_C100},COMPUTE)
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032023BIZKAIAAdditionalDataScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return true;
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
		return (this == DVG01);
	}

	@Override
	public int getFieldSize(Mod303Key key) {
		if (key == Mod303Key.BZ_X050 
			|| key == Mod303Key.BZ_X112
			|| key == Mod303Key.BZ_X053 
			|| key == Mod303Key.BZ_X056
			|| key == Mod303Key.BZ_X068
			|| key == Mod303Key.BZ_X115
			|| key == Mod303Key.BZ_X071 
			|| key == Mod303Key.BZ_X074) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.BZ_X050
		 || key == Mod303Key.BZ_X112
		 || key == Mod303Key.BZ_X053 
		 || key == Mod303Key.BZ_X056
		 || key == Mod303Key.BZ_X068
		 || key == Mod303Key.BZ_X115
		 || key == Mod303Key.BZ_X071 
		 || key == Mod303Key.BZ_X074) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


