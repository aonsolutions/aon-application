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
		return new OCRAddress()
			.setText(OCRJSONUtils.getString(json, OCRNames.TEXT))
			.setAddressNumber(OCRJSONUtils.getString(json, OCRNames.ADDRESS_NUMBER))
			.setCountry(OCRJSONUtils.getString(json, OCRNames.COUNTRY))
			.setMunicipality(OCRJSONUtils.getString(json, OCRNames.MUNICIPALITY))
			.setNeighborhood(OCRJSONUtils.getString(json, OCRNames.NEIGHBORHOOD))
			.setPostalCode(OCRJSONUtils.getString(json, OCRNames.POSTAL_CODE))
			.setRegion(OCRJSONUtils.getString(json, OCRNames.REGION))
			.setStreet(OCRJSONUtils.getString(json, OCRNames.STREET))
			.setSubRegion(OCRJSONUtils.getString(json, OCRNames.SUB_REGION))
		;
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
			.putOpt(OCRNames.TEXT, address.getText().orElse(null))
			.putOpt(OCRNames.ADDRESS_NUMBER, address.getAddressNumber().orElse(null))
			.putOpt(OCRNames.COUNTRY, address.getCountry().orElse(null))
			.putOpt(OCRNames.MUNICIPALITY, address.getMunicipality().orElse(null))
			.putOpt(OCRNames.NEIGHBORHOOD, address.getNeighborhood().orElse(null))
			.putOpt(OCRNames.POSTAL_CODE, address.getPostalCode().orElse(null))
			.putOpt(OCRNames.REGION, address.getRegion().orElse(null))
			.putOpt(OCRNames.STREET, address.getStreet().orElse(null))
			.putOpt(OCRNames.SUB_REGION, address.getSubRegion().orElse(null))
		;
	}
}
