package com.esferalia.aon.occam.server.fiscal.format.mod131;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod131Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod131 mod131);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod131 mod131) throws IOException;
	}
	@FunctionalInterface
	protected interface IActivityPropertyFiller {
		public void propertyFill(Writer writer, Mod131 mod131, Mod131Activity act, int comp) throws IOException;
	}
	protected interface IMod131Writer {
		public void fillWriter(Mod131 mod131, Writer wr);	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod131Writer getInstance();
	}

	
	private enum Writers {
		 AEAT_2024	(m -> (m.isAEAT() && m.getYear() >= 2024), Mod131WriterAEAT2024::new)
		,AEAT_2019	(m -> (m.isAEAT() && m.getYear() >= 2019 && m.getYear() < 2024), Mod131WriterAEAT2019::new)
		,AEAT_2016	(m -> (m.isAEAT() && m.getYear() >= 2016 && m.getYear() < 2019), Mod131WriterAEAT2016::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod131 mod131) {
			return accepter.accept(mod131);
		}
		public IMod131Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod131 mod131, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod131)) {
				writer.getInstance().fillWriter(mod131, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
