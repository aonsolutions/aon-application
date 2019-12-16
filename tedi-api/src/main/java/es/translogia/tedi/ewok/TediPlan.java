package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediPlan implements Serializable {

	private static final long serialVersionUID = -9103976053890088583L;

	private String plan;
	private String period;
	private String promo;

	public TediPlan() {}
	
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

	public String getPromo() {
		return promo;
	}

	public TediPlan setPromo(String promo) {
		this.promo = promo;
		return this;
	}
	
	

}
