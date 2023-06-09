package net.aonsolutions.infovox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;

import org.json.JSONObject;

import net.aonsolutions.infovox.json.OCRErrorJSON;
import net.aonsolutions.infovox.json.OCRResponseJSON;
import net.aonsolutions.infovox.model.OCRError;
import net.aonsolutions.infovox.model.OCRResponse;

public class OCRInvofox {
	private static final String BASE_URL = "https://prod.kinequo.com/backends/midas";
	private static final String DOCUMENTS = BASE_URL + "/documents";
	private static final String DOCUMENT = DOCUMENTS + "/{0}";
	private static final String TOKEN = "$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi";
	
	private static final int STATUS_OK = 200;

	private OCRInvofox() {
	}

	private static OCRResponse get( String url) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					  .uri( new URI( url ) )
					  .header("x-api-key",TOKEN)
					  .GET()
					  .build(); 
			HttpResponse<String> response = HttpClient.newBuilder()
				.build().send(request, BodyHandlers.ofString());
			String resp = response.body();
			JSONObject responseJson = new JSONObject(resp);
			if ( STATUS_OK == response.statusCode()) {
				return OCRResponseJSON.from(responseJson)
					.setHttpCode(response.statusCode());
			} else {
				return new OCRResponse()
					.setError(OCRErrorJSON.from(responseJson))
					.setHttpCode(response.statusCode());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse( e );
		} catch (IOException | URISyntaxException e) {
			return internalErrorResponse( e );
		}
	}
	
	private static OCRResponse internalErrorResponse(Exception e) {
		return new OCRResponse()
			.setHttpCode(500)
			.setError(new OCRError()
				.setCode("INTERNAL")
				.setInfo(e.getMessage()));
	}

	public static OCRResponse getDocument( String documentId ) {
		return get( MessageFormat.format(DOCUMENT,documentId) );
	}

}