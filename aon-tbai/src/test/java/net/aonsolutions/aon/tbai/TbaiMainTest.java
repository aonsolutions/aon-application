package net.aonsolutions.aon.tbai;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

class TbaiMainTest {

	@Test
	void invoice_emission_test() {
		try{
			final InputStream is = TbaiMainTest.class.getResourceAsStream("factura.json");
			TbaiMain.tbai_emision(TbaiMain.json_to_invoice(is), "JSONtoTBAI.xml");
		}catch(Exception e) { e.printStackTrace(); fail("Unexpepected Extension " + e); }
	}
}
