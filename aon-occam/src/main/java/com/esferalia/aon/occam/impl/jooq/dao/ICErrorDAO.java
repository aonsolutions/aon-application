package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

public class ICErrorDAO {
	
	private ICErrorDAO() {

	}
	
	public static void addDocumentInvoice(AONContext ctx, Invoice invoice) {
		ctx.getDslContext().update(INVOICE)
		.set(INVOICE.RDOCUMENT, invoice.getRegistryDocument())
		.set(INVOICE.RDOCUMENT_COUNTRY, invoice.getRegistryDocumentCountry().getIso2())
		.set(INVOICE.RDOCUMENT_TYPE, invoice.getRegistryDocumentType().value())
		.where(INVOICE.ID.eq(invoice.getId()))
		.execute();
	}
	
}




