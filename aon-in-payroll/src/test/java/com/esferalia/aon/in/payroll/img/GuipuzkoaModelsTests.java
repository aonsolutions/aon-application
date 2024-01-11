package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ParserUtils;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class GuipuzkoaModelsTests {

	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();

	@Test
	public void mod110GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/4-110_3T_2021_ROBERTO_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "15387202H";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "DE BLAS BOAL ROBERTO";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			int expectedYear = 2021;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			Double expectedAmount = 6.79;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);

//			String period = mod110.setPeriod(text);
//			assertEquals("3 Trimestre", period);
//			String issueDate = mod110.setIssueDate(text);
//			assertEquals("25/10/2021", issueDate);
//			System.out.println(issueDate);
		}
	}

	@Test
	public void mod115GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/MOD_115_3T_2023_GOENA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B20098919";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "GOENA SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			Double expectedAmount = 741.0007;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			
		

//		String hacienda = mod115.setHacienda(text);
//		assertEquals("Diputacion foral de Gipuzkoa", hacienda);
//		String presentationDate = mod115.setPresentationDate(text);
//		assertEquals("13/10/2023", presentationDate);
//		String period = mod115.setPeriod(text);
//		assertEquals("3er Trimestre", period);
		}
	}

//
	@Test
	public void mod130GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/1-MOD_130_ROBERTO_4T_2022_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "15387202H";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "DE BLAS BOAL ROBERTO";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			Double expectedAmount = 54.71;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

			int expectedYear = 2022;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T4, actualPeriod);

//		String principalActivity = mod130.setPrincipalActivity(text);
//		assertEquals("VENTA MENOR LABORES DEL TABACO EN EXPEND", principalActivity);
//		String hacienda = mod130.setHacienda(text);
//		assertEquals("gipuzkoa", hacienda);
//		String model = mod130.setModel(text);
//		assertEquals("130", model);
//		String exercise = mod130.setExercise(text);
//		assertEquals("4º Trimestre", exercise);
		}
	}

	@Test
	public void mod180GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/2-MOD_180_ROBERTO_2022_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "15387202H";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "DE BLAS BOAL ROBERTO";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedPhone = "691568973";
			String actualPhone = fiscalModel.getContactPhone();
			assertEquals(expectedPhone, actualPhone);

			int expectedYear = 2022;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			Double expectedAmount = 570.00;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

//		String perceivers = mod180.setPerceivers(text);
//		assertEquals("1", perceivers);
//		String amountsPaid = mod180.setAmountsPaid(text);
//		assertEquals("3.000,00", amountsPaid);
		}
	}

	@Test
	public void mod200GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/1-MOD_200_FIDCOMMERCE_2022_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B75121103";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);
			
			String expectedName = "FIDCOMMERCE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			
			String expectedEmail = "JESUSM.GARCIA@AYUDATPYMES.ES";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);
			
			String expectedPhoneNumber = "691568973";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);
	
		
//		String relationPersonNif = mod200.setRelationPersonNif(text);
//		assertEquals("B93011708", relationPersonNif);
//		String relationPersonName = mod200.setRelationPersonName(text);
//		assertEquals("JESUS MUÑOZ", relationPersonName);
//		String legalRepresentator = mod200.setLegalRepresntators(text);
//		assertEquals("NOTARIA : JAVIER OÑATE CUADROS", legalRepresentator);
		}
	}
//
	@Test
	public void mod300GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/6-300_3T_2021_ROBERTO_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "15387202H";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "DE BLAS BOAL ROBERTO";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			
			int expectedYear = 2021;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			
			Double expectedAmount = 199.39;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);

		
//		String period = mod300.setPeriod(text);
//		assertEquals("3 Trimestre", period);
//		String issueDate = mod300.setIssueDate(text);
//		assertEquals("25/10/2021", issueDate);
		}
	}

	@Test
	public void mod349GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/2-MOD_349_FIDCOMMERCE_2T_2023_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B75121103";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "FIDCOMMERCE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
	
			int expectedYear = 23;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			//2.424,29
			Double expectedAmount = 2424.29;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
	
//		String period = mod349.setPeriod(text);
//		assertEquals("2T", period);

//		List<String> lista = mod349.operationsLines(text);
//		System.out.println(lista);
		}
	}

	@Test
	public void mod390GipuzkoaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/4-MOD_390_FIDCOMMERCE_2022_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B75121103";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);
			
			String expectedName = "FIDCOMMERCE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			//4.685,07
			Double expectedAmount = 4685.07;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

//		String issueDate = mod390.setIssueDate(text);
//		assertEquals("31/01/2023", issueDate);
//		String hacienda = mod390.setHacienda(text);
//		assertEquals("Diputacion Foral de Gipuzkoa", hacienda);
	
		}
	}

	@Test
	public void mod190Gipuzkoatest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/3-MOD_190_FIDCOMMERCE_2022_GIPUZKOA.pdf";
		ClassLoader classLoader = GuipuzkoaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B75121103";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);
		
			String expectedName = "FIDCOMMERCE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
		
			String expectedEmail = "JESUSM.GARCIA@AYUDATPYMES.ES";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);
			
			String expectedPhoneNumber = "691568973";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);
			//5.840,33
			Double expectedAmount = 5840.33;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			int expectedYear = 2022;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
		
//		String issueDate = mod190.setIssueDate(text);
//		assertEquals("30/01/2023", issueDate);
		}
	}

}
