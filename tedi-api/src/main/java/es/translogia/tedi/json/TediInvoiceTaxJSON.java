package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceTaxFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceTaxToJSON;

public enum TediInvoiceTaxJSON {

	TAX(
		(invoiceTax, json) -> invoiceTax.setTaxType(json.optEnum(TediTaxType.class, IConstants.TAX)),
		(invoiceTax, json) -> json.put(IConstants.TAX, invoiceTax.getTaxType())
	),
	BASE(
		(tax, json) -> tax.setBase(TediJSONUtils.optDouble(json, IConstants.BASE)),
		(tax, json) -> json.put(IConstants.BASE, tax.getBase())
	),
	PERCENTAGE(
		(tax, json) -> tax.setPercentage(TediJSONUtils.optDouble(json, IConstants.PERCENTAGE)),
		(tax, json) -> json.put(IConstants.PERCENTAGE, tax.getPercentage())
	),
	QUOTA(
		(tax, json) -> tax.setQuota(TediJSONUtils.optDouble(json, IConstants.QUOTA)),
		(tax, json) -> json.put(IConstants.QUOTA, tax.getQuota())
	),
	SURCHARGE(
		(tax, json) -> tax.setSurcharge(TediJSONUtils.optDouble(json, IConstants.SURCHARGE)),
		(tax, json) -> json.put(IConstants.SURCHARGE, tax.getSurcharge())
	),
	SURCHARGE_QUOTA(
		(tax, json) -> tax.setSurchargeQuota(TediJSONUtils.optDouble(json, IConstants.SURCHARGE_QUOTA)),
		(tax, json) -> {
			json.put(IConstants.SURCHARGE_QUOTA, tax.getSurchargeQuota());
				return json;
		}
	);

	private ITediInvoiceTaxFromJSON fromJSON;
	private ITediInvoiceTaxToJSON toJSON;

	private TediInvoiceTaxJSON(ITediInvoiceTaxFromJSON fromJSON, ITediInvoiceTaxToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediInvoiceTax t) {
		JSONObject json = new JSONObject();
		for (TediInvoiceTaxJSON p : TediInvoiceTaxJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediInvoiceTax fromJSON(JSONObject json) {
		TediInvoiceTax tax = new TediInvoiceTax();
		if (json != null) {
			for (TediInvoiceTaxJSON p : TediInvoiceTaxJSON.values()) {
				p.fromJSON.from(tax, json);
			}
		}
		return tax;
	}
}
