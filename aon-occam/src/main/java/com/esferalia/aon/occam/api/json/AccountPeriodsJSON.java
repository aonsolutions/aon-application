package com.esferalia.aon.occam.api.json;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.BiConsumer;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.AccountPeriod;

public class AccountPeriodsJSON {
	
	public static BiConsumer<Collection<AccountPeriod>, JSONArray> PERIODS_TOJSON = (periods, json) -> {
		periods.forEach(period -> json.put(AccountPeriodJSON.toJSON(period)));
	};
	
	public static BiConsumer<Collection<AccountPeriod>, JSONArray> PERIODS_FROMJSON = (periods, json) -> {
		for (int i=0; i<json.length(); i++) {
			periods.add(AccountPeriodJSON.fromJSON(json.optJSONObject(i)));
		}
	};
	
	
	
	
	
	public static JSONArray toJSON(Collection<AccountPeriod> periods) {
		JSONArray json = new JSONArray();
		
		PERIODS_TOJSON.accept(periods, json);
		return json;
	}
	
	public static Collection<AccountPeriod> fromJSON(JSONArray json) {
		Collection<AccountPeriod> periods = new LinkedList<AccountPeriod>();
		PERIODS_FROMJSON.accept(periods, json);
		return Collections.unmodifiableCollection(periods);
	}

	public static Collection<AccountPeriod> fromString(String text) {
		JSONArray json = new JSONArray(text);
		return fromJSON(json);
	}
	
	
}
