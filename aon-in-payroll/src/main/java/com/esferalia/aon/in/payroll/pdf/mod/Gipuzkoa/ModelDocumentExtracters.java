package com.esferalia.aon.in.payroll.pdf.mod.Gipuzkoa;

import java.util.Arrays;

public class ModelDocumentExtracters {

	interface IModelDocumentExtracter{
		boolean accept (byte [] bytes);
		String extract(byte [] bytes);
	}
	
	static IModelDocumentExtracter getExtracter(byte[] bytes) {
		return Arrays.stream(new IModelDocumentExtracter[] {new PDFExtracter(),new ImageExtracter()})
			.filter(e -> e.accept(bytes))
			.findFirst()
			.orElseThrow(() -> new ModelDocumentParseException("El formato no es soportado"));
	}
}
