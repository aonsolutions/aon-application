package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final String LROE = "LROE";
	protected static final String DATE_FORMAT = "dd-MM-yyyy";
	
	public LROEResponse send(TbaiConfiguration tbaiConfiguration, JSONObject json, byte[] xml) {
		JSONObject responseJSON = new JSONObject();
		URL url;
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(tbaiConfiguration.getCertificate().getData());	
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, tbaiConfiguration.getCertificate().getPassword().toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, tbaiConfiguration.getCertificate().getPassword().toCharArray());
   	        
            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			SSLContext.setDefault(sslContext);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
           
			String uri = TbaiUri.getUrlEmision(tbaiConfiguration);
			url = new URL(uri);
			System.out.println("***** REQUEST *****");
			System.out.println("[POST] " + uri);
			System.out.println(json.toString());
			String contentLength = Integer.toString(xml.length);
			System.out.println("Content-Length: " + contentLength);
			
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();

	        https.setHostnameVerifier(new TrustAllHosts());
	        https.setRequestMethod("POST"); 
			https.setRequestProperty("Accept-Encoding", "gzip");
			https.setRequestProperty("Content-Encoding", "gzip");
			https.setRequestProperty("Content-Length", contentLength);
			https.setRequestProperty("Content-Type", "application/octet-stream");
			https.setRequestProperty("eus-bizkaia-n3-version", "1.0");
			https.setRequestProperty("eus-bizkaia-n3-content-type", "application/xml");
			https.setRequestProperty("eus-bizkaia-n3-data", json.toString());
			
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setUseCaches(false);
			for( String str : https.getRequestProperties().keySet()) {
				System.out.println(str + ": " + https.getRequestProperty(str));
			}

			
			OutputStream os = https.getOutputStream();
			os.write(xml);
			os.close();

			responseJSON.put("responseCode", https.getResponseCode());
			System.out.println(https.getResponseCode());
			responseJSON.put("responseMessage", https.getResponseMessage());
			System.out.println(https.getResponseMessage());
			responseJSON.put("responseContentType", https.getContentType());
			responseJSON.put("responseContentLength", https.getContentLength());
			for (String key2 : https.getHeaderFields().keySet()) {
				if(key2 != null) {
					responseJSON.put(key2, https.getHeaderField(key2));
					System.out.println( key2 + " - " + https.getHeaderField(key2));
				}
			} 
			
			byte[] responseData = null;
			try {
				InputStream respons = https.getInputStream();
				byte[] bytes = respons.readAllBytes();
				responseData = decompress(bytes);
				if(responseData != null) {
					Document d = XMLUtils.getDocument(responseData);
					System.out.println(XMLUtils.documentToString(d));
					String status = d.getElementsByTagName("EstadoRegistro").item(0).getTextContent();
					boolean error = "incorrecto".equalsIgnoreCase(status);
					responseJSON.put("error", error);
					if(error) {
						String errorCode = d.getElementsByTagName("CodigoErrorRegistro").item(0).getTextContent();
						String errorMessage = d.getElementsByTagName("DescripcionErrorRegistroES").item(0).getTextContent();

						responseJSON.put("errorMessage", errorCode + " - " + errorMessage);
					}
				}
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
			return new LROEResponse(responseJSON, responseData);
		} catch (Exception e) {
			e.printStackTrace();
			responseJSON.put("error", true);
			responseJSON.put("errorMessage", e.getMessage());
			return new LROEResponse(responseJSON);
		}
	}
	
	public LROEResponse sendConsulta(TbaiConfiguration tbaiConfiguration, JSONObject json, byte[] xml) {
		JSONObject responseJSON = new JSONObject();
		URL url;
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(tbaiConfiguration.getCertificate().getData());	
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, tbaiConfiguration.getCertificate().getPassword().toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   			kmf.init(keyStore, tbaiConfiguration.getCertificate().getPassword().toCharArray());
   	        
            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			SSLContext.setDefault(sslContext);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
           
			String uri = TbaiUri.getUrlConsulta(tbaiConfiguration);
			url = new URL(uri);
			System.out.println("***** REQUEST *****");
			System.out.println("[POST] " + uri);
			System.out.println(json.toString());
			String contentLength = Integer.toString(xml.length);
			System.out.println("Content-Length: " + contentLength);
			
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();

	        https.setHostnameVerifier(new TrustAllHosts());
	        https.setRequestMethod("POST"); 
			https.setRequestProperty("Accept-Encoding", "gzip");
			https.setRequestProperty("Content-Encoding", "gzip");
			https.setRequestProperty("Content-Length", contentLength);
			https.setRequestProperty("Content-Type", "application/octet-stream");
			https.setRequestProperty("eus-bizkaia-n3-version", "1.0");
			https.setRequestProperty("eus-bizkaia-n3-content-type", "application/xml");
			https.setRequestProperty("eus-bizkaia-n3-data", json.toString());
			
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setUseCaches(false);
			for( String str : https.getRequestProperties().keySet()) {
				System.out.println(str + ": " + https.getRequestProperty(str));
			}

			
			OutputStream os = https.getOutputStream();
			os.write(xml);
			os.close();

			responseJSON.put("responseCode", https.getResponseCode());
			System.out.println(https.getResponseCode());
			responseJSON.put("responseMessage", https.getResponseMessage());
			System.out.println(https.getResponseMessage());
			responseJSON.put("responseContentType", https.getContentType());
			responseJSON.put("responseContentLength", https.getContentLength());
			for (String key2 : https.getHeaderFields().keySet()) {
				if(key2 != null) {
					responseJSON.put(key2, https.getHeaderField(key2));
					System.out.println( key2 + " - " + https.getHeaderField(key2));
				}
			} 
			
			byte[] responseData = null;
			try {
				InputStream respons = https.getInputStream();
				byte[] bytes = respons.readAllBytes();
				responseData = decompress(bytes);
				if(responseData != null) {
					Document d = XMLUtils.getDocument(responseData);
					System.out.println(XMLUtils.documentToString(d));
				}
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
			return new LROEResponse(responseJSON, responseData);
		} catch (Exception e) {
			e.printStackTrace();
			responseJSON.put("error", true);
			responseJSON.put("errorMessage", e.getMessage());
			return new LROEResponse(responseJSON);
		}
	}
	
	private class TrustAllCertificates implements X509TrustManager {
	    public void checkClientTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public void checkServerTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	}
	
	private class TrustAllHosts implements HostnameVerifier {
	    public boolean verify(String hostname, SSLSession session) {
	        return true;
	    }
	}
	
	public byte[] decompress(byte[] file) {
	         byte[] buffer = new byte[1024];
	        try
	        {
	            GZIPInputStream is = 
	                    new GZIPInputStream(new ByteArrayInputStream(file));
	                      
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	              
	            int totalSize;
	            while((totalSize = is.read(buffer)) > 0 )
	            {
	                out.write(buffer, 0, totalSize);
	            }
	              
	            out.close();
	            is.close();
	              
	            return out.toByteArray();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	          
	    }
	
	public byte[] toGzip(byte[] data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		GZIPOutputStream gzipStream = new GZIPOutputStream(baos);
		try {
			gzipStream.write(data);
		} finally {
			baos.close();
			gzipStream.close();
		}
		return baos.toByteArray();
	}
	
	protected LROEResponse error(Exception e) {
		e.printStackTrace();
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("error", true);
		responseJSON.put("errorMessage", e.getMessage());
		return new LROEResponse(responseJSON);
	}
	
	@Deprecated
	protected Object unmarshal(Class clazz, String response) throws JAXBException {
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		JAXBElement o = (JAXBElement) unmar.unmarshal(new StringReader(response));
		return o.getValue();
	}

	protected byte[] marshall(Class clazz, Object object) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(object, bos);
		return bos.toByteArray();
	}
	
	protected Object unmarshall(Class clazz, String response) throws JAXBException {
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		return unmar.unmarshal(new StringReader(response));
	}
	
	public Integer getEjercicio(TbaiConfiguration tbaiConfiguration, Invoice invoice) {
		Date ejercicioDate = new Date(); 
		if(invoice.isSales()) {
			ejercicioDate = invoice.ensureFiscal().getExpDate() != null ? invoice.getFiscal().getExpDate() : invoice.getIssueDate();
		} else {
			ejercicioDate = tbaiConfiguration.isRegistryTaxDate()
					? invoice.getTaxDate() : invoice.getCreationDate();
			if(ejercicioDate.before(invoice.getIssueDate()))
				ejercicioDate = invoice.getIssueDate();
		}
		return AonDateUtils.getYear(ejercicioDate);
	}
}
