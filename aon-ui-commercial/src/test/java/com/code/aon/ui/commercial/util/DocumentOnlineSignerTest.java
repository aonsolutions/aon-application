package com.code.aon.ui.commercial.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;


public class DocumentOnlineSignerTest {

	
	static final String URL_ECERTIA = "https://app.ecertia.com/api/json/reply/EviSignSubmit";
	static final String URL_EVICERTIA = "https://app.evicertia.com/api/json/reply/EviSignSubmit";
	
	private String username;
	private String password;
	private Integer signingType;
	private String url;
	
	public void init() {
		// Test Url by default
		url = URL_ECERTIA;
		loadParams();
	}
	
	private void loadParams() {
		username = "jgarcia@aonsolutions.es";
		password = "945121010";
		signingType = (1);
	}
	
	public void sendData() throws Exception {
		String json = ""
				+ "{"
				+ "\"lookupKey\":\"Evisign\","
				+ "\"subject\":\"FIRMA\","
				+ "\"document\":\"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\","
				+ "\"options\":"
					+ "{"
					+ "\"pushNotificationUrl\":\"http://cau.aonsolutions.net/offer\","
					+ "\"pushNotificationFilter\":[\"EviSignSigned\",\"EviSignRejected\"]"
					+ "},"
				+ "\"signingParties\":"
					+ "["
					+ "{\"address\":\"eagirrezabal@aonsolutions.es\",\"role\":\"Signer\",\"name\":\"test rDirStaff\",\"signingMethod\":\"EmailPin\"},"
					+ "{\"address\":\"eagirrezabal@aonsolutions.es\",\"role\":\"Reviewer\",\"name\":\"TEST\",\"signingMethod\":\"WebClick\"}"
					+ "]"
				+ "}"
				;
		
		JSONObject responseJson = postObject(json.toString());
		if(responseJson.opt("uniqueId") != null) {
			//com.esferalia.aon.occam.api.model.management.Offer of = AON.getOffer(AonUtil.getDomainName(), offer.getDomain(), "", f -> f.getIdProperty().eq(offer.getId()));
//			of.setExternalReference( responseJson.getString("uniqueId"));
//			AON.updateOffer(AonUtil.getDomainName(), of.getDomain(), "", of);
//			AonUtil.addInfoMessage("Response ID: "+responseJson.getString("uniqueId"));
			System.out.println("Response ID: "+responseJson.getString("uniqueId"));
		} else {
//			AonUtil.addInfoMessage("No response obtained...");
		}
	}
	
	

	protected JSONObject postObject(String requestData) {
		String response = post(requestData);
		return response != null ? new JSONObject(response) : new JSONObject();
	}
	
	protected String post(String requestData) {
		try {
			URL my_url = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) my_url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			String encoding = new String(Base64.getEncoder().encode((username + ":" + password).getBytes())); 
			conn.setRequestProperty("Authorization", "Basic " + encoding);
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		DocumentOnlineSignerTest signer = new DocumentOnlineSignerTest();
		try {
			signer.init();
			signer.sendData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}