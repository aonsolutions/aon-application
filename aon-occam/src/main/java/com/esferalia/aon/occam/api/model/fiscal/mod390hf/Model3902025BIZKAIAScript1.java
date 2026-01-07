package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902025BIZKAIAScript1 implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("IVA DEVENGADO",null,TITLE)
	 
	,DVG02 ("R\u00E9gimen general"					,new Mod390Key[]{Mod390Key.BZ_C049	,Mod390Key.BZ_X049	,Mod390Key.BZ_C050},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 ("R\u00E9gimen general"					,new Mod390Key[]{Mod390Key.BZ_C020  ,Mod390Key.BZ_X020 	,Mod390Key.BZ_C021},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 ("R\u00E9gimen general"					,new Mod390Key[]{Mod390Key.BZ_C051	,Mod390Key.BZ_X051	,Mod390Key.BZ_C052},MODEL_INVOICE_VAT_BREAKDOWN)	
	,DVG05 ("R\u00E9gimen general"					,new Mod390Key[]{Mod390Key.BZ_C022	,Mod390Key.BZ_X022	,Mod390Key.BZ_C023},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 ("R\u00E9gimen general"					,new Mod390Key[]{Mod390Key.BZ_C024	,Mod390Key.BZ_X024	,Mod390Key.BZ_C025},MODEL_INVOICE_VAT_BREAKDOWN)

	,DVG07 ("Operaciones intragrupo"			    ,new Mod390Key[]{Mod390Key.BZ_C053	,Mod390Key.BZ_X053	,Mod390Key.BZ_C054},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 ("Operaciones intragrupo"				,new Mod390Key[]{Mod390Key.BZ_C026	,Mod390Key.BZ_X026	,Mod390Key.BZ_C027},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 ("Operaciones intragrupo"				,new Mod390Key[]{Mod390Key.BZ_C055	,Mod390Key.BZ_X055	,Mod390Key.BZ_C056},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG10 ("Operaciones intragrupo"				,new Mod390Key[]{Mod390Key.BZ_C028	,Mod390Key.BZ_X028	,Mod390Key.BZ_C029},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 ("Operaciones intragrupo"				,new Mod390Key[]{Mod390Key.BZ_C030	,Mod390Key.BZ_X030	,Mod390Key.BZ_C031},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG12 ("Recargo equivalencia"					,new Mod390Key[]{Mod390Key.BZ_C032	,Mod390Key.BZ_X032	,Mod390Key.BZ_C033},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 ("Recargo equivalencia"					,new Mod390Key[]{Mod390Key.BZ_C057	,Mod390Key.BZ_X057	,Mod390Key.BZ_C058},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 ("Recargo equivalencia"					,new Mod390Key[]{Mod390Key.BZ_C034	,Mod390Key.BZ_X034	,Mod390Key.BZ_C035},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 ("Recargo equivalencia"					,new Mod390Key[]{Mod390Key.BZ_C036	,Mod390Key.BZ_X036	,Mod390Key.BZ_C037},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG16 ("Recargo equivalencia"					,new Mod390Key[]{Mod390Key.BZ_C038	,Mod390Key.BZ_X038	,Mod390Key.BZ_C039},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG17 ("Adquisiciones intracomunitarias"
		,new Mod390Key[]{Mod390Key.BZ_C040	,null	,Mod390Key.BZ_C041},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG18 ("IVA devengado por inversi\u00F3n del sujeto pasivo"
		,new Mod390Key[]{Mod390Key.BZ_C042	,null	,Mod390Key.BZ_C043},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG19 ("Modificaci\u00F3n bases y cuotas, general."
		,new Mod390Key[]{Mod390Key.BZ_C044	,null	,Mod390Key.BZ_C045},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG20 ("Modificaci\u00F3n bases y cuotas (art. 80.3 y 80.4 NFIVA)."
		,new Mod390Key[]{Mod390Key.BZ_C046	,null	,Mod390Key.BZ_C047},NONE)
	,DVG21 ("Total cuota devengada"	,new Mod390Key[]{Mod390Key.BZ_C048},COMPUTE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902025BIZKAIAScript1(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		 || key == Mod390Key.BZ_X032 || key == Mod390Key.BZ_X034 || key == Mod390Key.BZ_X036 || key == Mod390Key.BZ_X038
		 || key == Mod390Key.BZ_X049 || key == Mod390Key.BZ_X051 || key == Mod390Key.BZ_X053 || key == Mod390Key.BZ_X055 || key == Mod390Key.BZ_X057) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.BZ_X020 || key == Mod390Key.BZ_X022 || key == Mod390Key.BZ_X024
		 || key == Mod390Key.BZ_X026 || key == Mod390Key.BZ_X028 || key == Mod390Key.BZ_X030 
		 || key == Mod390Key.BZ_X032 || key == Mod390Key.BZ_X034 || key == Mod390Key.BZ_X036 || key == Mod390Key.BZ_X038
		 || key == Mod390Key.BZ_X049 || key == Mod390Key.BZ_X051 || key == Mod390Key.BZ_X053 || key == Mod390Key.BZ_X055 || key == Mod390Key.BZ_X057) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


