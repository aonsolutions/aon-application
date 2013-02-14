package com.code.aon.ui.audabridge;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import com.code.aon.ui.audabridge.response.CalculationDataResponse;



public class AssessmentParseTest extends TestCase {
	
	private static final String SOURCE_FILE = "/com/code/aon/ui/audabridge/assessment.xml";
	
	public void testCreateXMLRequest() throws Exception {
		InputStream in =  AssessmentParseTest.class.getResourceAsStream(SOURCE_FILE);
		InputStreamReader reader = new InputStreamReader(in); 
		AudaBridgeManager manager = new AudaBridgeManager();
		CalculationDataResponse response =  manager.parseCalculationDataResponse(reader);
		in.close();
		assertEquals("WANES10000001569", response.getWan());
		assertEquals("QUATTRO", response.getTotalGeneral().getNombreVariante());
		assertEquals(18.0,response.getTotalGeneral().getPorcentajeIva());
	}
	
}
