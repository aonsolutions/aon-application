package es.translogia.tedi;

import org.json.JSONObject;

public class TediPlan {

	public static final String SRC = "/company";
	
	private String plan;
	private String period;

	
	public TediPlan() {}
	
	public TediPlan(JSONObject json) {
		this.plan = json.getString("plan");
		this.period = json.getString("period");
	}
	
	public String getPlan() {
		return plan;
	}
	public TediPlan setPlan(String plan) {
		this.plan = plan;
		return this;
	}
	public String getPeriod() {
		return period;
	}

	public TediPlan setPeriod(String period) {
		this.period = period;
		return this;
	}

	public JSONObject getJSON(){
		return new JSONObject()
			.put("plan", getPlan())
			.put("period", getPeriod());
	}

}
