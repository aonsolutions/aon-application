package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.json.FunctionalInterfaces.ITediRegistryFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediRegistryToJSON;

public enum TediRegistryJSON {

	DOCUMENT(
		(registry, json) -> registry.setDocument(json.optString(IConstants.DOCUMENT)),
		(registry, json) -> json.put(IConstants.DOCUMENT, registry.getDocument())
	),
	DOCUMENT_COUNTRY(
		(registry, json) -> registry.setDocumentCountry(json.optString(IConstants.DOCUMENT_COUNTRY)),
		(registry, json) -> json.put(IConstants.DOCUMENT_COUNTRY, registry.getDocumentCountry())
	),
	NAME(
		(registry, json) -> registry.setName(json.optString(IConstants.NAME)),
		(registry, json) -> json.put(IConstants.NAME, registry.getName())
	),
	ADDRESS(
		(registry, json) -> {
			JSONObject jsonAddress = json.optJSONObject(IConstants.ADDRESS);
			return (jsonAddress != null) ? registry.setAddress(TediAddressJSON.fromJSON(jsonAddress)) : registry;
		}, 
		(registry, json) -> (registry.getAddress() != null)
			? json.put(IConstants.ADDRESS, TediAddressJSON.toJSON(registry.getAddress()))
			: json
	);

	private ITediRegistryFromJSON fromJSON;
	private ITediRegistryToJSON toJSON;

	private TediRegistryJSON(ITediRegistryFromJSON fromJSON, ITediRegistryToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediRegistry t) {
		JSONObject json = new JSONObject();
		for (TediRegistryJSON p : TediRegistryJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediRegistry fromJSON(JSONObject json) {
		TediRegistry registry = new TediRegistry();
		if (json != null) {
			for (TediRegistryJSON p : TediRegistryJSON.values()) {
				p.fromJSON.from(registry, json);
			}
		}
		return registry;
	}

}
