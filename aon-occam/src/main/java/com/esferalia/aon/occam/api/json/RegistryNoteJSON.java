package com.esferalia.aon.occam.api.json;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class RegistryNoteJSON {
	
	private RegistryNoteJSON() {
	
	}
	
	public static List<RegistryNote> fromJSON(JSONArray json) {
		LinkedList<RegistryNote> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryNote fromJSON(JSONObject json) {
		return new RegistryNote()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setNoteDate(null == JsonUtils.getInteger(json, IJsonNames.DATE) ? null : new Date(JsonUtils.getInteger(json, IJsonNames.DATE)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setNoteType(NoteType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setSecurityLevel(SecurityLevel.safeValueOf(JsonUtils.getString(json, IJsonNames.SECURITY_LEVEL)))
			;
	}

	public static JSONArray toJSON(List<RegistryNote> notes) {
		return toJSON(notes.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryNote> notes) {
		JSONArray array = new JSONArray();
		notes.forEach(note -> array.put(toJSON(note)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryNote note) {
		return new JSONObject()
			.put(IJsonNames.ID, note.getId())
			.put(IJsonNames.DOMAIN, note.getDomain())
			.put(IJsonNames.REGISTRY, note.getRegistry())
			.put(IJsonNames.DESCRIPTION, note.getDescription())
			.put(IJsonNames.DATE, null == note.getNoteDate() ? null : note.getNoteDate().getTime())
			.put(IJsonNames.COMMENTS, note.getComments())
			.put(IJsonNames.TYPE, note.getNoteType().name())
			.put(IJsonNames.SECURITY_LEVEL, null == note.getSecurityLevel() ? null : note.getSecurityLevel().getName())
			;
	}
}
