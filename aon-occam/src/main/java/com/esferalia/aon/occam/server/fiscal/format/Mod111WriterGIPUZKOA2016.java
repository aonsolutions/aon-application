package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IMod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod111WriterGIPUZKOA2016 implements IMod111Writer{ 

	private static enum Mod111File {
		
		BIZKAIA_R01_2016 ( mod111 -> true ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("R01")
			   ,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod()?"111":"110")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 2))
			   ,(wr, mod) -> wr.append("C")
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getDay(new Date()),2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getMonth(new Date()) + 1,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getYear(new Date()),4))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		,BIZKAIA_R02_2016 ( mod111 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R02")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_RA3_2016 ( mod111 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_R05_2016 ( mod111 -> true ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("R05")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getName()
					   					+ AonStringUtils.SPACE
					   					+ AonStringUtils.trimToEmpty(mod.getSurname()),40))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 33))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 6))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 9))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 2))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 3))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 12))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 5))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			})
		,BIZKAIA_P00_2016 ( mod111 -> true ,new IPropertyFiller[] {
			    (wr, mod) -> wr.append("P00N00001")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C01),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00002")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C02),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00003")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C03),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00004")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C04),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00005")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C05),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00006")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C06),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00007")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C07),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00050")
		       ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C50),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00008")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C08),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00009")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C09),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00010")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C10),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00011")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C11),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00034")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C34T),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0012")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C12),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0023")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C23),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0013")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C13),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0024")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C24),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0014")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C14),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0025")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C25),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0015")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C15),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0026")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C26),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0016")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C16),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0027")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C27),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0017")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C17),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0028")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C28),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0018")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C18),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0029")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C29),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0051")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C51),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0052")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C52),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0019")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C19),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0030")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C30),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0020")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C20),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0031")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C31),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0021")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C21),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0032")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C32),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0022")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C22),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0033")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C33),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0035")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C35T),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0036")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C36T),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0039")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.BZ_C39),16,2))
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
