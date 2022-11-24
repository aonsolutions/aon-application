package com.esferalia.aon.occam.api.model.fiscal.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model123AEAT2022Script implements IModelScript<Mod123Key> {
	
	 R00 ("Retenciones e ingresos a cuenta",new Mod123Key[]{Mod123Key.CT_C01,Mod123Key.CT_C02,Mod123Key.CT_C03},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R01 ("Periodificaci\u00F3n. Ingresos de ejercicios anteriores.",new Mod123Key[]{Mod123Key.CT_C04},NONE)
	,R02 ("Periodificaci\u00F3n. Regularizaci\u00F3n.",new Mod123Key[]{Mod123Key.CT_C05},NONE)
	,R03 ("Suma de retenciones e ingresos a cuenta.",new Mod123Key[]{Mod123Key.CT_C06},COMPUTE)
	,R04 ("A deducir. Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo." 
			,new Mod123Key[]{Mod123Key.CT_C07},COMPUTE_KEY)
	,R05 ("Resultado a ingresar",new Mod123Key[]{Mod123Key.CT_C08},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123AEAT2022Script(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod123Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
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
		return (this == R00);
	};
}
