package com.esferalia.aon.gwt.fiscal.shared.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017GIPUZKOARScript1 implements IModelScript<Mod303Key> {
	
	 DVG01 ("IVA devengado",null,null,TITLE)
	,DVG02 (null					,new Mod303Key[]{Mod303Key.GP_C002	,Mod303Key.GP_X002	,Mod303Key.GP_C003},null,INVOICE,DIFF_INVOICE)
	,DVG03 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.GP_C004	,Mod303Key.GP_X004	,Mod303Key.GP_C005},null,INVOICE,DIFF_INVOICE)
	,DVG04 (null					,new Mod303Key[]{Mod303Key.GP_C006	,Mod303Key.GP_X006	,Mod303Key.GP_C007},null,INVOICE,DIFF_INVOICE)
	,DVG05 ("Modificaci\u00F3n bases y cuotas"
									,new Mod303Key[]{Mod303Key.GP_C039	,null			  	,Mod303Key.GP_C040},null,INVOICE,DIFF_INVOICE)
	,DVG06 (null					,new Mod303Key[]{Mod303Key.GP_C008	,Mod303Key.GP_X008	,Mod303Key.GP_C009},null,INVOICE,DIFF_INVOICE)
	,DVG07 ("Recargo equivalencia"	,new Mod303Key[]{Mod303Key.GP_C010	,Mod303Key.GP_X010	,Mod303Key.GP_C011},null,INVOICE,DIFF_INVOICE)
	,DVG08 (null					,new Mod303Key[]{Mod303Key.GP_C012	,Mod303Key.GP_X012	,Mod303Key.GP_C013},null,INVOICE,DIFF_INVOICE)
	,DVG09 ("Modificaciones bases y cuotas del recargo de equivalencia"
									,new Mod303Key[]{Mod303Key.GP_C041	,null			  	,Mod303Key.GP_C042},null,INVOICE,DIFF_INVOICE)
	,DVG10 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod303Key[]{Mod303Key.GP_C014	,null			  	,Mod303Key.GP_C015},null,INVOICE,DIFF_INVOICE)
	,DVG11 ("Inversi\u00F3n del sujeto pasivo"
									,new Mod303Key[]{Mod303Key.GP_C043	,null			  	,Mod303Key.GP_C044},null,INVOICE,DIFF_INVOICE)
	,DVG12 (Mod303Key.GP_C016.getDescription() 
									,new Mod303Key[]{null			  	,null			  	,Mod303Key.GP_C016},null,COMPUTE)
	,DED01 ("IVA deducible",null,null,TITLE)
	,DED02 ("IVA deducible en operaciones interiores"
									,new Mod303Key[]{Mod303Key.GP_C017	,null			  	,Mod303Key.GP_C018}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE,DIFF_INVOICE)
	,DED03 ("IVA deducible en importaciones"
									,new Mod303Key[]{Mod303Key.GP_C019	,null			  	,Mod303Key.GP_C020}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE,DIFF_INVOICE)
	,DED04 ("Adquisiciones intracomunitarias de bienes y servicios"
									,new Mod303Key[]{Mod303Key.GP_C021	,null			  	,Mod303Key.GP_C022}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE,DIFF_INVOICE)
	,DED05 ("Rectificaci\u00F3n de decucciones"
									,new Mod303Key[]{Mod303Key.GP_C045	,null			  	,Mod303Key.GP_C046}
									,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE,DIFF_INVOICE)
	,DED06 (Mod303Key.GP_C023.getDescription() 
									,new Mod303Key[]{null			  	,null			  	,Mod303Key.GP_C023}
									,new KeyTypes[]{KeyTypes.NONE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA}
									,INVOICE,DIFF_INVOICE)
	,DED07 (Mod303Key.GP_C024.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.GP_C024},null,NONE)
	,DED08 (Mod303Key.GP_C025.getDescription() ,new Mod303Key[]{null			  	,null			  	,Mod303Key.GP_C025},null,COMPUTE)
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032017GIPUZKOARScript1(String label, Mod303Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod303Key.GP_X002 || key == Mod303Key.GP_X004 || key == Mod303Key.GP_X006
		 || key == Mod303Key.GP_X008 || key == Mod303Key.GP_X010 || key == Mod303Key.GP_X012) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.GP_X002 || key == Mod303Key.GP_X004 || key == Mod303Key.GP_X006
		 || key == Mod303Key.GP_X008 || key == Mod303Key.GP_X010 || key == Mod303Key.GP_X012) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}
