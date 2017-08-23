package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEATResultScript implements IModelScript<Mod303Key> {
	
	 R04 ("Resultado",null,TITLE)
	,RES001("Regularizaci\u00F3n cuotas art. 80.cinco.5\u00AA LIVA"
		,new Mod303Key[]{Mod303Key.CT_C76},NONE)
	,RES002("Suma de resultados"
		,new Mod303Key[]{Mod303Key.CT_C64},COMPUTE)
	,RES003("% Atribuible a la Administraci\u00F3n del Estado"
		,new Mod303Key[]{Mod303Key.CT_C65},NONE)
	,RES004("Atribuible a la Administraci\u00F3n del Estado"
		,new Mod303Key[]{Mod303Key.CT_C66},COMPUTE)
	,RES005("IVA a la importaci\u00F3n liquidado por la Aduana pendiente de ingreso"  
		,new Mod303Key[]{Mod303Key.CT_C77},NONE)
	,RES006("Cuotas a compensar de periodos anteriores" 
		,new Mod303Key[]{Mod303Key.CT_C67},COMPUTE_KEY)
	,RES007("Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administraci\u00F3n "
			+"del Estado y a las Diputaciones Forales Resultado de la regularizaci\u00F3n anual" 
		,new Mod303Key[]{Mod303Key.CT_C68},NONE)
	,RES008("Resultado" 
		,new Mod303Key[]{Mod303Key.CT_C69},COMPUTE)
	,RES009("A deducir (exclusivamente en caso de autoliquidaci\u00F3n complementaria): Resultado de la anterior o anteriores declaraciones del mismo concepto, ejercicio y periodo"
		,new Mod303Key[]{Mod303Key.CT_C70},COMPUTE_KEY)
	,RES010("Resultado de la liquidaci\u00F3n" 
		,new Mod303Key[]{Mod303Key.CT_C71},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017AEATResultScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
