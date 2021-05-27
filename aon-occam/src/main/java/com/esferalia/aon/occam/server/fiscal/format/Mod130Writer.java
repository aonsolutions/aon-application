package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod130Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod130 mod130);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod130 mod130) throws IOException;
	}
	protected interface IMod130Writer {
		public void fillWriter(Mod130 mod130, Writer wr) throws IOException;	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod130Writer getInstance();
	}

	
	private enum Writers {
		 AEAT_2019		(mod130 -> (mod130.isAEAT() && mod130.getYear() >= 2019) 	, Mod130WriterAEAT2019::new)
		,AEAT_2016		(mod130 -> (mod130.isAEAT() && mod130.getYear() >= 2016 && mod130.getYear() < 2019)
				 																	, Mod130WriterAEAT2016::new)
		,BIZKAIA_2016	(mod130 -> (mod130.isBizkaia() && mod130.getYear() >= 2016) , Mod130WriterBIZKAIA2016::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod130 mod130) {
			return accepter.accept(mod130);
		}
		public IMod130Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod130 mod130, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod130)) {
				writer.getInstance().fillWriter(mod130, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
