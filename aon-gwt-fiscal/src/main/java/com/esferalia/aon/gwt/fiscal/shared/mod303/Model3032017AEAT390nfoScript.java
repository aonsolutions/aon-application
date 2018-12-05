package com.esferalia.aon.gwt.fiscal.shared.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017AEAT390nfoScript implements IModelScript<Mod303Key> {
	 R00 ("Informaci\u00F3n de la tributaci\u00F3n por raz\u00F3n de territorio (s\u00F3lo para sujetos pasivos que tributan a varias Administraciones)",null,TITLE)
	,M39001 (Mod303Key.CT_C89.getDescription(),new Mod303Key[]{Mod303Key.CT_C89},NONE)
	,M39002 (Mod303Key.CT_C90.getDescription(),new Mod303Key[]{Mod303Key.CT_C90},NONE)
	,M39003 (Mod303Key.CT_C91.getDescription(),new Mod303Key[]{Mod303Key.CT_C91},NONE)
	,M39004 (Mod303Key.CT_C92.getDescription(),new Mod303Key[]{Mod303Key.CT_C92},NONE)
	,M390041(Mod303Key.CT_C107.getDescription(),new Mod303Key[]{Mod303Key.CT_C107},NONE)
	,R01 ("Operaciones realizadas en el ejercicio",null,TITLE)
	
	,M39005(Mod303Key.CT_C80.getDescription(),new Mod303Key[]{Mod303Key.CT_C80},NONE)
	,M39006(Mod303Key.CT_C81.getDescription(),new Mod303Key[]{Mod303Key.CT_C81},NONE)
	,M39007(Mod303Key.CT_C82.getDescription(),new Mod303Key[]{Mod303Key.CT_C82},NONE)
	,M39008(Mod303Key.CT_C93.getDescription(),new Mod303Key[]{Mod303Key.CT_C93},NONE)
	,M39009(Mod303Key.CT_C94.getDescription(),new Mod303Key[]{Mod303Key.CT_C94},NONE)
	,M39010(Mod303Key.CT_C83.getDescription(),new Mod303Key[]{Mod303Key.CT_C83},NONE)
	,M39011(Mod303Key.CT_C84.getDescription(),new Mod303Key[]{Mod303Key.CT_C84},NONE)
	,M39012(Mod303Key.CT_C85.getDescription(),new Mod303Key[]{Mod303Key.CT_C85},NONE)
	,M39013(Mod303Key.CT_C86.getDescription(),new Mod303Key[]{Mod303Key.CT_C86},NONE)
	,M39014(Mod303Key.CT_C95.getDescription(),new Mod303Key[]{Mod303Key.CT_C95},NONE)
	,M39015(Mod303Key.CT_C96.getDescription(),new Mod303Key[]{Mod303Key.CT_C96},NONE)
	,M39016(Mod303Key.CT_C97.getDescription(),new Mod303Key[]{Mod303Key.CT_C97},NONE)
	,M39017(Mod303Key.CT_C98.getDescription(),new Mod303Key[]{Mod303Key.CT_C98},NONE)
	,M39018(Mod303Key.CT_C79.getDescription(),new Mod303Key[]{Mod303Key.CT_C79},NONE)
	,M39019(Mod303Key.CT_C99.getDescription(),new Mod303Key[]{Mod303Key.CT_C99},NONE)
	,M39020(Mod303Key.CT_C87.getDescription(),new Mod303Key[]{Mod303Key.CT_C87},NONE)
	,M39021(Mod303Key.CT_C88.getDescription(),new Mod303Key[]{Mod303Key.CT_C88},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017AEAT390nfoScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
