package es.translogia.tedi;

import org.json.JSONObject;

public class TediTax {
	public TediTax() {}
	
	public TediTax(JSONObject json) {
		
	}
	
	private String taxType;
	private Double base;
	private Double percentage;
	private Double quota;
	private Double surcharge;
	private Double surchargeQuota;
	
	public String getTaxType() {
		return taxType;
	}
	public TediTax setTaxType(String taxType) {
		this.taxType = taxType;
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
