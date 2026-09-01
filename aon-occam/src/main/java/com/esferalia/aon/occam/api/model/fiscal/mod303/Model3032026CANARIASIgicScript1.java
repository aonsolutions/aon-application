package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032026CANARIASIgicScript1 implements IModelScript<Mod303Key> {
	
	 DVG00 ("IGIC DEVENGADO",null,TITLE)
	,DVG01 ("IGIC devengado (1)"								, new Mod303Key[]{Mod303Key.CA_DB01, Mod303Key.CA_DT01, Mod303Key.CA_DC01},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG02 ("IGIC devengado (2)"								, new Mod303Key[]{Mod303Key.CA_DB02, Mod303Key.CA_DT02, Mod303Key.CA_DC02},MODEL_INVOICE_VAT_BREAKDOWN) 
	,DVG03 ("IGIC devengado (3)"								, new Mod303Key[]{Mod303Key.CA_DB03, Mod303Key.CA_DT03, Mod303Key.CA_DC03},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG04 ("IGIC devengado (4)"								, new Mod303Key[]{Mod303Key.CA_DB04, Mod303Key.CA_DT04, Mod303Key.CA_DC04},MODEL_INVOICE_VAT_BREAKDOWN) 
	,DVG05 ("IGIC devengado (5)"								, new Mod303Key[]{Mod303Key.CA_DB05, Mod303Key.CA_DT05, Mod303Key.CA_DC05},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG06 ("IGIC devengado (6)"								, new Mod303Key[]{Mod303Key.CA_DB06, Mod303Key.CA_DT06, Mod303Key.CA_DC06},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 ("IGIC devengado (7)"								, new Mod303Key[]{Mod303Key.CA_DB07, Mod303Key.CA_DT07, Mod303Key.CA_DC07},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 ("IGIC devengado (8)"								, new Mod303Key[]{Mod303Key.CA_DB08, Mod303Key.CA_DT08, Mod303Key.CA_DC08},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 ("Operaciones con inversi\u00F3n del sujeto pasivo"	, new Mod303Key[]{Mod303Key.CA_C019, null			  , Mod303Key.CA_C020},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG10 ("Modificaci\u00F3n bases y cuotas" 					, new Mod303Key[]{Mod303Key.CA_C021, null			  , Mod303Key.CA_C022},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG11 (Mod303Key.CA_C025.getDescription()					, new Mod303Key[]{null			   , null			  , Mod303Key.CA_C025},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032026CANARIASIgicScript1(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod303Key.CA_DT01 ||
			key == Mod303Key.CA_DT02 ||
			key == Mod303Key.CA_DT03 ||
			key == Mod303Key.CA_DT04 ||
			key == Mod303Key.CA_DT05 ||
			key == Mod303Key.CA_DT06 ||
			key == Mod303Key.CA_DT07 ||
			key == Mod303Key.CA_DT08) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.CA_DT01 ||
			key == Mod303Key.CA_DT02 ||
			key == Mod303Key.CA_DT03 ||
			key == Mod303Key.CA_DT04 ||
			key == Mod303Key.CA_DT05 ||
			key == Mod303Key.CA_DT06 ||
			key == Mod303Key.CA_DT07 ||
			key == Mod303Key.CA_DT08) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}
