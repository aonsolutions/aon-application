package com.esferalia.aon.occam.api.json;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AttachJSON {
	
	private AttachJSON() {
	
	}
	
	public static List<Attach> fromJSON(JSONArray json) {
		LinkedList<Attach> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Attach fromJSON(JSONObject json) {

		Attach attach = new Attach()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.getJSONObject(IJsonNames.DOMAIN)))
			.setAttachType(AttachType.safeValueOf(JsonUtils.getString(json, IJsonNames.ATTACH_TYPE)))
			.setAttachModule(JsonUtils.getInteger(json, IJsonNames.ATTACH_MODULE))
			.setDescription(AonStringUtils.isBlank(JsonUtils.getString(json, IJsonNames.NAME))
					? JsonUtils.getString(json, IJsonNames.NAME)
					: JsonUtils.getString(json, IJsonNames.CONTENT_NAME))
			.setDparentId(json.opt(IJsonNames.SIZE)!= null
				? Long.toString(json.optLong(IJsonNames.SIZE))
				: Long.toString(json.optLong(IJsonNames.CONTENT_SIZE)))
			.setMimeType(MimeType.get(json.optString(IJsonNames.CONTENT_TYPE)))
			.setDriveId(JsonUtils.getString(json, IJsonNames.DRIVE_ID))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setType(JsonUtils.getByte(json, IJsonNames.TYPE))
			.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL));
				
		if(json.opt(IJsonNames.CONTENT) != null) {
			String base64 = json.optString(IJsonNames.CONTENT);
			byte[] fileData = Base64.getDecoder().decode(base64);
			attach.setData(fileData);
		}

		return attach;
	}
	
	public static JSONArray toJSON(List<Attach> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Attach> attachs) {
		JSONArray array = new JSONArray();
		attachs.forEach(attach -> array.put(toJSON(attach)));
		return array;
	}
	
	public static JSONObject toJSON(Attach attach) {
		if(attach.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, attach.getId())
			.put(IJsonNames.ATTACH_TYPE, attach.getAttachType().name())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(attach.getDomain()))
			.put(IJsonNames.ATTACH_MODULE, attach.getAttachModule())
			.put(IJsonNames.NAME, attach.getDescription())
			.put(IJsonNames.SIZE, attach.getSize())
			.put(IJsonNames.CONTENT_TYPE, attach.getMimeType().getName())
			.put(IJsonNames.DRIVE_ID, attach.getDriveId())
			.put(IJsonNames.SCOPE, attach.getScope())
			.put(IJsonNames.DATE, attach.getDate())
			.put(IJsonNames.TYPE, attach.getType())
			.put(IJsonNames.CONFIDENTIAL, attach.isConfidential());
	}

}
