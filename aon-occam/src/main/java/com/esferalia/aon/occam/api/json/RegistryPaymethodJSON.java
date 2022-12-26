package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;

public class RegistryPaymethodJSON {

	private RegistryPaymethodJSON() {

	}
	
	public static List<RegistryPayMethod> fromJSON(JSONArray json) {
		LinkedList<RegistryPayMethod> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryPayMethod fromJSON(JSONObject json) {
		return new RegistryPayMethod()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setPayMethod(PayMethodJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PAYMETHOD)))
				.setRbank(RegistryBankJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.BANK)))
				.setNumberOfPymnts(JsonUtils.getInteger(json, IJsonNames.NUMBER_OF_PYMNTS).shortValue())
				.setDaysToFirstPymnt(JsonUtils.getInteger(json, IJsonNames.DAYS_TO_FIRST_PYMNT).shortValue())
				.setDaysBetwenPymnts(JsonUtils.getInteger(json, IJsonNames.DAYS_BETWEEN_PYMNTS).shortValue())
				.setPymntDays(JsonUtils.getString(json, IJsonNames.PYMNT_DAYS));
	}
	
	public static JSONArray toJSON(List<RegistryPayMethod> rpaymethods) {
		return toJSON(rpaymethods.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryPayMethod> rpaymethods) {
		JSONArray array = new JSONArray();
		rpaymethods.forEach(rpaymethod -> array.put(toJSON(rpaymethod)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryPayMethod rpaymethod) {
		return new JSONObject()
				.put(IJsonNames.ID, rpaymethod.getId())
				.put(IJsonNames.DOMAIN, rpaymethod.getDomain())
				.put(IJsonNames.REGISTRY, rpaymethod.getRegistry())
				.put(IJsonNames.PAYMETHOD, PayMethodJSON.toJSON(rpaymethod.getPayMethod()))
				.put(IJsonNames.BANK, RegistryBankJSON.toJSON(rpaymethod.getRbank()))
				.put(IJsonNames.NUMBER_OF_PYMNTS, rpaymethod.getNumberOfPymnts())
				.put(IJsonNames.DAYS_TO_FIRST_PYMNT, rpaymethod.getDaysToFirstPymnt())
				.put(IJsonNames.DAYS_BETWEEN_PYMNTS, rpaymethod.getDaysBetwenPymnts())
				.put(IJsonNames.PYMNT_DAYS, rpaymethod.getPymntDays());
	}
}
