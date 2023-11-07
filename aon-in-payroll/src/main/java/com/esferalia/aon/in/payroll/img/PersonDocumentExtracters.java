package com.esferalia.aon.in.payroll.img;

import java.io.InputStream;
import java.util.Arrays;

import com.esferalia.aon.in.payroll.img.PersonDocumentParser.IPersonDocumentExtracter;
import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentExtracters {

	public static String extract(InputStream is) {
		IPersonDocumentExtracter [] extracters = new IPersonDocumentExtracter[] {
				new PDFExtracter(),
				new ImageExtracter() 
		};
		
		return Arrays.stream(extracters)
				.filter(e -> e.accept(is))
				.map(e -> e.extract(is))
				.findFirst()
				.orElseThrow(() -> new PersonDocumentExtractException("El formato no es soportado"));
	}
	
	public interface IPersonDocumentParser{
		boolean accept (String text);
		Person parse(String text);
	}
}
