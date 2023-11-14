package com.esferalia.aon.occam.api.json.nordigen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessScope;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountAmount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenJSONUtils {
	
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_TIME_PATTERN_AUX = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	private static final DateTimeFormatter  DATE_TIME_FORMATTER_AUX = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_AUX);
	
	private NordigenJSONUtils() {
	}
	
	public static Stream<JSONObject> stream(JSONArray array) {
		return IntStream.range(0, array.length())
			.mapToObj(array::getJSONObject);
	}
	
	public static JSONObject getObject(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullGet(json.opt(key)
			, t -> json.optJSONObject(key, null));
	}
	
	public static JSONArray getArray(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullGet(json.opt(key)
			, t -> json.optJSONArray(key));
	}

	public static Integer getInteger(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullGet(json.opt(key)
			, t -> AonNumberUtils.toInteger(json.optNumber(key, null)));
	}
	
	public static Double getDouble(JSONObject json, String key ) {
		if(json == null) return null;
		return AonObjectUtils.ifNotNullGet(json.opt(key)
			, t -> AonNumberUtils.toDouble(json.optNumber(key, null)));
	}

	public static String getString(JSONObject json, String key ) {
		if(json == null) return null;
		return json.optString(key,null);
	}
	
	public static BigDecimal getBigDecimal(JSONObject json, String key ) {
		if(json == null) return null;
		return json.optBigDecimal(key,null);
	}
	
	public static boolean getBoolean(JSONObject json, String key ) {
		if (json != null && AonStringUtils.isNotBlank(getString(json, key))) {
			return Boolean.valueOf( json.optBoolean(key)); 
		}
		return false;
	}
	
	public static Date getDate(JSONObject json, String key ) {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) {
			LocalDate ld = LocalDate.parse( json.optString(key, null), DATE_FORMATTER);
			return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
	public static String formatDate(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
	}
	
	public static Date getDateTime(JSONObject json, String key ) {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) { 
			LocalDateTime ld = LocalDateTime.parse( json.optString(key, null), DATE_TIME_FORMATTER);
			return Date.from(ld.atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
	public static String formatDateTime(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMATTER);
	}
	
	public static Date getDateTimeAux(JSONObject json, String key ) {
		if(json != null && AonStringUtils.isNotBlank(getString(json, key))) { 
			LocalDateTime ld = LocalDateTime.parse( json.optString(key, null), DATE_TIME_FORMATTER_AUX);
			return Date.from(ld.atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
	public static String formatDateTimeAux(Date date) {
		if (date == null) return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMATTER_AUX);
	}

	// ************************************************************
	// ************************************************************
	// ************************************************************
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
	
	public static NordigenAccessScope[] accessScopesFromJSON(JSONArray scopesJson) {
		if (scopesJson == null) {
			return new NordigenAccessScope[0];
		}
		int arrLength = scopesJson.length();
		NordigenAccessScope[] scopesArr = new NordigenAccessScope[arrLength];
		for (int i=0; i<arrLength; i++) {
			scopesArr[i] = NordigenAccessScope.getByValue(scopesJson.optString(i));
		}
		return scopesArr;
	}
	
	
	public static JSONArray accessScopesToJSON(NordigenAccessScope[] scopes) {
		JSONArray jsonArr = new JSONArray();
		if (scopes != null) {
			for (NordigenAccessScope scope : scopes) {
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
	
}
