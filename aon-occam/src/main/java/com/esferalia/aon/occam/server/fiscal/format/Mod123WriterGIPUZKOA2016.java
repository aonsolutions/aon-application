package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IMod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123WriterGIPUZKOA2016 implements IMod123Writer{ 

	private static enum Mod123File {
		
		GIPUZKOA_2016 ( mod123 -> (mod123.isGipuzkoa() && mod123.getYear() > 2015) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append("123")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2))
		   ,(wr, mod) -> wr.append("01")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getFinanceCCC(), 20,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C01),7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C02),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C03),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C04),7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C05),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C06),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C07),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C08),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod123Key.GP_C09),13,2))
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
