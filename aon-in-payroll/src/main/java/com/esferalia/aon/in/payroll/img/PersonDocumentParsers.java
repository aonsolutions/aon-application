package com.esferalia.aon.in.payroll.img;

import java.util.Arrays;

import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParsers {

	public interface IPersonDocumentParser {
		boolean accept (String text);
		Person parse(String text);
	}

	public static IPersonDocumentParser getParser(String text) {
		IPersonDocumentParser [] parses = new IPersonDocumentParser[] {
				new DNICommonParser()
		};
		return Arrays.stream(parses)
				.filter(e ->e.accept(text))
				.findFirst()
				.orElseThrow(() -> new PersonDocumentParserException("El formato no es soportado"));
	}
}
