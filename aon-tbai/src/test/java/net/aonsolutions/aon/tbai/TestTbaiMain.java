package net.aonsolutions.aon.tbai;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import net.aonsolutions.aon.tbai.TbaiMain;

class TestTbaiMain {

	@Test
	void invoice_emission_test() {
		
		try{
			InputStream is = TestTbaiMain.class.getResourceAsStream("factura.json");
			TbaiMain.json_to_invoice(is);
		}catch(Exception e) {
			e.printStackTrace();
			fail("Unexpepected Extension " + e);
		}
	}

}
