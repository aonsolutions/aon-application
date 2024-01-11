package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IMod390HFWriter;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod390HFWriter.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFWriterGIPUZKOA2023 implements IMod390HFWriter {

	private static enum Mod390HFFile {
		GIPUZKOA_2015_R01 ( mod -> true ,new IPropertyFiller[] {
				
//	 			Los campos de Importes con signo, serán: Signo (0 o -) + X enteros + 2 decimales, excepto
//				el Porcentaje de Gipuzkoa, que se compone de 3 posiciones enteras y 4 posiciones decimales			

				 (wr, mod) -> wr.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod390Key.GP_I000), mod.getDocument()),9))      					// Nif presentador AN9
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      					// Nif declarante AN9
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))   					// Ejercicio N4
				,(wr, mod) -> wr.append("390") 																// Modelo 390
				,(wr, mod) -> wr.append("12") 																// Periodo AN2
				,(wr, mod) -> wr.append("01") 																// Código de registro N2
				
				,(wr, mod) -> wr.append((mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20)
						?AonFiscalFileUtils.text(mod.getFinanceCCC(),20)
						:AonFiscalFileUtils.zeros(20)) 														// Código cuenta cliente N20
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))	                // Cuatro primeras posiciones del IBAN (ESXX) AN4
				,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(3))																// LIBRE 

				,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isWithoutActivity()))                   // [01] Sin actividad AN1 (X o blanco)
				,(wr, mod) -> wr.append(" ") 																// GRAN EMPRESA
				,(wr, mod) -> wr.append(" ") 																// REGISTRO EXPORTADORES
				,(wr, mod) -> wr.append(" ") 																// REGISTRO EXPORTADORES
				,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod390Key.GP_A001)))          // Autoliquidación concursal PRE
				,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod390Key.GP_A002)))          // Autoliquidación concursal POST
				
			    ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C002),15))		//02-Base al 21	N	14 
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C003),14))		//03-Cuota al 21	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C004),15))		//04-Base al 10	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C005),14))		//05-Cuota al 10	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C114),15))		//04-Base al 10	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C115),14))		//05-Cuota al 10	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C006),15))		//06-Base al 4	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C007),14))		//07-Cuota al 4	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C116),15))		//06-Base al 4	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C008),15))		//08-Modificación base	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C009),14))		//09-Modificación cuota	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C010),15))		//10-Base recargo al 5,2	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C011),14))		//11-Cuota recargo al 5,2	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C117),15))		//12-Base recargo al 1,4	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C118),14))		//12-Base recargo al 1,4	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C012),15))		//12-Base recargo al 1,4	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C013),14))		//13-Cuota recargo al 1,4	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C014),15))		//14-Base recargo al 0,5	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C015),14))		//15-Cuota recargo al 0,5	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C016),15))		//16-Modificación base recargo	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C017),14))		//17-Modificación cuota recargo	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C018),15))		//18-Base adquisiciones intracomunitarias	N	14 
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C019),14))		//19-Cuota adquisiciones intracomunitarias	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C106),15))		//106-Base otras operaciones inversión sujeto pasivo (compras)	N	14	
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C107),14))		//107-Cuota otras operaciones inversión sujeto pasivo (compras)	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C020),15))		//20-Total cuota devengada anual	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C021),15))		//21-Base Iva deducible operaciones interiores	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C022),14))		//22-Cuota Iva deducible operaciones interiores	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C023),15))		//23-Base Iva deducible importaciones	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C024),14))		//24-Cuota Iva deducible importaciones	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C025),15))		//25-Base Iva deducible adquisiciones intracomunitarias	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C026),14))		//26-Cuota Iva deducible adquisiciones intracomunitarias	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C027),14))		//27-Cuota compensaciones Reg A.G. y P.	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C271),14))		//27.1-Cuota rectificación deducciones	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C028),14))		//28-Cuota Iva deducible Regularización Inversiones	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C029),14))		//29-Cuota total a deducir	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C030),14))		//30-Diferencia	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C031),7,4))	//31-Porcentaje Gipuzkoa	N	7	
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C032),7,4))	//32-Porcentaje Alava	N	7
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C033),7,4))	//33-Porcentaje Bizkaia	N	7
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C034),7,4))	//34-Porcentaje Navarra	N	7
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C035),7,4))	//35-Porcentaje Estado	N	7
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C036),12)) 	//36-Atribuible T. H. Gipuzkoa	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C037),11))	//37-Cuotas a compensar del año anterior	N	11 
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C038),11))	//38-Total ingresos efectuados durante el ejercicio	N	11 
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C039),11))	//39-Total devoluciones efectuadas durante el ejercicio	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C040),12))		//40-Resultado anual	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C041),11))	//41-A ingresar	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C042),11))	//42-A compensar	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_C043),11))	//43-A devolver	N	11
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C044),15))		//44-Existencias iniciales	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C045),15))		//45-Existencias finales	N	14
	            
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C046),15))		//46-Bienes corrientes Base al 21	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C047),14))		//47-Bienes corrientes Cuota al 21	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C048),15))		//48-Bienes corrientes Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C049),14))		//49-Bienes corrientes Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C119),15))		//48-Bienes corrientes Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C120),14))		//49-Bienes corrientes Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C050),15))		//50-Bienes corrientes Base al 4	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C051),14))		//51-Bienes corrientes Cuota al 4	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C121),15))		//48-Bienes corrientes Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C052),15))		//52-Bienes corrientes Base agricultura	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C053),14))		//53-Bienes corrientes Cuota agricultura	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C054),15))		//54-Bienes corrientes Modificación base	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C055),14))		//55-Bienes corrientes Modificación cuota	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C056),14))		//56-Bienes corrientes Cuota total	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C057),15))		//57-Gastos Base al 21	N	14	
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C058),14))		//58-Gastos Cuota al 21	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C059),15))		//59-Gastos Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C060),14))		//60-Gastos Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C122),15))		//59-Gastos Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C123),14))		//60-Gastos Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C061),15))		//61-Gastos Base al 4	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C062),14))		//62-Gastos Cuota al 4	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C124),15))		//59-Gastos Base al 10	N	14	        	
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C063),15))		//63-Gastos Modificación base	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C064),14))		//64-Gastos Modificación cuota	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C065),14))		//65-Gastos Cuota total	N	13
	        	
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C066),15))		//66-Bienes de inversión Base al 21	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C067),14))		//67-Bienes de inversión Cuota al 21	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C068),15))		//68-Bienes de inversión Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C069),14))		//69-Bienes de inversión Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C125),15))		//68-Bienes de inversión Base al 10	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C126),14))		//69-Bienes de inversión Cuota al 10	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C070),15))		//70-Bienes de inversión Base al 4	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C071),14))		//71-Bienes de inversión Cuota al 4	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C127),15))		//72-Bienes de inversión Modificación base	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C072),15))		//72-Bienes de inversión Modificación base	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C073),14))		//73-Bienes de inversión Modificación cuota	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C074),14))		//74-Bienes de inversión Total cuota	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C075),15))		//75-Total bases	N	14
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C076),14))		//76-Total cuotas	N	13
	        	,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C077),14))		//77-Total cuotas	N	13

				,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod390Key.GP_A078)))			//78-Prorrata especial-opción.	AN	1
				,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod390Key.GP_A079)))			//79-Prorrata especial-revocación.	AN	1
				
				// ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C082),15))		//82-Operaciones régimen general	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C108),15))		//82-Operaciones régimen general	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C083),15))		//83-Operaciones régimen especial A.G. Y P.	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C084),15))		//84-Operaciones en régimen especial de recargo de equivalencia	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C109),15))		//85-Entregas intracomunitarias exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C110),15))		//85-Entregas intracomunitarias exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C085),15))		//85-Entregas intracomunitarias exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C086),15))		//86-Exportaciones y otras op.exentas con derecho a deducción	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C087),15))		//87-Operaciones exentas sin derecho a deducción	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C111),15))		//85-Entregas intracomunitarias exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C088),15))		//88-Operaciones dan lugar inversión suj.pasivo	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C112),15))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C113),15))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C091),15))		//91-Entregas no habituales de bienes inmuebles	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C092),15))		//92-Operaciones financieras no habituales	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C093),15))		//93-Entregas de bienes de inversión	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C095),15))		//95-Total volumen de operaciones	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C096),15))		//96-Entr interiores por inversión sujeto pasivo por op. triangulares	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C097),15))		//97-Adquisiciones interiores exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C098),15))		//98-Importaciones exentas	N	14
				,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C099),15))		//99-Adquisiciones intracomunitarias exentas	N	14

	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C101),15))		//101-Base Imponible Entregas criterio de caja	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C102),14))		//102-Cuota Entregas criterio de caja	N	13
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C103),15))		//103-Base Imponible Adquisiciones criterio de caja	N	14
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_C104),14))		//104-Cuota Adquisiciones criterio de caja	N	13

	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getCNAEDescription(mod.getDescription(Mod390Key.GP_P1C)),40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod390Key.GP_P1C),3,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P1I),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P1D),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_P1T),1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_P1P),7,4))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P1S),15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getCNAEDescription(mod.getDescription(Mod390Key.GP_P2C)),40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod390Key.GP_P2C),3,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P2I),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P2D),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_P2T),1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_P2P),7,4))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P2S),15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getCNAEDescription(mod.getDescription(Mod390Key.GP_P3C)),40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod390Key.GP_P3C),3,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P3I),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P3D),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_P3T),1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_P3P),7,4))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P3S),15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getCNAEDescription(mod.getDescription(Mod390Key.GP_P4C)),40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod390Key.GP_P4C),3,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P4I),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P4D),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_P4T),1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_P4P),7,4))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P4S),15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(getCNAEDescription(mod.getDescription(Mod390Key.GP_P5C)),40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getDescription(Mod390Key.GP_P5C),3,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P5I),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P5D),15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_P5T),1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_P5P),7,4))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod390Key.GP_P5S),15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(3))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(7))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(40))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(3))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(1))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(7))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(15))

	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE1N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE1D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE1H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SE1X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE2N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE2D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE2H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SE2X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE3N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE3D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE3H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SE3X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE4N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE4D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE4H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SE4X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE5N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE5D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SE5H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SE5X),9,0))

	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR1N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR1D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR1H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SR1X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR2N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR2D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR2H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SR2X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR3N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR3D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR3H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SR3X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR4N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR4D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR4H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SR4X),9,0))
	            ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR5N),7))
	       		,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR5D),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDescription(Mod390Key.GP_SR5H),10))
				,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod390Key.GP_SR5X),9,0))
				,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod390HFFile(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod390HF mod390HF) {
			return accepter.accept(mod390HF);
		}
		private void fillPage(Mod390HF mod390HF, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod390HF);
			}
		}
	}

	public void fillWriter(Mod390HF mod390HF, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod390HFFile format : Mod390HFFile.values()) {
			if (format.accept(mod390HF)) {
				format.fillPage(mod390HF, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
	private static String getCNAEDescription(String cnae) {
    	String desc = null;
    	if (AonStringUtils.isNotBlank(cnae)) {
    		String cnae1 = AonStringUtils.substring(cnae,0, 1); 
    		String cnae2 = AonStringUtils.substring(cnae,2, 3);
    		CNAE2009 cnae2009 = CNAE2009.valueOfCode(cnae1 + "." + cnae2);
    		if (cnae2009 != null) {
    			desc = cnae2009.getDescription(); 	
    		}
    	}
    	return desc;
	}
}
