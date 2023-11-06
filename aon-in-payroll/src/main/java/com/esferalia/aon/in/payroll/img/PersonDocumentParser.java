package com.esferalia.aon.in.payroll.img;

import java.io.IOException;
import java.io.InputStream;

import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParser {

	public static Person parse(InputStream is) throws IOException {
		String text = extract(is);
	    return PersonDocumentParsers.parse( text ); 				
	}
	
	private static String extract(InputStream is) {
		return PersonDocumentExtracters.extract(is);
	}
	
	public interface IPersonDocumentExtracter{
		boolean accept(InputStream is);
		String extract(InputStream is);
	}
}
