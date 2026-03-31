package com.esferalia.aon.occam.server.fiscal.format.mod111;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod111Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod111 mod111);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod111 mod111) throws IOException;
	}
	protected interface IMod111Writer {
		public void fillWriter(Mod111 mod111, Writer wr) throws IOException;	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod111Writer getInstance();
	}
	
	private enum Writers {
		 AEAT_2019		(mod111 -> (mod111.isAEAT() && mod111.getYear() >= 2019), Mod111WriterAEAT2019::new)
		,AEAT_2016		(mod111 -> (mod111.isAEAT() && mod111.getYear() >= 2016 && mod111.getYear() < 2019), Mod111WriterAEAT2016::new)
		,BIZKAIA_2016	(mod111 -> (mod111.isBizkaia() && mod111.getYear() >= 2016), Mod111WriterBIZKAIA2016::new)
		,ARABA_2016		(mod111 -> (mod111.isAraba() && mod111.getYear() >= 2016 ), Mod111WriterARABA2016::new)
		,GIPUZKOA_2016	(mod111 -> (mod111.isGipuzkoa() && mod111.getYear() >= 2016), Mod111WriterGIPUZKOA2016::new)
		,NAVARRA_2026	(mod111 -> (mod111.isNavarra() && mod111.getYear() >= 2026), Mod111WriterNAVARRA2026::new)
		,NAVARRA_2016	(mod111 -> (mod111.isNavarra() && mod111.getYear() >= 2016 && mod111.getYear() <= 2025), Mod111WriterNAVARRA2016::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod111 mod111) {
			return accepter.accept(mod111);
		}
		public IMod111Writer getInstance() {
			return instancer.getInstance();
		}
	}

	public static void fillWriter(Mod111 mod111, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod111)) {
				writer.getInstance().fillWriter(mod111, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n del modelo no est\u00E1 soportada.");
		}
	}
	
}
