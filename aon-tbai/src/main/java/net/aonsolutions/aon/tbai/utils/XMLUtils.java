package net.aonsolutions.aon.tbai.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.Certificate;

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
	
	public static byte[] send(Certificate certificate, String uri, byte[] xml) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
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
