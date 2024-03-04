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
import net.aonsolutions.invofox.json.OCRDocumentJSON;
import net.aonsolutions.invofox.json.OCRDocumentResponseJSON;
import net.aonsolutions.invofox.json.OCRDocumentsResponseJSON;
import net.aonsolutions.invofox.json.OCRErrorJSON;
import net.aonsolutions.invofox.json.OCRInfoResponseJSON;
import net.aonsolutions.invofox.json.OCRLoginTokenResponseJSON;
import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRCompanyResponse;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRError;
import net.aonsolutions.invofox.model.OCRInfoResponse;
import net.aonsolutions.invofox.model.OCRLoginTokenResponse;
import net.aonsolutions.invofox.model.OCRResponse;
import net.aonsolutions.invofox.model.OCRSeverity;

public class OCRInvofox {

	private static final String APPLICATION_JSON = "application/json";


	private static final int STATUS_OK = 200;


	private static final String API_URL = "api-url";
    	private static final String API_KEY = "x-api-key";
	
	

	private OCRInvofox() {
	}
	
        
        private static String getCompaniesURL(String apiURL) {
            return apiURL + "/companies";
        }
	
        private static String getCompanyURL(String apiURL) {
            return getCompaniesURL(apiURL) + "/{0}";
        }

        private static String getDocumentURL(String apiURL) {
            return getDocumentsURL(apiURL) + "/{0}";
        }

        private static String getOcrInfoURL(String apiURL) {
            return getDocumentURL(apiURL)+ "/ocr";
        }

        private static String getDocumentsURL(String apiURL) {
            return apiURL + "/documents";
        }

