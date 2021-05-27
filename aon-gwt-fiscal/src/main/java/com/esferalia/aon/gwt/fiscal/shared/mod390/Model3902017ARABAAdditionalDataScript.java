package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.IN_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017ARABAAdditionalDataScript implements IModelScript<Mod390Key> {
	
	 ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (Mod390Key.AR_C153.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C153},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC02 (Mod390Key.AR_C252.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C252},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC03 (Mod390Key.AR_C154.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C154},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC04 (Mod390Key.AR_C155.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C155},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC05 (Mod390Key.AR_C156.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C156},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC06 (Mod390Key.AR_C157.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C157},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC07 (Mod390Key.AR_C158.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C158},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC08 (Mod390Key.AR_C210.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C210},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC09 (Mod390Key.AR_C211.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C211},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC10 (Mod390Key.AR_C212.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C212},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC11 (Mod390Key.AR_C161.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C161},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC12 (Mod390Key.AR_C162.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C162},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC13 (Mod390Key.AR_C163.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C163},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC14 (Mod390Key.AR_C167.getDescription() ,new Mod390Key[]{null,Mod390Key.AR_C167},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,ADC15 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo:",null,TITLE)
	,ADC16 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo"
			,new Mod390Key[]{Mod390Key.AR_C262,Mod390Key.AR_C263},OUT_ACCRUAL_INVOICE)	
	,ADC17 ("Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja"
			,new Mod390Key[]{Mod390Key.AR_C264,Mod390Key.AR_C265},IN_ACCRUAL_INVOICE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902017ARABAAdditionalDataScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3902017ARABAAdditionalDataScript(String label, Mod390Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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


