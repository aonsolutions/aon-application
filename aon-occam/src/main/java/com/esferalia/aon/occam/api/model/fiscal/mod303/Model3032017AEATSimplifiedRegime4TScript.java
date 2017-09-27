package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEATSimplifiedRegime4TScript implements IModelScript<Mod303Key> {
	
	  SM001 (Mod303Key.CT_S48.getDescription(),new Mod303Key[]{Mod303Key.CT_S48},COMPUTE)
	 ,SM002 (Mod303Key.CT_S49.getDescription(),new Mod303Key[]{Mod303Key.CT_S49},NONE)
	 ,SM003 (Mod303Key.CT_S50.getDescription(),new Mod303Key[]{Mod303Key.CT_S50},COMPUTE)
	 ,DV002 ("Cuotas devengadas",null,TITLE)
	 ,DV003 (Mod303Key.CT_S51.getDescription(),new Mod303Key[]{Mod303Key.CT_S51},INVOICE,DIFF_INVOICE)
	 ,DV004 (Mod303Key.CT_S52.getDescription(),new Mod303Key[]{Mod303Key.CT_S52},INVOICE,DIFF_INVOICE)
	 ,DV005 (Mod303Key.CT_S53.getDescription(),new Mod303Key[]{Mod303Key.CT_S53},INVOICE,DIFF_INVOICE)
	 ,DV006 (Mod303Key.CT_S54.getDescription(),new Mod303Key[]{Mod303Key.CT_S54},COMPUTE)
	 
	 ,DCG01 ("IVA deducible",null,TITLE)
	 ,DC002 (Mod303Key.CT_S55.getDescription(),new Mod303Key[]{Mod303Key.CT_S55},INVOICE,DIFF_INVOICE)
	 ,DC003 (Mod303Key.CT_S56.getDescription(),new Mod303Key[]{Mod303Key.CT_S56},NONE)
	 ,DC004 (Mod303Key.CT_S57.getDescription(),new Mod303Key[]{Mod303Key.CT_S57},COMPUTE)
	 
	 ,TTG00 ("Resultado",null,TITLE)
	 ,TT001 (Mod303Key.CT_S58.getDescription(),new Mod303Key[]{Mod303Key.CT_S58},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017AEATSimplifiedRegime4TScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	};
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	};
	
	@Override
	public int getFieldSize(Mod303Key key) {
		if (key == Mod303Key.CT_C02 || key == Mod303Key.CT_C05|| key == Mod303Key.CT_C08
		 || key == Mod303Key.CT_C17 || key == Mod303Key.CT_C20|| key == Mod303Key.CT_C23) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.CT_C02 || key == Mod303Key.CT_C05|| key == Mod303Key.CT_C08
		 || key == Mod303Key.CT_C17 || key == Mod303Key.CT_C20|| key == Mod303Key.CT_C23) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}
