package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

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
			.setParent(DomainJSON.from(AonJSONUtils.getObject(json, AonNames.PARENT)))
			.setType( DomainType.safeValueOf(AonJSONUtils.getString(json, AonNames.TYPE)).orElse(null) )
			.setEnableHeredity(AonJSONUtils.getBoolean(json, AonNames.INHERITS))
			.setActive(AonJSONUtils.getBoolean(json, AonNames.ACTIVE))
			.setScope(ScopeJSON.from(AonJSONUtils.getObject(json, AonNames.SCOPE)))
			.setCompany( RegistryJSON.from(AonJSONUtils.getObject(json, AonNames.COMPANY)))
			.setBooking( BookingJSON.from(AonJSONUtils.getObject(json, AonNames.BOOKING)))
			.setAudit( DomainAuditJSON.from(AonJSONUtils.getObject(json, AonNames.AUDIT)))
			.setUsers(UserJSON.from(AonJSONUtils.getArray(json, AonNames.USERS)))
			.setConfiguration(ConfigurationJSON.from(AonJSONUtils.getObject(json, AonNames.CONFIGURATION)))
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
			.putOpt(AonNames.PARENT, AonObjectUtils.ifOptionalPresent(domain.getParent(), DomainJSON::to ) )
			.putOpt(AonNames.TYPE, AonObjectUtils.ifNotNullDo(domain.getType(), Object::toString ))
			.putOpt(AonNames.INHERITS, domain.isEnableHeredity())
			.putOpt(AonNames.ACTIVE, domain.isActive())
			.putOpt(AonNames.SCOPE, AonObjectUtils.ifOptionalPresent(domain.getScope(), ScopeJSON::to ) )
			.putOpt(AonNames.COMPANY, RegistryJSON.to(domain.getCompany().orElse(null)))
			.putOpt(AonNames.BOOKING, BookingJSON.to(domain.getBooking().orElse(null)))
			.putOpt(AonNames.AUDIT, DomainAuditJSON.to(domain.getAudit().orElse(null)))
			.putOpt(AonNames.USERS, UserJSON.to(domain.getUsers().orElse(null)))
			.putOpt(AonNames.CONFIGURATION,ConfigurationJSON.to(domain.getConfiguration().orElse(null)))
			;
	}
}
