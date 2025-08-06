package net.aonsolutions.aon.verifactu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.watson.util.AonChronometer;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class VerifactuXMLUtils {
	
	private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";

	private VerifactuXMLUtils() {
		
	}
	
//	private static Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
//		InputStream is = new ByteArrayInputStream(data);
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		return dBuilder.parse(is);
//	}
	
//	private static String documentToString(Document doc) {
//	    try {
//	        java.io.StringWriter sw = new java.io.StringWriter();
//	        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
//	        javax.xml.transform.Transformer transformer = tf.newTransformer();
//	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//
//	        transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(sw));
//	        return sw.toString();
//	    } catch (Exception ex) {
//	        throw new RuntimeException("Error converting to String", ex);
//	    }
//	}
//	
	
//	private static Object unmarshal(byte[] data, Class<?> clazz) throws JAXBException {
//		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
//		final Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
//		InputStream is = new ByteArrayInputStream(data);
//		return jaxbMarshaller.unmarshal(is);
//	}
	
//	static <T> String soapMarshal(T suministro, Class<T> clazz) throws JAXBException {
//		Marshaller mar =  JAXBContext.newInstance(clazz).createMarshaller();
//		mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
//		StringWriter sw = new StringWriter();
//		mar.marshal(suministro, sw);
//		String soapEnvelope =
//		       "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//			       "<soapenv:Header/><soapenv:Body>%s</soapenv:Body></soapenv:Envelope>";
//		
//		String ret = String.format(soapEnvelope, sw.toString()); 
//		System.out.println("----- soapMarshal"); 
//		System.out.println( ret );
//		System.out.println("-----");
//		return ret;
//	}
	
	static <T> byte[] toBytes(Document document) throws VerifactuException {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
		    transformer.setOutputProperty(OutputKeys.INDENT, "no");
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		    return outputStream.toByteArray();
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			throw new VerifactuException(VerifactuError.AON_9004 ,e);
		}
	}
	
	static <T> Document toDocument(T data, Class<T> clazz) throws VerifactuException {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Marshaller mar =  JAXBContext.newInstance(clazz).createMarshaller();
			mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
			mar.marshal(data, document);
			return document;
		} catch (JAXBException | ParserConfigurationException e) {
			e.printStackTrace();
			throw new VerifactuException(VerifactuError.AON_9004 ,e);
		}
	}

	static <T> SOAPMessage soapMarshal(Document document) throws VerifactuException {
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
			soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
			String prefix = "soapenv";
			soapEnvelope.addNamespaceDeclaration(prefix, SOAP_NAMESPACE );
			soapEnvelope.setPrefix(prefix);
			SOAPHeader soapHeader = soapEnvelope.getHeader();
			soapHeader.setPrefix(prefix);
	        SOAPBody soapBody = soapEnvelope.getBody();
	        soapBody.setPrefix(prefix);
			soapBody.addDocument(document);
			soapMessage.saveChanges();		
			return soapMessage;
		} catch (SOAPException e) {
			e.printStackTrace();
			throw new VerifactuException(VerifactuError.AON_9004 ,e);
		}
			
	}

//	public static String soapMarshal(Object suministro, Class<?> clazz) throws JAXBException {
//		Marshaller mar =  JAXBContext.newInstance(clazz).createMarshaller();
//		mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
//		StringWriter sw = new StringWriter();
//		mar.marshal(suministro, sw);
//		String soapEnvelope =
//		       "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//			       "<soapenv:Header /><soapenv:Body>%s</soapenv:Body></soapenv:Envelope>";
//		return String.format(soapEnvelope, sw.toString());
//	}
	
	
//	private static <T> T _soapUnmarshal(Class<T> clazz, String response) throws JAXBException {
//		response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + response;
//		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
//		@SuppressWarnings("unchecked")
//		JAXBElement<T> o = (JAXBElement<T>) unmar.unmarshal(new StringReader(response));
//		return o.getValue();
//	}
	
	static VerifactuResponse post(Certificate cert, String uri, SOAPMessage soapMessage) throws VerifactuException {
		try {
			AonChronometer cr = new AonChronometer();
			cr.start();
			System.out.println("START TRACING POST");
			
	        System.out.print("\tTRACING POST: 0 - " + "REQUEST:");
	        soapMessage.writeTo(System.out);
	        System.out.println();
	        
	        System.out.println("\tTRACING POST: 1 - " + cr.getCurrentTime());
	        
			secure(cert, uri);
			
	        System.out.println("\tTRACING POST: 2 - " + cr.getCurrentTime());
	        
	        // Create SOAP Connection
	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	        // Send SOAP Message to SOAP Server
	        SOAPMessage soapResponse = soapConnection.call(soapMessage, uri);
	        soapConnection.close();
	        System.out.println("\tTRACING POST: 3 - " + cr.getCurrentTime());
	        
        	// SOAP Response to String
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        soapResponse.writeTo(baos);
	        String stringResp = new String(baos.toByteArray());
        	System.out.println("\tTRACING POST: 4 - RESPONSE: " + stringResp );
	        System.out.println("\tTRACING POST: 5 - " + cr.getCurrentTime());

			SOAPBody soapBody = soapResponse.getSOAPBody();
	        Document bodyDoc = soapBody.extractContentAsDocument();
	        VerifactuResponse vr = new VerifactuResponse();
	        System.out.println("\tTRACING POST: 6 - " + cr.getCurrentTime());
	        
	        NodeList faults = bodyDoc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
	        if (faults.getLength() > 0) {
	            Element faultElem = (Element) faults.item(0);
	            String faultString = faultElem.getElementsByTagName("faultstring").item(0).getTextContent();
	            vr.setError(true);
	            vr.setErrorMessage( faultString );            
	        } else {	        
	        	// SOAP Response to RespuestaRegFactuSistemaFacturacionType
	        	JAXBContext jc = JAXBContext.newInstance(RespuestaRegFactuSistemaFacturacionType.class.getPackage().getName());
	        	Unmarshaller um = jc.createUnmarshaller();
	        	JAXBElement<RespuestaRegFactuSistemaFacturacionType> o = um.unmarshal(bodyDoc, RespuestaRegFactuSistemaFacturacionType.class);
	        	vr.setError(false)
	        		.setResponse(o.getValue());
	        }
	        System.out.println("\tTRACING POST: 7 - " + cr.getCurrentTime());
			return vr.setBytes( stringResp.getBytes() );
		} catch (SOAPException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new VerifactuException(VerifactuError.AON_9004 ,e);
		} catch (SOAPFaultException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return new VerifactuResponse()
			 	.setError( true )
			 	.setErrorMessage(e.getMessage())
			 	.setBytes(sw.toString().getBytes());
		}
	}
	
