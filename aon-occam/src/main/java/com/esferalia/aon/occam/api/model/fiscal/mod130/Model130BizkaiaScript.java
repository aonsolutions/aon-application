package com.esferalia.aon.occam.api.model.fiscal.mod130;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public enum Model130BizkaiaScript implements IModelScript<Mod130Key> {
	
	 R00	("R\u00E9gimen general",null,TITLE)
	,P03	("A\u00F1o",new Mod130Key[]{Mod130Key.P3},NONE)
	,C01	("Total rendimiento neto anual del pen\u00FAltimo a\u00F1o anterior",new Mod130Key[]{Mod130Key.C01},NONE)
	,C02	("Importe pago fraccionado (5%)",new Mod130Key[]{Mod130Key.C02},COMPUTE)
	,C03	("A deducir retenciones e ingresos a cuenta del pen\u00FAltimo a\u00F1o (25%)",new Mod130Key[]{Mod130Key.C03},NONE)
	,C04	("Cuota a ingresar ([002]-[003])",new Mod130Key[]{Mod130Key.C04},COMPUTE)
	,R01	("Actividades agr\u00EDcolas, ganaderas, forestales o pesqueras",null,TITLE)
	,C05	("Volumen de ventas o ingresos del trimestre",new Mod130Key[]{Mod130Key.C05},NONE)
	,C06	("Importe pago fraccionado (2%)",new Mod130Key[]{Mod130Key.C06},COMPUTE)
	,C07	("A deducir retenciones e ingresos a cuenta del trimestre",new Mod130Key[]{Mod130Key.C07},NONE)
	,C08	("Cuota a ingresar ([006]-[007])",new Mod130Key[]{Mod130Key.C08},COMPUTE)
	,R02	("R\u00E9gimen excepcional. Inicio actividad.",null,TITLE)
	,C09	("Rendimiento neto del trimestre (Ingresos computables - Gastos deducibles)",new Mod130Key[]{Mod130Key.C09},NONE)
	,C10	("Importe pago fraccionado",new Mod130Key[]{Mod130Key.C10},COMPUTE)
	,C11	("A deducir retenciones e ingresos a cuenta del trimestre",new Mod130Key[]{Mod130Key.C11},NONE)
	,C12	("Cuota a ingresar ([010]-[011])",new Mod130Key[]{Mod130Key.C12},COMPUTE)
	,R03	("General. Rendimiento neto negativo.",null,TITLE)
	,C15	("Volumen de ven. o ingr. del pen\u00FAltimo a\u00F1o anterior",new Mod130Key[]{Mod130Key.C15},NONE)
	,C16	("Importe pago fraccionado (0,5%)",new Mod130Key[]{Mod130Key.C16},COMPUTE)
	,C17	("A deducir retenciones e ingresos a cuenta del pen\u00FAltimo a\u00F1o (25%)",new Mod130Key[]{Mod130Key.C17},NONE)
	,C18	("Cuota a ingresar ([016]-[017])",new Mod130Key[]{Mod130Key.C18},COMPUTE)
	,R04	("Mayoristas. Rendimiento neto negativo.",null,TITLE)
	,C19	("Volumen de ventas o ingresos del pen\u00FAltimo a\u00F1o anterior",new Mod130Key[]{Mod130Key.C19},NONE)
	,C20	("Importe pago fraccionado (0,25%)",new Mod130Key[]{Mod130Key.C20},COMPUTE)
	,R05	("-",null,TITLE)
	,C28	("Total pago fraccionado ([004]+[008]+[012]+[018]+[020])",new Mod130Key[]{Mod130Key.C28},COMPUTE)
	;
	
	private String label;
	private Mod130Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model130BizkaiaScript(String label, Mod130Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod130Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != COMPUTE
			&& getInfoKeys()[0] != TITLE;
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
		return (this == R00);
	};
}
