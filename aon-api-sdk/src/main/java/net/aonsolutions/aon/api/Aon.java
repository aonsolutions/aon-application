package net.aonsolutions.aon.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;

class Aon {
    
    static final String SESSION_ID = "session_id";
    static final String DOMAIN_NAME = "domain_name";
    static final String DOMAIN_LOGIN = "domain_login";
    static final String AON_API_URL = "https://%s/%s";
    /**
     * private static final String AON_API_URL = "http://%s:8080/aon-aio/%s";
     */
    static final String PREDEFINED_TOKEN = "AONd95770f269e711eb94390242ac130002";  

    static String get(String domainName, String userLogin, String path, Map<String, ?> params) throws URISyntaxException, IOException, InterruptedException {
	
	Map<String, Object> requestParams = new HashMap<>();
	requestParams.putAll(params);
	requestParams.put(IJsonNames.USER, userLogin);
	requestParams.put(IJsonNames.DOMAIN_NAME, domainName);
	
	String query = requestParams.entrySet().stream()
	.map(e -> String.format("%s=%s", e.getKey() , URLEncoder.encode(e.getValue().toString(), Charset.defaultCharset() ) ))
	.collect(Collectors.joining("&")) ;
	  
	HttpRequest httpRequest = HttpRequest
	.newBuilder(new URI(String.format(AON_API_URL + "?%s", domainName, path, query)))
	.setHeader(DOMAIN_NAME, domainName)
	.setHeader(DOMAIN_LOGIN, userLogin)
	.setHeader(SESSION_ID, PREDEFINED_TOKEN)
	.setHeader("Accept", "application/json")
    	.build();
	
	HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
	
	checkResponse(response);
	
	return response.body();
    }

    static JSONObject postJSON(String domainName, String userLogin, String path, JSONObject jsonObject)
	    throws URISyntaxException, IOException, InterruptedException {
	HttpRequest httpRequest = HttpRequest
	.newBuilder(new URI(String.format(AON_API_URL, domainName, path)))
	.setHeader(DOMAIN_NAME, domainName)
	.setHeader(DOMAIN_LOGIN, userLogin)
	.setHeader(SESSION_ID, PREDEFINED_TOKEN)
	.setHeader("Accept", "application/json")
	.setHeader("Content-Type", "application/json")
	.POST(BodyPublishers.ofString(jsonObject.toString()))
	.build();

	HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
	
	checkResponse(response);
	
	String body  = response.body();
	return new JSONObject(body);
    }
    
    static CompletableFuture<HttpResponse<String>> postJSONAsync(String domainName, String userLogin, String path, JSONObject jsonObject)
	    throws URISyntaxException {
	HttpRequest httpRequest = HttpRequest
	.newBuilder(new URI(String.format(AON_API_URL, domainName, path)))
	.setHeader(DOMAIN_NAME, domainName)
	.setHeader(DOMAIN_LOGIN, userLogin)
	.setHeader(SESSION_ID, PREDEFINED_TOKEN)
	.setHeader("Accept", "application/json")
	.setHeader("Content-Type", "application/json")
	.POST(BodyPublishers.ofString(jsonObject.toString()))
	.build();

	return HttpClient.newHttpClient().sendAsync(httpRequest, BodyHandlers.ofString());
    }
    
    private static void checkResponse(HttpResponse<String> response) throws IOException {
	int statusCode = response.statusCode();
	// info reponses 
	if ( statusCode >= 100 && statusCode <= 199 ) {
	    return;
	}
	// successful reponses 
	if ( statusCode >= 200 && statusCode <= 299 ) {
	    return;
	}
	// redirection messages
	if ( statusCode >= 200 && statusCode <= 299 ) {
	    return;
	}
	// client error messages 
	if ( statusCode >= 400 && statusCode <= 499 ) {
	    throw new IOException(getMessage(response));
	}
	// server error messages
	if ( statusCode >= 500  && statusCode <= 599 ) {
	    throw new IOException(getMessage(response));
	}
	 
    }
    
    static String getMessage(HttpResponse<String> response ) {
	return new JSONObject(response.body()).getString("message");
    }
    
}