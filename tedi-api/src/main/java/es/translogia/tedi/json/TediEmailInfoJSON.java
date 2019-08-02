package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediEmailInfo;
import es.translogia.tedi.json.FunctionalInterfaces.ITediEmailInfoFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediEmailInfoToJSON;

public enum TediEmailInfoJSON {

	ID(
		(emailInfo, json) -> emailInfo.setId(json.optString(IConstants.ID)),
		(emailInfo, json) -> json.put(IConstants.ID, emailInfo.getId())
	),
	FROM(
		(emailInfo, json) -> {
			Object o = json.opt(IConstants.FROM);
			if (o != null && o instanceof String[]) {
				emailInfo.setFrom((String[]) o);
			}
			return emailInfo;
		}, 
		(emailInfo, json) -> json.put(IConstants.FROM, emailInfo.getFrom())
	),
	ALIAS(
		(emailInfo, json) -> emailInfo.setFileName(json.optString(IConstants.FILE_NAME)),
		(emailInfo, json) -> json.put(IConstants.FILE_NAME, emailInfo.getFileName())
	);

	private ITediEmailInfoFromJSON fromJSON;
	private ITediEmailInfoToJSON toJSON;

	private TediEmailInfoJSON(ITediEmailInfoFromJSON fromJSON, ITediEmailInfoToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediEmailInfo t) {
		JSONObject json = new JSONObject();
		for (TediEmailInfoJSON p : TediEmailInfoJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediEmailInfo fromJSON(JSONObject json) {
		TediEmailInfo emailInfo = new TediEmailInfo();
		if (json != null) {
			for (TediEmailInfoJSON p : TediEmailInfoJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}

}
