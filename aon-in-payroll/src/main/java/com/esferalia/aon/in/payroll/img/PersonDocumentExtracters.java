package com.esferalia.aon.in.payroll.img;

import java.util.Arrays;


 class PersonDocumentExtracters {

	interface IPersonDocumentExtracter{
		boolean accept (byte [] bytes);
		String extract(byte [] bytes);
	}
	
	static IPersonDocumentExtracter getExtracter(byte[] bytes) {
		return Arrays.stream(new IPersonDocumentExtracter[] {new PDFExtracter(),new ImageExtracter()})
			.filter(e -> e.accept(bytes))
			.findFirst()
			.orElseThrow(() -> new PersonDocumentParserException("El formato no es soportado"));
	}
}
 