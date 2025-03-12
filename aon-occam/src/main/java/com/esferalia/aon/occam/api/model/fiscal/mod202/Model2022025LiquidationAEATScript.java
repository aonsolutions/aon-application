package com.esferalia.aon.occam.api.model.fiscal.mod202;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.ACT_ACCOUNT;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public enum Model2022025LiquidationAEATScript implements IModelScript<Mod202Key> {
	
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	 X00 (Mod202Key.X00.getDescription(),new Mod202Key[]{Mod202Key.X00},NONE)
	
	,A	 ("A) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.2 LIS",null,TITLE)
	,AC01(Mod202Key.C01.getDescription(),new Mod202Key[]{Mod202Key.C01},COMPUTE_KEY)
	,AC02(Mod202Key.C02.getDescription(),new Mod202Key[]{Mod202Key.C02},COMPUTE_KEY)
	,AC03(Mod202Key.C03.getDescription(),new Mod202Key[]{Mod202Key.C03},COMPUTE_KEY)
	
	,B 	 ("B) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.3 LIS",null,TITLE)
		,B04 	(Mod202Key.C04.getDescription(),new Mod202Key[]{Mod202Key.C04},COMPUTE_KEY,ACT_ACCOUNT)
	,BC	 ("Correcciones al resultado contable:",null,NONE)
		,BCC05_BC06	("Correcci\u00F3n por Impuesto sobre Sociedades (Aumentos/Disminuciones)",new Mod202Key[]{Mod202Key.C05,Mod202Key.C06},NONE)
		,BCC67 		("Correcci\u00F3n por Impuesto Complementario (IC) (Aumentos)",new Mod202Key[]{Mod202Key.C67,null},NONE)
		,BCC37 		(Mod202Key.C37.getDescription() + " (Disminuciones)",new Mod202Key[]{null,Mod202Key.C37},NONE)
		,BCC07_C08	("Resto correcciones al resultado contable, excepto comp. BI negativa ej. ant. (Aumentos/Disminuciones)",new Mod202Key[]{Mod202Key.C07,Mod202Key.C08},NONE)
		,BCC38_C39	("TOTAL (Aumentos/Disminuciones)",new Mod202Key[]{Mod202Key.C38,Mod202Key.C39},COMPUTE_KEY)
	,BC13		(Mod202Key.C13.getDescription(),new Mod202Key[]{Mod202Key.C13},COMPUTE_KEY)
	,BC44		(Mod202Key.C44.getDescription(),new Mod202Key[]{Mod202Key.C44},NONE)
	,BC14		(Mod202Key.C14.getDescription(),new Mod202Key[]{Mod202Key.C14},NONE)
	,B45_B46	("Reserva de nivelaci\u00F3n (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicaci\u00F3n de los incentivos de empresa de reducida dimensi\u00F3n (art. 101 LIS) y apliquen el tipo de gravamen espec\u00EDfico previsto para estas entidades) (Aumentos/Disminuciones)"
				,new Mod202Key[]{Mod202Key.C45,Mod202Key.C46},NONE)
	
	,B1 ("B.1) Caso general (entidades con porcentaje \u00FAnico)",null,TITLE)
		,B1C16	(Mod202Key.C16.getDescription(),new Mod202Key[]{Mod202Key.C16},COMPUTE_KEY)
		,B1C17	(Mod202Key.C17.getDescription(),new Mod202Key[]{Mod202Key.C17},COMPUTE_KEY)
		,B1C47	(Mod202Key.C47.getDescription(),new Mod202Key[]{Mod202Key.C47},NONE)
		,B1C40	(Mod202Key.C40.getDescription(),new Mod202Key[]{Mod202Key.C40},NONE)
		,B1C48_C49 ("Reserva de nivelaci\u00F3n (art. 105 LIS) convertido en cuotas (solo entidades que cumplan los requisitos para la aplicaci\u00F3n de los incentivos de empresa de reducida dimensi\u00F3n (art. 101 LIS) y apliquen el tipo de gravamen espec\u00EDfico previsto para estas entidades) (Aumentos/Disminuciones)"
				,new Mod202Key[]{Mod202Key.C48,Mod202Key.C49},NONE)
		,B1C18	(Mod202Key.C18.getDescription(),new Mod202Key[]{Mod202Key.C18},COMPUTE_KEY)

	,B2	("B.2) Casos espec\u00EDficos (entidades con m\u00E1s de un porcentaje)",null,TITLE)
		,B2C18			(Mod202Key.C19.getDescription(),new Mod202Key[]{Mod202Key.C19,null,null},COMPUTE_KEY)
		,B2C20_C21_C22	(Mod202Key.C20.getDescription() + " (Base/Porcentaje/Importe)",new Mod202Key[]{Mod202Key.C20,Mod202Key.C21,Mod202Key.C22},COMPUTE_KEY)
		,B2C23_C24_C25	(Mod202Key.C23.getDescription() + " (Base/Porcentaje/Importe)",new Mod202Key[]{Mod202Key.C23,Mod202Key.C24,Mod202Key.C25},COMPUTE_KEY)
		,B2C61_C62_C63	(Mod202Key.C61.getDescription() + " (Base/Porcentaje/Importe)",new Mod202Key[]{Mod202Key.C61,Mod202Key.C62,Mod202Key.C63},COMPUTE_KEY)
		,B2C64_C65_C66	(Mod202Key.C64.getDescription() + " (Base/Porcentaje/Importe)",new Mod202Key[]{Mod202Key.C64,Mod202Key.C65,Mod202Key.C66},COMPUTE_KEY)
		,B2C50			(Mod202Key.C50.getDescription(),new Mod202Key[]{Mod202Key.C50},NONE)
		,B2C42			(Mod202Key.C42.getDescription(),new Mod202Key[]{Mod202Key.C42},NONE)
		,B2C51_B2C52	("Reserva de nivelaci\u00F3n (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicaci\u00F3n de los incentivos de empresa de reducida dimensi\u00F3n (art. 101 LIS) y apliquen el tipo de gravamen espec\u00EDfico previsto para estas entidades) (Aumentos/Disminuciones)"
						,new Mod202Key[]{Mod202Key.C51,Mod202Key.C52},NONE)
		,B2C26			("Resultado previo(claves [22]+[25]+[63]+[66]+[50]-[42]+[51]-[52])",new Mod202Key[]{Mod202Key.C26},COMPUTE_KEY)
		,B2C27			(Mod202Key.C27.getDescription(),new Mod202Key[]{Mod202Key.C27},NONE)
		,B2C28			(Mod202Key.C28.getDescription(),new Mod202Key[]{Mod202Key.C28},NONE)
		,B2C29			(Mod202Key.C29.getDescription(),new Mod202Key[]{Mod202Key.C29},NONE)
		,B2C30			(Mod202Key.C30.getDescription(),new Mod202Key[]{Mod202Key.C30},NONE)
		,B2C31			(Mod202Key.C31.getDescription(),new Mod202Key[]{Mod202Key.C31},COMPUTE_KEY)
		,B2C32			(Mod202Key.C32.getDescription(),new Mod202Key[]{Mod202Key.C32},COMPUTE_KEY)
		,B2C33			(Mod202Key.C33.getDescription(),new Mod202Key[]{Mod202Key.C33},NONE)
		,B2C34			(Mod202Key.C34.getDescription(),new Mod202Key[]{Mod202Key.C34},COMPUTE_KEY)
	;
	
	private String label;
	private Mod202Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model2022025LiquidationAEATScript(String label, Mod202Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod202Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != TITLE
			&& this != AC01
			&& this != AC03
			&& this != BCC38_C39 			
			&& this != BC13	
			&& this != B1C16
			&& this != B1C17
			&& this != B1C18
			&& (this == B2C20_C21_C22 && AonCollectionUtils.isNotEmpty(getKeys()) && getKeys()[0] != Mod202Key.C20) 
			&& (this == B2C23_C24_C25 && AonCollectionUtils.isNotEmpty(getKeys()) && getKeys()[0] != Mod202Key.C23)
			&& (this == B2C61_C62_C63 && AonCollectionUtils.isNotEmpty(getKeys()) && getKeys()[0] != Mod202Key.C61)
			&& (this == B2C64_C65_C66 && AonCollectionUtils.isNotEmpty(getKeys()) && getKeys()[0] != Mod202Key.C64)
			&& this != B2C26
			&& this != B2C32 
			&& this != B2C34
		;
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
	public boolean hasGraphicParticularity() {
		return this == X00;
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	}
}
