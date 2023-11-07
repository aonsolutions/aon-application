package com.esferalia.aon.in.payroll.img;

import java.io.IOException;

import com.esferalia.aon.in.payroll.img.PersonDocumentExtracters.IPersonDocumentExtracter;
import com.esferalia.aon.in.payroll.img.PersonDocumentParsers.IPersonDocumentParser;
import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParser {

	public static Person parse(byte[] bytes) throws IOException {
		IPersonDocumentExtracter extracter = PersonDocumentExtracters.getExtracter(bytes);
		String text = extracter.extract(bytes);
		IPersonDocumentParser parser = PersonDocumentParsers.getParser(text);
	    return parser.parse( text ); 				
	}
	
}
