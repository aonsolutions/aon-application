package com.esferalia.aon.occam.api.json.invoice;

import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.JsonVersion;
import com.esferalia.aon.occam.api.json.JsonVersion.JsonVersionVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public class InvoiceJSON {
	
	private InvoiceJSON() {
	
	}
	
	// ---------------------------------------------------------------
	// ----------------------------------------------------- [FROM] --
	// ---------------------------------------------------------------
	public static Optional<Invoice> from(JSONObject json) {
		return JsonUtils.getVersion(json)
			.map(v -> from(v, json) )
			.orElse( Optional.ofNullable(InvoiceJSONV1.fromJSON(json)) )
		;
	}
	public static Optional<Invoice> from(JsonVersion v, JSONObject json) {
		if (v == null) return Optional.ofNullable(InvoiceJSONV1.fromJSON(json));
		return v.visit( new JsonVersionVisitor<Optional<Invoice>>() {

			@Override
			public Optional<Invoice> visitV1() {
				return Optional.ofNullable(InvoiceJSONV1.fromJSON(json));
			}

			@Override
			public Optional<Invoice> visitV2() {
				return InvoiceJSONV2.from(json);
			}
		});
	}

	// -------------------------------------------------------------
	// ----------------------------------------------------- [TO] --
	// -------------------------------------------------------------
	public static Optional<JSONObject> to(Invoice invoice) {
		return Optional.ofNullable(InvoiceJSONV1.toJSON(invoice));
	}
	
	public static Optional<JSONObject> to(JsonVersion v, Invoice invoice) {
		if (v == null) return Optional.ofNullable(InvoiceJSONV1.toJSON(invoice));
		return v.visit( new JsonVersionVisitor<Optional<JSONObject>>() {

			@Override
			public Optional<JSONObject> visitV1() {
				return Optional.ofNullable(InvoiceJSONV1.toJSON(invoice));
			}

			@Override
			public Optional<JSONObject> visitV2() {
				return InvoiceJSONV2.to(invoice);
			}
		});
	}
	

	// ---------------------------------------------------------------------------
	// ----------------------------------------------------- [PREVIOUS METHODS] --
	// ---------------------------------------------------------------------------
	public static Invoice fromJSON(String json) {
		return InvoiceJSONV1.fromJSON(json);
	}
	
	public static Invoice fromJSON(JSONObject json) {
		return InvoiceJSONV1.fromJSON(json);
	}

	public static JSONArray toJSON(List<Invoice> invoices) {
		return InvoiceJSONV1.toJSON(invoices);
	}
	
	public static JSONObject toJSON(Invoice invoice) {
		return InvoiceJSONV1.toJSON(invoice);
	}
	
}
