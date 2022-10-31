package com.esferalia.aon.occam.api.json.nordigen;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_ACCESS_SCOPES;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountAmount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenJSONUtils {
	
	private NordigenJSONUtils() throws IllegalAccessException {
		throw new IllegalAccessException("Utility class");
	}
	
	public static Set<Country> countryJSONArrayToSet(JSONArray countriesJSON) {
		Set<Country> countryList = new HashSet<>();
		if (countriesJSON != null) {
			for (int i=0; i<countriesJSON.length(); i++) {
				String countryIso2 = countriesJSON.optString(i, null);
				countryList.add(Country.safeValueOf(countryIso2));
			}
		}
		return countryList;
	}
	
	public static JSONArray countryJSONSetToArray(Set<Country> countriesSet) {
		JSONArray countryArray = new JSONArray();
		if (countriesSet != null) {
			countriesSet.forEach(country -> countryArray.put(Country.safeIso2(country)));
		}
		return countryArray;
	}
	
	public static NORDIGEN_ACCESS_SCOPES[] accessScopesFromJSON(JSONArray scopesJson) {
		if (scopesJson == null) {
			return new NORDIGEN_ACCESS_SCOPES[0];
		}
		int arrLength = scopesJson.length();
		NORDIGEN_ACCESS_SCOPES[] scopesArr = new NORDIGEN_ACCESS_SCOPES[arrLength];
		for (int i=0; i<arrLength; i++) {
			scopesArr[i] = NORDIGEN_ACCESS_SCOPES.getByValue(scopesJson.optString(i));
		}
		return scopesArr;
	}
	
	
	public static JSONArray accessScopesToJSON(NORDIGEN_ACCESS_SCOPES[] scopes) {
		JSONArray jsonArr = new JSONArray();
		if (scopes != null) {
			for (NORDIGEN_ACCESS_SCOPES scope : scopes) {
				if (scope != null) {				
					jsonArr.put(scope.getValue());
				}
			}
		}
		return jsonArr;
	}
	
	public static List<String> jsonStringArrayToList(JSONArray json) {
		List<String> strList = new LinkedList<>();
		if (json != null) {
			for (int i=0; i<json.length(); i++) {
				strList.add(json.optString(i));
			}
		}
		return strList;
	}
	
	public static JSONArray jsonStringArrayFromList(List<String> list) {
		JSONArray json = new JSONArray();
		if (list != null) {
			for (String str : list) {
				json.put(str);
			}
		}
		return json;
	}
	
	public static NordigenAccountAmount accountAmountFromJSON(JSONObject json) {
		if (json != null) {
			Double amount = AonNumberUtils.todouble(json.optString("amount"));
			String currency = json.optString("currency");
			return new NordigenAccountAmount(amount, currency);
		}
		return null;
	}
	
	public static JSONObject accountAmountToJSON(NordigenAccountAmount balanceAmount) {
		if (balanceAmount != null) {
			JSONObject json = new JSONObject();
			json.put("amount", AonNumberUtils.toString(balanceAmount.getAmount()));
			json.put("currency", balanceAmount.getCurrency() != null ? balanceAmount.getCurrency() : null);
			return json;
		}
		return null;
	}
	
	public static NordigenAccessToken accessTokenFromJSON(JSONObject json) {
		NordigenAccessToken token = new NordigenAccessToken();
		if (json != null) {
			token.setAccess(AonStringUtils.trimToNull(json.optString("access")));
			token.setAccessExpires(json.isNull("access_expires") ? null : json.optLong("access_expires"));
			token.setRefresh(AonStringUtils.trimToNull(json.optString("refresh")));
			token.setRefreshExpires(json.isNull("refresh_expires") ? null : json.optLong("refresh_expires"));
		}
		return token;
	}
	
	public static void updateAccessToken(JSONObject json, NordigenAccessToken token) {
		if (token != null) {
			if (!json.isNull("access")) {
				token.setAccess(AonStringUtils.trimToNull(json.optString("access")));				
			}
			if (!json.isNull("access_expires")) {
				token.setAccessExpires(json.isNull("access_expires") ? null : json.optLong("access_expires"));				
			}
			if (!json.isNull("refresh")) {
				token.setRefresh(AonStringUtils.trimToNull(json.optString("refresh")));				
			}
			if (!json.isNull("refresh_expires")) {
				token.setRefreshExpires(json.isNull("refresh_expires") ? null : json.optLong("refresh_expires"));				
			}
		}
	}
}
