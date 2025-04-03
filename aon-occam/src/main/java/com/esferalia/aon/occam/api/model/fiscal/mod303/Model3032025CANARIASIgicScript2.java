package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032025CANARIASIgicScript2 implements IModelScript<Mod303Key> {
	
	 DED01 ("IGIC DEDUCIBLE Y RESULTADO AUTOLIQUIDACION",null,null,TITLE)
	,DED02 ("IGIC deducible en operaciones interiores bienes y servicios corrientes"
		,new Mod303Key[]{Mod303Key.CA_C026	,Mod303Key.CA_C027},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED03 ("IGIC deducible en operaciones interiores bienes de inversi\u00F3n" 
		,new Mod303Key[]{Mod303Key.CA_C028	,Mod303Key.CA_C029},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED04 ("IGIC deducible por importaciones de bienes corrientes"
		,new Mod303Key[]{Mod303Key.CA_C030	,Mod303Key.CA_C031},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED05 ("IGIC deducible por importaciones de bienes de inversi\u00F3n"
		,new Mod303Key[]{Mod303Key.CA_C032	,Mod303Key.CA_C033},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED06 ("Rectificaci\u00F3n de deducciones"
		,new Mod303Key[]{Mod303Key.CA_C034	,Mod303Key.CA_C035},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED07 ("Compensaciones r\u00E9gimen especial de agricultura, ganader\u00EDa y pesca"
		,new Mod303Key[]{null				,Mod303Key.CA_C036},new KeyTypes[]{KeyTypes.BASE,KeyTypes.DEDUCTIBLE_QUOTA},PRORRATED_MODEL_INVOICE_VAT_BREAKDOWN)
	,DED08 ("Regularizaci\u00F3n de cuotas soportadas por bienes de inversi\u00F3n"        , new Mod303Key[]{null,Mod303Key.CA_C037},null,NONE)
	,DED09 ("Regularizaci\u00F3n de cuotas soportadas antes del inicio de la actividad"    , new Mod303Key[]{null,Mod303Key.CA_C038},null,NONE)
	,DED10 ("Regularizaci\u00F3n por aplicaci\u00F3n del porcentaje definitivo de prorrata", new Mod303Key[]{null,Mod303Key.CA_C039},null,COMPUTE_KEY)
	,DED11 ("Total cuotas deducibles"                                                      , new Mod303Key[]{null,Mod303Key.CA_C040},null,COMPUTE)
	,DIF   ("Diferencia"                                                                   , new Mod303Key[]{null,Mod303Key.CA_C041},null,COMPUTE)
	,RES01 ("Regularizaci\u00F3n cuotas art\u00EDculo 22.8.5\u00AA Ley 20/1991"	   		   , new Mod303Key[]{null,Mod303Key.CA_C042},null,NONE)
	,RES02 ("Cuotas de I.G.I.C. a compensar pendientes de per\u00EDodos anteriores"        , new Mod303Key[]{null,Mod303Key.CA_C043},null,COMPUTE_KEY)
	,RES03 ("A deducir (exclusivamente en caso de autoliquidaci\u00F3n complementaria)"    , new Mod303Key[]{null,Mod303Key.CA_C044},null,COMPUTE_KEY)
	,RES04 ("Resultado de la autoliquidaci\u00F3n"                                         , new Mod303Key[]{null,Mod303Key.CA_C045},null,COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3032025CANARIASIgicScript2(String label, Mod303Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
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
		return keyTypes;
	}
}
