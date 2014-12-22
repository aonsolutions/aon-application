package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKey;
import com.esferalia.aon.occam.api.model.type.ActivityGroup;
import com.google.gwt.i18n.client.Messages;

public interface FiscalMessages extends Messages {
	// ¡ --> \u00C1 · --> \u00E1
	// … --> \u00C9 È --> \u00E9
	// Õ --> \u00CD Ì --> \u00ED
	// ” --> \u00D3 Û --> \u00F3
	// ⁄ --> \u00DA ˙ --> \u00FA
	// — --> \u00D1 Ò --> \u00F1
	// ™ --> \u00AA ∫ --> \u00BA
	// ø --> \u00BF

	@DefaultMessage("----------")
	@AlternateMessage({
			"GROUP1",
			"Actividades empresariales sujetas al I.A.E.",
			"GROUP2",
			"Actividades profesionales sujetas al I.A.E.",
			"GROUP3",
			"Actividades art\u00EDsticas sujetas al I.A.E.",
			"GROUP4",
			"Arrendadores de locales de negocios",
			"GROUP5",
			"Actividades agr\u00EDcolas, ganaderas o pesqueras, no sujetas al I.A.E.",
			"GROUP6", "Otras actividades no sujetas al I.A.E.", "GROUP7",
			"Sujetos pasivos sin actividad" })
	String activityGroup(@Select ActivityGroup activityGroup);
	
	
	
	
	@DefaultMessage("----------")
	@AlternateMessage({
		 "K00_04","R\u00E9gimen ordinario"
		,"K00_08",""
		,"K00_10",""
		,"K00_18",""
		,"K00_21",""
		,"K01_04","Operaciones intragrupo"
		,"K01_08",""
		,"K01_10",""
		,"K01_18",""
		,"K01_21",""
		,"K02_04","R\u00E9gimen especial de bienes usados, objetos de arte, antig√ºedades y objetos de colecci\u00F3n"
		,"K02_08",""
		,"K02_10",""
		,"K02_18",""
		,"K02_21",""
		,"K03_18","R\u00E9gimen especial de agencias de viaje"
		,"K03_21",""
		,"K04_04","Adquisiciones intracomunitarias de bienes"
		,"K04_08",""
		,"K04_10",""
		,"K04_18",""
		,"K04_21",""
		,"K05_04","Adquisiciones intracomunitarias de servicios"
		,"K05_08",""
		,"K05_10",""
		,"K05_18",""
		,"K05_21",""
		,"K06","IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
		,"K07","Modificaci\u00F3n de bases y cuotas"
		,"K08","Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
		,"K09","Total bases y cuotas IVA"
		,"K10_05","Recargo de equivalencia"
		,"K10_1",""
		,"K10_14",""
		,"K10_4",""
		,"K10_52",""
		,"K10_175",""
		,"K11","Modificaci\u00F3n recargo equivalencia"
		,"K12","Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
		,"K13","Total cuotas IVA y recargo de equivalencia"
		,"K14_04","IVA deducible en operaciones interiores de bienes y servicios corrientes"
		,"K14_07",""
		,"K14_08",""
		,"K14_10",""
		,"K14_16",""
		,"K14_18",""
		,"K14_21",""
		,"K15","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
		,"K16_04","IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
		,"K16_07",""
		,"K16_08",""
		,"K16_10",""
		,"K16_16",""
		,"K16_18",""
		,"K16_21",""
		,"K17","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
		,"K18_04","IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
		,"K18_07",""
		,"K18_08",""
		,"K18_10",""
		,"K18_16",""
		,"K18_18",""
		,"K18_21",""
		,"K19","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
		,"K20_04","IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
		,"K20_07",""
		,"K20_08",""
		,"K20_10",""
		,"K20_16",""
		,"K20_18",""
		,"K20_21",""
		,"K21","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
		,"K22_04","IVA deducible en importaciones de bienes corrientes"
		,"K22_07",""
		,"K22_08",""
		,"K22_10",""
		,"K22_16",""
		,"K22_18",""
		,"K22_21",""
		,"K23","Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
		,"K24_04","IVA deducible en importaciones de bienes de inversi\u00F3n"
		,"K24_07",""
		,"K24_08",""
		,"K24_10",""
		,"K24_16",""
		,"K24_18",""
		,"K24_21",""
		,"K25","Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n"
		,"K26_04","IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
		,"K26_07",""
		,"K26_08",""
		,"K26_10",""
		,"K26_16",""
		,"K26_18",""
		,"K26_21",""
		,"K27","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes" 
		,"K28_04","IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"K28_07",""
		,"K28_08",""
		,"K28_10",""
		,"K28_16",""
		,"K28_18",""
		,"K28_21",""
		,"K29","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"K30_04","IVA deducible en adquisiciones intracomunitarias de servicios"
		,"K30_07",""
		,"K30_08",""
		,"K30_10",""
		,"K30_16",""
		,"K30_18",""
		,"K30_21",""
		,"K31","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
		,"K32","Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
		,"K33","Rectificaci\u00F3n de deducciones"
		,"K34","Regularizaci\u00F3n de bienes de inversi\u00F3n"
		,"K35","Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
		,"K36","Suma de deducciones"
		,"K37","Resultado r\u00E9gimen general"
	})
	
	String mod390DetailKey(@Select Mod390DetailKey key);
	
}
