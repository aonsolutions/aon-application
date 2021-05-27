package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017GIPUZKOARScript1 implements IModelScript<Mod390Key> {
	
	 DVG01 ("IVA devengado",null,null,TITLE)
	,DVG02 (null					,new Mod390Key[]{Mod390Key.GP_C002	,Mod390Key.GP_X002	,Mod390Key.GP_C003},null,INVOICE)
	,DVG03 ("R\u00E9gimen general"	,new Mod390Key[]{Mod390Key.GP_C004	,Mod390Key.GP_X004	,Mod390Key.GP_C005},null,INVOICE)
	,DVG04 (null					,new Mod390Key[]{Mod390Key.GP_C006	,Mod390Key.GP_X006	,Mod390Key.GP_C007},null,INVOICE)
	,DVG05 ("Modificaci\u00F3n bases y cuotas"
									,new Mod390Key[]{Mod390Key.GP_C008	,null			  	,Mod390Key.GP_C009},null,INVOICE)
	,DVG06 (null					,new Mod390Key[]{Mod390Key.GP_C010	,Mod390Key.GP_X010	,Mod390Key.GP_C011},null,INVOICE)
	,DVG07 ("Recargo equivalencia"	,new Mod390Key[]{Mod390Key.GP_C012	,Mod390Key.GP_X012	,Mod390Key.GP_C013},null,INVOICE)
	,DVG08 (null					,new Mod390Key[]{Mod390Key.GP_C014	,Mod390Key.GP_X014	,Mod390Key.GP_C015},null,INVOICE)
	,DVG09 ("Modificaciones bases y cuotas del recargo de equivalencia"
									,new Mod390Key[]{Mod390Key.GP_C016	,null			  	,Mod390Key.GP_C017},null,INVOICE)
	,DVG10 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod390Key[]{Mod390Key.GP_C018	,null			  	,Mod390Key.GP_C019},null,INVOICE)
	,DVG11 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod390Key[]{Mod390Key.GP_C106	,null			  	,Mod390Key.GP_C107},null,INVOICE)
	,DVG12 (Mod390Key.GP_C020.getDescription() 
									,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C020},null,COMPUTE)
	
	
	,DED01 ("IVA deducible",null,null,TITLE)
	,DED02 ("IVA deducible en operaciones interiores"
									,new Mod390Key[]{Mod390Key.GP_C021	,null			  	,Mod390Key.GP_C022}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE)
	,DED03 ("IVA deducible en importaciones"
									,new Mod390Key[]{Mod390Key.GP_C023	,null			  	,Mod390Key.GP_C024}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE)
	,DED04 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod390Key[]{Mod390Key.GP_C025	,null			  	,Mod390Key.GP_C026}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE)
	,DED06 (Mod390Key.GP_C027.getDescription() 
									,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C027}
									,new KeyTypes[]{KeyTypes.NONE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE)
	,DED05 ("Rectificaci\u00F3n de deducciones"
									,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C271}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE)
	,DED07 (Mod390Key.GP_C028.getDescription() ,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C028},null,NONE)
	,DED08 (Mod390Key.GP_C029.getDescription() ,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C029},null,COMPUTE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902017GIPUZKOARScript1(String label, Mod390Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
		this.keyTypes = keyTypes; 
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
		if (key == Mod390Key.GP_X002 || key == Mod390Key.GP_X004 || key == Mod390Key.GP_X006
		 || key == Mod390Key.GP_X010 || key == Mod390Key.GP_X012 || key == Mod390Key.GP_X014) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.GP_X002 || key == Mod390Key.GP_X004 || key == Mod390Key.GP_X006
		 || key == Mod390Key.GP_X010 || key == Mod390Key.GP_X012 || key == Mod390Key.GP_X014) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}
