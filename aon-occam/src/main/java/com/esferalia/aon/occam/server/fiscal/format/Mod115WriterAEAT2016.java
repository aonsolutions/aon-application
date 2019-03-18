package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IMod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod115WriterAEAT2016 implements IMod115Writer{ 

	private static enum Mod115File {
		
		AEAT_2016 ( mod115 -> (mod115.isAEAT() && mod115.getYear() > 2015) ,new IPropertyFiller[] { 
				(wr, mod) -> wr.append("<T")
			   ,(wr, mod) -> wr.append("115")
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
			   
			   ,(wr, mod) -> wr.append("<T11501000>")
			   ,(wr, mod) -> wr.append(" ")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))

			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod115Key.CT_C01),15,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod115Key.CT_C02),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod115Key.CT_C03),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod115Key.CT_C04),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod115Key.CT_C05),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
					   					:AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 236))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append("</T11501000>")
			   ,(wr, mod) -> wr.append("</T")
			   ,(wr, mod) -> wr.append("115")
			   ,(wr, mod) -> wr.append("0")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
			   ,(wr, mod) -> wr.append("0000>")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod115File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod115 mod115) {
			return accepter.accept(mod115);
		}
		private void fillPage(Mod115 mod111, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod111);
			}
		}
	}

	public void fillWriter(Mod115 mod115, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod115File format : Mod115File.values()) {
			if (format.accept(mod115)) {
				format.fillPage(mod115, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
