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

enum Model111AEAT2023Script implements IModelScript<Mod111Key> {
	
	 R00 ("I. Rendimientos del trabajo")
	 	,R01 ("Rendimientos dinerarios" 
	 		,new Mod111Key[]{Mod111Key.CT_C01,Mod111Key.CT_C02,Mod111Key.CT_C03}
	 		,MODEL_SALARY_IRPF_BREAKDOWN)
	 	,R02 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C04,Mod111Key.CT_C05,Mod111Key.CT_C06}
	 		,MODEL_SALARY_IRPF_BREAKDOWN)
	
	,R03 ("II. Rendimientos de actividades econ\u00F3micas")
		,R04 ("Rendimientos dinerarios" ,new Mod111Key[]{Mod111Key.CT_C07,Mod111Key.CT_C08,Mod111Key.CT_C09}
			,MODEL_INVOICE_IRPF_BREAKDOWN)
		,R05 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C10,Mod111Key.CT_C11,Mod111Key.CT_C12},NONE)
		
	,R06 ("III. Premios por la participaci\u00F3n en juegos, concursos, rifas o combinaciones aleatorias")
		,R07 ("Rendimientos en met\u00E1lico"  ,new Mod111Key[]{Mod111Key.CT_C13,Mod111Key.CT_C14,Mod111Key.CT_C15}
			,MODEL_INVOICE_IRPF_BREAKDOWN)
		,R08 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C16,Mod111Key.CT_C17,Mod111Key.CT_C18},NONE)
		
	,R09 ("IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en los montes p\u00FAblicos")
		,R10 ("Rendimientos dinerarios" ,new Mod111Key[]{Mod111Key.CT_C19,Mod111Key.CT_C20,Mod111Key.CT_C21}
			,MODEL_INVOICE_IRPF_BREAKDOWN)
		,R11 ("Rendimientos en especie",new Mod111Key[]{Mod111Key.CT_C22,Mod111Key.CT_C23,Mod111Key.CT_C24},NONE)
		
	,R12 ("V. Contraprestaciones por la cesi\u00F3n de derechos de imagen, ingresos a cuenta previstos en el art\u00EDculo 92.8 de la Ley del Impuesto ")
		,R13 ("Contraprestaciones dinerarias o en especie"  ,new Mod111Key[]{Mod111Key.CT_C25,Mod111Key.CT_C26,Mod111Key.CT_C27}
			,MODEL_INVOICE_IRPF_BREAKDOWN)
		
	,R14 ("Suma de retenciones e ingresos a cuenta",new Mod111Key[]{Mod111Key.CT_C28},COMPUTE)
	,R15 ("A deducir. Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo.",new Mod111Key[]{Mod111Key.CT_C29},COMPUTE_KEY)
	,R16 ("Resultado a ingresar",new Mod111Key[]{Mod111Key.CT_C30},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model111AEAT2023Script(String label) {
		this( label,null,TITLE );
	}
	
	private Model111AEAT2023Script(String label, Mod111Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
