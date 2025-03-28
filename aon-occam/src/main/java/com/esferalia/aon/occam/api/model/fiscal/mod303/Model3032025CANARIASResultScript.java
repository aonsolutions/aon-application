package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032025CANARIASResultScript implements IModelScript<Mod303Key> {
	
	 R04 ("Resultado",null,TITLE)
	,RES001("Regularizaci\u00F3n cuotas cuotas art\u00EDculo 22.8.5\u00AA Ley 20/1991",  new Mod303Key[]{Mod303Key.CA_C042},NONE)
	,RES002("Cuotas de I.G.I.C. a compensar pendientes de per\u00EDodos anteriores",     new Mod303Key[]{Mod303Key.CA_C043},COMPUTE_KEY)
	,RES003("A deducir (exclusivamente en caso de autoliquidaci\u00F3n complementaria)", new Mod303Key[]{Mod303Key.CA_C044},COMPUTE_KEY)
	,RES004("Resultado de la autoliquidaci\u00F3n",                                      new Mod303Key[]{Mod303Key.CA_C045},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032025CANARIASResultScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	}
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	}

}
