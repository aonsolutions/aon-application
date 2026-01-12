package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.esferalia.aon.jooq.tables.InvoiceTracking.INVOICE_TRACKING;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceTrackingStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class InvoiceTrackingDAO {
	
	private InvoiceTrackingDAO() {
		
	}
	
	static Optional<Invoice> get(AONContext ctx, Integer domain, Integer id) {
		return ctx.getDslContext()
			.select(INVOICE_TRACKING.JSON)
			.from(INVOICE_TRACKING)
			.where(INVOICE_TRACKING.DOMAIN.eq(domain))
			.and(INVOICE_TRACKING.ID.eq(id))
			.fetch()
			.stream()
			.findFirst()
			.map(r -> r.get(INVOICE_TRACKING.JSON) )
			.filter(AonStringUtils::isNotEmpty)
			.map(JSONObject::new)
			.filter(Objects::nonNull )
			.flatMap(InvoiceJSON::from)
			.map(i -> i.setAnnulled(true) )
		;
					
	}
	
	static void insert(AONContext ctx, Invoice invoice, InvoiceTrackingStatus status) {
		invoice.setComments(null);
		invoice.setRemarks(null);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		
		ctx.getDslContext().insertInto(INVOICE_TRACKING)
		.set(INVOICE_TRACKING.ID, invoice.getId())
		.set(INVOICE_TRACKING.DOMAIN, invoice.getDomain())
		.set(INVOICE_TRACKING.SERIES, invoice.getSeries())
		.set(INVOICE_TRACKING.NUMBER, invoice.getNumber())
		.set(INVOICE_TRACKING.REFERENCE_CODE, invoice.getReferenceCode())
		.set(INVOICE_TRACKING.ISSUE_DATE, AonDateUtils.toSql(invoice.getIssueDate()))
		.set(INVOICE_TRACKING.RDOCUMENT, invoice.getRegistryDocument())
		.set(INVOICE_TRACKING.RNAME, invoice.getRegistryName())
		.set(INVOICE_TRACKING.STATUS, status.value())
		.set(INVOICE_TRACKING.TYPE, invoice.getType().value())
		.set(INVOICE_TRACKING.TOTAL, invoice.getTotal())
		.set(INVOICE_TRACKING.JSON, json.toString())
		.set(INVOICE_TRACKING.CREATION_USER, invoice.getCreationUser())
		.set(INVOICE_TRACKING.CREATION_DATE, AonDateUtils.toTimestamp(invoice.getCreationDate()))
		.set(INVOICE_TRACKING.MODIFICATION_USER, ctx.getUser())
		.set(INVOICE_TRACKING.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
		.execute();
	}


}
