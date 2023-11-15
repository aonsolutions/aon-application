package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.PersonDocument;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@RunWith(Suite.class)

@SuiteClasses({ PersonDocumentParserGlobalTest.PersonDocumentParserTest.class,
		PersonDocumentParserGlobalTest.PersonDocumentParserTest2.class,
		PersonDocumentParserGlobalTest.PersonDocumentParserTest3.class,
		PersonDocumentParserGlobalTest.PersonDocumentParserTest4.class})

public class PersonDocumentParserGlobalTest {

	public static class PersonDocumentParserTest {

		@Test
		public void personDocumentParserJPEGRotateTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/image/dniJuanmaRotate.jpeg";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument  person = PersonDocumentParser.parse(bytes);
				String expectedDocument = "45339825V";
				String actualDni = person.getDocument();
				assertEquals(expectedDocument, actualDni);
			}
		}
		
		@Test
		public void personDocumentParserBirthdateTest() throws Exception{
			String file = "com/esferalia/aon/in/payroll/image/dniJuanma.png";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try(InputStream is = classLoader.getResourceAsStream(file)){
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument  person = PersonDocumentParser.parse(bytes);
				String expectedDate = "06 02 1997";
				SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
				Date expectedBirthDate = new Date();
				try {
					expectedBirthDate= formatter.parse(expectedDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date actualDate = person.getBirthDate();
		     
		        assertEquals(expectedBirthDate, actualDate);
			}
		}
		
		@Test
		public void personDocumentParserIssueDateTest() throws Exception{
			String file = "com/esferalia/aon/in/payroll/image/dniJuanma.png";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try(InputStream is = classLoader.getResourceAsStream(file)){
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument person = PersonDocumentParser.parse(bytes);
				String expectedDate = "27 07 2022";
				SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
				Date expectedIssueDate = new Date();
				try {
					expectedIssueDate= formatter.parse(expectedDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date actualIssueDate = person.getIssueDate();
				assertEquals(expectedIssueDate, actualIssueDate);
			}
		}
		
		@Test
		public void personDocumentParserValidityDateTest() throws Exception{
			String file = "com/esferalia/aon/in/payroll/image/dniJuanma.png";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try(InputStream is = classLoader.getResourceAsStream(file)){
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument person = PersonDocumentParser.parse(bytes);
				String expectedDate = "27 07 2027";
				SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
				Date expectedValidityDate = new Date();
				try {
					expectedValidityDate = formatter.parse(expectedDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date actualValidityDate = person.getValidity();
				assertEquals(expectedValidityDate, actualValidityDate);
			}
		}
		
		@Test
		public void personDocumentParserOtherFormat() throws Exception {
			String file = "com/esferalia/aon/in/payroll/image/dniJuanmaInvert.png";
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument  person = PersonDocumentParser.parse(bytes);
				Country expectedNation = Country.ES;
				Country actualNation = person.getNationality();
				assertEquals(expectedNation, actualNation);
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
			return Arrays.asList(new Object[] { "DNI-EUKE-1.jpg", "GenericDNI.jpeg" });
		}

		public PersonDocumentParserTest2(String fileName) {
			this.fileName = fileName;
		}

		@Test
		public void personDocumentParserOtherFormat() throws Exception {
			String file = "com/esferalia/aon/in/payroll/image/" + fileName;
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				AonCoreException e = assertThrows(AonCoreException.class, () ->PersonDocumentParser.parse(bytes));
				assertEquals (AonError.GROUP_NOT_MATCH.getMessage(), e.getMessage());
			}
		}

	}

	@RunWith(Parameterized.class)
	public static class PersonDocumentParserTest3 {

		private String fileName;

		@Parameterized.Parameters
		public static Collection<Object> fileNames() {
			return Arrays
					.asList(new Object[] { "pdf/dniJuanmaTorcido.pdf", "image/dniJuanmaTorcido.jpg", "image/dniJuanmaTorcido.png" });
		}

		public PersonDocumentParserTest3(String fileName) {
			this.fileName = fileName;
		}

		@Test
		public void personDocumentParserParameterizedTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/" + fileName;
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				PersonDocument  person = PersonDocumentParser.parse(bytes);
				String expectedName = "JUAN MANUEL";
				String actualName = person.getName();
				assertEquals(expectedName, actualName);
				
			}
		}

	}
	
	@RunWith(Parameterized.class)
	public static class PersonDocumentParserTest4{
		private String fileName;

		@Parameterized.Parameters
		public static Collection<Object> fileNames() {
			return Arrays
					.asList(new Object[] { "image/GenericDNI.jpg", "pdf/BlurredDNI.pdf", "pdf/DNIJordi.pdf", "image/GenericDNI.png", "image/oldFormatDNI.jpeg" });
		}
		
		public PersonDocumentParserTest4(String fileName) {
			this.fileName = fileName;
		}
		
		
		@Test
		public void personDocumentParserJPGTest() throws Exception {
			String file = "com/esferalia/aon/in/payroll/"+fileName;
			ClassLoader classLoader = PersonDocumentParserGlobalTest.class.getClassLoader();
			try (InputStream is = classLoader.getResourceAsStream(file)) {
				byte[] bytes = AonIOUtils.toByteArray(is);
				AonCoreException e = assertThrows(AonCoreException.class, () ->PersonDocumentParser.parse(bytes));
				assertEquals(AonError.GROUP_NOT_MATCH.getMessage(), e.getMessage());
			}
		}
	}
}
