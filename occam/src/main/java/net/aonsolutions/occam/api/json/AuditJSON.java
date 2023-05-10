package net.aonsolutions.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Audit;

public class AuditJSON {
	
	private AuditJSON() {
	}
	
	public static List<Audit> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(AuditJSON::from)
			.toList();		
	}
	
	public static Audit from(JSONObject json) {
		if (json == null) return null; 
		return new Audit()
			.setCreationUser( AonJSONUtils.getString(json, AonNames.CREATION_USER))
			.setCreationDate(AonJSONUtils.getSilentDate(json, AonNames.CREATION_DATE))
			.setModificationUser(AonJSONUtils.getString(json, AonNames.MODIFICATION_USER))
			.setModificationDate(AonJSONUtils.getSilentDateTime(json, AonNames.MODIFICATION_DATE))
		;
	}
	
	public static JSONArray to(List<Audit> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Audit> stream) {
		return stream
			.map(AuditJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Audit audit) {
		if (audit == null) return null;
		return new JSONObject()
			.putOpt(AonNames.CREATION_USER, audit.getCreationUser().orElse(null))
			.putOpt(AonNames.CREATION_DATE, AonJSONUtils.formatDate(audit.getCreationDate().orElse(null)))
			.putOpt(AonNames.MODIFICATION_USER, audit.getModificationUser().orElse(null))
			.putOpt(AonNames.MODIFICATION_DATE, AonJSONUtils.formatDateTime(audit.getModificationDate().orElse(null)))
			;
	}
}
