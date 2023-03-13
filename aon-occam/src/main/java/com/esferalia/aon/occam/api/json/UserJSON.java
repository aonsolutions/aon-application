package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

public class UserJSON {
	
	private UserJSON() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static List<User> fromJSON(JSONArray json) {
		return new LinkedList<>();
	}
	
	public static User fromJSON(JSONObject json) {
		return new User();
	}
	
	public static JSONArray toJSON(List<User> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<User> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(user -> array.put(toJSON(user)));
		return array;
	}
	
	public static JSONObject toJSON(User user) {
		JSONObject json = !user.getAuth().isEmpty() 
				? AuthJSON.toJSON(user.getAuth())
				: new JSONObject();
		
		return json
			.put(IJsonNames.ID, user.getId())
			.put(IJsonNames.DOMAIN, user.getDomain())
			.put(IJsonNames.TYPE, user.getType().name())
			.put(IJsonNames.NAME, AonStringUtils.isBlank(user.getAuth().getName())
					? user.getName()
					: user.getAuth().getName())
			.put(IJsonNames.PORTAL, user.isPortal())
			.put(IJsonNames.SHARED, user.isShared())
			.put(IJsonNames.LOGIN, user.getLogin())
			.put("taskHolders", TaskHolderJSON.toJSON(user.getTaskHolders()));
	}
}
