package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Scope;

public class ScopeJSON {
	
	private ScopeJSON() {
	}
	
	public static List<Scope> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(ScopeJSON::from)
			.toList();		
	}
	
	public static Scope from(JSONObject json) {
		if (json == null) return null;
		return new Scope()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setDescription(AonJSONUtils.getString(json, AonNames.DESCRIPTION))
		;
	}
	
	public static JSONArray to(List<Scope> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Scope> stream) {
		return stream
			.map(ScopeJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Scope scope) {
		if (scope == null) return null;
		return new JSONObject()
			.put(AonNames.ID, scope.getId())
			.put(AonNames.DOMAIN, scope.getDomain())
			.put(AonNames.DESCRIPTION, scope.getDescription())
			;
	}
}
