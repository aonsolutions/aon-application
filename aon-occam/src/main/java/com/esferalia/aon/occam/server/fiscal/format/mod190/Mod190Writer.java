package com.esferalia.aon.occam.server.fiscal.format.mod190;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod190Writer {

	@FunctionalInterface
	interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod190 mod190, Mod190Detail detail) throws IOException;
	}
	private enum Writers {
		AEAT_2023 {

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2023Aeat.fill(mod190, wr);
			}
			
			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return mod190.isAEAT() && mod190.getYear() >= 2023;				
			}
			
		},
		AEAT_2022 {

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2022Aeat.fill(mod190, wr);
			}
			
			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return mod190.isAEAT() && mod190.getYear() == 2022;				
			}
			
		},
		AEAT_2017 {

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}
			
			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return mod190.isAEAT() && mod190.getYear() >= 2017 && mod190.getYear() <= 2021;				
			}
			
		},
		AEAT_2016{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Aeat.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isAEAT() && mod190.getYear() == 2016);
			}
			
		},
		AEAT_2015{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2015Aeat.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isAEAT() && mod190.getYear() < 2016);
			}
			
		},
		BIZKAIA_2017{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Bizkaia.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isBizkaia() && mod190.getYear() >= 2017);
			}
			
		},

		BIZKAIA_2016{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Bizkaia.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isBizkaia() && mod190.getYear() < 2017);
			}
			
		},

		GIPUZKOA_2023{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2023Gipuzkoa.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2023Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isGipuzkoa() && mod190.getYear() >= 2023);
			}
			
		},
		GIPUZKOA_2022{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2022Gipuzkoa.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isGipuzkoa() && mod190.getYear() >= 2022 && mod190.getYear() < 2023);
			}
			
		},

		GIPUZKOA_2017{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Gipuzkoa.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isGipuzkoa() && mod190.getYear() >= 2017 && mod190.getYear() <= 2021);
			}
			
		},

		GIPUZKOA_2016{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Gipuzkoa.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isGipuzkoa() && mod190.getYear() == 2016); 
			}
			
		},

		GIPUZKOA_2015{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2015Gipuzkoa.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2015Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isGipuzkoa() && mod190.getYear() < 2016);
			}
			
		},

		ARABA_2023{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2023Araba.fill(mod190, wr);		
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2023Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isAraba() && mod190.getYear() >= 2023);
			}
			
		},
		ARABA_2017{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Araba.fill(mod190, wr);		
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isAraba() && mod190.getYear() >= 2017 && mod190.getYear() < 2023);
			}
			
		},

		ARABA_2016{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Araba.fill(mod190, wr);
			}
			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2016Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isAraba() && mod190.getYear() < 2017);
			}
			
		},
		NAVARRA_2021{

			@Override
			void fill(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2021Navarra.fill(mod190, wr);
			}

			@Override
			void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException {
				Mod190File2017Aeat.fill(mod190, wr);
			}

			@Override
			boolean accept(Mod190 mod190) {
				return (mod190.isNavarra());
			}
			
		}
		;

		abstract boolean accept(Mod190 mod190);
		abstract void fill(Mod190 mod190, Writer wr) throws IOException;
		abstract void fillBoeFormat(Mod190 mod190, Writer wr) throws IOException;
		
	}
	public static void fillWriter(Mod190 mod190, Writer wr) throws IOException {
		fillWriter(mod190, wr, false);
	}
	
	public static void fillWriter(Mod190 mod190, Writer wr, boolean boeFormat) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod190)) {
				if (boeFormat) {
					writer.fillBoeFormat(mod190, wr);
				} else {
					writer.fill(mod190, wr);
				}
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

}
