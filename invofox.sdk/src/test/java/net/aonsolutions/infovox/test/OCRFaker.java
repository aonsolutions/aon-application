package net.aonsolutions.infovox.test;

import java.util.Locale;

import com.github.javafaker.Faker;

import net.aonsolutions.infovox.model.OCRCompany;

public class OCRFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static OCRCompany getCompany() {
		return  new OCRCompany()
			.setName( faker.company().name() )
			.setCountryCode("ES")
			.setTaxId(faker.regexify(documentRegexp))
			.setAccountingPeriodLength(0);
	}

}

