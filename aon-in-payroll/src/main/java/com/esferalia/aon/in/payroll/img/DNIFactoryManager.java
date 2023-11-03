package com.esferalia.aon.in.payroll.img;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.Person;

public class DNIFactoryManager {
	
	private List<Factory> factories;

	public DNIFactoryManager() {
		factories = new ArrayList<>();
		factories.add(new DNIImageFactory());
		factories.add(new DNIPDFFactory());

	}

	public Factory getFactory(String text) {
		for (Factory factory : factories) {
			if (factory.accept(text)) {
				return factory;
			}
		}
		return null;
	}

	public Person parse(String text) {
		Factory factory = getFactory(text);
		if (factory != null) {
			return factory.getPerson(text);
		}
		return null;
	}
}

interface Factory {
	boolean accept(String text);

	Person getPerson(String text);
}

class DNIImageFactory implements Factory {
	@Override
	public boolean accept(String text) {
		DNIParserValidation.validateText(text);
		Pattern pattern = Pattern.compile("DNI[\\s\\S]*?(NOMBRE|NONBRE)[\\s\\S]+(APELLIDOS|APALLIDOS)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	@Override
	public Person getPerson(String text) {
		DNIParser dniParser = new DNIParser();
		
		return dniParser.getDniPerson(text);
	}
}

class DNIPDFFactory implements Factory {
	@Override
	public boolean accept(String text) {
		DNIParserValidation.validateText(text);
		Pattern pattern = Pattern.compile("DNI[\\s\\S]*?(NOMBRE|NONBRE)[\\s\\S]+(APELLIDOS|APALLIDOS)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	@Override
	public Person getPerson(String text) {
		DNIParser dniParser = new DNIParser();
		return dniParser.getDniPerson(text);
	}

}
