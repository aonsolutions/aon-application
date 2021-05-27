package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediInvoiceTax implements Serializable{

	private static final long serialVersionUID = 3247628476726126678L;
	
	private TediTaxType taxType;
	private Double base;
	private Double percentage;
	private Double quota;
	private Double surcharge;
	private Double surchargeQuota;

	public TediTaxType getTaxType() {
		return taxType;
	}

	public TediInvoiceTax setTaxType(TediTaxType tax) {
		this.taxType = tax;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public TediInvoiceTax setBase(Double base) {
		this.base = base;
		return this;
	}

	public Double getPercentage() {
		return percentage;
	}

	public TediInvoiceTax setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}

	public Double getQuota() {
		return quota;
	}

	public TediInvoiceTax setQuota(Double quota) {
		this.quota = quota;
		return this;
	}

	public Double getSurcharge() {
		return surcharge;
	}

	public TediInvoiceTax setSurcharge(Double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public Double getSurchargeQuota() {
		return surchargeQuota;
	}

	public TediInvoiceTax setSurchargeQuota(Double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}

}
