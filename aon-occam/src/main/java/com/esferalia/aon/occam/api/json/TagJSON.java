package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;

public class TagJSON {

	private TagJSON() {
	   throw new IllegalStateException("Utility class");
	}
	
	public static List<Tag> fromJSON(JSONArray json) {
		LinkedList<Tag> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Tag fromJSON(JSONObject json) {
		return new Tag()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.optString(json, IJsonNames.NAME))
			.setTagType(TagType.safeValueOf(JsonUtils.optString(json, "type"))) 
			.setType(JsonUtils.getByte(json, "type"))
			.setColor(JsonUtils.optString(json, "color"))
			;
	}
	
	public static JSONArray toJSON(LinkedList<Tag> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(List<Tag> list) {
		return toJSON(list.stream().filter(t-> t.getName()!=null));
	}
	
	public static JSONArray toJSON(Stream<Tag> tags) {
		JSONArray array = new JSONArray();
		tags.forEach(tag -> array.put(toJSON(tag)));
		return array;
	}
	
	public static JSONObject toJSON(Tag tag) {
		return new JSONObject()
			.put(IJsonNames.ID, tag.getId())
			.put(IJsonNames.DOMAIN, tag.getDomain())
			.put(IJsonNames.NAME, tag.getName())
			.put(IJsonNames.TYPE, tag.getType() )
			.put("tag_type", tag.getTagType())
			.put("color", tag.getColor());
	}

}
