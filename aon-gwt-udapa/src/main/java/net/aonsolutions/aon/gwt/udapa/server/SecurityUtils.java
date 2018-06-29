package net.aonsolutions.aon.gwt.udapa.server;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class SecurityUtils {

	public static SecurityUtils getInstance() {
		return new SecurityUtils();
	}
	
	public JSONObject getJSON(String value) throws JSONException{
		return new JSONObject(decode(value.getBytes()));
	}
	
	public HashMap<String, String> getParameters(String value){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] parameters = decode(value.getBytes()).split("&");
		for(String parameter : parameters){
			String[] values = parameter.split("=");
			if(values.length > 1)
				map.put(values[0], values[1]);
		}
		return map;
	}
	
	public HashMap<String, String[]> getParametersMap(String value){
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		String[] parameters = decode(value.getBytes()).split("&");
		for(String parameter : parameters){
			String[] values = parameter.split("=");
			if(values.length > 1) {
				if(map.containsKey(values[0])) {
					String[] strs = new String[map.get(values[0]).length + 1]; 
					for(Integer i = 0; i < map.get(values[0]).length; i++) {
						strs[i] = map.get(values[0])[i];
					}
					strs[map.get(values[0]).length] = values[1];
					map.put(values[0], strs);
				} else map.put(values[0], new String[]{values[1]});
			}
		}
		return map;
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
}
