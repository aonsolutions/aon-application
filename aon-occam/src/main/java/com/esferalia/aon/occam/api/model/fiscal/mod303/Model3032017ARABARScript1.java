package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017ARABARScript1 implements IModelScript<Mod303Key> {
	
	 DVG01 ("IVA devengado",null,TITLE)
	,DVG02 (null					,new Mod303Key[]{Mod303Key.AR_C001	,Mod303Key.AR_C002	,Mod303Key.AR_C003},INVOICE,DIFF_INVOICE)
	,DVG03 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.AR_C204	,Mod303Key.AR_C205	,Mod303Key.AR_C206},INVOICE,DIFF_INVOICE)
	,DVG04 (null					,new Mod303Key[]{Mod303Key.AR_C207	,Mod303Key.AR_C208	,Mod303Key.AR_C209},INVOICE,DIFF_INVOICE)
	,DVG05 ("Modificaci\u00F3n bases y cuotas"
									,new Mod303Key[]{Mod303Key.AR_C370	,null			  	,Mod303Key.AR_C371},INVOICE,DIFF_INVOICE)
	,DVG06 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod303Key[]{Mod303Key.AR_C372	,null			  	,Mod303Key.AR_C373},INVOICE,DIFF_INVOICE)
	,DVG07 (null					,new Mod303Key[]{Mod303Key.AR_C010	,Mod303Key.AR_C011	,Mod303Key.AR_C012},INVOICE,DIFF_INVOICE)
	,DVG08 ("Recargo equivalencia"	,new Mod303Key[]{Mod303Key.AR_C213	,Mod303Key.AR_C214	,Mod303Key.AR_C215},INVOICE,DIFF_INVOICE)
	,DVG09 (null					,new Mod303Key[]{Mod303Key.AR_C216	,Mod303Key.AR_C217	,Mod303Key.AR_C218},INVOICE,DIFF_INVOICE)
	,DVG10 ("Modificaciones bases y cuotas del recargo de equivalencia"
									,new Mod303Key[]{Mod303Key.AR_C374	,null			  	,Mod303Key.AR_C375},INVOICE,DIFF_INVOICE)
	
	,DVG11 (null					,new Mod303Key[]{Mod303Key.AR_C019	,Mod303Key.AR_C020	,Mod303Key.AR_C021},INVOICE,DIFF_INVOICE)
	,DVG12 ("Adquisiciones intracomunitarias de bienes y servicios"	
									,new Mod303Key[]{Mod303Key.AR_C222	,Mod303Key.AR_C223	,Mod303Key.AR_C224},INVOICE,DIFF_INVOICE)
	,DVG13 (null					,new Mod303Key[]{Mod303Key.AR_C225	,Mod303Key.AR_C226	,Mod303Key.AR_C227},INVOICE,DIFF_INVOICE)
	,DVG14 ("Modificaciones bases y cuotas de adquisiciones intracomunitarias"
			,new Mod303Key[]{Mod303Key.AR_C376	,null			  	,Mod303Key.AR_C377},INVOICE,DIFF_INVOICE)
	,DVG15 (Mod303Key.AR_C028.getDescription()	,new Mod303Key[]{null			  	,null			  	,Mod303Key.AR_C028},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017ARABARScript1(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