        private static String getLoginTokenURL(String apiURL) {
            return apiURL + "/auth/login-token";
        }
        
	
	// ---------------------------------------------------------------------- [POST METHOD]
	private static <T extends OCRResponse> T post(String apiKey, String url, JSONObject postData, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = post(apiKey, url, postData);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> post(String apiKey, String url, JSONObject postData) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(API_KEY, apiKey)
			.header("accept", APPLICATION_JSON)
			.header("Content-Type", APPLICATION_JSON)
			.POST( HttpRequest.BodyPublishers.ofString(postData.toString()) )
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	// ---------------------------------------------------------------------- [DELETE METHOD]
	private static <T extends OCRResponse> T delete(String apiKey, String url, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = delete(apiKey, url);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> delete(String apiKey, String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(API_KEY, apiKey)
			.DELETE()
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}
	// ---------------------------------------------------------------------- [PUT METHOD]
	private static <T extends OCRResponse> T put(String apiKey, String url, JSONObject putData, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = put(apiKey, url, putData);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> put(String apiKey, String url, JSONObject putData) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(API_KEY, apiKey)
			.header("accept", APPLICATION_JSON)
			.header("Content-Type", APPLICATION_JSON)
			.PUT( HttpRequest.BodyPublishers.ofString(putData.toString()) )
			.build();
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
	}

	// ---------------------------------------------------------------------- [GET METHOD]
	private static <T extends OCRResponse> T get(String apiKey, String url, Supplier<T> supplier, Function<JSONObject,T> jsonResponseBuilder) {
		try {
			HttpResponse<String> response = get(apiKey, url);
			return giveBack(response, supplier, jsonResponseBuilder);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return internalErrorResponse(e, supplier);
		} catch (IOException  e) {
			return internalErrorResponse(e, supplier);
		}
	}
	private static HttpResponse<String> get(String apiKey, String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header(API_KEY, apiKey)
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
	
	public static OCRLoginTokenResponse getLoginToken(String apiKey, String apiUrl) {
		return post(apiKey, getLoginTokenURL(apiUrl), new JSONObject(), OCRLoginTokenResponse::new, OCRLoginTokenResponseJSON::from);
	}
	
	// ---------------------------------------------------------------------- [DOCUMENTS]
	public static OCRDocumentsResponse getDocuments(String apiKey, String apiUrl, OCRDocumentsParams params) {
		return get(apiKey, getDocumentsURL(apiUrl) + params.build(), OCRDocumentsResponse::new, OCRDocumentsResponseJSON::from);
	}
	

	public static OCRDocumentsResponse getCompanyInvoices(String apiKey, String apiUrl, String taxId) {
		OCRCompaniesResponse resp = getCompanies(apiKey, apiUrl, OCRCompanyParams.get().withTaxId(taxId));
		Optional<List<OCRCompany>> companiesOpt = resp.getCompanies(); 
		if (resp.getError().isPresent() || !companiesOpt.isPresent()) {
			return resp.copy(new OCRDocumentsResponse());
		}
		return companiesOpt.get().stream()
			.findFirst()
			.map( company -> get(apiKey, getDocumentsURL(apiUrl) + OCRDocumentsParams.get().withCompany(company.getId()).build(), OCRDocumentsResponse::new, OCRDocumentsResponseJSON::from))
			.orElse( resp.copy(new OCRDocumentsResponse()) );
	}
	// ---------------------------------------------------------------------- [DOCUMENT]
	public static OCRDocumentResponse getDocument(String apiKey, String apiUrl, String documentId) {
		return get(apiKey, MessageFormat.format(getDocumentURL(apiUrl), documentId), OCRDocumentResponse::new, OCRDocumentResponseJSON::from);
	}
	
	public static OCRDocumentResponse putDocument(String apiKey, String apiUrl, OCRDocument document) {
		return put(apiKey, MessageFormat.format(getDocumentURL(apiUrl), document.getId().orElseThrow(IllegalArgumentException::new)), OCRDocumentJSON.to(document), OCRDocumentResponse::new, OCRDocumentResponseJSON::from);
	}

	public static OCRDocumentResponse markAsExported(String apiKey, String apiUrl, String documentId) {
		JSONObject putData = new JSONObject();
		putData.put( OCRNames.PUBLIC_STATE, OCRSeverity.exported );
		return put(apiKey, MessageFormat.format(getDocumentURL(apiUrl), documentId), putData, OCRDocumentResponse::new, OCRDocumentResponseJSON::from);
	}
	public static OCRInfoResponse getOcrInfo(String apiKey, String apiUrl, String documentId) {
		return get(apiKey, MessageFormat.format(getOcrInfoURL(apiUrl), documentId), OCRInfoResponse::new, OCRInfoResponseJSON::from);
	}
	// ---------------------------------------------------------------------- [COMPANIES]
	public static OCRCompanyResponse postCompany(String apiKey, String apiUrl, OCRCompany company) {
		return  post(apiKey, getCompaniesURL(apiUrl), OCRCompanyJSON.to(company), OCRCompanyResponse::new, OCRCompanyResponseJSON::from);
	}
	public static OCRResponse deleteCompany(String apiKey, String apiUrl, String companyId) {
		return delete(apiKey, MessageFormat.format(getCompanyURL(apiUrl), companyId), OCRCompanyResponse::new, OCRCompanyResponseJSON::from);
	}
	public static OCRCompaniesResponse getCompanies(String apiKey, String apiUrl, OCRCompanyParams params) {
		return get(apiKey, getCompaniesURL(apiUrl) + params.build(), OCRCompaniesResponse::new, OCRCompaniesResponseJSON::from);
	}
	
	
	public static void main(String[] args) {
//	    System.out.println(OCRCompaniesResponseJSON.to(getCompanies(TEST_X_API_KEY, DEF_API_URL, OCRCompanyParams.get().withTaxId("B01487271"))).toString(1));
//	    System.out.println(OCRDocumentResponseJSON.to(getDocument(TEST_X_API_KEY, DEF_API_URL, "659b530b802d990008d8ecde")).toString(1));
//	    System.out.println(OCRInfoResponseJSON.to(getOcrInfo(TEST_X_API_KEY, DEF_API_URL, "659b530b802d990008d8ecde")).toString(1));
//	    System.out.println(OCRLoginTokenResponseJSON.to(getLoginToken(TEST_API_KEY, DEF_API_URL)).toString(1).toString());
	}
	
}