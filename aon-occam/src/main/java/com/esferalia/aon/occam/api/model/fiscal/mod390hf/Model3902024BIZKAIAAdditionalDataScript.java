package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902024BIZKAIAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	 DVG01 ("Compras de bienes corrientes",null,TITLE)
	,DVG02 ("Bienes corrientes"	,new Mod390Key[]{Mod390Key.BZ_C142,Mod390Key.BZ_X142,Mod390Key.BZ_C143,Mod390Key.BZ_C144},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 ("Bienes corrientes"	,new Mod390Key[]{Mod390Key.BZ_C134,Mod390Key.BZ_X134,Mod390Key.BZ_C135,Mod390Key.BZ_C136},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 ("Bienes corrientes"	,new Mod390Key[]{Mod390Key.BZ_C145,Mod390Key.BZ_X145,Mod390Key.BZ_C146,Mod390Key.BZ_C147},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG05 ("Bienes corrientes"	,new Mod390Key[]{Mod390Key.BZ_C148,Mod390Key.BZ_X148,Mod390Key.BZ_C149,Mod390Key.BZ_C150},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 ("Bienes corrientes (Compensaciones REAGP)"	  ,new Mod390Key[]{Mod390Key.BZ_C151,null,Mod390Key.BZ_C152,Mod390Key.BZ_C153},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 ("Bienes corrientes (otros tipos impositivos)" ,new Mod390Key[]{Mod390Key.BZ_C154,null,Mod390Key.BZ_C155,Mod390Key.BZ_C156},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 ("Bienes corrientes (Totales)"	,new Mod390Key[]{Mod390Key.BZ_C157,null,Mod390Key.BZ_C158,Mod390Key.BZ_C159},COMPUTE)

	,DVG09 ("Gastos",null,TITLE)
	,DVG10 ("Gastos" ,new Mod390Key[]{Mod390Key.BZ_C160,Mod390Key.BZ_X160,Mod390Key.BZ_C161,Mod390Key.BZ_C162},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 ("Gastos" ,new Mod390Key[]{Mod390Key.BZ_C196,Mod390Key.BZ_X196,Mod390Key.BZ_C197,Mod390Key.BZ_C198},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)	
	,DVG12 ("Gastos" ,new Mod390Key[]{Mod390Key.BZ_C163,Mod390Key.BZ_X163,Mod390Key.BZ_C164,Mod390Key.BZ_C165},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 ("Gastos" ,new Mod390Key[]{Mod390Key.BZ_C166,Mod390Key.BZ_X166,Mod390Key.BZ_C167,Mod390Key.BZ_C168},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 ("Gastos (otros tipos impositivos)" ,new Mod390Key[]{Mod390Key.BZ_C169,null,Mod390Key.BZ_C170,Mod390Key.BZ_C171},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 ("Gastos (Totales)" ,new Mod390Key[]{Mod390Key.BZ_C172,null,Mod390Key.BZ_C173,Mod390Key.BZ_C174},COMPUTE)
	
	,DVG16 ("Bienes de inversi\u00F3n",null,TITLE)
	,DVG17 ("Bienes de inversi\u00F3n"	,new Mod390Key[]{Mod390Key.BZ_C175,Mod390Key.BZ_X175,Mod390Key.BZ_C176,Mod390Key.BZ_C177},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG18 ("Bienes de inversi\u00F3n"	,new Mod390Key[]{Mod390Key.BZ_C227,Mod390Key.BZ_X227,Mod390Key.BZ_C228,Mod390Key.BZ_C229},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)	
	,DVG19 ("Bienes de inversi\u00F3n"	,new Mod390Key[]{Mod390Key.BZ_C178,Mod390Key.BZ_X178,Mod390Key.BZ_C179,Mod390Key.BZ_C180},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG20 ("Bienes de inversi\u00F3n"	,new Mod390Key[]{Mod390Key.BZ_C181,Mod390Key.BZ_X181,Mod390Key.BZ_C182,Mod390Key.BZ_C183},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG21 ("Bienes de inversi\u00F3n (otros tipos impositivos)",new Mod390Key[]{Mod390Key.BZ_C184,null,Mod390Key.BZ_C185,Mod390Key.BZ_C186},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG22 ("Bienes de inversi\u00F3n (Totales)"	,new Mod390Key[]{Mod390Key.BZ_C187,null,Mod390Key.BZ_C188,Mod390Key.BZ_C189},COMPUTE)
	
	,DVG23 ("Totales" , new Mod390Key[]{Mod390Key.BZ_C190,null,Mod390Key.BZ_C191,Mod390Key.BZ_C192},COMPUTE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902024BIZKAIAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	public int getFieldSize(Mod390Key key) {
		if (key == Mod390Key.BZ_X142 || key == Mod390Key.BZ_X145 || key == Mod390Key.BZ_X148
		 || key == Mod390Key.BZ_X160 || key == Mod390Key.BZ_X163 || key == Mod390Key.BZ_X166
		 || key == Mod390Key.BZ_X175 || key == Mod390Key.BZ_X178 || key == Mod390Key.BZ_X181
		 || key == Mod390Key.BZ_X134 || key == Mod390Key.BZ_X196 || key == Mod390Key.BZ_X227) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.BZ_X142 || key == Mod390Key.BZ_X145 || key == Mod390Key.BZ_X148
		 || key == Mod390Key.BZ_X160 || key == Mod390Key.BZ_X163 || key == Mod390Key.BZ_X166
		 || key == Mod390Key.BZ_X175 || key == Mod390Key.BZ_X178 || key == Mod390Key.BZ_X181
		 || key == Mod390Key.BZ_X134 || key == Mod390Key.BZ_X196 || key == Mod390Key.BZ_X227) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


