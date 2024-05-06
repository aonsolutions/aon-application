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

public class Mod123WriterAEAT2024 implements IMod123Writer{ 

	private enum Mod123File {
		
		AEAT_2024 ( mod123 -> true ,new IPropertyFiller[] { 
				(wr, mod) -> wr.append("<T")
			   ,(wr, mod) -> wr.append("123")
			   ,(wr, mod) -> wr.append("0")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
			   ,(wr, mod) -> wr.append("0000>")
			   ,(wr, mod) -> wr.append("<AUX>")
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text("2024", 4))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonFiscalFileUtils.DEVELOPER_NIF, 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
			   ,(wr, mod) -> wr.append("</AUX>")
			   
			   ,(wr, mod) -> wr.append("<T12301000>")
			   ,(wr, mod) -> wr.append(" ")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationResultType().getValue(), 1))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
//			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
//			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFullName(),80))  // Identificación(1). Sujeto pasivo. Razón/denominación social, apellidos y nombre
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C01),15,0))  // Liquidación(3). Número de rentas. Dividendos y otras rentas (...) [01]                                               
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C02),15,0))  // Liquidación(3). Número de rentas. Resto de rentas [02]                                                               
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C03),15,0))  // Liquidación(3). Número de rentas. Totales [03]                                                                       
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C04),17,2))  // Liquidación(3). Base de retenciones e ingresos a cuenta. Dividendos y otras rentas (...) [04]                        
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C05),17,2))  // Liquidación(3). Base de retenciones e ingresos a cuenta. Resto de rentas [05]                                        
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C06),17,2))  // Liquidación(3). Base de retenciones e ingresos a cuenta. Totales [06]                                                
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C07),17,2))  // Liquidación(3). Retenciones e ingresos a cuenta. Dividendos y otras rentas (...) [07]                                
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C08),17,2))  // Liquidación(3). Retenciones e ingresos a cuenta. Resto de rentas [08]                                                
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C09),17,2))  // Liquidación(3). Retenciones e ingresos a cuenta. Totales [09]                                                        
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C10),17,2))  // Liquidación(3). Periodificación. Ingresos ejercicios anteriores [10]                                                 
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C11),17,2))  // Liquidación(3). Periodificación. Regularización. [11]                                                                
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C12),17,2))  // Liquidación(3). Suma de retenciones e ingresos a cuenta y regularización, en su caso ( [09] + [11] ) [12]            
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C13),17,2))  // Liquidación(3). Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo [13]
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod123Key.CT_C14),17,2))	 // Liquidación(3). Resultado a ingresar ( [12] - [13] ) [14]                                                            
			   
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
					   					:AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 200))  // Reservado AEAT 
			   ,(wr, mod) -> wr.append("</T12301000>")
			   ,(wr, mod) -> wr.append("</T")
			   ,(wr, mod) -> wr.append("123")
			   ,(wr, mod) -> wr.append("0")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
			   ,(wr, mod) -> wr.append("0000>")
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
