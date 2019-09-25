package es.translogia.tedi.baloo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TediRequest {
	private static final String TEDI_SNAPSHOT = "https://europe-west1-tedi-snapshot.cloudfunctions.net";
	private static final String TEDI = "https://europe-west1-tedicenter.cloudfunctions.net";
	
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
	/*
	protected JSONArray postArray(String tediUrl, String token, String requestData) {
		TediResponse tediResponse = post(tediUrl, token, requestData);
		String response = tediResponse.getContent();
		return response != null ? new JSONArray(response) : new JSONArray();
	}
	*/
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
	/*
	protected JSONObject put(String tediUrl, String token, String requestData) {
		try {
			URL url = new URL(tediUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("session_id", token);

			OutputStream os = conn.getOutputStream();
			os.write(requestData.getBytes());
			os.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			String output;	
			String response = "";
			while ((output = br.readLine()) != null) {
				response = output;	
			}
			conn.disconnect();
			return new JSONObject(response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected JSONObject delete(String tediUrl, String token) {
		try {
			URL url = new URL(tediUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("session_id", token);

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			String output;	
			String response = "";
			while ((output = br.readLine()) != null) {
				response = output;	
			}
			conn.disconnect();
			return new JSONObject(response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
}
