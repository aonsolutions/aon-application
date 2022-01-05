package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE {
	
	protected static final String LROE = "LROE";
	
	public static LROEResponse send(TbaiConfiguration tbaiConfiguration, JSONObject json, byte[] xml) {
		JSONObject responseJSON = new JSONObject();
		URL url;
		try {
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
			https.setRequestProperty("Accept-Encoding", "gzip");
			https.setRequestProperty("Content-Encoding", "gzip");
			https.setRequestProperty("Content-Length", Integer.toString(xml.length));
			https.setRequestProperty("Content-Type", "application/octet-stream");
			https.setRequestProperty("eus-bizkaia-n3-version", "1.0");
			https.setRequestProperty("eus-bizkaia-n3-content-type", "application/xml");
			https.setRequestProperty("eus-bizkaia-n3-data", json.toString());
			https.setDoOutput(true);
			https.setDoInput(true);
			
			OutputStream os = https.getOutputStream();
			os.write(xml);
			os.close();

			responseJSON.put("responseCode", https.getResponseCode());
			responseJSON.put("responseMessage", https.getResponseMessage());
			responseJSON.put("responseContentType", https.getContentType());
			responseJSON.put("responseContentLength", https.getContentLength());
			for (String key2 : https.getHeaderFields().keySet()) {
				if(key2 != null)
					responseJSON.put(key2, https.getHeaderField(key2));
			} 
			
			byte[] responseData = null;
			try {
				InputStream respons = https.getInputStream();
				byte[] bytes = respons.readAllBytes();
				responseData = decompress(bytes);
				Document d = XMLUtils.getDocument(responseData);
				System.out.println(XMLUtils.documentToString(d));
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
	
	public static byte[] decompress(byte[] file) {
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
	
	public static byte[] toGzip(byte[] data) throws IOException {
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
	
	protected static LROEResponse error(Exception e) {
		e.printStackTrace();
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("error", true);
		responseJSON.put("errorMessage", e.getMessage());
		return new LROEResponse(responseJSON);
	}
}
