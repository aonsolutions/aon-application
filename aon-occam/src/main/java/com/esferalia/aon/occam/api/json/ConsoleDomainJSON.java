package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ConsoleDomainJSON {
	
	private ConsoleDomainJSON() {
		
	}
	
	public static List<ConsoleDomain> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<ConsoleDomain> fromJSON(JSONArray json) {
		LinkedList<ConsoleDomain> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ConsoleDomain fromJSON(JSONObject json) {
		if(json == null) return new ConsoleDomain();
		ConsoleDomain consoleDomain =  new ConsoleDomain()
			.setChildCount(JsonUtils.getInteger(json,IJsonNames.CHILD_COUNT))
			.setActiveChildCount(JsonUtils.getInteger(json,IJsonNames.ACTIVE_CHILD_COUNT))
			.setRemoteAccessEnabled(JsonUtils.getboolean(json,IJsonNames.REMOTE_ACCESS_ENABLED))
			;
		consoleDomain.setId(JsonUtils.getInteger(json,IJsonNames.ID));
		consoleDomain.setName(JsonUtils.getString(json, IJsonNames.NAME));
		consoleDomain.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION));
		consoleDomain.setOwner(JsonUtils.getString(json, IJsonNames.OWNER));
		consoleDomain.setParentId(JsonUtils.optInteger(json, IJsonNames.PARENT_ID));
		consoleDomain.setDomainType( DomainType.safeValueOf( JsonUtils.getString(json,IJsonNames.DOMAIN_TYPE) ));
		consoleDomain.setEnableHeredity(JsonUtils.getboolean(json, IJsonNames.ENABLE_HEREDITY));
		consoleDomain.setDomainManagement(JsonUtils.getboolean(json, IJsonNames.DOMAIN_MANAGEMENT));
		consoleDomain.setDisableDomainManagement(JsonUtils.getboolean(json, IJsonNames.DISABLE_DOMAIN_MANAGEMENT));
		consoleDomain.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE));
		consoleDomain.setScope(JsonUtils.getInteger(json,IJsonNames.SCOPE));
		consoleDomain.setMaxDefinedUsers( JsonUtils.getInteger(json,IJsonNames.MAX_DEFINED_USERS));
		consoleDomain.setDefinedUsers( JsonUtils.getInteger(json,IJsonNames.DEFINED_USERS));
		consoleDomain.setMaxDocumentSize( JsonUtils.getInteger(json,IJsonNames.MAX_DOCUMENT_SIZE));
		consoleDomain.setMaxTotalDocumentSize( JsonUtils.getInteger(json,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE));
		consoleDomain.setLastAccessUser(JsonUtils.getString(json, IJsonNames.LAST_ACCESS_USER));
		consoleDomain.setLastAccessDate(JsonUtils.getDate(json, IJsonNames.LAST_ACCESS_DATE));
		consoleDomain.setExpirationDate(JsonUtils.getDate(json, IJsonNames.EXPIRATION_DATE));
		consoleDomain.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER));
		consoleDomain.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE));
		consoleDomain.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER));
		consoleDomain.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE));
		return consoleDomain;
	}

	public static JSONArray toJSON(List<ConsoleDomain> domains) {
		return toJSON(domains.stream());
	}
	
	public static JSONArray toJSON(Stream<ConsoleDomain> domains) {
		JSONArray array = new JSONArray();
		domains.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(ConsoleDomain domain) {
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
			.putOpt(IJsonNames.ACTIVE_CHILD_COUNT, domain.getActiveChildCount())
			.putOpt(IJsonNames.CHILD_COUNT, domain.getChildCount())
			.putOpt(IJsonNames.REMOTE_ACCESS_ENABLED, domain.isRemoteAccessEnabled())
			;		
	}

		
}
