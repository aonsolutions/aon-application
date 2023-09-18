package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_SALARY_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;


enum Model111Araba2022Script implements IModelScript<Mod111Key> {
	
	 R01("\u00BFHa sido declarado en concurso de acreedores en el presente per\u00EDodo de liquidaci\u00F3n?"
			,new Mod111Key[]{Mod111Key.AR_907},NONE)
	,R02("Si se ha dictado auto de declaraci\u00F3n de concurso en este per\u00EDodo, indique el tipo de autoliquidaci\u00F3n"
			,new Mod111Key[]{Mod111Key.AR_908},NONE)
	,R03("Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso"
			,new Mod111Key[]{Mod111Key.AR_909},NONE)
	,R04("Rendimientos procedentes del trabajo o servicios que se presten en el Territorio Hist\u00F3rico de \u00C1lava"
			,new Mod111Key[]{Mod111Key.AR_C50,Mod111Key.AR_C60,Mod111Key.AR_C70},MODEL_SALARY_IRPF_BREAKDOWN,MODEL_INVOICE_IRPF_BREAKDOWN)
	,R05("Pensiones"
			,new Mod111Key[]{Mod111Key.AR_C51,Mod111Key.AR_C61,Mod111Key.AR_C71},NONE)
	,R06("Retribuciones de los miembros del Consejo de Administraci\u00F3n y Juntas que hagan sus veces de entidades con domicilio fiscal en \u00C1lava"
			,new Mod111Key[]{Mod111Key.AR_C52,Mod111Key.AR_C62,Mod111Key.AR_C72},NONE)
	,R07("Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributan en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del volumen de operaciones)"
			,new Mod111Key[]{Mod111Key.AR_C53,Mod111Key.AR_C63,Mod111Key.AR_C73},NONE)
	,R08("Rendimientos satisfechos por contraprestaciones profesionales"
			,new Mod111Key[]{Mod111Key.AR_C54,Mod111Key.AR_C64,Mod111Key.AR_C74},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R09("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad signos, \u00EDndices o m\u00F3dulos"
			,new Mod111Key[]{Mod111Key.AR_C58,Mod111Key.AR_C68,Mod111Key.AR_C78},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R10("Rendimientos satisfechos por contraprestaciones de actividades agr\u00EDcolas, ganaderas y forestales"
			,new Mod111Key[]{Mod111Key.AR_C55,Mod111Key.AR_C65,Mod111Key.AR_C75},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R11("Premios satisfechos (en met\u00E1lico y en especie)"
			,new Mod111Key[]{Mod111Key.AR_C56,Mod111Key.AR_C66,Mod111Key.AR_C76},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R12("Retribuciones en especie y otras, excepto las correspondientes a Premios"
			,new Mod111Key[]{Mod111Key.AR_C57,Mod111Key.AR_C67,Mod111Key.AR_C77},MODEL_SALARY_IRPF_BREAKDOWN,MODEL_INVOICE_IRPF_BREAKDOWN)
	,R13 ("Total",new Mod111Key[]{Mod111Key.AR_C80,Mod111Key.AR_C81,Mod111Key.AR_C82},COMPUTE)
	,R14("Ajustes",new Mod111Key[]{Mod111Key.AR_C83},COMPUTE_KEY)
	,R15("Recargo pr\u00F3rroga",new Mod111Key[]{Mod111Key.AR_C84},NONE)
	,R16("Intereses de demora",new Mod111Key[]{Mod111Key.AR_C85},NONE)
	,R17("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.AR_C87},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model111Araba2022Script(String label, Mod111Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
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
	public boolean hasGraphicParticularity() {
		return (this == R01 || this == R02 || this == R03);
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
	public boolean paintHeaderBefore() {
		return (this == R04);
	};
}
