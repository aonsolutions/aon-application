package com.esferalia.aon.occam.server.fiscal.format.mod303;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod303Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod303 mod303);
	}
	@FunctionalInterface
	protected interface IWriterInstance {
		public IMod303Writer getInstance();
	}
	@FunctionalInterface
	protected interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod303 mod303) throws IOException;
	}
	protected interface IMod303Writer {
		public void fillWriter(Mod303 mod303, Writer wr) throws IOException;	
	}
	
	private enum Writers {
		 AEAT_2024		(mod303 -> (mod303.isAEAT() && mod303.getYear() >= 2024), Mod303WriterAEAT2024::new)
		,ARABA_2023		(mod303 -> (mod303.isAraba() && mod303.getYear() >= 2023)	, Mod303WriterARABA2023::new)
		,GIPUZKOA_2023	(mod303 -> (mod303.isGipuzkoa() && mod303.getYear() >= 2023), Mod303WriterGIPUZKOA2023::new)
		,BIZKAIA_2023	(mod303 -> (mod303.isBizkaia() && mod303.getYear() >= 2023) 	, Mod303WriterBIZKAIA2023::new)
		// ejercicios anteriores
		,AEAT_2023		(mod303 -> (mod303.isAEAT() && mod303.getYear() == 2023), Mod303WriterAEAT2023::new)
		,AEAT_2022		(mod303 -> (mod303.isAEAT() && mod303.getYear() == 2022), Mod303WriterAEAT2022::new)
		,AEAT_2021_2	(mod303 -> (mod303.isAEAT() && (mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester())), Mod303WriterAEAT20212::new)
		,AEAT_2021		(mod303 -> (mod303.isAEAT() && mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester()), Mod303WriterAEAT2021::new)
		,AEAT_2019		(mod303 -> (mod303.isAEAT() && mod303.getYear() > 2018 && mod303.getYear() <= 2020), Mod303WriterAEAT2019::new)
		,AEAT_2018T4	(mod303 -> (mod303.getYear() == 2018 && mod303.isLastPeriod()) 	, Mod303WriterAEAT20184T::new)
		,AEAT_2018		(mod303 -> (mod303.isAEAT() && mod303.getYear() == 2018 && !mod303.isLastPeriod()) 	, Mod303WriterAEAT20184T::new)
		,AEAT_2017		(mod303 -> (mod303.isAEAT() && mod303.getYear() == 2017) 	, Mod303WriterAEAT2017::new)
		,AEAT_2016		(mod303 -> (mod303.isAEAT() && mod303.getYear() <= 2016) 	, Mod303WriterAEAT2016::new)
		,BIZKAIA_2017	(mod303 -> (mod303.isBizkaia() && mod303.getYear() < 2023) 	, Mod303WriterBIZKAIA2017::new)
		,ARABA_2017		(mod303 -> (mod303.isAraba() && mod303.getYear() >= 2016 && mod303.getYear() < 2019)	, Mod303WriterARABA2017::new)
		,ARABA_2019		(mod303 -> (mod303.isAraba() && mod303.getYear() >= 2019 && mod303.getYear() < 2022)	, Mod303WriterARABA2019::new)
		,ARABA_2022		(mod303 -> (mod303.isAraba() && mod303.getYear() == 2022)	, Mod303WriterARABA2022::new)
		,GIPUZKOA_2021_2(mod303 -> (mod303.isGipuzkoa() && (mod303.getYear() == 2022 || (mod303.getYear() == 2021 && mod303.getPeriod().isLastSemester())) ), Mod303WriterGIPUZKOA2021::new)
		,GIPUZKOA_2021_1(mod303 -> (mod303.isGipuzkoa() && (mod303.getYear() == 2021 && mod303.getPeriod().isFirstSemester()) ), Mod303WriterGIPUZKOA2017::new)
		,GIPUZKOA_2017	(mod303 -> (mod303.isGipuzkoa() && mod303.getYear() < 2021), Mod303WriterGIPUZKOA2017::new)
		,NAVARRA_2022	(mod303 -> (mod303.isNavarra() && mod303.getYear() > 2021 && mod303.getPeriod().isQuarterPeriod()), Mod303WriterNAVARRA2022::new)
		;
		private IModelAccepter accepter;
		private IWriterInstance instancer;
		private Writers(IModelAccepter accepter, IWriterInstance instancer) {
			this.accepter = accepter;
			this.instancer = instancer; 
		}
		public boolean accept(Mod303 mod303) {
			return accepter.accept(mod303);
		}
		public IMod303Writer getInstance() {
			return instancer.getInstance();
		}
	}

	
	public static void fillWriter(Mod303 mod303, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod303)) {
				writer.getInstance().fillWriter(mod303, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
