package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VerifactuVerifactuAnulacionHuellaTest {

	@Test
	void testAnulacionHuella() {
		VerifactuAnulacionHuella block1 = VerifactuMocker.mock( VerifactuAnulacionHuella.class ); 
		 
		VerifactuAnulacionHuella block2 = new VerifactuAnulacionHuella();
		block2.setiDEmisorFacturaAnulada( block1.getiDEmisorFacturaAnulada() );
		block2.setNumSerieFacturaAnulada( block1.getNumSerieFacturaAnulada() );
		block2.setFechaExpedicionFacturaAnulada( block1.getFechaExpedicionFacturaAnulada() );
		block2.setPreviousHuella(block1.getPreviousHuella());
		block2.setFechaHoraHusoGenRegistro(block1.getFechaHoraHusoGenRegistro());
		VerifactuAsserts.assertClassEquals( block1, block2);

	}
	
	@Test
	void testFormat() {
		VerifactuAnulacionHuella h = new VerifactuAnulacionHuella();
		h.setiDEmisorFacturaAnulada("1");
		h.setNumSerieFacturaAnulada("2");
		h.setFechaExpedicionFacturaAnulada("3");
		h.setPreviousHuella("7");
		h.setFechaHoraHusoGenRegistro("8");
		String a = h.format();
		
		String expected = "IDEmisorFacturaAnulada=1"
				+ "&NumSerieFacturaAnulada=2"
				+ "&FechaExpedicionFacturaAnulada=3"
				+ "&Huella=7"
				+ "&FechaHoraHusoGenRegistro=8";
		assertEquals(expected, a);
		
	}
}
