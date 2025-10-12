package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRAddress;

public class OCRAddressJSON {
	
	private OCRAddressJSON() {
	}
	
	public static List<OCRAddress> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRAddressJSON::from)
			.toList();		
	}
	public static OCRAddress from(JSONObject json) {
		if (json == null) return null;
		JSONObject jsonValue = OCRJSONUtils.getObject(json, OCRNames.VALUE);
		return jsonValue != null ?  fromImpl(jsonValue) : fromImpl(json);
	}
	
	public static JSONArray to(List<OCRAddress> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRAddress> stream) {
		return stream
			.map(OCRAddressJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRAddress address) {
		if (address == null) return null;
		return new JSONObject()
			.putOpt(OCRNames.RAW, address.getRaw().orElse(null))
			.putOpt(OCRNames.MUNICIPALITY, address.getMunicipality().orElse(null))
			.putOpt(OCRNames.POSTAL_CODE, address.getPostalCode().orElse(null))
			.putOpt(OCRNames.REGION, address.getRegion().orElse(null))
			.putOpt(OCRNames.STREET, address.getStreet().orElse(null))
		;
	}
	
	private static OCRAddress fromImpl(JSONObject json) {
		if (json == null) return null; 
		return new OCRAddress()
			.setRaw(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.RAW)))
			.setMunicipality(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.MUNICIPALITY)))
			.setPostalCode(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.POSTAL_CODE)))
			.setRegion(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.REGION)))
			.setStreet(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.STREET)))
		;
	}
	
	
}
