package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceErrorJSON {

	private static class InvoiceErrorContextJSON {
		private InvoiceErrorContextJSON() {
		}

		private static Optional<InvoiceErrorContext> from(JSONObject json) {
			if (JsonUtils.isEmpty(json) ) return Optional.empty();
			return Optional.of(new InvoiceErrorContext()
				.setLine(JsonUtils.getInt(json,IJsonNames.LINE))
				.setKey(safeKeyOf(JsonUtils.getString(json,IJsonNames.KEY))));
		}
		
		public static JSONObject toJSON(InvoiceErrorContext context) {
			if (context == null) {
				return new JSONObject();
			}
			return new JSONObject().put(IJsonNames.LINE, context.getLine()).put(IJsonNames.KEY,
					safeNameOf(context.getKey()));
		}

		private static InvoiceErrorKey safeKeyOf(String name) {
			return AonCollectionUtils.stream( InvoiceErrorKey.values() )
				.filter( k -> AonStringUtils.equalsIgnoreCase(k.name(), name))
				.findFirst()
				.orElse(null);
		}
		private static String safeNameOf(InvoiceErrorKey key) {
			return key == null ? null : key.name();
		}
	}

	private InvoiceErrorJSON() {
		throw new IllegalStateException("Utility class");
	}

	public static Stream<InvoiceError> streamFromArray(JSONArray array) {
		return AonCollectionUtils.stream(array.length())
        	.mapToObj(i -> from(array.getJSONObject(i)))
        	.filter(o -> o.isPresent() )
        	.map(o -> o.get());		
    }
	
	public static List<InvoiceError> fromJSON(JSONArray jsonArray) {
		List<InvoiceError> list = new LinkedList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(fromJSON(jsonArray.getJSONObject(i)));
		}
		return list;
	}
	
	public static Optional<InvoiceError> from(JSONObject json) {
		if (JsonUtils.isEmpty( json )) return Optional.empty();
		return Optional.of(new InvoiceError()
			.setCode(json.getString(IJsonNames.CODE))
			.setMessage(json.getString(IJsonNames.MESSAGE))
			.setLevel(safeLevelOf(json.getString(IJsonNames.LEVEL)))
			.setContext(InvoiceErrorContextJSON.from(json.getJSONObject(IJsonNames.CONTEXT)).orElse(null)))
		;
	}

	public static InvoiceError fromJSON(JSONObject json) {
		return from(json).orElse(new InvoiceError());
	}

	public static JSONArray toJSON(List<InvoiceError> list) {
		return toJSON(list.stream());
	}

	public static JSONArray toJSON(Stream<InvoiceError> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(error -> array.put(toJSON(error)));
		return array;
	}

	public static JSONObject toJSON(InvoiceError error) {
		return new JSONObject()
			.put(IJsonNames.CODE, error.getCode()).put(IJsonNames.MESSAGE, error.getMessage())
			.put(IJsonNames.LEVEL, safeNameOf(error.getLevel()))
			.put(IJsonNames.CONTEXT, InvoiceErrorContextJSON.toJSON(error.getContext()));
	}

	private static String safeNameOf(InvoiceErrorLevel level) {
		return level == null ? null : level.name();
	}

	private static InvoiceErrorLevel safeLevelOf(String label) {
		for (InvoiceErrorLevel level : InvoiceErrorLevel.values()) {
			if (AonStringUtils.equalsIgnoreCase(level.name(), label)) {
				return level;
			} else if (AonStringUtils.equalsIgnoreCase(level.getLabel(), label)) {
				return level;
			}
		}
		return null;
	}

}
