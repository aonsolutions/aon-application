package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonDateIntervalFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonDateIntervalToJSON;
import com.esferalia.aon.occam.api.model.DateInterval;

public enum DateIntervalJSON {
	NAME(
		(interval, json) -> interval.setName(JsonUtils.getString(json, IJsonNames.NAME)),
		(interval, json) -> json.put(IJsonNames.NAME, interval.getName())
	),
	FROM_DATE(
		(interval, json) -> interval.setStart(JsonUtils.getDate(json, IJsonNames.FROM_DATE)),
		(interval, json) -> JsonUtils.putDate(json, IJsonNames.FROM_DATE, interval.getStart())
	),
	TO_DATE(
		(interval, json) -> interval.setEnd(JsonUtils.getDate(json, IJsonNames.TO_DATE)),
		(interval, json) -> JsonUtils.putDate(json, IJsonNames.TO_DATE, interval.getEnd())
	)
	;
	private IAonDateIntervalFromJSON fromJSON;
	private IAonDateIntervalToJSON toJSON;
	
	private DateIntervalJSON (IAonDateIntervalFromJSON fromJSON, IAonDateIntervalToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(DateInterval interval) {
		JSONObject json = new JSONObject();
		if (interval != null) {
			for (DateIntervalJSON p : DateIntervalJSON.values()) {
				p.toJSON.to(interval, json);
			}
			return json;
		}
		return null;
	}
	
	public static DateInterval fromString (String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}
	
	public static DateInterval fromJSON (JSONObject json) {
		DateInterval interval = new DateInterval();
		if (json != null) {
			for (DateIntervalJSON p : DateIntervalJSON.values()) {
				p.fromJSON.from(interval, json);
			}
			return interval;
		}
		return null;
	}
}
