package com.esferalia.aon.occam.server.fiscal.format.mod123;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer.IMod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123WriterGIPUZKOA2024 implements IMod123Writer{ 

	private static enum Mod123File {
		
		GIPUZKOA_2024 ( mod123 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append(AonFiscalFileUtils.text(
					(AonStringUtils.isNotBlank(mod.getDescription(Mod123Key.GP_X00))
							?mod.getDescription(Mod123Key.GP_X00)
							:mod.getDocument()),9))                                        // Nif presentador AN9
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))           // NIF Declarante
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))        // Ejercicio 
		   ,(wr, mod) -> wr.append("123")                                                  // Modelo
 		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))  // Periodo
		   ,(wr, mod) -> wr.append("01")                                                   // Código Registro
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.spaces(24))                          // Vacío
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34))       // Cuenta IBAN
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C01),7,0))  // Dividendos - Nº de Rentas
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C02),13,2)) // Dividendos - Base
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C03),13,2)) // Dividendos - Retenciones e ingresos a cuenta
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C04),7,0))  // Resto de rentas - Nº de Rentas                   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C05),13,2)) // Resto de rentas - Base                           
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C06),13,2)) // Resto de rentas - Retenciones e ingresos a cuenta
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C07),7,0))  // Totales - Nº de Rentas                   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C08),13,2)) // Totales - Base                           
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C09),13,2)) // Totales - Retenciones e ingresos a cuenta

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C10),13,2)) // Periodificación - Ingresos ejercicios anteriores
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C11),13,2)) // Periodificación - Regularización
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C12),13,2)) // A ingresar
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod123File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod123 mod123) {
			return accepter.accept(mod123);
		}
		private void fillPage(Mod123 mod123, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod123);
			}
		}
	}

	public void fillWriter(Mod123 mod123, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod123File format : Mod123File.values()) {
			if (format.accept(mod123)) {
				format.fillPage(mod123, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
