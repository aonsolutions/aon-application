package net.aonsolutions.infovox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;
import java.util.function.Supplier;

import org.json.JSONObject;

import net.aonsolutions.infovox.json.OCRCompaniesResponseJSON;
import net.aonsolutions.infovox.json.OCRDocumentResponseJSON;
import net.aonsolutions.infovox.json.OCRDocumentsResponseJSON;
import net.aonsolutions.infovox.json.OCRErrorJSON;
import net.aonsolutions.infovox.model.OCRCompaniesResponse;
import net.aonsolutions.infovox.model.OCRDocumentResponse;
import net.aonsolutions.infovox.model.OCRDocumentsResponse;
import net.aonsolutions.infovox.model.OCRError;
import net.aonsolutions.infovox.model.OCRResponse;

public class OCRInvofox {
	private static final String BASE_URL = "https://prod.kinequo.com/backends/midas";
	private static final String COMPANIES = BASE_URL + "/companies";
	private static final String DOCUMENTS = BASE_URL + "/documents";
	private static final String DOCUMENT = DOCUMENTS + "/{0}";
	private static final String TOKEN = "$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi";

	private static final int STATUS_OK = 200;

	private OCRInvofox() {
	}

	private static HttpResponse<String> get(String url) throws URISyntaxException, IOException, InterruptedException {
		
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header("x-api-key", TOKEN)
			.GET()
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	private static <T extends OCRResponse> T errorResponse(int statusCode,JSONObject responseJson, Supplier<T> supplier) {
		T t = supplier.get(); 
		t.setError(OCRErrorJSON.from(responseJson));
		t.setHttpCode(statusCode);
		return t;
	}

	private static <T extends OCRResponse> T internalErrorResponse(Exception e, Supplier<T> supplier) {
		T resp = supplier.get(); 
		resp.setHttpCode(500);
		resp.setError(new OCRError()
				.setCode("INTERNAL")
				.setInfo(e.getMessage()));
		return resp; 
	}

	public static OCRDocumentResponse getDocument(String documentId) {
		try {
			HttpResponse<String> response = get(MessageFormat.format(DOCUMENT, documentId)); 
			JSONObject responseJson = new JSONObject(response.body());
			if (STATUS_OK != response.statusCode()) {
				return errorResponse(response.statusCode(), responseJson, OCRDocumentResponse::new);
			} 
			return OCRDocumentResponseJSON
				.from(responseJson)
				.setHttpCode(response.statusCode());
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, OCRDocumentResponse::new);
		} catch (IOException | URISyntaxException e) {
			return internalErrorResponse(e, OCRDocumentResponse::new);
		}
	}

	// ******************************************************************
	// *************************************************** [DOCUMENTS] **
	// ******************************************************************
	public static OCRDocumentsResponse getDocuments() {
		try {
			HttpResponse<String> response = get(DOCUMENTS); 
			JSONObject responseJson = new JSONObject(response.body());
			if (STATUS_OK != response.statusCode()) {
				return errorResponse(response.statusCode(), responseJson, OCRDocumentsResponse::new);
			} 
			return OCRDocumentsResponseJSON
				.from(responseJson)
				.setHttpCode(response.statusCode());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, OCRDocumentsResponse::new);
		} catch (IOException | URISyntaxException e) {
			return internalErrorResponse(e, OCRDocumentsResponse::new);
		}
	}

	public static OCRCompaniesResponse getCompanies() {
		try {
			HttpResponse<String> response = get(COMPANIES); 
			JSONObject responseJson = new JSONObject(response.body());
			if (STATUS_OK != response.statusCode()) {
				return errorResponse(response.statusCode(), responseJson, OCRCompaniesResponse::new);
			} 
			return OCRCompaniesResponseJSON
					.from(responseJson)
					.setHttpCode(response.statusCode());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, OCRCompaniesResponse::new);
		} catch (IOException | URISyntaxException e) {
			return internalErrorResponse(e, OCRCompaniesResponse::new);
		}
	}

}