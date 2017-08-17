package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303Writer {

	@FunctionalInterface
	private interface IModelAccepter {
		public boolean accept(Mod303 mod303);
	}
	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod303 mod303) throws IOException;
	}

	
	private enum Mod303File2016 {
		
		// **************************************************************** AEAT 									
		AEAT_2017_REG_0_START ( mod303 -> (mod303.isAEAT() && mod303.getYear() > 2016) ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("303")
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
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_1 ( mod303 -> (mod303.isAEAT() && mod303.getYear() > 2016) ,new IPropertyFiller[] {
			(wr, mod) -> wr.append("<T30301000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CM_002)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text((mod.getAmount(Mod303Key.CT_A02)+1),1))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A03)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A04)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.convertDate(mod.getDescription(Mod303Key.CT_A05)))
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A06)==0?" ":"1")		// TODO Soporte POSTCONCURSAL
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A07)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A08)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A09)==1?"1":"2")
		   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A10)==1?"1":"2")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C02), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C04),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C05), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C08), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C09),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C11),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C13),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C16),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C17), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C18),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C19),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C20), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C21),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C22),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C23), 5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C24),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C25),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C26),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C27),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C28),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C29),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C30),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C31),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C32),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C33),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C34),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C35),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C36),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C37),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C38),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C39),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C40),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C41),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C42),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C43),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C44),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C45),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C46),17,2))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 582))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T30301000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_3 ( mod303 -> (mod303.isAEAT() && mod303.getYear() > 2016) ,new IPropertyFiller[] {
				(wr, mod) -> wr.append("<T30303000>")
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C59),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C60),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C61),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C62),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C63),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C74),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C75),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C76),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C64),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C65),9,6))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C66),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C77),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getAmount(Mod303Key.CT_C67),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C68),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C69),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C70),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod303Key.CT_C71),17,2))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
			   ,(wr, mod) -> wr.append(mod.isComplementary()
					   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
					   					:AonStringUtils.repeat(' ', 13))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isWithoutActivity()?"X":" ",1))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 11))
			   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 4	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 1	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat('0',17	))
			   ,(wr, mod) -> wr.append(mod.getAmount(Mod303Key.CT_A11)==0?"0":"1")
			   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 590))
			   ,(wr, mod) -> wr.append("</T30303000>")
			   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		,AEAT_2017_REG_0_END ( mod303 -> (mod303.isAEAT() && mod303.getYear() > 2016) ,new IPropertyFiller[] { 
		    (wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("303")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})
		;
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod303File2016(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303); 
		}
		private void fillPage(Mod303 mod303, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod303);
			}
		}
	}


	public static void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod303File2016 format : Mod303File2016.values()) {
			if (format.accept(mod303)) {
				format.fillPage(mod303, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
}
