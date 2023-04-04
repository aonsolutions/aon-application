package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;


public class Mod202Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod202 mod202);
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod202 mod202) throws IOException;
	}
	protected interface IMod202Writer {
		public void fillWriter(Mod202 mod202, Writer wr) throws IOException;	
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod202Writer getInstance();
	}

	
	private enum Writers {
		 AEAT_2023		(mod202 -> (mod202.isAEAT() && (mod202.getYear() >= 2023))	, Mod202WriterAEAT2023::new)
		,AEAT_2019		(mod202 -> (mod202.isAEAT() && (mod202.getYear() >= 2019 && mod202.getYear() < 2023))	, Mod202WriterAEAT2019::new)
		,AEAT_2017_1	(mod202 -> (mod202.isAEAT() && (mod202.getYear() == 2018 && mod202.getPeriod().ordinal() >= Period.T2.ordinal()))	, Mod202WriterAEAT20172::new)
		,AEAT_2017_2	(mod202 -> (mod202.isAEAT() && (mod202.getYear() == 2017 || (mod202.getYear() == 2018 && mod202.getPeriod().ordinal() < Period.T2.ordinal()) ))	, Mod202WriterAEAT20171::new)
		,AEAT_2016_2	(mod202 -> (mod202.isAEAT() && mod202.getYear() == 2016 && mod202.getPeriod() != Period.T1)	, Mod202WriterAEAT20162::new)
		,AEAT_2016_1	(mod202 -> (mod202.isAEAT() && mod202.getYear() == 2016 && mod202.getPeriod() == Period.T1)	, Mod202WriterAEAT20161::new)
		,AEAT_2015		(mod202 -> (mod202.isAEAT() && mod202.getYear() < 2016) 									, Mod202WriterAEAT2015::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod202 mod202) {
			return accepter.accept(mod202);
		}
		public IMod202Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod202 mod202, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod202)) {
				writer.getInstance().fillWriter(mod202, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
}
