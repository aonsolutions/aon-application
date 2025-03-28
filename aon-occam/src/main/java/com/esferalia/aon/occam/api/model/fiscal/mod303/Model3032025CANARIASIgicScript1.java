package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_VAT_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032025CANARIASIgicScript1 implements IModelScript<Mod303Key> {
	
	 DVG01 ("IGIC DEVENGADO",null,TITLE)
	,DVG02 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C001, Mod303Key.CA_C002, Mod303Key.CA_C003},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG03 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C004, Mod303Key.CA_C005, Mod303Key.CA_C006},MODEL_INVOICE_VAT_BREAKDOWN) 
	,DVG04 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C007, Mod303Key.CA_C008, Mod303Key.CA_C009},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG05 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C010, Mod303Key.CA_C011, Mod303Key.CA_C012},MODEL_INVOICE_VAT_BREAKDOWN) 
	,DVG06 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C013, Mod303Key.CA_C014, Mod303Key.CA_C015},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG07 ("IGIC devengado"									, new Mod303Key[]{Mod303Key.CA_C016, Mod303Key.CA_C017, Mod303Key.CA_C018},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG08 ("Operaciones con inversi\u00F3n del sujeto pasivo"	, new Mod303Key[]{Mod303Key.CA_C019, null			  , Mod303Key.CA_C020},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG09 ("Modificaci\u00F3n bases y cuotas" 					, new Mod303Key[]{Mod303Key.CA_C021, null			  , Mod303Key.CA_C022},MODEL_INVOICE_VAT_BREAKDOWN)
	,DVG10 ("Cuotas devueltas en r\u00E9gimen de viajeros"		, new Mod303Key[]{Mod303Key.CA_C023, null			  , Mod303Key.CA_C024},NONE)
	,DVG11 (Mod303Key.CA_C025.getDescription()					, new Mod303Key[]{null			   , null			  , Mod303Key.CA_C025},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032025CANARIASIgicScript1(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	}
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == DVG01);
	}
	@Override
	public int getFieldSize(Mod303Key key) {
		if (key == Mod303Key.CA_C002 ||
			key == Mod303Key.CA_C005 ||
			key == Mod303Key.CA_C008 || 
			key == Mod303Key.CA_C011 || 
			key == Mod303Key.CA_C014 || 
			key == Mod303Key.CA_C017 ) {
			return PERCENT_FIELD_LENGTH;	
		}
		return IModelScript.super.getFieldSize(key);
	}
	
	// FALTA - LOS PORCENTAJES NO SE SI SERAN FIJOS O SE PODRA PONER CUALQUIERA EN CUALQUIER CASILLA
	//         SI SON FIJOS NO SE PUEDEN MODIFICAR, SI SE PUEDE PONER CUALQUIER PORCENTAJE SE DEJAN MODIFICAR
	//         POR AHORA SE PONEN QUE SE PUEDAN MODIFICAR
	@Override
	public boolean isEnabled(Mod303Key key) {
//		if (key == Mod303Key.CA_C002 ||
//				key == Mod303Key.CA_C005 ||
//				key == Mod303Key.CA_C008 || 
//				key == Mod303Key.CA_C011 || 
//				key == Mod303Key.CA_C014 || 
//				key == Mod303Key.CA_C017 ) {
//			return false;	
//		}
		return IModelScript.super.isEnabled(key);
	}
}
