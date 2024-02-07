package com.esferalia.aon.gwt.common.client.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.BookingResume;
import com.esferalia.aon.occam.api.model.security.DomainTypeInfo;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class BookingJSON {
	
	public static List<Booking> parseBookingJSONArr(String json) {
		JSONArray arr = JSONParser.parseStrict(json).isArray();
		LinkedList<Booking> list = new LinkedList<>();
		
		for(Integer i = 0; i < arr.size(); i++) {
			list.add(parseBookingJSON(arr.get(i).isObject()));
		}
 		
		return list;
	}
	
	public static Booking parseBookingJSON(JSONObject json) {
		List<AonApp> apps = new LinkedList<>();
		List<AonApp> parentApps = new LinkedList<>();
		
		for(int i=0; i<JsonGWTUtils.getJSONArray(json, IJsonNames.APPS).size(); i++) {
			String aonAppStr = JsonGWTUtils.getJSONArray(json, IJsonNames.APPS).get(i).toString();
			aonAppStr = AonStringUtils.containsIgnoreCase(aonAppStr, "\"") ? aonAppStr.replace("\"", "") : aonAppStr;
			apps.add(AonApp.safeValueOf(aonAppStr));
		}
		
		for(int i=0; i<JsonGWTUtils.getJSONArray(json, IJsonNames.PARENT_APPS).size(); i++)
			parentApps.add(AonApp.safeValueOf(JsonGWTUtils.getJSONArray(json, IJsonNames.PARENT_APPS).get(i).toString()));
		
		return new Booking()
			.setDomain(parseDomainJSON(JsonGWTUtils.getJSONObject(json, IJsonNames.DOMAIN)))
			.setCompany(parseCompanyJSON(JsonGWTUtils.getJSONObject(json, IJsonNames.COMPANY)))
			.setType(DomainType.safeValueOf(JsonGWTUtils.getString(json, IJsonNames.TYPE)))
			.setApps(apps)
			.setParentApps(parentApps)
			.setNumberOfUsers(JsonGWTUtils.getInteger(json, IJsonNames.NUMBER_OF_USERS))
			.setPayer(JsonGWTUtils.getString(json, IJsonNames.PAYER))
			.setResume(bookingResume(JsonGWTUtils.getJSONObject(json, IJsonNames.RESUME)));
	}

	private static Domain parseDomainJSON(JSONValue json) {
		if(json == null) return new Domain();
		
		JSONObject jsonObj = json.isObject();
		
		return new Domain()
			.setId(JsonGWTUtils.getInteger(jsonObj,IJsonNames.ID))
			.setName(JsonGWTUtils.getString(jsonObj, IJsonNames.NAME))
			.setDescription(JsonGWTUtils.getString(jsonObj, IJsonNames.DESCRIPTION))
			.setOwner(JsonGWTUtils.getString(jsonObj, IJsonNames.OWNER))
			.setParentId(JsonGWTUtils.getInteger(jsonObj, IJsonNames.PARENT_ID))
			.setDomainType( DomainType.safeValueOf( JsonGWTUtils.getString(jsonObj,IJsonNames.DOMAIN_TYPE) ))
			.setEnableHeredity(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ENABLE_HEREDITY))
			.setDomainManagement(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ACTIVE))
			.setScope(JsonGWTUtils.getInteger(jsonObj,IJsonNames.SCOPE))
			.setMaxDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonGWTUtils.getString(jsonObj, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.EXPIRATION_DATE))
			.setCreationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.CREATION_USER))
			.setCreationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.MODIFICATION_DATE))
			.setAonCustomer(JsonGWTUtils.getInteger(jsonObj, IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.AON_STATUS)))
		;
	}

	private static Company parseCompanyJSON(JSONValue json) {
		if(json == null) return new Company();
		
		JSONObject jsonObj = json.isObject();
		
		return new Company()
				.copy(parseRegistryJSON(jsonObj))
				.setActive(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.ACTIVE))
				.setSurcharge(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.SURCHARGE))
				.setWithholding(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.WITHHOLDING))
				.setVatAccrualPayment(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.VAT_ACCRUAL_PAYMENT))
				.seteInvoice(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.E_INVOICE));
	}
	
	private static Registry parseRegistryJSON(JSONObject jsonObj) {
		if(jsonObj == null) {
			return new Registry();
		}
		
		return new Registry() 
			.setId(JsonGWTUtils.getInteger(jsonObj, IJsonNames.ID))
			.setDomain(parseDomainJSON(JsonGWTUtils.getJSONObject(jsonObj, IJsonNames.DOMAIN)))
			.setDocument(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT))
			.setDocumentCountry(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_COUNTRY)))
			.setDocumentType(DocumentType.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_TYPE)))
			.setName(JsonGWTUtils.getString(jsonObj,IJsonNames.NAME))
			.setAlias(JsonGWTUtils.getString(jsonObj,IJsonNames.ALIAS))
			.setLegalPerson(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.LEGAL_PERSON))
			.setNationality(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.NATIONALITY)))
			.setConfidential(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.CONFIDENTIAL))
			.setGlobal(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.GLOBAL) == null ? false : JsonGWTUtils.getBoolean(jsonObj, IJsonNames.GLOBAL))
			.setDirty(JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DIRTY) == null ? false : JsonGWTUtils.getBoolean(jsonObj, IJsonNames.DIRTY));
	}
	
	private static BookingResume bookingResume(JSONObject json) {
		
		BookingResume bookingResume = new BookingResume();
		
		if(null != json) {
			
			// Childs
			JSONArray childsArr = JsonGWTUtils.getJSONArray(json, "childs");
			bookingResume.setChilds(domainFromJSON(childsArr));
			
			// ChildApps
			HashMap<AonApp, Long> childApps = new  HashMap<AonApp, Long>();
			JSONObject childAppsObj = JsonGWTUtils.getJSONObject(json, "childApps");
			AonApp[] aonApps = AonApp.values();
			for (Integer j = 0; j < aonApps.length; j++) {
				AonApp aonApp = aonApps[j];
				Integer aonAppLong = JsonGWTUtils.getInteger(childAppsObj, aonApp.name());
				if(null != aonAppLong) childApps.put(aonApp, aonAppLong.longValue());
			}
			bookingResume.setChildApps(childApps);
			
			// UserTypes
			HashMap<UserType, Long> userTypes = new  HashMap<UserType, Long>();
			JSONObject userTypesObj = JsonGWTUtils.getJSONObject(json, "userTypes");
			UserType[] userTypesValues = UserType.values();
			for (Integer j = 0; j < userTypesValues.length; j++) {
				UserType userType = userTypesValues[j];
				Integer userTypeLong = JsonGWTUtils.getInteger(userTypesObj, userType.name());
				if(null != userTypeLong) userTypes.put(userType, userTypeLong.longValue());
			}
			bookingResume.setUserTypes(userTypes);
			
			// ChildDefinedUsers
			bookingResume.setChildDefinedUsers(JsonGWTUtils.getInteger(json, "childDefinedUsers"));
			
			// ChildBillingUsers
			bookingResume.setChildBillingUsers((JsonGWTUtils.getInteger(json, "childBillingUsers")));
			
			// DomainTypes
			HashMap<DomainType, DomainTypeInfo> domainTypes = new  HashMap<DomainType, DomainTypeInfo>();
			JSONObject domainTypesObj = JsonGWTUtils.getJSONObject(json, "domainTypes");
			DomainType[] domainTypesValues = DomainType.values();
			for (Integer j = 0; j < domainTypesValues.length; j++) {
				DomainType domainType = domainTypesValues[j];
				JSONObject domainTypeInfo = JsonGWTUtils.getJSONObject(domainTypesObj, domainType.name());
				if(null != domainTypeInfo) domainTypes.put(domainType, domainTypeInfoJson(domainTypeInfo));
			}
			bookingResume.setDomainTypes(domainTypes);
			
		}
		
		return bookingResume;
			
	}
	
	public static List<Domain> domainFromJSON(JSONArray json) {
		LinkedList<Domain> list = new LinkedList<>();
		for(Integer i = 0; i < json.size(); i++) {
			list.add(domainFromJSON(json.get(i).isObject()));
		}
 		return list;
	}
	
	public static Domain domainFromJSON(JSONObject json) {
		if(json == null) return new Domain();
		return new Domain()
			.setId(JsonGWTUtils.getInteger(json,IJsonNames.ID))
			.setName(JsonGWTUtils.getString(json, IJsonNames.NAME))
			.setDescription(JsonGWTUtils.getString(json, IJsonNames.DESCRIPTION))
			.setOwner(JsonGWTUtils.getString(json, IJsonNames.OWNER))
			.setParentId(JsonGWTUtils.getInteger(json, IJsonNames.PARENT_ID))
			.setDomainType( DomainType.safeValueOf( JsonGWTUtils.getString(json,IJsonNames.DOMAIN_TYPE) ))
			.setEnableHeredity(JsonGWTUtils.getBoolean(json, IJsonNames.ENABLE_HEREDITY))
			.setDomainManagement(JsonGWTUtils.getBoolean(json, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonGWTUtils.getBoolean(json, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonGWTUtils.getBoolean(json, IJsonNames.ACTIVE))
			.setScope(JsonGWTUtils.getInteger(json,IJsonNames.SCOPE))
			.setMaxDefinedUsers( JsonGWTUtils.getInteger(json,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers( JsonGWTUtils.getInteger(json,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize( JsonGWTUtils.getInteger(json,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize( JsonGWTUtils.getInteger(json,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonGWTUtils.getString(json, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonGWTUtils.getDate(json, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonGWTUtils.getDate(json, IJsonNames.EXPIRATION_DATE))
			.setCreationUser(JsonGWTUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonGWTUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonGWTUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonGWTUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
			.setAonCustomer(JsonGWTUtils.getInteger(json, IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.safeValueOf(JsonGWTUtils.getString(json,IJsonNames.AON_STATUS)))
			.setApps(getDomainApps(JsonGWTUtils.getJSONArray(json, IJsonNames.APPS)))
			.setUsers(getDomainUsers(JsonGWTUtils.getJSONArray(json, IJsonNames.USERS)))
		;
	}

	private static List<DomainApp> getDomainApps(JSONArray jsonArray) {
		LinkedList<DomainApp> list = new LinkedList<>();
		for(Integer i = 0; i < jsonArray.size(); i++) {
			String app = jsonArray.get(i).toString();
			app = AonStringUtils.isNotBlank(app) ? app.split("\"")[1] : "";
			list.add(new DomainApp().setApp(AonApp.safeValueOf(app)));
		}
 		return list;
	}
	
	private static List<User> getDomainUsers(JSONArray jsonArray) {
		LinkedList<User> list = new LinkedList<>();
		for(Integer i = 0; i < jsonArray.size(); i++) {
			JSONObject userJson = jsonArray.get(i).isObject();
			
			if(null != userJson) {
				User user = new User()
					.setId(JsonGWTUtils.getInteger(userJson, IJsonNames.ID))
					.setDomain(JsonGWTUtils.getInteger(userJson, IJsonNames.DOMAIN))
					.setType(UserType.valueOf(JsonGWTUtils.getString(userJson, IJsonNames.TYPE)))
					.setName(JsonGWTUtils.getString(userJson, IJsonNames.NAME))
					.setLogin(JsonGWTUtils.getString(userJson, IJsonNames.LOGIN))
					.setActive(JsonGWTUtils.getBoolean(userJson, IJsonNames.ACTIVE))
					;
				
				if(JsonGWTUtils.getBoolean(userJson, IJsonNames.PORTAL))
					user.setType(UserType.PORTAL);
					
				list.add(user);
			}
		}
 		return list;
	}
	
	/*
	 * public static JSONObject toJSON(User user) {
		JSONObject json = !user.getAuth().isEmpty() 
				? AuthJSON.toJSON(user.getAuth())
				: new JSONObject();
		
		return json
			.put(IJsonNames.ID, user.getId())
			.put(IJsonNames.DOMAIN, user.getDomain())
			.put(IJsonNames.TYPE, user.getType().name())
			.put(IJsonNames.NAME, AonStringUtils.isBlank(user.getAuth().getName())
					? user.getName()
					: user.getAuth().getName())
			.put(IJsonNames.PORTAL, user.isPortal())
			.put(IJsonNames.SHARED, user.isShared())
			.put(IJsonNames.LOGIN, user.getLogin())
			.put("taskHolders", TaskHolderJSON.toJSON(user.getTaskHolders()));
		}
	 * **/
	
	private static DomainTypeInfo domainTypeInfoJson(JSONObject domainTypeInfoJson) {
		DomainTypeInfo domainTypeInfo = new DomainTypeInfo();
		
		if(null != domainTypeInfoJson) {
			
			// Number
			domainTypeInfo.setNumber(JsonGWTUtils.getInteger(domainTypeInfoJson, "number"));
			
			// ChildApps
			HashMap<AonApp, Long> childApps = new  HashMap<AonApp, Long>();
			JSONObject childAppsObj = JsonGWTUtils.getJSONObject(domainTypeInfoJson, "childApps");
			AonApp[] aonApps = AonApp.values();
			for (Integer j = 0; j < aonApps.length; j++) {
				AonApp aonApp = aonApps[j];
				Integer aonAppLong = JsonGWTUtils.getInteger(childAppsObj, aonApp.name());
				if(null != aonAppLong) childApps.put(aonApp, aonAppLong.longValue());
			}
			domainTypeInfo.setChildApps(childApps);
			
			// Childs
			JSONArray childsArr = JsonGWTUtils.getJSONArray(domainTypeInfoJson, "childs");
			domainTypeInfo.setChilds(domainFromJSON(childsArr));
		}
		
		return domainTypeInfo;
	}
}
