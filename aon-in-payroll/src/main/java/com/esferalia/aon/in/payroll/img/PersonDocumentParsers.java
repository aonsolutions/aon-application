package com.esferalia.aon.in.payroll.img;

import java.util.Arrays;

import com.esferalia.aon.in.payroll.img.PersonDocumentExtracters.IPersonDocumentParser;
import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParsers {

	public static Person parse(String text) {
		IPersonDocumentParser [] parses = new IPersonDocumentParser[] {
				new DNICommonParser(),
				new DNIVascoParser()
		};
		return Arrays.stream(parses)
				.filter(e ->e.accept(text))
				.map(e -> e.parse(text))
				.findFirst()
				.orElseThrow(() -> new PersonDocumentParserException("El formato no es soportado"));
	}
}
