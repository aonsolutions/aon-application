package com.esferalia.aon.in.payroll.pdf.modAlava;

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
		case "115":
			IModelDocumentParser [] parses = new IModelDocumentParser[] {
					new Mod1152023Alava()
			};
			
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
			
		case "303":
					parses = new IModelDocumentParser[] {
					new Mod3002023Alava()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		case "349":
			parses = new IModelDocumentParser[] {
					new Mod3492023Alava()
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
				new Mod1152023Alava(),
				new Mod3002023Alava(),
				new Mod3492023Alava()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
