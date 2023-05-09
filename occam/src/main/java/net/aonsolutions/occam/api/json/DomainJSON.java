package net.aonsolutions.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;

public class DomainJSON {
	
	private DomainJSON() {
	}
	
	public static List<Domain> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(DomainJSON::from)
			.toList();		
	}
	
	public static Domain from(JSONObject json) {
		if (json == null) return null; 
		return new Domain()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setDescription(AonJSONUtils.getString(json, AonNames.DESCRIPTION))
			.setOwner(AonJSONUtils.getString(json, AonNames.OWNER))
			.setParent(DomainJSON.from(AonJSONUtils.getObject(json, AonNames.PARENT)))
			.setType( DomainType.safeValueOf(AonJSONUtils.getString(json, AonNames.TYPE)).orElse(null) )
			.setSubDomainSuffix(AonJSONUtils.getString(json, AonNames.SUBDOMAIN_SUFFIX))
			.setEnableHeredity(AonJSONUtils.getBoolean(json, AonNames.ENABLE_HEREDITY))
			.setDomainManagement(AonJSONUtils.getBoolean(json, AonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(AonJSONUtils.getBoolean(json, AonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(AonJSONUtils.getBoolean(json, AonNames.ACTIVE))
			.setScope(AonJSONUtils.getInteger(json, AonNames.SCOPE))
			.setMaxDefinedUsers(AonJSONUtils.getInteger(json, AonNames.MAX_DEFINED_USERS))
			.setMaxDocumentSize(AonJSONUtils.getInteger(json, AonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize(AonJSONUtils.getInteger(json, AonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(AonJSONUtils.getString(json, AonNames.LAST_ACCESS_USER))
			.setLastAccessDate(AonJSONUtils.getSilentDateTime(json, AonNames.LAST_ACCESS_DATE))
			.setExpirationDate(AonJSONUtils.getSilentDate(json, AonNames.EXPIRATION_DATE))
			.setCreationUser( AonJSONUtils.getString(json, AonNames.CREATION_USER))
			.setCreationDate(AonJSONUtils.getSilentDate(json, AonNames.CREATION_DATE))
			.setModificationUser(AonJSONUtils.getString(json, AonNames.MODIFICATION_USER))
			.setModificationDate(AonJSONUtils.getSilentDateTime(json, AonNames.MODIFICATION_DATE))
			.setAonCustomer(AonJSONUtils.getInteger(json, AonNames.AON_CUSTOMER))  
			.setAonStatus( AonStatus.safeValueOf(AonJSONUtils.getString(json, AonNames.AON_STATUS)).orElse(null) )
			.setUsers(UserJSON.from(AonJSONUtils.getArray(json, AonNames.USERS)))
		;
	}
	
	public static JSONArray to(List<Domain> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Domain> stream) {
		return stream
			.map(DomainJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Domain domain) {
		if (domain == null) return null;
		return new JSONObject()
			.put(AonNames.ID, domain.getId())
			.put(AonNames.NAME, domain.getName())
			.put(AonNames.DESCRIPTION, domain.getDescription())
			.putOpt(AonNames.OWNER, domain.getOwner().orElse(null))
			.putOpt(AonNames.PARENT, AonObjectUtils.ifOptionalPresent(domain.getParent(), DomainJSON::to ) )
			.putOpt(AonNames.TYPE, AonObjectUtils.ifOptionalPresent(domain.getType(), Object::toString) )
			.putOpt(AonNames.SUBDOMAIN_SUFFIX, domain.getSubDomainSuffix().orElse(null))
			.putOpt(AonNames.ENABLE_HEREDITY, domain.isEnableHeredity().orElse(null))
			.putOpt(AonNames.DOMAIN_MANAGEMENT, domain.isDomainManagement().orElse(null))
			.putOpt(AonNames.DISABLE_DOMAIN_MANAGEMENT, domain.isDisableDomainManagement().orElse(null))
			.putOpt(AonNames.ACTIVE, domain.isActive().orElse(null))
			.putOpt(AonNames.SCOPE, domain.getScope().orElse(null))
			.putOpt(AonNames.MAX_DEFINED_USERS, domain.getMaxDefinedUsers().orElse(null))
			.putOpt(AonNames.MAX_DOCUMENT_SIZE, domain.getMaxDocumentSize().orElse(null))
			.putOpt(AonNames.MAX_TOTAL_DOCUMENT_SIZE, domain.getMaxTotalDocumentSize().orElse(null))
			.putOpt(AonNames.LAST_ACCESS_USER, domain.getLastAccessUser().orElse(null))
			.putOpt(AonNames.LAST_ACCESS_DATE, AonJSONUtils.formatDateTime(domain.getLastAccessDate().orElse(null)))
			.putOpt(AonNames.EXPIRATION_DATE, AonJSONUtils.formatDate(domain.getExpirationDate().orElse(null)))
			.putOpt(AonNames.CREATION_USER, domain.getCreationUser().orElse(null))
			.putOpt(AonNames.CREATION_DATE, AonJSONUtils.formatDate(domain.getCreationDate().orElse(null)))
			.putOpt(AonNames.MODIFICATION_USER, domain.getModificationUser().orElse(null))
			.putOpt(AonNames.MODIFICATION_DATE, AonJSONUtils.formatDateTime(domain.getModificationDate().orElse(null)))
			.putOpt(AonNames.AON_STATUS, AonObjectUtils.ifOptionalPresent(domain.getAonStatus(), Object::toString) )
			.putOpt(AonNames.AON_CUSTOMER, domain.getAonCustomer().orElse(null))
			.putOpt(AonNames.USERS, UserJSON.to(domain.getUsers().orElse(null)))
			;
	}
}
