package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017ARABAAdditionalDataScript implements IModelScript<Mod303Key> {
	
	 ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (Mod303Key.AR_C050.getDescription() ,new Mod303Key[]{null					,Mod303Key.AR_C050},INVOICE,DIFF_INVOICE)
	,ADC02 (Mod303Key.AR_C051.getDescription() ,new Mod303Key[]{null					,Mod303Key.AR_C051},INVOICE,DIFF_INVOICE)
	,ADC03 (Mod303Key.AR_C052.getDescription() ,new Mod303Key[]{null					,Mod303Key.AR_C052},INVOICE,DIFF_INVOICE)
	,ADC04 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo:",null,TITLE)
	,ADC05 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo"
			,new Mod303Key[]{Mod303Key.AR_C180,Mod303Key.AR_C181},INVOICE,DIFF_INVOICE)	
	,ADC06 ("Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja"
			,new Mod303Key[]{Mod303Key.AR_C182,Mod303Key.AR_C183},INVOICE,DIFF_INVOICE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017ARABAAdditionalDataScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		if (key == Mod303Key.BZ_X050 || key == Mod303Key.BZ_X053 || key == Mod303Key.BZ_X056
		 || key == Mod303Key.BZ_X068 || key == Mod303Key.BZ_X071 || key == Mod303Key.BZ_X074) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	@Override
	public boolean isEnabled(Mod303Key key) {
		if (key == Mod303Key.BZ_X050 || key == Mod303Key.BZ_X053 || key == Mod303Key.BZ_X056
		 || key == Mod303Key.BZ_X068 || key == Mod303Key.BZ_X071 || key == Mod303Key.BZ_X074) {
			return false;	
		}
		return IModelScript.super.isEnabled(key);
	}
}


