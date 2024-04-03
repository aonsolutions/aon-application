package com.esferalia.aon.occam.api.model.fiscal.mod123;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model123AEAT2024Script implements IModelScript<Mod123Key> {
	
	 R00 ("Dividendos y otras rentas de participaci\u00F3n en fondos propios de entidades", new Mod123Key[]{Mod123Key.CT_C01,Mod123Key.CT_C04,Mod123Key.CT_C07},MODEL_INVOICE_IRPF_BREAKDOWN)
    ,R01 ("Resto de rentas", new Mod123Key[]{Mod123Key.CT_C02,Mod123Key.CT_C05,Mod123Key.CT_C08},MODEL_INVOICE_IRPF_BREAKDOWN)
    ,R02 ("Totales", new Mod123Key[]{Mod123Key.CT_C03,Mod123Key.CT_C06,Mod123Key.CT_C09},COMPUTE)
	,R03 ("Periodificaci\u00F3n: Ingresos de ejercicios anteriores",new Mod123Key[]{Mod123Key.CT_C10},NONE)
	,R04 ("Periodificaci\u00F3n: Regularizaci\u00F3n",new Mod123Key[]{Mod123Key.CT_C11},NONE)
	,R05 ("Suma de retenciones e ingresos a cuenta y regularizaci\u00F3n, en su caso",new Mod123Key[]{Mod123Key.CT_C12},COMPUTE)
	,R06 ("A deducir: Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo" 
			,new Mod123Key[]{Mod123Key.CT_C13},COMPUTE_KEY)
	,R07 ("Resultado a ingresar",new Mod123Key[]{Mod123Key.CT_C14},COMPUTE)
	;
	
	private String label;
	private Mod123Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model123AEAT2024Script(String label, Mod123Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
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
