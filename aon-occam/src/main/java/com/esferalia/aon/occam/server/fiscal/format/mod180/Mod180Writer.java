package com.esferalia.aon.occam.server.fiscal.format.mod180;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod180Writer {

	@FunctionalInterface
	interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod180 mod180, Mod180Detail detail) throws IOException;
	}
	private enum Writers {
		ALL_2014 {

			@Override
			void fill(Mod180 mod180, Writer wr) throws IOException {
				Mod180File2014.fill(mod180, wr);
			}
			
			@Override
			boolean accept(Mod180 mod180) {
				return true;				
			}
		},
		;

		abstract boolean accept(Mod180 mod180);
		abstract void fill(Mod180 mod180, Writer wr) throws IOException;
	}
	
	public static void fillWriter(Mod180 mod180, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod180)) {
				writer.fill(mod180, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

}
