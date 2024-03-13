package com.esferalia.aon.in.payroll.pdf.mod.Navarra;

import java.util.Arrays;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public class ModelDocumentParsers {
	
	static String modelSearch(String text) {
		ParserUtils pu = new ParserUtils();
		return pu.modelSearch(text);
	}

	interface IModelDocumentParser{
		boolean accept (String text);
		FiscalModel parse(String text);
	}
	
	static FiscalModel parse (String text) {
		
		String modelSearched = modelSearch(text);
		
		switch (modelSearched) {
		case "130":
			IModelDocumentParser [] parses = new IModelDocumentParser[] {
					new Mod1302023Navarra()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		case "715":
					parses = new IModelDocumentParser[] {
					new Mod7152023Navarra()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
			
		case "F69":
			parses = new IModelDocumentParser[] {
					new ModF692023Navarra()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		default:
			break;
		}
		
		IModelDocumentParser [] parses = new IModelDocumentParser[] {
				new Mod1302023Navarra(),
				new Mod7152023Navarra(),
				new ModF692023Navarra()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
