package es.translogia.tedi.json;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediInsightInvoice;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInsightInvoiceFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInsightInvoiceToJSON;

public enum TediInsightInvoiceJSON {

	NIFS(
		(insight, json) -> {
			JSONArray nifsArray = json.optJSONArray(IConstants.NIFS);
			if (nifsArray != null) {
				TediNif nifs [] = new TediNif [nifsArray.length()];
				for (int i = 0; i < nifsArray.length(); i++ ) {
					nifs[i] = TediNifJSON.fromJSON(nifsArray.getJSONObject(i));
				}
				insight.setNifs(nifs);
			}
			return insight;			
		},
		(insight, json) -> json.put(IConstants.NIFS, insight.getNifs())
		),
	DATES(
		(insight, json) -> {
			JSONArray datesArray = json.optJSONArray(IConstants.DATES);
			if (datesArray != null) {
				Date dates [] = new Date [datesArray.length()];
				for (int i = 0; i < datesArray.length(); i++ ) {
					dates[i] = TediJSONUtils.parseDate(datesArray.getString(i));
				}
				insight.setDates(dates);
			}
			return insight;			
		},
		(insight, json) -> json.put(IConstants.DATES, insight.getDates())
	),
	AMOUNTS(
		(insight, json) -> {
			JSONArray amountsArray = json.optJSONArray(IConstants.AMOUNTS);
			if (amountsArray != null) {
				Double amounts [] = new Double [amountsArray.length()];
				for (int i = 0; i < amountsArray.length(); i++ ) {
					amounts[i] = amountsArray.getDouble(i);
				}
				insight.setAmounts(amounts);
			}
			return insight;			
		},
		(insight, json) -> json.put(IConstants.AMOUNTS, insight.getAmounts())
	),
	ISSUE_DATE(
		(insight, json) -> insight.setIssueDate(TediJSONUtils.parseDate(json.optString(IConstants.ISSUE_DATE))),
		(insight, json) -> TediJSONUtils.putDate(json, IConstants.ISSUE_DATE, insight.getIssueDate())
	),
	TOTAL(
		(insight, json) -> insight.setTotal(TediJSONUtils.optDouble(json, IConstants.TOTAL)),
		(insight, json) -> json.put(IConstants.TOTAL, insight.getTotal())
	),
	
//	REFERENCES(
//		(insight, json) -> {
//			JSONArray referencesArray = json.optJSONArray(IConstants.REFERENCES);
//			if (referencesArray != null) {
//				String references [] = new String [referencesArray.length()];
//				for (int i = 0; i < referencesArray.length(); i++ ) {
//					references[i] = referencesArray.getString(i);
//				}
//				insight.setReferences(references);
//			}
//			return insight;			
//		},
//		(insight, json) -> json.put(IConstants.REFERENCES, insight.getReferences())
//	)
	;

	private ITediInsightInvoiceFromJSON fromJSON;
	private ITediInsightInvoiceToJSON toJSON;

	private TediInsightInvoiceJSON(ITediInsightInvoiceFromJSON fromJSON, ITediInsightInvoiceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediInsightInvoice t) {
		JSONObject json = new JSONObject();
		for (TediInsightInvoiceJSON p : TediInsightInvoiceJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediInsightInvoice fromJSON(JSONObject json) {
		TediInsightInvoice insight = new TediInsightInvoice();
		if (json != null) {
			for (TediInsightInvoiceJSON p : TediInsightInvoiceJSON.values()) {
				p.fromJSON.from(insight, json);
			}
		}
		return insight;
	}

}
