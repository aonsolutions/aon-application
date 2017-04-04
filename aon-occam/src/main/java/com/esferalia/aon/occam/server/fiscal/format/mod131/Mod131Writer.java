package com.esferalia.aon.occam.server.fiscal.format.mod131;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131Writer {

	@FunctionalInterface
	private interface IModelAccepter {
		public boolean accept(Mod131 mod131);
	}
	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod131 mod131) throws IOException;
	}

	
	private enum Mod131File2016 {
		
		// **************************************************************** AEAT 									
		AEAT_2016 ( mod131 -> (mod131.isAEAT() && mod131.getYear() > 2015) ,new IPropertyFiller[] { 
			(wr, mod) -> wr.append("<T")
		   ,(wr, mod) -> wr.append("131")
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
		   
		   ,(wr, mod) -> wr.append("<T13101000>")
		   ,(wr, mod) -> wr.append(" ")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationType().getValue(), 1))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?mod.getName():mod.getSurname(),60))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isEntity()?" ":mod.getName(),20))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,0)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,0).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,0).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,0).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,1)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,1).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,1).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,1).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,2)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,2).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,2).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,2).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,3)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,3).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,3).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,3).getRes(),17,2))

		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(extractEpigraph(ensureActivity(mod,4)), 4))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,4).getNet(),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(ensureActivity(mod,4).getPor(),5,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(ensureActivity(mod,4).getRes(),17,2))
		   
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C01),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C02),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C03),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C04),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C05),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C06),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C07),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C08),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C091),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C10),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C11),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C12),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C13),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C14),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.signedZero(mod.getAmount(Mod131Key.C15),17,2))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceIban(),34)) 
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.isComplementary()?"X":" ",1))
		   ,(wr, mod) -> wr.append(mod.isComplementary()
				   					?AonFiscalFileUtils.unsigned(mod.getReplacedNumber(), 13,0)
				   					:AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 100))
		   ,(wr, mod) -> wr.append(AonStringUtils.repeat(' ', 13))
		   ,(wr, mod) -> wr.append("</T13101000>")
		   ,(wr, mod) -> wr.append("</T")
		   ,(wr, mod) -> wr.append("131")
		   ,(wr, mod) -> wr.append("0")
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(), 4,0))
		   ,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))
		   ,(wr, mod) -> wr.append("0000>")
		   ,(wr, mod) -> wr.append(AonStringUtils.CR_LF)
		})

		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod131File2016(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod131 mod131) {
			return accepter.accept(mod131); 
		}
		private void fillPage(Mod131 mod131, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod131);
			}
		}
	}


	public static void fillWriter(Mod131 mod131, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod131File2016 format : Mod131File2016.values()) {
			if (format.accept(mod131)) {
				format.fillPage(mod131, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

	public static Mod131Activity ensureActivity(Mod131 mod, int i) {
		return mod.getActivities().get(i); 
	}
	
	public static String extractEpigraph(Mod131Activity activity) {
		return AonStringUtils.remove(activity.getEpigraph(), AonStringUtils.DOT);
	}
	
}
