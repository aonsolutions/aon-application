package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017ARABARScript1 implements IModelScript<Mod390Key> {
	
	  DVG01 ("IVA devengado",null,TITLE)
	 ,DVG02 (null					,new Mod390Key[]{Mod390Key.AR_C001	,Mod390Key.AR_C002	,Mod390Key.AR_C003},NONE)
	 ,DVG03 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.AR_C804	,Mod390Key.AR_C805	,Mod390Key.AR_C806},NONE)
	 ,DVG04 (null					,new Mod390Key[]{Mod390Key.AR_C807	,Mod390Key.AR_C808	,Mod390Key.AR_C809},NONE)
	 ,DVG05 ("Modificaci\u00F3n bases y cuotas"
									,new Mod390Key[]{Mod390Key.AR_C351	,null				,Mod390Key.AR_C352},NONE)
	 ,DVG06 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod390Key[]{Mod390Key.AR_C019	,null				,Mod390Key.AR_C020},NONE)
	 ,DVG07 ("Concurso de acreedores"
									,new Mod390Key[]{Mod390Key.AR_C223	,null				,Mod390Key.AR_C224},NONE)
	
	 ,DVG08 (null					,new Mod390Key[]{Mod390Key.AR_C025	,Mod390Key.AR_C026	,Mod390Key.AR_C027},NONE)
	 ,DVG09 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.AR_C034	,Mod390Key.AR_C035	,Mod390Key.AR_C036},NONE)
	 ,DVG10 (null					,new Mod390Key[]{Mod390Key.AR_C828	,Mod390Key.AR_C829	,Mod390Key.AR_C830},NONE)
	 ,DVG11 (null					,new Mod390Key[]{Mod390Key.AR_C831	,Mod390Key.AR_C832	,Mod390Key.AR_C833},NONE)
	 ,DVG12 ("Modificaciones bases y cuotas del recargo de equivalencia"
									,new Mod390Key[]{Mod390Key.AR_C037	,null				,Mod390Key.AR_C038},NONE)
	 ,DVG13 ("Concurso de acreedores"
									,new Mod390Key[]{Mod390Key.AR_C239	,null				,Mod390Key.AR_C240},NONE)
	 ,DVG14 (null					,new Mod390Key[]{Mod390Key.AR_C010	,Mod390Key.AR_C011	,Mod390Key.AR_C012},NONE)
	 ,DVG15 ("Adquisiciones intracomunitarias de bienes y servicios"	
			 						,new Mod390Key[]{Mod390Key.AR_C813	,Mod390Key.AR_C814	,Mod390Key.AR_C815},NONE)
	 ,DVG16 (null					,new Mod390Key[]{Mod390Key.AR_C816	,Mod390Key.AR_C817	,Mod390Key.AR_C818},NONE)
	 ,DVG17 ("Modificaciones bases y cuotas de adquisiciones intracomunitarias"
									,new Mod390Key[]{Mod390Key.AR_C353	,null				,Mod390Key.AR_C354},NONE)				
	 ,DVG18 (Mod390Key.AR_C041.getDescription()	
			 						,new Mod390Key[]{null				,null				,Mod390Key.AR_C041},COMPUTE)
	 ,DVG19 (Mod390Key.AR_C042.getDescription()
			 						,new Mod390Key[]{null				,null				,Mod390Key.AR_C042},NONE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017ARABARScript1(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod390Key.AR_C002
		 || key == Mod390Key.AR_C805
		 || key == Mod390Key.AR_C808
		 || key == Mod390Key.AR_C026
		 || key == Mod390Key.AR_C035
		 || key == Mod390Key.AR_C829
		 || key == Mod390Key.AR_C832
		 || key == Mod390Key.AR_C011
		 || key == Mod390Key.AR_C814
		 || key == Mod390Key.AR_C817) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.AR_C002
		 || key == Mod390Key.AR_C805
		 || key == Mod390Key.AR_C808
		 || key == Mod390Key.AR_C026
		 || key == Mod390Key.AR_C035
		 || key == Mod390Key.AR_C829
		 || key == Mod390Key.AR_C832
		 || key == Mod390Key.AR_C011
		 || key == Mod390Key.AR_C814
		 || key == Mod390Key.AR_C817) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


