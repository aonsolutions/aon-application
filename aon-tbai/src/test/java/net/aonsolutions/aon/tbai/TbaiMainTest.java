package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai._enums.Territory.GIPUZKOA;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import net.aonsolutions.aon.tbai._enums.Territory;
import net.aonsolutions.aon.tbai.emision.EmisionInvoice;

public class TbaiMainTest {

	@Test
	public void invoiceEmissionGipuzkoaTest() {
		try{
			final InputStream is = TbaiMainTest.class.getResourceAsStream("factura.json");
			final EmisionInvoice invoice = TbaiMain.jsonToInvoice(is);
			TbaiMain.createEmisionTBAI(invoice, "JSONtoTBAI.xml",GIPUZKOA);
		}catch(Exception e) { e.printStackTrace(); fail("Unexpected Extension " + e); }
	}

}
