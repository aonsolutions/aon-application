package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class SeriesJSON {
	
	private SeriesJSON() {
	}
	
	public static Series from(String json) {
		return from(new JSONObject(json));
	}

	public static List<Series> from(JSONArray json) {
		LinkedList<Series> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(from(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Series from(JSONObject json) {
		return new Series()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInt(json, IJsonNames.DOMAIN))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setCode(JsonUtils.getString(json, IJsonNames.CODE))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setTas(JsonUtils.getboolean(json, IJsonNames.TAS))
			.setOffer(JsonUtils.getboolean(json, IJsonNames.OFFER))
			.setSales(JsonUtils.getboolean(json, IJsonNames.SALES))
			.setDelivery(JsonUtils.getboolean(json, IJsonNames.DELIVERY))
			.setInvoice(JsonUtils.getboolean(json, IJsonNames.INVOICE))
			.setRectification(JsonUtils.getboolean(json, IJsonNames.RECTIFICATION))
			.setPos(JsonUtils.getboolean(json, IJsonNames.POS))
			.setSecurityLevel( SecurityLevel.safeValueOf( JsonUtils.getInteger(json,IJsonNames.SECURITY_LEVEL) ));
	}
	
	public static JSONArray to(List<Series> list) {
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Series> series) {
		JSONArray array = new JSONArray();
		series.forEach( s -> array.put(to(s)));
		return array;
	}
	
	public static JSONObject to(Series series) {
		return new JSONObject()
			.putOpt(IJsonNames.ID, series.getId())
			.putOpt(IJsonNames.DOMAIN, series.getDomain())
			.putOpt(IJsonNames.DESCRIPTION, series.getDescription())
			.putOpt(IJsonNames.SCOPE, series.getScope())
			.putOpt(IJsonNames.CODE, series.getCode())
			.put(IJsonNames.ACTIVE, series.isActive())
			.put(IJsonNames.TAS, series.isTas())
			.put(IJsonNames.OFFER, series.isOffer())
			.put(IJsonNames.SALES, series.isSales())
			.put(IJsonNames.DELIVERY, series.isDelivery())
			.put(IJsonNames.INVOICE, series.isInvoice())
			.put(IJsonNames.RECTIFICATION, series.isRectification())
			.put(IJsonNames.POS, series.isPos())
			.putOpt(IJsonNames.SECURITY_LEVEL, series.getSecurityLevel()==null?null:series.getSecurityLevel().ordinal())
		;
	}

}
