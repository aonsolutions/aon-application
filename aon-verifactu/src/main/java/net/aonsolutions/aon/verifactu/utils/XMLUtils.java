package net.aonsolutions.aon.verifactu.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.Certificate;

import net.aonsolutions.aon.verifactu.VerifactuResponse;

public class XMLUtils {

	public static Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}
	
	public static String documentToString(Document doc) {
	    try {
	        java.io.StringWriter sw = new java.io.StringWriter();
	        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
	        javax.xml.transform.Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}
	
	public static byte[] marshal(Object object, Class<?> clazz) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(object, bos);
		return bos.toByteArray();
	}
	
	public static Object unmarshal(byte[] data, Class<?> clazz) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		final Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		InputStream is = new ByteArrayInputStream(data);
		return jaxbMarshaller.unmarshal(is);
	}
	
	public static String soapMarshal(Object suministro, Class<?> clazz) throws JAXBException {
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
	
	public static Object soapUnmarshal(Class clazz, String response) throws JAXBException {
		response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + response;
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		JAXBElement o = (JAXBElement) unmar.unmarshal(new StringReader(response));
		return o.getValue();
	}
	
	public static VerifactuResponse post(Certificate cert, String uri, String document) throws SOAPException, IOException {
        System.out.println("********************* REQUEST *******************");
        System.out.println(document);
        
		InputStream is = new ByteArrayInputStream(document.getBytes());
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage soapMessage = factory.createMessage(null, is);
		is.close();
		secure(cert, uri);
		
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
        VerifactuResponse vr = new VerifactuResponse();

        
        String body = "<env:Body>";
        String endBody = "</env:Body>";
        if(result.contains("<env:Body Id=\"Body\">")) {
        	body = "<env:Body Id=\"Body\">";
            result = result.split(body )[1];
            result = result.split(endBody)[0];
        } else if(result.contains("<env:Body Id='Body'>")) {
        	body = "<env:Body Id='Body'>";
        	result = result.split(body )[1];
            result = result.split(endBody)[0];
        } else if(result.contains("<soap:Body>")) {
        	body = "<soap:Body>";
        	endBody = "</soap:Body>";
        	result = result.split(body )[1];
            result = result.split(endBody)[0];
        } else if(result.contains("<faultstring>")) {
        	body = "<faultstring>";
        	endBody = "</faultstring>";

            String message = result.split(body )[1];
            message = message.split(endBody)[0];
            vr.setError(true);
            vr.setErrorMessage(message);            
        }
        vr.setResponse(result);
        return vr;
	}
	
	private static void secure(Certificate cert, String uri) {
		try {
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
	
	
	public static byte[] send(Certificate certificate, String uri, byte[] xml) throws Exception{
		ByteArrayInputStream key = new ByteArrayInputStream(certificate.getData());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(key, certificate.getPassword().toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, certificate.getPassword().toCharArray());

		TrustManager[] trustAll = new TrustManager[] { new TrustAllCertificates() };

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
		SSLContext.setDefault(sslContext);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

		URL url = new URL(uri);
		URLConnection con = url.openConnection();
		HttpsURLConnection https = (HttpsURLConnection) con;

		https.setHostnameVerifier(new TrustAllHosts());
		https.setRequestMethod("POST");
		https.setRequestProperty("Content-Type", "application/xml; charset=utf-8;");
		https.setDoOutput(true);
		https.setDoInput(true);
		https.setUseCaches(false);

		OutputStream os = https.getOutputStream();
		os.write(xml);
		os.close();

		InputStream response = (InputStream) https.getContent();
		return response.readAllBytes();
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

}
