package com.esferalia.aon.occam.api.model.fiscal.mod202;


import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.ACT_ACCOUNT;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.CORPORATE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public enum Model202AEATScript implements IModelScript<Mod202Key> {
	
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF

	
	 R00 ("Devengo",null,TITLE)
	,R01 (Mod202Key.P02.getDescription(),new Mod202Key[]{Mod202Key.P02},NONE)
	,R02 (Mod202Key.P03.getDescription(),new Mod202Key[]{Mod202Key.P03},NONE)
	,R03 ("Datos adicionales",null,TITLE)
	,R04 ( Mod202Key.X01.getDescription(),new Mod202Key[]{Mod202Key.X01},NONE)
	,R05 ( Mod202Key.X02.getDescription(),new Mod202Key[]{Mod202Key.X02},NONE)
	,R06 ( Mod202Key.X04.getDescription(),new Mod202Key[]{Mod202Key.X04},NONE)
	,R07 ( Mod202Key.X12.getDescription(),new Mod202Key[]{Mod202Key.X12},NONE)
	,R08 ( Mod202Key.X06.getDescription(),new Mod202Key[]{Mod202Key.X06},NONE)
	,R09 ( Mod202Key.X13.getDescription(),new Mod202Key[]{Mod202Key.X13},NONE)
	,R10 ( Mod202Key.X11.getDescription(),new Mod202Key[]{Mod202Key.X11},NONE)
	,R11 ("- Entidad que aplica la Reserva para inversiones en Canarias o tenga derecho a la bonificaci\u00F3n del art. 26 Ley 19/1994",null,NONE)
	,R12 ("- Entidad que aplica el r\u00E9gimen ZEC",null,NONE)
	,R13 ("- Entidad que aplica la bonificaci\u00F3n de Ceuta y Melilla art. 33 LIS",null,NONE)
	,R14 ("- Entidad con resultados positivos por operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos que no se integran en la base imponible por aplicaci\u00F3n del art. 17.2 LIS",null,NONE)
	,R15 ("- Entidad parcialmente exenta que aplica el r\u00E9gimen fiscal especial Cap. XIV T\u00EDt. VII LIS",null,NONE)
	,R16 ("- Entidad que aplica la bonificaci\u00F3n del art. 34 LIS",null,NONE)
	,R17 (Mod202Key.X14.getDescription(),new Mod202Key[]{Mod202Key.X14},NONE)
	,R18 (Mod202Key.X08.getDescription(),new Mod202Key[]{Mod202Key.X08},NONE)
	,R19 (Mod202Key.X09.getDescription(),new Mod202Key[]{Mod202Key.X09},NONE)
	,R20 ("Liquidaci\u00F3n",null,TITLE)
	,R21 (Mod202Key.X00.getDescription(),new Mod202Key[]{Mod202Key.X00},NONE)
	
	,R22 ("A) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.2 LIS",null,TITLE)
	,R23 (Mod202Key.C01.getDescription(),new Mod202Key[]{Mod202Key.C01},CORPORATE)
	,R24 (Mod202Key.C02.getDescription(),new Mod202Key[]{Mod202Key.C02},NONE)
	,R25 (Mod202Key.C03.getDescription(),new Mod202Key[]{Mod202Key.C03},COMPUTE)
	
	,R26 ("B) C\u00E1lculo del pago fraccionado: modalidad art\u00EDculo 40.3 LIS",null,TITLE)
	,R27 (Mod202Key.C04.getDescription(),new Mod202Key[]{Mod202Key.C04},ACT_ACCOUNT)
	,R28 ("Correcciones al resultado contable",null,NONE)
	,R29 ("Correcci\u00F3n por Impuesto sobre Sociedades",new Mod202Key[]{Mod202Key.C05,Mod202Key.C06},NONE)
	,R30 (Mod202Key.C37.getDescription(),new Mod202Key[]{Mod202Key.C37},NONE)
	,R31 ("Resto correcciones al resultado contable, excepto comp. BI negativa ej. ant."
			,new Mod202Key[]{Mod202Key.C07,Mod202Key.C08},NONE)
	,R32 ("TOTAL",new Mod202Key[]{Mod202Key.C38,Mod202Key.C39},COMPUTE)
	,R33 (Mod202Key.C13.getDescription(),new Mod202Key[]{Mod202Key.C13},COMPUTE)
	,R34 (Mod202Key.C44.getDescription(),new Mod202Key[]{Mod202Key.C44},NONE)
	,R35 (Mod202Key.C14.getDescription(),new Mod202Key[]{Mod202Key.C14},NONE)
	,R36 ("Reserva de nivelaci\u00F3n (art. 105 LIS) (s\u00F3lo entidades que cumplan los requisitos"
		+ "del art. 101 LIS y apliquen tipo gravamen art. 29.1, 1 er p\u00E1rrafo LIS)"
			,new Mod202Key[]{Mod202Key.C45,Mod202Key.C46},NONE)
	,R37 ("B.1) Caso general (entidades con porcentaje \u00FAnico)",null,TITLE)
	,R38 (Mod202Key.C16.getDescription(),new Mod202Key[]{Mod202Key.C16},COMPUTE)
	,R39 (Mod202Key.C17.getDescription(),new Mod202Key[]{Mod202Key.C17},COMPUTE)
	
	,R40 (Mod202Key.C47.getDescription(),new Mod202Key[]{Mod202Key.C47},NONE)
	,R41 (Mod202Key.C40.getDescription(),new Mod202Key[]{Mod202Key.C40},NONE)
	,R42 ("Reserva de nivelaci\u00F3n (art. 105 LIS) convertido en cuotas (s\u00F3lo entidades que cumplan"
		+ "los requisitos del art. 101 LIS y apliquen tipo gravamen art. 29.1, 1 er p\u00E1rrafo LIS)"
			,new Mod202Key[]{Mod202Key.C48,Mod202Key.C49},NONE)
	,R43 (Mod202Key.C18.getDescription(),new Mod202Key[]{Mod202Key.C18},COMPUTE)
	
	,R44 ("B.2) Casos espec\u00EDficos (entidades con m\u00E1s de un porcentaje)",null,TITLE)
	,R45 (Mod202Key.C19.getDescription(),new Mod202Key[]{Mod202Key.C19,null,null},COMPUTE)
	,R46 (Mod202Key.C20.getDescription(),new Mod202Key[]{Mod202Key.C20,Mod202Key.C21,Mod202Key.C22},COMPUTE)
	,R47 (Mod202Key.C23.getDescription(),new Mod202Key[]{Mod202Key.C23,Mod202Key.C24,Mod202Key.C25},COMPUTE)
	,R48 (Mod202Key.C50.getDescription(),new Mod202Key[]{Mod202Key.C50},NONE)
	,R49 (Mod202Key.C42.getDescription(),new Mod202Key[]{Mod202Key.C42},NONE)
	,R50 ("Reserva de nivelaci\u00F3n (art. 105 LIS) (s\u00F3lo entidades que cumplan los "
		+ "requisitos del art. 101 LIS y apliquen tipo gravamen art. 29.1, 1 er p\u00E1rrafo LIS)"
				,new Mod202Key[]{Mod202Key.C51,Mod202Key.C52},NONE)
	,R51 (Mod202Key.C26.getDescription(),new Mod202Key[]{Mod202Key.C26},COMPUTE)
	,R52 (Mod202Key.C27.getDescription(),new Mod202Key[]{Mod202Key.C27},NONE)
	,R53 (Mod202Key.C28.getDescription(),new Mod202Key[]{Mod202Key.C28},NONE)
	,R54 (Mod202Key.C29.getDescription(),new Mod202Key[]{Mod202Key.C29},NONE)
	,R55 (Mod202Key.C30.getDescription(),new Mod202Key[]{Mod202Key.C30},NONE)
	,R56 (Mod202Key.C31.getDescription(),new Mod202Key[]{Mod202Key.C31},NONE)
	,R57 (Mod202Key.C32.getDescription(),new Mod202Key[]{Mod202Key.C32},COMPUTE)
	,R58 (Mod202Key.C33.getDescription(),new Mod202Key[]{Mod202Key.C33},NONE)
	,R59 (Mod202Key.C34.getDescription(),new Mod202Key[]{Mod202Key.C34},COMPUTE)
	
	,R60 ("Informaci\u00F3n adicional",null,TITLE)
	,R61 (Mod202Key.A01.getDescription(),new Mod202Key[]{Mod202Key.A01},NONE)
	,R62 (Mod202Key.A02.getDescription(),new Mod202Key[]{Mod202Key.A02},NONE)
	,R63 (Mod202Key.A03.getDescription(),new Mod202Key[]{Mod202Key.A03},NONE)
	,R64 (Mod202Key.A04.getDescription(),new Mod202Key[]{Mod202Key.A04},NONE)
	,R65 (Mod202Key.A05.getDescription(),new Mod202Key[]{Mod202Key.A05},NONE)
	,R66 (Mod202Key.A06.getDescription(),new Mod202Key[]{Mod202Key.A06},NONE)
	,R67 (Mod202Key.A07.getDescription(),new Mod202Key[]{Mod202Key.A07},NONE)
	,R68 (Mod202Key.A08.getDescription(),new Mod202Key[]{Mod202Key.A08},NONE)
	,R69 (Mod202Key.A09.getDescription(),new Mod202Key[]{Mod202Key.A09},NONE)
	,R70 (Mod202Key.A10.getDescription(),new Mod202Key[]{Mod202Key.A10},NONE)
	,R71 (Mod202Key.A11.getDescription(),new Mod202Key[]{Mod202Key.A11},NONE)
	,R72 (Mod202Key.A12.getDescription(),new Mod202Key[]{Mod202Key.A12},NONE)
	,R73 (Mod202Key.A13.getDescription(),new Mod202Key[]{Mod202Key.A13},NONE)
	;
	
	private String label;
	private Mod202Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model202AEATScript(String label, Mod202Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == R01
			|| this == R02
			|| this == R04
			|| this == R05
			|| this == R06
			|| this == R07
			|| this == R08
			|| this == R09
			|| this == R10
			|| this == R17
			|| this == R18
			|| this == R19
			|| this == R21
			|| this == R61
			|| this == R62
		);
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == R37);
	};
}
