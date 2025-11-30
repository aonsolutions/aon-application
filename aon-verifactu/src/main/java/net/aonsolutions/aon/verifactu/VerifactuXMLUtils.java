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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;

class VerifactuXMLUtils {
	
	private static final String SOAP_NAMESPACE_PREFIX = "soapenv";
	private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String FAULT_ELEMENT = "Fault";
	private static final String FAULTSTRING2_ELEMENT = "faultstring";
	private static final Pattern FAULT_STRING = Pattern.compile("Codigo\\[(\\d+)\\]");
	
	private VerifactuXMLUtils() {
		
	}
	
	static byte[] toBytes(Document document) throws InvoiceCommunicationException {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
		    transformer.setOutputProperty(OutputKeys.INDENT, "no");
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		    return outputStream.toByteArray();
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9004 ,e);
		}
	}
	
	static <T> Document toDocument(T data, Class<T> clazz) throws InvoiceCommunicationException {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Marshaller mar =  JAXBContext.newInstance(clazz).createMarshaller();
			mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
			mar.marshal(data, document);
			return document;
		} catch (JAXBException | ParserConfigurationException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9004 ,e);
		}
	}

	static SOAPMessage soapMarshal(Document document) throws InvoiceCommunicationException {
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
			soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
			String prefix = SOAP_NAMESPACE_PREFIX;
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
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9004 ,e);
		}
			
	}

	static VerifactuResponse post(Certificate cert, String uri, SOAPMessage soapMessage) throws InvoiceCommunicationException {
		try {
			if (cert == null || cert.getData() == null || cert.getData().length == 0) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0023);
			}
			
//			// ++++++++++++++++++ BORRAR
//			System.out.println("** REQUEST");
//			soapMessage.writeTo(System.out);
//			System.out.println("");
//			System.out.println("**");
//			// ++++++++++++++++++ 
			
			secure(cert, uri);
	        // Create SOAP Connection
	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	        // Send SOAP Message to SOAP Server
	        SOAPMessage soapResponse = soapConnection.call(soapMessage, uri);
	        
//			// ++++++++++++++++++ BORRAR
//			System.out.println("** RESPONSE");
//	        soapResponse.writeTo(System.out);
//			System.out.println();
//			System.out.println("**");
//			// ++++++++++++++++++ 
	        
	        soapConnection.close();
        	// SOAP Response to String
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        soapResponse.writeTo(baos);
	        String stringResp = new String(baos.toByteArray());
			SOAPBody soapBody = soapResponse.getSOAPBody();
	        Document bodyDoc = soapBody.extractContentAsDocument();
	        VerifactuResponse vr = new VerifactuResponse();
	        NodeList faults = bodyDoc.getElementsByTagNameNS(SOAP_NAMESPACE, FAULT_ELEMENT);
	        if (faults.getLength() > 0) {
	            Element faultElem = (Element) faults.item(0);
	            String faultString = faultElem.getElementsByTagName(FAULTSTRING2_ELEMENT).item(0).getTextContent();
	            Matcher matcher = FAULT_STRING.matcher(faultString);
	            if (matcher.find()) {
	                String number = matcher.group(1); 
	                InvoiceCommunicationException e = InvoiceCommunicationError.safeValueof(number)
	                	.map(InvoiceCommunicationException::new)
	                	.orElse(null);
	                if (e != null) throw e;
	            }	            
	            throw new InvoiceCommunicationException( faultString );
	        } else {	        
	        	// SOAP Response to RespuestaRegFactuSistemaFacturacionType
	        	JAXBContext jc = JAXBContext.newInstance(RespuestaRegFactuSistemaFacturacionType.class.getPackage().getName());
	        	Unmarshaller um = jc.createUnmarshaller();
	        	JAXBElement<RespuestaRegFactuSistemaFacturacionType> o = um.unmarshal(bodyDoc, RespuestaRegFactuSistemaFacturacionType.class);
	        	vr.setResponse(o.getValue());
	        }
			return vr.setBytes( stringResp.getBytes() );
		} catch (SOAPException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9004 ,e);
		} catch (SOAPFaultException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw new InvoiceCommunicationException( e.getMessage() );
		}
	}
	
	private static void secure(Certificate cert, String uri) throws InvoiceCommunicationException {
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(cert.getData());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, cert.getPassword().toCharArray());
    	
    		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, cert.getPassword().toCharArray());
   	        
            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};
            
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
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
			throw new InvoiceCommunicationException(e);
		}
	}
	
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

	static List<String> parseHistory(byte[] xmlBytes, Integer invoiceId) throws InvoiceCommunicationException {
		try {
			List<String> messages = new LinkedList<>();
			ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapResponse = messageFactory.createMessage(null, bais);
	        SOAPBody soapBody = soapResponse.getSOAPBody();
	        Document bodyDoc = soapBody.extractContentAsDocument();
	        NodeList faults = bodyDoc.getElementsByTagNameNS(SOAP_NAMESPACE, FAULT_ELEMENT);
	        if (faults.getLength() > 0) {
	            Element faultElem = (Element) faults.item(0);
	            String faultString = faultElem.getElementsByTagName(FAULTSTRING2_ELEMENT).item(0).getTextContent();
	            Matcher matcher = FAULT_STRING.matcher(faultString);
	            if (matcher.find()) {
	                String number = matcher.group(1); 
	                InvoiceCommunicationException e = InvoiceCommunicationError.safeValueof(number)
	                	.map(InvoiceCommunicationException::new)
	                	.orElse(null);
	                if (e != null) {
	                	messages.add(e.getMessage());
	                }
	            }
	            messages.add(faultString);
	        } else {
	        	String id = AonNumberUtils.toString(invoiceId);
	        	// SOAP Response to RespuestaRegFactuSistemaFacturacionType
	        	JAXBContext jc = JAXBContext.newInstance(RespuestaRegFactuSistemaFacturacionType.class.getPackage().getName());
	        	Unmarshaller um = jc.createUnmarshaller();
	        	JAXBElement<RespuestaRegFactuSistemaFacturacionType> o = um.unmarshal(bodyDoc, RespuestaRegFactuSistemaFacturacionType.class);
	        	RespuestaRegFactuSistemaFacturacionType r = o.getValue();
	        	AonCollectionUtils.stream(r.getRespuestaLinea())
	        		.filter(l -> AonStringUtils.equals(id, l.getRefExterna()))
	        		.filter(l -> AonStringUtils.isNotEmpty(l.getDescripcionErrorRegistro()))
	        		.forEach(l ->  messages.add(l.getDescripcionErrorRegistro()) );
	        }
	        return messages;
		} catch (SOAPException | JAXBException | IOException e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9004 ,e);
		}		
	}
}