//	private static VerifactuResponse _post(Certificate cert, String uri, String document) throws VerifactuException {
//		try {
//	        System.out.println("********************* REQUEST *******************");
//	        System.out.println(document);
//	        
//			InputStream is = new ByteArrayInputStream(document.getBytes());
//			MessageFactory factory = MessageFactory.newInstance();
//			SOAPMessage soapMessage = factory.createMessage(null, is);
//			is.close();
//			secure(cert, uri);
//			
//	        // Create SOAP Connection
//	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//
//	        // Send SOAP Message to SOAP Server
//	        SOAPMessage soapResponse = soapConnection.call(soapMessage, uri);
//	        soapConnection.close();
//	        
//	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        soapResponse.writeTo(baos);
//	        String result = baos.toString();
//	        System.out.println("********************* RESPONSE *******************");
//	        System.out.println(result);
//	        
//	        VerifactuResponse vr = new VerifactuResponse();
//	        String body = "<env:Body>";
//	        String endBody = "</env:Body>";
//	        if(result.contains("<env:Body Id=\"Body\">")) {
//	        	body = "<env:Body Id=\"Body\">";
//	            result = result.split(body )[1];
//	            result = result.split(endBody)[0];
//	        } else if(result.contains("<env:Body Id='Body'>")) {
//	        	body = "<env:Body Id='Body'>";
//	        	result = result.split(body )[1];
//	            result = result.split(endBody)[0];
//	        } else if(result.contains("<soap:Body>")) {
//	        	body = "<soap:Body>";
//	        	endBody = "</soap:Body>";
//	        	result = result.split(body )[1];
//	            result = result.split(endBody)[0];
//	        } else if(result.contains("<faultstring>")) {
//	        	body = "<faultstring>";
//	        	endBody = "</faultstring>";
//
//	            String message = result.split(body )[1];
//	            message = message.split(endBody)[0];
//	            vr.setError(true);
//	            vr.setErrorMessage(message);            
//	        }
//			RespuestaRegFactuSistemaFacturacionType respuesta = VerifactuXMLUtils._soapUnmarshal(RespuestaRegFactuSistemaFacturacionType.class, result );
//			vr.setResponse(respuesta)
//	        	.setBytes(result.getBytes());
//	        return vr;
//		} catch (SOAPException | JAXBException | IOException e) {
//			e.printStackTrace();
//			throw new VerifactuException(VerifactuError.AON_9004 ,e);
//		}
//	}

	private static void secure(Certificate cert, String uri) throws VerifactuException {
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
            // URL url = new URL(uri);
            URL url = URI.create(uri).toURL();
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
            // Trust all hosts
            httpsConnection.setHostnameVerifier(new TrustAllHosts());
            // Connect
            httpsConnection.connect();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | KeyManagementException e) {
			e.printStackTrace();
			throw new VerifactuException(e);
		}
	}
	
	
//	private static byte[] send(Certificate certificate, String uri, byte[] xml) throws VerifactuException {
//		try {
//			ByteArrayInputStream key = new ByteArrayInputStream(certificate.getData());
//			KeyStore keyStore = KeyStore.getInstance("PKCS12");
//			keyStore.load(key, certificate.getPassword().toCharArray());
//			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//			kmf.init(keyStore, certificate.getPassword().toCharArray());
//	
//			TrustManager[] trustAll = new TrustManager[] { new TrustAllCertificates() };
//	
//			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//			sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
//			SSLContext.setDefault(sslContext);
//			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//	
//			URL url = new URL(uri);
//			URLConnection con = url.openConnection();
//			HttpsURLConnection https = (HttpsURLConnection) con;
//	
//			https.setHostnameVerifier(new TrustAllHosts());
//			https.setRequestMethod("POST");
//			https.setRequestProperty("Content-Type", "application/xml; charset=utf-8;");
//			https.setDoOutput(true);
//			https.setDoInput(true);
//			https.setUseCaches(false);
//	
//			OutputStream os = https.getOutputStream();
//			os.write(xml);
//			os.close();
//	
//			InputStream response = (InputStream) https.getContent();
//			return response.readAllBytes();
//		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
//			e.printStackTrace();
//			throw new VerifactuException(e);
//		}
//	}
	
	private static class TrustAllCertificates implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] certs, String authType) {
			// Nothing
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
			// Nothing
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
