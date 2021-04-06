package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonDomainFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonDomainToJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum DomainJSON {

	ID(
		(domain, json) -> domain.setId( JsonUtils.getInteger(json,IJsonNames.ID)),
		(domain, json) -> json.put(IJsonNames.ID, domain.getId())
	),
	NAME(
		(domain, json) -> domain.setName(json.optString(IJsonNames.NAME,null)),
		(domain, json) -> json.put(IJsonNames.NAME, domain.getName())
	),
	DESCRIPTION(
		(domain, json) -> domain.setDescription(json.optString(IJsonNames.DESCRIPTION,null)),
		(domain, json) -> json.put(IJsonNames.DESCRIPTION, domain.getDescription())
	),
	OWNER(
			(domain, json) -> domain.setOwner(json.optString(IJsonNames.OWNER,null)),
			(domain, json) -> json.put(IJsonNames.OWNER, domain.getOwner())
	),
	PARENT_ID(
		(domain, json) -> domain.setId( JsonUtils.getInteger(json,IJsonNames.PARENT_ID)),
		(domain, json) -> json.put(IJsonNames.PARENT_ID, domain.getId())
	),
	DOMAIN_TYPE(
		(params, json) -> params.setDomainType( DomainType.safeValueOf( JsonUtils.getInteger(json,IJsonNames.DOMAIN_TYPE) )),
		(params, json) -> json.put(IJsonNames.DOMAIN_TYPE, params.getDomainType())
	),
	ENABLE_HEREDITY(
		(domain, json) -> domain.setEnableHeredity(json.optBoolean(IJsonNames.ENABLE_HEREDITY)),
		(domain, json) -> json.put(IJsonNames.ENABLE_HEREDITY, domain.isEnableHeredity())
	),
	DOMAIN_MANAGEMENT(
		(domain, json) -> domain.setDomainManagement(json.optBoolean(IJsonNames.DOMAIN_MANAGEMENT)),
		(domain, json) -> json.put(IJsonNames.DOMAIN_MANAGEMENT, domain.isDomainManagement())
	),
	ACTIVE(
		(domain, json) -> domain.setActive(json.optBoolean(IJsonNames.ACTIVE)),
		(domain, json) -> json.put(IJsonNames.ACTIVE, domain.isActive())
	),
	SCOPE(
		(domain, json) -> domain.setScope( JsonUtils.getInteger(json,IJsonNames.SCOPE)),
		(domain, json) -> json.put(IJsonNames.SCOPE, domain.getScope())
	),
	MAX_DEFINED_USERS(
		(domain, json) -> domain.setMaxDefinedUsers( JsonUtils.getInteger(json,IJsonNames.MAX_DEFINED_USERS)),
		(domain, json) -> json.put(IJsonNames.MAX_DEFINED_USERS, domain.getMaxDefinedUsers())
	),
	DEFINED_USERS(
		(domain, json) -> domain.setDefinedUsers( JsonUtils.getInteger(json,IJsonNames.DEFINED_USERS)),
		(domain, json) -> json.put(IJsonNames.DEFINED_USERS, domain.getDefinedUsers())
	),
	;

	private IAonDomainFromJSON fromJSON;
	private IAonDomainToJSON toJSON;

	private DomainJSON(IAonDomainFromJSON fromJSON, IAonDomainToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(Domain domain) {
		if (domain != null) {
			JSONObject json = new JSONObject();
			for (DomainJSON p : DomainJSON.values()) {
				p.toJSON.to(domain, json);
			}
			return json;
		}
		return null;
	}
	
	public static Domain fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static Domain fromJSON(JSONObject json) {
		if (json != null) {
			Domain domain = new Domain();
			for (DomainJSON p : DomainJSON.values()) {
				p.fromJSON.from(domain, json);
			}
			return domain;
		}
		return null;
	}

}
