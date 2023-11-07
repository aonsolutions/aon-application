package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import org.junit.Test;
import com.esferalia.aon.occam.api.model.Person;

public class PersonDocumentParserTest {

	@Test
	public void personDocumentExtractersTests() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file); 
		String text = PersonDocumentExtracters.extract(is);
		Person person = PersonDocumentParsers.parse(text);
		String expectedDni = "45339825V";
		String actualDni = person.getDocument();
		assertEquals(expectedDni, actualDni);
	}
}
