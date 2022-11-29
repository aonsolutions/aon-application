package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_IN_VAT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_OUT_VAT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902022GIPUZKOASpecificOperationsScript implements IModelScript<Mod390Key> {
	
	 VOL00 ("Volumen de operaciones",null,TITLE)
	,VOL01 (Mod390Key.GP_C108.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C108},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL02 (Mod390Key.GP_C083.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C083},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL03 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C084},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL04 (Mod390Key.GP_C109.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C109},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL05 (Mod390Key.GP_C110.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C110},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL06 (Mod390Key.GP_C085.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C085},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL07 (Mod390Key.GP_C086.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C086},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL08 (Mod390Key.GP_C087.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C087},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL09 (Mod390Key.GP_C111.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C111},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL10 (Mod390Key.GP_C088.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C088},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL11 (Mod390Key.GP_C112.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C112},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL12 (Mod390Key.GP_C113.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C113},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL13 (Mod390Key.GP_C091.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C091},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL14 (Mod390Key.GP_C092.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C092},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL15 (Mod390Key.GP_C093.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C093},MODEL_INVOICE_VAT_BREAKDOWN)
	,VOL16 (Mod390Key.GP_C095.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C095},COMPUTE)
	
	,OPE00 ("Operaciones de compra",null,TITLE)
	,OPE01 (Mod390Key.GP_C096.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C096},MODEL_INVOICE_VAT_BREAKDOWN)
	,OPE02 (Mod390Key.GP_C097.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C097},MODEL_INVOICE_VAT_BREAKDOWN)
	,OPE03 (Mod390Key.GP_C098.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C098},MODEL_INVOICE_VAT_BREAKDOWN)
	,OPE04 (Mod390Key.GP_C099.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C099},MODEL_INVOICE_VAT_BREAKDOWN)
	
	,ADC00 ("Exclusivamente para sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y para "+ 
		"destinatarios/as de operaciones afectadas por el mismo",null,TITLE)
	,ADC01 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen "+ 
		"especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida "+
		"en el art\u00EDculo 75 NFIVA"		,new Mod390Key[]{Mod390Key.GP_C101,Mod390Key.GP_C102},MODEL_OUT_VAT_ACCRUAL_INVOICE)	
	,ADC02 ("Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el r\u00E9gimen "+
		"especial del criterio de caja"		,new Mod390Key[]{Mod390Key.GP_C103,Mod390Key.GP_C104},MODEL_IN_VAT_ACCRUAL_INVOICE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902022GIPUZKOASpecificOperationsScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3902022GIPUZKOASpecificOperationsScript(String label, Mod390Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		return keyTypes;
	}
	
}
