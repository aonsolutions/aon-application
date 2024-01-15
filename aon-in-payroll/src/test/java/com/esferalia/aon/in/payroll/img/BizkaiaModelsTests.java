package com.esferalia.aon.in.payroll.img;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import com.esferalia.aon.in.payroll.pdf.mod.Bizkaia.ModelDocumentParser;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class BizkaiaModelsTests {
	PDFExtracter pdfExtracter = new PDFExtracter();

	@Test
	public void mod110BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/2-MOD_110_ELDORADO_2T_2023_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedEmail = "notificacionesforales@ayudatpymes.com";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String expectedPhoneNumber = "655044226";
			String actualPhoneNumber = fiscalModel.getPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			Double expectedAmount = 6.089;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T2, actualPeriod);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "VERA NUÑEZ MARIA SILVERIA";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);

//		String nif = mod110.setNif(text);
//		String declarant = mod110.setDeclarant(text);
//		String presenter = mod110.setPresenter(text);
//		String presenterNif = mod110.setPresenterNif(text);
//		String email = mod110.setEmail(text);
//		String phoneNumber = mod110.setPhoneNumber(text);
//		String amount = mod110.setAmount(text);
//		String yearAndperiod = mod110.setYearAndPeriod(text);
//		String model = mod110.setModel(text);
//
//		assertEquals("B95541520", nif);
//		assertEquals("ELDORADO AUTOMOTIVE SL", declarant);
//		assertEquals("VERA NUÑEZ MARIA SILVERIA", presenter);
//		assertEquals("50604707S", presenterNif);
//		assertEquals("notificacionesforales@ayudatpymes.com", email);
//		assertEquals("655044226", phoneNumber);
//		assertEquals("6.089,28", amount);
//		assertEquals("Año : 2023 Periodo : TRIM2", yearAndperiod);
//		assertEquals("110", model);
		}
	}

	@Test
	public void mod1152023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/MOD_115_2T_2023_LAETITIA_FERREIRO.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "X8657717B";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "FERREIRO LAETITIA VANESSA";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedEmail = "NOTIFICACIONESFORALES@AYUDATPYMES.ES";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			Double expectedAmount = 171.00;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T2, actualPeriod);

			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "VERA NUÑEZ MARIA SILVERIA";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);


		}
	}

	@Test
	public void mod1802023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/4-MOD_180_ELDORADO_2022_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			// 4.280,74
			Double expectedAmount = 4280.74;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);

			int expectedYear = 2022;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);

			String expectedPhoneNumber = "691568973";
			String actualPhoneNumber = fiscalModel.getPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);

			String expectedContactPerson = "MUÑOZ GARCIA JESUS";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);
			
			
			
//		String issueDate = mod180.setIssueDate(text);
//		assertEquals("30 DE ENERO DE 2023",issueDate);
//		String declarantName = mod180.setNameDeclarant(text);
//		assertEquals("ELDORADO AUTOMOTIVE SL", declarantName);
//		 = mod180.setContactPerson(text);
//		assertEquals(, contactPerson);
//		String phoneNumber = mod180.setPhoneNumber(text);
//		assertEquals("691568973" , phoneNumber);
//		String exercise = mod180.setExercise(text);
//		assertEquals("2022" ,exercise);
//		String amount = mod180.setAmount(text);
//		assertEquals("4.280,74", amount);
//		String model = mod180.setModel(text);
//		assertEquals("180",model);
		}
	}

//
	@Test
	public void mod1902023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/5-MOD_190_ELDORADO_2022_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedEmail = "JESUSM.GARCIA@AYUDATPYMES.ES";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String phoneNumber = "691568973";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(phoneNumber, actualPhoneNumber);
			// 24.562,77
			Double expectedAmount = 24562.77;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "MUÑOZ GARCIA JESUS";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);



//		String issueDate = mod190.setIssueDate(text);
//		assertEquals("31 DE ENERO DE 2023", issueDate);
		}
	}

//	
	@Test
	public void mod3032023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/1-MOD_303_ELDORADO_2T_2023_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedEmail = "GESTION@AUTOSELDORADO.COM";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String expectedPhoneNumber = "658825867";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);

			int expectedYear = 2023;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			// 1.510,06
			Double expectedAmount = 1510.06;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "VERA NUÑEZ MARIA SILVERIA";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);

//			String principalActivity = mod303.setPrincipalActivity(text);
//			assertEquals("COMERCIO AL POR MAYOR DE", principalActivity);
//			String presentatorNif = mod303.setPresentatorNif(text);
//			assertEquals("50604707S", presentatorNif);
//			String presentatorName = mod303.setPresentatorName(text);
//			assertEquals("VERA NUÑEZ MARIA SILVERIA", presentatorName);
//			String amount = mod303.setAmount(text);
//			assertEquals("1.510,06", amount);
//			String year = mod303.setYear(text);
//			assertEquals("2023", year);
//			String period = mod303.setPeriod(text);
//			assertEquals("TRIM2", period);
//			String issueDate = mod303.setIssueDate(text);
//			assertEquals("25 DE JULIO DE 2023", issueDate);
//			String hacienda = mod303.setHacienda(text);
//			assertEquals("Bizkaia", hacienda);
		}
	}

//	
	@Test
	public void mod3492023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/7-7_MOD_349_JULIO_2021_ELDORADO_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif = "B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);

			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);

			String expectedEmail = "JESUSM.GARCIA@AYUDATPYMES.ES";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);

			String expectedPhoneNumber = "691568973";
			String actualPhoneNumber = fiscalModel.getContactPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);

			int expectedExercise = 2021;
			int actualExercise = fiscalModel.getYear();
			assertEquals(expectedExercise , actualExercise);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.M07, actualPeriod);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "MUÑOZ GARCIA JESUS";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);
//			String representativeNif = mod349.setRepresentativeNif(text);
//			assertEquals("4976686W", representativeNif);
//			String representativeName = mod349.setRepresentativeName(text);
//			assertEquals("HERRERO VALLINAS YOLANDA", representativeName);
		}
	}

	@Test
	public void mod3902023BizkaiaTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/6-MOD_390_ELDORADO_2022_BIZKAIA.PDF";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(file)) {

			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);

			String expectedNif ="B95541520";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);
			
			String expectedName = "ELDORADO AUTOMOTIVE SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			
			String expectedPhoneNumber = "658825867";
			String actualPhoneNumber = fiscalModel.getPhone();
			assertEquals(expectedPhoneNumber, actualPhoneNumber);
			
			String expectedEmail = "GESTION@AUTOSELDORADO.COM";
			String actualEmail = fiscalModel.getContactEmail();
			assertEquals(expectedEmail, actualEmail);
			//18.369,75
			Double expectedAmount = 18369.75;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			int expectedYear = 2022;
			int actualYear = fiscalModel.getYear();
			assertEquals(expectedYear, actualYear);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.YEAR, actualPeriod);
			
			Administration actualAdministration = fiscalModel.getAdministration();
			assertEquals(Administration.BIZKAIA , actualAdministration);
			
			String expectedContactPerson = "VERA NUÑEZ MARIA SILVERIA";
			String actualContactPerson = fiscalModel.getContactPerson();
			assertEquals(expectedContactPerson, actualContactPerson);


		}
	}
	
}
