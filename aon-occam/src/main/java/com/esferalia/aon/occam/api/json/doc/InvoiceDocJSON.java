package com.esferalia.aon.occam.api.json.doc;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;

public class InvoiceDocJSON {

	private InvoiceDocJSON() {
	}
	
	// ***********************************************************
	// ************************************************ [FROM] ***
	// ***********************************************************
	public static Optional<InvoiceDoc> from(JSONObject json) {
		return from(json, InvoiceDoc::new);
	}
	public static Optional<InvoiceDoc> from(JSONObject json, Supplier<InvoiceDoc> docSupplier) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		InvoiceDoc doc = docSupplier.get();
		ExternalDocJSON.from(json, () -> doc);
		return Optional.of(doc
			.setInvoice(JsonUtils.getInteger(json, IJsonNames.INVOICE))
		);
	}
	
	// ***********************************************************
	// ************************************************** [TO] ***
	// ***********************************************************
	public static Optional<JSONObject> to(Optional<InvoiceDoc> doc) {
		return doc.flatMap(d -> to(d));
	}
	public static Optional<JSONObject> to(InvoiceDoc doc) {
		return to(doc, JSONObject::new);
	}
	public static Optional<JSONObject> to(InvoiceDoc doc, Supplier<JSONObject> jsonSupplier) {
		if (doc == null) return Optional.empty();
		return ExternalDocJSON.to(doc, jsonSupplier )
			.map(json -> json
				.put(IJsonNames.INVOICE, doc.getInvoice() )
		);
	}


}
