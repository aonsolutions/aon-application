package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonAddressFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonAddressToJSON;


public enum AonAddressJSON {
	ID(
		(address, json) -> address.setId(json.optInt(IConstants.ID)),
		(address, json) -> json.put(IConstants.ID, address.getId())
	),
	ADDRESS(
		(address, json) -> address.setAddress(json.optString(IConstants.ADDRESS)),
		(address, json) -> json.put(IConstants.ADDRESS, address.getFullAddress())
	),
	CITY(
		(address, json) -> address.setCity(json.optString(IConstants.CITY)),
		(address, json) -> json.put(IConstants.CITY, address.getCity())
	),
	COUNTRY(
		(address, json) -> address.setCountry(json.optEnum(Country.class, IConstants.COUNTRY)),
		(address, json) -> json.put(IConstants.COUNTRY, address.getCountry())
	),
	PROVINCE(
		(address, json) -> {
			Country c = json.optEnum(Country.class, IConstants.COUNTRY);
			String zip = json.optString(IConstants.POSTAL_CODE);
			if(Country.ES.equals(c) &&  zip != null && !zip.isBlank()) {
				String code = json.optString(IConstants.POSTAL_CODE).substring(0,2);
				address.setGeozoneCode(code);
			}
			address.setGeozoneName(json.optString(IConstants.PROVINCE));
			return address;
		},
		(address, json) -> json.put(IConstants.PROVINCE, address.getGeozoneName())
	),
	POSTAL_CODE(
		(address, json) -> address.setZip(json.optString(IConstants.POSTAL_CODE)),
		(address, json) -> json.put(IConstants.POSTAL_CODE, address.getZip())
	);
	
	private IAonAddressFromJSON fromJSON;
	private IAonAddressToJSON toJSON;

	private AonAddressJSON(IAonAddressFromJSON fromJSON, IAonAddressToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(RAddress t) {
		JSONObject json = new JSONObject();
		for (AonAddressJSON p : AonAddressJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}
	
	public static RAddress fromJSON(JSONObject json) {
		RAddress address = new RAddress();
		address.getGeozoneCode();
		if (json != null) {
			for (AonAddressJSON p : AonAddressJSON.values()) {
				p.fromJSON.from(address, json);
			}
		}

		return address;
	}

}
