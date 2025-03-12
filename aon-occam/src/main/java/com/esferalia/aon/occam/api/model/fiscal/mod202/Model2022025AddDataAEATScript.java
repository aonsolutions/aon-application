package com.esferalia.aon.occam.api.model.fiscal.mod202;


import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public enum Model2022025AddDataAEATScript implements IModelScript<Mod202Key> {
	
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	
	 R00 ("Territorio Foral",null,TITLE)
	 	,R00_X15 ( Mod202Key.X15.getDescription(),new Mod202Key[]{Mod202Key.X15},NONE)
	 	,R00_X16 ( Mod202Key.X16.getDescription(),new Mod202Key[]{Mod202Key.X16},NONE)
	 	,R00_X17 ( Mod202Key.X17.getDescription(),new Mod202Key[]{Mod202Key.X17},NONE)
	 	,R00_X18 ( Mod202Key.X18.getDescription(),new Mod202Key[]{Mod202Key.X18},NONE)

	,R01 ("Devengo",null,TITLE)
		,R01_P02 (Mod202Key.P02.getDescription(),new Mod202Key[]{Mod202Key.P02},NONE)
		,R01_P03 (Mod202Key.P03.getDescription(),new Mod202Key[]{Mod202Key.P03},NONE)
	,R02 ("Datos adicionales",null,TITLE)
		,R02_X01 ( Mod202Key.X01.getDescription(),new Mod202Key[]{Mod202Key.X01},NONE)
		,R02_X02 ( Mod202Key.X02.getDescription(),new Mod202Key[]{Mod202Key.X02},NONE)
		,R02_X19 ( Mod202Key.X19.getDescription(),new Mod202Key[]{Mod202Key.X19},NONE)
		,R02_X04 ( Mod202Key.X04.getDescription(),new Mod202Key[]{Mod202Key.X04},NONE)
		,R02_X12 ( "Entidad que cumpla los requisitos para la aplicaci\u00F3n de los incentivos de empresa de reducida dimensi\u00F3n (art. 101 LIS) y apliquen tipo gravamen espec\u00EDfico previsto para estas entidades",new Mod202Key[]{Mod202Key.X12},NONE)
		,R02_X06 ( "Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del per\u00EDodo impositivo es superior a 6.000.000 euros",new Mod202Key[]{Mod202Key.X06},NONE)
		,R02_X13 ( Mod202Key.X13.getDescription(),new Mod202Key[]{Mod202Key.X13},NONE)
		,R02_X11 ( Mod202Key.X11.getDescription(),new Mod202Key[]{Mod202Key.X11},NONE)
			,R02_X11_1 ("- Entidad que aplica la Reserva para inversiones en Canarias o tenga derecho a la bonificaci\u00F3n del art. 26 Ley 19/1994",null,NONE)
			,R02_X11_2 ("- Entidad que aplica el r\u00E9gimen ZEC",null,NONE)
			,R02_X11_3 ("- Entidad que aplica la bonificaci\u00F3n de Ceuta y Melilla art. 33 LIS",null,NONE)
			,R02_X11_4 ("- Entidad con resultados positivos por operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos que no se integran en la base imponible por aplicaci\u00F3n del art. 17.2 LIS",null,NONE)
			,R02_X11_5 ("- Entidad parcialmente exenta que aplica el r\u00E9gimen fiscal especial Cap. XIV T\u00EDt. VII LIS",null,NONE)
			,R02_X11_6 ("- Entidad que aplica la bonificaci\u00F3n del art. 34 LIS",null,NONE)
		,R02_X20 (Mod202Key.X20.getDescription(),new Mod202Key[]{Mod202Key.X20},NONE)
		,R02_X14 ("Otras entidades con posibilidad de aplicar m\u00E1s de un tipo impositivo",new Mod202Key[]{Mod202Key.X14},NONE)
		,R02_X08 (Mod202Key.X08.getDescription(),new Mod202Key[]{Mod202Key.X08},NONE)
		,R02_X09 ("Importe neto de la cifra de negocios en los doce meses anteriores a la fecha de inicio del per\u00EDodo impositivo",new Mod202Key[]{Mod202Key.X09},NONE)
	;
	
	private String label;
	private Mod202Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model2022025AddDataAEATScript(String label, Mod202Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return getInfoKeys()[0] != TITLE;
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
		return (this == R00_X15
			|| this == R00_X16
			|| this == R00_X17
			|| this == R00_X18
			|| this == R01_P02
			|| this == R01_P03
			|| this == R02_X01
			|| this == R02_X02
			|| this == R02_X19
			|| this == R02_X04
			|| this == R02_X12
			|| this == R02_X06
			|| this == R02_X13
			|| this == R02_X11
			|| this == R02_X14
			|| this == R02_X20
			|| this == R02_X08
			|| this == R02_X09
		);
		
		
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	}
}
