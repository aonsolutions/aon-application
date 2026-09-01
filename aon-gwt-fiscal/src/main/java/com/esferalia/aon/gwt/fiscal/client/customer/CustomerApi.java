package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AonHttpCallback;
import com.esferalia.aon.gwt.common.client.json.ActivitySummaryJSON;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.occam.api.model.customer.CustomerSyncLog;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.http.client.RequestBuilder;
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
	        requestBuilder.sendRequest(null, new AonHttpCallback<String>(callback) {
	            @Override
	            protected String parse(Response response) {
	            	String responseBody = response.getText();
	                JSONValue json = JSONParser.parseStrict(responseBody);
	            	
	                if(null == json.isObject().get("customer")) {
	                	return null;
	                } else {
		                
		            	String customer = json.isObject().get("customer").isString().stringValue();
		            	String schema = json.isObject().get("schema").isString().stringValue();
		            	String domainName = json.isObject().get("domainName").isString().stringValue();
		            	String domainId = json.isObject().get("domainId").isString().stringValue();
	        			
	        			String result = "Customer: " + customer + ", Schema: " + schema + ", DomainName: " + domainName + ", DomainId: " + domainId;
	        			return result;
	                }
	            }
	        });
	    } catch (RequestException exception) {
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
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
		    });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getCustomerActivitySummary(String host, String endPoint, HashMap<String, String> headers, AsyncCallback<List<ActivitySummaryObject>> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host); 
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
	        requestBuilder.sendRequest(null, new AonHttpCallback<List<ActivitySummaryObject>>(callback) {
	            @Override
	            protected List<ActivitySummaryObject> parse(Response response) {
	            	return ActivitySummaryJSON.parseActivitySummaryArr(JSONParser.parseStrict(response.getText()).isArray());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getCustomerActivitySummary(String host, String endPoint, HashMap<String, String> headers, JSONObject body, AsyncCallback<List<ActivitySummaryObject>> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host); 
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
	        requestBuilder.sendRequest(body.toString(), new AonHttpCallback<List<ActivitySummaryObject>>(callback) {
	            @Override
	            protected List<ActivitySummaryObject> parse(Response response) {
	            	return ActivitySummaryJSON.parseActivitySummaryArr(JSONParser.parseStrict(response.getText()).isArray());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
		
	}
	
	public void syncAonCustomerDomain(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
		    });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getAonCustomerDomain(String host, String endPoint, AsyncCallback<Domain> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
	        requestBuilder.sendRequest(null, new AonHttpCallback<Domain>(callback) {
	            @Override
	            protected Domain parse(Response response) {
	            	return DomainCompanyJSON.parseDomainJSON(JSONParser.parseStrict(response.getText()));
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void syncCustomerDomain(String host, String endPoint, HashMap<String, String> headers, JSONObject body, AsyncCallback<CustomerSyncLog> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
	        requestBuilder.sendRequest(body.toString(), new AonHttpCallback<CustomerSyncLog>(callback) {
	            @Override
	            protected CustomerSyncLog parse(Response response) {
	            	JSONObject json = JSONParser.parseStrict(response.getText()).isObject();
	                
	            	return new CustomerSyncLog()
	                		.setCustomerId(cleanQuotes(json.get("customerId").toString()))
	                		.setCustomerName(cleanQuotes(json.get("customerName").toString()))
	                		.setCustomerDocument(cleanQuotes(json.get("customerDocument").toString()))
	                		.setEnterpriseId(cleanQuotes(json.get("enterpriseId").toString()))
	                		.setEnterpriseName(cleanQuotes(json.get("enterpriseName").toString()))
	                		.setEnterpriseDocument(cleanQuotes(json.get("enterpriseDocument").toString()))
	                		.setMessageType(cleanQuotes(json.get("messageType").toString()))
	                		.setMessage(cleanQuotes(json.get("message").toString()));
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getSigIntegrity(String host, String endPoint, JSONObject body, AsyncCallback<List<SigIntegrityRow>> callback) {
	    UrlBuilder urlBuilder = new UrlBuilder();
	    urlBuilder.setProtocol(Window.Location.getProtocol());
	    urlBuilder.setHost(host);
	    urlBuilder.setPath(endPoint);

	    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
	    requestBuilder.setHeader("session_id", sessionId);

	    try {
	        requestBuilder.sendRequest(body.toString(), new AonHttpCallback<List<SigIntegrityRow>>(callback) {
	            @Override
	            protected List<SigIntegrityRow> parse(Response response) {
	                return SigIntegrityRow.parseArray(response.getText());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	private String cleanQuotes(String text) {
	    return AonStringUtils.isNotBlank(text) ? text.replaceAll("\"", "").trim() : "";
	}
}
