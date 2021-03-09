package net.aonsolutions.aon.tbai.toolkit;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class JsonToolkit {

	
	public static JsonObject read(InputStream is) {
		return Json.createReader(is).readObject();
	};
	
	public static JsonArray getArray(final JsonObject parent, final String name) {
		if(parent == null) 	 return null;
		return (parent.containsKey(name))? 	
		parent.getJsonArray(name) : null; 
	}
	
	public static JsonObject getObject(final JsonObject parent, final String name) {
		if(parent == null) 	 return null;
		return (parent.containsKey(name))?
		parent.getJsonObject(name) : null; 
	}
	
	public static JsonNumber getNumber(final JsonObject parent, final String name) {
		if(parent == null) 	 return null;
		return (parent.containsKey(name))?
		parent.getJsonNumber(name) : null; 
	}
	
	public static Boolean getBoolean(final JsonObject parent, final String name) {
		if(parent == null) 	 return null;
		return (parent.containsKey(name))?
		parent.getBoolean(name) : null; 
	}

	public static String getString(final JsonObject parent, final String name) {
		if(parent == null) 	 return null;
		return (parent.containsKey(name))?
		parent.getString(name) : null; 
	}
	
	
	private static void prettyPrint(JsonObject json) {
		 Map<String, Object> properties = new HashMap<>(1);
         properties.put(JsonGenerator.PRETTY_PRINTING, true);

         StringWriter sw = new StringWriter();
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(sw);

		jsonWriter.writeObject(json);
		System.out.println("\n" + sw.toString());
		jsonWriter.close();
	}

}
