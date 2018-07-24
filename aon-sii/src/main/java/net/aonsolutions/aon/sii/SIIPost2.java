package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.json.JSONObject;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.mime.MimeContainer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

public class SIIPost2 extends WebServiceGatewaySupport{
		
	public static SIIPost2 getInstance(byte[] cert, String pass, String requestContextPath, String responseContextPath) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return new SIIPost2(cert, pass, requestContextPath, responseContextPath);
	}
	
	byte[] cert;
	String pass;
	
	public SIIPost2(byte[] cert, String pass, String requestContextPath, String responseContextPath) {
		this.cert = cert;
		this.pass = pass;
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(cert);
	
			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			keyStore.load(key, pass.toCharArray());
    	
    		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, pass.toCharArray());

   			HttpsUrlConnectionMessageSender messageSender = new HttpsUrlConnectionMessageSender();
   			messageSender.setKeyManagers(kmf.getKeyManagers());
    	
    		messageSender.setSslProtocol("SSLv3");
  
    		setMessageSender(messageSender);
    	
    		CustJaxbUnMarshaller marshaller = new CustJaxbUnMarshaller();
    		marshaller.setContextPath(requestContextPath);
    		
    		CustJaxbUnMarshaller unmarshaller = new CustJaxbUnMarshaller();
    		unmarshaller.setContextPath(responseContextPath);

    		setMarshaller(marshaller);
    		setUnmarshaller(unmarshaller);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
	}

	protected Object post(String uri, Object object) {
		return getWebServiceTemplate().marshalSendAndReceive(uri, object);
	}
	
	protected JSONObject json(Integer id, String name, String referenceCode){
		JSONObject json = new JSONObject();
    	json.put("id", id);
    	json.put("name", "Factura " + referenceCode + (!id.equals(200) ?  " - Error " + id + ": " : " - ") + name);
    	return json;
	}
	
	public byte[] getCert() {
		return cert;
	}
 
    // -------------------- SUB-CLASES
    
    public class CustJaxbUnMarshaller extends Jaxb2Marshaller {
  	  
	    @Override
	    public Object unmarshal(Source source) throws XmlMappingException {
	        return super.unmarshal(source, null);
	    }
	    @Override
	    public Object unmarshal(Source source, MimeContainer mimeContainer)
	            throws XmlMappingException {
	        Object mimeMessage = new DirectFieldAccessor(mimeContainer)
	                .getPropertyValue("mimeMessage");
	        Object unmarshalObject = null;
	        if (mimeMessage instanceof SaajSoapMessage) {
	            SaajSoapMessage soapMessage = (SaajSoapMessage) mimeMessage;
	            String faultReason = soapMessage.getFaultReason();
	            if (faultReason != null) {
	                throw convertJaxbException(new JAXBException(faultReason));
	            } else {
	                unmarshalObject = super.unmarshal(source, mimeContainer);
	            }
	        }
	        return unmarshalObject;
	    }
	}
}