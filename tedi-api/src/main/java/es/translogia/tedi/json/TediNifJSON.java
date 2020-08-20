package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.json.FunctionalInterfaces.ITediNifFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediNifToJSON;

public enum TediNifJSON {

	STR(
		(nif, json) -> nif.setStr(json.optString(IConstants.STR)),
		(nif, json) -> json.put(IConstants.STR, nif.getStr())
	),
	TYPE(
		(nif, json) -> nif, 
		(nif, json) -> json.put(IConstants.FROM, nif.getType())
	);

	private ITediNifFromJSON fromJSON;
	private ITediNifToJSON toJSON;

	private TediNifJSON(ITediNifFromJSON fromJSON, ITediNifToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediNif t) {
		JSONObject json = new JSONObject();
		for (TediNifJSON p : TediNifJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediNif fromJSON(JSONObject json) {
		TediNif nif = new TediNif();
		if (json != null) {
			for (TediNifJSON p : TediNifJSON.values()) {
				p.fromJSON.from(nif, json);
			}
		}
		return nif;
	}

}
