package net.aonsolutions.core.tgss.creta.jaxb;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import net.aonsolutions.core.tgss.creta.jaxb.respuesta.Respuesta;

class UtilsTestCase {

	@Test
	void testUnmarshalClassOfTInputStream() throws JAXBException {
		//Respuesta respuesta130 = Utils.unmarshal(Respuesta.class, UtilsTestCase.class.getResourceAsStream("Respuesta130.xml"));
		//System.out.println(respuesta130.getAutorizado());
		Respuesta respuesta120 = Utils.unmarshal(Respuesta.class, UtilsTestCase.class.getResourceAsStream("Respuesta120.xml"));
		//System.out.println(respuesta120.getAutorizado());
		Respuesta respuesta110 = Utils.unmarshal(Respuesta.class, UtilsTestCase.class.getResourceAsStream("Respuesta110.xml"));
		//System.out.println(respuesta110.getAutorizado());
	}
	
	
}
