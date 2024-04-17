package com.esferalia.aon.gwt.fiscal.client.customer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CustomerApi {
	
	private String sessionId;
	
	public CustomerApi(String sessionId) {
		super();
		this.sessionId = sessionId;
	}
	
	public void checkCustomerDomianSync(String host, String endPoint, AsyncCallback<String> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		        	if (response.getStatusCode() == 200) {
		            	String responseBody = response.getText();
		                JSONValue json = JSONParser.parseStrict(responseBody);
		            	
		                if(null == json.isObject().get("customer")) {
		                	callback.onSuccess(null);
		                } else {
			                
			            	String customer = json.isObject().get("customer").isString().stringValue();
			            	String schema = json.isObject().get("schema").isString().stringValue();
			            	String domainName = json.isObject().get("domainName").isString().stringValue();
			            	String domainId = json.isObject().get("domainId").isString().stringValue();
		        			
		        			String result = "Customer: " + customer + ", Schema: " + schema + ", DomainName: " + domainName + ", DomainId: " + domainId;
		        			callback.onSuccess(result);
		                }
		            }
		        }

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
		        }
		    });
		    
		} catch (Exception exception) {
			callback.onFailure(exception);
		}
	}
	
	public void syncSupportAgentCustomer(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	callback.onSuccess(null);
		            }
		        }

				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
		        }
		    });
		} catch (RequestException exception) {
			callback.onFailure(exception);
		}
	}
	
}
