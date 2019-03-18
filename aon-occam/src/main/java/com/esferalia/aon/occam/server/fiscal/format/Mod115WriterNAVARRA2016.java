package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IMod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod115WriterNAVARRA2016 implements IMod115Writer{ 

	private static enum Mod115File {
		
		NAVARRA_1_2016 ( mod115 -> (mod115.isNavarra() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("1")
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod()?"759":"760")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getPeriod().getStartMonth() + 1 ), 2))
			,(wr, mod) -> wr.append("0")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getName()
								+ AonStringUtils.SPACE
					   			+ AonStringUtils.trimToEmpty(mod.getSurname()),40))
			,(wr, mod) -> wr.append("00000000000000")
			,(wr, mod) -> wr.append("T")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPhone(),9))	
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPerson(),40))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getNumber(),13))
			,(wr, mod) -> wr.append(" ")
			,(wr, mod) -> wr.append((mod.isReplacement()?"S":(mod.isComplementary()?"C":" ")))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getReplacedNumber(),13))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceCCC(),20))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType() == FiscalModelDeclarationType.DEPOSIT?"1":"0",1))				
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 22))
			,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 8))
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 28))
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,NAVARRA_2_2016 ( mod115 -> (mod115.isNavarra() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("2")
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod()?"715":"745")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getPeriod().getStartMonth() + 1 ), 2))
			,(wr, mod) -> wr.append("0")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			,(wr, mod) -> wr.append("0001")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signed(mod.getAmount(Mod115Key.NF_C01),14,2))
			,(wr, mod) -> wr.append(AonStringUtils.repeat("0000 000000000000000", 9))
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 24))
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,NAVARRA_3_2016 ( mod115 -> (mod115.isNavarra() && mod115.getYear() > 2015) ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("3")
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod()?"715":"745")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getFormatName( mod.getAdministration() ), 1))
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getPeriod().getStartMonth() + 1 ), 2))
			,(wr, mod) -> wr.append("0")
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
			,(wr, mod) -> wr.append("9000")
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 50))
			,(wr, mod) -> wr.append("0000")
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 50))
			,(wr, mod) -> wr.append("0000")
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 50))
			,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 62))
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
