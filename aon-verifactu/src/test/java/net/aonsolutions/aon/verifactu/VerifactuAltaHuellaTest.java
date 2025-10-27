package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VerifactuAltaHuellaTest {

	@Test
	void testAltaHuella() {
		VerifactuAltaHuella block1 = VerifactuMocker.mock( VerifactuAltaHuella.class ); 
		 
		VerifactuAltaHuella block2 = new VerifactuAltaHuella();
		block2.setIDEmisorFactura( block1.getIDEmisorFactura() );
		block2.setNumSerieFactura(block1.getNumSerieFactura());
		block2.setFechaExpedicionFactura(block1.getFechaExpedicionFactura());
		block2.setTipoFactura(block1.getTipoFactura());
		block2.setCuotaTotal(block1.getCuotaTotal());
		block2.setImporteTotal(block1.getImporteTotal());
		block2.setPreviousHuella(block1.getPreviousHuella());
		block2.setFechaHoraHusoGenRegistro(block1.getFechaHoraHusoGenRegistro());
		VerifactuAsserts.assertClassEquals( block1, block2);

	}
	
	@Test
	void testFormat() {
		VerifactuAltaHuella h = new VerifactuAltaHuella();
		h.setIDEmisorFactura("1");
		h.setNumSerieFactura("2");
		h.setFechaExpedicionFactura("3");
		h.setTipoFactura("4");
		h.setCuotaTotal("5");
		h.setImporteTotal("6");
		h.setPreviousHuella("7");
		h.setFechaHoraHusoGenRegistro("8");
		String a = h.format();
		
		String expected = "IDEmisorFactura=1"
				+ "&NumSerieFactura=2"
				+ "&FechaExpedicionFactura=3"
				+ "&TipoFactura=4"
				+ "&CuotaTotal=5"
				+ "&ImporteTotal=6"
				+ "&Huella=7"
				+ "&FechaHoraHusoGenRegistro=8";
		assertEquals(expected, a);
		
	}
}
