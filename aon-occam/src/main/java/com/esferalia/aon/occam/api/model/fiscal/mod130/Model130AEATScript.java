package com.esferalia.aon.occam.api.model.fiscal.mod130;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public enum Model130AEATScript implements IModelScript<Mod130Key> {
	
	 P00	("R\u00E9gimen de determinaci\u00F3n de rendimientos"
				,new Mod130Key[]{Mod130Key.P0},NONE)
	,P01	("Porcentaje de participaci\u00F3n"
				,new Mod130Key[]{Mod130Key.P1},NONE)
	,P02	("Realiza pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual."
			,new Mod130Key[]{Mod130Key.P2},NONE)
	,R00	("I. Actividades econ\u00F3micas en estimaci\u00F3n directa, modalidad "
	 		+ "normal o simplificada, distinta de las agr\u00EDcolas, ganadera"
	 		+ "s, forestales y pesqueras(Datos acumulados del per\u00EDodo com"
	 		+ "prendido entre el primer d\u00EDa del a\u00F1o y el \u00FAltimo d\u00EDa del t"
	 		+ "rimestre)",null,TITLE)
	,C01	("Ingresos computables correspondientes al conjunto de las act"
			+ "ividades ejercidas"
			,new Mod130Key[]{Mod130Key.C01},COMPUTE_KEY)
	,C02	("Gastos fiscalmente deducibles correspondientes al conjunto d"
			+ "e actividades ejercidas"
			,new Mod130Key[]{Mod130Key.C02},COMPUTE_KEY)
	,C03	("Rendimiento neto ([01]-[02]). Si se obtiene una cantidad neg"
			+ "ativa, cons\u00EDgnela con signo menos (-)."
			,new Mod130Key[]{Mod130Key.C03},COMPUTE)
	,C04	("20 por 100 del importe positivo de la casilla [03], si dicho"
			+ " importe es positivo"
			,new Mod130Key[]{Mod130Key.C04},COMPUTE)
	,C05	("A deducir. De los trimestres anteriores: suma de los importe"
			+ "s positivos de la casilla [07] menos la suma de los importe"
			+ "s de la casilla [16]"
			,new Mod130Key[]{Mod130Key.C05},COMPUTE_KEY)
	,C06	("A deducir. Retenciones e ingresos a cuenta soportados por la"
			+ "s actividades incluidas en este apartado y correspondientes"
			+ " al per\u00EDodo comprendido entre el primer d\u00EDa del a\u00F1o y el \u00FAl"
			+ "timo d\u00EDa del trimestre"
			,new Mod130Key[]{Mod130Key.C06},INVOICE,DIFF_INVOICE)
	,C07	("Pago fraccionado previo del trimestre ([04]-[05]-[06])"
			,new Mod130Key[]{Mod130Key.C07},COMPUTE)
	,R01	("II. Actividades agr\u00EDcolas, ganaderas, forestales y pesqueras "
			+ "en estimaci\u00F3n directa, modalidad normal o simplificada"
			,null,TITLE)
	,C08	("Volumen de ingresos del trimestre (excluidas las subvenciones"
			+ " de capital y las indemnizaciones)"
			,new Mod130Key[]{Mod130Key.C08},COMPUTE_KEY)
	,C09	("2 por 100 del importe de la casilla [08]"
			,new Mod130Key[]{Mod130Key.C09},COMPUTE)
	,C10	("A deducir: Retenciones e ingresos a cuenta soportados por las"
			+ " actividades incluidas en este apartado y correspondientes a"
			+ "l trimestre"
			,new Mod130Key[]{Mod130Key.C10},INVOICE,DIFF_INVOICE)
	,C11	("Pago fraccionado previo del trimestre ([09]-[10])"
			,new Mod130Key[]{Mod130Key.C11},COMPUTE)
	,R02	("III. Total liquidaci\u00F3n",null,TITLE)
	,C12	("Suma de pagos fraccionados previos del trimestre ([07]+[11])"
			,new Mod130Key[]{Mod130Key.C12},COMPUTE)
	,C13	("A deducir: Minoraci\u00F3n por aplicaci\u00F3n de la deducci\u00F3n a que se "
			+ "refiere el art\u00EDculo 110.3 c) del Reglamento del Impuesto"
			,new Mod130Key[]{Mod130Key.C131},NONE)
	,C14	("Diferencia ([12]-[13]). Si se obtiene una cantidad negativa, co"
			+ "ns\u00EDgnela con signo menos (-)"
			,new Mod130Key[]{Mod130Key.C14},COMPUTE)
	,C15	("Resultados negativos de trimestres anteriores"
			,new Mod130Key[]{Mod130Key.C15},NONE)
	,C16	("El 2 por 100 del [03] (m\u00E1ximo: 660,14 euros por trimestre) o el "
			+ "2 por 100 de [08] (m\u00E1ximo: 660,14 euros anuales)"
			,new Mod130Key[]{Mod130Key.C16},COMPUTE)
	,C17	("Total ([14]-[15]-[16])"
			,new Mod130Key[]{Mod130Key.C17},COMPUTE)
	,C18	("Resultado a ingresar de las anteriores autoliquidaciones presenta"
			+ "das por el mismo concepto, ejercicio y per\u00EDodo"
			,new Mod130Key[]{Mod130Key.C18},COMPUTE_KEY)
	,C19	("Resultado de la autoliquidaci\u00F3n ([17]-[18])"
			,new Mod130Key[]{Mod130Key.C19},COMPUTE)
	;
	
	private String label;
	private Mod130Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model130AEATScript(String label, Mod130Key[] keys,FiscalModelKeyInfo ... infoKeys ) {
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
		return (this == P00 || this == P01 || this == P02);
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R00);
	};
}
