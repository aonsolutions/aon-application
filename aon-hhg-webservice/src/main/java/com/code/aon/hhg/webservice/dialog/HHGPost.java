package com.code.aon.hhg.webservice.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class HHGPost {

	public static JSONObject post(String url, JSONObject json){
		JSONObject object = null;
		try {
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
	 
			if(json != null){
				StringEntity stringEntity = new StringEntity(json.toString());
				post.setEntity(stringEntity);
			}
			
			HttpResponse response = client.execute(post);
			printResponse(response);
			object = new JSONObject(inputStreamToString(response.getEntity().getContent()).toString());
		} catch (UnsupportedOperationException | JSONException | IOException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static JSONObject post(String url) {
		JSONObject object = null;
		try {
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);    
			HttpResponse response = client.execute(post);
			printResponse(response);
			object = new JSONObject(inputStreamToString(response.getEntity().getContent()).toString());
		} catch (UnsupportedOperationException | JSONException | IOException e) {
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
