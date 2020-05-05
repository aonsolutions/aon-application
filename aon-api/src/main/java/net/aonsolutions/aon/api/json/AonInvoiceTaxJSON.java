package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.type.TaxType;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceTaxFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceTaxToJSON;

public enum AonInvoiceTaxJSON {

	TAX(
		(tax, json) -> tax.setTaxType(json.optEnum(TaxType.class, IConstants.TAX)),
		(tax, json) -> json.put(IConstants.TAX, tax.getTaxType())
	),
	BASE(
		(tax, json) -> tax.setBase(json.optDouble(IConstants.BASE)),
		(tax, json) -> json.put(IConstants.BASE, tax.getBase())
	),
	PERCENTAGE(
		(tax, json) -> tax.setPercentage(json.optDouble(IConstants.PERCENTAGE)),
		(tax, json) -> json.put(IConstants.PERCENTAGE, tax.getPercentage())
	),
	QUOTA(
		(tax, json) -> tax.setQuota(json.optDouble(IConstants.QUOTA)),
		(tax, json) -> json.put(IConstants.QUOTA, tax.getQuota())
	),
	SURCHARGE(
		(tax, json) -> tax.setSurcharge(json.optDouble(IConstants.SURCHARGE)),
		(tax, json) -> json.put(IConstants.SURCHARGE, tax.getSurcharge())
	),
	SURCHARGE_QUOTA(
		(tax, json) -> tax.setSurchargeQuota(json.optDouble(IConstants.SURCHARGE_QUOTA)),
		(tax, json) -> {
			json.put(IConstants.SURCHARGE_QUOTA, tax.getSurchargeQuota());
				return json;
		}
	);

	private IAonInvoiceTaxFromJSON fromJSON;
	private IAonInvoiceTaxToJSON toJSON;

	private AonInvoiceTaxJSON(IAonInvoiceTaxFromJSON fromJSON, IAonInvoiceTaxToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(InvoiceBreakdown t) {
		JSONObject json = new JSONObject();
		for (AonInvoiceTaxJSON p : AonInvoiceTaxJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static InvoiceBreakdown fromJSON(JSONObject json) {
		InvoiceBreakdown tax = new InvoiceBreakdown();
		if (json != null) {
			for (AonInvoiceTaxJSON p : AonInvoiceTaxJSON.values()) {
				p.fromJSON.from(tax, json);
			}
		}
		return tax;
	}
}
