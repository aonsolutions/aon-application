package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.responses.ResponseHandler.HandleStatusCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
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
import java.util.Date;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.exceptions.response.TbaiResponseException;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import ticketbai.emision.TicketBai;

public class TbaiMain {

	
	private TbaiMain() {
	
	}

	public static void createEmisionTBAI(Company company, Invoice invoice, TbaiConfiguration tbaiConfiguration) throws StatusCodeException, TbaiResponseException, JAXBException {
		TbaiBlockchain blockchain = TbaiData.getBlockchain(company.getDomain(), new User().setLogin(""));
		final TicketBai tbai = Invoice2tbai.build(company, invoice, blockchain); 
			
		final JAXBContext jaxbContext = JAXBContext.newInstance( TicketBai.class );
		final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	 		
			
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
						
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal( tbai, bos );
		byte[] data = bos.toByteArray();
//		InputStream doc = new ByteArrayInputStream(data);
		
		TbaiResponse response = sendXML(tbaiConfiguration, 
				TbaiSign.sign(tbaiConfiguration.getCertificate(), data));		

		TbaiBlockchain bc = new TbaiBlockchain()
				.setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
				.setNumber(Integer.toString(invoice.getNumber()))
				.setSerie(invoice.getSeries())
				.setSignature(response.getSign().substring(0, 100));
		
		DataRequest request = TbaiData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, data);
		
		TbaiData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, response, bc, request);
	}
	
	public static TbaiResponse sendXML(TbaiConfiguration tbaiConfiguration, byte[] xml) throws StatusCodeException, TbaiResponseException {
		URL url;
		try {
			Document doc = getDocument(xml);
			String sign = doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
			
			ByteArrayInputStream key = new ByteArrayInputStream(tbaiConfiguration.getCertificate().getCertificate());	
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, tbaiConfiguration.getCertificate().getPassword().toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, tbaiConfiguration.getCertificate().getPassword().toCharArray());
   	        
            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			SSLContext.setDefault(sslContext);
          
			url = new URL(TbaiUri.getUrlEmision(tbaiConfiguration));
			URLConnection con = url.openConnection();
			HttpsURLConnection https = (HttpsURLConnection)con;
			
	        https.setHostnameVerifier(new TrustAllHosts());
	        https.setRequestMethod("POST"); 
			https.setRequestProperty("Content-Type", "application/xml; charset=utf-8;");
			https.setDoOutput(true);
			https.setDoInput(true);
			
			OutputStream os = https.getOutputStream();
			os.write(xml);
			os.close();
			
			System.out.println("\n\tServer status: \t" + https.getResponseCode() + ": " + https.getResponseMessage());			
			System.out.println("\tMethod used: \t" + https.getRequestMethod());
			System.out.println("\tEncoding used: \t" + https.getRequestProperty("Content-Type"));
			
			HandleStatusCode(https.getResponseCode());
			
			System.out.println("\n\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\t SERVICE RESPONSE: ");
			System.out.println("\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			
			InputStream response = (InputStream) https.getContent();
			byte[] bytes = response.readAllBytes();
//			HandleTbaiResponse(bytes);			
			return getTbaiResponse(bytes, sign);
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return new TbaiResponse().setDescription(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new TbaiResponse().setDescription(e.getMessage());
		}catch (KeyStoreException e) {
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
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
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
	private static Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}
	
	private static TbaiResponse getTbaiResponse(byte[] bytes, String sign) {
		try {
			System.out.println("\t Parsing XML response.... ");
			
			InputStream is = new ByteArrayInputStream(bytes);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			System.out.println("\t XML version: \t " + doc.getXmlVersion());

			
			String idTbai;
			try{idTbai = doc.getElementsByTagName("IdentificadorTBAI").item(0).getTextContent();}
			catch(Exception e) {idTbai = null;}
			
			String estado;
			try{estado = doc.getElementsByTagName("Estado").item(0).getTextContent();}
			catch(Exception e) {estado = null;}
			
			String fecha_str;
			try{fecha_str = doc.getElementsByTagName("FechaRecepcion").item(0).getTextContent();}
			catch(Exception e) {fecha_str = null;}
			
			String descripcion;
			try{descripcion = doc.getElementsByTagName("Descripcion").item(0).getTextContent();}
			catch(Exception e) {descripcion = null;}
			
			String descripcion_eus;
			try{descripcion_eus = doc.getElementsByTagName("Azalpena").item(0).getTextContent();}
			catch(Exception e) {descripcion_eus = null;}
			
			String validation_code;
			try{validation_code = doc.getElementsByTagName("Codigo").item(0).getTextContent();}
			catch(Exception e) {validation_code = null;}
			
			String validation_desc;
			try{validation_desc = doc.getElementsByTagName("Descripcion").item(1).getTextContent();}
			catch(Exception e) {validation_desc = null;}
			
			String validation_desc_eus;
			try{validation_desc_eus = doc.getElementsByTagName("Azalpena").item(1).getTextContent();}
			catch(Exception e) {validation_desc_eus = null;}
						
			System.out.println(toString(doc));

			return new TbaiResponse()
					.setSign(sign)
					.setTbaiId(idTbai)
					.setStatus(Integer.parseInt(estado))
					.setDescription(descripcion)
					.setDescriptionEUS(descripcion_eus)
					.setReceptionDate(AonDateUtils.parse(fecha_str, "dd-MM-yyyy hh:mm:ss"))
					.setValidationCode(Integer.parseInt(validation_code))
					.setValidationDescription(validation_desc)
					.setValidationDescriptionEUS(validation_desc_eus)
					.setOk(idTbai != null)
					.setData(bytes);

		} catch(IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String toString(Document doc) {
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
	
	public static void createAnulacionTBAI(){/*TO DO uwu*/}
}
