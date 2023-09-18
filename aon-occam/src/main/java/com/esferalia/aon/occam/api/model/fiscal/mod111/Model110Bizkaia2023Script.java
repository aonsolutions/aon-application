package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_SALARY_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

enum Model110Bizkaia2023Script implements IModelScript<Mod111Key> {
	
	 R00 ("Rendimientos procedentes de trabajos o servicios que se presten en Bizkaia"
			 ,new Mod111Key[]{Mod111Key.BZ_C01,Mod111Key.BZ_C12,Mod111Key.BZ_C23},MODEL_SALARY_IRPF_BREAKDOWN,MODEL_INVOICE_IRPF_BREAKDOWN)
	,R01 ("Retribuciones de miembros de Consejos de Administraci\u00F3n y Juntas que hagan sus veces de empresas o entidades con domicilio fiscal en Bizkaia"
			,new Mod111Key[]{Mod111Key.BZ_C02,Mod111Key.BZ_C13,Mod111Key.BZ_C24},NONE)
	,R02 ("Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributen en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del porcentaje)"
			,new Mod111Key[]{Mod111Key.BZ_C03,Mod111Key.BZ_C14,Mod111Key.BZ_C25},NONE)
	,R03 ("Rendimientos de trabajo en per\u00EDodos inferiores al a\u00F1o, trabajos de temporada o trabajos circunstanciales"
			,new Mod111Key[]{Mod111Key.BZ_C04,Mod111Key.BZ_C15,Mod111Key.BZ_C26},NONE)
	,R04 ("Prestaciones por desempleo"
			,new Mod111Key[]{Mod111Key.BZ_C05,Mod111Key.BZ_C16,Mod111Key.BZ_C27},NONE)
	,R05 ("Pensiones y haberes pasivos"
			,new Mod111Key[]{Mod111Key.BZ_C06,Mod111Key.BZ_C17,Mod111Key.BZ_C28},NONE)
	,R06 ("Rendimientos satisfechos por contraprestaciones profesionales, art\u00EDsticas o deportivas y retribuciones de comisionistas, agentes comerciales, agentes de seguros y subagentes"
			,new Mod111Key[]{Mod111Key.BZ_C07,Mod111Key.BZ_C18,Mod111Key.BZ_C29},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R07 ("Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad de signos, \u00EDndices o m\u00F3dulos"
			,new Mod111Key[]{Mod111Key.BZ_C50,Mod111Key.BZ_C51,Mod111Key.BZ_C52},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R08 ("Retenciones sobre rendimientos de actividades agr\u00EDcolas, ganaderas y forestales"
			,new Mod111Key[]{Mod111Key.BZ_C08,Mod111Key.BZ_C19,Mod111Key.BZ_C30},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R09 ("Retribuciones en especie"
			,new Mod111Key[]{Mod111Key.BZ_C09,Mod111Key.BZ_C20,Mod111Key.BZ_C31},MODEL_SALARY_IRPF_BREAKDOWN)
	,R10 ("Premios"
			,new Mod111Key[]{Mod111Key.BZ_C10,Mod111Key.BZ_C21,Mod111Key.BZ_C32},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R11 ("Rendimientos no comprendidos en apartados anteriores"
			,new Mod111Key[]{Mod111Key.BZ_C11,Mod111Key.BZ_C22,Mod111Key.BZ_C33},MODEL_INVOICE_IRPF_BREAKDOWN)
	,R12 ("Totales",new Mod111Key[]{Mod111Key.BZ_C34T,Mod111Key.BZ_C35T,Mod111Key.BZ_C36T},COMPUTE)
	,R13 ("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.BZ_C39},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model110Bizkaia2023Script(String label, Mod111Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == R00);
	};
}
