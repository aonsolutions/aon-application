package com.esferalia.aon.in.payroll.pdf.modBizkaia;

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
		case "110":
					IModelDocumentParser [] parses = new IModelDocumentParser[] {
					new Mod1102023Bizkaia()		
				};
				 return Arrays.stream(parses)
			                .filter(e ->e.accept(text))
			                .map(e -> e.parse(text))
			                .findFirst()
			                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		case "115":
					parses = new IModelDocumentParser[] {			
					new Mod1152023Bizkaia()
				};
				 return Arrays.stream(parses)
			                .filter(e ->e.accept(text))
			                .map(e -> e.parse(text))
			                .findFirst()
			                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		case "180":
			parses = new IModelDocumentParser[] {			
			new Mod1802023Bizkaia()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		
		case "190":
			parses = new IModelDocumentParser[] {			
			new Mod1902023Bizkaia()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		 
		case "200":
			parses = new IModelDocumentParser[] {			
			new Mod2002023Bizkaia()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		
		case "303":
			parses = new IModelDocumentParser[] {			
			new Mod3032023Bizkaia()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		
		case "349":
			parses = new IModelDocumentParser[] {			
			new Mod3492023Bizkaia()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
		
		case "390":
			parses = new IModelDocumentParser[] {			
			new Mod3902023Bizkaia()
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
			new Mod1102023Bizkaia(),
			new Mod1152023Bizkaia(),
			new Mod1802023Bizkaia(),
			new Mod1902023Bizkaia(),
			new Mod2002023Bizkaia(),
			new Mod3032023Bizkaia(),
			new Mod3492023Bizkaia(),
			new Mod3902023Bizkaia()			
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
