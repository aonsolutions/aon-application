package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonRegistryFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonRegistryToJSON;

public enum AonRegistryJSON {

	ID(
		(registry, json) -> registry.setId(json.optInt(IConstants.ID)),
		(registry, json) -> json.put(IConstants.ID, registry.getId())
	),
	DOCUMENT(
		(registry, json) -> registry.setDocument(json.optString(IConstants.DOCUMENT)),
		(registry, json) -> json.put(IConstants.DOCUMENT, registry.getDocument())
	),
	DOCUMENT_COUNTRY(
		(registry, json) -> registry.setDocumentCountry(json.optEnum(Country.class, IConstants.DOCUMENT_COUNTRY)),
		(registry, json) -> json.put(IConstants.DOCUMENT_COUNTRY, registry.getDocumentCountry())
	),
	NAME(
		(registry, json) -> registry.setName(json.optString(IConstants.NAME)),
		(registry, json) -> json.put(IConstants.NAME, registry.getName())
	),
	ADDRESS(
		(registry, json) -> {
			JSONObject jsonAddress = json.optJSONObject(IConstants.ADDRESS);
			return (jsonAddress != null) ? registry.setAddress(AonAddressJSON.fromJSON(jsonAddress)) : registry;
		}, 
		(registry, json) -> (registry.getAddress() != null)
			? json.put(IConstants.ADDRESS, AonAddressJSON.toJSON(registry.getAddress()))
			: json
	);

	private IAonRegistryFromJSON fromJSON;
	private IAonRegistryToJSON toJSON;

	private AonRegistryJSON(IAonRegistryFromJSON fromJSON, IAonRegistryToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(Registry t) {
		JSONObject json = new JSONObject();
		for (AonRegistryJSON p : AonRegistryJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static Registry fromJSON(JSONObject json) {
		Registry registry = new Registry().setAddress(new RAddress());
		if (json != null) {
			for (AonRegistryJSON p : AonRegistryJSON.values()) {
				p.fromJSON.from(registry, json);
			}
		}
		return registry;
	}

}
