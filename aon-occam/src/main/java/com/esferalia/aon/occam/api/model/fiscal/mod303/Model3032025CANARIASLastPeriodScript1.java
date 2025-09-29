// MODELO 417 - 11.- Operaciones realizadas en el ejercicio
package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032025CANARIASLastPeriodScript1 implements IModelScript<Mod303Key> {
	
	 T01 ("Operaciones realizadas en el ejercicio",null,TITLE)	
	,C52(Mod303Key.CA_C052.getDescription(),new Mod303Key[]{Mod303Key.CA_C052},NONE)
	,C53(Mod303Key.CA_C053.getDescription(),new Mod303Key[]{Mod303Key.CA_C053},NONE)
	,C54(Mod303Key.CA_C054.getDescription(),new Mod303Key[]{Mod303Key.CA_C054},NONE)
	,C55(Mod303Key.CA_C055.getDescription(),new Mod303Key[]{Mod303Key.CA_C055},NONE)
	,C56(Mod303Key.CA_C056.getDescription(),new Mod303Key[]{Mod303Key.CA_C056},NONE)
	,C57(Mod303Key.CA_C057.getDescription(),new Mod303Key[]{Mod303Key.CA_C057},NONE)
	,C58(Mod303Key.CA_C058.getDescription(),new Mod303Key[]{Mod303Key.CA_C058},NONE)
	,C59(Mod303Key.CA_C059.getDescription(),new Mod303Key[]{Mod303Key.CA_C059},NONE)
	,C60(Mod303Key.CA_C060.getDescription(),new Mod303Key[]{Mod303Key.CA_C060},NONE)
	,C61(Mod303Key.CA_C061.getDescription(),new Mod303Key[]{Mod303Key.CA_C061},NONE)
	,C62(Mod303Key.CA_C062.getDescription(),new Mod303Key[]{Mod303Key.CA_C062},NONE)
	,C63(Mod303Key.CA_C063.getDescription(),new Mod303Key[]{Mod303Key.CA_C063},NONE)
	,C64(Mod303Key.CA_C064.getDescription(),new Mod303Key[]{Mod303Key.CA_C064},NONE)
	,C65(Mod303Key.CA_C065.getDescription(),new Mod303Key[]{Mod303Key.CA_C065},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032025CANARIASLastPeriodScript1(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
