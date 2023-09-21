package net.aonsolutions.invofox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONObject;

import net.aonsolutions.invofox.json.OCRCompaniesResponseJSON;
import net.aonsolutions.invofox.json.OCRCompanyJSON;
import net.aonsolutions.invofox.json.OCRCompanyResponseJSON;
import net.aonsolutions.invofox.json.OCRDocumentResponseJSON;
import net.aonsolutions.invofox.json.OCRDocumentsResponseJSON;
import net.aonsolutions.invofox.json.OCRErrorJSON;
import net.aonsolutions.invofox.json.OCRLoginTokenResponseJSON;
import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRCompanyResponse;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRError;
import net.aonsolutions.invofox.model.OCRLoginTokenResponse;
import net.aonsolutions.invofox.model.OCRResponse;
import net.aonsolutions.invofox.model.OCRSeverity;

public class OCRInvofox {
	private static final String X_API_KEY = "x-api-key";
	
	private static final String BASE_URL = "https://prod.kinequo.com/backends/midas";
	private static final String LOGIN_TOKEN = BASE_URL + "/auth/login-token";
	private static final String COMPANIES = BASE_URL + "/companies";
	private static final String COMPANY = COMPANIES + "/{0}";
	private static final String DOCUMENTS = BASE_URL + "/documents";
	private static final String DOCUMENT = DOCUMENTS + "/{0}";
	private static final String TOKEN = "$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi";

	private static final int STATUS_OK = 200;

	private OCRInvofox() {
	}

	// ---------------------------------------------------------------------- [POST METHOD]
	private static <T extends OCRResponse> T post(String url, JSONObject postData, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = post(url, postData);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> post(String url, JSONObject postData) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(X_API_KEY, TOKEN)
			.header("accept", "application/json")
			.header("Content-Type", "application/json")
			.POST( HttpRequest.BodyPublishers.ofString(postData.toString()) )
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	// ---------------------------------------------------------------------- [DELETE METHOD]
	private static <T extends OCRResponse> T delete(String url, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = delete(url);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> delete(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(X_API_KEY, TOKEN)
			.DELETE()
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}
	// ---------------------------------------------------------------------- [PUT METHOD]
	private static <T extends OCRResponse> T put(String url, JSONObject putData, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = put(url, putData);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> put(String url, JSONObject putData) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(X_API_KEY, TOKEN)
			.header("accept", "application/json")
			.header("Content-Type", "application/json")
			.PUT( HttpRequest.BodyPublishers.ofString(putData.toString()) )
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	// ---------------------------------------------------------------------- [GET METHOD]
	private static <T extends OCRResponse> T get(String url, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = get(url);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> get(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(X_API_KEY, TOKEN)
			.GET()
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	private static <T extends OCRResponse> T internalErrorResponse(Exception e, Supplier<T> supplier) {
		T resp = supplier.get(); 
		resp.setHttpCode(500);
		resp.setError(new OCRError()
			.setCode("INTERNAL")
			.setInfo(e.getMessage()));
		return resp; 
	}

	private static <T extends OCRResponse> T giveBack(HttpResponse<String> response, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		JSONObject responseJson = new JSONObject(response.body());
		T t = null;
		if (STATUS_OK != response.statusCode()) {
			t = supplier.get(); 
			t.setError(OCRErrorJSON.from(responseJson));
		} else {
			t = jsonResponseBuilder.apply(responseJson);
		}
		t.setHttpCode(response.statusCode());
		return t; 
	}

	// ---------------------------------------------------------------------- [LOGIN TOKEN]
	
	public static OCRLoginTokenResponse getLoginToken() {
		return post(LOGIN_TOKEN, new JSONObject(), OCRLoginTokenResponse::new, OCRLoginTokenResponseJSON::from);
	}
	
	// ---------------------------------------------------------------------- [DOCUMENTS]
	public static OCRDocumentsResponse getDocuments(OCRDocumentsParams params) {
		return get(DOCUMENTS + params.build(), OCRDocumentsResponse::new, OCRDocumentsResponseJSON::from);
	}
	
	public static OCRDocumentsResponse getCompanyInvoices(String taxId) {
		OCRCompaniesResponse resp = getCompanies(OCRCompanyParams.get().withTaxId(taxId));
		Optional<List<OCRCompany>> companiesOpt = resp.getCompanies(); 
		if (resp.getError().isPresent() || !companiesOpt.isPresent()) {
			return resp.copy(new OCRDocumentsResponse());
		}
		return companiesOpt.get().stream()
			.findFirst()
			.map( company -> get(DOCUMENTS + OCRDocumentsParams.get().withCompany(company.getId()).build(), OCRDocumentsResponse::new, OCRDocumentsResponseJSON::from))
			.orElse( resp.copy(new OCRDocumentsResponse()) );
	}
	// ---------------------------------------------------------------------- [DOCUMENT]
	public static OCRDocumentResponse getDocument(String documentId) {
		return get(MessageFormat.format(DOCUMENT, documentId), OCRDocumentResponse::new, OCRDocumentResponseJSON::from);
	}
	public static OCRDocumentResponse markAsExported(String documentId) {
		JSONObject putData = new JSONObject();
		putData.put( OCRNames.PUBLIC_STATE, OCRSeverity.exported );
		return put(MessageFormat.format(DOCUMENT, documentId), putData, OCRDocumentResponse::new, OCRDocumentResponseJSON::from);
	}
	// ---------------------------------------------------------------------- [COMPANIES]
	public static OCRCompanyResponse postCompany(OCRCompany company) {
		return  post(COMPANIES, OCRCompanyJSON.to(company), OCRCompanyResponse::new, OCRCompanyResponseJSON::from);
	}
	public static OCRResponse deleteCompany(String companyId) {
		return delete(MessageFormat.format(COMPANY, companyId), OCRCompanyResponse::new, OCRCompanyResponseJSON::from);
	}
	public static OCRCompaniesResponse getCompanies(OCRCompanyParams params) {
		return get(COMPANIES + params.build(), OCRCompaniesResponse::new, OCRCompaniesResponseJSON::from);
	}
	
}