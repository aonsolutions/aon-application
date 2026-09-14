package net.aonsolutions.aon.tbai;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;

import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import ticketbai.kontsulta.KontsultaTicketBAI;

public class KontsultaArabaTest extends AbstractTbaiTest {
	
	@Test
	public void test() throws JAXBException, StatusCodeException {
		// TODO Auto-generated constructor stub
		Certificate certificate = AonSecret.getSigCert();
		
		KontsultaTicketBAI query = TbaiConsulta.buildQuery();
		
		
		final JAXBContext jaxbContext = JAXBContext.newInstance(KontsultaTicketBAI.class);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(query, bos);
	
	
		byte[] data = bos.toByteArray();

		TbaiMain tbaiMain = new TbaiMain();
			
//		String lastInvoiceUri = "https://ticketbai.araba.eus/TicketBAIConsultas/v1/ultimafactura" ;
//		tbaiMain.sendXML("https://ticketbai.araba.eus/TicketBAIConsultas/v1/facturaspaginadas", certificate, data, false);
	}	

}
