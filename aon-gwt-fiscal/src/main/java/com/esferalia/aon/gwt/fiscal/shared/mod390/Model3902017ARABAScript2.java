package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017ARABAScript2 implements IModelScript<Mod390Key> {
	
	 DED01 ("IVA deducible",null,null,TITLE)
	,DED02 (null
		,new Mod390Key[]{Mod390Key.AR_C043,Mod390Key.AR_C044,Mod390Key.AR_C045}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED03 ("Adquisiciones interiores de bienes y servicios corrientes"
		,new Mod390Key[]{Mod390Key.AR_C846,Mod390Key.AR_C847,Mod390Key.AR_C848}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED04 (null
		,new Mod390Key[]{Mod390Key.AR_C849,Mod390Key.AR_C850,Mod390Key.AR_C851}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED05 (null
		,new Mod390Key[]{Mod390Key.AR_C076,Mod390Key.AR_C077,Mod390Key.AR_C078}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED06 ("Adquisiciones interiores de bienes de inversi\u00F3n"
		,new Mod390Key[]{Mod390Key.AR_C879,Mod390Key.AR_C880,Mod390Key.AR_C881}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED07 (null
		,new Mod390Key[]{Mod390Key.AR_C882,Mod390Key.AR_C883,Mod390Key.AR_C884}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED08 ("Rectificaci\u00F3n de deducciones en operaciones interiores"
		,new Mod390Key[]{Mod390Key.AR_C355,null			,Mod390Key.AR_C356}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED09 (null
		,new Mod390Key[]{Mod390Key.AR_C054,Mod390Key.AR_C055,Mod390Key.AR_C056}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED10 ("Importaciones de bienes corrientes" 
		,new Mod390Key[]{Mod390Key.AR_C857,Mod390Key.AR_C858,Mod390Key.AR_C859}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED11 (null
		,new Mod390Key[]{Mod390Key.AR_C860,Mod390Key.AR_C861,Mod390Key.AR_C862}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED12 (null
		,new Mod390Key[]{Mod390Key.AR_C087,Mod390Key.AR_C088,Mod390Key.AR_C089}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED13 ("Importaciones de bienes de inversi\u00F3n"
		,new Mod390Key[]{Mod390Key.AR_C890,Mod390Key.AR_C891,Mod390Key.AR_C892}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED14 (null
		,new Mod390Key[]{Mod390Key.AR_C893,Mod390Key.AR_C894,Mod390Key.AR_C895}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED15 ("Rectificaci\u00F3n de deducciones en importaciones"
		,new Mod390Key[]{Mod390Key.AR_C357,null				,Mod390Key.AR_C358}
			,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	 
// *************************
	,DED16 (null
		,new Mod390Key[]{Mod390Key.AR_C065,Mod390Key.AR_C066,Mod390Key.AR_C067}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED17 ("Adquisiciones intracomunitarias de bienes y servicios corrientes" 
		,new Mod390Key[]{Mod390Key.AR_C868,Mod390Key.AR_C869,Mod390Key.AR_C870}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED18 (null
		,new Mod390Key[]{Mod390Key.AR_C871,Mod390Key.AR_C872,Mod390Key.AR_C873}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED19 (null
		,new Mod390Key[]{Mod390Key.AR_C098,Mod390Key.AR_C099,Mod390Key.AR_C100}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED20 ("Adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,new Mod390Key[]{Mod390Key.AR_C361,Mod390Key.AR_C362,Mod390Key.AR_C363}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED21 (null
		,new Mod390Key[]{Mod390Key.AR_C364,Mod390Key.AR_C365,Mod390Key.AR_C366}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED22 ("Rectificaci\u00F3n de deducciones en adquisiciones intracomunitarias"
		,new Mod390Key[]{Mod390Key.AR_C359,null				,Mod390Key.AR_C360}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED23 ("Compensaciones R\u00E9gimen Especial A.G. y P."
		,new Mod390Key[]{Mod390Key.AR_C109,null				,Mod390Key.AR_C110}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED24 ("Regularizaci\u00F3n Inversiones"
		,new Mod390Key[]{Mod390Key.AR_C111,null				,Mod390Key.AR_C112}
		,new KeyTypes[]{KeyTypes.BASE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED25 ("Regularizaci\u00F3n aplicaci\u00F3n definitiva de prorrata"
		,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C115}
		,new KeyTypes[]{KeyTypes.NONE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	,DED26 ("Suma de deducciones"
		,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C113}
		,new KeyTypes[]{KeyTypes.NONE,KeyTypes.NONE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902017ARABAScript2(String label, Mod390Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
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
	public KeyTypes[] getKeyTypes() {
		return this.keyTypes;
	}
	
	@Override
	public int getFieldSize(Mod390Key key) {
		if (key == Mod390Key.AR_C044
		 || key == Mod390Key.AR_C847
		 || key == Mod390Key.AR_C850
		 || key == Mod390Key.AR_C077
		 || key == Mod390Key.AR_C880
		 || key == Mod390Key.AR_C883
		 || key == Mod390Key.AR_C055
		 || key == Mod390Key.AR_C858
		 || key == Mod390Key.AR_C861
		 || key == Mod390Key.AR_C088
		 || key == Mod390Key.AR_C891
		 || key == Mod390Key.AR_C894
		 || key == Mod390Key.AR_C066
		 || key == Mod390Key.AR_C869
		 || key == Mod390Key.AR_C872
		 || key == Mod390Key.AR_C099
		 || key == Mod390Key.AR_C362
		 || key == Mod390Key.AR_C365) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod390Key key) {
		if (key == Mod390Key.AR_C044
		 || key == Mod390Key.AR_C847
		 || key == Mod390Key.AR_C850
		 || key == Mod390Key.AR_C077
		 || key == Mod390Key.AR_C880
		 || key == Mod390Key.AR_C883
		 || key == Mod390Key.AR_C055
		 || key == Mod390Key.AR_C858
		 || key == Mod390Key.AR_C861
		 || key == Mod390Key.AR_C088
		 || key == Mod390Key.AR_C891
		 || key == Mod390Key.AR_C894
		 || key == Mod390Key.AR_C066
		 || key == Mod390Key.AR_C869
		 || key == Mod390Key.AR_C872
		 || key == Mod390Key.AR_C099
		 || key == Mod390Key.AR_C362
		 || key == Mod390Key.AR_C365) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}
