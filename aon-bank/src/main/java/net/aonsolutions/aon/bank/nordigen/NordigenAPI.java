package net.aonsolutions.aon.bank.nordigen;

import static com.esferalia.aon.watson.server.AonDateUtils.SIMPLE_DATE_FORMAT4;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.ACCEPT_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.ACCESS_SCOPE_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.ACCESS_VALID_FOR_DAYS_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.APPLICATION_JSON;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.AUTHORIZATION_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.BEARER;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.CONTENT_TYPE_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.COUNTRY_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.DATE_FROM_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.DATE_TO_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.INSTITUTION_ID_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.LIMIT_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.MAX_HISTORICAL_DAYS_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.OFFSET_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.PAYMENTS_ENABLED_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.REFRESH_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.SECRET_ID_PARAM;
import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.SECRET_KEY_PARAM;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenAccessTokenJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountBalanceJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountDetailJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountMetadataJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountTransactionsJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAgreementJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAgreementsJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenInstitutionJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenRequisitionJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenRequisitionsJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenResponseJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessScope;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetail;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreements;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenResponse;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.http.AonURIBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

class NordigenAPI {
	private static final Logger LOGGER = Logger.getLogger(NordigenAPI.class.getName()); 
	
	static final String SECRET_ID = "f3559685-bbec-45c4-9dfa-f01721e7190e";
	static final String SECRET_KEY = "35bc90403b8ef02b19cddda29bb58ec97383ed85bffc265736438aaba88fe7cc61b586cb9f3b45eb0327c7a1bc16bff8ba2416db4ff58bc8f24752a02defe133";
	private static final String HOST = "bankaccountdata.gocardless.com";
	private static final String BASE_URL = "https://"+HOST+"/";
	static final String API_URL = "api/v2/";
	static final String REFRESH_URL = "refresh/";
	static final String NEW_URL = "new/";
	static final String ENDUSER_URL = "enduser/";
	static final String ACCOUNTS_URL = "accounts/";
	static final String PREMIUM_URL = "premium/";
	static final String AGREEMENTS_URL = "agreements/";
	static final String INSTITUTIONS_URL = "institutions/";
	static final String PAYMENT_URL = "payment/";
	static final String REQUISITIONS_URL = "requisitions/";
	static final String TOKEN_URL = "token/";
	static final int[] CORRECT_STATUS_CODES = {200, 201};
	
	NordigenAPI() {
	}

	// **********************************************************************************
	// ******************************************************************* [HTTP METHODS]
	// **********************************************************************************

	@FunctionalInterface
	private static interface HeaderSupplier {
		public void addHeaders(HttpRequest.Builder reqBuilder);
	}

