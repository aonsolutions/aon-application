package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public enum Model111BizkaiaScript implements IModelScript {
	 R00 ("Rendimientos procedentes de trabajos o servicios que se presten en Bizkaia"
			 ,new Mod111Key[]{Mod111Key.BZ_C01,Mod111Key.BZ_C12,Mod111Key.BZ_C23},SALARY)
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
			,new Mod111Key[]{Mod111Key.BZ_C07,Mod111Key.BZ_C18,Mod111Key.BZ_C29},INVOICE)
	,R08 ("Retenciones sobre rendimientos de actividades agr\u00EDcolas, ganaderas y forestales"
			,new Mod111Key[]{Mod111Key.BZ_C08,Mod111Key.BZ_C19,Mod111Key.BZ_C30},INVOICE)
	,R09 ("Retribuciones en especie"
			,new Mod111Key[]{Mod111Key.BZ_C09,Mod111Key.BZ_C20,Mod111Key.BZ_C31},SALARY)
	,R10 ("Premios"
			,new Mod111Key[]{Mod111Key.BZ_C10,Mod111Key.BZ_C21,Mod111Key.BZ_C32},NONE)
	,R11 ("Rendimientos no comprendidos en apartados anteriores"
			,new Mod111Key[]{Mod111Key.BZ_C11,Mod111Key.BZ_C22,Mod111Key.BZ_C33},NONE)
	,R12 ("Totales",new Mod111Key[]{Mod111Key.BZ_C34T,Mod111Key.BZ_C35T,Mod111Key.BZ_C36T},COMPUTE)
	,R13 ("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.BZ_C39},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private Mod111KeyInfo infoKey;
	
	private Model111BizkaiaScript(String label, Mod111Key[] keys,Mod111KeyInfo infoKey) {
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
