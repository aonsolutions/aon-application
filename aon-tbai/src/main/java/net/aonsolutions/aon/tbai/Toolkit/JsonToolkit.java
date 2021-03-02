package net.aonsolutions.aon.tbai.Toolkit;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;

public class JsonToolkit {

	
	public static JsonObject read(InputStream is) {
		return Json.createReader(is).readObject();
	};
	
	public static JsonArray getArray(final JsonObject parent, final String name) {
		return (parent.containsKey(name))? 	
		parent.getJsonArray(name) : null; 
	}
	
	public static JsonObject getObject(final JsonObject parent, final String name) {
		return (parent.containsKey(name))?
		parent.getJsonObject(name) : null; 
	}
	
	public static JsonNumber getNumber(final JsonObject parent, final String name) {
		return (parent.containsKey(name))?
		parent.getJsonNumber(name) : null; 
	}
	
	public static Boolean getBoolean(final JsonObject parent, final String name) {
		return (parent.containsKey(name))?
		parent.getBoolean(name) : null; 
	}

	public static String getString(final JsonObject parent, final String name) {
		return (parent.containsKey(name))?
		parent.getString(name) : null; 
	}
}
