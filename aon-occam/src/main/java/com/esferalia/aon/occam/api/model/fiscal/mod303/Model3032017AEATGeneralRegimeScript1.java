package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEATGeneralRegimeScript1 implements IModelScript<Mod303Key> {
	
	 DVG01 ("R\u00E9gimen general. IVA devengado",null,TITLE)
	,DVG02 (null					,new Mod303Key[]{Mod303Key.CT_C01	,Mod303Key.CT_C02	,Mod303Key.CT_C03},INVOICE,DIFF_INVOICE)
	,DVG03 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.CT_C04	,Mod303Key.CT_C05	,Mod303Key.CT_C06},INVOICE,DIFF_INVOICE)
	,DVG04 (null					,new Mod303Key[]{Mod303Key.CT_C07	,Mod303Key.CT_C08	,Mod303Key.CT_C09},INVOICE,DIFF_INVOICE)
	,DVG05 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod303Key[]{Mod303Key.CT_C10	,null			  	,Mod303Key.CT_C11},INVOICE,DIFF_INVOICE)
	,DVG06 ("Otras operaciones con inversi\u00F3n del sujeto pasivo (excepto. adq. intracom)"
									,new Mod303Key[]{Mod303Key.CT_C12	,null			  	,Mod303Key.CT_C13},INVOICE,DIFF_INVOICE)
	,DVG07 ("Modificaci\u00F3n bases y cuotas"
									,new Mod303Key[]{Mod303Key.CT_C14	,null			  	,Mod303Key.CT_C15},INVOICE,DIFF_INVOICE)
	,DVG08 (null					,new Mod303Key[]{Mod303Key.CT_C16	,Mod303Key.CT_C17	,Mod303Key.CT_C18},INVOICE,DIFF_INVOICE)
	,DVG09 ("Recargo equivalencia"	,new Mod303Key[]{Mod303Key.CT_C19	,Mod303Key.CT_C20	,Mod303Key.CT_C21},INVOICE,DIFF_INVOICE)
	,DVG10 (null					,new Mod303Key[]{Mod303Key.CT_C22	,Mod303Key.CT_C23	,Mod303Key.CT_C24},INVOICE,DIFF_INVOICE)
	,DVG11 ("Modificaciones bases y cuotas del recargo de equivalencia"
									,new Mod303Key[]{Mod303Key.CT_C25	,null			  	,Mod303Key.CT_C26},INVOICE,DIFF_INVOICE)
	,DVG12 (Mod303Key.CT_C27.getDescription() 
									,new Mod303Key[]{null			  	,null			  	,Mod303Key.CT_C27},COMPUTE)
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017AEATGeneralRegimeScript1(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == DVG01);
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
