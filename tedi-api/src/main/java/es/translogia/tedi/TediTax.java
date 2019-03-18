package es.translogia.tedi;

import org.json.JSONObject;

public class TediTax {
	public TediTax() {}
	
	public TediTax(JSONObject json) {
		if(json != null) {
			this.tax = json.optString("tax");
			this.percentage = json.optDouble("percentage");
			this.quota = json.optDouble("quota");
			this.base = json.optDouble("base");	
			this.surcharge = json.optDouble("surcharge");
			this.surchargeQuota = json.optDouble("surcharge_quota");
		}
	}
	
	private String tax;
	private Double base;
	private Double percentage;
	private Double quota;
	private Double surcharge;
	private Double surchargeQuota;
	
	public String getTax() {
		return tax;
	}
	public TediTax setTaxType(String tax) {
		this.tax = tax;
		return this;
	}
	public Double getBase() {
		return base;
	}
	public TediTax setBase(Double base) {
		this.base = base;
		return this;
	}
	public Double getPercentage() {
		return percentage;
	}
	public TediTax setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}
	public Double getQuota() {
		return quota;
	}
	public TediTax setQuota(Double quota) {
		this.quota = quota;
		return this;
	}

	public Double getSurcharge() {
		return surcharge;
	}

	public TediTax setSurcharge(Double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public Double getSurchargeQuota() {
		return surchargeQuota;
	}

	public TediTax setSurchargeQuota(Double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}
	
}
