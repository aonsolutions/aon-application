package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.Auth;

public class AuthJSON {
	
	public static Auth fromJSON(JSONObject json) {
		return new Auth()
				.setEmail(JsonUtils.getString(json, IJsonNames.EMAIL))
				.setUuid(JsonUtils.getString(json, IJsonNames.UUID))
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setSurname(JsonUtils.getString(json, IJsonNames.SURNAME))
				.setDocument(JsonUtils.getString(json, IJsonNames.DOCUMENT))
				.setPhone(JsonUtils.getString(json, IJsonNames.PHONE))
				.setAvatar(JsonUtils.getString(json, IJsonNames.AVATAR));
	}
	
	public static JSONObject toJSON(Auth auth) {
		return new JSONObject()
				.put(IJsonNames.EMAIL, auth.getEmail())
				.put(IJsonNames.UUID, auth.getUuid())
				.put(IJsonNames.NAME, auth.getName() != null ? auth.getName() : "")
				.put(IJsonNames.SURNAME, auth.getSurname() != null ? auth.getSurname() : "")
				.put(IJsonNames.DOCUMENT, auth.getDocument() != null ? auth.getDocument() : "")
				.put(IJsonNames.PHONE, auth.getPhone() != null ? auth.getPhone() : "")
				.put(IJsonNames.AVATAR, auth.getAvatar());
	}
}
