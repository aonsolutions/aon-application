package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IMod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303WriterGIPUZKOA2017 implements IMod303Writer{

	private static enum Mod303File {
		
		 GIPUZKOA_2015_R01 ( mod -> true ,new IPropertyFiller[] {
			
// 			Los campos de Importes con signo, serán: Signo (0 o -) + X enteros + 2 decimales, excepto
//			el Porcentaje de Gipuzkoa, que se compone de 3 posiciones enteras y 4 posiciones decimales			
			 (wr, mod) -> wr.append(AonFiscalFileUtils.text(AonStringUtils.defaultString(mod.getDescription(Mod303Key.GP_I000), mod.getDocument()),9))      // Nif presentador AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))      // Nif declarante AN9
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))   // Ejercicio N4
			,(wr, mod) -> wr.append(mod.getPeriod().isQuarterPeriod() ? "300" : "320") // Modelo 300 (si presentacion trimestral) o 320 (si presentacion mensual) AN3
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2)) // Periodo AN2
			
			// FALTA - Que hay que poner ?? en los que genera el programa de ayuda, y en los que genera hasta ahora AON, siempre pone 01
			,(wr, mod) -> wr.append("01") // Código de registro N2
			
			,(wr, mod) -> wr.append((mod.getFinanceCCC() != null) && (mod.getFinanceCCC().length() == 20)?AonFiscalFileUtils.text(mod.getFinanceCCC(),20):AonFiscalFileUtils.zeros(20)) // Código cuenta cliente N20
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))	                // Cuatro primeras posiciones del IBAN (ESXX) AN4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(3)) 							            // Libre blancos AN3 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.isWithoutActivity()))                   // [01] Sin actividad AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A001)))          // [PRE] Autoliquidacion Concursal PRE AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.mark(mod.getAmount(Mod303Key.GP_A002)))          // [POST] Autoliquidacion Concursal POST AN1 (X o blanco)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C002),15))     //	[02] Base al 21 Signo+N14 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C003),14))     //	[03] Cuota al 21 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C004),15))     // [04] Base al 10 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C005),14))     // [05] Cuota al 10 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C006),15))     // [06] Base al 4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C007),14))     // [07] Cuota al 4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C039),15))     // [39] Modificación de base régimen general Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C040),14))     // [40] Modificación de cuota régimen general Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C008),15))     // [08] Base recargo al 5,2 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C009),14))     // [09] Cuota recargo al 5,2 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C010),15))     // [10] Base recargo al 1,4 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C011),14))     // [11] Cuota recargo al 1,4 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C012),15))     // [12] Base recargo al 0,5 Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C013),14))     // [13] Cuota recargo al 0,5 Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C041),15))     // [41] Modificación de base recargo equivalencia Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C042),14))     // [42] Modificación de cuota recargo equivalencia Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C014),15))     // [14] Base adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C015),14))     // [15] Cuota adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C043),15))     // [43] Base otras operaciones inversión sujeto pasivo (compras) Signo+N14	
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C044),14))     // [44] Cuota otras operaciones inversión sujeto pasivo (compras) Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C016),15))     // [16] Total cuota devengada Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C017),15))     // [17] Base Iva deducible operaciones interiores Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C018),14))     // [18] Cuota Iva deducible operaciones interiores Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C019),15))     // [19] Base Iva deducible importaciones Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C020),14))     // [20] Cuota Iva deducible importaciones Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C021),15))     // [21] Base Iva deducible adquisiciones intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C022),14))     // [22] Cuota Iva deducible adquisiciones intracomunitarias Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C045),15))     // [45] Rectificación de deducciones. Base. Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C046),14))     // [46] Rectificación de deducciones. Cuota. Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C023),14))     // [23] Cuota compensaciones Reg A.G. y P. Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C024),14))     // [24] Cuota Iva deducible Regularización Inversiones Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C025),14))     // [25] Cuota total a deducir Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C026),14))     // [26] Diferencia Signo+N13 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C027),7,4))  // [27] Porcentaje Gipuzkoa N7 (3 enteros + 4 decimales)
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C028),12))     // [28] Atribuible T. H. Gipuzkoa Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C029),11))   // [29] Cuotas a compensar de periodos anteriores en T.H. Gipuzkoa N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C030),15))     // [30] Entregas intracomunitarias Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C031),15))     // [31] Exportaciones y operaciones asimiladas Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C032),15))     // [32] Op. No sujetas o con inversión del sujeto pasivo Signo+N14 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C047),15))     // [47] Base imponible Entregas criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C048),14))     // [48] Cuota Entregas criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C049),15))     // [49] Base imponible Adquisiciones criterio de caja Signo+N14
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C050),14))     // [50] Cuota Adquisiciones criterio de caja Signo+N13
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod303Key.GP_C035),12))     // [35] Resultado Signo+N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)>=0?mod.getAmount(Mod303Key.GP_C035):0.0,11))  // [36] A ingresar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.COMPENSATE?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))  // [37] A compensar N11 
            ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.GP_C035)<0 && mod.getDeclarationType() == FiscalModelDeclarationType.PAYBACK?mod.getAmount(Mod303Key.GP_C035)*(-1):0.0,11))     // [38] A devolver N11
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})				
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303);
		}
		private void fillPage(Mod303 mod303, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod303);
			}
		}
	}

	public void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File format : Mod303File.values()) {
			if (format.accept(mod303)) {
				format.fillPage(mod303, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
