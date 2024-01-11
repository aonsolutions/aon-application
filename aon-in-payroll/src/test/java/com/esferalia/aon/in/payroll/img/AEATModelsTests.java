package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modAeat.ModelDocumentParser;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ParserUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Period;

public class AEATModelsTests {

	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();
	
	
	@Test
	public void mod115AeatTest() throws Exception {
		String file = "com/esferalia/aon/in/payroll/pdf/MOD_115_AEAT_2023.pdf";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			FiscalModel fiscalModel = ModelDocumentParser.parse(bytes);
			
			String expectedNif = "B93011708";
			String actualNif = fiscalModel.getDocument();
			assertEquals(expectedNif , actualNif);
			
			String expectedName = "AYUDA-T UN LUGAR TODAS LAS SOLUCIONES SL";
			String actualName = fiscalModel.getName();
			assertEquals(expectedName, actualName);
			
			Double expectedAmount = 228.00;
			Double actualAmount = fiscalModel.getDeclarationResult();
			assertEquals(expectedAmount , actualAmount);
			
			int expectedExercise = 2023;
			int actualExercise = fiscalModel.getYear();
			assertEquals(expectedExercise , actualExercise);
			
			Period actualPeriod = fiscalModel.getPeriod();
			assertEquals(Period.T3, actualPeriod);
			
		}
		
		
	}
}
