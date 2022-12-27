package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class UserJSON {
	
	private UserJSON() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static List<User> fromJSON(JSONArray json) {
		return new LinkedList<>();
	}
	
	public static User fromJSON(JSONObject json) {
		return new User()
		        .setId(JsonUtils.getInteger(json, IJsonNames.ID))
		        .setDomain(new Domain().setId(JsonUtils.getInteger(json, IJsonNames.DOMAIN)))
		        .setType(UserType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
		        .setName(JsonUtils.getString(json, IJsonNames.NAME))
		        .setLogin(JsonUtils.getString(json, IJsonNames.LOGIN))
		        .setRegistry(RegistryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.REGISTRY)))
		        .setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
		        .setAuth(JsonUtils.getString(json, IJsonNames.UUID))
		        .setLastAccess(JsonUtils.getDate(json, IJsonNames.LAST_ACCESS))
		        .setAllowConcurrent(JsonUtils.getboolean(json, IJsonNames.ALLOW_CONCURRENT))
		        .setLocale(JsonUtils.getString(json, IJsonNames.LOCALE))
		        .setPageLimit(JsonUtils.getInteger(json, IJsonNames.PAGE_LIMIT))
		        .setLinesPageLimit(JsonUtils.getInteger(json, IJsonNames.LINES_PAGE_LIMIT))
		        .setInitAction(JsonUtils.getString(json, IJsonNames.INIT_ACTION))
		        .setToolbar(UserToolbar.safeValueOf(JsonUtils.getString(json, IJsonNames.TOOLBAR)))
		        .setWorkgroups(WorkgroupJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.WORKGROUPS)));
		

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
		return new JSONObject()
		    .put(IJsonNames.ID, user.getId())
		    .put(IJsonNames.DOMAIN, user.getDomain().getId())
			.put(IJsonNames.TYPE, user.getType().name())
			.put(IJsonNames.NAME, user.getName())
			.put(IJsonNames.LOGIN, user.getLogin())
			.put(IJsonNames.REGISTRY, RegistryJSON.toJSON(user.getRegistry()))
			.put(IJsonNames.PORTAL, user.isPortal())
			.put(IJsonNames.SHARED, user.isShared())
			.put(IJsonNames.LOGIN, user.getLogin())
			.put(IJsonNames.ACTIVE, user.isActive())
			.put(IJsonNames.AUTH, user.getAuth())
			.put(IJsonNames.LAST_ACCESS, AonDateUtils.simpleFormat(user.getLastAccess()))
			.put(IJsonNames.ALLOW_CONCURRENT, user.isAllowConcurrent())
			.put(IJsonNames.LOCALE, user.getLocale())
			.put(IJsonNames.PAGE_LIMIT, user.getPageLimit())
			.put(IJsonNames.LINES_PAGE_LIMIT, user.getLinesPageLimit())
			.put(IJsonNames.INIT_ACTION, user.getInitAction())
			.put(IJsonNames.TOOLBAR, user.getToolbar().name())
			.put(IJsonNames.WORKGROUPS, WorkgroupJSON.toJSON(user.getWorkgroups()));
	}
}
