package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;

public class NoteJSON {
	
	public static LinkedList<Note> fromJSON(JSONArray json) {
		LinkedList<Note> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Note fromJSON(JSONObject json) {
		return new Note()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setOwner(JsonUtils.getInteger(json, "owner"))
			.setSubject(JsonUtils.getString(json, "subject"))
			.setNote(JsonUtils.getString(json, "note"))
			.setDate(JsonUtils.getDate(json, "date"))
			;
	}
	
	public static JSONArray toJSON(LinkedList<Note> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Note> notes) {
		JSONArray array = new JSONArray();
		notes.forEach(note -> array.put(toJSON(note)));
		return array;
	}
	
	public static JSONObject toJSON(Note note) {
		return new JSONObject()
			.put(IJsonNames.ID, note.getId())
			.put(IJsonNames.DOMAIN, note.getDomain())
			.put("owner", note.getOwner())
			.put("subject",note.getSubject())
			.put("note",note.getNote())
			.put("date",note.getDate());
	}

}
