package net.aonsolutions.invofox.json;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRUser;

public class OCRUserJSON {
	
	private OCRUserJSON() {
		
	}
	
	public static List<OCRUser> from(JSONArray array) {
		if (array == null || array.isEmpty()) return Collections.emptyList();
		return OCRJSONUtils.stream(array)
			.map(OCRUserJSON::from)
			.toList();		
	}
	
	public static OCRUser from(JSONObject json) {
		if (json == null) return null; 
		return new OCRUser()
			.setId(OCRJSONUtils.getString(json, OCRNames.ID))
			.setAccount(OCRJSONUtils.getString(json, OCRNames.ACCOUNT))
			.setEmail(OCRJSONUtils.getString(json, OCRNames.EMAIL))
			.setLang(OCRJSONUtils.getString(json, OCRNames.LANG))
			.setName(OCRJSONUtils.getString(json, OCRNames.NAME))
//			.setSecurityGroups(null)
			;
	}
	
	public static JSONArray to(List<OCRUser> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRUser> stream) {
		return stream
			.map(OCRUserJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRUser user) {
		if (user == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.ID, user.getId().orElse(null))
			.putOpt(OCRNames.ACCOUNT, user.getAccount().orElse(null))
			.putOpt(OCRNames.EMAIL, user.getEmail().orElse(null))
			.putOpt(OCRNames.LANG, user.getLang().orElse(null))
			.putOpt(OCRNames.NAME, user.getName().orElse(null))
		;
	}

	public static List<OCRUser> from(Object rawObject) {
		if (rawObject instanceof JSONArray) {
			return from((JSONArray) rawObject);
		}
		if (rawObject instanceof JSONObject) {
			LinkedList<OCRUser> list = new LinkedList<>();
			list.add(from( (JSONObject) rawObject));
			return list;
		}
		return Collections.emptyList();
	}
}
