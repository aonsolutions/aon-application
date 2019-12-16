package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediPlan;
import es.translogia.tedi.json.FunctionalInterfaces.ITediPlanFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediPlanToJSON;

public enum TediPlanJSON {
	PLAN(
		(plan, json) -> plan.setPlan(json.optString(IConstants.PLAN)),
		(plan, json) -> json.put(IConstants.PLAN, plan.getPlan())
	),
	PERIOD(
		(plan, json) -> plan.setPeriod(json.optString(IConstants.PERIOD)),
		(plan, json) -> json.put(IConstants.PERIOD, plan.getPeriod())
	),
	PROMO(
		(plan, json) -> plan.setPromo(json.optString(IConstants.PROMO)),
		(plan, json) -> json.put(IConstants.PROMO, plan.getPeriod())
	);
	
	private ITediPlanFromJSON fromJSON;
	private ITediPlanToJSON toJSON;

	private TediPlanJSON(ITediPlanFromJSON fromJSON, ITediPlanToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediPlan t) {
		JSONObject json = new JSONObject();
		for (TediPlanJSON p : TediPlanJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}
	
	public static TediPlan fromJSON(JSONObject json) {
		TediPlan emailInfo = new TediPlan();
		if (json != null) {
			for (TediPlanJSON p : TediPlanJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}

}
