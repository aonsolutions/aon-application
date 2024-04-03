package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.gwt.common.client.json.BookingJSON;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BookingApi {
	
	private String sessionId;
	
	public BookingApi(String sessionId) {
		super();
		this.sessionId = sessionId;
	}

	public void getBooking(String host, String endPoint, AsyncCallback<Booking> callback) {
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
		            	String domainBookingJson = response.getText();
		            	Booking booking = BookingJSON.parseBookingJSON(JSONParser.parseStrict(domainBookingJson).isObject());
		            	callback.onSuccess(booking);
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
	
	public void getBooking(String host, String endPoint, HashMap<String, String> headers, AsyncCallback<Booking> callback) {
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
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String domainBookingJson = response.getText();
		            	Booking booking = BookingJSON.parseBookingJSON(JSONParser.parseStrict(domainBookingJson).isObject());
		            	callback.onSuccess(booking);
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
	
	public void getCustomerBooking(String host, String endPoint, Integer customerId, AsyncCallback<List<Booking>> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host); 
		urlBuilder.setPath(endPoint);
		
		urlBuilder.setParameter("customer", customerId.toString());
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String responseBody = response.getText();
		            	List<Booking> bookingList = BookingJSON.parseBookingJSONArr(responseBody);
		            	callback.onSuccess(bookingList);
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
	
	public void getDomainCompanies(String host, String endPoint, AsyncCallback<List<DomainCompany>> callback) {
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
		                List<DomainCompany> domainCompanies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
		                callback.onSuccess(domainCompanies);
		                
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

	public void syncDomainCustomer(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
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
	
	public void unsyncDomainCustomer(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
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

	public void syncCustomerBooking(String host, String endPoint, HashMap<String, String> headers, JSONObject body, AsyncCallback<Response> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host); 
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	callback.onSuccess(response);
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
	
	public void unsyncCustomerBooking(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host); 
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.DELETE, urlBuilder.buildString());
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

	public void enableDomainRemote(String host, String endPoint, HashMap<String, String> headers, AsyncCallback<Void> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
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

	public void getDomainUsers(String host, String endPoint, HashMap<String, String> headers, AsyncCallback<List<User>> callback) {
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
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String responseBody = response.getText();
		            	List<User> users = DomainCompanyJSON.parseUsersJSONArr(responseBody);
		            	callback.onSuccess(users);
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
