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

public class Mod115WriterGIPUZKOA2016 implements IMod115Writer{ 

	private static enum Mod115File {
		
		GIPUZKOA_2016 ( mod115 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append("115")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2))
		   ,(wr, mod) -> wr.append("01")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceCCC(), 20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C01),7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C02),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C03),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C04),7,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C05),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C06),13,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.GP_C07),13,2))
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
