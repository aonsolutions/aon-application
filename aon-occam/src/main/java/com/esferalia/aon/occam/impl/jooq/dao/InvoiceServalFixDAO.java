package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataRequest.DATA_REQUEST;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;

public class InvoiceServalFixDAO {
	
	public static List<Invoice> getTbaiDeletedInvoices(AONContext ctx) {
		Collection<Integer> ids = ctx.getDslContext().select(DATA_RESPONSE.DATA_REQUEST)
			.from(DATA_RESPONSE)
			.leftOuterJoin(INVOICE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID))
			.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()))
				.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.TBAI.value()))
				.and(INVOICE.ID.isNull())
			.fetch().stream().map(r -> r.getValue(DATA_RESPONSE.DATA_REQUEST))
			.collect(Collectors.toCollection(LinkedList::new));
		
		return ctx.getDslContext().select()
			.from(DATA_REQUEST)
			.where(DATA_REQUEST.ID.in(ids))
			.fetch().stream().map(r -> {
				String blackBox = r.getValue(DATA_REQUEST.BLACK_BOX);
				JSONObject bbJson = new JSONObject(blackBox);
				Invoice invoice = new Invoice();
				if(bbJson.opt("invoice") != null) {
					invoice = InvoiceJSON.fromJSON(JsonUtils.getJSONObject(bbJson, "invoice"));
				} else invoice = InvoiceJSON.fromJSON(bbJson); 
				return invoice;
			}).collect(Collectors.toCollection(LinkedList::new));
	}
	
}
