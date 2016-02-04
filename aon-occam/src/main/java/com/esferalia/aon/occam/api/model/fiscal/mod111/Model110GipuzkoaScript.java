package com.esferalia.aon.occam.api.model.fiscal.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public enum Model110GipuzkoaScript implements IModelScript {
	
	  R00 ("Retenciones. Rendimientos del trabajo"
			 ,new Mod111Key[]{Mod111Key.GP_C01,Mod111Key.GP_C02,Mod111Key.GP_C03},SALARY)
	 ,R01 ("Retenciones. Rendimientos de actividades econ\u00F3micas"
			 ,new Mod111Key[]{Mod111Key.GP_C04,Mod111Key.GP_C05,Mod111Key.GP_C06},INVOICE)
	 ,R02 ("Retenciones. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales"
			 ,new Mod111Key[]{Mod111Key.GP_C07,Mod111Key.GP_C08,Mod111Key.GP_C09},INVOICE)
	 ,R03 ("Retenciones. Premios"
			 ,new Mod111Key[]{Mod111Key.GP_C10,Mod111Key.GP_C11,Mod111Key.GP_C12},NONE)
	 ,R04 ("Total"
			 ,new Mod111Key[]{Mod111Key.GP_C13,Mod111Key.GP_C14},COMPUTE)
	 ,R05 ("Ingresos a cuenta. Rendimientos del trabajo"
			 ,new Mod111Key[]{Mod111Key.GP_C15,Mod111Key.GP_C16,Mod111Key.GP_C17},NONE)
	 ,R06 ("Ingresos a cuenta. Rendimientos de actividades econ\u00F3micas" 
			 ,new Mod111Key[]{Mod111Key.GP_C18,Mod111Key.GP_C19,Mod111Key.GP_C20},NONE)
	 ,R07 ("Ingresos a cuenta. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales" 
			 ,new Mod111Key[]{Mod111Key.GP_C21,Mod111Key.GP_C22,Mod111Key.GP_C23},NONE)
	 ,R08 ("Ingresos a cuenta. Premios" 
			 ,new Mod111Key[]{Mod111Key.GP_C24,Mod111Key.GP_C25,Mod111Key.GP_C26},NONE)
	 ,R09 ("Total",new Mod111Key[]{Mod111Key.GP_C27,Mod111Key.GP_C28},COMPUTE)
	 ,R10 ("A ingresar",new Mod111Key[]{Mod111Key.GP_C29},COMPUTE)
	;
	
	private String label;
	private Mod111Key[] keys;
	private Mod111KeyInfo infoKey;
	
	private Model110GipuzkoaScript(String label, Mod111Key[] keys,Mod111KeyInfo infoKey) {
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
