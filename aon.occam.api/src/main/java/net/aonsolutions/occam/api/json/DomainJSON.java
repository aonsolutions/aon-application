package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.type.AonStatus;
import net.aonsolutions.occam.api.model.type.DomainType;

public class DomainJSON {
	
	private DomainJSON() {
		
	}
	
	public static Domain fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return new Domain()
			.setId(JsonUtils.getInteger(json,IJsonNames.ID))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setOwner(JsonUtils.getString(json, IJsonNames.OWNER))
			.setParentId(JsonUtils.getInteger(json, IJsonNames.PARENT))
			.setDomainType(DomainType.value( JsonUtils.getString(json, IJsonNames.TYPE)).orElse(null))
			.setHeredityEnabled(JsonUtils.getboolean(json, IJsonNames.HEREDITY_ENABLED))
			.setDomainManagement(JsonUtils.getboolean(json, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonUtils.getboolean(json, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setScope(JsonUtils.getInteger(json,IJsonNames.SCOPE))
			.setMaxDefinedUsers(JsonUtils.getInteger(json,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers(JsonUtils.getInteger(json,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize(JsonUtils.getInteger(json,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize(JsonUtils.getInteger(json,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonUtils.getString(json, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonUtils.parseDateTime(json, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonUtils.parseDate(json, IJsonNames.EXPIRATION_DATE))
			.setAonCustomer(JsonUtils.getInteger(json,IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.value(JsonUtils.getString(json,IJsonNames.AON_STATUS)).orElse(null))
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
			.setCreationDate(JsonUtils.parseDateTime(json, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonUtils.parseDateTime(json, IJsonNames.MODIFICATION_DATE))
		;
		
	}
	
	public static JSONObject toJSON(Domain domain) {
		if(domain == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, domain.getId())
			.put(IJsonNames.NAME, domain.getName())
			.put(IJsonNames.DESCRIPTION, domain.getDescription())
			.put(IJsonNames.OWNER, domain.getOwner())
			.put(IJsonNames.PARENT, domain.getParentId())
			.put(IJsonNames.TYPE, DomainType.value( domain.getDomainType()))
			.put(IJsonNames.HEREDITY_ENABLED, domain.isHeredityEnabled())
			.put(IJsonNames.DOMAIN_MANAGEMENT, domain.isDomainManagement())
			.put(IJsonNames.DISABLE_DOMAIN_MANAGEMENT, domain.isDisableDomainManagement())
			.put(IJsonNames.ACTIVE, domain.isActive())
			.put(IJsonNames.SCOPE, domain.getScope())
			.put(IJsonNames.MAX_DEFINED_USERS, domain.getMaxDefinedUsers())
			.put(IJsonNames.DEFINED_USERS, domain.getDefinedUsers())
			.put(IJsonNames.MAX_DOCUMENT_SIZE, domain.getMaxDocumentSize())
			.put(IJsonNames.MAX_TOTAL_DOCUMENT_SIZE, domain.getMaxTotalDocumentSize())
			.put(IJsonNames.LAST_ACCESS_USER, domain.getLastAccessUser())
			.put(IJsonNames.LAST_ACCESS_DATE, JsonUtils.formatDateTime( domain.getLastAccessDate()))
			.put(IJsonNames.EXPIRATION_DATE, JsonUtils.formatDate( domain.getExpirationDate())) 
			.put(IJsonNames.AON_CUSTOMER, domain.getAonCustomer())
			.put(IJsonNames.AON_STATUS, AonStatus.name( domain.getAonStatus()))
			.put(IJsonNames.CREATION_USER, domain.getCreationUser())
			.put(IJsonNames.CREATION_DATE, JsonUtils.formatDateTime( domain.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER, domain.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, JsonUtils.formatDateTime( domain.getModificationDate()))
		;		
	}
		
}
