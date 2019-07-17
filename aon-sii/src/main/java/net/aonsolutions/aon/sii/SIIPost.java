package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.json.JSONObject;

public class SIIPost {
	
	public static SIIPost getInstance(byte[] cert, String pass) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return new SIIPost(cert, pass);
	}
	
	byte[] cert;
	String pass;

	public SIIPost(byte[] cert, String pass) {
		this.cert = cert;
		this.pass = pass;
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(cert);
		
			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			if(cert == null) {
				System.out.println("SII CERT LOG - NULLPOINTER cert value");
			}
			System.out.println("SII CERT LOG -/" + pass + "/-" + key);
			keyStore.load(key, pass.toCharArray());
	
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, pass.toCharArray());

			String alias = (String) keyStore.aliases().nextElement();
            Date certExpiryDate = ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter();
        
            if(certExpiryDate.compareTo(new Date())<= 0) {
    			throw new IllegalStateException("El certificado ha expirado.");
            }

	        TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};

			SSLContext sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			// Set trust all certificates context to HttpsURLConnection
        
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (KeyManagementException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			String msg = ("keystore password was incorrect".equals(e.getMessage())) 
					? "La contraseña es incorrecta." : e.getMessage();
			throw new IllegalStateException(msg);
		}
	}
	
	
	private static class TrustAllCertificates implements X509TrustManager {
	    public void checkClientTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public void checkServerTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	}
	
	protected String post(String uri, String document) throws SOAPException, IOException {
        System.out.println("********************* REQUEST *******************");
        System.out.println(uri);
        System.out.println(document);

		InputStream is = new ByteArrayInputStream(document.getBytes());

		MimeHeaders headers = new MimeHeaders();
		headers.addHeader("Content-Type", SOAPConstants.SOAP_1_1_CONTENT_TYPE);

		SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(headers, is);
		is.close();

        SOAPConnection soapConnection =  SOAPConnectionFactory.newInstance().createConnection();
        SOAPMessage soapResponse = soapConnection.call(soapMessage, uri);
		
        soapConnection.close();

        System.out.println("********************* RESPONSE *******************");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapResponse.writeTo(baos);
        String result = baos.toString();
        System.out.println(result);
        if(result.contains("<faultstring>")) {
            result = result.split("<faultstring>")[1];	
            result = result.split("</faultstring>")[0];
			throw new IllegalStateException(result);
        } else {
        	Boolean isBodyId = result.contains("<env:Body Id=\"Body\">");
        	result = isBodyId
        		? result.split("<env:Body Id=\"Body\">")[1]
        		: result.split("<soap:Body>")[1];	
        	result = isBodyId
        		? result.split("</env:Body>")[0]
        		: result.split("</soap:Body>")[0];
        		System.out.println(result);
        	return result;
        }
	}
	
	protected String marshal(Class clazz,Object suministro) throws JAXBException {
		Marshaller mar =  JAXBContext.newInstance(clazz).createMarshaller();
		mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
		StringWriter sw = new StringWriter();
		mar.marshal(suministro, sw);
		String soapEnvelope =
		       "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
			       "<soapenv:Header /><soapenv:Body>%s</soapenv:Body></soapenv:Envelope>";
		String output = String.format(soapEnvelope, sw.toString());
		return output;
	}
	
	protected Object unmarshal(Class clazz, String response) throws JAXBException {
		response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + response;
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		JAXBElement o = (JAXBElement) unmar.unmarshal(new StringReader(response));
		return o.getValue();
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
	
}