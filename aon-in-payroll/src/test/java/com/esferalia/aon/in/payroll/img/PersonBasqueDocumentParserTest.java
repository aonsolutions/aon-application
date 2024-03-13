package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.PersonDocument;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class PersonBasqueDocumentParserTest {

	@Test
	public void personBasqueDocumentTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/DNI-EUKE-1.pdf";
		ClassLoader classLoader = PersonBasqueDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			PersonDocument person = PersonDocumentParser.parse(bytes);

			String expectedDocument = "44671367";
			String actualDocument = person.getDocument();
			assertEquals(expectedDocument, actualDocument);

			String expectedName = "EUGENIO";
			String actualName = person.getName();
			assertEquals(expectedName, actualName);

			String expectedFirstSurname = "CASTELLANO";
			String actualFirstSurname = person.getFirstSurname();
			assertEquals(expectedFirstSurname, actualFirstSurname);

			String expectedSecondSurName = "HURTADO";
			String actualSecondSurname = person.getSecondSurname();
			assertEquals(expectedSecondSurName, actualSecondSurname);

			Country expectedNation = Country.ES;
			Country actualNation = person.getNationality();
			assertEquals(expectedNation, actualNation);

			SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
			Date expectedValidityDate = new Date();
			try {
				expectedValidityDate = formatter.parse("14 10 2024");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date actualValidityDate = person.getValidity();
			assertEquals(expectedValidityDate, actualValidityDate);

			Date expectedBirthDate = new Date();
			try {
				expectedBirthDate = formatter.parse("04 06 1974");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date actualDate = person.getBirthDate();
			assertEquals(expectedBirthDate, actualDate);

		}
	}

}
