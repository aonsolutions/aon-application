package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod421Key implements IFiscalModelKey  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 X00("421-X00",false,null,"Confecci\u00F3n manual")
	,X01("421-X01",false,null,"C\u00E1lculo por diferencia deshabilitado")
	,X02("421-X02",false,null,"C\u00F3digo de municipio")     
	,X03("421-X03",false,null,"Concurso de acreedores")
	
	// ACTIVIDAD 1
	
	,A1EP1("421-A1EP1",false,null,"Epigrafe IAE")
	,A1EP2("421-A1EP2",false,null,"Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722")
	,A1EPD("421-A1EPD",false,null,"Epigrafe IAE - Descripci\u00F3n")                                               
	,A1TEM("421-A1TEM",false,null,"Actividad de Temporada: N\u00BA de d\u00EDas de ejercicio de la actividad")	// Año anterior (1T/2T/3T) y año actual (4T)
	,A1DIA("421-A1DIA",false,null,"N\u00FAmero de d\u00EDas de ejercicio de la actividad en el trimestre")
	
	,A1M1D("421-A1M1D",false,null,"Descripci\u00F3n")					// Descripción
	,A1M1I("421-A1M1I",false,null,"Unidades") 							// Unidades 
	,A1M1U("421-A1M1U",false,null,"Unidad") 							// Unidad (texto)
	,A1M1F("421-A1M1F",false,null,"Cuota devengada anual por unidad")	// Cuota devengada anual por unidad
	,A1M1R("421-A1M1R",false,null,"Resultado")		 					// Resultado (Unidades * Cuota devengada anual por unidad)
	
	,A1M2D("421-A1M2D",false,null, A1M1D.getDescription())	
	,A1M2I("421-A1M2I",false,null, A1M1I.getDescription()) 	
	,A1M2U("421-A1M2U",false,null, A1M1U.getDescription()) 	
	,A1M2F("421-A1M2F",false,null, A1M1F.getDescription())	
	,A1M2R("421-A1M2R",false,null, A1M1R.getDescription())
	
	,A1M3D("421-A1M3D",false,null, A1M1D.getDescription())
	,A1M3I("421-A1M3I",false,null, A1M1I.getDescription())
	,A1M3U("421-A1M3U",false,null, A1M1U.getDescription())
	,A1M3F("421-A1M3F",false,null, A1M1F.getDescription())
	,A1M3R("421-A1M3R",false,null, A1M1R.getDescription())

	,A1M4D("421-A1M4D",false,null, A1M1D.getDescription())
	,A1M4I("421-A1M4I",false,null, A1M1I.getDescription())
	,A1M4U("421-A1M4U",false,null, A1M1U.getDescription())
	,A1M4F("421-A1M4F",false,null, A1M1F.getDescription())
	,A1M4R("421-A1M4R",false,null, A1M1R.getDescription())
	
	,A1M5D("421-A1M5D",false,null, A1M1D.getDescription())
	,A1M5I("421-A1M5I",false,null, A1M1I.getDescription())
	,A1M5U("421-A1M5U",false,null, A1M1U.getDescription())
	,A1M5F("421-A1M5F",false,null, A1M1F.getDescription())
	,A1M5R("421-A1M5R",false,null, A1M1R.getDescription())
	
	,A1M6D("421-A1M6D",false,null, A1M1D.getDescription())
	,A1M6I("421-A1M6I",false,null, A1M1I.getDescription())
	,A1M6U("421-A1M6U",false,null, A1M1U.getDescription())
	,A1M6F("421-A1M6F",false,null, A1M1F.getDescription())
	,A1M6R("421-A1M6R",false,null, A1M1R.getDescription())
	
	,A1M7D("421-A1M7D",false,null, A1M1D.getDescription())
	,A1M7I("421-A1M7I",false,null, A1M1I.getDescription())
	,A1M7U("421-A1M7U",false,null, A1M1U.getDescription())
	,A1M7F("421-A1M7F",false,null, A1M1F.getDescription())
	,A1M7R("421-A1M7R",false,null, A1M1R.getDescription())
	 
	,A1DEV("421-A1DEV",false,"DEV1","Cuota devengada operaciones corrientes")
	,A1ICT("421-A1ICT",false,"","\u00CDndice corrector actividades de temporada")
	,A1POR("421-A1POR",false,"","Porcentaje de ingreso a cuenta")
	,A1ING("421-A1ING",false,"ING1","Ingreso a cuenta")
	,A1SO1("421-A1SO1",false,"","1% de la cuota devengada por operaciones corrientes")
	,A1SOR("421-A1SOR",false,"","Resto de cuotas soportadas")
	,A1SOP("421-A1SOP",false,"SOP1","Total cuotas soportadas operaciones corrientes")
	,A1RES("421-A1RES",false,"","RESULTADO")
	,A1PCM("421-A1PCM",false,"","Porcentaje cuota m\u00EDnima")
	,A1CMN("421-A1CMN",false,"","Cuota m\u00EDnima")
	,A1CAD("421-A1CAD",false,"CAD1","Cuota anual derivada r\u00E9gimen simplificado")
	
	// ACTIVIDAD 2
	
	,A2EP1("421-A2EP1",false,null, A1EP1.getDescription())
	,A2EP2("421-A2EP2",false,null, A1EP2.getDescription())
	,A2EPD("421-A2EPD",false,null, A1EPD.getDescription())
	,A2TEM("421-A2TEM",false,null, A1TEM.getDescription())
	,A2DIA("421-A2DIA",false,null, A1DIA.getDescription())
	
	,A2M1D("421-A2M1D",false,null, A1M1D.getDescription())	
	,A2M1I("421-A2M1I",false,null, A1M1I.getDescription()) 	
	,A2M1U("421-A2M1U",false,null, A1M1U.getDescription()) 	
	,A2M1F("421-A2M1F",false,null, A1M1F.getDescription())	
	,A2M1R("421-A2M1R",false,null, A1M1R.getDescription())	
	,A2M2D("421-A2M2D",false,null, A1M1D.getDescription())
	,A2M2I("421-A2M2I",false,null, A1M1I.getDescription())
	,A2M2U("421-A2M2U",false,null, A1M1U.getDescription())
	,A2M2F("421-A2M2F",false,null, A1M1F.getDescription())
	,A2M2R("421-A2M2R",false,null, A1M1R.getDescription())
	,A2M3D("421-A2M3D",false,null, A1M1D.getDescription())
	,A2M3I("421-A2M3I",false,null, A1M1I.getDescription())
	,A2M3U("421-A2M3U",false,null, A1M1U.getDescription())
	,A2M3F("421-A2M3F",false,null, A1M1F.getDescription())
	,A2M3R("421-A2M3R",false,null, A1M1R.getDescription())
	,A2M4D("421-A2M4D",false,null, A1M1D.getDescription())
	,A2M4I("421-A2M4I",false,null, A1M1I.getDescription())
	,A2M4U("421-A2M4U",false,null, A1M1U.getDescription())
	,A2M4F("421-A2M4F",false,null, A1M1F.getDescription())
	,A2M4R("421-A2M4R",false,null, A1M1R.getDescription())
	,A2M5D("421-A2M5D",false,null, A1M1D.getDescription())
	,A2M5I("421-A2M5I",false,null, A1M1I.getDescription())
	,A2M5U("421-A2M5U",false,null, A1M1U.getDescription())
	,A2M5F("421-A2M5F",false,null, A1M1F.getDescription())
	,A2M5R("421-A2M5R",false,null, A1M1R.getDescription())
	,A2M6D("421-A2M6D",false,null, A1M1D.getDescription())
	,A2M6I("421-A2M6I",false,null, A1M1I.getDescription())
	,A2M6U("421-A2M6U",false,null, A1M1U.getDescription())
	,A2M6F("421-A2M6F",false,null, A1M1F.getDescription())
	,A2M6R("421-A2M6R",false,null, A1M1R.getDescription())
	,A2M7D("421-A2M7D",false,null, A1M1D.getDescription())
	,A2M7I("421-A2M7I",false,null, A1M1I.getDescription())
	,A2M7U("421-A2M7U",false,null, A1M1U.getDescription())
	,A2M7F("421-A2M7F",false,null, A1M1F.getDescription())
	,A2M7R("421-A2M7R",false,null, A1M1R.getDescription())
	
	,A2DEV("421-A2DEV",false,"DEV2",A1DEV.getDescription())
	,A2ICT("421-A2ICT",false,"",A1ICT.getDescription())
	,A2POR("421-A2POR",false,"",A1POR.getDescription())
	,A2ING("421-A2ING",false,"ING2",A1ING.getDescription())
	,A2SO1("421-A2SO1",false,"",A1SO1.getDescription())
	,A2SOR("421-A2SOR",false,"",A1SOR.getDescription())
	,A2SOP("421-A2SOP",false,"SOP2",A1SOP.getDescription())
	,A2RES("421-A2RES",false,"",A1RES.getDescription())
	,A2PCM("421-A2PCM",false,"",A1PCM.getDescription())
	,A2CMN("421-A2CMN",false,"",A1CMN.getDescription())
	,A2CAD("421-A2CAD",false,"CAD2",A1CAD.getDescription())

	// ACTIVIDAD 3 
	
	,A3EP1("421-A3EP1",false,null, A1EP1.getDescription())
	,A3EP2("421-A3EP2",false,null, A1EP2.getDescription())
	,A3EPD("421-A3EPD",false,null, A1EPD.getDescription())
	,A3TEM("421-A3TEM",false,null, A1TEM.getDescription())
	,A3DIA("421-A3DIA",false,null, A1DIA.getDescription())
	
	,A3M1D("421-A3M1D",false,null, A1M1D.getDescription())	
	,A3M1I("421-A3M1I",false,null, A1M1I.getDescription()) 	
	,A3M1U("421-A3M1U",false,null, A1M1U.getDescription()) 	
	,A3M1F("421-A3M1F",false,null, A1M1F.getDescription())	
	,A3M1R("421-A3M1R",false,null, A1M1R.getDescription())	
	,A3M2D("421-A3M2D",false,null, A1M1D.getDescription())
	,A3M2I("421-A3M2I",false,null, A1M1I.getDescription())
	,A3M2U("421-A3M2U",false,null, A1M1U.getDescription())
	,A3M2F("421-A3M2F",false,null, A1M1F.getDescription())
	,A3M2R("421-A3M2R",false,null, A1M1R.getDescription())
	,A3M3D("421-A3M3D",false,null, A1M1D.getDescription())
	,A3M3I("421-A3M3I",false,null, A1M1I.getDescription())
	,A3M3U("421-A3M3U",false,null, A1M1U.getDescription())
	,A3M3F("421-A3M3F",false,null, A1M1F.getDescription())
	,A3M3R("421-A3M3R",false,null, A1M1R.getDescription())
	,A3M4D("421-A3M4D",false,null, A1M1D.getDescription())
	,A3M4I("421-A3M4I",false,null, A1M1I.getDescription())
	,A3M4U("421-A3M4U",false,null, A1M1U.getDescription())
	,A3M4F("421-A3M4F",false,null, A1M1F.getDescription())
	,A3M4R("421-A3M4R",false,null, A1M1R.getDescription())
	,A3M5D("421-A3M5D",false,null, A1M1D.getDescription())
	,A3M5I("421-A3M5I",false,null, A1M1I.getDescription())
	,A3M5U("421-A3M5U",false,null, A1M1U.getDescription())
	,A3M5F("421-A3M5F",false,null, A1M1F.getDescription())
	,A3M5R("421-A3M5R",false,null, A1M1R.getDescription())
	,A3M6D("421-A3M6D",false,null, A1M1D.getDescription())
	,A3M6I("421-A3M6I",false,null, A1M1I.getDescription())
	,A3M6U("421-A3M6U",false,null, A1M1U.getDescription())
	,A3M6F("421-A3M6F",false,null, A1M1F.getDescription())
	,A3M6R("421-A3M6R",false,null, A1M1R.getDescription())
	,A3M7D("421-A3M7D",false,null, A1M1D.getDescription())
	,A3M7I("421-A3M7I",false,null, A1M1I.getDescription())
	,A3M7U("421-A3M7U",false,null, A1M1U.getDescription())
	,A3M7F("421-A3M7F",false,null, A1M1F.getDescription())
	,A3M7R("421-A3M7R",false,null, A1M1R.getDescription())
	
	,A3DEV("421-A3DEV",false,"DEV3",A1DEV.getDescription())
	,A3ICT("421-A3ICT",false,"",A1ICT.getDescription())
	,A3POR("421-A3POR",false,"",A1POR.getDescription())
	,A3ING("421-A3ING",false,"ING3",A1ING.getDescription())
	,A3SO1("421-A3SO1",false,"",A1SO1.getDescription())
	,A3SOR("421-A3SOR",false,"",A1SOR.getDescription())
	,A3SOP("421-A3SOP",false,"SOP3",A1SOP.getDescription())
	,A3RES("421-A3RES",false,"",A1RES.getDescription())
	,A3PCM("421-A3PCM",false,"",A1PCM.getDescription())
	,A3CMN("421-A3CMN",false,"",A1CMN.getDescription())
	,A3CAD("421-A3CAD",false,"CAD3",A1CAD.getDescription())
	
	// ACTIVIDAD 4
	
	,A4EP1("421-A4EP1",false,null, A1EP1.getDescription())
	,A4EP2("421-A4EP2",false,null, A1EP2.getDescription())
	,A4EPD("421-A4EPD",false,null, A1EPD.getDescription())
	,A4TEM("421-A4TEM",false,null, A1TEM.getDescription())
	,A4DIA("421-A4DIA",false,null, A1DIA.getDescription())
	
	,A4M1D("421-A4M1D",false,null, A1M1D.getDescription())	
	,A4M1I("421-A4M1I",false,null, A1M1I.getDescription()) 	
	,A4M1U("421-A4M1U",false,null, A1M1U.getDescription()) 	
	,A4M1F("421-A4M1F",false,null, A1M1F.getDescription())	
	,A4M1R("421-A4M1R",false,null, A1M1R.getDescription())	
	,A4M2D("421-A4M2D",false,null, A1M1D.getDescription())
	,A4M2I("421-A4M2I",false,null, A1M1I.getDescription())
	,A4M2U("421-A4M2U",false,null, A1M1U.getDescription())
	,A4M2F("421-A4M2F",false,null, A1M1F.getDescription())
	,A4M2R("421-A4M2R",false,null, A1M1R.getDescription())
	,A4M3D("421-A4M3D",false,null, A1M1D.getDescription())
	,A4M3I("421-A4M3I",false,null, A1M1I.getDescription())
	,A4M3U("421-A4M3U",false,null, A1M1U.getDescription())
	,A4M3F("421-A4M3F",false,null, A1M1F.getDescription())
	,A4M3R("421-A4M3R",false,null, A1M1R.getDescription())
	,A4M4D("421-A4M4D",false,null, A1M1D.getDescription())
	,A4M4I("421-A4M4I",false,null, A1M1I.getDescription())
	,A4M4U("421-A4M4U",false,null, A1M1U.getDescription())
	,A4M4F("421-A4M4F",false,null, A1M1F.getDescription())
	,A4M4R("421-A4M4R",false,null, A1M1R.getDescription())
	,A4M5D("421-A4M5D",false,null, A1M1D.getDescription())
	,A4M5I("421-A4M5I",false,null, A1M1I.getDescription())
	,A4M5U("421-A4M5U",false,null, A1M1U.getDescription())
	,A4M5F("421-A4M5F",false,null, A1M1F.getDescription())
	,A4M5R("421-A4M5R",false,null, A1M1R.getDescription())
	,A4M6D("421-A4M6D",false,null, A1M1D.getDescription())
	,A4M6I("421-A4M6I",false,null, A1M1I.getDescription())
	,A4M6U("421-A4M6U",false,null, A1M1U.getDescription())
	,A4M6F("421-A4M6F",false,null, A1M1F.getDescription())
	,A4M6R("421-A4M6R",false,null, A1M1R.getDescription())
	,A4M7D("421-A4M7D",false,null, A1M1D.getDescription())
	,A4M7I("421-A4M7I",false,null, A1M1I.getDescription())
	,A4M7U("421-A4M7U",false,null, A1M1U.getDescription())
	,A4M7F("421-A4M7F",false,null, A1M1F.getDescription())
	,A4M7R("421-A4M7R",false,null, A1M1R.getDescription())
	
	,A4DEV("421-A4DEV",false,"DEV4",A1DEV.getDescription())
	,A4ICT("421-A4ICT",false,"",A1ICT.getDescription())
	,A4POR("421-A4POR",false,"",A1POR.getDescription())
	,A4ING("421-A4ING",false,"ING4",A1ING.getDescription())
	,A4SO1("421-A4SO1",false,"",A1SO1.getDescription())
	,A4SOR("421-A4SOR",false,"",A1SOR.getDescription())
	,A4SOP("421-A4SOP",false,"SOP4",A1SOP.getDescription())
	,A4RES("421-A4RES",false,"",A1RES.getDescription())
	,A4PCM("421-A4PCM",false,"",A1PCM.getDescription())
	,A4CMN("421-A4CMN",false,"",A1CMN.getDescription())
	,A4CAD("421-A4CAD",false,"CAD4",A1CAD.getDescription())
	
	// RESULTADO

	,C06("421-C06",false,"06","Cantidad a cuenta de acuerdo con los datos base provisionales")
	,C07("421-C07",false,"07","Cuota anual devengada por operaciones corrientes")
	,C08("421-C08",false,"08","Cuotas soportadas o satisfechas en el ejercicio por operaciones corrientes")
	,C09("421-C09",false,"09","Cuota anual derivada del r\u00E9gimen simplificado")
	,C10T1("421-C10T1",false,"T1","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T1)")
	,C10T2("421-C10T2",false,"T2","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T2)")
	,C10T3("421-C10T3",false,"T3","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T3)")
	,C10("421-C10",false,"10","Cantidad a cuenta autoliquidaciones trimestrales anteriores")
	,C11("421-C11",false,"11","Diferencia")
	
	,C12("421-C12",true,"12","Cuotas devengadas por entregas o transmisiones de activos fijos y por inversi\u00F3n del sujeto pasivo")
	,C13("421-C13",true,"13","Cuotas devengadas por arrendamiento de bienes inmuebles")
	,C14("421-C14",true,"14","Rectificaci\u00F3n de cuotas impositivas repercutidas")
	,C15("421-C15",true,"15","Cuotas deducibles por adquisiciones o importaciones de activos fijos")
	,C16("421-C16",true,"16","Cuotas deducibles correspondientes a la actividad de arrendamiento de bienes inmuebles")
	,C17("421-C17",true,"17","Cuotas del I.G.I.C. a compensar de per\u00EDodos anteriores")
	,C18("421-C18",true,"18","A deducir (exclusivamente en caso de autoliquidaci\u00F3n complementaria)")
	,C19("421-C19",true,"19","Resultado de la autoliquidaci\u00F3n")
	;
	
	private String value;
	private boolean diffEnabled;
	private String box;
	private String description;
	
	private Mod421Key(String value,boolean diffEnabled,String box,String description) {
		this.value = value;
		this.diffEnabled = diffEnabled;
		this.box = box;
		this.description = description;
	}
	
	public boolean isDiffEnabled() {
		return this.diffEnabled;
	}

    @Override
	public String getValue() {
		return value;
	}
    @Override
	public int getBox() {
		if (AonStringUtils.isNumeric(box)) {
			return AonNumberUtils.toint(box);
		}
		return 0;
	}
	public String getBoxCode() {
		if (AonStringUtils.isNumeric(box)) {
			return AonStringUtils.leftPad(box, 3, AonStringUtils.ZERO); 
		}
		return box;
	}
	public String getDescription() {
		return description;
	}
    @Override
	public String getBoxFormatted() {
		return " [" + getBoxAsString() +"] ";
	}
	public String getBoxAsString() {
		if (AonStringUtils.isNumeric(box)) {
			return AonStringUtils.leftPad(Integer.toString(getBox()), 3, '0');
		} else {
			return box;
		}
	}
	
	public static Mod421Key getKey(String value) {
		for (Mod421Key key : Mod421Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}

}
