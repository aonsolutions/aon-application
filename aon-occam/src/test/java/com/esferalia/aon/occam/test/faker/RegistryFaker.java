package com.esferalia.aon.occam.test.faker;

import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.github.javafaker.Faker;

public class RegistryFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static Registry get( AONContext ctx ) {
		Registry registry = new Registry();
		registry.setDomain(new Domain().setId(ctx.getDomainId()));
		registry.setDocument(faker.regexify(documentRegexp));
		registry.setDocumentType( AonRandom.randomEnum(DocumentType.class) );
		registry.setDocumentCountry( AonRandom.b(95) ? Country.ES: AonRandom.randomEnum(Country.class));
		registry.setName( faker.company().name() );
		registry.setAlias( faker.company().profession() );
		registry.setNationality( AonRandom.b(95) ? Country.ES: AonRandom.randomEnum(Country.class));
		registry.setConfidential( !AonRandom.b(98) );
		return registry;
	}
}

