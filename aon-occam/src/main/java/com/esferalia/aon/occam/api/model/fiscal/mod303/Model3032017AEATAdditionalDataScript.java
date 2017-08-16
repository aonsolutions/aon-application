package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.IN_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEATAdditionalDataScript implements IModelScript<Mod303Key> {
	
	 R03 ("Informaci\u00F3n adicional"
		 ,null,TITLE)
	,ADC01 ("Entregas intracomunitarias de bienes y servicios"
		,new Mod303Key[]{null					,Mod303Key.CT_C59},INVOICE,DIFF_INVOICE)
	,ADC02 ("Exportaciones y operaciones asimiladas"
		,new Mod303Key[]{null					,Mod303Key.CT_C60},INVOICE,DIFF_INVOICE)
	,ADC03 ("Operaciones no sujetas o con inversi\u00F3n del sujeto pasivo que originan el derecho a deducci\u00F3n"
		,new Mod303Key[]{null					,Mod303Key.CT_C61},INVOICE,DIFF_INVOICE)
	,ADC04 ("Exclusivamente para aquellos sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y"+ 
		 " para aqu\u00E9llos que sean destinatarios de operaciones afectadas por el mismo",null,TITLE)
	,ADC05 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen especial "+
			"del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA"
		,new Mod303Key[]{Mod303Key.CT_C62		,Mod303Key.CT_C63},OUT_ACCRUAL_INVOICE,DIFF_INVOICE)	
	,ADC06 ("Importes de las adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el" + 
			" r\u00E9gimen especial del criterio de caja"
		,new Mod303Key[]{Mod303Key.CT_C74		,Mod303Key.CT_C75},IN_ACCRUAL_INVOICE,DIFF_INVOICE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017AEATAdditionalDataScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	}

}
