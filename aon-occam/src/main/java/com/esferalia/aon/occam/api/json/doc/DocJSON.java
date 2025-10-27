package com.esferalia.aon.occam.api.json.doc;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DocJSON {

	private DocJSON() {
	}
	
	// ***********************************************************
	// ************************************************ [FROM] ***
	// ***********************************************************
	
	public static <T extends Enum<?>> Optional<Doc<T>> from(JSONObject json, Supplier<Doc<T>> docSupplier) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		Doc<T> doc = docSupplier.get();
		return Optional.of(doc
			.setType(enumValue( doc, JsonUtils.getString( json, IJsonNames.TYPE )))
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setMimeType(MimeType.safeValueOf(JsonUtils.getString(json, IJsonNames.MIME_TYPE)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
		);
	}
	
	private static <T extends Enum<?>> T enumValue(Doc<T> doc, String name){
		if ( AonStringUtils.isBlank(name) ) return null;
		try {
			Type docClass = doc.getClass().getGenericSuperclass();
			@SuppressWarnings("unchecked")
			Class<T> enumClass = ((Class<T>) ((ParameterizedType) docClass).getActualTypeArguments()[0]);
			return AonCollectionUtils.stream(enumClass.getEnumConstants())
				.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), name))
				.findFirst()
				.orElse(null);
		} catch ( TypeNotPresentException | MalformedParameterizedTypeException e) {
			return null;
		}
	}
	
	// ***********************************************************
	// ************************************************** [TO] ***
	// ***********************************************************
	
	public static <T extends Enum<?>> Optional<JSONObject> to(Doc<T> doc, Supplier<JSONObject> jsonSupplier) {
		if (doc == null) return Optional.empty();
		return Optional.of(
			jsonSupplier.get()
				.put(IJsonNames.TYPE, doc.getType()==null?null:doc.getType().name())
				.put(IJsonNames.ID, doc.getId())
				.put(IJsonNames.DOMAIN, doc.getDomain())
				.put(IJsonNames.MIME_TYPE, MimeType.name( doc.getMimeType()) )
				.put(IJsonNames.DESCRIPTION, doc.getDescription())
				.put(IJsonNames.DATE,JsonUtils.getDateJSON( doc.getDate()))
		);
	}

}
