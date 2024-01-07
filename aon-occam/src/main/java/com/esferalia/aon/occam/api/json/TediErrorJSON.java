package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.tedi.TediContext;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TediErrorJSON {
    
    	private static class TediContextJSON {
    	    
    	    private  TediContextJSON() {
	    }
    	    
    	    private static TediContext  fromJSON(JSONObject json) {
		return new TediContext()
			.setLine(json.getInt(IJsonNames.LINE))
			.setKey(safeKeyOf(json.getString(IJsonNames.KEY)));
    	    }
    	    
	    public static JSONObject toJSON(TediContext context) {
		if(context == null ) { 
			return new JSONObject();
		}
		return new JSONObject()
			.put(IJsonNames.LINE, context.getLine())
			.put(IJsonNames.KEY, safeNameOf(context.getKey()));
	    }    	    

	    private static TediContextKey safeKeyOf(String name) {
		for (TediContextKey key : TediContextKey.values()) {
		    if (AonStringUtils.equalsIgnoreCase(key.name(), name)) {
			return key;
		    }
		}
		return null;
	    }

	    private static String safeNameOf(TediContextKey key) {
		return key == null ? null : key.name();
	    }
    	}
	
	private TediErrorJSON() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static List<TediError> fromJSON(JSONArray jsonArray) {
		List<TediError> list =  new LinkedList<>();
		for ( int i = 0; i < jsonArray.length(); i++) {
		    list.add(fromJSON(jsonArray.getJSONObject(i)));
		}
	    	return list;
	}
	
	public static TediError fromJSON(JSONObject json) {
		return new TediError()
			.setCode(json.getString(IJsonNames.CODE))
			.setMessage(json.getString(IJsonNames.MESSAGE))
			.setLevel(safeLevelOf( json.getString(IJsonNames.LEVEL)))
			.setContext(TediContextJSON.fromJSON(json.getJSONObject(IJsonNames.CONTEXT)))
			;
	}
	
	public static JSONArray toJSON(List<TediError> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<TediError> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(error -> array.put(toJSON(error)));
		return array;
	}
	
	public static JSONObject toJSON(TediError error) {
		return new JSONObject()
			.put(IJsonNames.CODE, error.getCode())
			.put(IJsonNames.MESSAGE, error.getMessage())
			.put(IJsonNames.LEVEL, safeNameOf( error.getLevel())) 
			.put(IJsonNames.CONTEXT, TediContextJSON.toJSON(error.getContext()))
			;
	}
	
	
	private static String safeNameOf( TediLevel level) {
	    return level == null  ? null : level.name();
	}

	private static TediLevel safeLevelOf( String label) {
	    for (TediLevel level : TediLevel.values()) {
		if (AonStringUtils.equalsIgnoreCase(level.name(), label)) {
		    return level;
		} else if (AonStringUtils.equalsIgnoreCase(level.getLabel(), label)) {
		    return level;
		}
	    }
	    return null;
	}
	
}
