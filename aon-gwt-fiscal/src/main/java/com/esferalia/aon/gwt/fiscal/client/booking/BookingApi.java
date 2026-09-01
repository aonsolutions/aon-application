package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AonHttpCallback;
import com.esferalia.aon.gwt.common.client.json.BookingJSON;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.http.client.RequestBuilder;
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
	        requestBuilder.sendRequest(null, new AonHttpCallback<Booking>(callback) {
	            @Override
	            protected Booking parse(Response response) {
	            	return BookingJSON.parseBookingJSON(JSONParser.parseStrict(response.getText()).isObject());
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
	        requestBuilder.sendRequest(null, new AonHttpCallback<Booking>(callback) {
	            @Override
	            protected Booking parse(Response response) {
	            	return BookingJSON.parseBookingJSON(JSONParser.parseStrict(response.getText()).isObject());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
		
	}
	
	public void exportCustomerBookingResume(String host, String endPoint, HashMap<String, String> headers, AsyncCallback<Void> callback) {
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
			requestBuilder.sendRequest(null, new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
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
	        requestBuilder.sendRequest(null, new AonHttpCallback<List<Booking>>(callback) {
	            @Override
	            protected List<Booking> parse(Response response) {
	                return BookingJSON.parseBookingJSONArr(response.getText());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getDomainCompanies(String host, String endPoint, AsyncCallback<List<DomainCompany>> callback) {
	    UrlBuilder urlBuilder = new UrlBuilder();
	    urlBuilder.setProtocol(Window.Location.getProtocol());
	    urlBuilder.setHost(host);
	    urlBuilder.setPath(endPoint);

	    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
	    requestBuilder.setHeader("session_id", sessionId);

	    try {
	        requestBuilder.sendRequest(null, new AonHttpCallback<List<DomainCompany>>(callback) {
	            @Override
	            protected List<DomainCompany> parse(Response response) {
	                return DomainCompanyJSON.parseDomainCompanyJSONArr(response.getText());
	            }
	        });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void getDomainCompanies(String host, String endPoint, JSONObject body, AsyncCallback<List<DomainCompany>> callback) {
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
	        requestBuilder.sendRequest(body.toString(), new AonHttpCallback<List<DomainCompany>>(callback) {
	            @Override
	            protected List<DomainCompany> parse(Response response) {
	                return DomainCompanyJSON.parseDomainCompanyJSONArr(response.getText());
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
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
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
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
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
	        requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Response>(callback) {
	            @Override
	            protected Response parse(Response response) {
	                return response;
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
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
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
			requestBuilder.sendRequest(null, new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
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
	        requestBuilder.sendRequest(null, new AonHttpCallback<List<User>>(callback) {
	            @Override
	            protected List<User> parse(Response response) {
	                return DomainCompanyJSON.parseUsersJSONArr(response.getText());
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
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", sessionId);
		
		try {
			requestBuilder.sendRequest(body.toString(), new AonHttpCallback<Void>(callback) {
		        @Override protected Void parse(Response response) { return null; }
		    });
	    } catch (RequestException exception) {
	        callback.onFailure(exception);
	    }
	}
	
	public void createBookingUser(String host, String endPoint, JSONObject body, AsyncCallback<Void> callback) {
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
	
}
