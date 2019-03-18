package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod115Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod115 mod115);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod115 mod115) throws IOException;
	}
	protected interface IMod115Writer {
		public void fillWriter(Mod115 mod115, Writer wr) throws IOException;	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod115Writer getInstance();
	}
	
	private enum Writers {
		 AEAT_2019		(mod115 -> (mod115.isAEAT() && mod115.getYear() >= 2019) 	, Mod115WriterAEAT2019::new)
		,AEAT_2016		(mod115 -> (mod115.isAEAT() && mod115.getYear() >= 2016 && mod115.getYear() < 2019)
				 																	, Mod115WriterAEAT2016::new)
		,BIZKAIA_2016	(mod115 -> (mod115.isBizkaia() && mod115.getYear() >= 2016) , Mod115WriterBIZKAIA2016::new)
		,ARABA_2016		(mod115 -> (mod115.isAraba() && mod115.getYear() >= 2016 )	, Mod115WriterARABA2016::new)
		,GIPUZKOA_2016	(mod115 -> (mod115.isGipuzkoa() && mod115.getYear() >= 2016), Mod115WriterGIPUZKOA2016::new)
		,NAVARRA_2016	(mod115 -> (mod115.isNavarra() && mod115.getYear() >= 2016), Mod115WriterGIPUZKOA2016::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod115 mod115) {
			return accepter.accept(mod115);
		}
		public IMod115Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod115 mod115, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod115)) {
				writer.getInstance().fillWriter(mod115, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
