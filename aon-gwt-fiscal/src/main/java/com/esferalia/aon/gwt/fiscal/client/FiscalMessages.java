package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKeyGroup;
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
			"GROUP1", "Actividades empresariales sujetas al I.A.E.",
			"GROUP2", "Actividades profesionales sujetas al I.A.E.",
			"GROUP3", "Actividades art\u00EDsticas sujetas al I.A.E.",
			"GROUP4", "Arrendadores de locales de negocios",
			"GROUP5", "Actividades agr\u00EDcolas, ganaderas o pesqueras, no sujetas al I.A.E.",
			"GROUP6", "Otras actividades no sujetas al I.A.E.", "GROUP7",
			"Sujetos pasivos sin actividad" })
	String activityGroup(@Select ActivityGroup activityGroup);

	@DefaultMessage("----------")
	@AlternateMessage({
		 "DEV_001","R\u00E9gimen ordinario"
		,"DEV_002","Operaciones intragrupo"
		,"DEV_003","R\u00E9gimen especial del criterio de caja"
		,"DEV_004","R\u00E9gimen especial de bienes usados, objetos de arte, antig√ºedades y objetos de colecci\u00F3n"
		,"DEV_005","R\u00E9gimen especial de agencias de viaje"
		,"DEV_006","Adquisiciones intracomunitarias de bienes"
		,"DEV_007","Adquisiciones intracomunitarias de servicios"
		,"DEV_008","IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
		,"DEV_009","Modificaci\u00F3n de bases y cuotas"
		,"DEV_010","Modificaci\u00F3n de bases y cuotas de operaciones intragrupo"
		,"DEV_011","Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
		,"DEV_012","Total bases y cuotas IVA"
		,"DEV_013","Recargo de equivalencia"
		,"DEV_014","Modificaci\u00F3n recargo equivalencia"
		,"DEV_015","Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
		,"DEV_016","Total cuotas IVA y recargo de equivalencia"
	
		,"DED_001","IVA deducible en operaciones interiores de bienes y servicios corrientes"
		,"DED_002","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
		,"DED_003","IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
		,"DED_004","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
		,"DED_005","IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
		,"DED_006","Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
		,"DED_007","IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
		,"DED_008","Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
		,"DED_009","IVA deducible en importaciones de bienes corrientes"
		,"DED_010","Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
		,"DED_011","IVA deducible en importaciones de bienes de inversi\u00F3n"
		,"DED_012","Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n"
		,"DED_013","IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
		,"DED_014","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes" 
		,"DED_015","IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"DED_016","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,"DED_017","IVA deducible en adquisiciones intracomunitarias de servicios"
		,"DED_018","Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
		,"DED_019","Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
		,"DED_020","Rectificaci\u00F3n de deducciones"
		,"DED_021","Rectificaci\u00F3n de deducciones por operaciones intragrupo"
		,"DED_022","Regularizaci\u00F3n de bienes de inversi\u00F3n"
		,"DED_023","Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
		,"DED_024","Suma de deducciones"
		,"DED_025","Resultado r\u00E9gimen general"
	})
	String mod390DetailKeyGroup(@Select Mod390DetailKeyGroup group);

}
