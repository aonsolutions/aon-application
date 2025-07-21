package com.esferalia.aon.occam.api.json.invoice;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON.InvoiceJSONVersion.JsonVersionVisitor;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class InvoiceJSON {
	
	private InvoiceJSON() {
	
	}
	
	public enum InvoiceJSONVersion implements Serializable  {

		 V1 {@Override public <T> T visit(JsonVersionVisitor<T> visitor) {return visitor.visitV1();}}
		,V2 {@Override public <T> T visit(JsonVersionVisitor<T> visitor) {return visitor.visitV2();}}
		;
		
		public byte value() {
			return (byte) this.ordinal();
		}
		
		public abstract <T> T visit(JsonVersionVisitor<T> visitor);
		
		public interface JsonVersionVisitor<T> {
			public T visitV1();		
			public T visitV2();
		}

		public static Optional<InvoiceJSONVersion> safeValueOf(String v) {
			return AonCollectionUtils.stream(values())
				.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), v))
				.findFirst();
		}
		
		
	}	

	public static Optional<InvoiceJSONVersion> getVersion(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.of(InvoiceJSONVersion.V1);
		return InvoiceJSONVersion.safeValueOf( JsonUtils.getString(json, IJsonNames.VERSION) );
	}
	
	// ---------------------------------------------------------------
	// ----------------------------------------------------- [FROM] --
	// ---------------------------------------------------------------
	public static Optional<Invoice> from(JSONObject json) {
		return getVersion(json)
			.map(v -> from(v, json) )
			.orElse( Optional.ofNullable(InvoiceJSONV1.fromJSON(json)) )
		;
	}
	public static Optional<Invoice> from(InvoiceJSONVersion v, JSONObject json) {
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
	
	public static Optional<JSONObject> to(InvoiceJSONVersion v, Invoice invoice) {
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
