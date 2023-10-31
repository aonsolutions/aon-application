package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.occam.api.model.type.Country;


public class DNIParserTest {
	DNIParser dniParser = new DNIParser();

	@Test
	public void extractImageNullTest() {
		byte[] bytes = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extractImage(bytes));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void parseNullTest() {
		InputStream is = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.parse(is));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void parseNullListTest() {
		List<InputStream> inputStreams = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.parse(inputStreams));
		assertEquals(AonError.NULL_FILES_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void getNullImagesTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> DNIParser.getImages(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void parserNullTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.parser(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void extractNullTest() {
		Document document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extract(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void extractSizeExceededTest() throws Exception {
		String filePath = "com/esferalia/aon/in/payroll/pdf/SegInf_63-end.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(filePath);
		byte[] bytes = IOUtils.toByteArray(is);
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extractImage(bytes));
		assertEquals(AonError.FILE_SIZE_EXCEEDED.getMessage(), e.getMessage());
	}

	@Test 
	public void getDNIDataJpeg() throws Exception{
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI4.jpeg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String expectedName = "CARMEN\r";
		String actualName = persona.getName();
		assertEquals(expectedName, actualName);
	}
	
	
	@Test
	public void getDNIDataPdf() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/DNIJordi.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parse(is);
		String text = dnip.getText();
		String expectedName = "JORDI\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualName = persona.getName(); 
		assertEquals(expectedName, actualName);
	}

	@Test
	public void getDNIDataJpg() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI4.jpg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		System.out.println(text);
		String expectedDNi = "12345678A\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}

	@Test
	public void getDNIDataPng() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI4.png";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		String expectedDNi = "12345678A\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);

		String actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}

	@Test
	public void getDNIDataPdfRotate() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/DNIJordiRotate.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parse(is);
		String text = dnip.getText();
		String expectedName = "JORDI\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualName = persona.getName();
		assertEquals(expectedName, actualName);
	}

	@Test
	public void getDNIDataLowRes() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parse(is);
		String text = dnip.getText();
		System.out.println(text);
		String expectedDNi = "65004204V\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}

	@Test
	public void getDNIDataJpgRotate() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaRotate.jpg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		String expectedDNi = "45339825V\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}

	@Test
	public void getDNIDataPngRotate() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaInvert.png";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		String expectedName = "JUAN MANUEL\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualName = persona.getName();
		assertEquals(expectedName, actualName);
	}
	
	@Test
	public void getDNIDataJpegRotate() throws Exception{
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaRotate.jpeg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		Country expectedNationality = Country.ES;
		Country actualNationality = persona.getNationality();
		assertEquals(expectedNationality, actualNationality);
	}

	@Test
	public void getDNIDataJpgTorcido() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.jpg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		String expectedSecondSurname = "ALVAREZ\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualSecondSurname = persona.getSecondSurname();
		assertEquals(expectedSecondSurname, actualSecondSurname);
	}

	@Test
	public void getDNIDataPngTorcido() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.png";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImage(bytes);
		String expectedFirstSurname = "ORTEGA\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		String actualFirstSurname = persona.getFirstSurname();
		assertEquals(expectedFirstSurname, actualFirstSurname);
	}

	@Test
	public void getDNIDataPdfTorcido() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parse(is);
		String text = dnip.getText();
		String expectedDNi = "45339825V\r";
		Person persona = new Person();
		persona = dnip.getDniDataPerson(text);
		Object actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}
	
	
}
