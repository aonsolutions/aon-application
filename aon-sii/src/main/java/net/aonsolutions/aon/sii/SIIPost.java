package net.aonsolutions.aon.sii;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;

public class SIIPost {
	
	public static SIIPost getInstance(SiiConfiguration siiConfiguration) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return new SIIPost(siiConfiguration);
	}
	
	SiiConfiguration siiConfiguration;

	Marshaller marshaller;
	Unmarshaller unmarshaller;
	
	public SIIPost(SiiConfiguration siiConfiguration) {
		this.siiConfiguration = siiConfiguration;
	}
	
	public SiiConfiguration getSiiConfiguration() {
		return siiConfiguration;
	}
	
	private void secure(String uri) {
		try {
			Certificate cert = getSiiConfiguration().getCertificate();
			ByteArrayInputStream key = new ByteArrayInputStream(cert.getData());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, cert.getPassword().toCharArray());
    	
    		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, cert.getPassword().toCharArray());
   	        
            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			SSLContext.setDefault(sslContext);
            // Set trust all certificates context to HttpsURLConnection
            
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            // Open HTTPS connection
            URL url = new URL(uri);
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
            // Trust all hosts
            httpsConnection.setHostnameVerifier(new TrustAllHosts());
            // Connect
            httpsConnection.connect();
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (KeyManagementException e) {
			e.printStackTrace();
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
	
	private static class TrustAllHosts implements HostnameVerifier {
	    public boolean verify(String hostname, SSLSession session) {
	        return true;
	    }
	}
	
	protected String post(String uri, String document) throws SOAPException, IOException {
        System.out.println("********************* REQUEST *******************");
        System.out.println(document);

		InputStream is = new ByteArrayInputStream(document.getBytes());
		SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, is);
		is.close();
		secure(uri);
		
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = soapConnection.call(soapMessage, uri);
        soapConnection.close();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapResponse.writeTo(baos);
        String result = baos.toString();
        System.out.println("********************* RESPONSE *******************");
        System.out.println(result);
        
        String body = "<env:Body>";
        String endBody = "</env:Body>";
        if(result.contains("<env:Body Id=\"Body\">")) {
        	body = "<env:Body Id=\"Body\">";
        } else if(result.contains("<soap:Body>")) {
        	body = "<soap:Body>";
        	endBody = "</soap:Body>";
        } else if(result.contains("<faultstring>")) {
        	body = "<faultstring>";
        	endBody = "</faultstring>";
        }
        result = result.split(body )[1];
        result = result.split(endBody)[0];
        System.out.println(result);
        return result;
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
}