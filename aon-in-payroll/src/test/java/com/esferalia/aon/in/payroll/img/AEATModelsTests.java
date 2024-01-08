package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modAeat.Mod1152023AEAT;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ParserUtils;

public class AEATModelsTests {

	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();
	
	
	@Test
	public void mod115AeatTest() throws Exception {
		Mod1152023AEAT mod115 = new Mod1152023AEAT();
		String file = "com/esferalia/aon/in/payroll/pdf/MOD_115_AEAT_2023.pdf";
		ClassLoader classLoader = AEATModelsTests.class.getClassLoader();
		try(InputStream is = classLoader.getResourceAsStream(file)){
			byte[] bytes = IOUtils.toByteArray(is);
			String text = pdfExtracter.extract(bytes);
			
			String nif = mod115.setNif(text);
			assertEquals("B93011708" , nif);
			String name = mod115.setSocialReason(text);
			assertEquals("AYUDA-T UN LUGAR TODAS LAS SOLUCIONES SL", name);
			String amount = mod115.setAmount(text);
			assertEquals("228,00" , amount);
			String hacienda = mod115.setHacienda(text);
			assertEquals("AEAT" , hacienda);
			String period = mod115.setPeriod(text);
			assertEquals("3T" , period);
			String exercise = mod115.setExercise(text);
			assertEquals("2023" , exercise);
			String model = mod115.setModel(text);
			assertEquals("115" , model);
		}
		
		
	}
}
