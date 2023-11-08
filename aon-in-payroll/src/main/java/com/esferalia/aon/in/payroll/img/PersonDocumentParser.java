package com.esferalia.aon.in.payroll.img;

import com.esferalia.aon.in.payroll.img.PersonDocumentExtracters.IPersonDocumentExtracter;
import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParser {
		
	private PersonDocumentParser() {
	}


	public static Person parse(byte[] bytes) {
		PersonDocumentParserValidation.validateBytes(bytes);
		IPersonDocumentExtracter extracter = PersonDocumentExtracters.getExtracter(bytes);
		String text = extracter.extract(bytes);
	    return PersonDocumentParsers.parse( text ); 				
	}
	
}
