package net.aonsolutions.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.User;

public class UserJSON {
	
	private UserJSON() {
	}
	
	public static List<User> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(UserJSON::from)
			.toList();		
	}
	
	public static User from(JSONObject json) {
		return new User()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setLogin(AonJSONUtils.getString(json, AonNames.LOGIN))
			.setActive(AonJSONUtils.getBoolean(json, AonNames.ACTIVE))
		;
	}
	
	public static JSONArray to(List<User> list) {
		if (AonCollectionUtils.isEmpty(list)) return null;
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<User> stream) {
		return stream
			.map(UserJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(User user) {
		return new JSONObject()
			.put(AonNames.ID, user.getId())
			.put(AonNames.DOMAIN, user.getDomain())
			.put(AonNames.NAME, user.getName())
			.put(AonNames.LOGIN, user.getLogin())
			.put(AonNames.ACTIVE, user.isActive())
			;
	}
}