	private static <T> T get(String url, JSONObject params, HeaderSupplier headerSupplier, Function<String,T> responseBuilder) throws NordigenException {
		try {
			HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
				.uri( getURI(url, params) );
			if (headerSupplier != null) {
				headerSupplier.addHeaders(reqBuilder);				
			}
			HttpRequest request = reqBuilder.GET().build();
			HttpResponse<String> resp = HttpClient.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString());
			checkResposeStatus(resp);
			return responseBuilder.apply(resp.body());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NordigenException(e.getMessage());
		} catch (IOException  e) {
			throw new NordigenException(e.getMessage());
		}
	}

	private static <T> T post(String url, JSONObject params, HeaderSupplier headerSupplier, Function<String,T> responseBuilder) throws NordigenException {
		try {
			HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
				.uri( URI.create(BASE_URL + API_URL + url) );
			if (headerSupplier != null) {
				headerSupplier.addHeaders(reqBuilder);				
			}
			HttpRequest request = reqBuilder
				.POST( HttpRequest.BodyPublishers.ofString(params.toString()) )
				.build();
			HttpResponse<String> resp = HttpClient.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString());
			checkResposeStatus(resp);
			return responseBuilder.apply(resp.body());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NordigenException(e.getMessage());
		} catch (IOException e) {
			throw new NordigenException(e.getMessage());
		}
	}

	private static <T> T delete(String url, JSONObject params, HeaderSupplier headerSupplier, Function<String,T> responseBuilder) throws NordigenException {
		try {
			HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
				.uri( getURI(url, params) );
			if (headerSupplier != null) {
				headerSupplier.addHeaders(reqBuilder);
			}
			HttpRequest request = reqBuilder.DELETE()
				.build();
			HttpResponse<String> resp = HttpClient.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString());
			checkResposeStatus(resp);
			return responseBuilder.apply(resp.body());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new NordigenException(e.getMessage());
		} catch (IOException  e) {
			throw new NordigenException(e.getMessage());
		}
	}

	private static boolean checkStatus(int statusCode) {
		return !Arrays.stream(CORRECT_STATUS_CODES).anyMatch(code -> code == statusCode);
	}

	private static void checkResposeStatus(HttpResponse<String> resp) {
		if (checkStatus(resp.statusCode())) {
			String errStr = resp.body();					
			if (errStr != null && errStr.charAt(0) == '{') {
				throw new NordigenException(NordigenResponseJSON.from(errStr));
			}
		}
	}
	
	private static URI getURI(String url, JSONObject params) throws NordigenException {
		try {
			AonURIBuilder uriBuilder = new AonURIBuilder(BASE_URL + API_URL + url);
			if (params != null) {
				params.keySet().forEach(key -> uriBuilder.addParameter(key, params.optString(key)));
			}
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new NordigenException(e.getMessage());
		}
	}

	public static boolean isNordigenAvailabilitySocketAlive() {
        boolean isAlive = false;
        int timeout = 2000;
        try (Socket socket = new Socket()) {
        	SocketAddress socketAddress = new InetSocketAddress(HOST, 80);
            socket.connect(socketAddress, timeout);
            isAlive = true;
        } catch (SocketTimeoutException exception) {
        	LOGGER.severe("SocketTimeoutException " + HOST + ":80. " + exception.getMessage() );
        } catch (IOException exception) {
        	LOGGER.severe("IOException - Unable to connect to " + HOST + ":80. " + exception.getMessage());
        }
        return isAlive;
	}
	
	// ************************************************************************************
	// ************************************************************************************
	private static JSONObject getLoginJSON() {
		return new JSONObject()
			.put(SECRET_ID_PARAM, SECRET_ID)
			.put(SECRET_KEY_PARAM, SECRET_KEY);
	}
	private static void fillRequestHeaders(Builder req) {
		req
			.header(ACCEPT_PARAM, APPLICATION_JSON)
			.header(CONTENT_TYPE_PARAM, APPLICATION_JSON);
	}
	
	static NordigenAccessToken newAccessToken() throws NordigenException {
		return post(TOKEN_URL + NEW_URL 
			, getLoginJSON()
			, req -> fillRequestHeaders(req)
			, NordigenAccessTokenJSON::from );
	}
	
	static NordigenAccessToken refreshAccessToken(String refreshToken) throws NordigenException {
		return post(TOKEN_URL + REFRESH_URL 
			, new JSONObject().put(REFRESH_PARAM, refreshToken)
			, req -> fillRequestHeaders(req)
			, NordigenAccessTokenJSON::from );
	}
	
	static NordigenInstitution getInstitution(String token, String id) throws NordigenException {
		return get(INSTITUTIONS_URL + (AonStringUtils.isBlank(id) ? "null" : id) + "/" 
			, new JSONObject()
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenInstitutionJSON::from);
	}

	static NordigenAgreements getEndUserAgreements(String token, Integer limit, Integer offset) throws NordigenException {
		return  getEndUserAgreements(token
			, new JSONObject()
				.put(LIMIT_PARAM, limit)
				.put(OFFSET_PARAM, offset));
	}

	static NordigenAgreements getEndUserAgreements(String token, JSONObject params) throws NordigenException {
		return  get(AGREEMENTS_URL + ENDUSER_URL 
			, params
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenAgreementsJSON::from);
	}
	
	static NordigenAgreement createEndUserAgreement(String token, Integer maxHistoricalDays, Integer accessValidForDays
			,NordigenAccessScope[] accessScopes, String institutionId) throws NordigenException {
		
		JSONObject paramJson = new JSONObject()
			.put(MAX_HISTORICAL_DAYS_PARAM, maxHistoricalDays)
			.put(ACCESS_VALID_FOR_DAYS_PARAM, accessValidForDays)
			.put(INSTITUTION_ID_PARAM, institutionId);
		if (accessScopes != null) {
			JSONArray scopeArray = new JSONArray();
			Arrays.stream(accessScopes).forEach(scope -> scopeArray.put(scope.getValue()));
			paramJson.put(ACCESS_SCOPE_PARAM, scopeArray);
		}
		return createEndUserAgreement(token, paramJson);
	}

	private static NordigenAgreement createEndUserAgreement(String token, JSONObject params) throws NordigenException {
		return post(AGREEMENTS_URL + ENDUSER_URL 
			, params
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
				.header(CONTENT_TYPE_PARAM, APPLICATION_JSON)
			,NordigenAgreementJSON::from);
	}

	static NordigenAgreement getEndUserAgreement(String token, String id) throws NordigenException {
		return  get(AGREEMENTS_URL + ENDUSER_URL + id + "/" 
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenAgreementJSON::from);
	}

	public static NordigenResponse deleteEndUserAgreement(String token, String id) throws NordigenException {
		return delete(AGREEMENTS_URL + ENDUSER_URL + id + "/" 
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
				.header(CONTENT_TYPE_PARAM, APPLICATION_JSON)
			,NordigenResponseJSON::from);
	}

	static List<NordigenInstitution> getInstitutions(String token, Country country, Boolean paymentsEnabled) throws NordigenException {
		return  getInstitutions(token
			, new JSONObject()
				.put(COUNTRY_PARAM, country != null ? country.getIso2() : null)
				.put(PAYMENTS_ENABLED_PARAM, paymentsEnabled));
	}
	
	static List<NordigenInstitution> getInstitutions(String token, JSONObject params) throws NordigenException {
		return  get(INSTITUTIONS_URL 
			, params
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenInstitutionJSON::fromArray);
	}

	static NordigenRequisition createRequisition(String token, RequisitionParams params) throws NordigenException {
		return createRequisition(token, params.toJSON());
	}
	
	private static NordigenRequisition createRequisition(String token, JSONObject params) throws NordigenException {
		return post(REQUISITIONS_URL 
			, params
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
				.header(CONTENT_TYPE_PARAM, APPLICATION_JSON)
			,NordigenRequisitionJSON::from);
	}

	static NordigenRequisition getRequisition(String token, String id) throws NordigenException {
		return get(REQUISITIONS_URL + id + "/"
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenRequisitionJSON::from);
	}
	
	static NordigenResponse deleteRequisition(String token, String id) throws NordigenException {
		return  delete(REQUISITIONS_URL + id + "/"
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenResponseJSON::from);
	}

	static NordigenRequisitions getRequisitions(String token, Integer limit, Integer offset) throws NordigenException {
		return getRequisitions(token
			, new JSONObject()
				.put(LIMIT_PARAM, limit)
				.put(OFFSET_PARAM, offset));
	}

	private static NordigenRequisitions getRequisitions(String token, JSONObject params) throws NordigenException {
		return get(REQUISITIONS_URL 
			, params
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenRequisitionsJSON::from);
	}
	
	static NordigenAccountMetadata getAccountMetadata(String token, String id) throws NordigenException {
		return get(ACCOUNTS_URL + id + "/",
			null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenAccountMetadataJSON::from
		);
	}
	
	static LinkedList<NordigenAccountBalance> getBalances(String token, String id) throws NordigenException {
		return get(ACCOUNTS_URL + id + "/balances/"
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			, NordigenAccountBalanceJSON::fromBalances);
	}
	

	static NordigenAccountDetail getDetail(String token, String id) throws NordigenException {
		return get(ACCOUNTS_URL + id + "/details/"
			, null
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			, NordigenAccountDetailJSON::fromAccount);
	}
	
	static NordigenAccountTransactions getTransactions(String token, String id, Date dateFrom, Date dateTo) throws NordigenException {
		JSONObject jsonParams = new JSONObject();
		if (dateFrom != null) {
			jsonParams.putOnce(DATE_FROM_PARAM, AonDateUtils.format(dateFrom, SIMPLE_DATE_FORMAT4));
		}
		if (dateTo != null) {
			jsonParams.putOnce(DATE_TO_PARAM, AonDateUtils.format(dateTo, SIMPLE_DATE_FORMAT4));			
		}
		return get(ACCOUNTS_URL + id + "/transactions/"
			, jsonParams
			, req -> req
				.header(ACCEPT_PARAM, APPLICATION_JSON)
				.header(AUTHORIZATION_PARAM, BEARER + token)
			,NordigenAccountTransactionsJSON::fromTransactions);
	}

	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************
	// ************************************************************************************

	// ************************************* [OP METHDS]
	

	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	// *******************************************************
	
	
