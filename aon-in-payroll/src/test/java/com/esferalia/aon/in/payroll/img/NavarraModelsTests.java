package com.esferalia.aon.in.payroll.img;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modNavarra.ParserUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.in.payroll.pdf.modNavarra.ModelDocumentParser;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

public class NavarraModelsTests {
	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();

	@Test
	public void mod130NavarraTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/1-MODELO_130_3T_2023_OLIVER_NAVARRA.pdf";
		ClassLoader classLoader = NavarraModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "78773433C";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedEmail = "OLIVERARBIOL1994@GMAIL.COM";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String expectedPhoneNumber = "654825913";
			String actualPhoneNumber = fiscalModel.getPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			Double expectedAmount = 0.00;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);
			

//		String periodAndYear = mod130.setPeriodAndYear(text);
//		assertEquals("2023 T3" , periodAndYear);
//		String hacienda = mod130.setHacienda(text);
//		assertEquals("Hacienda Navarra" , hacienda);	
		}
	}

	@Test
	public void mod715NavarraTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/MOD_715_3T_2023_BLAS_OLIVA.pdf";
		ClassLoader classLoader = NavarraModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedName = "BLAS OLIVA SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedNif = "B71001309";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			int expectedExercise = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedExercise, actualYear);

			String expectedPhoneNumber = "691570158";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);

			String expectedEmail = "BEATRIZGONZALEZ@AYUDATPYMES.COM";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			Double expectedAmount = 318.84;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

			String expectedIban = "ES52 3008 0192 57 3490616020";
			String actualIban = fiscalModel.getIban();
			assertEquals(expectedIban, actualIban);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);

//		String registryNumber = mod715.setRegistryNumber(text);
//		assertEquals("92677" , registryNumber);
//		String periodAndYear = mod715.setPeriodAndYear(text);
//		assertEquals("T3 2023" , periodAndYear);
//		String issueDate = mod715.setIssueDate(text);
//		assertEquals("18/10/2023", issueDate);
//		String sign = mod715.setSign(text);
//		assertEquals("2194ECF68A8E936C13C84F5BF8B122A7" , sign);
//		String csv = mod715.setCsv(text);
//		assertEquals("A0098468DEEE3F41" , csv);
//		String hacienda = mod715.setHacienda(text);
//		assertEquals("Hacienda Navarra" , hacienda);	
		}
	}

	@Test
	public void modF69NavarraTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/2-MODELO_F69_3T_2023_OLIVER_NAVARRA.pdf";
		ClassLoader classLoader = NavarraModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedName = "C ARBIOL, LUCIO, OLIVER";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedNif = "78773433C";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedEmail = "OLIVERARBIOL1994@GMAIL.COM";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String expectedPhone = "654825913";
			String actualPhone = fiscalModel.getContactPhone();
			assertEquals(expectedPhone, actualPhone);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			Double expectedAmount = -34.62;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);

//		String hacienda = modF69.setHacienda(text);
//		assertEquals("Hacienda Navarra", hacienda);
//		String model = modF69.setModel(text);
//		assertEquals("F69", model);
		}
	}

}
