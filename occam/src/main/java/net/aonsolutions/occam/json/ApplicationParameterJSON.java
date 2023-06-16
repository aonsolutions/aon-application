package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class ApplicationParameterJSON {
	
	private ApplicationParameterJSON() {
	}
	
	public static List<ApplicationParameter> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(ApplicationParameterJSON::from)
			.toList();		
	}
	
	public static ApplicationParameter from(JSONObject json) {
		if (json == null) return null; 
		return new ApplicationParameter()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setName( AppParam.safeValueOf(AonJSONUtils.getString(json, AonNames.NAME)).orElse(null) )
			.setValue(AonJSONUtils.getString(json, AonNames.VALUE))
		;
	}
	
	public static JSONArray to(List<ApplicationParameter> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<ApplicationParameter> stream) {
		return stream
			.map(ApplicationParameterJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(ApplicationParameter parameter) {
		if (parameter == null) return null;
		return new JSONObject()
			.put(AonNames.ID, parameter.getId())
			.put(AonNames.DOMAIN, parameter.getDomain())
			.putOpt(AonNames.NAME, AonObjectUtils.ifNotNullGet(parameter.getName(), Object::toString ))
			.putOpt(AonNames.VALUE, parameter.getValue())
			;
	}
}
