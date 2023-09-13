package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_INVOICE_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.MODEL_SALARY_IRPF_BREAKDOWN;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public enum Model110Gipuzkoa2023Script implements IModelScript<Mod111Key> {
	
	  R00 ("Retenciones. Rendimientos del trabajo"
			 ,new Mod111Key[]{Mod111Key.GP_C01,Mod111Key.GP_C02,Mod111Key.GP_C03},MODEL_SALARY_IRPF_BREAKDOWN,MODEL_INVOICE_IRPF_BREAKDOWN)
	 ,R01 ("Retenciones. Rendimientos de actividades econ\u00F3micas"
			 ,new Mod111Key[]{Mod111Key.GP_C04,Mod111Key.GP_C05,Mod111Key.GP_C06},MODEL_INVOICE_IRPF_BREAKDOWN)
	 ,R02 ("Retenciones. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales"
			 ,new Mod111Key[]{Mod111Key.GP_C07,Mod111Key.GP_C08,Mod111Key.GP_C09},MODEL_INVOICE_IRPF_BREAKDOWN)
	 ,R03 ("Retenciones. Premios"
			 ,new Mod111Key[]{Mod111Key.GP_C10,Mod111Key.GP_C11,Mod111Key.GP_C12},MODEL_INVOICE_IRPF_BREAKDOWN)
	 ,R04 ("Total"
			 ,new Mod111Key[]{Mod111Key.GP_C13,Mod111Key.GP_C14},COMPUTE)
	 ,R05 ("Ingresos a cuenta. Rendimientos del trabajo"
			 ,new Mod111Key[]{Mod111Key.GP_C15,Mod111Key.GP_C16,Mod111Key.GP_C17},MODEL_SALARY_IRPF_BREAKDOWN)
	 ,R06 ("Ingresos a cuenta. Rendimientos de actividades econ\u00F3micas" 
			 ,new Mod111Key[]{Mod111Key.GP_C18,Mod111Key.GP_C19,Mod111Key.GP_C20},NONE)
	 ,R07 ("Ingresos a cuenta. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales" 
			 ,new Mod111Key[]{Mod111Key.GP_C21,Mod111Key.GP_C22,Mod111Key.GP_C23},NONE)
	 ,R08 ("Ingresos a cuenta. Premios" 
			 ,new Mod111Key[]{Mod111Key.GP_C24,Mod111Key.GP_C25,Mod111Key.GP_C26},NONE)
	 ,R09 ("Total",new Mod111Key[]{Mod111Key.GP_C27,Mod111Key.GP_C28},COMPUTE)
	 ,R10 ("A ingresar",new Mod111Key[]{Mod111Key.GP_C29},COMPUTE)
	 ,X00 ("NIF del Presentador telem\u00E1tico (en caso de ser diferente del declarante)",new Mod111Key[]{Mod111Key.GP_X00},NONE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model110Gipuzkoa2023Script(String label, Mod111Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == X00);
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
