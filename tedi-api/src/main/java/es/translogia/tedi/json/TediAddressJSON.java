package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.json.FunctionalInterfaces.ITediAddressFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediAddressToJSON;

public enum TediAddressJSON {
	ADDRESS(
		(address, json) -> address.setAddress(json.optString(IConstants.ADDRESS)),
		(address, json) -> json.put(IConstants.ADDRESS, address.getAddress())
	),
	CITY(
		(address, json) -> address.setCity(json.optString(IConstants.CITY)),
		(address, json) -> json.put(IConstants.CITY, address.getCity())
	),
	COUNTRY(
		(address, json) -> address.setCountry(json.optString(IConstants.COUNTRY)),
		(address, json) -> json.put(IConstants.COUNTRY, address.getCountry())
	),
	PROVINCE(
		(address, json) -> address.setProvince(json.optString(IConstants.PROVINCE)),
		(address, json) -> json.put(IConstants.PROVINCE, address.getProvince())
	),
	POSTAL_CODE(
		(address, json) -> address.setPostalCode(json.optString(IConstants.POSTAL_CODE)),
		(address, json) -> json.put(IConstants.POSTAL_CODE, address.getPostalCode())
	);
	
	private ITediAddressFromJSON fromJSON;
	private ITediAddressToJSON toJSON;

	private TediAddressJSON(ITediAddressFromJSON fromJSON, ITediAddressToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediAddress t) {
		JSONObject json = new JSONObject();
		for (TediAddressJSON p : TediAddressJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}
	
	public static TediAddress fromJSON(JSONObject json) {
		TediAddress emailInfo = new TediAddress();
		if (json != null) {
			for (TediAddressJSON p : TediAddressJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}

}
