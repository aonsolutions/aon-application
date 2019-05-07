package es.translogia.tedi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class TediRequest {
//	public static final String TEDI = "https://europe-west1-tedi-snapshot.cloudfunctions.net";
	public static final String TEDI_SNAPSHOT = "https://europe-west1-tedi-snapshot.cloudfunctions.net";
	public static final String TEDI = "https://europe-west1-tedicenter.cloudfunctions.net";
	
	protected JSONObject getObject(String tediUrl, String token) {
		String response = get(tediUrl, token);
		return response != null ? new JSONObject(response) : new JSONObject();
	}
	
	protected JSONArray getArray(String tediUrl, String token) {
		String response = get(tediUrl, token);
		return response != null ? new JSONArray(response) : new JSONArray();
	}
	
	protected String get(String tediUrl, String token) {
		try {
			URL url = new URL(tediUrl);
		
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("session_id", token);
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			String output;
			String response  = "";
			while ((output = br.readLine()) != null) {
				response = output;
			}
			conn.disconnect();
			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected JSONObject postObject(String tediUrl, String token, String requestData) {
		String response = post(tediUrl, token, requestData);
		return response != null ? new JSONObject(response) : new JSONObject();
	}
	
	protected JSONArray postArray(String tediUrl, String token, String requestData) {
		String response = post(tediUrl, token, requestData);
		return response != null ? new JSONArray(response) : new JSONArray();
	}
	
	protected String post(String tediUrl, String token, String requestData) {
		try {
			URL url = new URL(tediUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
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
			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
}
