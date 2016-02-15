package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public enum Model111AEATScript implements IModelScript {
	
	 R00 ("I. Rendimientos del trabajo",null, null)
	,R01 ("Rendimientos dinerarios" ,new Mod111Key[]{Mod111Key.CT_C01,Mod111Key.CT_C02,Mod111Key.CT_C03},SALARY)
	,R02 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C04,Mod111Key.CT_C05,Mod111Key.CT_C06},SALARY)
	,R03 ("II. Rendimientos de actividades econ\u00F3micas",null,null)
	,R04 ("Rendimientos dinerarios" ,new Mod111Key[]{Mod111Key.CT_C07,Mod111Key.CT_C08,Mod111Key.CT_C09},INVOICE)
	,R05 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C10,Mod111Key.CT_C11,Mod111Key.CT_C12},NONE)
	,R06 ("III. Premios por la participaci\u00F3n en juegos, concursos, rifas o combinaciones aleatorias",null,null)
	,R07 ("Rendimientos en met\u00E1lico"  ,new Mod111Key[]{Mod111Key.CT_C13,Mod111Key.CT_C14,Mod111Key.CT_C15},NONE)
	,R08 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C16,Mod111Key.CT_C17,Mod111Key.CT_C18},NONE)
	,R09 ("IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en los montes p\u00FAblicos",null,null)
	,R10 ("Rendimientos dinerarios" ,new Mod111Key[]{Mod111Key.CT_C19,Mod111Key.CT_C20,Mod111Key.CT_C21},NONE)
	,R11 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C22,Mod111Key.CT_C23,Mod111Key.CT_C24},NONE)
	,R12 ("V. Contraprestaciones por la cesi\u00F3n de derechos de imagen, ingresos a cuenta previstos en el art\u00EDculo 92.8 de la Ley del Impuesto ",null,null)
	,R13 ("Contraprestaciones dinerarias o en especie"  ,new Mod111Key[]{Mod111Key.CT_C25,Mod111Key.CT_C26,Mod111Key.CT_C27},NONE)
	,R14 ("Suma de retenciones e ingresos a cuenta",new Mod111Key[]{Mod111Key.CT_C28},COMPUTE)
	,R15 ("A deducir. Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo."
				  ,new Mod111Key[]{Mod111Key.CT_C29},NONE)
	,R16 ("Resultado a ingresar",new Mod111Key[]{Mod111Key.CT_C30},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private Mod111KeyInfo infoKey;
	
	private Model111AEATScript(String label, Mod111Key[] keys,Mod111KeyInfo infoKey) {
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
		return (getInfoKey() != null && getInfoKey() != COMPUTE);
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
