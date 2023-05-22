package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class DomainAuditJSON {
	
	private DomainAuditJSON() {
	}
	
	public static List<DomainAudit> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(DomainAuditJSON::from)
			.toList();		
	}
	
	public static DomainAudit from(JSONObject json) {
		if (json == null) return null; 
		return new DomainAudit()
			.setLastAccessUser(AonJSONUtils.getString(json, AonNames.LAST_ACCESS_USER))
			.setLastAccessDate(AonJSONUtils.getDateTime(json, AonNames.LAST_ACCESS_DATE))
			.setCreationUser( AonJSONUtils.getString(json, AonNames.CREATION_USER))
			.setCreationDate(AonJSONUtils.getDateTime(json, AonNames.CREATION_DATE))
			.setModificationUser(AonJSONUtils.getString(json, AonNames.MODIFICATION_USER))
			.setModificationDate(AonJSONUtils.getDateTime(json, AonNames.MODIFICATION_DATE))
		;
	}
	
	public static JSONArray to(List<DomainAudit> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<DomainAudit> stream) {
		return stream
			.map(DomainAuditJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(DomainAudit audit) {
		if (audit == null) return null;
		return new JSONObject()
			.putOpt(AonNames.LAST_ACCESS_DATE, AonJSONUtils.formatDateTime(audit.getLastAccessDate().orElse(null)))
			.putOpt(AonNames.LAST_ACCESS_USER, audit.getLastAccessUser().orElse(null))
			.putOpt(AonNames.CREATION_USER, audit.getCreationUser().orElse(null))
			.putOpt(AonNames.CREATION_DATE, AonJSONUtils.formatDateTime(audit.getCreationDate().orElse(null)))
			.putOpt(AonNames.MODIFICATION_USER, audit.getModificationUser().orElse(null))
			.putOpt(AonNames.MODIFICATION_DATE, AonJSONUtils.formatDateTime(audit.getModificationDate().orElse(null)))
			;
	}
}
