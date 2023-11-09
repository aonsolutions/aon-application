package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@RunWith(Suite.class)

@SuiteClasses({ PersonDocumentParserGlobalTest.PersonDocumentParserTest.class,
		PersonDocumentParserGlobalTest.PersonDocumentParserTest2.class,
		PersonDocumentParserGlobalTest.PersonDocumentParserTest3.class, })

public class PersonDocumentParserGlobalTest {

	public static class PersonDocumentParserTest {

		@Test
		public void personDocumentParserJPGTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/GenericDNI.jpg";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedDocument = "12345678A";
				String actualDocument = person.getDocument();
				assertEquals(expectedDocument, actualDocument);
			}
		}

		@Test
		public void personDocumentParserBlurredTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/BlurredDNI.pdf";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedDni = "65004204V";
				String actualDni = person.getDocument();
				assertEquals(expectedDni, actualDni);
			}
		}

		@Test
		public void personDocumentParserPDFOldFormatTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/DNIJordi.pdf";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedName = "JORDI";
				String actualName = person.getName();
				assertEquals(expectedName, actualName);
			}
		}

		@Test
		public void personDocumentParserPNGTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/GenericDNI.png";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedName = "CARMEN";
				String actualName = person.getName();
				assertEquals(expectedName, actualName);
			}
		}

		@Test
		public void personDocumentParserJPEGRotateTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaRotate.jpeg";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedDocument = "45339825V";
				String actualDni = person.getDocument();
				assertEquals(expectedDocument, actualDni);
			}
		}

		@Test
		public void personDocumentParserOldFormatTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/oldFormatDNI.jpeg";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedDni = "99999999-R";
				String actualDni = person.getDocument();
				assertEquals(expectedDni, actualDni);
			}
		}

		@Test
		public void personDocumentParserNullInputTest() throws Exception {
			byte[] bytes = null;
			AonCoreException e = assertThrows(AonCoreException.class, () -> PersonDocumentParser.parse(bytes));
			assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
		}

	}

	@RunWith(Parameterized.class)
	public static class PersonDocumentParserTest2 {
		private String fileName;

		@Parameterized.Parameters
		public static Collection<Object> fileNames() {
			return Arrays.asList(new Object[] { "DNI-EUKE-1.jpg", "GenericDNI.jpeg", "dniJuanmaInvert.png" });
		}

		public PersonDocumentParserTest2(String fileName) {
			this.fileName = fileName;
		}

		@Test
		public void personDocumentParserOtherFormat() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/" + fileName;
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				Country expectedNation = Country.ES;
				Country actualNation = person.getNationality();
				assertEquals(expectedNation, actualNation);
			}
		}

	}

	@RunWith(Parameterized.class)
	public static class PersonDocumentParserTest3 {

		private String fileName;

		@Parameterized.Parameters
		public static Collection<Object> fileNames() {
			return Arrays
					.asList(new Object[] { "dniJuanmaTorcido.pdf", "dniJuanmaTorcido.jpg", "dniJuanmaTorcido.png" });
		}

		public PersonDocumentParserTest3(String fileName) {
			this.fileName = fileName;
		}

		@Test
		public void personDocumentParserParameterizedTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/pdf/" + fileName;
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				Person person = PersonDocumentParser.parse(bytes);
				String expectedName = "JUAN MANUEL";
				String actualName = person.getName();
				assertEquals(expectedName, actualName);
			}
		}

	}
}
