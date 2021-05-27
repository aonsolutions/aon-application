package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IMod130Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod130WriterAEAT2016 implements IMod130Writer{ 

	private static enum Mod130File {
		
		// **************************************************************** AEAT 									
		AEAT_2016 ( mod130 -> true ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("130")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append("<AUX>")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 70))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 213))
		   ,(wr, mod) -> wr.append("</AUX>")
		   
		   ,(wr, mod) -> wr.append("<T13001000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C02),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C04),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C05),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C08),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C09),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C11),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C131),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C16),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C17),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.C18),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod130Key.C19),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
		   ,(wr, mod) -> wr.append(mod.isComplementary()
				   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
				   					:AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 96))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T13001000>")
		   ,(wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("130")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod130File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod130 mod130) {
			return accepter.accept(mod130);
		}
		private void fillPage(Mod130 mod130, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod130);
			}
		}
	}

	public void fillWriter(Mod130 mod130, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod130File format : Mod130File.values()) {
			if (format.accept(mod130)) {
				format.fillPage(mod130, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
