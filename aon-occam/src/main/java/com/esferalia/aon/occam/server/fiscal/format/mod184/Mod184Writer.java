package com.esferalia.aon.occam.server.fiscal.format.mod184;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod184Writer {

	@FunctionalInterface
	interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod184 mod184, Mod184Partner prt, Mod184Income inc) throws IOException;
	}
	
	private enum Writers {
		Mod184_File_2022 {

			@Override
			void fill(Mod184 mod184, Writer wr) throws IOException {
				Mod184File2022.fill(mod184, wr);
			}
			
			@Override
			boolean accept(Mod184 mod184) {
				return (mod184.getYear() >= 2022 );
			}
		},
		Mod184_File_2019 {

			@Override
			void fill(Mod184 mod184, Writer wr) throws IOException {
				Mod184File2019.fill(mod184, wr);
			}
			
			@Override
			boolean accept(Mod184 mod184) {
				return (mod184.getYear() >= 2019 && mod184.getYear() <= 2021);
			}
		},
		Mod184_File_2016 {

			@Override
			void fill(Mod184 mod184, Writer wr) throws IOException {
				Mod184File2016.fill(mod184, wr);
			}
			
			@Override
			boolean accept(Mod184 mod184) {
				return (mod184.getYear() >= 2016 && mod184.getYear() <= 2018);				
			}
		},
		Mod184_File_2015 {

			@Override
			void fill(Mod184 mod184, Writer wr) throws IOException {
				Mod184File2015.fill(mod184, wr);
			}
			
			@Override
			boolean accept(Mod184 mod184) {
				return (mod184.getYear() == 2015 );				
			}
		},
		Mod184_File_2014 {

			@Override
			void fill(Mod184 mod184, Writer wr) throws IOException {
				Mod184File2014.fill(mod184, wr);
			}
			
			@Override
			boolean accept(Mod184 mod184) {
				return (mod184.getYear() < 2015 );				
			}
		},
	;

		abstract boolean accept(Mod184 mod184);
		abstract void fill(Mod184 mod184, Writer wr) throws IOException;
	}
	
	public static void fillWriter(Mod184 mod184, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod184)) {
				writer.fill(mod184, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
}
