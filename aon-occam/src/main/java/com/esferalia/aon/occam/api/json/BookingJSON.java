package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.BookingResume;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class BookingJSON {
	
	public static LinkedList<Booking> fromJSON(JSONArray json) {
		LinkedList<Booking> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Booking fromJSON(JSONObject json) {
		List<AonApp> apps = new LinkedList<>();
		List<AonApp> parentApps = new LinkedList<>();
		
		JsonUtils.getJSONArray(json, IJsonNames.APPS).forEach(r -> apps.add(AonApp.safeValueOf(r.toString())));
		JsonUtils.getJSONArray(json, IJsonNames.PARENT_APPS).forEach(r -> parentApps.add(AonApp.safeValueOf(r.toString())));
		
		return new Booking()
			.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
			.setCompany(CompanyJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.COMPANY)))
			.setType(DomainType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setApps(apps)
			.setParentApps(parentApps)
			.setNumberOfUsers(JsonUtils.getInt(json, IJsonNames.NUMBER_OF_USERS))
			.setPayer(JsonUtils.getString(json, IJsonNames.PAYER));
	}
	
	public static JSONArray toJSON(List<Booking> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Booking> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Booking object) {
		JSONArray apps = new JSONArray();
		JSONArray parentApps = new JSONArray();
		object.getApps().forEach(r -> apps.put(r.name()));
		object.getParentApps().forEach(r -> parentApps.put(r.name()));

		return new JSONObject()
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(object.getDomain()))
			.put(IJsonNames.COMPANY, CompanyJSON.toJSON(object.getCompany()))
			.put(IJsonNames.TYPE, object.getType() != null ? object.getType().name() : null)
			.put(IJsonNames.APPS, apps) 
			.put(IJsonNames.PARENT_APPS, parentApps) 
			.put(IJsonNames.NUMBER_OF_USERS, object.getNumberOfUsers())
			.put(IJsonNames.PAYER, object.getPayer())
			.put(IJsonNames.RESUME, object.getResume() != null ? bookingResume(object.getResume()): null);
	}
	
	private static JSONObject bookingResume(BookingResume resume) {
		JSONObject o = new JSONObject();
		if(resume != null) { 
			JSONObject apps = new JSONObject();
			resume.getChildApps().keySet().forEach(r -> apps.put(r.name(), resume.getChildApps().get(r)));
			o.put(IJsonNames.APPS, apps);
		
			JSONObject domain = new JSONObject();
			resume.getDomainTypes().keySet().forEach(r -> domain.put(r.name(), resume.getDomainTypes().get(r)));
			o.put(IJsonNames.DOMAIN, domain);

			JSONObject user = new JSONObject();
			resume.getUserTypes().keySet().forEach(r -> user.put(r.name(), resume.getUserTypes().get(r)));
			user.put("childDefinedUsers", resume.getChildDefinedUsers());
			user.put("childBillingUsers", resume.getChildBillingUsers());
			o.put(IJsonNames.USER, user);
		}
		return o;
	}

}
