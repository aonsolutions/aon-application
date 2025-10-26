package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.Scope;

public class ScopeJSON {
	
	private ScopeJSON() {
	
	}
	
	public static Optional<Scope> from(JSONObject json) {
		return from(json, Scope::new );
	}
	public static Optional<Scope> from(JSONObject json, Supplier<Scope> scope) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of(scope.get()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
		);
	}
	public static Optional<JSONObject> to(Scope scope) {
		return to(scope, JSONObject::new );
	}
	public static Optional<JSONObject> to(Scope scope, Supplier<JSONObject> sup) {
		if (scope ==null) return Optional.empty();
		return Optional.of(sup.get()
			.put(IJsonNames.ID, scope.getId())
			.put(IJsonNames.DOMAIN, scope.getDomain())
			.put(IJsonNames.DESCRIPTION, scope.getDescription())
			.put(IJsonNames.NAME, scope.getDescription())	// DEPRECATED use desription.
		);
	}
	
	public static List<Scope> fromJSON(JSONArray json) {
		LinkedList<Scope> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	public static Scope fromJSON(JSONObject json) {
		return from(json).orElse(new Scope());
	}
	
	public static JSONArray toJSON(List<Scope> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Scope> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	public static JSONObject toJSON(Scope object) {
		return to(object).orElse(new JSONObject());
	}
}
