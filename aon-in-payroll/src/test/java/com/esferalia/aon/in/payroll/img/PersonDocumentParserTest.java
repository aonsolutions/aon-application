package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import org.junit.Test;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class PersonDocumentParserTest {

	@Test
	public void personDocumentExtractersTests() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedDni = "45339825V";
			String actualDni = person.getDocument();
			assertEquals(expectedDni, actualDni);
		}
	}
}
