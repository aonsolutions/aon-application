package net.aonsolutions.invofox.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.invofox.model.OCRRegistry;

public class OCRRegistryJSON {
	
	private OCRRegistryJSON() {
	}
	
	public static List<OCRRegistry> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return OCRJSONUtils.stream(array)
			.map(OCRRegistryJSON::from)
			.toList();		
	}
	
	public static OCRRegistry from(JSONObject json) {
		if (json == null) return null; 
		return new OCRRegistry()
			.setAddress(OCRAddressJSON.from(OCRJSONUtils.getObject(json, OCRNames.ADDRESS)))
			.setCountry(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.COUNTRY)))
			.setEmail(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.EMAIL)))
			.setFax(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.FAX)))
			.setName(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.NAME)))
			.setPhoneNumber(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.PHONE_NUMBER)))
			.setTaxId(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.TAX_ID)))
			.setWebsite(OCRStringJSON.from(OCRJSONUtils.getObject(json, OCRNames.WEBSITE)));
	}
	
	public static JSONArray to(List<OCRRegistry> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<OCRRegistry> stream) {
		return stream
			.map(OCRRegistryJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(OCRRegistry object) {
		if (object == null) return null;
		return new JSONObject()
				.putOpt(OCRNames.ADDRESS, OCRAddressJSON.to(object.getAddress().orElse(null)))
				.putOpt(OCRNames.COUNTRY, OCRStringJSON.to(object.getCountry().orElse(null)))
				.putOpt(OCRNames.EMAIL, OCRStringJSON.to(object.getEmail().orElse(null)))
				.putOpt(OCRNames.FAX, OCRStringJSON.to(object.getFax().orElse(null)))
				.putOpt(OCRNames.NAME, OCRStringJSON.to(object.getName().orElse(null)))
				.putOpt(OCRNames.PHONE_NUMBER, OCRStringJSON.to(object.getPhoneNumber().orElse(null)))
				.putOpt(OCRNames.TAX_ID, OCRStringJSON.to(object.getTaxId().orElse(null)))
				.putOpt(OCRNames.WEBSITE, OCRStringJSON.to(object.getWebsite().orElse(null)));
	}
}
