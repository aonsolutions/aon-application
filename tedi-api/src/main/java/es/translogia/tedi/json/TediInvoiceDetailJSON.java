package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceDetailFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceDetailToJSON;

public enum TediInvoiceDetailJSON {

	DESCRIPTION(
		(detail, json) -> detail.setDescription(json.optString(IConstants.DESCRIPTION)),
		(detail, json) -> json.put(IConstants.DESCRIPTION, detail.getDescription())
	),
	QUANTITY(
		(detail, json) -> detail.setQuantity(TediJSONUtils.optDouble(json, IConstants.QUANTITY)),
		(detail, json) -> json.put(IConstants.QUANTITY, detail.getQuantity())
	),
	PRICE(
		(detail, json) -> detail.setPrice(TediJSONUtils.optDouble(json, IConstants.PRICE)),
		(detail, json) -> json.put(IConstants.PRICE, detail.getPrice())
	),
	DISCOUNT(
		(detail, json) -> detail.setDiscount(TediJSONUtils.optDouble(json, IConstants.DISCOUNT)),
		(detail, json) -> json.put(IConstants.DISCOUNT, detail.getDiscount())
	),
	BASE(
		(detail, json) -> detail.setBase(TediJSONUtils.optDouble(json, IConstants.BASE)),
		(detail, json) -> json.put(IConstants.BASE, detail.getBase())
	),
	VAT(
		(detail, json) -> detail.setVat(TediJSONUtils.optDouble(json, IConstants.VAT)),
		(detail, json) -> json.put(IConstants.VAT, detail.getVat())
	),
	SURCHARGE(
		(detail, json) -> detail.setSurcharge(TediJSONUtils.optDouble(json, IConstants.SURCHARGE)),
		(detail, json) -> json.put(IConstants.SURCHARGE, detail.getSurcharge())
	);

	private ITediInvoiceDetailFromJSON fromJSON;
	private ITediInvoiceDetailToJSON toJSON;

	private TediInvoiceDetailJSON(ITediInvoiceDetailFromJSON fromJSON, ITediInvoiceDetailToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediInvoiceDetail t) {
		JSONObject json = new JSONObject();
		for (TediInvoiceDetailJSON p : TediInvoiceDetailJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediInvoiceDetail fromJSON(JSONObject json) {
		TediInvoiceDetail emailInfo = new TediInvoiceDetail();
		if (json != null) {
			for (TediInvoiceDetailJSON p : TediInvoiceDetailJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}
}
