package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modAlava.ModelDocumentParser;
import com.esferalia.aon.in.payroll.pdf.modAlava.ParserUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class AlavaModelsTests {

	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();
	
	
	@Test
	public void mod115AlavaTest() throws Exception {
		
		String file = "com/esferalia/aon/in/payroll/pdf/MODELO_4T_115_DEPO_2022.pdf";
		ClassLoader classLoader = AlavaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B8586644 0";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif, actualNif);
			
			String expectedName = "DEPOCONSULTING SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
		
			Double expectedAmount = 279.40;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount, actualAmount);
			
			int expectedExercise = 2022;
			int actualExercise = fiscalModel.getYear();
			assertEquals(expectedExercise, actualExercise);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T4, actualPeriod);
			
//			FiscalModelType expectedModel = FiscalModelType.M115;
//			FiscalModelType actualModel = fiscalModel.getModel();
//			assertEquals(expectedModel, actualModel);
						
		}
	
	}
	
	@Test
	public void mod303AlavaTest() throws Exception{
		
		String file = "com/esferalia/aon/in/payroll/pdf/aonSolutions-M_303_2023_1T.pdf";
		ClassLoader classLoader = AlavaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){

		byte[] bytes = IOUtils.toByteArray(is);
		FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
		
		String expectedNif = "B0148727";
		String actualNif = fiscalModel.getDocument();
		assertEquals(expectedNif, actualNif);
		
		String expectedName = "AON SOLUTIONS SLU";
		String actualName = fiscalModel.getName();
		assertEquals(expectedName, actualName);
		
		Double expectedAmount = 31.664;
		Double actualAmount = fiscalModel.getDeclarationResult();
		assertEquals(expectedAmount, actualAmount);
		
		int expectedExercise = 2023;
		int actualExercise = fiscalModel.getYear();
		assertEquals(expectedExercise, actualExercise);
		
		System.out.println(fiscalModel.getPeriod());
	
//		FiscalModelType expectedModel = FiscalModelType.M115;
//		FiscalModelType actualModel = fiscalModel.getModel();
//		assertEquals(expectedModel, actualModel);

		}
	}
	
	@Test
	public void mod349AlavaTest() throws Exception{
		String file = "com/esferalia/aon/in/payroll/pdf/aonSolutions-M_349_2023_2T.pdf";
		ClassLoader classLoader = AlavaModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){

		byte [] bytes = IOUtils.toByteArray(is);
		FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
		
		String expectedNif = "B01487271";
		String actualNif = fiscalModel.getDocument();
		assertEquals( expectedNif, actualNif);
		
		String expectedName = "AON SOLUTIONS SLU";
		String actualName = fiscalModel.getName();
		assertEquals(expectedName, actualName);
		
		Double expectedAmount = 2.176;
		Double actualAmount = fiscalModel.getDeclarationResult();
		assertEquals(expectedAmount, actualAmount);
		
		int expectedYear = 2023;
		int actualYear = fiscalModel.getYear();
		assertEquals(expectedYear, actualYear);
		
		System.out.println(fiscalModel.getPeriod());

		
//		String model = mod349.setModel(text);
//		assertEquals("349", model);
//		String exercise = mod349.setExercise(text);
//		assertEquals("2.023" , exercise);
//		String period = mod349.setPeriod(text);
//		assertEquals("2T" , period);
//		String issueDate = mod349.setIssueDate(text);
//		assertEquals("19-07-2023" , issueDate);
//		String amount = mod349.setAmount(text);
//		assertEquals("2.176" , amount);
		
		}
	}
}
