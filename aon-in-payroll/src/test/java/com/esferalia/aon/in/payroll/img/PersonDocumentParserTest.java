package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@RunWith(Parameterized.class)
public class PersonDocumentParserTest {
	
	private String fileName;
	
	@Parameterized.Parameters
	public static Collection<Object> fileNames() {
		return Arrays.asList(new Object[] {
			"dniJuanmaTorcido.pdf",
			"dniJuanmaTorcido.jpg",
			"dniJuanmaTorcido.png"
		});
	}
	
	public PersonDocumentParserTest(String fileName) {
		this.fileName = fileName;
	}
	
	@Test
	public void personDocumentParserParameterizedTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/"+fileName;
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedSecondSurname = "ALVAREZ";
			String actualSecondSurname = person.getSecondSurname();
			assertEquals(expectedSecondSurname, actualSecondSurname);
		}
	}
	
	@Test
	public void personDocumentParserJPGTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/GenericDNI.jpg";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedFirstSurname = "MUESTRA";
			String actualFirstSurname = person.getFirstSurname();
			assertEquals(expectedFirstSurname, actualFirstSurname);
		}
	}

	@Test
	public void personDocumentParserBlurredTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/BlurredDNI.pdf";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
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
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedName = "JORDI";
			String actualName = person.getName();
			assertEquals(expectedName, actualName);
		}
	}

	@Test
	public void personDocumentParserOtherFormat() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/DNI-EUKE-1.jpg";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedSecondSurname = "CASTELLANO";
			String actualSecondSurname = person.getSecondSurname();
			assertEquals(expectedSecondSurname, actualSecondSurname);
		}
	}

	@Test
	public void personDocumentParserJPEGTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/GenericDNI.jpeg";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			Country expectedNationality = Country.ES;
			Country actualNationality = person.getNationality();
			assertEquals(expectedNationality, actualNationality);
		}
	}

	@Test
	public void personDocumentParserPNGTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/GenericDNI.png";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			String expectedName = "CARMEN";
			String actualName = person.getName();
			assertEquals(expectedName, actualName);
		}
	}

	@Test
	public void personDocumentParserPNGInvertTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaInvert.png";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = AonIOUtils.toByteArray(is);
			Person person = PersonDocumentParser.parse(bytes);
			Country expectedNationality = Country.ES;
			Country actualNationality = person.getNationality();
			assertEquals(expectedNationality, actualNationality);
		}
	}

	@Test
	public void personDocumentParserJPEGRotateTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaRotate.jpeg";
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
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
		ClassLoader classLoader = PersonDocumentParserTest.class.getClassLoader();
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
		byte [] bytes = null;
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> PersonDocumentParser.parse(bytes));		
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

}
