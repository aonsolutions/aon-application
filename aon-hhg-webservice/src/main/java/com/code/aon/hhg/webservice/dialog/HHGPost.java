package com.code.aon.hhg.webservice.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class HHGPost {
	private static final String TLS = "TLS";
	private static final String HTTPS = "https";
	
	public static JSONObject post(String url, JSONObject json) {
		JSONObject object = null;
		try {
			HttpClientBuilder base = HttpClientBuilder.create();
			SSLContext ctx = SSLContext.getInstance(TLS);
			X509TrustManager tm = new X509TrustManager() {
			    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

			    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

			    public X509Certificate[] getAcceptedIssuers() {
			        return null;
			    }
			};
			
			ctx.init(null, new TrustManager[]{tm}, null);
	
		    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(ctx);
		     
		    base.setSSLSocketFactory(sslConnectionFactory);
		    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
		            .register(HTTPS, sslConnectionFactory)
		            .build();
		    HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
		    base.setConnectionManager(ccm);
		    
		    HttpClient client = base.build();
			HttpPost post = new HttpPost(url);  
		
			if(json != null){
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("json", json.toString()));
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
			}
			
			HttpResponse response = client.execute(post);
			printResponse(response);
			object = new JSONObject(inputStreamToString(response.getEntity().getContent()).toString());
		} catch (UnsupportedOperationException | JSONException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static JSONObject post2(String url, JSONObject json) {
		JSONObject object = null;
		try {
			HttpClientBuilder base = HttpClientBuilder.create();
			SSLContext ctx = SSLContext.getInstance(TLS);
			X509TrustManager tm = new X509TrustManager() {
			    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

			    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

			    public X509Certificate[] getAcceptedIssuers() {
			        return null;
			    }
			};
			
			ctx.init(null, new TrustManager[]{tm}, null);
		    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(ctx, new HostnameVerifier(){

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
		    	
		    });
		    
		    base.setSSLSocketFactory(sslConnectionFactory);
		    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
		            .register(HTTPS, sslConnectionFactory)
		            .build();
		    HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
		    base.setConnectionManager(ccm);
		    
		    HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		    
		    
		    HttpClient client = base.build();
			HttpPost post = new HttpPost(url);    
			
			if(json != null){
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("json", json.toString()));
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
			}
			
			HttpResponse response = client.execute(post);
			printResponse(response);
			object = new JSONObject(inputStreamToString(response.getEntity().getContent()).toString());
		} catch (UnsupportedOperationException | JSONException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		return object;
	}

	private static void printResponse(HttpResponse response) {
		System.out.println("");
		System.out.println("****************************************");
		System.out.println("");
		System.out.println(response.toString());
		System.out.println("");
		System.out.println("****************************************");
	}	
	private static StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuilder;
	}
	
}
