package com.esferalia.aon.occam.api.json;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.IJsonNames;

import com.esferalia.aon.occam.api.model.CreditorSupplier;

public class CreditorSupplierJSON {

	public static JSONArray toJSON(Stream<CreditorSupplier> stream) {
		JSONArray array = new JSONArray();
		List<CreditorSupplier> list = stream.collect(Collectors.toList());
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getType().equals("creditor")) {
				JSONObject obj = CreditorJSON.toJSON(list.get(i).getCreditor());
				obj.put(IJsonNames.TYPE, list.get(i).getType());
				array.put(obj);
			}else if(list.get(i).getType().equals("supplier")) {
				JSONObject obj = SupplierJSON.toJSON(list.get(i).getSupplier());
				obj.put(IJsonNames.TYPE, list.get(i).getType());
				array.put(obj);
			}
		}
		return array;
	}
	
}
