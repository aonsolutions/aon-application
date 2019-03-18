package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IMod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod115WriterBIZKAIA2016 implements IMod115Writer{ 

	private static enum Mod115File {
		
		 BIZKAIA_R01_2016 ( mod115 -> (mod115.isBizkaia() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R01")
		   ,(wr, mod) -> wr.append("115")
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
		,BIZKAIA_R02_2016 ( mod115 -> (mod115.isBizkaia() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("R02")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 14))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_RA3_2016 ( mod115 -> (mod115.isBizkaia() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("RA3")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceBankAlias(),25))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),24))
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,BIZKAIA_R05_2016 ( mod115 -> (mod115.isBizkaia() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
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
		,BIZKAIA_P00_2016 ( mod115 -> (mod115.isBizkaia() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			    (wr, mod) -> wr.append("P00N00001")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C01),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00002")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C02),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00003")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C03),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00004")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C04),15,0))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00005")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C05),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00006")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C06),16,2))
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
			   ,(wr, mod) -> wr.append("P00N00007")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod115Key.BZ_C07),16,2))
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
