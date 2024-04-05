package com.esferalia.aon.occam.server.fiscal.format.mod131;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod131.Mod131Writer.IActivityPropertyFiller;
import com.esferalia.aon.occam.server.fiscal.format.mod131.Mod131Writer.IMod131Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod131.Mod131Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131WriterAEAT2024 implements IMod131Writer{ 

	private enum Mod131File {
		
		AEAT_2024_BEGIN ( new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("131")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text("2020", 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		})
		,AEAT_2024 ( new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("<T13101000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationResultType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(isDiscapacitado(mod)))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,0)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,0).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,0).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,0).getRes(),17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,1)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,1).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,1).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,1).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,2)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,2).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,2).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,2).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,3)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,3).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,3).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,3).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,4)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,4).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,4).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,4).getRes(),17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C02),17,2))

		   // Actividades sin posibilidad de determinar datos base - Deducción por rentas obtenidas en Ceuta y Melilla
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod131Key.C03_1)))
		   // Actividades sin posibilidad de determinar datos base - Volumen de ventas o ingresos del trimestre
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03),10,2))
//		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03_2),10,2))
		   // Actividades sin posibilidad de determinar datos base - Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03_3),4,2))
		   // Actividades sin posibilidad de determinar datos base - Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Volumen de ingresos del primer trimestre o, si la actividad se ha iniciado en el ejercicio de devengo, del trimestre en el que se haya comenzado su ejercicio
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03_4),10,2))
		   // Actividades sin posibilidad de determinar datos base - Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Número de días en los que ha ejercido la actividad en el primer trimestre /no, si la actividad se ha iniciado en el ejercicio de devengo, del trimestre en el que se haya comenzado su ejercicio
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03_5),3))		   
		   // Actividades sin posibilidad de determinar datos base - Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Número de días en los que previsiblemente ejercerá la actividad durante el año
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C03_6),3))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C04),17,2))
		   
		   // Actividades agrícolas, ganaderas y forestales - A) VOLUMEN DE INGRESOS DEL TRIMESTRE (excepto Ceuta y Melilla) (incluye subvenciones corrientes y compensación IVA) - Ingresos de explotaciones ordinarias
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05),10,2))
//		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_1),10,2))
		   // Actividades agrícolas, ganaderas y forestales - A) VOLUMEN DE INGRESOS DEL TRIMESTRE (excepto Ceuta y Melilla) (incluye subvenciones corrientes y compensación IVA) - Ingresos de explotaciones prioritarias (Reducción 25% agricultores jóvenes D.A. sexta Ley IRPF)
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_2),10,2))
		   // Actividades agrícolas, ganaderas y forestales - A) VOLUMEN DE INGRESOS DEL TRIMESTRE (excepto Ceuta y Melilla) (incluye subvenciones corrientes y compensación IVA) - Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_3),4,2))
		   // Actividades agrícolas, ganaderas y forestales - B) RENTAS OBTENIDAS EN CEUTA Y MELILLA (deducción art. 68.4 Ley IRPF) - Ingresos de explotaciones ordinarias
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_4),10,2))
		   // Actividades agrícolas, ganaderas y forestales - B) RENTAS OBTENIDAS EN CEUTA Y MELILLA (deducción art. 68.4 Ley IRPF) - Ingresos de explotaciones prioritarias (Reducción 25% agricultores jóvenes D.A. sexta Ley IRPF)
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_5),10,2))
		   // Actividades agrícolas, ganaderas y forestales - B) RENTAS OBTENIDAS EN CEUTA Y MELILLA (deducción art. 68.4 Ley IRPF) - Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_6),4,2))
		   // Actividades agrícolas, ganaderas y forestales - C) Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Volumen de ingresos del primer trimestre o, si la actividad se ha iniciado en el ejercicio de devengo, del trimestre en el que se haya comenzado su ejercicio
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_7),10,2))
		   // Actividades agrícolas, ganaderas y forestales - C) Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Número de días en los que ha ejercido la actividad en el primer trimestre o, si la actividad se ha iniciado en el ejercicio de devengo, del trimestre en el que se haya comenzado su ejercicio
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_8),3))
		   // Actividades agrícolas, ganaderas y forestales - C) Deducción por destinar cantidades al pago de préstamos para la adquisición o rehabilitación de la vivienda habitual: Número de días en los que previsiblemente ejercerá la actividad durante el año
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod131Key.C05_9),3))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C05),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C08),17,2))
		   
		   
		   // *******
		   // *******
			// Liquidación (3) - IV. Total liquidación - Deducción del art. 110.3.c) del Reglamento del Impuesto - Cuantía de los rendimientos netos de actividades económicas del ejercicio anterior al de devengo, en el caso de que no excedieran de 12.000 euros
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0,1))
			// Liquidación (3) - IV. Total liquidación - Deducción del art. 110.3.c) del Reglamento del Impuesto - En el caso excepcional de que en el trimestre deba presentar tambien el modelo 130 de pago fraccionado, indique la cantidad reflejada en él por la presente deducción
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0,5,2))
		   // *******
		   // *******
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C091),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C11),17,2))
		   
		   // *******
		   // *******
		   // Liquidación (3) - IV. Total liquidación - Deducción por vivienda - Marque X si tiene derecho a aplicar la deducción por destinar cantidades a la adquisición o rehabilitación de su vivienda habitual utilizando financiación ajena, por las que vaya a tener derecho a deducción por inversión en vivienda habitual (no existe derecho a la deducción si la adquisición o rehabilitación se ha efectuado a partir de 1 de enero de 2013 y no se han efectuado pagos para la construcción o rehabilitación de la vivienda con anterioridad a esa fecha)
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod131Key.P2)))
		   // Liquidación (3) - IV. Total liquidación - Deducción por vivienda - Suma de los importes de la deducción aplicada (casilla 12 del modelo 131) en los trimestres anteriores del ejercicio de devengo
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(0,10,2))
		   // *******
		   // *******
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C13),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isComplementary()))
		   ,(wr, mod) -> wr.append(mod.isComplementary()
				   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
				   					:AonStringUtils.repeat(' ', 13))
		   
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 101))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T13101000>")
		})
		,AEAT_DID (new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T131DID00>")
			// 5	12	34	An	Domiciliación - IBAN		
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 	// Domiciliacion/Devolucion - IBAN
		   // 6	46	200	An	RESERVADO PARA LA A.E.A.T. (Dejar en blanco) 		
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(200))
		   ,(wr, mod) -> wr.append("</T131DID00>")
		})
		,AEAT_2024_END ( new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("131")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		})

		;
		
		private IPropertyFiller[] propertyFillers;

		private Mod131File(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}
		
		private void fillPage(Mod131 mod131, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod131);
			}
		}
		
		private static boolean isDiscapacitado(Mod131 mod) {
			return  AonCollectionUtils.stream( mod.getActivities() ).anyMatch(a -> a.isDis() );
		}
	}

	private enum Mod131ActivityFile {
		 DPA ( new IActivityPropertyFiller[] {
			//1	1	2	An	C	Inicio del identificador de modelo y página.	Obligatorio	 Constante "<T"
			 (wr, mod, act, comp) -> wr.append("<T131DPA")
			,(wr, mod, act, comp) -> wr.append("00")
			,(wr, mod, act, comp) -> wr.append(">")
			//5	12	1	 A 	C	 Indicador de página complementaria.		Blanco (No complementaria) o "C" (Complementaria)
			,(wr, mod, act, comp) -> wr.append(comp>0?"C":" ")
			//6	13	4	An	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Epigrafe IAE
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(act), 4))
			//7	17	1	An	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Epigrafe IAE  - Indicador auxiliar de actividad en el caso de epígrafes 659.4 y 691.9		blanco, "1" o "2"  (Nota 2)
			,(wr, mod, act, comp) -> wr.append(" ")  // TODO
			//8	18	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Comunidad, sociedad civil o similar: porcentaje de participación		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getCom(), 4,2))
			//9	22	3	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Actividad de temporada: nº de días de ejercicio en el año anterior		3 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getTem(), 3))
			//10	25	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Nuevas actividades iniciadas a partir del 1 de enero del ejercicio anterior al devengo: Año de inicio		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getNue(), 4))		    
			//11	29	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Deducción por rentas obtenidas en Ceuta y Melilla		"0" BLANCO, "1" SI, "2" NO.
		    ,(wr, mod, act, comp) -> wr.append(act.isCeu()?"1":"2")
			//12	30	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Ejerce la actividad en un solo local o sin él		"0" BLANCO, "1" SI, "2" NO.
		    ,(wr, mod, act, comp) -> wr.append(act.isLoc()?"1":"2")
			//13	31	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Indique el número de vehículos afectos a la actividad		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getVeh(), 2))		    
			//14	33	1	An	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Si la actividad se realiza con tractocamiones y el titular carece de semirremolques, marque esta casilla		blanco o "X"
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.mark(act.isTns()))		    
			//15	34	1	An	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Si la actividad se realiza con un único tractocamión y sin semirremolques, marque esta casilla		blanco o "X"
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.mark(act.isTss()))		    
			//16	35	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - La capacidad de carga del vehículo es superior a 1000 Kg.		"0" BLANCO, "1" SI, "2" NO.
		    ,(wr, mod, act, comp) -> wr.append(act.isCap()?"1":"2")		    
			//17	36	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Municipio donde se ejerce la actividad		Nota 3
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(extractMun(act), 1))
			//18	37	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Número de bateas y de barcos auxiliares de la empresa		Nota 4
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getBat(), 1))
			//19	38	3	An	C	RESERVADO PARA LA A.E.A.T. (Dejar en blanco) 		blanco
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.spaces(3))
			//20	41	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Si en el año de devengo realiza la actividad en LORCA, seleccione lo que proceda		0, 1 o 2. Nota 5
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getLor(), 1))
			//21	42	1	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Si en el año de devengo realiza la actividad en la Isla de La Palma, seleccione lo que proceda		0, 1 o 2. Nota 5
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getPal(), 1))
			//22	43	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getPrc(), 4,2))
			//23	47	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal asalariado o Personal asalariado de fabricación - Horas anuales - Mayores de 19 años		7 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getMay19Hours(),7))
			//24	54	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal asalariado o Personal asalariado de fabricación - Horas anuales - Menores de 19 años y trabajadores con contratos de aprendizaje o formación que no sean discapacitados		7 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getMen19Hours(),7))
			//25	61	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal asalariado o Personal asalariado de fabricación - Horas anuales - Discapacitados con grado de minusvalía igual o superior al 33 por 100		7 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDisHours(),7))
			//26	68	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal asalariado o Personal asalariado de fabricación - Horas anuales - Horas anuales fijadas en el convenio colectivo vigente		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getYearHours(),4))
	    	//27	72	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Resto personal asalariado - Horas anuales - Mayores de 19 años		7 enteros. Nota 6
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRsMay19Hours(),7))
			//28	79	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Resto personal asalariado - Horas anuales - Menores de 19 años y trabajadores con contratos de aprendizaje o formación que no sean discapacitados		7 enteros. Nota 6
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRsMen19Hours(),7))
			//29	86	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Resto personal asalariado - Horas anuales - Discapacitados con grado de minusvalía igual o superior al 33 por 100		7 enteros. Nota 6
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRsDisHours(),7))
			//30	93	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Resto personal asalariado - Horas anuales - Horas anuales fijadas en el convenio colectivo vigente		4 enteros. Nota 6
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRsYearHours(),4))
			//31	97	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: titular		4 enteros. Nota 7
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getOwnerHours(),4))
			//32	101	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: cónyuge		4 enteros. Nota 7
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getSpouseHours(),4))		    
			//33	105	7	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Personal Empleado - Personal No Asalariado - Horas anuales: hijos menores de 18 años		7 enteros. Nota 7
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getChildMen18Hours(),7))
		    
			//34	112	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Capacidad		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDesks1(),2))
			//35	114	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Mesas		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDeskCapacity1(),4))
			//36	118	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Capacidad		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDesks2(),2))
			//37	120	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Mesas		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDeskCapacity2(),4))
			//38	124	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Capacidad		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDesks3(),2))
			//39	126	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Mesas		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDeskCapacity3(),4))
			//40	130	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Capacidad		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDesks4(),2))
			//41	132	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo Mesas - Mesas		4 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDeskCapacity4(),4))

		    //42	136	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 1 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,0).getValue(),10,2))
		    //43	146	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 1 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,0).getResult(),17,2))
		    
		    //44	163	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 2 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,1).getValue(),10,2))
		    //45	173	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 2 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,1).getResult(),17,2))
		    //46	190	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 3 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,2).getValue(),10,2))
		    //47	200	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 3 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,2).getResult(),17,2))
		    //48	217	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 4 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,3).getValue(),10,2))
		    //49	227	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 4 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,3).getResult(),17,2))
		    //50	244	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 5 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,4).getValue(),10,2))
		    //51	254	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 5 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,4).getResult(),17,2))
		    //52	271	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 6 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,5).getValue(),10,2))
		    //53	281	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 6 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,5).getResult(),17,2))
		    //54	298	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 7 - Nº Unidades	 	8 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,6).getValue(),10,2))
		    //55	308	17	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Módulo 7 - Rendimiento neto por módulo	 	15 enteros y 2 decimales. Nota 8
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(ensureModule(act,6).getResult(),17,2))
		    //56	325	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B. Rendimiento a efectos de pagos fraccionados - Incentivos al empleo		8 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIem(),10,2))
			//57	335	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B. Rendimiento a efectos de pagos fraccionados - Incentivos a la inversión		8 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIin(),10,2))
			//58	345	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B1. Índices correctores - 1. Especiales		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIc1(),4,2))
			//59	349	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B1. Índices correctores - 2. Empresas de pequeña dimensión		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIc2(),4,2))
			//60	353	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B1. Índices correctores - 3. De temporada		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIc3(),4,2))
			//61	357	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B1. Índices correctores - 4. De exceso		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIc4(),4,2))
			//62	361	4	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - B1. Índices correctores - 5. De inicio de nueva actividad		2 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getIc5(),4,2))
			//63	365	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Reducción para actividades económicas realizadas en el término municipal de Lorca		8 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRlo(),10,2))
			//64	375	10	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Reducción para actividades económicas realizadas en la Isla de La Palma		8 enteros y 2 decimales
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getRpa(),10,2))
			//65	385	2	Num	C	Liquidación (3) - I. Activ. económicas estimac. objetiva - Actividad - Días de ejercicio en el trimestre		2 enteros
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.unsigned(act.getDia(),2))
			//66	387	200	An	C	RESERVADO PARA LA A.E.A.T. (Dejar en blanco) 		
		    ,(wr, mod, act, comp) -> wr.append(AonFiscalFileUtils.spaces(200))
		    //67	587	12	An	C	Indicador de fin de registro	Obligatorio	Constante "</T131DPA00>"
		    ,(wr, mod, act, comp) -> wr.append("</T131DPA00>")
		})
		;
		private IActivityPropertyFiller[] propertyFillers;

		private Mod131ActivityFile(IActivityPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}
		
		private static int extractMun(Mod131Activity act) {
			if ( "6594".equals(extractEpigraph( act) )) { // quioscos
				if (act.getMun() == 0) return 5;		// Hasta 2.000 habitantes. 
				else if (act.getMun() == 1) return 4;	// Desde 2.001 hasta 5.000 habitantes.
				else if (act.getMun() == 2) return 3;	// Desde 5.001 hasta 10.000 habitantes.
				else if (act.getMun() == 3) return 3;	// Desde 10.001 hasta 50.000 habitantes.
				else if (act.getMun() == 4) return 3;	// Desde 50.001 hasta 100.000 habitantes.
				else if (act.getMun() == 5) return 2;	// M\u00E1s de 100.000 habitantes.
				else if (act.getMun() == 6) return 1;	// Madrid o Barcelona.
			} else if ( "7212".equals(extractEpigraph( act) )) { // Transporte por autotaxis
				if (act.getMun() == 0) return 5;		// Hasta 2.000 habitantes. 
				else if (act.getMun() == 1) return 4;	// Desde 2.001 hasta 5.000 habitantes.
				else if (act.getMun() == 2) return 4;	// Desde 5.001 hasta 10.000 habitantes.
				else if (act.getMun() == 3) return 3;	// Desde 10.001 hasta 50.000 habitantes.
				else if (act.getMun() == 4) return 2;	// Desde 50.001 hasta 100.000 habitantes.
				else if (act.getMun() == 5) return 1;	// M\u00E1s de 100.000 habitantes.
				else if (act.getMun() == 6) return 1;	// Madrid o Barcelona.
			}
			if (act.getMun() == 0) return 1;		// Hasta 2.000 habitantes. 
			else if (act.getMun() == 1) return 2;	// Desde 2.001 hasta 5.000 habitantes.
			else if (act.getMun() == 2) return 3;	// Desde 5.001 hasta 10.000 habitantes.
			else if (act.getMun() == 3) return 3;	// Desde 10.001 hasta 50.000 habitantes.
			else if (act.getMun() == 4) return 3;	// Desde 50.001 hasta 100.000 habitantes.
			else if (act.getMun() == 5) return 3;	// M\u00E1s de 100.000 habitantes.
			else if (act.getMun() == 6) return 3;	// Madrid o Barcelona.

			return 0;
		}

		private void fillPage(Mod131 mod131, Mod131Activity act, Writer wr, int comp) throws IOException {
			for (IActivityPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod131, act, comp);
			}
		}
		
	}

	public void fillWriter(Mod131 mod131, Writer wr) {
		try {
			Mod131File.AEAT_2024_BEGIN.fillPage(mod131, wr);
			Mod131File.AEAT_2024.fillPage(mod131, wr);
			int page =  0;
			for (Mod131Activity act :  mod131.getActivities() ) {
				if (act.isNotEmpty()) {
					Mod131ActivityFile.DPA.fillPage(mod131, act, wr , page);
					++page;		
				}
			}
			Mod131File.AEAT_DID.fillPage(mod131, wr);
			Mod131File.AEAT_2024_END.fillPage(mod131, wr);
		} catch (IOException e) {
			throw new AonCoreException("Error desconocido durante la generaci\u00F3n de el modelo.");
		}
	}
	
	public static Mod131Activity ensureActivity(Mod131 mod, int i) {
		return mod.getActivities().get(i); 
	}
	public static Mod131ActivityModule ensureModule(Mod131Activity act, int i) {
		return act.getModules().get(i); 
	}
	
	public static String extractEpigraph(Mod131Activity activity) {
		return AonStringUtils.remove(activity.getEpigraph(), AonStringUtils.DOT);
	}
	
}

