package com.esferalia.aon.occam.server.fiscal.format.mod193;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod193Writer {

	@FunctionalInterface
	interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod193 mod193, Mod193Detail detail) throws IOException;
	}
	private enum Writers {
		Mod193_File2016 {

			@Override
			void fill(Mod193 mod193, Writer wr) throws IOException {
				Mod193File2016.fill(mod193, wr);
			}
			
			@Override
			boolean accept(Mod193 mod193) {
				return mod193.getYear() >= 2016;				
			}
		},
		Mod193_File2014 {

			@Override
			void fill(Mod193 mod193, Writer wr) throws IOException {
				Mod193File2014.fill(mod193, wr);
			}
			
			@Override
			boolean accept(Mod193 mod193) {
				return mod193.getYear() < 2016;				
			}
		},
		;

		abstract boolean accept(Mod193 mod193);
		abstract void fill(Mod193 mod193, Writer wr) throws IOException;
	}

	public static void fillWriter(Mod193 mod193, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod193)) {
				writer.fill(mod193, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

}
