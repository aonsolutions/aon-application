package com.esferalia.aon.gwt.fiscal.shared.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_IN_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_OUT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.IN_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017GIPUZKOAAdditionalDataScript implements IModelScript<Mod303Key> {
	
	 ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (Mod303Key.GP_C030.getDescription() ,new Mod303Key[]{null,Mod303Key.GP_C030},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE,DIFF_INVOICE)
	,ADC02 (Mod303Key.GP_C031.getDescription() ,new Mod303Key[]{null,Mod303Key.GP_C031},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE,DIFF_INVOICE)
	,ADC03 (Mod303Key.GP_C032.getDescription() ,new Mod303Key[]{null,Mod303Key.GP_C032},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE,DIFF_INVOICE)
	,ADC04 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo:",null,TITLE)
	,ADC05 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo"
			,new Mod303Key[]{Mod303Key.GP_C047,Mod303Key.GP_C048},OUT_ACCRUAL_INVOICE,DIFF_OUT_ACCRUAL_INVOICE)	
	,ADC06 ("Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja"
			,new Mod303Key[]{Mod303Key.GP_C049,Mod303Key.GP_C050},IN_ACCRUAL_INVOICE,DIFF_IN_ACCRUAL_INVOICE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032017GIPUZKOAAdditionalDataScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3032017GIPUZKOAAdditionalDataScript(String label, Mod303Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		return false;
	};
	
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}


