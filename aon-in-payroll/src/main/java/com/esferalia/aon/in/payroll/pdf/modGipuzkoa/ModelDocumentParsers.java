package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

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
		System.out.println(modelSearched);
		switch (modelSearched) {
		case "110":
			IModelDocumentParser [] parses = new IModelDocumentParser[] {
					new Mod1102023Gipuzkoa()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
			 
		case "115":
			 		parses = new IModelDocumentParser[] {
					new Mod1152023Gipuzkoa()
			};
			 return Arrays.stream(parses)
		                .filter(e ->e.accept(text))
		                .map(e -> e.parse(text))
		                .findFirst()
		                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
			 
		case "130":
	 		parses = new IModelDocumentParser[] {
			new Mod1302023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "180":
	 		parses = new IModelDocumentParser[] {
			new Mod1802023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "190":
	 		parses = new IModelDocumentParser[] {
			new Mod1902023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "200":
	 		parses = new IModelDocumentParser[] {
			new Mod2002023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "300":
	 		parses = new IModelDocumentParser[] {
			new Mod3002023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "349":
	 		parses = new IModelDocumentParser[] {
			new Mod3492023Gipuzkoa()
	};
	 return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	 
		case "390":
	 		parses = new IModelDocumentParser[] {
			new Mod3902023Gipuzkoa()
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
				new Mod1102023Gipuzkoa(),
				new Mod1152023Gipuzkoa(),
				new Mod1302023Gipuzkoa(),
				new Mod1802023Gipuzkoa(),
				new Mod1902023Gipuzkoa(),
				new Mod2002023Gipuzkoa(),
				new Mod3002023Gipuzkoa(),
				new Mod3492023Gipuzkoa(),
				new Mod3902023Gipuzkoa()
				
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
