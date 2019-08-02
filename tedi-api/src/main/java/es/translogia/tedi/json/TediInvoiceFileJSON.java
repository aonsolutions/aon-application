package es.translogia.tedi.json;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediInvoiceFile;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceFileFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceFileToJSON;

public enum TediInvoiceFileJSON {

	URL(
		(invoiceFile, json) -> invoiceFile.setUrl(json.optString(IConstants.URL)),
		(invoiceFile, json) -> json.put(IConstants.URL, invoiceFile.getUrl())
	),
	THUMB_URL(
		(invoiceFile, json) -> invoiceFile.setThumbUrl(json.optString(IConstants.THUMB_URL)),
		(invoiceFile, json) -> json.put(IConstants.THUMB_URL, invoiceFile.getThumbUrl())
	),
	CONTENT_TYPE(
		(invoiceFile, json) -> invoiceFile.setContentType(json.optString(IConstants.CONTENT_TYPE)),
		(invoiceFile, json) -> json.put(IConstants.CONTENT_TYPE, invoiceFile.getContentType())
	);

	private ITediInvoiceFileFromJSON fromJSON;
	private ITediInvoiceFileToJSON toJSON;

	private TediInvoiceFileJSON(ITediInvoiceFileFromJSON fromJSON, ITediInvoiceFileToJSON toJSON) {
			this.fromJSON = fromJSON;
			this.toJSON = toJSON;
		}

	public static JSONObject toJSON(TediInvoiceFile t) {
		JSONObject json = new JSONObject();
		for (TediInvoiceFileJSON p : TediInvoiceFileJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediInvoiceFile fromJSON(JSONObject json) {
		TediInvoiceFile invoiceFile = new TediInvoiceFile();
		if (json != null) {
			for (TediInvoiceFileJSON p : TediInvoiceFileJSON.values()) {
				p.fromJSON.from(invoiceFile, json);
			}
		}
		return invoiceFile;
	}
}
