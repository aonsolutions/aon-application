package es.translogia.tedi.baloo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class TediRequest {
	private static final String TEDI_SNAPSHOT = "https://europe-west1-tedi-snapshot.cloudfunctions.net";
	private static final String TEDI = "https://europe-west1-tedicenter.cloudfunctions.net";
	private static final String LINE_FEED = "\r\n";
	
	public static String getTediURL(boolean snapshot) {
		return snapshot?TEDI_SNAPSHOT:TEDI; 
	}
	
	protected TediResponse get(String tediUrl, String token) {
		HttpsURLConnection conn = null;
		Integer responseCode = null;
		try {
			URL url = new URL(tediUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("session_id", token);

			
			responseCode = conn.getResponseCode();
			
			if (responseCode != null && responseCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				
				String output;	
				String response = "";
				while ((output = br.readLine()) != null) {
					response = output;	
				}
				return new TediResponse(response);
			} else {
				return new TediResponse(conn.getResponseMessage(), conn.getResponseCode());
			}
		} catch (Throwable  e) {
			return new TediResponse(e.getMessage(), responseCode==null?null:responseCode);
		} finally {
			if (conn != null) conn.disconnect();
		}
	}
	protected TediResponse post(String tediUrl, String token, String requestData) {
		HttpsURLConnection conn = null;
		Integer responseCode = null;
		try {
			URL url = new URL(tediUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("session_id", token);

			OutputStream os = conn.getOutputStream();
			os.write(requestData.getBytes());
			os.flush();
			
			responseCode = conn.getResponseCode();
			
			if (responseCode != null && responseCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				
				String output;	
				String response = "";
				while ((output = br.readLine()) != null) {
					response = output;	
				}
				return new TediResponse(response);
			} else {
				return new TediResponse(conn.getResponseMessage(), conn.getResponseCode());
			}
			
		} catch (Throwable  e) {
			e.printStackTrace();
			return new TediResponse(e.getMessage(), responseCode==null?null:responseCode);
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	protected TediResponse postMultipartFile(String tediUrl, String token, InputStream input) {
		HttpsURLConnection conn = null;
		Integer responseCode = null;
		try {
			String fileName = "invoice.pdf"; 
			String charset = "UTF-8";
			String boundary = "===" + System.currentTimeMillis() + "===";
		    
			
			URL url = new URL(tediUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("session_id", token);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			
			OutputStream os = conn.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, charset), true);
			
	        writer
		        .append("--" + boundary).append(LINE_FEED)
		        .append("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" + fileName + "\"").append(LINE_FEED)
		        .append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED)
//		        .append("Content-Transfer-Encoding: base64").append(LINE_FEED)
		        .append(LINE_FEED)
		        .flush();
			
	    	final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			for (int length = 0; ((length = input.read(buffer)) > 0);) {
				os.write(buffer, 0, length);
			}

	        os.flush();
	        writer.append(LINE_FEED).flush();
        	writer.append("--" + boundary + "--").append(LINE_FEED).close();
			os.flush();
			
			responseCode = conn.getResponseCode();
			
			if (responseCode != null && responseCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				
				String outputResponse;	
				String response = "";
				while ((outputResponse = br.readLine()) != null) {
					response = outputResponse;	
				}
				return new TediResponse(response);
			} else {
				return new TediResponse(conn.getResponseMessage(), conn.getResponseCode());
			}
		} catch (Throwable  e) {
			e.printStackTrace();
			return new TediResponse(e.getMessage(), responseCode==null?null:responseCode);
		} finally {
			if (conn != null) conn.disconnect();
		}

	}
	
}
