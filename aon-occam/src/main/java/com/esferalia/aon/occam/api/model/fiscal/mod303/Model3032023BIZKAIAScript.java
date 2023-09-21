package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032023BIZKAIAScript implements IModelScript<Mod303Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEVENGADO ----------------------
	// ---------------------------------------------------------
	 DVG01 ("IVA DEVENGADO",null,TITLE)
	,DVG02 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.BZ_C048	,Mod303Key.BZ_X048	,Mod303Key.BZ_C049},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 (DVG02.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C003	,Mod303Key.BZ_X003	,Mod303Key.BZ_C004},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 (DVG02.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C110	,Mod303Key.BZ_X110	,Mod303Key.BZ_C111},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG05 (DVG02.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C005	,Mod303Key.BZ_X005	,Mod303Key.BZ_C006},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 (DVG02.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C007	,Mod303Key.BZ_X007	,Mod303Key.BZ_C008},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG07 ("Recargo equivalencia"	,new Mod303Key[]{Mod303Key.BZ_C009	,Mod303Key.BZ_X009	,Mod303Key.BZ_C010},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 (DVG07.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C011	,Mod303Key.BZ_X011	,Mod303Key.BZ_C012},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 (DVG07.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C013	,Mod303Key.BZ_X013	,Mod303Key.BZ_C014},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG10 (DVG07.getLabel()		,new Mod303Key[]{Mod303Key.BZ_C015	,Mod303Key.BZ_X015	,Mod303Key.BZ_C016},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DVG11 ("Adquisiciones intracomunitarias"
		,new Mod303Key[]{Mod303Key.BZ_C017	,null	,Mod303Key.BZ_C018},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG12 ("IVA devengado por inversi\u00F3n del sujeto pasivo"
		,new Mod303Key[]{Mod303Key.BZ_C019	,null	,Mod303Key.BZ_C020},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG13 ("Modificaci\u00F3n bases y cuotas, general."
		,new Mod303Key[]{Mod303Key.BZ_C021	,null	,Mod303Key.BZ_C022},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG14 ("Modificaci\u00F3n bases y cuotas (art. 80.3 y 80.4 NFIVA)."
		,new Mod303Key[]{Mod303Key.BZ_C046	,null	,Mod303Key.BZ_C047},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG15 ("Total cuota devengada"	,new Mod303Key[]{Mod303Key.BZ_C023},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032023BIZKAIAScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod303Key.BZ_X048
			|| key == Mod303Key.BZ_X003
			|| key == Mod303Key.BZ_X110
			|| key == Mod303Key.BZ_X005 
			|| key == Mod303Key.BZ_X007
			|| key == Mod303Key.BZ_X009 
			|| key == Mod303Key.BZ_X011 
			|| key == Mod303Key.BZ_X013 
			|| key == Mod303Key.BZ_X015) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.BZ_X048
			|| key == Mod303Key.BZ_X003
			|| key == Mod303Key.BZ_X110
			|| key == Mod303Key.BZ_X005 
			|| key == Mod303Key.BZ_X007
			|| key == Mod303Key.BZ_X009 
			|| key == Mod303Key.BZ_X011 
			|| key == Mod303Key.BZ_X013 
			|| key == Mod303Key.BZ_X015) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


