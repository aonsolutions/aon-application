package com.esferalia.aon.occam.api.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.BookingResume;
import com.esferalia.aon.occam.api.model.security.DomainTypeInfo;
import com.esferalia.aon.occam.api.model.security.UserType;
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
			.setPayer(JsonUtils.getString(json, IJsonNames.PAYER))
			.setResume(bookingResume(JsonUtils.getJSONObject(json, IJsonNames.RESUME)));
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
			.put(IJsonNames.RESUME, object.getResume() != null ? bookingResumeConsole(object.getResume()): null);
	}
	
	public static JSONObject toJSON(Booking object, Boolean bookingCheck) {
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
			.put(IJsonNames.RESUME, object.getResume() != null ? (null != bookingCheck && bookingCheck ? bookingResume(object.getResume()) : bookingResumeConsole(object.getResume())): null);
	}
	
	private static JSONObject bookingResumeConsole(BookingResume resume) {
		JSONObject o = new JSONObject();
		if(resume != null) { 
			JSONObject domain = new JSONObject();
			resume.getDomainTypes().keySet().forEach(r ->{
				DomainTypeInfo dti = resume.getDomainTypes().get(r);
				JSONObject oa = new JSONObject();
				oa.put("number", dti.getNumber());
				
				JSONObject apps = new JSONObject();
				dti.getChildApps().keySet().forEach(r2 -> apps.put(r2.name(), dti.getChildApps().get(r2)));
				oa.put(IJsonNames.APPS, apps);

				oa.put("childs", childJson(dti.getChilds()));

				domain.put(r.name(), oa);	
			});
			o.put(IJsonNames.DOMAIN, domain);

			JSONObject user = new JSONObject();
			resume.getUserTypes().keySet().forEach(r -> user.put(r.name(), resume.getUserTypes().get(r)));
			user.put("childDefinedUsers", resume.getChildDefinedUsers());
			user.put("childBillingUsers", resume.getChildBillingUsers());
			
			o.put(IJsonNames.USER, user);
		}
		return o;
	}
	
	private static JSONArray childJson(List<Domain> childs) {
		JSONArray array = new JSONArray();
		childs.stream().forEach(d -> {
			JSONArray apps = new JSONArray();
			d.getApps().stream().forEach(app -> apps.put(app.getApp().name()));
			JSONObject json = new JSONObject();
			json.put(IJsonNames.ID, d.getId());
			json.put(IJsonNames.NAME, d.getName());
			json.put(IJsonNames.DESCRIPTION, d.getDescription());
			json.put(IJsonNames.APPS, apps);
			json.put(IJsonNames.MAX_DEFINED_USERS, d.getMaxDefinedUsers());
			array.put(json);
		});
		return array;
	}
	
	private static JSONObject bookingResume(BookingResume resume) {
		JSONObject resumeObj = new JSONObject();
		
		if(resume != null) { 
			
			// Childs
			JSONArray childsArr = new JSONArray();
			if(null != resume.getChilds())
				resume.getChilds().forEach(childDomain -> childsArr.put(DomainJSON.toJSON(childDomain)));
			resumeObj.put("childs", childsArr);
			
			// ChildApps
			JSONObject childAppObj = new JSONObject();
			if(null != resume.getChildApps())
				resume.getChildApps().entrySet().forEach(entry -> childAppObj.put(entry.getKey().name(), entry.getValue()));
			resumeObj.put("childApps", childAppObj);
			
			// UserTypes
			JSONObject userTypesObj = new JSONObject();
			if(null != resume.getUserTypes())
				resume.getUserTypes().entrySet().forEach(entry -> userTypesObj.put(entry.getKey().name(), entry.getValue()));
			resumeObj.put("userTypes", userTypesObj);
			
			// ChildDefinedUsers
			resumeObj.put("childDefinedUsers", resume.getChildDefinedUsers());
			
			// ChildBillingUsers
			resumeObj.put("childBillingUsers", resume.getChildBillingUsers());
			
			// ChildBillingUsers
			resumeObj.put("totalChilds", resume.getTotalChilds());
			
			// DomainTypes
			JSONObject domainTypesObj = new JSONObject();
			if(null != resume.getDomainTypes())
				resume.getDomainTypes().entrySet().forEach(entry -> domainTypesObj.put(entry.getKey().name(), domainTypeInfoJson(entry.getValue())));
			resumeObj.put("domainTypes", domainTypesObj);
		}
		
		return resumeObj;
	}
	
	private static BookingResume bookingResume(JSONObject json) {
		BookingResume bookingResume = new BookingResume();
		
		if(!json.isEmpty()) {
			
			// Childs
			JSONArray childsArr = JsonUtils.getJSONArray(json, "childs");
			bookingResume.setChilds(DomainJSON.fromJSON(childsArr));
			
			// ChildApps
			HashMap<AonApp, Long> childApps = new  HashMap<AonApp, Long>();
			JSONObject childAppsObj = JsonUtils.getJSONObject(json, "childApps");
			AonApp[] aonApps = AonApp.values();
			for (Integer j = 0; j < aonApps.length; j++) {
				AonApp aonApp = aonApps[j];
				Integer aonAppLong = JsonUtils.getInteger(childAppsObj, aonApp.name());
				if(null != aonAppLong) childApps.put(aonApp, aonAppLong.longValue());
			}
			bookingResume.setChildApps(childApps);
			
			// UserTypes
			HashMap<UserType, Long> userTypes = new  HashMap<UserType, Long>();
			JSONObject userTypesObj = JsonUtils.getJSONObject(json, "userTypes");
			UserType[] userTypesValues = UserType.values();
			for (Integer j = 0; j < userTypesValues.length; j++) {
				UserType userType = userTypesValues[j];
				Integer userTypeLong = JsonUtils.getInteger(userTypesObj, userType.name());
				if(null != userTypeLong) userTypes.put(userType, userTypeLong.longValue());
			}
			bookingResume.setUserTypes(userTypes);
			
			// ChildDefinedUsers
			bookingResume.setChildDefinedUsers(JsonUtils.getInteger(json, "childDefinedUsers"));
			
			// ChildBillingUsers
			bookingResume.setChildBillingUsers((JsonUtils.getInteger(json, "childBillingUsers")));
			
			// TotalChilds
			bookingResume.setTotalChilds((JsonUtils.getInteger(json, "totalChilds")));
			
			// DomainTypes
			HashMap<DomainType, DomainTypeInfo> domainTypes = new  HashMap<DomainType, DomainTypeInfo>();
			JSONObject domainTypesObj = JsonUtils.getJSONObject(json, "domainTypes");
			DomainType[] domainTypesValues = DomainType.values();
			for (Integer j = 0; j < domainTypesValues.length; j++) {
				DomainType domainType = domainTypesValues[j];
				JSONObject domainTypeInfo = JsonUtils.getJSONObject(domainTypesObj, domainType.name());
				if(null != domainTypeInfo) domainTypes.put(domainType, domainTypeInfoJson(domainTypeInfo));
			}
			bookingResume.setDomainTypes(domainTypes);
			
		}
		
		return bookingResume;
		
	}
	
	private static DomainTypeInfo domainTypeInfoJson(JSONObject domainTypeInfoJson) {
		DomainTypeInfo domainTypeInfo = new DomainTypeInfo();
		
		if(null != domainTypeInfoJson) {
			
			// Number
			domainTypeInfo.setNumber(JsonUtils.getInteger(domainTypeInfoJson, "number"));
			
			// ChildApps
			HashMap<AonApp, Long> childApps = new  HashMap<AonApp, Long>();
			JSONObject childAppsObj = JsonUtils.getJSONObject(domainTypeInfoJson, "childApps");
			AonApp[] aonApps = AonApp.values();
			for (Integer j = 0; j < aonApps.length; j++) {
				AonApp aonApp = aonApps[j];
				Integer aonAppLong = JsonUtils.getInteger(childAppsObj, aonApp.name());
				if(null != aonAppLong) childApps.put(aonApp, aonAppLong.longValue());
			}
			domainTypeInfo.setChildApps(childApps);
			
			// Childs
			JSONArray childsArr = JsonUtils.getJSONArray(domainTypeInfoJson, "childs");
			domainTypeInfo.setChilds(DomainJSON.fromJSON(childsArr));
		}
		
		return domainTypeInfo;
	}

	private static JSONObject domainTypeInfoJson(DomainTypeInfo domainTypeInfo) {
		JSONObject domainTypeInfoObj = new JSONObject();
		
		if(null != domainTypeInfo) {
			
			// Number
			domainTypeInfoObj.put("number", domainTypeInfo.getNumber());
			
			// ChildApps
			JSONObject childAppObj = new JSONObject();
			if(null != domainTypeInfo.getChildApps())
				domainTypeInfo.getChildApps().entrySet().forEach(entry -> childAppObj.put(entry.getKey().name(), entry.getValue()));
			domainTypeInfoObj.put("childApps", childAppObj);
			
			// Childs
			JSONArray childsArr = new JSONArray();
			if(null != domainTypeInfo.getChilds())
				domainTypeInfo.getChilds().forEach(childDomain -> childsArr.put(DomainJSON.toJSON(childDomain)));
			domainTypeInfoObj.put("childs", childsArr);
		}
		
		return domainTypeInfoObj;
	}
	
//	private static JSONArray childJson(List<Domain> childs) {
//		JSONArray array = new JSONArray();
//		childs.stream().forEach(d -> {
//			JSONArray apps = new JSONArray();
//			d.getApps().stream().forEach(app -> apps.put(app.getApp().name()));
//			JSONObject json = new JSONObject();
//			json.put(IJsonNames.ID, d.getId());
//			json.put(IJsonNames.NAME, d.getName());
//			json.put(IJsonNames.DESCRIPTION, d.getDescription());
//			json.put(IJsonNames.APPS, apps);
//			json.put(IJsonNames.MAX_DEFINED_USERS, d.getMaxDefinedUsers());
//			array.put(json);
//		});
//		return array;
//	}

}
