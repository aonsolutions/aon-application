package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class PersonDocumentExtracters {

	public interface IPersonDocumentExtracter {
		boolean accept (InputStream is);
		String extract(byte[] bytes);
	}

	public static IPersonDocumentExtracter getExtracter(byte[] bytes) {
		try ( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
			return Arrays.stream(new IPersonDocumentExtracter[] {
					new PDFExtracter(),
					new ImageExtracter()})
					.filter(e -> e.accept(is))
					.findFirst()
					.orElseThrow(() -> new PersonDocumentExtractException("El formato no es soportado"));
		} catch (IOException e1) {
			throw new PersonDocumentExtractException("Error input");
		}
	}
	
}
