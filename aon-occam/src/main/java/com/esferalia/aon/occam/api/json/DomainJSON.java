package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainJSON {
	
	private DomainJSON() {
		
	}
	
	public static List<Domain> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<Domain> fromJSON(JSONArray json) {
		LinkedList<Domain> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Domain fromJSON(JSONObject json) {
		if(json == null) return new Domain();
		return new Domain()
			.setId(JsonUtils.getInteger(json,IJsonNames.ID))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setOwner(JsonUtils.getString(json, IJsonNames.OWNER))
			.setParentId(JsonUtils.optInteger(json, IJsonNames.PARENT_ID))
			.setDomainType( DomainType.safeValueOf( JsonUtils.getString(json,IJsonNames.DOMAIN_TYPE) ))
			.setEnableHeredity(JsonUtils.getboolean(json, IJsonNames.ENABLE_HEREDITY))
			.setDomainManagement(JsonUtils.getboolean(json, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonUtils.getboolean(json, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setScope(JsonUtils.getInteger(json,IJsonNames.SCOPE))
			.setMaxDefinedUsers( JsonUtils.getInteger(json,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers( JsonUtils.getInteger(json,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize( JsonUtils.getInteger(json,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize( JsonUtils.getInteger(json,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonUtils.getString(json, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonUtils.getDate(json, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonUtils.getDate(json, IJsonNames.EXPIRATION_DATE))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
			.setAonCustomer(JsonUtils.getInteger(json, IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.safeValueOf(JsonUtils.getString(json,IJsonNames.AON_STATUS)))
			.setApps(getDomainApps(JsonUtils.getJSONArray(json, IJsonNames.APPS)))
		;
		
	}

	private static List<DomainApp> getDomainApps(JSONArray jsonArray) {
		LinkedList<DomainApp> list = new LinkedList<>();
		for(Integer i = 0; i < jsonArray.length(); i++) {
			String app = jsonArray.get(i).toString();
			try {
				app = AonStringUtils.isNotBlank(app) ? app.split("\"")[1] : "";
				list.add(new DomainApp().setApp(AonApp.safeValueOf(app)));
			} catch (Exception e) {}
		}
 		return list;
	}

	public static JSONArray toJSON(List<Domain> domains) {
		return toJSON(domains.stream());
	}
	
	public static JSONArray toJSON(Stream<Domain> domains) {
		JSONArray array = new JSONArray();
		domains.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(Domain domain) {
		if(domain == null) return new JSONObject();
		return new JSONObject()
			.putOpt(IJsonNames.ID, domain.getId())
			.putOpt(IJsonNames.NAME, domain.getName())
			.putOpt(IJsonNames.DESCRIPTION, domain.getDescription())
			.putOpt(IJsonNames.OWNER, domain.getOwner())
			.putOpt(IJsonNames.PARENT_ID, domain.getParentId())
			.putOpt(IJsonNames.DOMAIN_TYPE, domain.getDomainType() == null?null:domain.getDomainType().toString())
			.putOpt(IJsonNames.ENABLE_HEREDITY, domain.isEnableHeredity())
			.putOpt(IJsonNames.DOMAIN_MANAGEMENT, domain.isDomainManagement())
			.putOpt(IJsonNames.DISABLE_DOMAIN_MANAGEMENT, domain.isDisableDomainManagement())
			.putOpt(IJsonNames.ACTIVE, domain.isActive())
			.putOpt(IJsonNames.SCOPE, domain.getScope())
			.putOpt(IJsonNames.MAX_DEFINED_USERS, domain.getMaxDefinedUsers())
			.putOpt(IJsonNames.DEFINED_USERS, domain.getDefinedUsers())
			.putOpt(IJsonNames.MAX_DOCUMENT_SIZE, domain.getMaxDocumentSize())
			.putOpt(IJsonNames.MAX_TOTAL_DOCUMENT_SIZE, domain.getMaxTotalDocumentSize())
			.putOpt(IJsonNames.LAST_ACCESS_USER, domain.getLastAccessUser())
			.putOpt(IJsonNames.LAST_ACCESS_DATE, AonDateUtils.format(domain.getLastAccessDate(), AonDateUtils.DATE_TIME_FORMAT))
			.putOpt(IJsonNames.EXPIRATION_DATE, AonDateUtils.format(domain.getExpirationDate(), AonDateUtils.DATE_TIME_FORMAT))
			.putOpt(IJsonNames.CREATION_USER, domain.getCreationUser())
			.putOpt(IJsonNames.CREATION_DATE, AonDateUtils.format(domain.getCreationDate(), AonDateUtils.DATE_TIME_FORMAT))
			.putOpt(IJsonNames.MODIFICATION_USER, domain.getModificationUser())
			.putOpt(IJsonNames.MODIFICATION_DATE, AonDateUtils.format(domain.getModificationDate(), AonDateUtils.DATE_TIME_FORMAT))
			.putOpt(IJsonNames.AON_CUSTOMER, domain.getAonCustomer())
			.putOpt(IJsonNames.AON_STATUS, domain.getAonStatus() == null?null:domain.getAonStatus().toString())	
			.putOpt(IJsonNames.APPS, domain.getApps() == null ? null : getDomainApps(domain))	
			;		
	}

	private static JSONArray getDomainApps(Domain domain) {
		JSONArray apps = new JSONArray();
		domain.getApps().stream().forEach(app -> apps.put(app.getApp().name()));
		return apps;
	}

		
}
