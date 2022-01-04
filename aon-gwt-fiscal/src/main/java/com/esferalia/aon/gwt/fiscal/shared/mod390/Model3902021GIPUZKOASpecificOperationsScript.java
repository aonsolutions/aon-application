package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.IN_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902021GIPUZKOASpecificOperationsScript implements IModelScript<Mod390Key> {
	
	 VOL00 ("Volumen de operaciones",null,TITLE)
	,VOL01 (Mod390Key.GP_C082.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C108},INVOICE)
	,VOL02 (Mod390Key.GP_C083.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C083},INVOICE)
	,VOL03 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C084},INVOICE)
	,VOL04 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C109},INVOICE)
	,VOL05 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C110},INVOICE)
	,VOL06 (Mod390Key.GP_C085.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C085},INVOICE)
	,VOL07 (Mod390Key.GP_C086.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C086},INVOICE)
	,VOL08 (Mod390Key.GP_C087.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C087},INVOICE)
	,VOL09 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C111},INVOICE)
	,VOL10 (Mod390Key.GP_C088.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C088},INVOICE)
	,VOL11 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C112},INVOICE)
	,VOL12 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C113},INVOICE)
	,VOL13 (Mod390Key.GP_C091.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C091},INVOICE)
	,VOL14 (Mod390Key.GP_C092.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C092},INVOICE)
	,VOL15 (Mod390Key.GP_C093.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C093},INVOICE)
	,VOL16 (Mod390Key.GP_C095.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C095},COMPUTE)
	
	,OPE00 ("Operaciones de compra",null,TITLE)
	,OPE01 (Mod390Key.GP_C096.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C096},INVOICE)
	,OPE02 (Mod390Key.GP_C097.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C097},INVOICE)
	,OPE03 (Mod390Key.GP_C098.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C098},INVOICE)
	,OPE04 (Mod390Key.GP_C099.getDescription()	,new Mod390Key[]{null,Mod390Key.GP_C099},INVOICE)
	
	,ADC00 ("Exclusivamente para sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y para "+ 
		"destinatarios/as de operaciones afectadas por el mismo",null,TITLE)
	,ADC01 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen "+ 
		"especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida "+
		"en el art\u00EDculo 75 NFIVA"		,new Mod390Key[]{Mod390Key.GP_C101,Mod390Key.GP_C102},OUT_ACCRUAL_INVOICE)	
	,ADC02 ("Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el r\u00E9gimen "+
		"especial del criterio de caja"		,new Mod390Key[]{Mod390Key.GP_C103,Mod390Key.GP_C104},IN_ACCRUAL_INVOICE)
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902021GIPUZKOASpecificOperationsScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3902021GIPUZKOASpecificOperationsScript(String label, Mod390Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
