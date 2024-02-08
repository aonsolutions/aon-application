package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod390HFWriter {
	
	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod390HF mod390HF);
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod390HFWriter getInstance();
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod390HF mod390HF) throws IOException;
	}
	protected interface IMod390HFWriter {
		public void fillWriter(Mod390HF mod390HF, Writer wr) throws IOException;	
	}
	
	protected static SimpleDateFormat df = new SimpleDateFormat("MMMMM");
	
	private enum Writers {
		 ARABA_2023		(mod -> (mod.isAraba() && mod.getYear() >= 2023), Mod390HFWriterARABA2023::new)
		,ARABA_2021		(mod -> (mod.isAraba() && mod.getYear() >= 2021 && mod.getYear() < 2023), Mod390HFWriterARABA2021::new)
		,ARABA_2019		(mod -> (mod.isAraba() && mod.getYear() >= 2019 && mod.getYear() < 2021), Mod390HFWriterARABA2019::new)
		,ARABA_2017		(mod -> (mod.isAraba() && mod.getYear() >= 2017 && mod.getYear() < 2019), Mod390HFWriterARABA2017::new)
		,GIPUZKOA_2023	(mod -> (mod.isGipuzkoa() && mod.getYear() >= 2023), Mod390HFWriterGIPUZKOA2023::new)
		,GIPUZKOA_2021	(mod -> (mod.isGipuzkoa() && mod.getYear() >= 2021 && mod.getYear() < 2023), Mod390HFWriterGIPUZKOA2021::new)
		,GIPUZKOA_2017	(mod -> (mod.isGipuzkoa() && mod.getYear() >= 2015 && mod.getYear() < 2021), Mod390HFWriterGIPUZKOA2017::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod390HF mod390HF) {
			return accepter.accept(mod390HF);
		}
		public IMod390HFWriter getInstance() {
			return instancer.getInstance();
		}
	}
	
	public static void fillWriter(Mod390HF mod390, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod390)) {
				writer.getInstance().fillWriter(mod390, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
}


