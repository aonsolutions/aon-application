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

public enum Model3032017BIZKAIASpecificOperationsScript implements IModelScript<Mod303Key> {
	
	 ADC00 ("Exclusivamente para sujetos pasivos acogidos al r\u00E9gimen especial del criterio de caja y para "+ 
			"destinatarios/as de operaciones afectadas por el mismo",null,TITLE)
	,ADC01 ("Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen "+ 
			"especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida "+
			"en el art\u00EDculo 75 NFIVA"		,new Mod303Key[]{Mod303Key.BZ_C200,Mod303Key.BZ_C201},OUT_ACCRUAL_INVOICE,DIFF_INVOICE)	
	,ADC02 ("Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el r\u00E9gimen "+
			"especial del criterio de caja"		,new Mod303Key[]{Mod303Key.BZ_C202,Mod303Key.BZ_C203},IN_ACCRUAL_INVOICE,DIFF_INVOICE)
	,OPE00 ("Operaciones espec\u00EDficas",null,TITLE)
	,OPE01 (Mod303Key.BZ_C104.getDescription()	,new Mod303Key[]{null,Mod303Key.BZ_C104},INVOICE,DIFF_INVOICE)
	,OPE02 (Mod303Key.BZ_C105.getDescription()	,new Mod303Key[]{null,Mod303Key.BZ_C105},INVOICE,DIFF_INVOICE)
	,OPE03 (Mod303Key.BZ_C106.getDescription()	,new Mod303Key[]{null,Mod303Key.BZ_C106},INVOICE,DIFF_INVOICE)
	,OPE04 (Mod303Key.BZ_C107.getDescription()	,new Mod303Key[]{null,Mod303Key.BZ_C107},INVOICE,DIFF_INVOICE)
	,OPE05 (Mod303Key.BZ_C108.getDescription()	,new Mod303Key[]{null,Mod303Key.BZ_C108},INVOICE,DIFF_INVOICE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017BIZKAIASpecificOperationsScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	};
	
}
