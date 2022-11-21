package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902021ARABAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	 ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (Mod390Key.AR_C153.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C153},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC02 (Mod390Key.AR_C252.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C252},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC03 (Mod390Key.AR_C156.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C156},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC04 (Mod390Key.AR_C157.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C157},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC05 (Mod390Key.AR_C158.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C158},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC06 (Mod390Key.AR_C214.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C214},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC07 (Mod390Key.AR_C215.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C215},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC08 (Mod390Key.AR_C216.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C216},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC09 (Mod390Key.AR_C217.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C217},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC10 (Mod390Key.AR_C218.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C218},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC11 (Mod390Key.AR_C219.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C219},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC12 (Mod390Key.AR_C154.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C154},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC13 (Mod390Key.AR_C155.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C155},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC14 (Mod390Key.AR_C220.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C220},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC15 (Mod390Key.AR_C221.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C221},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC16 (Mod390Key.AR_C212.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C212},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC17 (Mod390Key.AR_C161.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C161},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC18 (Mod390Key.AR_C162.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C162},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC19 (Mod390Key.AR_C163.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C163},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	,ADC20 (Mod390Key.AR_C167.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C167},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},NONE)
	
	
	,ADC21 (Mod390Key.AR_C212.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C212})
	,ADC22 (Mod390Key.AR_C161.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C161})
	,ADC23 (Mod390Key.AR_C162.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C162})
	,ADC24 (Mod390Key.AR_C163.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C163})
	,ADC25 (Mod390Key.AR_C167.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C167})
	
	
	,ADC26 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo:",null,TITLE)
	,ADC27 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo"
			,new Mod390Key[]{Mod390Key.AR_C262,Mod390Key.AR_C263},NONE)	
	,ADC28 ("Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja"
			,new Mod390Key[]{Mod390Key.AR_C264,Mod390Key.AR_C265},NONE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902021ARABAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3902021ARABAAdditionalDataScript(String label, Mod390Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		return false;
	};
	
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}


