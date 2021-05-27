package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediComments;
import es.translogia.tedi.json.FunctionalInterfaces.ITediCommentsFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediCommentsToJSON;

public enum TediCommentsJSON {

	DATE(
		(comments, json) -> comments.setDate(TediJSONUtils.parseDate(json.optString(IConstants.DATE))),
		(comments, json) -> TediJSONUtils.put(json, IConstants.DATE, comments.getDate())
	),
	COMMENTS(
		(comments, json) -> comments.setComment(json.optString(IConstants.COMMENT)),
		(comments, json) -> json.put(IConstants.COMMENT, comments.getComment())
	),
	USER(
		(comments, json) -> comments.setUser(json.optString(IConstants.USER)),
		(comments, json) -> json.put(IConstants.USER, comments.getUser())
	);

	private ITediCommentsFromJSON fromJSON;
	private ITediCommentsToJSON toJSON;

	private TediCommentsJSON(ITediCommentsFromJSON fromJSON, ITediCommentsToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediComments t) {
		JSONObject json = new JSONObject();
		for (TediCommentsJSON p : TediCommentsJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediComments fromJSON(JSONObject json) {
		TediComments emailInfo = new TediComments();
		if (json != null) {
			for (TediCommentsJSON p : TediCommentsJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}

}
