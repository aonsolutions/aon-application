package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediFile;
import es.translogia.tedi.json.FunctionalInterfaces.ITediFileFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediFileToJSON;

public enum TediFileJSON {
	ID(
		(file, json) -> file.setId(json.optInt(IConstants.ID)),
		(file, json) -> json.put(IConstants.ID, file.getId())
	),
	ATTACH_TYPE(
		(file, json) -> file.setAttachType(json.optString(IConstants.ATTACH_TYPE)),
		(file, json) -> json.put(IConstants.ATTACH_TYPE, file.getAttachType())
	),
	URL(
		(file, json) -> file.setUrl(json.optString(IConstants.URL)),
		(file, json) -> json.put(IConstants.URL, file.getUrl())
	),
	THUMB_URL(
		(file, json) -> file.setThumbUrl(json.optString(IConstants.THUMB_URL)),
		(file, json) -> json.put(IConstants.THUMB_URL, file.getThumbUrl())
	),
	CONTENT_TYPE(
		(file, json) -> file.setContentType(json.optString(IConstants.CONTENT_TYPE)),
		(file, json) -> json.put(IConstants.CONTENT_TYPE, file.getContentType())
	);

	private ITediFileFromJSON fromJSON;
	private ITediFileToJSON toJSON;

	private TediFileJSON(ITediFileFromJSON fromJSON, ITediFileToJSON toJSON) {
			this.fromJSON = fromJSON;
			this.toJSON = toJSON;
		}

	public static JSONObject toJSON(TediFile t) {
		JSONObject json = new JSONObject();
		for (TediFileJSON p : TediFileJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediFile fromJSON(JSONObject json) {
		TediFile file = new TediFile();
		if (json != null) {
			for (TediFileJSON p : TediFileJSON.values()) {
				p.fromJSON.from(file, json);
			}
		}
		return file;
	}
}
