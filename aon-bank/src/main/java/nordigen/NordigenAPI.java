package nordigen;

import static com.esferalia.aon.watson.server.AonDateUtils.SIMPLE_DATE_FORMAT4;

import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_ACCESS_SCOPES;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenAPI extends NordigenAPIAbstract {
	private static final String SECRET_ID = "f3559685-bbec-45c4-9dfa-f01721e7190e";
	private static final String SECRET_KEY = "35bc90403b8ef02b19cddda29bb58ec97383ed85bffc265736438aaba88fe7cc61b586cb9f3b45eb0327c7a1bc16bff8ba2416db4ff58bc8f24752a02defe133";
	
	//TOKEN RELATED API METHODS
	
	public static JSONObject newAccessToken() throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(SECRET_ID_PARAM, SECRET_ID);
		paramJson.put(SECRET_KEY_PARAM, SECRET_KEY);
		return (JSONObject) post(TOKEN_URL + "new/" , paramJson, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	public static JSONObject refreshAccessToken(String refreshToken) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(REFRESH_PARAM, refreshToken);
		return (JSONObject) post(TOKEN_URL + "refresh/" , paramJson, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	//---------------------------
	
	//INSTITUTION RELATED API METHODS
	
	public static JSONArray getInstitutions(String token, Country country, Boolean paymentsEnabled) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(COUNTRY_PARAM, country != null ? country.getIso2() : null);
		paramJson.put(PAYMENTS_ENABLED_PARAM, paymentsEnabled);
		return  getInstitutions(token, paramJson);
	}
	
	public static JSONArray getInstitutions(String token, JSONObject params) throws NordigenException {
		return  (JSONArray) get(INSTITUTIONS_URL , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getInstitution(String token, String id) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		return  (JSONObject) get(INSTITUTIONS_URL + (AonStringUtils.isBlank(id) ? "null" : id) + "/" , paramJson, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	//-------------------------------
	
	//AGREEMENT RELATED API METHODS
	
	public static JSONObject getEndUserAgreement(String token, String id) throws NordigenException {
		return  (JSONObject) get(AGREEMENTS_URL + "enduser/" + id + "/" , null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getEndUserAgreements(String token, JSONObject params) throws NordigenException {
		return  (JSONObject) get(AGREEMENTS_URL + "enduser/" , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getEndUserAgreements(String token, Integer limit, Integer offset) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(LIMIT_PARAM, limit);
		paramJson.put(OFFSET_PARAM, offset);
		return  getEndUserAgreements(token, paramJson);
	}
	
	public static JSONObject createEndUserAgreement(String token, JSONObject params) throws NordigenException {
		return (JSONObject) post(AGREEMENTS_URL + "enduser/" , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}

	public static JSONObject createEndUserAgreement(String token, Integer maxHistoricalDays, Integer accessValidForDays, NORDIGEN_ACCESS_SCOPES[] accessScopes, String institutionId) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(MAX_HISTORICAL_DAYS_PARAM, maxHistoricalDays);
		paramJson.put(ACCESS_VALID_FOR_DAYS_PARAM, accessValidForDays);
		if (accessScopes != null) {
			JSONArray scopeArray = new JSONArray();
			Arrays.stream(accessScopes).forEach(scope -> scopeArray.put(scope.getValue()));
			paramJson.put(ACCESS_SCOPE_PARAM, scopeArray);
		}
		paramJson.put(INSTITUTION_ID_PARAM, institutionId);
		
		return createEndUserAgreement(token, paramJson);
	}
	
	public static JSONObject deleteEndUserAgreement(String token, String id) throws NordigenException {
		return (JSONObject) delete(AGREEMENTS_URL + "enduser/" + id + "/" , null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	public static JSONObject acceptEndUserAgreement(String token, String id, JSONObject params) throws NordigenException {
		return (JSONObject) put(AGREEMENTS_URL + "enduser/" + id + "/accept/" , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	public static JSONObject acceptEndUserAgreement(String token, String id, String userAgent, String ipAddress) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(USER_AGENT_PARAM, userAgent);
		paramJson.put(IP_ADDRESS_PARAM, ipAddress);
		return (JSONObject) put(AGREEMENTS_URL + "enduser/" + id + "/accept/" , paramJson, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	//-----------------------------
	
	//REQUISITIONS RELATED API METHODS
	
	public static JSONObject getRequisitions(String token, JSONObject params) throws NordigenException {
		return  (JSONObject) get(REQUISITIONS_URL , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getRequisitions(String token, Integer limit, Integer offset) throws NordigenException {
		JSONObject paramJson = new JSONObject();
		paramJson.put(LIMIT_PARAM, limit);
		paramJson.put(OFFSET_PARAM, offset);
		
		return  getRequisitions(token, paramJson);
	}
	
	public static JSONObject createRequisition(String token, JSONObject params) throws NordigenException {
		return  (JSONObject) post(REQUISITIONS_URL , params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
			req.addHeader(CONTENT_TYPE_PARAM, APPLICATION_JSON);
		});
	}
	
	public static JSONObject createRequisition(String token, CreateRequisitionParams params) throws NordigenException {
		JSONObject paramJson = params.buildJson();
		return  createRequisition(token, paramJson);
	}
	
	public static JSONObject getRequisition(String token, String id) throws NordigenException {
		return  (JSONObject) get(REQUISITIONS_URL + id + "/", null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject deleteRequisition(String token, String id) throws NordigenException {
		return  (JSONObject) delete(REQUISITIONS_URL + id + "/", null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	//--------------------------------
	
	//ACCOUNTS RELATED API METHODS
	
	public static JSONObject getAccount(String token, String id) throws NordigenException {
		return  (JSONObject) get(ACCOUNTS_URL + id + "/", null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getBalances(String token, String id) throws NordigenException {
		return  (JSONObject) get(ACCOUNTS_URL + id + "/balances/", null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getDetails(String token, String id) throws NordigenException {
		return  (JSONObject) get(ACCOUNTS_URL + id + "/details/", null, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getTransactions(String token, String id, JSONObject params) throws NordigenException {
		return  (JSONObject) get(ACCOUNTS_URL + id + "/transactions/", params, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	
	public static JSONObject getTransactions(String token, String id, Date dateFrom, Date dateTo) throws NordigenException {
		JSONObject jsonParams = new JSONObject();
		if (dateFrom != null) {
			jsonParams.putOnce(DATE_FROM_PARAM, AonDateUtils.format(dateFrom, SIMPLE_DATE_FORMAT4));
		}
		if (dateTo != null) {
			jsonParams.putOnce(DATE_TO_PARAM, AonDateUtils.format(dateTo, SIMPLE_DATE_FORMAT4));			
		}
		return  (JSONObject) get(ACCOUNTS_URL + id + "/transactions/", jsonParams, req -> {
			req.addHeader(ACCEPT_PARAM, APPLICATION_JSON);
			req.addHeader(AUTHORIZATION_PARAM, BEARER + token);
		});
	}
	//----------------------------
}
