package net.aonsolutions.aon.nif;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.xml.bind.JAXBElement;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import net.aonsolutions.aeat.nif.ObjectFactory;
import net.aonsolutions.aeat.nif.VNifV1Ent;
import net.aonsolutions.aeat.nif.VNifV2Ent;
import net.aonsolutions.aeat.nif.VNifV2Sal;

public class NIFPost extends WebServiceGatewaySupport{
		
	public static NIFPost getInstance(byte[] cert, String pass){
		return new NIFPost(cert, pass);
	}
	
	public NIFPost(byte[] cert, String pass) {
		try{
			ByteArrayInputStream key = new ByteArrayInputStream(cert);
	    	KeyStore keyStore = KeyStore.getInstance("PKCS12");

	    	keyStore.load(key, pass.toCharArray());
    	
    		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, pass.toCharArray());

   			HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
   			messageSender.setKeyManagers(kmf.getKeyManagers());
    	
    		setMessageSender(messageSender);
    	
        	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        	marshaller.setCheckForXmlRootElement(false);
        	marshaller.setContextPath("net.aonsolutions.aeat.nif");
        	setMarshaller(marshaller);
        	setUnmarshaller(marshaller);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object post(String uri, Object object) {
		return getWebServiceTemplate().marshalSendAndReceive(uri, object);
	}
	
	@SuppressWarnings("unchecked")
	public VNifV2Sal vnifV2(VNifV2Ent vnif) {
		String uri = "https://www1.agenciatributaria.gob.es/wlpl/BURT-JDIT/ws/VNifV2SOAP";
		ObjectFactory of = new ObjectFactory();
		JAXBElement<VNifV2Ent> reqjaxb = of.createVNifV2Ent(vnif);
    	JAXBElement<VNifV2Sal> response = (JAXBElement<VNifV2Sal>) post(uri, reqjaxb);
    	return response.getValue();
	}

	@SuppressWarnings("unchecked")
	public Boolean vnifV1(VNifV1Ent vnif) {
		String uri = "https://www1.agenciatributaria.gob.es/wlpl/BURT-JDIT/ws/VNifV1SOAP";
		ObjectFactory of = new ObjectFactory();
		JAXBElement<VNifV1Ent> reqjaxb = of.createVNifV1Ent(vnif);
		try{
			Object response = post(uri, reqjaxb);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}