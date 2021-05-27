package com.esferalia.aon.gwt.fiscal.shared.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEATGeneralRegimeScript2 implements IModelScript<Mod303Key> {
	
	 DED01 ("R\u00E9gimen general. IVA deducible",null,null,TITLE)
	,DED02 ("Por cuotas soportadas en operaciones interiores corrientes"
		,new Mod303Key[]{Mod303Key.CT_C28	,Mod303Key.CT_C29},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED03 ("Por cuotas soportadas en operaciones interiores con bienes de inversi\u00F3n" 
		,new Mod303Key[]{Mod303Key.CT_C30	,Mod303Key.CT_C31},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED04 ("Por cuotas soportadas en las importaciones de bienes corrientes"
		,new Mod303Key[]{Mod303Key.CT_C32	,Mod303Key.CT_C33},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED05 ("Por cuotas soportadas en las importaciones de bienes de inversi\u00F3n"
		,new Mod303Key[]{Mod303Key.CT_C34	,Mod303Key.CT_C35},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED06 ("En adquisiciones intracomunitarias de bienes y servicios corrientes"
		,new Mod303Key[]{Mod303Key.CT_C36	,Mod303Key.CT_C37},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED07 ("En adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,new Mod303Key[]{Mod303Key.CT_C38	,Mod303Key.CT_C39},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED08 ("Rectificaci\u00F3n de deducciones"
		,new Mod303Key[]{Mod303Key.CT_C40	,Mod303Key.CT_C41},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED09 ("Compensaciones R\u00E9gimen Especial A.G. y P."
		,new Mod303Key[]{null				,Mod303Key.CT_C42},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},INVOICE,DIFF_INVOICE)
	,DED10 ("Regularizaci\u00F3n inversiones"
		,new Mod303Key[]{null				,Mod303Key.CT_C43},null,NONE)
	,DED11 ("Regularizaci\u00F3n por aplicaci\u00F3n del porcentaje definitivo de prorrata"
		,new Mod303Key[]{null				,Mod303Key.CT_C44},null,NONE)
	,DED12 ("Total a deducir"
		,new Mod303Key[]{null				,Mod303Key.CT_C45},null,COMPUTE)
	,R01   ("Resultado r\u00E9gimen general"
		,new Mod303Key[]{null				,Mod303Key.CT_C46},null,COMPUTE)
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032017AEATGeneralRegimeScript2(String label, Mod303Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
	};
	
	@Override
	public KeyTypes[] getKeyTypes() {
		return keyTypes;
	}
}
