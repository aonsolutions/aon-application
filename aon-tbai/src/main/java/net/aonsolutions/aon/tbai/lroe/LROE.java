package net.aonsolutions.aon.tbai.lroe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jdk.internal.org.jline.utils.InputStreamReader;
import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE {
	
	public static TbaiResponse send(TbaiConfiguration tbaiConfiguration, JSONObject json, byte[] xml, String sign) {
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

			System.out.println(https.getResponseCode());
			System.out.println(https.getResponseMessage());
			System.out.println(https.getContentType());
			System.out.println(https.getContentLength());
			TbaiResponse response = new TbaiResponse();
			for (String key2 : https.getHeaderFields().keySet()) {
				System.out.println(key2 + ": " + https.getHeaderField(key2));
			} 
			
 
			InputStream respons = https.getInputStream();
			byte[] bytes = respons.readAllBytes();
			byte[] a = decompress(bytes);
			try {
				Document d = XMLUtils.getDocument(a);
				System.out.println(XMLUtils.documentToString(d));
			} catch (ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
			return response;
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
}
