package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.InputStream;

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
//	@Test
//	public void getDocumentDNIRegexTest() throws Exception {
//		InputStream is = new FileInputStream("/tmp/dniJuanmaPDF.pdf");
//		dniParser.parse(is); 
//		String text = dniParser.getText();
//		String dni = dniParser.getDocumentDNI(text);
//		System.out.println(dni + "dni");
//	}
//	
//	@Test
//	public void getDocumentNationalityRegexTest() throws Exception {
//		InputStream is = new FileInputStream("/tmp/dniJuanmaPDF.pdf");
//		dniParser.parse(is); 
//		String text = dniParser.getText();
//		System.out.println(text);
//		Country nationality = dniParser.getNationality(text);
//		System.out.println(nationality + " nacionalidad");
//	}
//	
//	@Test 
//	public void getDocumentNameRegexTest() throws Exception{
//		InputStream is = new FileInputStream("/tmp/dniJuanmaPDF.pdf");
//		dniParser.parse(is); 
//		String text = dniParser.getText();
//		String name = dniParser.getDocumentName(text);
//		
//		System.out.println(name+ "nombre ");
//	}
//	
//	@Test
//	public void getDocumentSurnamesRegexTest() throws Exception{
//		InputStream is = new FileInputStream("/tmp/dniJuanmaPDF.pdf");
//		dniParser.parse(is); 
//		String text = dniParser.getText();
//		String[] surname = dniParser.getDocumentSurnames(text);
//		String primerApellido = surname[0];
//		String segundoApellido = surname[1];
//
//		System.out.println(primerApellido +""+ segundoApellido + "apellidos");
//	}
//	
//	
//	@Test
//	public void getDocumentData() throws Exception{
//		Person person = new Person();
//		InputStream is = new FileInputStream("/tmp/dniJuanmaPDF.pdf");
//		dniParser.parse(is); 
//		String text = dniParser.getText();
//		person = dniParser.getDniDataPerson(text);
//		
//		System.out.println(person.getDocument());
//		System.out.println(person.getName());
//		System.out.println(person.getFirstSurname());
//		System.out.println(person.getSecondSurname());
//		System.out.println(person.getNationality());
//	}
 
	@Test
	public void extractImageNullTest() {
		byte[] bytes = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extractImages(bytes));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void parseNullTest() {
		InputStream is = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.parsePublic(is));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
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
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extractImages(bytes));
		assertEquals(AonError.FILE_SIZE_EXCEEDED.getMessage(), e.getMessage());
	}

	@Test 
	public void getDNIDataJpeg() throws Exception{
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI4.jpeg";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		String text = dnip.extractImages(bytes);
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
		String expectedName = "CARMEN";
		String actualName = persona.getName();
		assertEquals(expectedName, actualName);
	}
	
	
	@Test
	public void getDNIDataPdf() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/DNIJordi.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parsePublic(is);
		String text = dnip.getText();
		String expectedName = "JORDI";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		System.out.println(text);
		String expectedDNi = "12345678A";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		String expectedDNi = "12345678A";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
		String actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}

	@Test
	public void getDNIDataPdfRotate() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/DNIJordiRotate.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parsePublic(is);
		String text = dnip.getText();
		String expectedName = "JORDI";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
		String actualName = persona.getName();
		assertEquals(expectedName, actualName);
	}

	@Test
	public void getDNIDataLowRes() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/pruebaDNI.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parsePublic(is);
		String text = dnip.getText();
		System.out.println(text);
		String expectedDNi = "65004204V";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		String expectedDNi = "45339825V";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		String expectedName = "JUAN MANUEL";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		String expectedSecondSurname = "ALVAREZ";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
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
		String text = dnip.extractImages(bytes);
		String expectedFirstSurname = "ORTEGA";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
		String actualFirstSurname = persona.getFirstSurname();
		assertEquals(expectedFirstSurname, actualFirstSurname);
	}

	@Test
	public void getDNIDataPdfTorcido() throws Exception {
		DNIParser dnip = new DNIParser();
		String file = "com/esferalia/aon/in/payroll/pdf/dniJuanmaTorcido.pdf";
		ClassLoader classLoader = DNIParserTest.class.getClassLoader();
		InputStream is = classLoader.getResourceAsStream(file);
		dnip.parsePublic(is);
		String text = dnip.getText();
		String expectedDNi = "45339825V";
		Person persona = new Person();
		persona = dnip.getDniPerson(text);
		Object actualDni = persona.getDocument();
		assertEquals(expectedDNi, actualDni);
	}
	
	
}