//	@FunctionalInterface
//	private static interface HeaderCallbackPut {
//		public void addHeaders(HttpPut delete);
//	}
	
//	@FunctionalInterface
//	private static interface HeaderCallbackGet {
//		public void addHeaders(HttpGet get);
//	}
	
//	private static Object put(String url, JSONObject params, HeaderCallbackPut headerCallback) throws NordigenException {
//		try (CloseableHttpClient client = HttpClients.createDefault()) {
//			HttpPut put = new HttpPut(BASE_URL + API_URL + url);
//			if (headerCallback != null) {
//				headerCallback.addHeaders(put);				
//			}
//			if (params != null) {
//				StringEntity entity = new StringEntity(params.toString());				
//				put.setEntity(entity);
//			}
//			try (CloseableHttpResponse resp = client.execute(put)) {
//				if (checkStatus(resp.getStatusLine().getStatusCode())) {
//					String errStr = EntityUtils.toString(resp.getEntity());					
//					if (errStr != null && errStr.charAt(0) == '{') {						
//						JSONObject errJson = new JSONObject(errStr);
//						NordigenException.throwNordigenException(errJson);
//					}
//				}
//				if (resp.getEntity() != null) {
//					String str = EntityUtils.toString(resp.getEntity());
//					if (str != null && str.charAt(0) == '{') {						
//						return new JSONObject(str);
//					} else if (str != null && str.charAt(0) == '[') {
//						return new JSONArray(str);
//					}
//				}
//			}
//			return null;
//		} catch (IOException e) {			
//			return null;
//		}
//	}
	
