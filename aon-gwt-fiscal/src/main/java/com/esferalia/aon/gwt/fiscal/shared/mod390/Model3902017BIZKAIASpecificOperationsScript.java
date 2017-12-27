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

public enum Model3902017BIZKAIASpecificOperationsScript implements IModelScript<Mod390Key> {
	
	 ADC00 ("Exclusivamente para sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y para "+ 
			"destinatarios/as de operaciones afectadas por el mismo",null,TITLE)
	,ADC01 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen "+ 
			"especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida "+
			"en el art\u00EDculo 75 NFIVA"		,new Mod390Key[]{Mod390Key.BZ_C130,Mod390Key.BZ_C131},OUT_ACCRUAL_INVOICE)	
	,ADC02 ("Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el r\u00E9gimen "+
			"especial del criterio de caja"		,new Mod390Key[]{Mod390Key.BZ_C132,Mod390Key.BZ_C133},IN_ACCRUAL_INVOICE)
	,VOL00 ("Volumen de operaciones",null,TITLE)
	,VOL01 (Mod390Key.BZ_C200.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C200},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL02 (Mod390Key.BZ_C201.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C201},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL03 (Mod390Key.BZ_C202.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C202},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL04 (Mod390Key.BZ_C203.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C203},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL05 (Mod390Key.BZ_C204.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C204},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL06 (Mod390Key.BZ_C205.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C205},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL07 (Mod390Key.BZ_C206.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C206},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL08 (Mod390Key.BZ_C207.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C207},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL09 (Mod390Key.BZ_C208.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C208},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL10 (Mod390Key.BZ_C209.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C209},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL11 (Mod390Key.BZ_C210.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C210},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL12 (Mod390Key.BZ_C211.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C211},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL13 (Mod390Key.BZ_C212.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C212},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL14 (Mod390Key.BZ_C213.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C213},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL15 (Mod390Key.BZ_C214.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C214},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,VOL16 (Mod390Key.BZ_C215.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C215},new KeyTypes[]{KeyTypes.NONE,KeyTypes.NONE},COMPUTE)
	
	,OPE00 ("Operaciones espec\u00EDficas",null,TITLE)
	,OPE01 (Mod390Key.BZ_C220.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C220},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,OPE02 (Mod390Key.BZ_C221.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C221},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,OPE03 (Mod390Key.BZ_C222.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C222},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	,OPE04 (Mod390Key.BZ_C223.getDescription()	,new Mod390Key[]{null,Mod390Key.BZ_C223},new KeyTypes[]{KeyTypes.NONE,KeyTypes.BASE},INVOICE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902017BIZKAIASpecificOperationsScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this(label,keys,null,infoKeys);	
	}
	
	private Model3902017BIZKAIASpecificOperationsScript(String label, Mod390Key[] keys, KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
