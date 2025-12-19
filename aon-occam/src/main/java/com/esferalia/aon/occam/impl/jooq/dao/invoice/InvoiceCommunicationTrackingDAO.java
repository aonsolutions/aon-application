package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDAO.InvoiceBatchFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceBatchDetailDAO.InvoiceBatchDetailFiller;

public class InvoiceCommunicationTrackingDAO {
	
	private InvoiceCommunicationTrackingDAO() {
	
	}
	
	private static SelectOnConditionStep<Record> select(AONContext ctx){	
		return ctx.getDslContext()
			.select()
			.from(INVOICE_BATCH)
			.join(INVOICE_BATCH_DETAIL).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH));
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, Integer invoiceId){	
		return select(ctx)
			.where(INVOICE_BATCH_DETAIL.INVOICE.eq(invoiceId));
	}
	
	public static Optional<InvoiceCommunicationTracking> getNoVerifactuRegister(AONContext ctx, Integer domain, Integer invoice) {
		return getRegister(ctx, domain, invoice, InvoiceCommunicationType.NO_VERIFACTU);
	}

	public static Optional<InvoiceCommunicationTracking> getVerifactuRegister(AONContext ctx, Integer domain, Integer invoice) {
		return getRegister(ctx, domain, invoice, InvoiceCommunicationType.VERIFACTU);
	}
	
	private static Optional<InvoiceCommunicationTracking> getRegister(AONContext ctx, Integer domain, Integer invoice, InvoiceCommunicationType type) {
		return select(ctx)
			.where(INVOICE_BATCH_DETAIL.DOMAIN.eq(domain))
			.and(INVOICE_BATCH_DETAIL.INVOICE.eq(invoice))
			.and(INVOICE_BATCH.TYPE.eq(type.value()))
			.and(INVOICE_BATCH.OPERATION.eq(InvoiceCommunicationOperation.REGISTER.value()))
			.limit(1)
			.fetch()
			.stream()
			.map(new InvoiceCommunicationTrackingFiller())
			.findFirst();
	}

	public static Stream<InvoiceCommunicationTracking> stream(AONContext ctx, Integer invoiceId) {
		return select(ctx, invoiceId)
			.fetch()
			.stream()
			.map(new InvoiceCommunicationTrackingFiller())
		;
	}
	
//	public static Optional<InvoiceCommunicationTracking> get(AONContext ctx, Integer invoiceId) {
//		return select(ctx, invoiceId)
//			.fetch()
//			.stream()
//			.map(new InvoiceCommunicationTrackingFiller())
//			.findFirst();
//	}
	
	public static InvoiceCommunicationTracking save(AONContext ctx, InvoiceCommunicationTracking invoiceCommunicationTracking) {
		Invoice inv = InvoiceDAO.getInvoice(ctx, invoiceCommunicationTracking.getInvoiceBatchDetail().getInvoice());
		if(inv != null && inv.getId() != null) {
			InvoiceBatch invoiceBatch = InvoiceBatchDAO.save(ctx, invoiceCommunicationTracking.getInvoiceBatch());
			invoiceCommunicationTracking.setInvoiceBatch(invoiceBatch);
		
			invoiceCommunicationTracking.getInvoiceBatchDetail().setInvoiceBatch(invoiceBatch.getId());
			InvoiceBatchDetail invoiceBatchDetail = InvoiceBatchDetailDAO.save(ctx, invoiceCommunicationTracking.getInvoiceBatchDetail());
			invoiceCommunicationTracking.setInvoiceBatchDetail(invoiceBatchDetail);
		}
		return invoiceCommunicationTracking;
	}
	
	public static void delete(AONContext ctx, Integer invoiceId) {
		Integer[] array = InvoiceBatchDetailDAO.getStream(ctx, f -> f.getInvoiceProperty().eq(invoiceId))
				.map(InvoiceBatchDetail::getInvoiceBatch)
				.toArray(Integer[]::new);
		InvoiceBatchDetailDAO.delete(ctx, f -> f.getInvoiceProperty().eq(invoiceId));
		InvoiceBatchDAO.delete(ctx, f -> f.getIdProperty().in(array));
	}

	private static class InvoiceCommunicationTrackingFiller extends Filler implements Function<Record, InvoiceCommunicationTracking> {

		@Override
		public InvoiceCommunicationTracking apply(Record r) {
			return build(r);
		}
		
		public static InvoiceCommunicationTracking build(Record r) {
			return new InvoiceCommunicationTracking()
				.setInvoiceBatch(InvoiceBatchFiller.build(r))
				.setInvoiceBatchDetail(InvoiceBatchDetailFiller.build(r));
		}
	}
}
