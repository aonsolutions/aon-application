package com.esferalia.aon.in.payroll.pdf.modAeat;

import java.util.Arrays;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;

public class ModelDocumentParsers {

	interface IModelDocumentParser{
		boolean accept (String text);
		FiscalModel parse(String text);
	}
	
	static FiscalModel parse (String text) {
		IModelDocumentParser [] parses = new IModelDocumentParser[] {
				new Mod1152023AEAT()
		};
		 return Arrays.stream(parses)
	                .filter(e ->e.accept(text))
	                .map(e -> e.parse(text))
	                .findFirst()
	                .orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
