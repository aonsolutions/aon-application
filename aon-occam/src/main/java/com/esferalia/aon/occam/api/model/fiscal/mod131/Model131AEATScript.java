package com.esferalia.aon.occam.api.model.fiscal.mod131;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.IRPF_ACTIVITY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public enum Model131AEATScript implements IModelScript<Mod131Key> {
	
	 P01	("Porcentaje de participaci\u00F3n"
		,new Mod131Key[]{Mod131Key.P1},NONE)
	,P02	("Realiza pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual."
		,new Mod131Key[]{Mod131Key.P2},NONE)
	,R00	("I. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales.",null,TITLE)
	,ACT1	("1",new Mod131Key[]{Mod131Key.AC1_EPI,Mod131Key.AC1_EPD,Mod131Key.AC1_NET,Mod131Key.AC1_POR,Mod131Key.AC1_RES},IRPF_ACTIVITY)
	,ACT2	("2",new Mod131Key[]{Mod131Key.AC2_EPI,Mod131Key.AC2_EPD,Mod131Key.AC2_NET,Mod131Key.AC2_POR,Mod131Key.AC2_RES},IRPF_ACTIVITY)
	,ACT3	("3",new Mod131Key[]{Mod131Key.AC3_EPI,Mod131Key.AC3_EPD,Mod131Key.AC3_NET,Mod131Key.AC3_POR,Mod131Key.AC3_RES},IRPF_ACTIVITY)
	,ACT4	("4",new Mod131Key[]{Mod131Key.AC4_EPI,Mod131Key.AC4_EPD,Mod131Key.AC4_NET,Mod131Key.AC4_POR,Mod131Key.AC4_RES},IRPF_ACTIVITY)
	,ACT5	("5",new Mod131Key[]{Mod131Key.AC5_EPI,Mod131Key.AC5_EPD,Mod131Key.AC5_NET,Mod131Key.AC5_POR,Mod131Key.AC5_RES},IRPF_ACTIVITY)
	,C01	("Suma de rendimientos netos"
		,new Mod131Key[]{Mod131Key.C01},COMPUTE)
	,C02	("Pago fraccionado previo del trimestre. Suma de resultados."
		,new Mod131Key[]{Mod131Key.C02},COMPUTE)
	,R01	("II. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales, sin posibilidad de determinar ninguno de los datos-base a efectos del pago fraccionado.",null,TITLE)
	,C03	("Volumen de ventas o  ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)"
		,new Mod131Key[]{Mod131Key.C03},NONE)
	,C04	("Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 03."
		,new Mod131Key[]{Mod131Key.C04},COMPUTE)
	,R02	("III. Actividades agr\u00EDcolas, ganaderas y forestales, en estimaci\u00F3n objetiva.",null,TITLE)		
	,C05	("Volumen de ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)"
		,new Mod131Key[]{Mod131Key.C05},NONE)
	,C06	("Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 05."
		,new Mod131Key[]{Mod131Key.C06},COMPUTE)
	,R03	("IV. Total liquidaci\u00F3n.",null,TITLE)
	,C07	("Suma de los pagos fraccionados previos de trimestre (02 + 04 + 06)"
		,new Mod131Key[]{Mod131Key.C07},COMPUTE)
	,C08	("A deducir. Retenciones e ingresos a cuenta soportados correspondientes al trimestre."
		,new Mod131Key[]{Mod131Key.C08},COMPUTE_KEY)
	,C09	("A deducir. Minoraci\u00F3n por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 110.3 C) del reglamento del impuesto."
		,new Mod131Key[]{Mod131Key.C091},NONE)
	,C10	("Diferencia 		([07] - [08] - [09]). Si se obtiene una cantidad negativa, cons\u00EDgnela con signo menos (-)"
		,new Mod131Key[]{Mod131Key.C10},COMPUTE)
	,C11	("Resultados negativos de trimestres anteriores."
		,new Mod131Key[]{Mod131Key.C11},COMPUTE_KEY)
	,C12	("Por destinar cantidades al pago de pr\u00E9stamos por adquisici\u00F3n o rehabilitaci\u00F3n de vivienda habitual."
		,new Mod131Key[]{Mod131Key.C12},COMPUTE_KEY)
	,C13	("Total ([10] - [11] - [12]). Si se obtiene una cantidad negativa, cons\u00EDgnela con signo menos (-)"
		,new Mod131Key[]{Mod131Key.C13},COMPUTE)
	,C14	("A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo."
		,new Mod131Key[]{Mod131Key.C14},COMPUTE_KEY)
	,C15	("Resultado de la declaraci\u00F3n		([13] - [14]). Si se obtiene una cantidad negativa, cons\u00EDgnela con signo menos (-)"
		,new Mod131Key[]{Mod131Key.C15},COMPUTE)
	;
	
	private String label;
	private Mod131Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model131AEATScript(String label, Mod131Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod131Key[] getKeys() {
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
		return (this == P01 
			 || this == P02
			 || this == ACT1
			 || this == ACT2
			 || this == ACT3
			 || this == ACT4
			 || this == ACT5
				);
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
