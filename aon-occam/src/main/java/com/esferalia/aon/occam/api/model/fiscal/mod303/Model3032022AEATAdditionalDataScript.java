package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_OUT_VAT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_IN_VAT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032022AEATAdditionalDataScript implements IModelScript<Mod303Key> {
	
	 R03 ("Informaci\u00F3n adicional"
		 ,null,TITLE)
	,ADC01 ("Entregas intracomunitarias de bienes y servicios"
		,new Mod303Key[]{null					,Mod303Key.CT_C59},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC02 ("Exportaciones y operaciones asimiladas"
		,new Mod303Key[]{null					,Mod303Key.CT_C60},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC03 ("Operaciones no sujetas por reglas de localizaci\u00F3n (excepto las incluidas en la casilla 123)."
		,new Mod303Key[]{null					,Mod303Key.CT_C120},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC04 ("Operaciones sujetas con inversi\u00F3n del sujeto pasivo."
		,new Mod303Key[]{null					,Mod303Key.CT_C122},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC05 ("Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica."
		,new Mod303Key[]{null					,Mod303Key.CT_C123},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC06 ("Operaciones sujetas y acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica."
		,new Mod303Key[]{null					,Mod303Key.CT_C124},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},MODEL_INVOICE_VAT_BREAKDOWN)
	,ADC07 ("Exclusivamente para aquellos sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y"+ 
		 " para aqu\u00E9llos que sean destinatarios de operaciones afectadas por el mismo",null,TITLE)
	,ADC08 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen especial "+
			"del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA"
		,new Mod303Key[]{Mod303Key.CT_C62		,Mod303Key.CT_C63},MODEL_OUT_VAT_ACCRUAL_INVOICE)	
	,ADC09 ("Importes de las adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el" + 
			" r\u00E9gimen especial del criterio de caja"
		,new Mod303Key[]{Mod303Key.CT_C74		,Mod303Key.CT_C75},MODEL_IN_VAT_ACCRUAL_INVOICE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032022AEATAdditionalDataScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3032022AEATAdditionalDataScript(String label, Mod303Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
	
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
	

}
