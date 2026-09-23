package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032022ARABAScript2 implements IModelScript<Mod303Key> {
	
	 DED01 ("IVA deducible"						,null								,null,TITLE)
	,DED02 (Mod303Key.AR_C030.getDescription()	,new Mod303Key[]{Mod303Key.AR_C030}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED03 (Mod303Key.AR_C031.getDescription()	,new Mod303Key[]{Mod303Key.AR_C031}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED04 (Mod303Key.AR_C032.getDescription()	,new Mod303Key[]{Mod303Key.AR_C032}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED05 (Mod303Key.AR_C033.getDescription()	,new Mod303Key[]{Mod303Key.AR_C033}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED06 (Mod303Key.AR_C034.getDescription()	,new Mod303Key[]{Mod303Key.AR_C034}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED07 (Mod303Key.AR_C035.getDescription()	,new Mod303Key[]{Mod303Key.AR_C035}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED08 (Mod303Key.AR_C036.getDescription()	,new Mod303Key[]{Mod303Key.AR_C036}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	
	,DED09 (Mod303Key.AR_C037.getDescription()	,new Mod303Key[]{Mod303Key.AR_C037}	,null,MODEL_INVOICE_VAT_BREAKDOWN)
	,DED10 (Mod303Key.AR_C046.getDescription()	,new Mod303Key[]{Mod303Key.AR_C046}	,new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED11 (Mod303Key.AR_C038.getDescription()	,new Mod303Key[]{Mod303Key.AR_C038}	,null,COMPUTE)
	,DED12 (Mod303Key.AR_C039.getDescription()	,new Mod303Key[]{Mod303Key.AR_C039}	,null,COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032022ARABAScript2(String label, Mod303Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
	}
	
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
		return this.keyTypes;
	}
	
}
