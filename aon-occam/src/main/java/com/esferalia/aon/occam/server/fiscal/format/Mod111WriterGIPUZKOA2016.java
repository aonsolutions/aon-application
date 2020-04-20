package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IMod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod111WriterGIPUZKOA2016 implements IMod111Writer{ 

	private static enum Mod111File {
		
		GIPUZKOA_2016 ( mod111 -> true ,new IPropertyFiller[] {
				(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod()?"111":"110")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2))
			   ,(wr, mod) -> wr.append("01")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceCCC(), 20))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),4))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C01),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C02),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C03),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C04),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C05),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C06),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C07),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C08),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C09),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C10),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C11),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C12),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C13),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C14),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C15),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C16),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C17),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C18),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C19),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C20),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C21),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C22),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C23),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C24),7,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C25),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C26),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C27),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C28),13,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod111Key.GP_C29),13,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod111File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod111 mod111) {
			return accepter.accept(mod111);
		}
		private void fillPage(Mod111 mod111, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod111);
			}
		}
	}

	public void fillWriter(Mod111 mod111, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod111File format : Mod111File.values()) {
			if (format.accept(mod111)) {
				format.fillPage(mod111, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
