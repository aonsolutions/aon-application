package com.esferalia.aon.in.payroll.img;

import java.util.Arrays;

import com.esferalia.aon.occam.api.model.PersonDocument;

class PersonDocumentParsers {
	
    interface IPersonDocumentParser {
        boolean accept (String text);
        PersonDocument parse(String text);
    }
    
    static PersonDocument parse(String text) {
    	PersonDocumentParserValidation.validateText(text);
        IPersonDocumentParser [] parses = new IPersonDocumentParser[] {
                new DNICommonParser()
        };
        return Arrays.stream(parses)
                .filter(e ->e.accept(text))
                .map(e -> e.parse(text))
                .findFirst()
                .orElseThrow(() -> new PersonDocumentParserException("El formato no es soportado"));
    }

	
}
