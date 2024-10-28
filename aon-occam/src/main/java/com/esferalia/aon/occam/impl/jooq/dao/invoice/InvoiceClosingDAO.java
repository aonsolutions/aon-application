package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceClosingDAO {

	private InvoiceClosingDAO() {
		
	}
	
	public static Stream<InvoiceBatch> get(AONContext ctx) {	
		return InvoiceBatchDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getTypeProperty().eq(InvoiceCommunicationType.CLOSING.value())));
	}

	public static void save(AONContext ctx, InvoiceBatch invoiceBatch) {
		Date startDate = invoiceBatch.getDate();
		Date endDate = invoiceBatch.getEndDate();
		if(endDate.before(new Date())) throw new AonCoreException("El periodo indicado no ha finalizado.");
		InvoiceBatch ib =  InvoiceBatchDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getTypeProperty().eq(InvoiceCommunicationType.CLOSING.value()))
				.and(f.getDateProperty().ge(AonDateUtils.toTimestamp(startDate)))
				.and(f.getEndDateProperty().le(AonDateUtils.toTimestamp(endDate))));
		if(ib != null && ib.getId() != null) {
			throw new AonCoreException("Ya existe un cierre en el periodo indicado.");
		} else {
			invoiceBatch = InvoiceBatchDAO.save(ctx, invoiceBatch);
			Set<Integer> invoices = InvoiceDAO.getInvoiceStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getCreationDateProperty().between(
						AonDateUtils.toTimestamp(startDate),
						AonDateUtils.toTimestamp(endDate))))
			.map(Invoice::getId).collect(Collectors.toSet());

			AlcatrazDAO.saveInvoiceBatchInvoices(ctx, invoiceBatch, invoices);
		}
	}
}
