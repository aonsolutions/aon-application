package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902022BIZKAIAScript1 implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("IVA DEVENGADO",null,TITLE)
	,DVG02 (null					,new Mod390Key[]{Mod390Key.BZ_C020	,Mod390Key.BZ_X020	,Mod390Key.BZ_C021},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.BZ_C022	,Mod390Key.BZ_X022	,Mod390Key.BZ_C023},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 (null					,new Mod390Key[]{Mod390Key.BZ_C024	,Mod390Key.BZ_X024	,Mod390Key.BZ_C025},MODEL_INVOICE_VAT_BREAKDOWN)

	,DVG05 (null					,new Mod390Key[]{Mod390Key.BZ_C026	,Mod390Key.BZ_X026	,Mod390Key.BZ_C027},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 ("Operaciones intragrupo",new Mod390Key[]{Mod390Key.BZ_C028	,Mod390Key.BZ_X028	,Mod390Key.BZ_C029},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 (null					,new Mod390Key[]{Mod390Key.BZ_C030	,Mod390Key.BZ_X030	,Mod390Key.BZ_C031},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG08 (null					,new Mod390Key[]{Mod390Key.BZ_C032	,Mod390Key.BZ_X032	,Mod390Key.BZ_C033},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.BZ_C034	,Mod390Key.BZ_X034	,Mod390Key.BZ_C035},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG10 (null					,new Mod390Key[]{Mod390Key.BZ_C036	,Mod390Key.BZ_X036	,Mod390Key.BZ_C037},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 (null					,new Mod390Key[]{Mod390Key.BZ_C038	,Mod390Key.BZ_X038	,Mod390Key.BZ_C039},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG12 ("Adquisiciones intracomunitarias"
		,new Mod390Key[]{Mod390Key.BZ_C040	,null	,Mod390Key.BZ_C041},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 ("IVA devengado por inversi\u00F3n del sujeto pasivo"
		,new Mod390Key[]{Mod390Key.BZ_C042	,null	,Mod390Key.BZ_C043},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 ("Modificaci\u00F3n bases y cuotas, general."
		,new Mod390Key[]{Mod390Key.BZ_C044	,null	,Mod390Key.BZ_C045},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 ("Modificaci\u00F3n bases y cuotas (art. 80.3 y 80.4 NFIVA)."
		,new Mod390Key[]{Mod390Key.BZ_C046	,null	,Mod390Key.BZ_C047},NONE)
	,DVG16 ("Total cuota devengada"	,new Mod390Key[]{Mod390Key.BZ_C048},COMPUTE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902022BIZKAIAScript1(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod390Key.BZ_X020 || key == Mod390Key.BZ_X022 || key == Mod390Key.BZ_X024
		 || key == Mod390Key.BZ_X026 || key == Mod390Key.BZ_X028 || key == Mod390Key.BZ_X030 
		 || key == Mod390Key.BZ_X032 || key == Mod390Key.BZ_X034 || key == Mod390Key.BZ_X036 || key == Mod390Key.BZ_X038) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.BZ_X020 || key == Mod390Key.BZ_X022 || key == Mod390Key.BZ_X024
		 || key == Mod390Key.BZ_X026 || key == Mod390Key.BZ_X028 || key == Mod390Key.BZ_X030 
		 || key == Mod390Key.BZ_X032 || key == Mod390Key.BZ_X034 || key == Mod390Key.BZ_X036 || key == Mod390Key.BZ_X038) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