//	private static Object get(String url, JSONObject params, HeaderCallbackGet headerCallback) throws NordigenException {
//		try (CloseableHttpClient client = HttpClients.createDefault()) {
//			URIBuilder uriBuilder = new URIBuilder(BASE_URL + API_URL + url);
//			if (params != null) {
//				params.keySet().forEach(key -> uriBuilder.addParameter(key, params.optString(key)));
//			}
//			URI uri = uriBuilder.build();
//			HttpGet get = new HttpGet(uri);
//			
//			if (headerCallback != null) {
//				headerCallback.addHeaders(get);				
//			}
//			try (CloseableHttpResponse resp = client.execute(get)) {
//				if (checkStatus(resp.getStatusLine().getStatusCode())) {
//					String errStr = EntityUtils.toString(resp.getEntity());					
//					if (errStr != null && errStr.charAt(0) == '{') {						
//						JSONObject errJson = new JSONObject(errStr);
//						NordigenException.throwNordigenException(errJson);
//					}
//				}
//				if (resp.getEntity() != null) {
//					String str = EntityUtils.toString(resp.getEntity());
//					if (str != null && str.charAt(0) == '{') {						
//						return new JSONObject(str);
//					} else if (str != null && str.charAt(0) == '[') {
//						return new JSONArray(str);
//					}
//				}
//			}
//			return null;
//		} catch (IOException | URISyntaxException e) {			
//			return null;
//		}
//	}
	
	//---------------------------
	
	//INSTITUTION RELATED API METHODS
	
	
	//-------------------------------
	
	//AGREEMENT RELATED API METHODS
	
//	private static JSONObject acceptEndUserAgreement(String token, String id, JSONObject params) throws NordigenException {
//		return (JSONObject) put(AGREEMENTS_URL + ENDUSER_URL + id + "/accept/" , params, req -> {
//			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
//			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
//			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
//		});
//	}
	
//	private static JSONObject acceptEndUserAgreement(String token, String id, String userAgent, String ipAddress) throws NordigenException {
//		JSONObject paramJson = new JSONObject();
//		paramJson.put(USER_AGENT_PARAM, userAgent);
//		paramJson.put(IP_ADDRESS_PARAM, ipAddress);
//		return (JSONObject) put(AGREEMENTS_URL + ENDUSER_URL + id + "/accept/" , paramJson, req -> {
//			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
//			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
//			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
//		});
//	}
	

	//-----------------------------
	
	//REQUISITIONS RELATED API METHODS
	
	
	//--------------------------------
	
	//ACCOUNTS RELATED API METHODS
	
	
//	private static JSONObject getTransactions(String token, String id, JSONObject params) throws NordigenException {
//		return  (JSONObject) get(ACCOUNTS_URL + id + "/transactions/", params, req -> {
//			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
//			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
//		});
//	}
//	


}
