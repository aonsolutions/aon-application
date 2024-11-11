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
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		if(endDate.after(new Date())) throw new AonCoreException("El periodo indicado no ha finalizado.");
		InvoiceBatch ib =  InvoiceBatchDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getTypeProperty().eq(InvoiceCommunicationType.CLOSING.value()))
				.and(f.getDateProperty().ge(AonDateUtils.toTimestamp(startDate)))
				.and(f.getEndDateProperty().le(AonDateUtils.toTimestamp(endDate))));
		if(ib != null && ib.getId() != null) {
			throw new AonCoreException("Ya existe un cierre en el periodo indicado.");
		} else {
			Set<Integer> invoices = InvoiceDAO.getInvoiceStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getCreationDateProperty().between(
						AonDateUtils.toTimestamp(startDate),
						AonDateUtils.toTimestamp(endDate))))
			.map(Invoice::getId).collect(Collectors.toSet());
			
			StringBuilder builder = new StringBuilder();
			InvoiceDataDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getNameProperty().eq("MD5"))
					.and(f.getInvoiceProperty().in(invoices.toArray(Integer[]::new))))
			.filter(r -> !AonStringUtils.isBlank(r.getValue()))
			.forEach(r -> builder.append(r.getValue()));

			invoiceBatch.setMd5(AonDigestUtils.md5Hex(builder.toString()));
			invoiceBatch = InvoiceBatchDAO.save(ctx, invoiceBatch);
			AlcatrazDAO.saveInvoiceBatchInvoices(ctx, invoiceBatch, invoices);
		}
	}
}
