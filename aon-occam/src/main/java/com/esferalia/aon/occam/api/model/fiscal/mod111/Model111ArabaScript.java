package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public enum Model111ArabaScript implements IModelScript {
	
	 R00 ("Rendimientos procedentes del trabajo o servicios que se presten en el Territorio Hist\u00F3rico de \u00C1lava"
			,new Mod111Key[]{Mod111Key.AR_C50,Mod111Key.AR_C60,Mod111Key.AR_C70},SALARY)
	,R01 ("Pensiones"
			,new Mod111Key[]{Mod111Key.AR_C51,Mod111Key.AR_C61,Mod111Key.AR_C71},NONE)
	,R02 ("Retribuciones de los miembros del Consejo de Administraci\u00F3n y Juntas que hagan sus veces de entidades con domicilio fiscal en \u00C1lava"
			,new Mod111Key[]{Mod111Key.AR_C52,Mod111Key.AR_C62,Mod111Key.AR_C72},NONE)
	,R03 ("Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributan en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del volumen de operaciones)"
			,new Mod111Key[]{Mod111Key.AR_C53,Mod111Key.AR_C63,Mod111Key.AR_C73},NONE)
	,R04 ("Rendimientos satisfechos por contraprestaciones profesionales"
			,new Mod111Key[]{Mod111Key.AR_C54,Mod111Key.AR_C64,Mod111Key.AR_C74},INVOICE)
	,R05 ("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad signos, \u00EDndices o m\u00F3dulos"
			,new Mod111Key[]{Mod111Key.AR_C58,Mod111Key.AR_C68,Mod111Key.AR_C78},INVOICE)
	,R06 ("Rendimientos satisfechos por contraprestaciones de actividades agr\u00EDcolas, ganaderas y forestales"
			,new Mod111Key[]{Mod111Key.AR_C55,Mod111Key.AR_C65,Mod111Key.AR_C75},INVOICE)
	,R07 ("Premios satisfechos (en met\u00E1lico y en especie)"
			,new Mod111Key[]{Mod111Key.AR_C56,Mod111Key.AR_C66,Mod111Key.AR_C76},NONE)
	,R08 ("Retribuciones en especie y otras, excepto las correspondientes a Premios"
			,new Mod111Key[]{Mod111Key.AR_C57,Mod111Key.AR_C67,Mod111Key.AR_C77},SALARY)
	,R09 ("Total"
			,new Mod111Key[]{Mod111Key.AR_C82},COMPUTE)
	,R10("Recargo pr\u00F3rroga"
			,new Mod111Key[]{Mod111Key.AR_C84},NONE)
	,R11("Intereses de demora"
			,new Mod111Key[]{Mod111Key.AR_C85},NONE)
	,R12("Deuda tributaria a ingresar"
			,new Mod111Key[]{Mod111Key.AR_C87},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private Mod111KeyInfo infoKey;
	
	private Model111ArabaScript(String label, Mod111Key[] keys,Mod111KeyInfo infoKey) {
		this.label = label;
		this.keys = keys;
		this.infoKey = infoKey;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod111Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKey() != COMPUTE;
	}
	@Override
	public Mod111KeyInfo getInfoKey() {
		return infoKey;
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
