package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IMod130Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod130WriterBIZKAIA2016 implements IMod130Writer{ 

	private static enum Mod130File {
		
		BIZKAIA_R01_2016 ( mod130 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R01")
		   ,(wr, mod) -> wr.append("130")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text( mod.getPeriod().getFormatName( mod.getAdministration()) 
				   	+ (mod.getPeriod().isMonthPeriod()?"M":""), 25))
		   ,(wr, mod) -> wr.append("C")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 15))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getDay(new Date()),2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getMonth(new Date()) + 1,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(AonDateUtils.getYear(new Date()),4))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_R02_2016 ( mod130 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R02")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_RA3_2016 ( mod130 -> true ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_R05_2016 ( mod130 -> true ,new IPropertyFiller[] {
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
		,BIZKAIA_P00_2016 ( mod130 -> true ,new IPropertyFiller[] {
			    (wr, mod) -> wr.append("P00IM0001")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C01),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0002")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C02),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0003")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C03),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0004")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C04),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0005")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C05),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0006")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C06),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0007")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C07),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0008")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C08),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0009")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C09),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0010")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C10),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0011")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C11),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0012")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C12),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0015")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C15),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0016")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C16),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0017")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C17),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0018")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C18),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0019")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C19),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0020")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C20),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00IM0028")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedStandard(mod.getAmount(Mod130Key.C28),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00FE0900")
			   ,(wr, mod) -> wr.append("      ")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod130Key.P3),4,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00P20901")
			   ,(wr, mod) -> wr.append("005.00")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00P20902")
			   ,(wr, mod) -> wr.append("002.00")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00P20903")
			   ,(wr, mod) -> wr.append("020.00")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00P20905")
			   ,(wr, mod) -> wr.append("000.50")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00P20906")
			   ,(wr, mod) -> wr.append("000.25")
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
