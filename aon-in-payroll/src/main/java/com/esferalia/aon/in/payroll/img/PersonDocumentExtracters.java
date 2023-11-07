package com.esferalia.aon.in.payroll.img;

import java.util.Arrays;

public class PersonDocumentExtracters {

	public interface IPersonDocumentExtracter {	
		boolean accept (byte[] is);
		String extract(byte[] bytes);
	}

	public static IPersonDocumentExtracter getExtracter(byte[] bytes) {
		return Arrays.stream(new IPersonDocumentExtracter[] {new PDFExtracter(),new ImageExtracter()})
			.filter(e -> e.accept(bytes))
			.findFirst()
			.orElseThrow(() -> new PersonDocumentExtractException("El formato no es soportado"));
	}
	
}
